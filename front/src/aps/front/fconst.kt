package aps.front

import aps.*

object fconst {
    val defaultScrollBursts = 8

    object key {
        object topNavItem {
            object why : KeyDef() {val decl = name; val testRef = name}
            object prices : KeyDef() {val decl = name; val testRef = name}
            object samples : KeyDef() {val decl = name; val testRef = name}
            object faq : KeyDef() {val decl = name; val testRef = name}
            object contact : KeyDef() {val decl = name; val testRef = name}
            object blog : KeyDef() {val decl = name; val testRef = name}
            object orders : KeyDef() {val decl = name; val testRef = name}
            object support : KeyDef() {val decl = name; val testRef = name}
            object store : KeyDef() {val decl = name; val testRef = name}
            object profile : KeyDef() {val decl = name; val testRef = name}
            object adminUsers : KeyDef() {val decl = name; val testRef = name}
            object dashboard : KeyDef() {val decl = name; val testRef = name}
            object signIn : KeyDef() {val decl = name; val testRef = name}
        }

        object refreshPage : KeyDef() {val decl = name; val testRef = name}
        object plus : KeyDef() {val decl = name; val testRef = name}
        object upload : KeyDef() {val decl = name; val testRef = name}
        object file : KeyDef() {val decl = name; val testRef = name}
        object search : KeyDef() {val decl = name; val testRef = name}
        object filter : KeyDef() {val decl = name; val testRef = name}
        object ordering : KeyDef() {val decl = name; val testRef = name}
        object primary : KeyDef() {val decl = name; val testRef = name}
        object cancel : KeyDef() {val decl = name; val testRef = name}

        object link {
            object createAccount : KeyDef() {val decl = name; val testRef = name}
        }
    }

    object url {
        object test {
            val writerLocalBase = "http://aps-ua-writer.local:3022"
        }
    }

    object test {
        object default {
            val animationHalfwaySignalTimeout = 1000
        }

        object url {
            val customer = "http://aps-ua-customer.local:3012"
        }

        object sha1 {
            val pieceOfTrial2 = "75509ed6012db7b99db0ba5051e306bef5760f75"
        }

        val filesRoot = "E:\\work\\aps\\back\\testfiles\\"
        val defaultResponseTimeout = 5000
    }

    object storage {
        val clientStateSnapshotPrefix = "clientStateSnapshot-"
    }
}


