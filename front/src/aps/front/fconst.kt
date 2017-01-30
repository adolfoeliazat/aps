package aps.front

import aps.*

object fconst {
    val defaultScrollBursts = 8
    val scrollbarWidth = 17

    object symbols {
        val rightDoubleAngleQuotation = "»"
        val rightDoubleAngleQuotationSpaced = " » "
    }

//    object pageNames {
//        object common {
//            object dynamic {
//                val dashboard by myName()
//                val profile by myName()
//                val test by myName()
//                val signIn by myName()
//                val signUp by myName()
//                val orders by myName()
//            }
//        }
//
//        object customer {
//            object static {
//                val index by myName()
//                val why by myName()
//                val prices by myName()
//            }
//            object dynamic {
//                val order by myName()
//                val support by myName()
//
//                private val co = common.dynamic
//                val all = listOf(
//                    co.test, co.signIn, co.signUp, co.dashboard, co.orders, order, support)
//            }
//        }
//
////        object writer {
////            object static {
////                val index by myName()
////                val why by myName()
////            }
////            object dynamic {
////                val support by myName()
////                val store by myName()
////                val users by myName()
////                val adminMyTasks by myName()
////                val adminHeap by myName()
////                val adminUsers by myName()
////                val debugPerfRender by myName()
////                val debugKotlinPlayground by myName()
////                val debug by myName()
////
////                private val co = common.dynamic
////                val all = listOf(
////                    co.test, co.signIn, co.signUp, co.dashboard, co.orders, support,
////                    store, users, co.profile, adminMyTasks, adminHeap, adminUsers, debugPerfRender,
////                    debugKotlinPlayground, debug)
////            }
////        }
//    }

    object key {
//        object topNavItem {
//            object why : KeyDef() {val decl = name; val testRef = name}
//            object prices : KeyDef() {val decl = name; val testRef = name}
//            object samples : KeyDef() {val decl = name; val testRef = name}
//            object faq : KeyDef() {val decl = name; val testRef = name}
//            object contact : KeyDef() {val decl = name; val testRef = name}
//            object blog : KeyDef() {val decl = name; val testRef = name}
//            object makeOrder : KeyDef() {val decl = name; val testRef = name}
//            object orders : KeyDef() {val decl = name; val testRef = name}
//            object support : KeyDef() {val decl = name; val testRef = name}
//            object store : KeyDef() {val decl = name; val testRef = name}
//            object profile : KeyDef() {val decl = name; val testRef = name}
//            object adminUsers : KeyDef() {val decl = name; val testRef = name}
//            object dashboard : KeyDef() {val decl = name; val testRef = name}
//            object signIn : KeyDef() {val decl = name; val testRef = name}
//        }

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


