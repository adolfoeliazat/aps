package aps.back.spike

import aps.back.*

fun main(args: Array<String>) {
    val res = runProcessAndWait(listOf(
        bconst.magick,
        "compare",
        "c:\\tmp\\aps-tmp\\visual-capture\\ecec02be-35ab-4c44-afc4-a50b73d45b1c.png",
        "c:\\tmp\\aps-tmp\\visual-capture\\current.png",
        "c:\\tmp\\aps-tmp\\visual-capture\\diff.png"))
    println(res)
}
