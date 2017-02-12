package aps.back

import java.time.LocalDateTime
import java.util.*

fun main(args: Array<String>) {
    var prev = "2016-03-09 01:15:05" // "2015-12-15 16:43:05"
    val count = 100
    val random = Random(54629823847)
    val minStepMinutes = 1
    val maxStepMinutes = 10 // 60 * 24 * 3

    for (i in 1..count) {
        val t = LocalDateTime.parse(prev, PG_LOCAL_DATE_TIME)
        val maxDistanceFromMin = maxStepMinutes - minStepMinutes
        prev = PG_LOCAL_DATE_TIME.format(t.plusMinutes(minStepMinutes + random.nextInt(maxDistanceFromMin + 1).toLong()))
        print("\"$prev\"")
        if (i < count) print(",")
        println()
    }
}

