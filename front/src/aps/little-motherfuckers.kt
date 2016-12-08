package aps

import aps.front.*
import aps.front.Color.*

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

