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









//if (msg.fun === 'danger_openSourceCode') {
//    let file, line, column, offset, ide
//    if (msg.tag) {
//        const ft = findTagInSourceCode(msg.tag)
//        if (!ft) return {error: 'Tag is not found in code'}
//        ;({file, offset} = ft)
//    } else if (msg.sourceLocation) {
//        // Source location example: aps/src/backend.ts[7556]:181:35
//        dlog(1111111, msg.sourceLocation)
//
//        const openBracket = msg.sourceLocation.indexOf('[' /*]*/)
//        const closingBracket = msg.sourceLocation.indexOf(/*[*/ ']')
//        const firstColon = msg.sourceLocation.indexOf(':')
//        const secondColon = msg.sourceLocation.indexOf(':', firstColon + 1)
//        let filePartEnd
//            if (~openBracket && ~closingBracket) {
//            filePartEnd = openBracket
//        } else if (~firstColon && ~secondColon) {
//            filePartEnd = firstColon
//        } else {
//            return {error: 'I want either brackets or two colons in source location'}
//        }
//        const filePart = msg.sourceLocation.slice(0, filePartEnd)
//        file = {
//            'aps/src/common.ts': 'E:/work/aps/aps/src/common.ts',
//            'common.ts': 'E:/work/aps/aps/src/common.ts',
//            'aps/src/client.ts': 'E:/work/aps/aps/src/client.ts',
//            'client.ts': 'E:/work/aps/aps/src/client.ts',
//            'aps/src/test-admin-ua.ts': 'E:/work/aps/aps/src/test-admin-ua.ts',
//            'test-admin-ua.ts': 'E:/work/aps/aps/src/test-admin-ua.ts',
//            'aps/src/test-writer-ua.ts': 'E:/work/aps/aps/src/test-writer-ua.ts',
//            'test-writer-ua.ts': 'E:/work/aps/aps/src/test-writer-ua.ts',
//            'aps/src/backend.ts': 'E:/work/aps/aps/src/backend.ts',
//            'backend.ts': 'E:/work/aps/aps/src/backend.ts',
//            'ui.ts': 'E:/work/foundation/u/src/ui.ts',
//            'u/src/ui.ts': 'E:/work/foundation/u/src/ui.ts',
//            'test-shit-ua.ts': 'E:/work/aps/aps/src/test-shit-ua.ts',
//        }[filePart]
//        if (!file && filePart.startsWith('APS/')) {
//            file = 'E:/work/aps/' + filePart.slice('APS/'.length)
//        }
//        if (!file) return {error: `Weird file in source location: [${filePart}]`}
//
//        if (~filePart.indexOf('.kt')) ide = 'idea'
//        else ide = 'eclipse'
//
//        if (ide === 'idea') {
//            line = parseInt(msg.sourceLocation.slice(firstColon + 1, secondColon), 10)
//            column = parseInt(msg.sourceLocation.slice(secondColon + 1), 10)
//        }
//        else if (ide === 'eclipse') {
//            if (~openBracket && ~closingBracket) {
//                offset = parseInt(msg.sourceLocation.slice(openBracket + 1, closingBracket))
//            } else if (~firstColon && ~secondColon) {
//                line = parseInt(msg.sourceLocation.slice(firstColon + 1, secondColon), 10) - 1
//                column = parseInt(msg.sourceLocation.slice(secondColon + 1), 10) - 1
//                const code = fs.readFileSync(file, 'utf8')
//                let currentLine = 0, currentColumn = 0, feasibleLineStartOffset
//                offset = 0
//                while (offset < code.length) {
//                    if (currentLine === line) {
//                        if (feasibleLineStartOffset === undefined) {
//                            feasibleLineStartOffset = offset
//                        }
//                        if (currentColumn === column) break
//                    }
//                    if (code[offset] === '\r' && code[offset + 1] === '\n') {
//                        if (feasibleLineStartOffset !== undefined) { // Likely, column was mangled by code generation
//                            offset = feasibleLineStartOffset
//                            break
//                        }
//                        offset += 2
//                        currentLine += 1
//                        currentColumn = 0
//                    } else if (code[offset] === '\n') {
//                        offset += 1
//                        currentLine += 1
//                        currentColumn = 0
//                    } else {
//                        offset += 1
//                        currentColumn += 1
//                    }
//                }
//            } else {
//                raise('Weird line/column/offset')
//            }
//        }
//    } else {
//        dlog('Message for weird source location descriptor', msg)
//        raise('Weird source location descriptor')
//    }
//
//    if (ide === 'idea') {
//        const command = `C:\\opt\\idea-ce\\bin\\idea64.exe e:\\work\\aps --line ${line} ${file}`
//            dlog('Executing external command', command)
//        const child = sh.exec(command, {silent: false}, code => {
//            dlog('External command finished with code', code)
//        })
//    }
//    else if (ide === 'eclipse') {
//        const res = await RPCClient({url: 'http://127.0.0.1:4001/rpc'}).call({fun: 'openEditor', file, offset})
//    }
//    else {
//        raise('WTF is IDE')
//    }
//    return hunkyDory()
//}







