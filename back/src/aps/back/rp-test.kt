/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.USERS
import com.google.debugging.sourcemap.SourceMapConsumerFactory
import com.google.debugging.sourcemap.SourceMapping
import com.sun.jna.platform.win32.User32
import into.kommon.*
import org.jooq.Record
import org.jooq.UpdateSetMoreStep
import org.jooq.UpdateSetStep
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
}

fun <Req : RequestMatumba, Res : Any>
testProcedure(
    req: Req,
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
        needsUser = false,
        userKinds = setOf(),
        considerNextRequestTimestampFiddling = false,
        logRequestJSON = logRequestJSON ?: true
    ))

@RemoteProcedureFactory fun imposeNextRequestTimestamp() = testProcedure(
    ImposeNextRequestTimestampRequest(),
    runShit = void {ctx, req ->
        TestServerFiddling.nextRequestTimestamp = stringToStamp(req.stamp.value)
    }
)

@RemoteProcedureFactory fun imposeNextGeneratedPassword() = testProcedure(
    ImposeNextGeneratedPasswordRequest(),
    runShit = void {ctx, req ->
        TestServerFiddling.nextGeneratedPassword = req.password.value
    }
)

@RemoteProcedureFactory fun resetTestDatabase() = testProcedure(
    ResetTestDatabaseRequest(),
    runShit = void {ctx, req ->
        val templateDB = DB.byNameOnTestServer(req.templateDB.value)

        if (req.recreateTemplate.value) {
            templateDB.recreate()
        }

        DB.apsTestOnTestServer.recreate(template = templateDB)
    }
)

object EmailMatumba {
    val sentEmails = Collections.synchronizedList(mutableListOf<Email>())

    fun send(email: Email) {
        if (GlobalMatumba.mode == GlobalMatumba.Mode.DEBUG) {
            return sentEmails.add(email) /ignore
        }

        imf("Production email sending")
    }
}

@RemoteProcedureFactory fun getSentEmails() = testProcedure(
    RequestMatumba(),
    runShit = {ctx, req ->
        GetSentEmailsRequest.Response(EmailMatumba.sentEmails)
    }
)

@RemoteProcedureFactory fun clearSentEmails() = testProcedure(
    RequestMatumba(),
    runShit = void {ctx, req ->
        EmailMatumba.sentEmails.clear()
    }
)

@RemoteProcedureFactory fun worldPoint() = testProcedure(
    WorldPointRequest(),
    runShit = void {ctx, req ->
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
                it("Recreate database").execute(""""
                    drop database if exists "$databaseToCreate";
                    create database "$databaseToCreate" template = "$databaseToUseAsTemplate";
                """)
            }
        } finally {
            TestServerFiddling.rejectAllRequestsNeedingDB = oldRejectAllRequests
        }
    }
)

val backendInstanceID = "" + UUID.randomUUID()

@RemoteProcedureFactory fun getSoftwareVersion() = testProcedure(
    GetSoftwareVersionRequest(),
    logRequestJSON = false,
    runShit = {ctx, req ->
        val path = Paths.get("$APS_HOME/front/out/front-enhanced.js")
        val attrs = Files.readAttributes(path, BasicFileAttributes::class.java)
        GetSoftwareVersionRequest.Response(
            ctime = "" + Math.max(attrs.creationTime().toMillis(), attrs.lastModifiedTime().toMillis()),
            backendInstanceID = backendInstanceID)
    }
)

@RemoteProcedureFactory fun getGeneratedShit() = testProcedure(
    RequestMatumba(),
    runShit = {ctx, req ->
        GetGeneratedShitRequest.Response(GodServlet::class.java.getResource("generated-shit.js").readText())
    }
)


@RemoteProcedureFactory fun openSourceCode() = testProcedure(
    OpenSourceCodeRequest(),
    runShit = {ctx, req ->
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
        cmd.addAll(listOf(IDEA_EXE, APS_HOME, "--line", line, file))
        dlog("Executing external command:", cmd.joinToString(" "))
        pb.inheritIO()
        val proc = pb.start()
        val exitCode = proc.waitFor()
        dlog("External command finished with code", exitCode)

        OpenSourceCodeRequest.Response(error = if (exitCode == 0) null else "Bad exit code: $exitCode")
    }
)

@RemoteProcedureFactory fun testSetUserFields() = testProcedure(
    TestSetUserFieldsRequest(),
    needsDB = true,
    runShit = {ctx, req ->
        var step = ctx.q("Update user")
            .update(USERS)
            .set(USERS.ID, USERS.ID)

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
    }
)

@RemoteProcedureFactory fun fuckingRemoteProcedure() = testProcedure(
    FuckingRemoteProcedureRequest(),
    needsDB = false,
    runShit = fun (ctx, req): JSONResponse {
        val rmap = shittyObjectMapper.readValue(req.json.value, Map::class.java)
        val proc: String = cast(rmap["proc"])

        val res: Any = run {when (proc) {
            "loadTestShit" -> frp_loadTestShit(rmap)
            "updateTestShit" -> frp_updateTestShit(rmap)
            "robotClickOnChrome" -> frp_robotClickOnChrome(rmap)
            "robotTypeTextCRIntoWindowTitledOpen" -> frp_robotTypeTextCRIntoWindowTitledOpen(rmap)
            else -> wtf("proc: $proc")

        }}

        return JSONResponse(shittyObjectMapper.writeValueAsString(res))
    }
)

fun frp_loadTestShit(rmap: Map<*, *>): String {
    val id: String = cast(rmap["id"])
    val file = File("$APS_HOME/front/test-shit/$id")
    return if (file.exists()) file.readText()
    else "[No shit yet]"
}

fun frp_updateTestShit(rmap: Map<*, *>) {
    val id: String = cast(rmap["id"])
    val newValue: String = cast(rmap["newValue"])
    File("$APS_HOME/front/test-shit/$id").writeText(newValue)
}

fun frp_robotClickOnChrome(rmap: Map<*, *>) {
    val hwnd = User32.INSTANCE.FindWindow(null, "APS UA - Google Chrome") ?: bitch("No necessary Chrome window")
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
        robotTypeTextCR(text)
        return
    }

    bitch("I'm sick of waiting for Open window")
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














