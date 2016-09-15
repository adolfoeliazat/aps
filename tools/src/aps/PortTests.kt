/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import java.io.File

fun escapedString(s: String): String {
    return '"' + s.replace('"', '\'') + '"'
}

fun main(args: Array<String>) {
    val inputFile = File("E:\\work\\aps\\aps\\src\\test-writer-ua.ts")
    val input = inputFile.readText()

    val output = StringBuilder()
    input.lines().forEach {line ->
        Regex("(\\s*)s\\{beginSection: \\{long: t\\('(.*?)'\\)\\}\\},").find(line)?.let {mr ->
            output.append("${mr.groupValues[1]}section(${escapedString(mr.groupValues[2])}) {\n")
            return@forEach
        }

        Regex("(\\s*)s\\{endSection: \\{\\}\\},").find(line)?.let {mr ->
            output.append("${mr.groupValues[1]}}\n")
            return@forEach
        }

        Regex("(\\s*)s\\{step: \\{kind: 'action', long: t\\('(.*?)'\\)\\}\\},").find(line)?.let {mr ->
            output.append("${mr.groupValues[1]}action(${escapedString(mr.groupValues[2])}) {\n")
            output.append("${mr.groupValues[1]}    // TODO\n")
            output.append("${mr.groupValues[1]}}\n")
            return@forEach
        }

        Regex("(\\s*)s\\{step: \\{kind: 'state', long: t\\('(.*?)'\\)\\}\\},").find(line)?.let {mr ->
            output.append("${mr.groupValues[1]}state(${escapedString(mr.groupValues[2])}) {\n")
            output.append("${mr.groupValues[1]}    // TODO\n")
            output.append("${mr.groupValues[1]}}\n")
            return@forEach
        }

        Regex("(\\s*)s\\{assert: \\{\\\$tag: '(.*?)', expected: '---generated-shit---'\\}\\},").find(line)?.let {mr ->
            output.append("${mr.groupValues[1]}assertGen(${escapedString(mr.groupValues[2])})\n")
            return@forEach
        }

        Regex("(\\s*)s\\{setValue: \\{shame: '(.*?)', value: '(.*?)'\\}\\},").find(line)?.let {mr ->
            output.append("${mr.groupValues[1]}setValue(${escapedString(mr.groupValues[2])}, ${escapedString(mr.groupValues[3])})\n")
            return@forEach
        }

        Regex("(\\s*)s\\{click: \\{shame: '(.*?)', timestamp: '(.*?)'\\}\\},").find(line)?.let {mr ->
            output.append("${mr.groupValues[1]}click(${escapedString(mr.groupValues[2])}, ${escapedString(mr.groupValues[3])})\n")
            return@forEach
        }


        output.append(line + "\n")
    }

    val outputFile = File("C:\\tmp\\out.txt")
    outputFile.writeText(output.toString())

    println("OK")
}
