package aps.front

import aps.*

object fconst {
    val defaultScrollBursts = 8

    object key {
        object refreshPage : KeyDef() {val decl = name; val testRef = name}
        object plus : KeyDef() {val decl = name; val testRef = name}
        object upload : KeyDef() {val decl = name; val testRef = name}
        object file : KeyDef() {val decl = name; val testRef = name}
        object search : KeyDef() {val decl = name; val testRef = name}
        object filter : KeyDef() {val decl = name; val testRef = name}
        object ordering : KeyDef() {val decl = name; val testRef = name}
    }

    object test {
        object default {
            val animationHalfwaySignalTimeout = 1000
        }
    }
}

abstract class KeyDef {
    protected val name = this::class.simpleName!!
}

fun qwe() {
    clog("decl", fconst.key.refreshPage.decl)
    clog("testRef", fconst.key.refreshPage.testRef)
}

//class Key(
//    val decl: String,
//    val testRef: String
//)

