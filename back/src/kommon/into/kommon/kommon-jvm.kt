/*
 * Into Kommon
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import java.io.File

fun currentTimeMillis(): Long = System.currentTimeMillis()

fun getenv(name: String): String? = System.getenv(name)

fun inspectMatchResult(mr: MatchResult): String =
    mr.groups.mapIndexed {i, g -> "Group $i: ${g?.value}"}.joinToString("\n")

fun normalizePath(junky: String): String {
    val path = File(junky).toPath()
    var normal = path.normalize().toString()
    if (normal.matches(Regex("\\w:(\\\\|/).*"))) normal = normal[0].toLowerCase() + normal.substring(1)
    return normal
}
