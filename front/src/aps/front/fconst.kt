package aps.front

import aps.*

object fconst {
    val defaultScrollBursts = 8
    val scrollbarWidth = 17

    object tab                               : NamedGroup<TabKey>(null) {
        object order                         : NamedGroup<TabKey>(this) {
            object params                    : TabKeyRefs(this) {val ref = key; val testRef = TestRef(key)}
            object files                     : TabKeyRefs(this) {val ref = key; val testRef = TestRef(key)}
            object messages                  : TabKeyRefs(this) {val ref = key; val testRef = TestRef(key)}
        }

        object shebang                       : NamedGroup<TabKey>(this) {
            object diff                      : TabKeyRefs(this) {val ref = key; val testRef = TestRef(key)}
            object actualPaste               : TabKeyRefs(this) {val ref = key; val testRef = TestRef(key)}
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

        // TODO:vgrechka Move out of `key`
        object button                            : NamedGroup<ButtonKey>(null) {
            object sendForApproval               : ButtonKeyRefs(this) {val ref = key; val testRef = TestRef(key)}
            object edit                          : ButtonKeyRefs(this) {val ref = key; val testRef = TestRef(key)}
            object primary                       : ButtonKeyRefs(this) {val ref = key; val testRef = TestRef(key)}
            object plus                          : ButtonKeyRefs(this) {val ref = key; val testRef = TestRef(key)}
            object upload                        : ButtonKeyRefs(this) {val ref = key; val testRef = TestRef(key)}
            object cancel                        : ButtonKeyRefs(this) {val ref = key; val testRef = TestRef(key)}
            object refreshPage                   : ButtonKeyRefs(this) {val ref = key; val testRef = TestRef(key)}
            object loadMore                      : ButtonKeyRefs(this) {val ref = key; val testRef = TestRef(key)}

            object modal                         : NamedGroup<ButtonKey>(this) {
                object ok                        : ButtonKeyRefs(this) {val ref = key; val testRef = TestRef(key)}
                object cancel                    : ButtonKeyRefs(this) {val ref = key; val testRef = TestRef(key)}
            }

            object assertionBanner               : NamedGroup<ButtonKey>(this) {
                object vdiff                     : ButtonKeyRefs(this) {val ref = key; val testRef = TestRef(key)}
                object accept                    : ButtonKeyRefs(this) {val ref = key; val testRef = TestRef(key)}
                object play                      : ButtonKeyRefs(this) {val ref = key; val testRef = TestRef(key)}
            }
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



















