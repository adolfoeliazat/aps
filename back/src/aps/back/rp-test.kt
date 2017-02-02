/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.USERS
import aps.back.generated.jooq.enums.*
import aps.back.generated.jooq.tables.JQUaOrderFiles.*
import aps.back.generated.jooq.tables.records.*
import com.google.debugging.sourcemap.SourceMapConsumerFactory
import com.google.debugging.sourcemap.SourceMapping
import com.sun.jna.platform.win32.User32
import into.kommon.*
import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.jooq.Record
import org.jooq.UpdateSetMoreStep
import org.jooq.UpdateSetStep
import org.mindrot.jbcrypt.BCrypt
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.awt.MouseInfo
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.sql.Timestamp
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

object TestServerFiddling {
    @Volatile var nextRequestTimestamp: Timestamp? = null
    @Volatile var rejectAllRequestsNeedingDB: Boolean = false
    @Volatile var nextGeneratedPassword: String? = null
    @Volatile var nextRequestError: String? = null
}

fun <Req : RequestMatumba, Res : CommonResponseFields>
testProcedure(
    req: (ProcedureContext) -> Req,
    runShit: (ProcedureContext, Req) -> Res,
    needsDB: Boolean? = null,
    logRequestJSON: Boolean? = null
): (HttpServletRequest, HttpServletResponse) -> Unit =
    remoteProcedure(ProcedureSpec(
        req,
        runShit = runShit,
        wrapInFormResponse = false,
        needsDB = needsDB ?: false,
        needsDangerousToken = true,
        needsUser = NeedsUser.NO,
        userKinds = setOf(),
        considerNextRequestTimestampFiddling = false,
        logRequestJSON = logRequestJSON ?: true
    ))

@RemoteProcedureFactory fun imposeNextRequestTimestamp() = testProcedure(
    {ImposeNextRequestTimestampRequest()},
    runShit = fun(ctx, req): GenericResponse {
        TestServerFiddling.nextRequestTimestamp = stringToStamp(req.stamp.value)
        return GenericResponse()
    }
)

@RemoteProcedureFactory fun imposeNextRequestError() = testProcedure(
    {ImposeNextRequestErrorRequest()},
    runShit = fun(ctx, req): GenericResponse {
        TestServerFiddling.nextRequestError = req.error.value ?: const.msg.serviceFuckedUp
        return GenericResponse()
    }
)

@RemoteProcedureFactory fun imposeNextGeneratedPassword() = testProcedure(
    {ImposeNextGeneratedPasswordRequest()},
    runShit = fun(ctx, req): GenericResponse {
        TestServerFiddling.nextGeneratedPassword = req.password.value
        return GenericResponse()
    }
)

@RemoteProcedureFactory fun resetTestDatabase() = testProcedure(
    {ResetTestDatabaseRequest()},
    runShit = fun(ctx, req): GenericResponse {
        DB.apsTestOnTestServer.recreate()
        return GenericResponse()
    }
)

@RemoteProcedureFactory fun serveRecreateTestDatabaseSchema() = testProcedure(
    {RecreateTestDatabaseSchemaRequest()},
    runShit = fun(ctx, req): GenericResponse {
        springctx = AnnotationConfigApplicationContext(AppConfig::class.java)
//        val beanFactory = springctx.beanFactory as BeanDefinitionRegistry
//        beanFactory.removeBeanDefinition("transactionManager")
//        beanFactory.removeBeanDefinition("entityManagerFactory")
//        val beanFactory = springctx.beanFactory as DefaultListableBeanFactory
//        beanFactory.destroySingleton("transactionManager")
//        beanFactory.destroySingleton("entityManagerFactory")

        val templateDB =
            req.snapshotID.value?.let {DB.apsTestSnapshotOnTestServer(it)}
            ?: req.templateDB.value?.let {DB.byID(it)}

        val db = DB.apsTestOnTestServer
        if (templateDB == null) {
            db.recreateSchema()
            db.joo {q->
                USERS.let {
                    tracingSQL("Insert Dasja") {q
                        .insertInto(USERS)
                        .set(it.INSERTED_AT, ctx.requestTimestamp)
                        .set(it.UPDATED_AT, ctx.requestTimestamp)
                        .set(it.EMAIL, "dasja@test.shit.ua")
                        .set(it.KIND, JQUserKind.ADMIN)
                        .set(it.LANG, ctx.lang.name)
                        .set(it.STATE, UserState.COOL.name)
                        .set(it.PASSWORD_HASH, BCrypt.hashpw("dasjasecret", BCrypt.gensalt()))
                        .set(it.FIRST_NAME, "Дася")
                        .set(it.LAST_NAME, "Админовна")
                        .set(it.ADMIN_NOTES, "")
                        .execute()
                    }
                }
            }
        } else {
            db.recreate(template = templateDB)
        }
        return GenericResponse()
    }
)

