/*
 * Into Kommon
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package kommon.into.kommon.spike

import aps.*
import com.google.debugging.sourcemap.SourceMapConsumerFactory
import java.io.File
import java.nio.file.Path

fun main(args: Array<String>) {
    spike_pathNormalization()
}

fun spike_sourceMap() {
    val sm = SourceMapConsumerFactory.parse(File("E:/work/aps/front/out/front.js.map").readText())
    val orig = sm.getMappingForLine(12178, 48)
    println(orig)
}

fun spike_pathNormalization() {
    val junky = "E:/work/./aps/front///src/aps/../front/igniter.kt/"
    println("Junky     : " + junky)
    println("Normalized: " + normalizePath(junky))
}
