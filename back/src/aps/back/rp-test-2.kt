package aps.back

import aps.*
import into.kommon.*
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.data.repository.findOrDie
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
    return MirandaGetGeneratedTestTimestamps.Response(generateTestTimestamps("2014-03-02 04:32:11"))
}

fun MirandaSeedSomeShit.serve(): MirandaSeedSomeShit.Response {
    exhaustive/when (what) {
        is Stuff1 -> {
            val vit = userRepo.save(User(UserFields(
                firstName = "Tony", lastName = "De Vit", email = "vit@test.shit.ua", profilePhone = "+38 (01) 2345678",
                kind = UserKind.CUSTOMER, state = UserState.COOL, passwordHash = hashPassword("vit-secret"),
                aboutMe = "Я Тони-длинный-макарони", adminNotes = "Тони мудак",
                profileRejectionReason = "", banReason = "", subscribedToAllCategories = false,
                common = CommonFields(createdAt = nextRandomOldStamp(), updatedAt = nextRandomOldStamp()), profileUpdatedAt = nextRandomOldStamp()
            )))

            run {
                val customer = vit
                uaOrderRepo.save(UAOrder(UAOrderFields(
                    title = "Король-лягушонок", documentType = UADocumentType.ESSAY, state = UAOrderState.IN_STORE,
                    category = uaDocumentCategoryRepo.findOrDie(const.uaDocumentCategoryID.linguistics),
                    numPages = 53, numSources = 20,
                    minAllowedPriceOffer = 1000_00, maxAllowedPriceOffer = 5000_00,
                    minAllowedDurationOffer = 10 * 24, maxAllowedDurationOffer = 15 * 24,
                    details = "Золотой мяч королевны при игре падает в бездонный колодец, что вызывает поток слёз. Сострадательный лягушонок вызывается помочь вытащить мячик. Королевна в свою очередь обещает, что будет его подругой детства и разделит с ним досуг, стол и кровать. Когда королевна получает мяч назад, то быстро убегает. Всё же, на следующий день лягушка добирается до двери дворца и, по настоянию отца-короля королевна неохотно исполняет своё обещание. Однако, когда лягушонок требует, чтобы она взяла его с собой на кровать, грозя пожаловаться отцу-королю, та от отвращения что было мочи бросает лягушонка об стену. В то же самое мгновение лягушка превращается в статного королевича с красивыми и ласковыми глазами. Выясняется, что над ним тяготели чары злой ведьмы и только королевна могла их разрушить. Наутро он везёт королевскую дочь как свою законную супругу, в роскошной карете в собственное королевство. Во время поездки их сопровождает верный слуга Генрих, который от печали по королевичу заковал своё сердце тремя железными обручами. В дороге, от переполняющей сердце Генриха радости по поводу освобождения его господина, эти железные оковы лопаются с громким треском.",
                    customer = customer, customerFirstName = customer.user.firstName, customerLastName = customer.user.lastName, customerPhone = customer.user.profilePhone, customerEmail = customer.user.email,
                    adminNotes = "Fucking notes 1", confirmationSecret = "", whatShouldBeFixedByCustomer  = "",
                    common = CommonFields(createdAt = nextRandomOldStamp(), updatedAt = nextRandomOldStamp()), movedToStoreAt = nextRandomOldStamp()
                )))
            }
        }
    }
    return MirandaSeedSomeShit.Response()
}

























