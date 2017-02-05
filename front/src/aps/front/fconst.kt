package aps.front

import aps.*

object fconst {
    val defaultScrollBursts = 8
    val scrollbarWidth = 17

    object text {
        object symbols {
            val rightDoubleAngleQuotation = "»"
            val rightDoubleAngleQuotationSpaced = " » "
            val nl2 = "\n\n"
        }
    }

    object key {
        object refreshPage : KeyDef() {val decl = name; val testRef = name}
        object upload : KeyDef() {val decl = name; val testRef = name}
        object file : KeyDef() {val decl = name; val testRef = name}
        object search : KeyDef() {val decl = name; val testRef = name}
        object filter : KeyDef() {val decl = name; val testRef = name}
        object ordering : KeyDef() {val decl = name; val testRef = name}
        object cancel : KeyDef() {val decl = name; val testRef = name}

        object link {
            object createAccount : KeyDef() {val decl = name; val testRef = name}
            object signUp : KeyDef() {val decl = name; val testRef = name}
        }

        object button {
            val sendForApproval by named {it}; val sendForApproval_testRef = TestRef(sendForApproval)
            val edit by named {it}; val edit_testRef = TestRef(edit)
            val plus by named {it}; val plus_testRef = TestRef(plus)
            val primary by named {it}; val primary_testRef = TestRef(primary)
        }
    }

    object url {
        object test {
            val writerLocalBase = "http://aps-ua-writer.local:3022"
            val debugMailbox = "$writerLocalBase/debug.html?page=mailbox"
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


