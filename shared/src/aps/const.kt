package aps

object const {

    val moreableChunkSize = 10

    object text {
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

    object test {
        val filesRoot = "E:\\work\\aps\\back\\testfiles\\"
    }
}


