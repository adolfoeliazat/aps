package aps.front

import aps.Promise
import aps.ReactElement
import aps.UserRTO
import into.kommon.die

// - Step 1: Fill this interface



// - Step 2: Get rid of [dynamic] in signatures






class ShitPile {
    val liveStatus: dynamic = json()
    val liveStatusUpdaters: dynamic = json()
    var prevPageStuff: dynamic = json()
    var currentPageStuff: dynamic = json()

    var currentPage: dynamic = null
    lateinit var liveBadge2: (arg: dynamic) -> dynamic
    lateinit var replaceNavigate: (url: String) -> Promise<Unit>
    lateinit var pushNavigate: (url: String) -> Promise<Unit>
    lateinit var setUser: (newUser: UserRTO) -> Unit
    var token: String? = null
    var signedUpOK: Boolean = false
    var user: UserRTO? = null
    lateinit var startLiveStatusPolling: () -> Unit
    lateinit var setPage: (spec: Json) -> Unit
    lateinit var urlLink: (spec: Json) -> ReactElement
    lateinit var getUser: () -> UserRTO?
    lateinit var signOut: () -> Unit
    lateinit var urlQuery: Map<String, String>
    lateinit var loadPageForURL: () -> Promise<Unit>
    lateinit var setRootContent: (re: ReactElement) -> Unit
    lateinit var updatePage: () -> Unit
    lateinit var updatePageHeader: () -> Unit
    lateinit var pollLiveStatus: () -> Promise<Unit>
    var updateNavbar: dynamic = null
    var prevPathname: String? = null
    var prevHref: String? = null
    lateinit var stopLiveStatusPolling: () -> Unit
    var loadPageForURLFirstRun: Boolean = false
    var loadSignInPage: dynamic = null
    var loadSignUpPage: dynamic = null
    var rootContent: dynamic = null
    var updateRoot: dynamic = null
    lateinit var boot: () -> Promise<Unit>
    var liveStatusPollingIntervalHandle: dynamic = null
    lateinit var pageLink: (arg: dynamic) -> dynamic
}