@RemoteProcedureFactory fun resetTestDatabaseAlongWithTemplate() = testProcedure(
    {ResetTestDatabaseAlongWithTemplateRequest()},
    runShit = fun(ctx, req): GenericResponse {
        val templateDB = DB.byNameOnTestServer(req.templateDB.value)

        if (req.recreateTemplate.value) {
            templateDB.recreate()
        }

        DB.apsTestOnTestServer.recreate(template = templateDB)
        return GenericResponse()
    }
)

@RemoteProcedureFactory fun serveGetSentEmails() = testProcedure(
    {RequestMatumba()},
    runShit = fun(ctx, req): GetSentEmailsRequest.Response {
        return GetSentEmailsRequest.Response(EmailMatumba.sentEmails)
    }
)

@RemoteProcedureFactory fun serveClearSentEmails() = testProcedure(
    {RequestMatumba()},
    runShit = fun(ctx, req): GenericResponse {
        EmailMatumba.sentEmails.clear()
        return GenericResponse()
    }
)

@RemoteProcedureFactory fun worldPoint() = testProcedure(
    {WorldPointRequest()},
    runShit = fun(ctx, req): GenericResponse {
        val oldRejectAllRequests = TestServerFiddling.rejectAllRequestsNeedingDB
        TestServerFiddling.rejectAllRequestsNeedingDB = true

        try {
            val snapshotDBName = "world_point_${req.pointName.value}"

            val databaseToCreate: String; val databaseToUseAsTemplate: String
            when (req.action.value) {
                WorldPointRequest.Action.SAVE -> {
                    databaseToCreate = snapshotDBName
                    databaseToUseAsTemplate = DB.apsTestOnTestServer.name
                }
                WorldPointRequest.Action.RESTORE -> {
                    databaseToCreate = DB.apsTestOnTestServer.name
                    databaseToUseAsTemplate = snapshotDBName
                }
            }

            DB.apsTestOnTestServer.close()
            DB.postgresOnTestServer.joo {
                tracingSQL("Recreate database") {it.execute(""""
                    drop database if exists "$databaseToCreate";
                    create database "$databaseToCreate" template = "$databaseToUseAsTemplate";
                """)}
            }
        } finally {
            TestServerFiddling.rejectAllRequestsNeedingDB = oldRejectAllRequests
        }
        return GenericResponse()
    }
)

val backendInstanceID = "" + UUID.randomUUID()

@RemoteProcedureFactory fun getSoftwareVersion() = testProcedure(
    {GetSoftwareVersionRequest()},
    logRequestJSON = false,
    runShit = fun(ctx, req): GetSoftwareVersionRequest.Response {
        val path = Paths.get("$APS_HOME/front/out/front-enhanced.js")
        val attrs = Files.readAttributes(path, BasicFileAttributes::class.java)
        return GetSoftwareVersionRequest.Response(
            ctime = "" + Math.max(attrs.creationTime().toMillis(), attrs.lastModifiedTime().toMillis()),
            backendInstanceID = backendInstanceID)
    }
)

@RemoteProcedureFactory fun getGeneratedShit() = testProcedure(
    {RequestMatumba()},
    runShit = fun(ctx, req): GetGeneratedShitRequest.Response {
        return GetGeneratedShitRequest.Response(GodServlet::class.java.getResource("generated-shit.js").readText())
    }
)


