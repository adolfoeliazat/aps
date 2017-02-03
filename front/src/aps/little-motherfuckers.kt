package aps

import aps.front.*
import aps.Color.*
import into.kommon.*

fun errorLabel(title: String): ToReactElementable =
    kdiv(color = RED_300, marginTop = 5, marginRight = 9, textAlign = "right"){o->
        o- title
    }

fun ElementBuilder.maybeFieldError(error: String?, circleRight: Int) {
    val o = this
    error?.let {
        o- errorLabel(it)
        o- kdiv(width = 15,
                height = 15,
                backgroundColor = RED_300,
                borderRadius = 10,
                position = "absolute",
                right = circleRight,
                top = 10,
                zIndex = 1000)
    }
}

fun formatUAH(cents: Int): String {
    val int = cents / 100
    val fraction = cents - int
    return "$int,$fraction грн."
}

fun formatUnixTime(ms: Long, includeTZ: Boolean = true): String =
    when (Globus.lang) {
        Language.UA -> {
            val double: Double = ms.toString().toDoubleOrNull() ?: wtf()
            val s = moment.tz(double, "UTC").tz("Europe/Kiev").format("L LTS")
            if (includeTZ) "$s (Киев)"
            else s
        }
        Language.EN -> imf("formatUnixTime for EN")
    }

