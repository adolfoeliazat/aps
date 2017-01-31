package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import com.fasterxml.jackson.databind.JsonNode
import java.io.PrintWriter
import java.io.StringWriter
import java.sql.SQLException
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@RemoteProcedureFactory fun serveTestTakeSnapshot() = testProcedure(
    {TestTakeSnapshotRequest()},
    needsDB = true,
    runShit = fun(ctx, req): TestTakeSnapshotRequest.Response {
        dwarnStriking("Taking snapshot: ${req.name.value} @ ${req.url.value}")
//        KEY_VALUE_STORE.let {t->
//            ctx.insertShit("Store URL", t) {it
//                .set(t.KEY, bconst.kvs.test.snapshotURL)
//                .set(t.VALUE, objectMapper.valueToTree<JsonNode>(req.url.value))
//                .onConflict(t.KEY)
//                .doUpdate()
//                .set(t.VALUE, objectMapper.valueToTree<JsonNode>(req.url.value))
//                .execute()
//            }
//        }
        DB.apsTestSnapshotOnTestServer(req.name.value).recreate(template = DB.apsTestOnTestServer)
        return TestTakeSnapshotRequest.Response()
    }
)

@RemoteProcedureFactory fun serveTestLoadSnapshot() = testProcedure(
    {TestLoadSnapshotRequest()},
    needsDB = true,
    runShit = fun(ctx, req): TestLoadSnapshotRequest.Response {
        val rec = tracingSQL("Load snapshot") {
            ctx.q.fetchOne(KEY_VALUE_STORE, KEY_VALUE_STORE.KEY.eq(bconst.kvs.test.snapshotURL))
        }
        val url = objectMapper.treeToValue(rec.value, String::class.java)
        dwarnStriking("Snapshot URL: $url")
        return TestLoadSnapshotRequest.Response(url)
    }
)

@RemoteProcedureFactory fun serveTestSQLFiddle() = testProcedure(
    {TestSQLFiddleRequest()},
    needsDB = true,
    runShit = fun(ctx, req): TestSQLFiddleRequest.Response {
        try {
            val sql = req.input.value
            val ds = springctx.getBean(DataSource::class.java)
            ds.connection.use {con->
                val rs = con.prepareStatement(sql).executeQuery()
                val meta = rs.metaData
                var rowCount = 0
                val spew = StringBuilder()
                while (rs.next()) {
                    ++rowCount
                    spew.appendln("---- Row $rowCount ---------------")
                    for (col in 1..meta.columnCount) {
                        spew.appendln(meta.getColumnName(col) + ": " + rs.getObject(col))
                    }
                }
                return TestSQLFiddleRequest.Response(
                    isError = false,
                    spew = "Got $rowCount rows(s)\n" + spew.toString())
            }
        } catch(e: SQLException) {
            val sw = StringWriter()
            e.printStackTrace(PrintWriter(sw))
            return TestSQLFiddleRequest.Response(
                isError = true,
                spew = sw.toString()
            )
        }
    }
)