@RemoteProcedureFactory fun openSourceCode() = testProcedure(
    {OpenSourceCodeRequest()},
    runShit = fun(ctx, req): OpenSourceCodeRequest.Response {
        val sourceLocation = req.sourceLocation.value.replace("\\", "/")
        val firstColon = sourceLocation.indexOf(':')
        var afterLine = sourceLocation.indexOf(':', firstColon + 1)
        if (firstColon == -1) bitch("I want a colon in source location")
        if (afterLine == -1) afterLine = sourceLocation.length

        val filePartEnd = firstColon
        val filePart = sourceLocation.substring(0, filePartEnd)
        val file =
            if (filePart.startsWith("APS/")) {
                APS_HOME + "/" + filePart.substring("APS/".length)
            } else run {
                for (f in File("$APS_HOME/back/src").walkTopDown())
                    if (f.name == filePart) return@run f.absolutePath
                bitch("Obscure backend file: $filePart")
            }
        val line = sourceLocation.substring(firstColon + 1, afterLine)

        val pb = ProcessBuilder()
        val cmd = pb.command()
        cmd.addAll(listOf(bconst.ideaExe, APS_HOME, "--line", line, file))
        dlog("Executing external command:", cmd.joinToString(" "))
        pb.inheritIO()
        val proc = pb.start()
        val exitCode = proc.waitFor()
        dlog("External command finished with code", exitCode)

        return OpenSourceCodeRequest.Response(error = if (exitCode == 0) null else "Bad exit code: $exitCode")
    }
)

@RemoteProcedureFactory fun testSetUserFields() = testProcedure(
    {TestSetUserFieldsRequest()},
    needsDB = true,
    runShit = fun(ctx, req): GenericResponse {
        var step = tracingSQL("Update user") {ctx.q
            .update(USERS)
            .set(USERS.ID, USERS.ID)
        }

        req.state.let {if (it.specified) step = step.set(USERS.STATE, it.value.name)}
        req.profileRejectionReason.let {if (it.specified) step = step.set(USERS.PROFILE_REJECTION_REASON, it.value)}
        req.phone.let {if (it.specified) step = step.set(USERS.PHONE, it.value)}
        req.aboutMe.let {if (it.specified) step = step.set(USERS.ABOUT_ME, it.value)}
        req.banReason.let {if (it.specified) step = step.set(USERS.BAN_REASON, it.value)}
        req.profileUpdatedAt.let {if (it.specified) step = step.set(USERS.PROFILE_UPDATED_AT, stringToStamp(it.value))}
        req.insertedAt.let {if (it.specified) step = step.set(USERS.INSERTED_AT, stringToStamp(it.value))}

        step
            .where(USERS.EMAIL.eq(req.email.value))
            .execute()
        return GenericResponse()
    }
)

@RemoteProcedureFactory fun fuckingRemoteProcedure() = testProcedure(
    {FuckingRemoteProcedureRequest()},
    needsDB = true,
    runShit = fun (ctx, req): JSONResponse {
        val rmap = shittyObjectMapper.readValue(req.json.value, Map::class.java)
        val proc: String = cast(rmap["proc"])

        val res: Any? = run {when (proc) {
            "loadTestShit" -> frp_loadTestShit(rmap)
            "updateTestShit" -> frp_updateTestShit(rmap)
            "robotClickOnChrome" -> frp_robotClickOnChrome(rmap)
            "robotTypeTextCRIntoWindowTitledOpen" -> frp_robotTypeTextCRIntoWindowTitledOpen(rmap)
            "ping" -> frp_ping(rmap)
            "resetLastDownloadedFile" -> frp_resetLastDownloadedFile(rmap)
            "getLastDownloadedPieceOfShit" -> frp_getLastDownloadedPieceOfShit(rmap)
            "executeSQL" -> frp_executeSQL(ctx, rmap)
            "luceneParseRussian" -> frp_luceneParseRussian(rmap)
            else -> wtf("proc: $proc")

        }}

        return JSONResponse(shittyObjectMapper.writeValueAsString(res))
    }
)

fun frp_resetLastDownloadedFile(rmap: Map<*, *>) {
    BackGlobus.lastDownloadedPieceOfShit = null
}

fun frp_getLastDownloadedPieceOfShit(rmap: Map<*, *>): PieceOfShitDownload? {
    return BackGlobus.lastDownloadedPieceOfShit
}

fun frp_loadTestShit(rmap: Map<*, *>): String? {
    val id: String = cast(rmap["id"])
    val file = File("$APS_HOME/front/test-shit/$id")
    return if (file.exists()) file.readText()
    else null
}

fun frp_updateTestShit(rmap: Map<*, *>) {
    val id: String = cast(rmap["id"])
    val newValue: String = cast(rmap["newValue"])
    serveHardenScreenHTMLRequest(HardenScreenHTMLRequest()-{o->
        o.assertionID = id
        o.html = newValue
    })
}

