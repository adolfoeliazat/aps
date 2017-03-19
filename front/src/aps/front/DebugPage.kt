@file:Suppress("RemoveEmptyParenthesesFromLambdaCall")

package aps.front

import aps.*

class DebugPage(val world: World) {
    class URLQuery {
        var page: String? = null
    }

    suspend fun load(): PageLoadingError? {
        val urlQuery = typeSafeURLQuery(world){URLQuery()}
        when (urlQuery.page) {
            "log" -> DebugLogPage(world).load()
            "word-mention-finder" -> DebugWordMentionFinderPage(world).load()
            "mailbox" -> DebugMailboxPage(world).load()
            "sqlfiddle" -> SQLFiddlePage(world).load()
            else -> wtf("Fucky page: ${urlQuery.page}")
        }
        return null
    }
}




