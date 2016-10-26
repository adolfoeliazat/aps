/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import co.paralleluniverse.common.util.Tuple
import java.io.*
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*
import java.time.LocalDateTime
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.isEmpty()) bitch("I need a command, motherfucker")
    val command = args[0]

    when (command) {
        "jooq" -> runJava("aps.back.GenerateJOOQKt").waitFor()
        "forever" -> Forever()
        "lint" -> LintShit()
        "generate" -> GenerateShit()
        else -> bitch("Do your [$command] yourself")
    }
}

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

class LintShit {
    init {
        print("Linting your shit... ")
        visit(File("$APS_ROOT/front/src"))
        println("COOL")
    }

    fun visit(f: File) {
        if (f.isDirectory) return f.listFiles().forEach {visit(it)}
        if (!f.isFile) wtf("File: $f")
        if (f.extension != "kt") return

        f.useLines {it.forEachIndexed {lineIndex, line ->
            for (tag in listOf("kdiv", "kspan", "h3")) {
                if (Regex("\\W$tag\\W").containsMatchIn(line)) {
                    if (line.trimEnd().endsWith("{")) {
                        val fname = f.path.substring(APS_ROOT.length)
                        println("SHIT")
                        println("$fname:${lineIndex + 1}: Lambda parameter is mandatory for $tag")
                        exitProcess(1)
                    }
                }
            }
        }}
    }
}

class GenerateShit {
    init {
        fun loadProps(path: String, beginSnippet: String, endSnippet: String): List<String> {
            return mutableListOf<String>().applet {res->
                File(path).useLines {lines->
                    var readingProps = false; var doneReadingProps = false
                    lines.saforEachIndexed {lineIndex, line ->
                        try {
                            if (!readingProps) {
                                if (line.trimEnd().endsWith(beginSnippet)) readingProps = true
                            } else {
                                if (line.trim() == endSnippet) {
                                    doneReadingProps = true
                                    abort()
                                }
                                Regex("\\s+var ([^,]*),?$").find(line)?.let {
                                    res.add(it.groups[1]?.value ?: wtf("group"))
                                } ?: wtf("find")
                            }
                        } catch (e: Exception) {
                            throw Exception("\nLine ${lineIndex + 1} in $path: " + e.message, e)
                        }
                    }
                    if (!readingProps) wtf("I want [$beginSnippet] in $path")
                    if (!doneReadingProps) wtf("I want [$endSnippet] in $path")
                }
            }
        }

        fun genParams(props: Iterable<String>) =
            props.map {
                "\n                        $it"
            }
            .joinToString(",")

        fun genArgs(props: Iterable<String>) =
            props.map {
                val name = it.substring(0, it.indexOf(":"))
                "\n                            $name=$name"
            }
            .joinToString(",")

        print("Generating some shit for you... ")
        val attrsProps = loadProps("$APS_ROOT/front/src/aps/front/Control2.kt", "class Attrs(", ")")
        val styleProps = loadProps("$APS_ROOT/front/src/aps/front/new-shit.kt", "class Style(", ") {")
        val newCode = reindent(4, """
            operator fun invoke(${genParams(attrsProps)}, ${genParams(styleProps)},
                    block: ((ElementBuilder) -> Unit)? = null): ElementBuilder
                = invoke(
                    Attrs(${genArgs(attrsProps)}),
                    Style(${genArgs(styleProps)}),
                    block)
        """) + "\n"

        val file = File("$APS_ROOT/front/src/aps/front/ElementBuilderFactory.kt")
        val currentCode = file.readText()
        val stamp = LocalDateTime.now().format(PG_LOCAL_DATE_TIME).replace(Regex("[ :\\.]"), "-")
        File("$GENERATOR_BAK_DIR/ElementBuilderFactory.kt--$stamp").writeText(currentCode)
        val beginMarker = "//---------- BEGIN GENERATED SHIT { ----------"
        val beginMarkerIndex = currentCode.indexOf(beginMarker)
        if (beginMarkerIndex == -1) wtf("No beginMarkerIndex in ${file.path}")
        val endMarker = "    //---------- END GENERATED SHIT } ----------"
        val endMarkerIndex = currentCode.indexOf(endMarker)
        if (endMarkerIndex == -1) wtf("No endMarkerIndex in ${file.path}")
        val before = currentCode.substring(0, beginMarkerIndex + beginMarker.length)
        val after = currentCode.substring(endMarkerIndex)
        File("$APS_ROOT/front/src/aps/front/ElementBuilderFactory.kt").writeText(
            before + "\n\n" + newCode + "\n" + after)

        println("COOL")
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            GenerateShit()
        }
    }
}

fun runJava(entryPoint: String): Process {
    val pb = ProcessBuilder()
    val cmd = pb.command()
    val cp = buildString {
        append("out${File.pathSeparator}")
        for (dir in sequenceOf("../lib/kotlin-jvm", "lib", "lib-gradle"))
            for (file in File(dir).list().filter { it.endsWith(".jar") })
                append("$dir/$file${File.pathSeparator}")
    }
    cmd.addAll(sequenceOf(
        "java",
        "-Dorg.slf4j.simpleLogger.defaultLogLevel=info",
//        "-javaagent:lib-gradle/quasar-core-0.7.6-jdk8.jar",
//        "-Dco.paralleluniverse.fibers.verifyInstrumentation=true",
        "-cp", cp,
        entryPoint))
    // println("Command: " + cmd.joinToString(" "))

    pb.inheritIO()

    return pb.start()
}




















