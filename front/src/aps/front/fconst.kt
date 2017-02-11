package aps.front

import aps.*

object fconst {
    val defaultScrollBursts = 8
    val scrollbarWidth = 17

    object tab                               : Fuckers<TabKey>(null) {
        object order                         : Fuckers<TabKey>(this) {
            val params                       by namedFucker(::TabKey); val params_testRef = TestRef(params)
            val files                        by namedFucker(::TabKey); val files_testRef = TestRef(files)
            val messages                     by namedFucker(::TabKey); val messages_testRef = TestRef(messages)
        }

        object shebang                       : Fuckers<TabKey>(this) {
            val diff                         by namedFucker(::TabKey); val diff_testRef = TestRef(diff)
            val actualPaste                  by namedFucker(::TabKey); val actualPaste_testRef = TestRef(actualPaste)
        }
    }

    object button                            : Fuckers<ButtonKey>(null) {
        val sendForApproval                  by namedFucker(::ButtonKey); val sendForApproval_testRef = TestRef(sendForApproval)
        val edit                             by namedFucker(::ButtonKey); val edit_testRef = TestRef(edit)
        val primary                          by namedFucker(::ButtonKey); val primary_testRef = TestRef(primary)
        val plus                             by namedFucker(::ButtonKey); val plus_testRef = TestRef(plus)
        val upload                           by namedFucker(::ButtonKey); val upload_testRef = TestRef(upload)
        val cancel                           by namedFucker(::ButtonKey); val cancel_testRef = TestRef(cancel)
        val refreshPage                      by namedFucker(::ButtonKey); val refreshPage_testRef = TestRef(refreshPage)
        val loadMore                         by namedFucker(::ButtonKey); val loadMore_testRef = TestRef(loadMore)

        object modal                         : Fuckers<ButtonKey>(this) {
            val ok                           by namedFucker(::ButtonKey); val ok_testRef = TestRef(ok)
            val cancel                       by namedFucker(::ButtonKey); val cancel_testRef = TestRef(cancel)
        }

        object assertionBanner               : Fuckers<ButtonKey>(this) {
            val vdiff                        by namedFucker(::ButtonKey); val vdiff_testRef = TestRef(vdiff)
            val accept                       by namedFucker(::ButtonKey); val accept_testRef = TestRef(accept)
            val play                         by namedFucker(::ButtonKey); val play_testRef = TestRef(play)
        }
    }

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



















