/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import into.kommon.*
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.WatchEvent

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

                    if (shouldReactToRestartFlag && kind == ENTRY_MODIFY && pathString.contains("_restart-flag")) {
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

    companion object {
        @JvmStatic fun main(vararg args: String) {
            Forever()
        }
    }
}

