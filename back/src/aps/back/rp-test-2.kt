package aps.back

import aps.*
import into.kommon.*
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.*
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource
import kotlin.properties.Delegates.notNull

class TestState {
    var snapshotName by notNull<String>()
    var browseroidName by notNull<String>()
    var href by notNull<String>()
    var token: String? = null
    var sqlCommands by notNull<List<String>>()
    var nextRequestTimestampIndex by notNull<Int>()
}

@Servant class ServeTestTakeTestPointSnapshot(
    val emf: EntityManagerFactory
) : BitchyProcedure() {
    override fun serve() {
        fuckDangerously(FuckDangerouslyParams(
            bpc = bpc,
            makeRequest = {TestTakeTestPointSnapshotRequest()},
            runShit = fun(ctx, req: TestTakeTestPointSnapshotRequest): TestTakeTestPointSnapshotRequest.Response {
                snapshotFile(req.snapshotName.value).writeText(
                    objectMapper.writeValueAsString(TestState()-{o->
                        o.snapshotName = req.snapshotName.value
                        o.browseroidName = req.browseroidName.value
                        o.token = req.token.value
                        o.href = req.href.value
                        o.nextRequestTimestampIndex = req.nextRequestTimestampIndex.value
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
                return TestTakeTestPointSnapshotRequest.Response()
            }
        ))
    }
}

@Servant class ServeTestRestoreTestPointSnapshot : BitchyProcedure() {
    override fun serve() {
        fuckDangerously(FuckDangerouslyParams(
            bpc = bpc,
            makeRequest = {TestRestoreTestPointSnapshotRequest()},
            runShit = fun(ctx, req: TestRestoreTestPointSnapshotRequest): TestRestoreTestPointSnapshotRequest.Response {
                val testState = objectMapper.readValue(snapshotFile(req.snapshotName.value), TestState::class.java)

                val ds = springctx.getBean(DataSource::class.java)
                ds.connection.use {con->
                    testState
                        .sqlCommands
                        .filter {it.toLowerCase().startsWith("insert into ")}
                        .reversed()
                        .forEach {sql->
                            clog("Restoring: $sql")
                            con.prepareStatement(sql).use(PreparedStatement::execute)
                        }
                }

                return TestRestoreTestPointSnapshotRequest.Response(
                    browseroidName = testState.browseroidName,
                    href = testState.href,
                    token = testState.token,
                    nextRequestTimestampIndex = testState.nextRequestTimestampIndex
                )
            }
        ))
    }
}

private fun snapshotFile(snapshotName: String) =
    File("${SharedGlobus.APS_TEMP}/snapshot-$snapshotName.json")


@Servant class ServeTestSQLFiddle : BitchyProcedure() {
    override fun serve() {
        fuckDangerously(FuckDangerouslyParams(
            bpc = bpc, makeRequest = {TestSQLFiddleRequest()},
            runShit = fun(ctx, req: TestSQLFiddleRequest): TestSQLFiddleRequest.Response {
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
        ))
    }
}

@Servant class ServeTestCodeFiddle : BitchyProcedure() {
    override fun serve() {
        fuckDangerously(FuckDangerouslyParams(
            bpc = bpc, makeRequest = {TestCodeFiddleRequest()},
            runShit = fun(ctx, req: TestCodeFiddleRequest): TestCodeFiddleRequest.Response {
                when (req.what.value) {
                    "fuck1" -> {
                        val repo = springctx.getBean(UserTokenRepository::class.java)
                        val ut = repo.findOne(1) ?: wtf("No token with ID 1")
                        val user = ut.user!!
                        dwarnStriking("firstName = ${user.firstName}; lastName = ${user.lastName}")
                    }
                    else -> wtf("what = ${req.what.value}")
                }
                return TestCodeFiddleRequest.Response()
            }
        ))
    }
}

@Servant class ServeTestGetFileUploadData : BitchyProcedure() {
    override fun serve() {
        fuckDangerously(FuckDangerouslyParams(
            bpc = bpc,
            makeRequest = {TestGetFileUploadDataRequest()},
            runShit = fun(ctx, req: TestGetFileUploadDataRequest): TestGetFileUploadDataRequest.Response {
                val file = File(const.test.filesRoot + req.fileName.value)
                val bytes = file.readBytes()
                return TestGetFileUploadDataRequest.Response(
                    name = file.name,
                    size = bytes.size,
                    base64 = Base64.getEncoder().encodeToString(bytes)
                )
            }
        ))
    }
}





















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
