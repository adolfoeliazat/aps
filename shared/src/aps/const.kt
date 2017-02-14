package aps

import into.kommon.*

object const {

    val moreableChunkSize = 10

    object text {
        val numberSign = t("#", "№")

        object symbols {
            val rightDoubleAngleQuotation = "»"
            val rightDoubleAngleQuotationSpaced = " » "
            val nl2 = "\n\n"
            val nbsp: String = "" + 0xa0.toChar()
            val mdash = "—"
            val ndash = "–"
            val threeQuotes = "\"\"\""
            val times = "×"
        }

        object shebang {
            val defaultCancelButtonTitle = t("Nah", "Не надо")
        }
    }

    object msg {
        val noItems = t("TOTE", "Савсэм ничего нэт, да...")
        val serviceFuckedUp = t("Service is temporarily fucked up, sorry", "Сервис временно в жопе, просим прощения")
    }

    object orderArea {
        val admin = "Admin"
        val customer = "Customer"
        val writer = "Writer"
    }

    object windowMessage {
        val whatsUp = "What's up?"
        val fileForbidden = "Fucking file is forbidden"
    }

//    object common {
//        val minTitleLen = 5
//        val maxTitleLen = 100
//        val minDetailsLen = 5
//        val maxDetailsLen = 2000
//    }
//
//    object order {
//        val minTitleLen = common.minTitleLen
//        val maxTitleLen = common.maxTitleLen
//        val minDetailsLen = common.minDetailsLen
//        val maxDetailsLen = common.maxDetailsLen
//    }
//
//    object file {
//        val minTitleLen = common.minTitleLen
//        val maxTitleLen = common.maxTitleLen
//        val minDetailsLen = common.minDetailsLen
//        val maxDetailsLen = common.maxDetailsLen
//    }

    val topNavbarHeight = 50.0

    object urlq {
        object test {
            val test = "test"
            val testSuite = "testSuite"
            val stopOnAssertions = "stopOnAssertions"
            val dontStopOnCorrectAssertions  = "dontStopOnCorrectAssertions"
            val animateUserActions = "animateUserActions"
            val handPauses = "handPauses"
        }
    }

    object elementID {
        val dynamicFooter = "dynamicFooter"
        val cutLineContainer = "cutLineContainer"
    }

    object productName {
        val uaCustomer = "APS UA"
    }

    object file {
        val APS_HOME get()= getenv("APS_HOME") ?: die("I want APS_HOME environment variable")
        val GENERATOR_BAK_DIR get()= "c:/tmp/aps-bak" // TODO:vgrechka @unhardcode
        val TMPDIR get()= getenv("TMPDIR") ?: die("I want TMPDIR environment variable")
        val APS_CLOUD_BACK_HOST get() = getenv("APS_CLOUD_BACK_HOST") ?: die("I want APS_CLOUD_BACK_HOST environment variable")
        val APS_TEMP get()= "c:/tmp/aps-tmp" // TODO:vgrechka @unhardcode

        val testFiles get()= "$APS_HOME/back/testfiles"
    }
}


