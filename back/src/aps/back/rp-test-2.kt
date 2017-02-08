package aps.back

import aps.*
import into.kommon.*
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.sql.SQLException
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource
import kotlin.properties.Delegates.notNull

class TestState {
    var snapshotName by notNull<String>()
    var browseroidName by notNull<String>()
    var href by notNull<String>()
    var token: String? = null
    var sqlCommands by notNull<List<String>>()
}

@Servant class ServeTestTakeDBSnapshot(
    val emf: EntityManagerFactory
) : BitchyProcedure() {
    override fun serve() {
        fuckDangerously(FuckDangerouslyParams(
            bpc = bpc,
            makeRequest = {TestTakeDBSnapshotRequest()},
            runShit = fun(ctx, req: TestTakeDBSnapshotRequest): TestTakeDBSnapshotRequest.Response {
                File("${SharedGlobus.APS_TEMP}/snapshot-${req.snapshotName.value}.json").writeText(
                    objectMapper.writeValueAsString(TestState()-{o->
                        o.snapshotName = req.snapshotName.value
                        o.browseroidName = req.browseroidName.value
                        o.token = req.token.value
                        o.href = req.href.value
                        o.sqlCommands = run {
                            val em = emf.createEntityManager()
                            em.transaction.begin()
                            try {
                                cast(em.createNativeQuery("script").resultList)
                            } finally {
                                em.transaction.rollback()
                                em.close()
                            }
                        }
                    })
                )
                return TestTakeDBSnapshotRequest.Response()
            }
        ))
    }
}

@Servant class ServeTestRestoreDBSnapshot(
    val emf: EntityManagerFactory
    ) : BitchyProcedure() {
    override fun serve() {
        fuckDangerously(FuckDangerouslyParams(
            bpc = bpc,
            makeRequest = {TestRestoreDBSnapshotRequest()},
            runShit = fun(ctx, req: TestRestoreDBSnapshotRequest): TestRestoreDBSnapshotRequest.Response {
                imf()
//                val s = repo.findBySnapshotName(req.snapshotName.value)!!
//                return TestRestoreDBSnapshotRequest.Response(
//                    browseroidName = s.browseroidName,
//                    href = s.href,
//                    token = s.token)

//                val em = emf.createEntityManager()
//                em.transaction.begin()
//                try {
//                    dwarnStriking("Fucking around, boy? I'll make a little DB snapshot for you...")
//                    em.createNativeQuery("script to '${SharedGlobus.APS_TEMP}/dbsnap-${req.snapshotName.value}.sql'").resultList
//                    dwarnStriking("Done. Use it with care, son. Time for a friendly final word... Fuck you")
//                    return TestRestoreDBSnapshotRequest.Response()
//                } finally {
//                    em.transaction.rollback()
//                    em.close()
//                }
            }
        ))
    }
}




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








//@RemoteProcedureFactory fun serveTestTakeSnapshot() = testProcedure(
//    {TestTakeSnapshotRequest()},
//    needsDB = true,
//    runShit = fun(ctx, req): TestTakeSnapshotRequest.Response {
//        dwarnStriking("Taking snapshot: ${req.name.value} @ ${req.url.value}")
////        KEY_VALUE_STORE.let {t->
////            ctx.insertShit("Store URL", t) {it
////                .set(t.KEY, bconst.kvs.test.snapshotURL)
////                .set(t.VALUE, objectMapper.valueToTree<JsonNode>(req.url.value))
////                .onConflict(t.KEY)
////                .doUpdate()
////                .set(t.VALUE, objectMapper.valueToTree<JsonNode>(req.url.value))
////                .execute()
////            }
////        }
//        DB.apsTestSnapshotOnTestServer(req.name.value).recreate(template = DB.apsTestOnTestServer)
//        return TestTakeSnapshotRequest.Response()
//    }
//)
//
//@RemoteProcedureFactory fun serveTestLoadSnapshot() = testProcedure(
//    {TestLoadSnapshotRequest()},
//    needsDB = true,
//    runShit = fun(ctx, req): TestLoadSnapshotRequest.Response {
//        val rec = tracingSQL("Load snapshot") {
//            ctx.q.fetchOne(KEY_VALUE_STORE, KEY_VALUE_STORE.KEY.eq(bconst.kvs.test.snapshotURL))
//        }
//        val url = objectMapper.treeToValue(rec.value, String::class.java)
//        dwarnStriking("Snapshot URL: $url")
//        return TestLoadSnapshotRequest.Response(url)
//    }
//)
