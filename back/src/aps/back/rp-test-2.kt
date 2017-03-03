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
    var sqlFileName by notNull<String>()
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
                val snapshotName = req.snapshotName.value

                val PG_HOME = getenv("PG_HOME") ?: die("No fucking PG_HOME")
                val sqlFileName = snapshotDBDumpFile(snapshotName)
                val res = runProcessAndWait(listOf(
                    "$PG_HOME\\bin\\pg_dump.exe",
                    // "--data-only",
                    "--clean",
                    "--no-owner",
                    "--host=127.0.0.1",
                    "--port=5433",
                    "--username=postgres",
                    "--no-password",
                    "--file=$sqlFileName",
                    "aps-test"
                ))

                if (res.exitValue != 0) die("pg_dump said us 'fuck you'")
                snapshotJSONFile(snapshotName).writeText(
                    objectMapper.writeValueAsString(TestState()-{o->
                        o.snapshotName = snapshotName
                        o.browseroidName = req.browseroidName.value
                        o.token = req.token.value
                        o.href = req.href.value
                        o.nextRequestTimestampIndex = req.nextRequestTimestampIndex.value
                        o.sqlFileName = sqlFileName
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
                val snapshotName = req.snapshotName.value
                val testState = objectMapper.readValue(snapshotJSONFile(snapshotName), TestState::class.java)

                val PG_HOME = getenv("PG_HOME") ?: die("No fucking PG_HOME")
                val sqlFileName = snapshotDBDumpFile(snapshotName)
                val res = runProcessAndWait(listOf(
                    "$PG_HOME\\bin\\psql.exe",
                    "--host=127.0.0.1",
                    "--port=5433",
                    "--username=postgres",
                    "--no-password",
                    "--dbname=aps-test",
                    "--file=$sqlFileName"
                ))
                if (res.exitValue != 0) die("psql said us 'fuck you'")

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

private fun snapshotJSONFile(snapshotName: String) =
    File(snapshotFileNameBase(snapshotName) + ".json")

private fun snapshotDBDumpFile(snapshotName: String) =
    snapshotFileNameBase(snapshotName) + ".sql"

private fun snapshotFileNameBase(snapshotName: String) =
    "${const.file.APS_TEMP}/snapshot-$snapshotName"

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
                        dwarnStriking("firstName = ${user.user.firstName}; lastName = ${user.user.lastName}")
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
                val file = File(const.file.testFiles + "/" + req.fileName.value)
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

fun MirandaImposeNextGeneratedUserToken.serve(): MirandaImposeNextGeneratedUserToken.Response {
    TestServerFiddling.nextGeneratedUserToken.set(this.token)
    return MirandaImposeNextGeneratedUserToken.Response()
}

fun MirandaImposeNextGeneratedPassword.serve(): MirandaImposeNextGeneratedPassword.Response {
    TestServerFiddling.nextGeneratedPassword.set(this.password)
    return MirandaImposeNextGeneratedPassword.Response()
}

fun MirandaGetGeneratedTestTimestamps.serve(): MirandaGetGeneratedTestTimestamps.Response {
    return MirandaGetGeneratedTestTimestamps.Response(generateTestTimestamps())
}

























