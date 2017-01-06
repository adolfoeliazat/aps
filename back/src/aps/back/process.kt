package aps.back

import aps.*

fun runProcessAndWait(cmdPieces: List<String>): Int {
    clog("Executing: " + cmdPieces.joinToString(" "))
    val pb = ProcessBuilder()
    val cmd = pb.command()
    cmd.addAll(cmdPieces)
    pb.inheritIO()
    val proc = pb.start()
    return proc.waitFor()
}

