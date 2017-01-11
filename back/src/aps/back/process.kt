package aps.back

import aps.*

data class RunProcessResult(
    val exitValue: Int,
    val stdout: String,
    val stderr: String
)

fun runProcessAndWait(cmdPieces: List<String>, inheritIO: Boolean = true): RunProcessResult {
    clog("Executing: " + cmdPieces.joinToString(" "))
    val pb = ProcessBuilder()
    val cmd = pb.command()
    cmd.addAll(cmdPieces)
    if (inheritIO) pb.inheritIO()
    val proc = pb.start()

    return RunProcessResult(
        exitValue = proc.waitFor(),
        stdout = proc.inputStream.readBytes().toString(Charsets.UTF_8),
        stderr = proc.errorStream.readBytes().toString(Charsets.UTF_8)
    )
}

