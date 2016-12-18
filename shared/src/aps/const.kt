package aps

object const {
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

    object common {
        val minTitleLen = 5
        val maxTitleLen = 100
        val minDetailsLen = 5
        val maxDetailsLen = 1000
    }

    object order {
        val minTitleLen = common.minTitleLen
        val maxTitleLen = common.maxTitleLen
        val minDetailsLen = common.minDetailsLen
        val maxDetailsLen = common.maxDetailsLen
        val minPages = 1
        val maxPages = 300
        val minSources = 0
        val maxSources = 20
    }

    object file {
        val minTitleLen = common.minTitleLen
        val maxTitleLen = common.maxTitleLen
        val minDetailsLen = common.minDetailsLen
        val maxDetailsLen = common.maxDetailsLen
    }
}

