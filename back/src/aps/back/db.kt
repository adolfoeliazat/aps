/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.RedisLogMessage.SQL.Stage.*
import into.kommon.*
import aps.back.generated.jooq.Tables
import aps.back.generated.jooq.tables.records.UserRolesRecord
import aps.back.generated.jooq.tables.records.UserTokensRecord
import aps.back.generated.jooq.tables.records.UsersRecord
import com.zaxxer.hikari.HikariDataSource
import org.jooq.*
import org.jooq.SelectField
import org.jooq.conf.ParamType
import org.jooq.conf.Settings
import org.jooq.exception.DataAccessException
import org.jooq.exception.DataTypeException
import org.jooq.impl.*
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor
import javax.annotation.Generated

val PG_LOCAL_DATE_TIME = DateTimeFormatterBuilder()
    .parseCaseInsensitive()
    .append(DateTimeFormatter.ISO_LOCAL_DATE)
    .appendLiteral(' ')
    .append(DateTimeFormatter.ISO_LOCAL_TIME)
    .toFormatter()

fun stringToStamp(s: String): Timestamp = Timestamp.valueOf(LocalDateTime.parse(s, PG_LOCAL_DATE_TIME))

object DB {
    val PORT_DEV = 5432   // On disk
    val PORT_TEST = 5433  // On memory drive

    val dbs = mutableListOf<Database>()
    val testTemplateUA1 = Database("127.0.0.1", PORT_TEST, "test-template-ua-1", "postgres", allowRecreation = true, populate = {q -> populate_testTemplateUA1(q)})
    val apsTestOnTestServer = Database("127.0.0.1", PORT_TEST, "aps-test", "postgres", allowRecreation = true)
    val postgresOnTestServer = Database("127.0.0.1", PORT_TEST, "postgres", "postgres")
    val postgresOnDevServer = Database("127.0.0.1", PORT_DEV, "postgres", "postgres")

    val systemDatabases = mapOf(
        PORT_DEV to postgresOnDevServer,
        PORT_TEST to postgresOnTestServer
    )

    fun byNameOnTestServer(name: String): Database =
        dbs.find {it.port == PORT_TEST && it.name == name} ?: wtf("No database [$name] on test server")

    class Database(val host: String, val port: Int, val name: String,
                   val user: String, val password: String? = null,
                   val allowRecreation: Boolean = false, val populate: ((DSLContextProxyFactory) -> Unit)? = null) {

        private val dslazy = relazy {HikariDataSource().applet {o->
            o.dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource"
            o.dataSourceProperties.let {o->
                o.put("serverName", host)
                o.put("databaseName", name)
                o.put("portNumber", port)
                o.put("user", user)
                password?.let {o.put("password", it)}
            }
        }}
        val ds by dslazy

        init {
            dbs.add(this)
        }

        fun recreate(template: Database? = null) {
            check(allowRecreation) {"You crazy? I'm not recreating THIS: $this"}
            checkAtMostOneOf("template" to template, "populate" to populate)

            close()
            template?.let {it.close()}

            val sysdb = systemDatabases[port] ?: wtf("No system DB on port $port")
            sysdb.joo {
                it("Recreate database $name")
                    .execute("""
                        drop database if exists "$name";
                        create database "$name" ${template.letoes {"template = \"${it.name}\""}};
                    """)
            }

            if (template == null)
                joo {
                    it("schema.sql")
                        .execute(DB::class.java.getResource("schema.sql").readText())
                }

            populate?.let {joo(it)}
        }

        fun close() {
            ds.close()
            dslazy.reset()
        }

        fun <T> joo(act: (DSLContextProxyFactory) -> T): T {
            ds.connection.use {con->
                // TODO:vgrechka Cache jOOQ DSLContext
                val q = DSL.using(
                    DefaultConfiguration()
                        .set(con)
                        .set(SQLDialect.POSTGRES_9_5)
                        .set(DefaultExecuteListenerProvider(object:DefaultExecuteListener() {
                            override fun executeStart(ctx: ExecuteContext) {
                                fun dumpShit(shit: String) {
                                    requestShit.actualSQLFromJOOQ = shit
                                }

                                fun dumpShit(shit: QueryPart) = dumpShit(
                                    DSL.using(ctx.configuration().dialect(), Settings().withRenderFormatted(true))
                                        .renderInlined(shit))

                                ctx.query()?.let {dumpShit(it); return}
                                ctx.routine()?.let {dumpShit(it); return}
                                ctx.sql()?.let {dumpShit(it); return}
                            }
                        }))
                )

                return act(DSLContextProxyFactory(q))
            }
        }

        override fun toString() = "Database(host='$host', port=$port, name='$name', user='$user')"
    }

