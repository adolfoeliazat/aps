package aps.back

import java.time.LocalDateTime
import java.util.*

fun main(args: Array<String>) {
    val list = generateTestTimestamps()

    for (i in 1..list.size) {
        print("\"${list[i - 1]}\"")
        if (i < list.size) print(", ")
        if (i % 500 == 0) println()
    }
}

fun generateTestTimestamps(): MutableList<String> {
    val count = 10000
    var prev = "2014-03-02 04:32:11"
    val random = Random(54629823847)
    val minStepMinutes = 1
    val maxStepMinutes = 10 // 60 * 24 * 3

    val list = mutableListOf<String>()

    for (i in 1..count) {
        val t = LocalDateTime.parse(prev, PG_LOCAL_DATE_TIME)
        val maxDistanceFromMin = maxStepMinutes - minStepMinutes
        prev = PG_LOCAL_DATE_TIME.format(t.plusMinutes(minStepMinutes + random.nextInt(maxDistanceFromMin + 1).toLong()))
        list += prev
    }
    return list
}