fun frp_robotClickOnChrome(rmap: Map<*, *>) {
    val hwnd =
        User32.INSTANCE.FindWindow(null, "APS UA - Google Chrome")
        ?: User32.INSTANCE.FindWindow(null, "Writer UA - Google Chrome")
        ?: bitch("No necessary Chrome window")
    User32.INSTANCE.SetForegroundWindow(hwnd) || bitch("Cannot bring Chrome to foreground")
    val origLocation = MouseInfo.getPointerInfo().location
    val robot = Robot()
    robot.mouseMove(18, 416)
    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
    robot.mouseMove(origLocation.x, origLocation.y)
}

fun frp_robotTypeTextCRIntoWindowTitledOpen(rmap: Map<*, *>) {
    val text: String = cast(rmap["text"])

    (1..3).forEach {
        val hwnd = User32.INSTANCE.FindWindow(null, "Open")
        if (hwnd == null) {
            Thread.sleep(500)
            return@forEach
        }

        User32.INSTANCE.SetForegroundWindow(hwnd) || bitch("Cannot bring Open window to foreground")
        Thread.sleep(100)
        robotTypeTextCR(text)
        return
    }

    bitch("I'm sick of waiting for Open window")
}

@RemoteProcedureFactory fun ping() = publicProcedure(
    {GenericRequest()},
    runShit = fun(ctx, req): GenericResponse {
        return GenericResponse()
    }
)

fun frp_ping(rmap: Map<*, *>): String {
    return "pong"
}

fun frp_executeSQL(ctx: ProcedureContext, rmap: Map<*, *>) {
    val descr: String = cast(rmap["descr"])
    val sql: String = cast(rmap["sql"])
    tracingSQL(descr) {ctx.q.execute(sql)}
}

private fun robotTypeTextCR(text: String) {
    val robot = Robot()
    text.forEach {c->
        // dwarnStriking("Typing key: " + c)
        var holdShift = c.isLetter() && c.isUpperCase()

        val keyCode = when {
            c.isLetterOrDigit() -> c.toUpperCase().toInt()
            else -> when (c) {
                ' ' -> KeyEvent.VK_SPACE
                ':' -> {holdShift = true; KeyEvent.VK_SEMICOLON}
                '.' -> KeyEvent.VK_PERIOD
                '/' -> KeyEvent.VK_SLASH
                '\\' -> KeyEvent.VK_BACK_SLASH
                else -> wtf("Dunno how to type key `$c`")
            }
        }

        if (holdShift)
            robot.keyPress(KeyEvent.VK_SHIFT)

        try {
            robot.keyPress(keyCode)
            robot.keyRelease(keyCode)
        } finally {
            if (holdShift)
                robot.keyRelease(KeyEvent.VK_SHIFT)
        }
    }
    robot.keyPress(KeyEvent.VK_ENTER)
    robot.keyRelease(KeyEvent.VK_ENTER)
}

fun frp_luceneParseRussian(rmap: Map<*, *>): List<LuceneParseToken> {
    val text: String = cast(rmap["text"])
    return luceneParse(text, russianAnalyzer)
}

fun serveHardenScreenHTMLRequest(req: HardenScreenHTMLRequest) {
    val file = File("$APS_HOME/front/test-shit/${req.assertionID}")
    if (file.exists()) {
        file.copyTo(File("${bconst.tempBakDir}/${req.assertionID}"), true)
    }
    file.writeText(req.html)
}

@RemoteProcedureFactory
fun serveTestCopyOrderFileToArea() = adminProcedure(
    {TestCopyOrderFileToAreaRequest()},
    runShit = fun(ctx, req): TestCopyOrderFileToAreaRequest.Response {
        val protoOrderFile: JQUaOrderFilesRecord = selectUAOrderFile(ctx, req.orderFileID.value.toLong())
        val area: JQUaOrderAreasRecord = selectUAOrderAreaByName(ctx, protoOrderFile.uaOrderId, req.areaName.value)

        val orderFileID = UA_ORDER_FILES.let {t->
            ctx.insertShit("Insert order file", t) {it
                .set(t.UA_ORDER_ID, protoOrderFile.uaOrderId)
                .set(t.FILE_ID, protoOrderFile.fileId)
                .set(t.UA_ORDER_AREA_ID, area.id)
                .set(t.SEEN_AS_FROM, protoOrderFile.seenAsFrom)
                .returnID(t)
            }
        }

        insertFileUserPermission(ctx, protoOrderFile.fileId, req.permissionForUserID.value.toLong())

        return TestCopyOrderFileToAreaRequest.Response(orderFileID.toString())
    }
)