    fun populate_testTemplateUA1(q: DSLContextProxyFactory) {
        redisLog.group("populate_testTemplateUA1") {
            val secretHash = "\$2a\$10\$x5bq4zVvcyTb2oUb5.fhreJfl/2NqsaH3TcAwm/C1apAazlBJX2t6" // secret
            var nextUserID = 101L

            class Action(val stamp: Timestamp, val act: () -> Unit)

            val actions = mutableListOf<Action>()

            class UserSpec(val user: UsersRecord, val roles: Iterable<UserRole> = listOf())

            fun addInsertUserActions(_kind: UserKind, userSpecs: Iterable<UserSpec>) {
                for (u in userSpecs) {
                    actions.add(Action(u.user.insertedAt) {
                        dlog("Inserting ${_kind.name} ${u.user.firstName} at ${u.user.insertedAt}")

                        q("Insert user ${u.user.email}")
                            .insertInto(Tables.USERS)
                            .set(u.user.apply {
                                id = nextUserID++
                                kind = _kind.name
                                lang = Language.UA.name
                                state = UserState.COOL.name
                                passwordHash = secretHash
                            })
                            .execute()

                        q("Insert token for ${u.user.email}")
                            .insertInto(Tables.USER_TOKENS)
                            .set(UserTokensRecord().apply {
                                userId = u.user.id
                                token = "temp-${u.user.id}"
                            })
                            .execute()

                        for (r in u.roles) {
                            q("Insert role ${r.name} for ${u.user.email}")
                                .insertInto(Tables.USER_ROLES)
                                .set(UserRolesRecord().apply {
                                    userId = u.user.id
                                    role = r.name
                                })
                                .execute()
                        }
                    })
                }
            }


            addInsertUserActions(UserKind.ADMIN, listOf(
                UserSpec(UsersRecord().apply {firstName = "Дася"; lastName = "Админовна"; email = "dasja@test.shit.ua"; insertedAt = stringToStamp("2016-07-10 13:14:15")},listOf(UserRole.SUPPORT)),
                UserSpec(UsersRecord().apply {firstName = "Тодд"; lastName = "Суппортод"; email = "todd@test.shit.ua"; insertedAt = stringToStamp("2016-07-13 02:44:05")}, listOf(UserRole.SUPPORT)),
                UserSpec(UsersRecord().apply {firstName = "Алиса"; lastName = "Планктоновна"; email = "alice@test.shit.ua"; insertedAt = stringToStamp("2016-07-11 20:28:17")}, listOf(UserRole.SUPPORT)),
                UserSpec(UsersRecord().apply {firstName = "Элеанора"; lastName = "Суконская"; email = "eleanor@test.shit.ua"; insertedAt = stringToStamp("2016-07-11 20:28:17")}, listOf(UserRole.SUPPORT))
            ))

            addInsertUserActions(UserKind.WRITER, listOf(
                UserSpec(UsersRecord().apply {firstName = "Франц"; lastName = "Кафка"; email = "kafka@test.shit.ua"; insertedAt = stringToStamp("2016-07-11 01:05:30")}),
                UserSpec(UsersRecord().apply {firstName = "Лев"; lastName = "Толстой"; email = "leo@test.shit.ua"; insertedAt = stringToStamp("2016-07-29 22:54:42")}),
                UserSpec(UsersRecord().apply {firstName = "Николай"; lastName = "Гоголь"; email = "gogol@test.shit.ua"; insertedAt = stringToStamp("2016-07-13 17:32:27")}),
                UserSpec(UsersRecord().apply {firstName = "Федор"; lastName = "Достоевский"; email = "fedor@test.shit.ua"; insertedAt = stringToStamp("2016-08-04 14:41:07")}),
                UserSpec(UsersRecord().apply {firstName = "Александр"; lastName = "Пушкин"; email = "pushkin@test.shit.ua"; insertedAt = stringToStamp("2016-07-19 21:35:30")}),
                UserSpec(UsersRecord().apply {firstName = "Георг"; lastName = "Гегель"; email = "hegel@test.shit.ua"; insertedAt = stringToStamp("2016-07-22 07:19:47")}),
                UserSpec(UsersRecord().apply {firstName = "Иммануил"; lastName = "Кант"; email = "kant@test.shit.ua"; insertedAt = stringToStamp("2016-07-13 05:54:20")}),
                UserSpec(UsersRecord().apply {firstName = "Мигель"; lastName = "Сервантес"; email = "miguel@test.shit.ua"; insertedAt = stringToStamp("2016-07-16 22:25:56")}),
                UserSpec(UsersRecord().apply {firstName = "Карлос"; lastName = "Кастанеда"; email = "carlos@test.shit.ua"; insertedAt = stringToStamp("2016-07-29 13:39:06")}),
                UserSpec(UsersRecord().apply {firstName = "Елена"; lastName = "Блаватская"; email = "blava@test.shit.ua"; insertedAt = stringToStamp("2016-07-27 12:06:11")}),
                UserSpec(UsersRecord().apply {firstName = "Джейн"; lastName = "Остин"; email = "jane@test.shit.ua"; insertedAt = stringToStamp("2016-07-15 15:13:27")}),
                UserSpec(UsersRecord().apply {firstName = "Мэри"; lastName = "Шелли"; email = "mary@test.shit.ua"; insertedAt = stringToStamp("2016-07-22 01:33:17")}),
                UserSpec(UsersRecord().apply {firstName = "Франсуаза"; lastName = "Саган"; email = "francoise@test.shit.ua"; insertedAt = stringToStamp("2016-08-08 16:04:53")}),
                UserSpec(UsersRecord().apply {firstName = "Жорж"; lastName = "Санд"; email = "sand@test.shit.ua"; insertedAt = stringToStamp("2016-07-16 20:47:26")}),
                UserSpec(UsersRecord().apply {firstName = "Агата"; lastName = "Кристи"; email = "agatha@test.shit.ua"; insertedAt = stringToStamp("2016-08-12 09:59:53")})
            ))

            addInsertUserActions(UserKind.CUSTOMER, listOf(
                UserSpec(UsersRecord().apply {firstName = "Пися"; lastName = "Камушкин"; email = "pisya@test.shit.ua"; insertedAt = stringToStamp("2016-08-02 14:38:15")}),
                UserSpec(UsersRecord().apply {firstName = "Люк"; lastName = "Хуюк"; email = "luke@test.shit.ua"; insertedAt = stringToStamp("2016-07-14 18:36:35")}),
                UserSpec(UsersRecord().apply {firstName = "Павло"; lastName = "Зибров"; email = "zibrov@test.shit.ua"; insertedAt = stringToStamp("2016-08-09 11:36:01")}),
                UserSpec(UsersRecord().apply {firstName = "Василий"; lastName = "Теркин"; email = "terkin@test.shit.ua"; insertedAt = stringToStamp("2016-07-16 18:59:35")}),
                UserSpec(UsersRecord().apply {firstName = "Иво"; lastName = "Бобул"; email = "ivo@test.shit.ua"; insertedAt = stringToStamp("2016-07-16 22:51:51")}),
                UserSpec(UsersRecord().apply {firstName = "Регина"; lastName = "Дубовицкая"; email = "regina@test.shit.ua"; insertedAt = stringToStamp("2016-07-12 06:31:58")}),
                UserSpec(UsersRecord().apply {firstName = "Евгений"; lastName = "Ваганович"; email = "vaganovich@test.shit.ua"; insertedAt = stringToStamp("2016-08-06 03:59:58")}),
                UserSpec(UsersRecord().apply {firstName = "Павел"; lastName = "Дристальский"; email = "paul@test.shit.ua"; insertedAt = stringToStamp("2016-07-27 07:57:02")}),
                UserSpec(UsersRecord().apply {firstName = "Тело"; lastName = "Странное"; email = "telo@test.shit.ua"; insertedAt = stringToStamp("2016-07-26 08:50:23")}),
                UserSpec(UsersRecord().apply {firstName = "Арчибальд"; lastName = "Нелеподлиннаяфамилияуменя"; email = "archie@test.shit.ua"; insertedAt = stringToStamp("2016-08-12 10:26:14")}),
                UserSpec(UsersRecord().apply {firstName = "Даздраперма"; lastName = "Дивизионная"; email = "perma@test.shit.ua"; insertedAt = stringToStamp("2016-08-11 07:56:00")}),
                UserSpec(UsersRecord().apply {firstName = "Уменяреальносамыйдлинный"; lastName = "Ымя"; email = "ymya@test.shit.ua"; insertedAt = stringToStamp("2016-08-06 08:23:56")}),
                UserSpec(UsersRecord().apply {firstName = "Варсоновий"; lastName = "Оптинский"; email = "varso@test.shit.ua"; insertedAt = stringToStamp("2016-08-04 08:29:23")}),
                UserSpec(UsersRecord().apply {firstName = "Евстафий"; lastName = "Антиохийский"; email = "anti@test.shit.ua"; insertedAt = stringToStamp("2016-08-12 17:38:34")}),
                UserSpec(UsersRecord().apply {firstName = "Ксенофонт"; lastName = "Тутанский"; email = "xeno@test.shit.ua"; insertedAt = stringToStamp("2016-07-10 17:07:24")})
            ))

            actions.sortBy {it.stamp}
            for (action in actions) action.act()
        }
    }
}

