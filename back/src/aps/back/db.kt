/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables
import aps.back.generated.jooq.tables.records.UserRolesRecord
import aps.back.generated.jooq.tables.records.UserTokensRecord
import aps.back.generated.jooq.tables.records.UsersRecord
import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*
import kotlin.reflect.KProperty

//var DS = DSClass()
//
//class DSClass {
//    private val shit = Collections.synchronizedList(mutableListOf<HikariDataSource>())
//
//    val postgresOnTestServer by lazyDSOnTestServer("postgres")
//    val apsTestOnTestServer by lazyDSOnTestServer("aps-test")
//    val testTemplateUA1OnTestServer by lazyDSOnTestServer("test-template-ua-1")
//
//    /**
//     * For tests only
//     */
//    fun shutDownAllShit() {
//        for (piece in shit) piece.close()
//        DS = DSClass()
//    }
//
//    private fun lazyDSOnTestServer(databaseName: String) = lazy {
//        build(HikariDataSource()) {
//            dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource"
//            with(dataSourceProperties) {
//                put("serverName", "127.0.0.1")
//                put("databaseName", databaseName)
//                put("portNumber", 5433)
//                put("user", "postgres")
//            }
//            shit.add(this)
//        }
//    }
//}

object DB {
    val testTemplateUA1 = Database("127.0.0.1", 5433, "test-template-ua-1", "postgres", allowRecreation = true)
    val apsTestOnTestServer = Database("127.0.0.1", 5433, "aps-test", "postgres", allowRecreation = true)

    private val systemDatabases = mapOf(
        5432 to Database("127.0.0.1", 5432, "postgres", "postgres"), // Dev, on disk
        5433 to Database("127.0.0.1", 5433, "postgres", "postgres")  // Test, on memory drive
    )

    class Database(val host: String, val port: Int, val name: String, val user: String, val password: String? = null, val allowRecreation: Boolean = false) {
        private val dslazy = relazy {HikariDataSource().applet {o->
            o.dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource"
            o.dataSourceProperties.let {o->
                o.put("serverName", host)
                o.put("databaseName", name)
                o.put("portNumber", port)
                o.put("user", user)
                o.put("password", password)
            }
        }}
        val ds by dslazy

        fun recreate(template: String? = null) {
            if (!allowRecreation) bitch("You crazy? I'm not recreating THIS: $this")

            ds.close()
            dslazy.reset()

            val sysdb = systemDatabases[port] ?: wtf("DB port $port")
            sysdb.joo{q->
                q.execute("""
                    drop database if exists "$name";
                    create database "$name" ${if (template != null) "template = \"$template\"" else ""};
                """)
            }

            if (template == null) {
                joo{q->
                    q.execute(DB::class.java.getResource("schema.sql").readText())
                }
            }
        }

        fun <T> joo(act: (DSLContext) -> T): T {
            val connection = ds.connection
            try {
                val q = DSL.using(connection, SQLDialect.POSTGRES_9_5)
                return act(q)
            } finally {
                connection.close()
            }
        }

        override fun toString() = "Database(host='$host', port=$port, name='$name', user='$user')"
    }
}


//fun recreateTestDB(databaseName: String, template: String? = null) {
//    DS.shutDownAllShit()
//
//    DSL.using(DS.postgresOnTestServer.connection, SQLDialect.POSTGRES_9_5).execute("""
//        drop database if exists "$databaseName";
//        create database "$databaseName" ${if (template != null) "template = \"$template\"" else ""};
//    """)
//
//    if (template == null) {
//        DSL.using(ds.connection, SQLDialect.POSTGRES_9_5).execute(
//            DSClass::class.java.getResource("schema.sql").readText()
//        )
//    }
//}

val PG_LOCAL_DATE_TIME = DateTimeFormatterBuilder()
    .parseCaseInsensitive()
    .append(DateTimeFormatter.ISO_LOCAL_DATE)
    .appendLiteral(' ')
    .append(DateTimeFormatter.ISO_LOCAL_TIME)
    .toFormatter()

