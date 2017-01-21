/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import into.kommon.*
import java.io.*
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.isEmpty()) bitch("I need a command, motherfucker")
    val command = args[0]

    when (command) {
        "jooq" -> runJava("aps.back.GenerateJOOQKt").waitFor()
        "forever" -> Forever()
        "lint" -> LintShit()
//        "generate" -> GenerateShit()
        else -> bitch("Do your [$command] yourself")
    }
}

fun runJava(entryPoint: String): Process {
    val pb = ProcessBuilder()
    val cmd = pb.command()

    val cp = System.getProperty("java.class.path")
//    val cp = buildString {
//        append("out${File.pathSeparator}")
//        for (dir in sequenceOf("../lib/kotlin/1.1-m02-eap", "lib", "lib-gradle"))
//            for (file in File(dir).list().filter { it.endsWith(".jar") })
//                append("$dir/$file${File.pathSeparator}")
//    }

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




