class ActivityParams {
    lateinit var shortDescription: String
}

class DSLContextProxyFactory(val q: DSLContext) {
    operator fun invoke(shortDescription: String): DSLContextProxy {
        val activityParams = ActivityParams()-{o->
            o.shortDescription = shortDescription
        }
        return DSLContextProxy(activityParams, q)
    }
}

class DSLContextProxy(val activityParams: ActivityParams, val q: DSLContext) {
    fun <R : Record> insertInto(into: Table<R>, vararg fields: Field<*>): InsertValuesStepN<R> {
        return q.insertInto(into, *fields)
    }

    fun fetch(sql: String, vararg bindings: Any?): Result<Record> {
        return q.fetch(sql, *bindings)
    }

    fun selectCount(): SelectSelectStep<Record1<Int>> {
        return q.selectCount()
    }

    fun <R : Record> update(table: Table<R>): UpdateSetFirstStep<R> {
        return q.update(table)
    }

    fun select(vararg fields: SelectField<*>): SelectSelectStep<Record> {
        return q.select(*fields)
    }

    fun fetch(sql: String): Result<Record> {
        return q.fetch(sql)
    }

    fun <R : Record> insertInto(into: Table<R>): InsertSetStep<R> {
        val res = q.insertInto(into)
        return InsertSetStepProxy(activityParams, res)
    }