fun createTestTemplateUA1DB() {
    resetImposed()

//    val jooq = DSL.using(DS.testTemplateUA1OnTestServer.connection, SQLDialect.POSTGRES_9_5)
    with(DB.testTemplateUA1) {
        recreate()
        joo{q->
            val secretHash = "\$2a\$10\$x5bq4zVvcyTb2oUb5.fhreJfl/2NqsaH3TcAwm/C1apAazlBJX2t6" // secret
            var nextUserID = 101L

            class Action(val stamp: Timestamp, val act: () -> Unit)
            val actions = mutableListOf<Action>()

            class UserSpec(val user: UsersRecord, val roles: Iterable<UserRole> = listOf())

            fun addInsertUserActions(_kind: UserKind, us: Iterable<UserSpec>) {
                for (u in us) {
                    actions.add(Action(u.user.insertedAt) {
                        dlog("Inserting ${_kind.name} ${u.user.firstName} at ${u.user.insertedAt}")

                        q.insertInto(Tables.USERS).set(u.user.apply {
                            id = nextUserID++
                            kind = _kind.name
                            lang = Language.UA.name
                            state = UserState.COOL.name
                            passwordHash = secretHash
                        })
                        q.insertInto(Tables.USER_TOKENS).set(UserTokensRecord().apply {
                            userId = u.user.id
                            token = "temp-${u.user.id}"
                        })

                        for (r in u.roles) {
                            q.insertInto(Tables.USER_ROLES).set(UserRolesRecord().apply {
                                userId = u.user.id
                                role = r.name
                            })
                        }
                    })
                }
            }

            fun stamp(s: String): Timestamp = Timestamp.valueOf(LocalDateTime.parse(s, PG_LOCAL_DATE_TIME))

            addInsertUserActions(UserKind.ADMIN, listOf(
                UserSpec(UsersRecord().apply {firstName = "Дася"; lastName = "Админовна"; email = "dasja@test.shit.ua"; insertedAt = stamp("2016/07/10 13:14:15")},listOf(UserRole.SUPPORT)),
                UserSpec(UsersRecord().apply {firstName = "Тодд"; lastName = "Суппортод"; email = "todd@test.shit.ua"; insertedAt = stamp("2016-07-13 02:44:05")}, listOf(UserRole.SUPPORT)),
                UserSpec(UsersRecord().apply {firstName = "Алиса"; lastName = "Планктоновна"; email = "alice@test.shit.ua"; insertedAt = stamp("2016-07-11 20:28:17")}, listOf(UserRole.SUPPORT)),
                UserSpec(UsersRecord().apply {firstName = "Элеанора"; lastName = "Суконская"; email = "eleanor@test.shit.ua"; insertedAt = stamp("2016-07-11 20:28:17")}, listOf(UserRole.SUPPORT))
            ))

            addInsertUserActions(UserKind.WRITER, listOf(
                UserSpec(UsersRecord().apply {firstName = "Франц"; lastName = "Кафка"; email = "kafka@test.shit.ua"; insertedAt = stamp("2016-07-11 01:05:30")}),
                UserSpec(UsersRecord().apply {firstName = "Лев"; lastName = "Толстой"; email = "leo@test.shit.ua"; insertedAt = stamp("2016-07-29 22:54:42")}),
                UserSpec(UsersRecord().apply {firstName = "Николай"; lastName = "Гоголь"; email = "gogol@test.shit.ua"; insertedAt = stamp("2016-07-13 17:32:27")}),
                UserSpec(UsersRecord().apply {firstName = "Федор"; lastName = "Достоевский"; email = "fedor@test.shit.ua"; insertedAt = stamp("2016-08-04 14:41:07")}),
                UserSpec(UsersRecord().apply {firstName = "Александр"; lastName = "Пушкин"; email = "pushkin@test.shit.ua"; insertedAt = stamp("2016-07-19 21:35:30")}),
                UserSpec(UsersRecord().apply {firstName = "Георг"; lastName = "Гегель"; email = "hegel@test.shit.ua"; insertedAt = stamp("2016-07-22 07:19:47")}),
                UserSpec(UsersRecord().apply {firstName = "Иммануил"; lastName = "Кант"; email = "kant@test.shit.ua"; insertedAt = stamp("2016-07-13 05:54:20")}),
                UserSpec(UsersRecord().apply {firstName = "Мигель"; lastName = "Сервантес"; email = "miguel@test.shit.ua"; insertedAt = stamp("2016-07-16 22:25:56")}),
                UserSpec(UsersRecord().apply {firstName = "Карлос"; lastName = "Кастанеда"; email = "carlos@test.shit.ua"; insertedAt = stamp("2016-07-29 13:39:06")}),
                UserSpec(UsersRecord().apply {firstName = "Елена"; lastName = "Блаватская"; email = "blava@test.shit.ua"; insertedAt = stamp("2016-07-27 12:06:11")}),
                UserSpec(UsersRecord().apply {firstName = "Джейн"; lastName = "Остин"; email = "jane@test.shit.ua"; insertedAt = stamp("2016-07-15 15:13:27")}),
                UserSpec(UsersRecord().apply {firstName = "Мэри"; lastName = "Шелли"; email = "mary@test.shit.ua"; insertedAt = stamp("2016-07-22 01:33:17")}),
                UserSpec(UsersRecord().apply {firstName = "Франсуаза"; lastName = "Саган"; email = "francoise@test.shit.ua"; insertedAt = stamp("2016-08-08 16:04:53")}),
                UserSpec(UsersRecord().apply {firstName = "Жорж"; lastName = "Санд"; email = "sand@test.shit.ua"; insertedAt = stamp("2016-07-16 20:47:26")}),
                UserSpec(UsersRecord().apply {firstName = "Агата"; lastName = "Кристи"; email = "agatha@test.shit.ua"; insertedAt = stamp("2016-08-12 09:59:53")})
            ))

            addInsertUserActions(UserKind.CUSTOMER, listOf(
                UserSpec(UsersRecord().apply {firstName = "Пися"; lastName = "Камушкин"; email = "pisya@test.shit.ua"; insertedAt = stamp("2016-08-02 14:38:15")}),
                UserSpec(UsersRecord().apply {firstName = "Люк"; lastName = "Хуюк"; email = "luke@test.shit.ua"; insertedAt = stamp("2016-07-14 18:36:35")}),
                UserSpec(UsersRecord().apply {firstName = "Павло"; lastName = "Зибров"; email = "zibrov@test.shit.ua"; insertedAt = stamp("2016-08-09 11:36:01")}),
                UserSpec(UsersRecord().apply {firstName = "Василий"; lastName = "Теркин"; email = "terkin@test.shit.ua"; insertedAt = stamp("2016-07-16 18:59:35")}),
                UserSpec(UsersRecord().apply {firstName = "Иво"; lastName = "Бобул"; email = "ivo@test.shit.ua"; insertedAt = stamp("2016-07-16 22:51:51")}),
                UserSpec(UsersRecord().apply {firstName = "Регина"; lastName = "Дубовицкая"; email = "regina@test.shit.ua"; insertedAt = stamp("2016-07-12 06:31:58")}),
                UserSpec(UsersRecord().apply {firstName = "Евгений"; lastName = "Ваганович"; email = "vaganovich@test.shit.ua"; insertedAt = stamp("2016-08-06 03:59:58")}),
                UserSpec(UsersRecord().apply {firstName = "Павел"; lastName = "Дристальский"; email = "paul@test.shit.ua"; insertedAt = stamp("2016-07-27 07:57:02")}),
                UserSpec(UsersRecord().apply {firstName = "Тело"; lastName = "Странное"; email = "telo@test.shit.ua"; insertedAt = stamp("2016-07-26 08:50:23")}),
                UserSpec(UsersRecord().apply {firstName = "Арчибальд"; lastName = "Нелеподлиннаяфамилияуменя"; email = "archie@test.shit.ua"; insertedAt = stamp("2016-08-12 10:26:14")}),
                UserSpec(UsersRecord().apply {firstName = "Даздраперма"; lastName = "Дивизионная"; email = "perma@test.shit.ua"; insertedAt = stamp("2016-08-11 07:56:00")}),
                UserSpec(UsersRecord().apply {firstName = "Уменяреальносамыйдлинный"; lastName = "Ымя"; email = "ymya@test.shit.ua"; insertedAt = stamp("2016-08-06 08:23:56")}),
                UserSpec(UsersRecord().apply {firstName = "Варсоновий"; lastName = "Оптинский"; email = "varso@test.shit.ua"; insertedAt = stamp("2016-08-04 08:29:23")}),
                UserSpec(UsersRecord().apply {firstName = "Евстафий"; lastName = "Антиохийский"; email = "anti@test.shit.ua"; insertedAt = stamp("2016-08-12 17:38:34")}),
                UserSpec(UsersRecord().apply {firstName = "Ксенофонт"; lastName = "Тутанский"; email = "xeno@test.shit.ua"; insertedAt = stamp("2016-07-10 17:07:24")})
            ))

            actions.sortBy {it.stamp}
            for (action in actions) action.act()
        }
    }
}

