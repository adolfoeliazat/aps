package aps.back.spike

import aps.*
import com.google.common.hash.Hashing

fun main(args: Array<String>) {
    val hash = Hashing.sha1().hashBytes(ByteArray(3)-{o->
        o[0] = 10
        o[1] = 20
        o[2] = 30
    }).toString()
    println(hash)
}