    fun execute(descr: String, sql: String): Int {
        return tracing(descr) {q.execute(sql)}
    }

    fun execute(sql: String): Int {
        return execute("Describe me (execute)", sql)
    }

    private fun <T> tracing(descr: String, block: () -> T): T {
        val rlm = RedisLogMessage.SQL() - {o ->
            o.shortDescription = descr
            o.stage = PENDING
            o.text = "Not known yet"
        }
        redisLog.send(rlm)

        requestShit.actualSQLFromJOOQ = null

        try {
            val res = block()
            rlm.stage = SUCCESS
            return res
        } catch (e: Throwable) {
            rlm.stage = FAILURE
            rlm.exceptionStack = e.stackString()
            throw e
        } finally {
            requestShit.actualSQLFromJOOQ?.let {rlm.text = it}
            rlm.endMillis = currentTimeMillis()
            redisLog.amend(rlm)
        }
    }
}

class InsertSetStepProxy<R : Record>(val activityParams: ActivityParams, val wrappee: InsertSetStep<R>) : InsertSetStep<R> {

    override fun columns(fields: Array<Field<*>>): InsertValuesStepN<R> {
        return wrappee.columns(*fields)
    }

    override fun columns(fields: Collection<Field<*>>): InsertValuesStepN<R> {
        return wrappee.columns(fields)
    }

