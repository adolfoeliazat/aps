package aps.back

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

object nextRandomOldStamp {
    private val seq = RandomInstantSequence(startingFrom = "2010-02-07 01:02:03")

    operator fun invoke(): Timestamp = Timestamp.valueOf(seq.current)
        .also {seq.advance()}
}

class RandomInstantSequence(startingFrom: String) {
    @Volatile var current = LocalDateTime.parse(startingFrom, PG_LOCAL_DATE_TIME)
    val random = Random(54629823847)
    val minStepMinutes = 1
    val maxStepMinutes = 10 // 60 * 24 * 3
    val maxDistanceFromMin = maxStepMinutes - minStepMinutes

    fun advance() {
        current = current.plusMinutes(minStepMinutes + random.nextInt(maxDistanceFromMin + 1).toLong())
    }

}



