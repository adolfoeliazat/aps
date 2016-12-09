package aps

object const {
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