    override fun <T1> columns(field1: Field<T1>): InsertValuesStep1<R, T1> {
        return wrappee.columns(field1)
    }

    override fun <T1, T2> columns(field1: Field<T1>, field2: Field<T2>): InsertValuesStep2<R, T1, T2> {
        return wrappee.columns(field1, field2)
    }

    override fun <T1, T2, T3> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>): InsertValuesStep3<R, T1, T2, T3> {
        return wrappee.columns(field1, field2, field3)
    }

    override fun <T1, T2, T3, T4> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>): InsertValuesStep4<R, T1, T2, T3, T4> {
        return wrappee.columns(field1, field2, field3, field4)
    }

    override fun <T1, T2, T3, T4, T5> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>): InsertValuesStep5<R, T1, T2, T3, T4, T5> {
        return wrappee.columns(field1, field2, field3, field4, field5)
    }

    override fun <T1, T2, T3, T4, T5, T6> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>): InsertValuesStep6<R, T1, T2, T3, T4, T5, T6> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>): InsertValuesStep7<R, T1, T2, T3, T4, T5, T6, T7> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>): InsertValuesStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>): InsertValuesStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>): InsertValuesStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>): InsertValuesStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>): InsertValuesStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>): InsertValuesStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>): InsertValuesStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>): InsertValuesStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>): InsertValuesStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>, field17: Field<T17>): InsertValuesStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>, field17: Field<T17>, field18: Field<T18>): InsertValuesStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>, field17: Field<T17>, field18: Field<T18>, field19: Field<T19>): InsertValuesStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>, field17: Field<T17>, field18: Field<T18>, field19: Field<T19>, field20: Field<T20>): InsertValuesStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>, field17: Field<T17>, field18: Field<T18>, field19: Field<T19>, field20: Field<T20>, field21: Field<T21>): InsertValuesStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21)
    }

    override fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> columns(field1: Field<T1>, field2: Field<T2>, field3: Field<T3>, field4: Field<T4>, field5: Field<T5>, field6: Field<T6>, field7: Field<T7>, field8: Field<T8>, field9: Field<T9>, field10: Field<T10>, field11: Field<T11>, field12: Field<T12>, field13: Field<T13>, field14: Field<T14>, field15: Field<T15>, field16: Field<T16>, field17: Field<T17>, field18: Field<T18>, field19: Field<T19>, field20: Field<T20>, field21: Field<T21>, field22: Field<T22>): InsertValuesStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> {
        return wrappee.columns(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22)
    }

    operator override fun <T> set(field: Field<T>, value: T): InsertSetMoreStep<R> {
        val res = wrappee.set(field, value)
        return InsertSetMoreStepProxy(activityParams, res)
    }

    operator override fun <T> set(field: Field<T>, value: Field<T>): InsertSetMoreStep<R> {
        val res = wrappee.set(field, value)
        return InsertSetMoreStepProxy(activityParams, res)
    }

    operator override fun <T> set(field: Field<T>, value: Select<out Record1<T>>): InsertSetMoreStep<R> {
        val res = wrappee.set(field, value)
        return InsertSetMoreStepProxy(activityParams, res)
    }

    override fun set(map: Map<out Field<*>, *>): InsertSetMoreStep<R> {
        val res = wrappee.set(map)
        return InsertSetMoreStepProxy(activityParams, res)
    }

    override fun set(record: Record): InsertSetMoreStep<R> {
        val res = wrappee.set(record)
        return InsertSetMoreStepProxy(activityParams, res)
    }

    override fun values(vararg values: Any): InsertValuesStepN<R> {
        return wrappee.values(*values)
    }

    override fun values(values: Array<Field<*>>): InsertValuesStepN<R> {
        return wrappee.values(*values)
    }

    override fun values(values: Collection<*>): InsertValuesStepN<R> {
        return wrappee.values(values)
    }

    override fun defaultValues(): InsertReturningStep<R> {
        return wrappee.defaultValues()
    }

    override fun select(select: Select<*>): InsertReturningStep<R> {
        return wrappee.select(select)
    }
}

