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
import org.jooq.conf.Settings
import org.jooq.exception.DataAccessException
import org.jooq.impl.*
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

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
                   val allowRecreation: Boolean = false, val populate: ((DSLContext) -> Unit)? = null) {

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
            // TODO:vgrechka @duplication e530f4e5-7a5f-4dc2-8a59-cafc22e19870 1
            sysdb.joo("Recreate database") {
                it.execute("""
                    drop database if exists "$name";
                    create database "$name" ${template.letoes {"template = \"${it.name}\""}};
                """)
            }

            if (template == null)
                joo("schema.sql") {
                    it.execute(DB::class.java.getResource("schema.sql").readText())
                }
            populate?.let {joo("Some shit", it)}
        }

        fun close() {
            ds.close()
            dslazy.reset()
        }

        fun <T> joo(descr: String, act: (DSLContext) -> T): T {
            ds.connection.use {con->
                val rlm = RedisLogMessage.SQL()-{o->
                    o.shortDescription = descr
                    o.text = "Not known yet"
                    o.stage = PENDING
                }
                redisLog.send(rlm)

                // TODO:vgrechka Cache jOOQ DSLContext
                val q = DSL.using(
                    DefaultConfiguration()
                        .set(con)
                        .set(SQLDialect.POSTGRES_9_5)
                        .set(DefaultExecuteListenerProvider(object:DefaultExecuteListener() {
                            override fun executeStart(ctx: ExecuteContext) {
                                fun dumpShit(shit: String) {
                                    rlm.text = shit
                                    redisLog.amend(rlm)
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

                try {
                    val res = act(q)
                    rlm.stage = SUCCESS
                    return res
                } catch (e: Throwable) {
                    rlm.stage = FAILURE
                    rlm.exceptionStack = e.stackString()
                    throw e
                } finally {
                    rlm.endMillis = currentTimeMillis()
                    redisLog.amend(rlm)
                }
            }
        }

        override fun toString() = "Database(host='$host', port=$port, name='$name', user='$user')"
    }

    fun populate_testTemplateUA1(q: DSLContext) {
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

                        q.insertInto(Tables.USERS).set(u.user.apply {
                            id = nextUserID++
                            kind = _kind.name
                            lang = Language.UA.name
                            state = UserState.COOL.name
                            passwordHash = secretHash
                        }).execute()
                        q.insertInto(Tables.USER_TOKENS).set(UserTokensRecord().apply {
                            userId = u.user.id
                            token = "temp-${u.user.id}"
                        }).execute()

                        for (r in u.roles) {
                            q.insertInto(Tables.USER_ROLES).set(UserRolesRecord().apply {
                                userId = u.user.id
                                role = r.name
                            }).execute()
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

//class DSLContextProxy(val q: DSLContext) {
//    fun <R : Record> insertInto(into: Table<R>, vararg fields: Field<*>): InsertValuesStepN<R> {
//        return q.insertInto(into, *fields)
//    }
//
//    fun fetch(sql: String, vararg bindings: Any?): Result<Record> {
//        return q.fetch(sql, *bindings)
//    }
//
//    fun selectCount(): SelectSelectStep<Record1<Int>> {
//        return q.selectCount()
//    }
//
//    fun <R : Record> update(table: Table<R>): UpdateSetFirstStep<R> {
//        return q.update(table)
//    }
//
//    fun select(vararg fields: SelectField<*>): SelectSelectStep<Record> {
//        return q.select(*fields)
//    }
//
//    fun fetch(sql: String): Result<Record> {
//        return q.fetch(sql)
//    }
//
//    fun <R : Record> insertInto(into: Table<R>): InsertSetStep<R> {
//        return q.insertInto(into)
//    }
//
//    fun execute(short: String, sql: String): Int {
//        val msg = RedisLogMessage.SQL()-{o->
//            o.shortDescription = short
//            o.stage = PENDING
//            o.text = sql
//        }
//        requestShit.sqlRedisLogMessage = msg
//        redisLog.send(msg)
//
//        try {
//            val res = q.execute(sql)
//            msg.stage = SUCCESS
//            return res
//        } catch (e: Throwable) {
//            msg.stage = FAILURE
//            msg.exceptionStack = captureStackAsString()
//            throw e
//        } finally {
//            redisLog.amend(msg)
//        }
//    }
//
//    fun execute(sql: String): Int {
//        return execute("Describe me", sql)
//    }
//}






















