@file:Suppress("RemoveEmptyParenthesesFromLambdaCall")

package aps.front

import aps.*
import into.kommon.*

class DebugPage(val world: World) {
    class URLQuery {
        var page: String? = null
    }

    fun load(): Promisoid<Unit> = async {
        val urlQuery = typeSafeURLQuery(world){URLQuery()}
        when (urlQuery.page) {
            "log" -> await(DebugLogPage(world).load())
            "word-mention-finder" -> await(DebugWordMentionFinderPage(world).load())
            else -> wtf("Fucky page: ${urlQuery.page}")
        }
    }
}




