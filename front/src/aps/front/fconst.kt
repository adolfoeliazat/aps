package aps.front

import aps.*

object fconst {
    val defaultScrollBursts = 8
    val scrollbarWidth = 17

    object key {
        object file : KeyDef() {val ref = name; val testRef = name}
        object search : KeyDef() {val ref = name; val testRef = name}
        object filter : KeyDef() {val ref = name; val testRef = name}
        object ordering : KeyDef() {val ref = name; val testRef = name}

        object link {
            object createAccount : KeyDef() {val decl = name; val testRef = name}
            object signUp : KeyDef() {val decl = name; val testRef = name}
        }

    }

    object url {
        object test {
            val writerLocalBase = "http://aps-ua-writer.local:3022"
            val debugMailbox = "$writerLocalBase/debug.html?page=mailbox"
        }
    }

    object test {
        val testOffClassSuffix = "-testoff"

        object default {
            val animationHalfwaySignalTimeout = 1000
        }

        object url {
            val customer = "http://aps-ua-customer.local:3012"
        }

        object sha1 {
            val pieceOfTrial2 = "75509ed6012db7b99db0ba5051e306bef5760f75"
        }

        val defaultResponseTimeout = 5000
    }

    object storage {
        val clientStateSnapshotPrefix = "clientStateSnapshot-"
    }

}



