class InsertSetMoreStepProxy<R : Record>(val activityParams: ActivityParams, val wrappee: InsertSetMoreStep<R>) : InsertSetMoreStep<R> {
    operator override fun <T> set(field: Field<T>, value: T): InsertSetMoreStep<R> {
        return wrappee.set(field, value)
    }

    operator override fun <T> set(field: Field<T>, value: Field<T>): InsertSetMoreStep<R> {
        return wrappee.set(field, value)
    }

    operator override fun <T> set(field: Field<T>, value: Select<out Record1<T>>): InsertSetMoreStep<R> {
        return wrappee.set(field, value)
    }

    override fun set(map: Map<out Field<*>, *>): InsertSetMoreStep<R> {
        return wrappee.set(map)
    }

    override fun set(record: Record): InsertSetMoreStep<R> {
        return wrappee.set(record)
    }

    override fun newRecord(): InsertSetStep<R> {
        return wrappee.newRecord()
    }

    override fun onDuplicateKeyUpdate(): InsertOnDuplicateSetStep<R> {
        return wrappee.onDuplicateKeyUpdate()
    }

    override fun onDuplicateKeyIgnore(): InsertFinalStep<R> {
        return wrappee.onDuplicateKeyIgnore()
    }

    override fun returning(): InsertResultStep<R> {
        return wrappee.returning()
    }

    override fun returning(fields: Array<Field<*>>): InsertResultStep<R> {
        return wrappee.returning(*fields)
    }

    override fun returning(fields: Collection<Field<*>>): InsertResultStep<R> {
        return wrappee.returning(fields)
    }

    @Throws(DataAccessException::class)
    override fun execute(): Int {
        val block = {wrappee.execute()}

        val rlm = RedisLogMessage.SQL() - {o ->
            o.shortDescription = activityParams.shortDescription
            o.stage = PENDING
            o.text = "Not known yet"
        }
        redisLog.send(rlm)

        requestShit.actualSQLFromJOOQ = null

        try {
            val res = block()
            rlm.stage = SUCCESS
            return res
        } catch (e: Throwable) {
            rlm.stage = FAILURE
            rlm.exceptionStack = e.stackString()
            throw e
        } finally {
            requestShit.actualSQLFromJOOQ?.let {rlm.text = it}
            rlm.endMillis = currentTimeMillis()
            redisLog.amend(rlm)
        }
    }

    override fun executeAsync(): CompletionStage<Int> {
        return wrappee.executeAsync()
    }

    override fun executeAsync(executor: Executor): CompletionStage<Int> {
        return wrappee.executeAsync(executor)
    }

    override fun isExecutable(): Boolean {
        return wrappee.isExecutable()
    }

    override fun getSQL(): String {
        return wrappee.getSQL()
    }

    @Deprecated("")
    override fun getSQL(inline: Boolean): String {
        return wrappee.getSQL(inline)
    }

    override fun getSQL(paramType: ParamType): String {
        return wrappee.getSQL(paramType)
    }

    override fun getBindValues(): List<Any> {
        return wrappee.bindValues
    }

    override fun getParams(): Map<String, Param<*>> = wrappee.params

    override fun getParam(name: String): Param<*> {
        return wrappee.getParam(name)
    }

    @Throws(IllegalArgumentException::class, DataTypeException::class)
    override fun bind(param: String, value: Any): Query {
        return wrappee.bind(param, value)
    }

    @Throws(IllegalArgumentException::class, DataTypeException::class)
    override fun bind(index: Int, value: Any): Query {
        return wrappee.bind(index, value)
    }

    override fun queryTimeout(timeout: Int): Query {
        return wrappee.queryTimeout(timeout)
    }

    override fun keepStatement(keepStatement: Boolean): Query {
        return wrappee.keepStatement(keepStatement)
    }

    @Throws(DataAccessException::class)
    override fun close() {
        wrappee.close()
    }

    @Throws(DataAccessException::class)
    override fun cancel() {
        wrappee.cancel()
    }

    override fun attach(configuration: Configuration) {
        wrappee.attach(configuration)
    }

    override fun detach() {
        wrappee.detach()
    }
}





















