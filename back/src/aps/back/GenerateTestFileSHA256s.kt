package aps.back

import aps.*
import com.google.common.hash.Hashing
import java.io.File

fun main(args: Array<String>) {
    var first = true
    for (f in File(const.file.testFiles).listFiles()) {
        val sha256 = Hashing.sha256().hashBytes(f.readBytes()).toString()
        if (first) first = false else println(",")
        print("""        "${f.name}" to "$sha256"""")
    }
}

