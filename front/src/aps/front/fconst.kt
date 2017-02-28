package aps.front

import aps.*

object fconst {
    val defaultScrollBursts = 8
    val scrollbarWidth = 17
    val backendURL by lazy {
        val url = ExternalGlobus.BACKEND_URL
        clog("Using backend $url")
        url
    }
    val testFilesDirAndSlash = "e:\\work\\aps\\back\\testfiles\\" // TODO:vgrechka @unhardcode

    object elementID {
        val topNavbarContainer = "topNavbarContainer"
        val root = "root"
        val staticShit = "staticShit"
        val ticker = "ticker"
        val footer = "footer"
        val wholePageTicker = "wholePageTicker"
        val underFooter = "underFooter"
        val testablePanes = "testablePanes"
    }

    object key { // TODO:vgrechka Delete this shit
        object file : KeyDef() {val ref = name; val testRef = name}
        object search : KeyDef() {val ref = name; val testRef = name}
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
            val writer = "http://aps-ua-writer.local:3022"
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



















