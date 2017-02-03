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
import aps.back.generated.jooq.tables.records.JQUserRolesRecord
import aps.back.generated.jooq.tables.records.JQUserTokensRecord
import aps.back.generated.jooq.tables.records.JQUsersRecord
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.NullNode
import com.zaxxer.hikari.HikariDataSource
import org.jooq.*
import org.jooq.SelectField
import org.jooq.conf.ParamType
import org.jooq.conf.Settings
import org.jooq.exception.DataAccessException
import org.jooq.exception.DataTypeException
import org.jooq.impl.*
import org.jooq.tools.Convert.convert
import java.sql.SQLFeatureNotSupportedException
import java.sql.Timestamp
import java.sql.Types
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*
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
    val snapshotPrefix = "apsTestSnapshotOnTestServer-"

    val dbs = mutableListOf<Database>()
    val testTemplateUA1 = Database("testTemplateUA1", "127.0.0.1", PORT_TEST, "test-template-ua-1", "postgres", allowRecreation = true, populate = {q -> populate_testTemplateUA1(q)})
    val postgresOnTestServer = Database("postgresOnTestServer", "127.0.0.1", PORT_TEST, "postgres", "postgres")
    val apsTestOnTestServer = Database("apsTestOnTestServer", "127.0.0.1", PORT_TEST, "aps-test", "postgres", allowRecreation = true, correspondingAdminDB = postgresOnTestServer)
    val postgresOnDevServer = Database("postgresOnDevServer", "127.0.0.1", PORT_DEV, "postgres", "postgres")
    val localDevUA = Database("localDevUA", "127.0.0.1", PORT_DEV, "aps-dev-ua", user = "postgres", password = null)
    val bmix_fuckingAround_postgres by lazy {databaseFromEnv("bmix_fuckingAround_postgres")}
    val bmix_fuckingAround_apsdevua by lazy {databaseFromEnv("bmix_fuckingAround_apsdevua", allowRecreation = true, correspondingAdminDB = bmix_fuckingAround_postgres)}

    fun apsTestSnapshotOnTestServer(id: String) =
        Database(snapshotPrefix + id, "127.0.0.1", PORT_TEST, snapshotPrefix + id, "postgres", allowRecreation = true, correspondingAdminDB = postgresOnTestServer)

    val systemDatabases = mapOf(
        PORT_DEV to postgresOnDevServer,
        PORT_TEST to postgresOnTestServer
    )

    fun databaseFromEnv(id: String, allowRecreation: Boolean = false, correspondingAdminDB: Database? = null): Database {
        val pname = "APS_DB_URI_$id"
        val uri = System.getenv(pname) ?: wtf("I want env property $pname")
        val mr = Regex("postgres://(.*?):(.*?)@(.*?):(.*?)/(.*?)").matchEntire(uri) ?: wtf("Bad database URI")
        return Database(id,
                        user = mr.groupValues[1],
                        password = mr.groupValues[2],
                        host = mr.groupValues[3],
                        port = mr.groupValues[4].toInt(),
                        name = mr.groupValues[5],
                        allowRecreation = allowRecreation,
                        correspondingAdminDB = correspondingAdminDB)
    }

    fun byNameOnTestServer(name: String): Database =
        dbs.find {it.port == PORT_TEST && it.name == name} ?: wtf("No database [$name] on test server")

    fun byID(id: String): Database = when {
        id == "bmix_fuckingAround_apsdevua" -> bmix_fuckingAround_apsdevua
        id == "apsTestOnTestServer" -> apsTestOnTestServer
        else -> wtf("No database with ID $id")
    }


    class Database(
        val id: String,
        val host: String, val port: Int, val name: String,
        val user: String, val password: String? = null,
        val allowRecreation: Boolean = false,
        val populate: ((DSLContext) -> Unit)? = null,
        val correspondingAdminDB: Database? = null
    ) {
        private val dslazy = relazy {HikariDataSource().applet {o->
            o.dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource"
            o.dataSourceProperties.let {o->
                o.put("serverName", host)
                o.put("databaseName", name)
                o.put("portNumber", port)
                o.put("user", user)
                password?.let {o.put("password", it)}
            }
            o.connectionInitSql = "set time zone 'UTC'"
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

            correspondingAdminDB!!.joo {
                tracingSQL("Recreate database $name" + template.letOrEmpty {" from ${it.name}"}) {it
                    .execute("""
                        drop database if exists "$name";
                        create database "$name" ${template.letOrEmpty {"template = \"${it.name}\""}};
                    """)
                }
            }

            if (template == null)
                joo {
                    tracingSQL("schema.sql") {it
                        .execute(DB::class.java.getResource("schema.sql").readText())
                    }
                }

            populate?.let {joo(it)}
        }

        fun recreateSchema() {
            check(allowRecreation) {"You crazy? I'm not recreating THIS: $this"}

            joo {
                tracingSQL("drop.sql") {it.execute(DB::class.java.getResource("drop.sql").readText())}
                tracingSQL("schema.sql") {it.execute(DB::class.java.getResource("schema.sql").readText())}
            }

            populate?.let {joo(it)}
        }

        fun close() {
            ds.close()
            dslazy.reset()
        }

        fun <T> joo(act: (DSLContext) -> T): T {
            ds.connection.use {con->
                // TODO:vgrechka Cache jOOQ DSLContext
                val q = DSL.using(
                    DefaultConfiguration()
                        .set(con)
                        .set(SQLDialect.POSTGRES_9_5)
                        .set(DefaultExecuteListenerProvider(object:DefaultExecuteListener() {
                            override fun executeStart(ctx: ExecuteContext) {
                                if (!BackGlobus.tracingEnabled || !isRequestThread()) return

                                fun dumpShit(shit: String) {
                                    RequestGlobus.actualSQLFromJOOQ = shit
                                }

                                fun dumpShit(shit: QueryPart) {
                                    dumpShit(DSL.using(ctx.configuration().dialect(), Settings().withRenderFormatted(true))
                                                 .renderInlined(shit))
                                }

                                ctx.query()?.let {dumpShit(it); return}
                                ctx.routine()?.let {dumpShit(it); return}
                                ctx.sql()?.let {dumpShit(it); return}
                            }

                            override fun fetchEnd(ctx: ExecuteContext) {
                                if (!BackGlobus.tracingEnabled || !isRequestThread()) return

                                RequestGlobus.resultFromJOOQ = ctx.result()
                            }
                        }))
                )

                return act(q)
            }
        }

        override fun toString() = "Database(id='$id')"
    }

    fun populate_testTemplateUA1(q: DSLContext) {
        redisLog.group("populate_testTemplateUA1") {
            val secretHash = "\$2a\$10\$x5bq4zVvcyTb2oUb5.fhreJfl/2NqsaH3TcAwm/C1apAazlBJX2t6" // secret
            var nextUserID = 101L

            class Action(val stamp: Timestamp, val act: () -> Unit)

            val actions = mutableListOf<Action>()

            class UserSpec(val user: JQUsersRecord, val roles: Iterable<UserRole> = listOf())

            fun addInsertUserActions(_kind: UserKind, userSpecs: Iterable<UserSpec>) {
                for (u in userSpecs) {
                    actions.add(Action(u.user.insertedAt) {
                        dlog("Inserting ${_kind.name} ${u.user.firstName} at ${u.user.insertedAt}")

                        redisLog.group("Make user: ${u.user.email}") {
                            tracingSQL("Insert user ${u.user.email}") {q
                                .insertInto(Tables.USERS)
                                .set(u.user.apply {
                                    id = nextUserID++
                                    kind = _kind.toJOOQ()
                                    lang = Language.UA.name
                                    state = UserState.COOL.name
                                    passwordHash = secretHash
                                    adminNotes = ""
                                })
                                .execute()
                            }

                            tracingSQL("Insert token for ${u.user.email}") {q
                                .insertInto(Tables.USER_TOKENS)
                                .set(JQUserTokensRecord().apply {
                                    userId = u.user.id
                                    token = "temp-${u.user.id}"
                                })
                                .execute()
                            }

                            for (r in u.roles) {
                                tracingSQL("Insert role ${r.name} for ${u.user.email}") {q
                                    .insertInto(Tables.USER_ROLES)
                                    .set(JQUserRolesRecord().apply {
                                        userId = u.user.id
                                        role = r.name
                                    })
                                    .execute()
                                }
                            }
                        }
                    })
                }
            }


            addInsertUserActions(UserKind.ADMIN, listOf(
                UserSpec(JQUsersRecord().apply {firstName = "Дася"; lastName = "Админовна"; email = "dasja@test.shit.ua"; insertedAt = stringToStamp("2016-07-10 13:14:15")},listOf(UserRole.SUPPORT))
//                UserSpec(UsersRecord().apply {firstName = "Тодд"; lastName = "Суппортод"; email = "todd@test.shit.ua"; insertedAt = stringToStamp("2016-07-13 02:44:05")}, listOf(UserRole.SUPPORT)),
//                UserSpec(UsersRecord().apply {firstName = "Алиса"; lastName = "Планктоновна"; email = "alice@test.shit.ua"; insertedAt = stringToStamp("2016-07-11 20:28:17")}, listOf(UserRole.SUPPORT)),
//                UserSpec(UsersRecord().apply {firstName = "Элеанора"; lastName = "Суконская"; email = "eleanor@test.shit.ua"; insertedAt = stringToStamp("2016-07-11 20:28:17")}, listOf(UserRole.SUPPORT))
            ))

            addInsertUserActions(UserKind.WRITER, listOf(
//                UserSpec(UsersRecord().apply {firstName = "Франц"; lastName = "Кафка"; email = "kafka@test.shit.ua"; insertedAt = stringToStamp("2016-07-11 01:05:30")}),
//                UserSpec(UsersRecord().apply {firstName = "Лев"; lastName = "Толстой"; email = "leo@test.shit.ua"; insertedAt = stringToStamp("2016-07-29 22:54:42")}),
//                UserSpec(UsersRecord().apply {firstName = "Николай"; lastName = "Гоголь"; email = "gogol@test.shit.ua"; insertedAt = stringToStamp("2016-07-13 17:32:27")}),
//                UserSpec(UsersRecord().apply {firstName = "Федор"; lastName = "Достоевский"; email = "fedor@test.shit.ua"; insertedAt = stringToStamp("2016-08-04 14:41:07")}),
//                UserSpec(UsersRecord().apply {firstName = "Александр"; lastName = "Пушкин"; email = "pushkin@test.shit.ua"; insertedAt = stringToStamp("2016-07-19 21:35:30")}),
//                UserSpec(UsersRecord().apply {firstName = "Георг"; lastName = "Гегель"; email = "hegel@test.shit.ua"; insertedAt = stringToStamp("2016-07-22 07:19:47")}),
//                UserSpec(UsersRecord().apply {firstName = "Иммануил"; lastName = "Кант"; email = "kant@test.shit.ua"; insertedAt = stringToStamp("2016-07-13 05:54:20")}),
//                UserSpec(UsersRecord().apply {firstName = "Мигель"; lastName = "Сервантес"; email = "miguel@test.shit.ua"; insertedAt = stringToStamp("2016-07-16 22:25:56")}),
//                UserSpec(UsersRecord().apply {firstName = "Карлос"; lastName = "Кастанеда"; email = "carlos@test.shit.ua"; insertedAt = stringToStamp("2016-07-29 13:39:06")}),
//                UserSpec(UsersRecord().apply {firstName = "Елена"; lastName = "Блаватская"; email = "blava@test.shit.ua"; insertedAt = stringToStamp("2016-07-27 12:06:11")}),
//                UserSpec(UsersRecord().apply {firstName = "Джейн"; lastName = "Остин"; email = "jane@test.shit.ua"; insertedAt = stringToStamp("2016-07-15 15:13:27")}),
//                UserSpec(UsersRecord().apply {firstName = "Мэри"; lastName = "Шелли"; email = "mary@test.shit.ua"; insertedAt = stringToStamp("2016-07-22 01:33:17")}),
//                UserSpec(UsersRecord().apply {firstName = "Франсуаза"; lastName = "Саган"; email = "francoise@test.shit.ua"; insertedAt = stringToStamp("2016-08-08 16:04:53")}),
//                UserSpec(UsersRecord().apply {firstName = "Жорж"; lastName = "Санд"; email = "sand@test.shit.ua"; insertedAt = stringToStamp("2016-07-16 20:47:26")}),
//                UserSpec(UsersRecord().apply {firstName = "Агата"; lastName = "Кристи"; email = "agatha@test.shit.ua"; insertedAt = stringToStamp("2016-08-12 09:59:53")})
            ))

            addInsertUserActions(UserKind.CUSTOMER, listOf(
//                UserSpec(UsersRecord().apply {firstName = "Пися"; lastName = "Камушкин"; email = "pisya@test.shit.ua"; insertedAt = stringToStamp("2016-08-02 14:38:15")}),
//                UserSpec(UsersRecord().apply {firstName = "Люк"; lastName = "Хуюк"; email = "luke@test.shit.ua"; insertedAt = stringToStamp("2016-07-14 18:36:35")}),
//                UserSpec(UsersRecord().apply {firstName = "Павло"; lastName = "Зибров"; email = "zibrov@test.shit.ua"; insertedAt = stringToStamp("2016-08-09 11:36:01")}),
//                UserSpec(UsersRecord().apply {firstName = "Василий"; lastName = "Теркин"; email = "terkin@test.shit.ua"; insertedAt = stringToStamp("2016-07-16 18:59:35")}),
//                UserSpec(UsersRecord().apply {firstName = "Иво"; lastName = "Бобул"; email = "ivo@test.shit.ua"; insertedAt = stringToStamp("2016-07-16 22:51:51")}),
//                UserSpec(UsersRecord().apply {firstName = "Регина"; lastName = "Дубовицкая"; email = "regina@test.shit.ua"; insertedAt = stringToStamp("2016-07-12 06:31:58")}),
//                UserSpec(UsersRecord().apply {firstName = "Евгений"; lastName = "Ваганович"; email = "vaganovich@test.shit.ua"; insertedAt = stringToStamp("2016-08-06 03:59:58")}),
//                UserSpec(UsersRecord().apply {firstName = "Павел"; lastName = "Дристальский"; email = "paul@test.shit.ua"; insertedAt = stringToStamp("2016-07-27 07:57:02")}),
//                UserSpec(UsersRecord().apply {firstName = "Тело"; lastName = "Странное"; email = "telo@test.shit.ua"; insertedAt = stringToStamp("2016-07-26 08:50:23")}),
//                UserSpec(UsersRecord().apply {firstName = "Арчибальд"; lastName = "Нелеподлиннаяфамилияуменя"; email = "archie@test.shit.ua"; insertedAt = stringToStamp("2016-08-12 10:26:14")}),
//                UserSpec(UsersRecord().apply {firstName = "Даздраперма"; lastName = "Дивизионная"; email = "perma@test.shit.ua"; insertedAt = stringToStamp("2016-08-11 07:56:00")}),
//                UserSpec(UsersRecord().apply {firstName = "Уменяреальносамыйдлинный"; lastName = "Ымя"; email = "ymya@test.shit.ua"; insertedAt = stringToStamp("2016-08-06 08:23:56")}),
//                UserSpec(UsersRecord().apply {firstName = "Варсоновий"; lastName = "Оптинский"; email = "varso@test.shit.ua"; insertedAt = stringToStamp("2016-08-04 08:29:23")}),
//                UserSpec(UsersRecord().apply {firstName = "Евстафий"; lastName = "Антиохийский"; email = "anti@test.shit.ua"; insertedAt = stringToStamp("2016-08-12 17:38:34")}),
//                UserSpec(UsersRecord().apply {firstName = "Ксенофонт"; lastName = "Тутанский"; email = "xeno@test.shit.ua"; insertedAt = stringToStamp("2016-07-10 17:07:24")})
            ))

            actions.sortBy {it.stamp}
            for (action in actions) action.act()
        }
    }

}

