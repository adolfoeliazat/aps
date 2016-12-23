package aps.back

import java.time.LocalDateTime
import java.util.*

fun main(args: Array<String>) {
    var prev = "2015-12-15 16:43:05"
    val count = 50
    val random = Random(54629823847)
    val maxStepMinutes = 60 * 24 * 3

    for (i in 1..count) {
        val t = LocalDateTime.parse(prev, PG_LOCAL_DATE_TIME)
        prev = PG_LOCAL_DATE_TIME.format(t.plusMinutes(random.nextInt(maxStepMinutes).toLong()))
        print("\"$prev\"")
        if (i < count) print(",")
        println()
    }
}

