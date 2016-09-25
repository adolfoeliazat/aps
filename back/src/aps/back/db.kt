/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.build
import co.paralleluniverse.fibers.jdbc.FiberDataSource
import com.zaxxer.hikari.HikariDataSource
import org.jooq.SQLDialect
import org.jooq.impl.DSL

object DS {
    val apsTestOnTestServer by lazy {
        build(HikariDataSource()) {
            dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource"
            with(dataSourceProperties) {
                put("serverName", "127.0.0.1")
                put("databaseName", "aps-test")
                put("portNumber", 5433)
                put("user", "postgres")
            }
        }
    }

    val postgresOnTestServer by lazy {
        build(HikariDataSource()) {
            dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource"
            with(dataSourceProperties) {
                put("serverName", "127.0.0.1")
                put("databaseName", "postgres")
                put("portNumber", 5433)
                put("user", "postgres")
            }
        }
    }
}


fun recreateTestDB() {
    DSL.using(DS.postgresOnTestServer.connection, SQLDialect.POSTGRES_9_5).execute("""
            drop database if exists "aps-test";
            create database "aps-test";
        """)
    DSL.using(DS.apsTestOnTestServer.connection, SQLDialect.POSTGRES_9_5).execute(
        DS::class.java.getResource("schema.sql").readText()
    )
}

