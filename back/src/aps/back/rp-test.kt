/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import com.google.debugging.sourcemap.SourceMapConsumerFactory
import com.google.debugging.sourcemap.SourceMapping
import into.kommon.*
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
testProcedure(req: Req, runShit: (ProcedureContext, Req) -> Res, logRequestJSON: Boolean? = null): (HttpServletRequest, HttpServletResponse) -> Unit =
    remoteProcedure(ProcedureSpec(
        req,
        runShit = runShit,
        wrapInFormResponse = false,
        needsDB = false,
        needsDangerousToken = true,
        needsUser = false,
        userKinds = setOf(),
        considerNextRequestTimestampFiddling = false,
        logRequestJSON = logRequestJSON ?: true))

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
            DB.postgresOnTestServer.joo{it.execute("""
                drop database if exists "${databaseToCreate}";
                create database "${databaseToCreate}" template = "${databaseToUseAsTemplate}";
            """)}
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
        val secondColon = sourceLocation.indexOf(':', firstColon + 1)
        if (firstColon == -1 || secondColon == -1) bitch("I want two colons in source location")

        val filePartEnd = firstColon
        val filePart = sourceLocation.substring(0, filePartEnd)
        if (!filePart.startsWith("APS/")) bitch("Obscure file in source location")
        val file = APS_HOME + "/" + filePart.substring("APS/".length)
        val line = sourceLocation.substring(firstColon + 1, secondColon)

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








