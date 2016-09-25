/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import java.io.File
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*

class Forever {
    @Volatile var shouldReactToRestartFlag = false
    @Volatile var shouldRestartOnDeath = false
    lateinit var process: Process

    init {
        startShit()
        watchDirectory()
    }

    fun startShit() {
        shouldRestartOnDeath = false
        process = runJava("aps.back.BackKt")
        shouldReactToRestartFlag = true

        watchProcess()
    }

    fun watchProcess() {
        Thread {
            val exitCode = process.waitFor()
            println("Shit died with code $exitCode")
            if (shouldRestartOnDeath) {
                println("Restarting shit")
                startShit()
            } else {
                println("Giving up")
                System.exit(0)
            }
        }.start()
    }

    fun watchDirectory() {
        Thread {
            val watcher = FileSystems.getDefault().newWatchService()
            val dir = Paths.get(".")
            dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)
            println("Watching for restart flag")
            while (true) {
                val key = watcher.take()

                for (event in key.pollEvents()) {
                    val kind = event.kind()

                    if (kind === OVERFLOW) {
                        println("WTF: I've got an OVERFLOW event")
                        continue
                    }

                    val ev = @Suppress("UNCHECKED_CAST") (event as WatchEvent<Path>)
                    val path: Path = dir.resolve(ev.context()) ?: throw Exception("Path is null")
                    val pathString = path.toString()
                    // println("kind = $kind; path = ${pathString}")

                    if (shouldReactToRestartFlag && kind == ENTRY_MODIFY && pathString.contains("restart-flag")) {
                        shouldReactToRestartFlag = false
                        print("Killing shit...")
                        shouldRestartOnDeath = true
                        process.destroyForcibly().waitFor()
                        println(" OK")
                    }
                }

                val valid = key.reset()
                if (!valid) {
                    println("FUCKUP: Cannot reset watch key")
                    break
                }
            }
        }.start()
    }

}

fun runJava(entryPoint: String): Process {
    val pb = ProcessBuilder()
    val cmd = pb.command()
    val cp = buildString {
        append("out${File.pathSeparator}")
        for (dir in sequenceOf("lib", "lib-gradle"))
            for (file in File(dir).list().filter { it.endsWith(".jar") })
                append("$dir/$file${File.pathSeparator}")
    }
    cmd.addAll(sequenceOf(
        "java",
//        "-Dco.paralleluniverse.fibers.verifyInstrumentation=true",
        "-cp", cp,
//        "-javaagent:lib-gradle/quasar-core-0.7.6-jdk8.jar",
        entryPoint))
    // println("Command: " + cmd.joinToString(" "))

    pb.inheritIO()

    return pb.start()
}


fun main(args: Array<String>) {
    if (args.size < 1) bitch("I need a command, motherfucker")
    val command = args[0]

    when (command) {
        "make-static-sites" -> runJava("aps.back.MakeStaticSitesKt").waitFor()
        "jooq" -> runJava("aps.back.GenerateJOOQKt").waitFor()
        "forever" -> Forever()
        else -> bitch("Do your [$command] yourself")
    }
}



