/**
 * Based on http://stackoverflow.com/questions/27044702/how-to-insert-a-updatable-record-with-json-column-in-postgresql-using-jooq
 */
class PostgresJSONBJacksonJsonNodeBinding : Binding<Any?, JsonNode> {

    override fun converter(): Converter<Any?, JsonNode> {
        return PostgresJSONBJacksonJsonNodeConverter()
    }

    override fun sql(ctx: BindingSQLContext<JsonNode>) {
        // This ::jsonb cast is explicitly needed by PostgreSQL:
        ctx.render().visit(DSL.`val`(ctx.convert(converter()).value())).sql("::jsonb")
    }

    override fun register(ctx: BindingRegisterContext<JsonNode>) {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR)
    }

    override fun set(ctx: BindingSetStatementContext<JsonNode>) {
        ctx.statement().setString(
            ctx.index(),
            convert(ctx.convert(converter()).value(), String::class.java))
    }

    override fun get(ctx: BindingGetResultSetContext<JsonNode>) {
        ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()))
    }

    override fun get(ctx: BindingGetStatementContext<JsonNode>) {
        ctx.convert(converter()).value(ctx.statement().getString(ctx.index()))
    }

    // The below methods aren't needed in PostgreSQL:

    override fun set(ctx: BindingSetSQLOutputContext<JsonNode>) {
        throw SQLFeatureNotSupportedException()
    }

    override fun get(ctx: BindingGetSQLInputContext<JsonNode>) {
        throw SQLFeatureNotSupportedException()
    }
}

class PostgresJSONBJacksonJsonNodeConverter : Converter<Any?, JsonNode> {
    override fun from(t: Any?): JsonNode {
        return if (t == null) NullNode.instance
        else ObjectMapper().readTree(t.toString())
    }

    override fun to(u: JsonNode?): Any? {
        return if (u == null || u == NullNode.instance) null
        else ObjectMapper().writeValueAsString(u)
    }

    override fun fromType(): Class<Any?> {
        @Suppress("UNCHECKED_CAST")
        return Object::class.java as Class<Any?>
    }

    override fun toType(): Class<JsonNode> {
        return JsonNode::class.java
    }
}






