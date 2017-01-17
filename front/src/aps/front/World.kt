/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*
import jquery.jq
import kotlin.browser.localStorage
import kotlin.browser.window
import kotlin.properties.Delegates.notNull

class World(val name: String) {
    lateinit var rootContent: ReactElement
    lateinit var currentPage: Page
    var tokenMaybe: String? = null
    var signedUpOK: Boolean = false
    var userMaybe: UserRTO? = null
    lateinit var urlQuery: Map<String, String>
    lateinit var updateNavbar: () -> Unit
    var prevPathname: String? = null
    var prevHref: String? = null
    var loadPageForURLFirstRun: Boolean = false
    lateinit var updatePage: () -> Unit
    lateinit var updatePageHeader: () -> Unit
    lateinit var updateRoot: () -> Unit
    lateinit var initialEmailFieldValueAfterSignUp: String
    lateinit var rootElement: ReactElement
    lateinit var topNavbarElement: ReactElement
    lateinit var footer: DynamicFooter

    val token: String get() = tokenMaybe!!
    val user: UserRTO get() = userMaybe.let {it ?: bitch("I want a fucking user")}

    fun boot(): Promise<Unit> {"__async"
        hrss.browserOld.ui = this
//        KotlinShit.ui = this

        global.onpopstate = {e: dynamic -> "__async"
            breatheBanner.show()
            __await<dynamic>(loadPageForURL())
            breatheBanner.hide()
        }

        hrss.browserOld.impl = this
        KotlinShit.clientImpl = this
        initEffects()
//        if (MODE == "debug") {
//            Shitus.initDebugFunctionsShit()
//        }
        __await<dynamic>(bootKillme())

        Globus.worldMaybe = this
        if (isTest()) {
            crossWorld.locationControl.update()
        }
        send(PingRequest())

        return __asyncResult(Unit)
    }

    fun urlLink(def: dynamic): dynamic {
        return KotlinShit.urlLink(this, def)
    }

    fun pageLink(def: dynamic): dynamic {
        return KotlinShit.pageLink(this, def)
    }

    fun getUser(): UserRTO? {
        return userMaybe
    }

    fun setUser(x: dynamic) {
        userMaybe = x
    }

    fun loadSignInPage(): Promise<Unit> = SignInPage(this).load()

    fun signOut() {
        typedStorageLocal.clear()
//        hrss.storageLocal.clear()
        tokenMaybe = null
        userMaybe = null
        replaceNavigate("/")
    }

    fun pushNavigate(url: String): Promise<Unit> = async {
        //        ui.currentPage = null.asDynamic() // TODO:vgrechka Do something about this
        Globus.location.pushState(null, "", url)
        await(loadPageForURL())
    }

    fun replaceNavigate(where: dynamic): Promise<Unit> {"__async"
        Globus.location.replaceState(null, "", where)
        return __await<dynamic>(loadPageForURL()) /ignora
    }

    fun bootKillme(): Promise<Unit> {"__async"
        Shitus.beginTrain(json("name" to "boot()")); try {
            tokenMaybe = typedStorageLocal.token
//            token = hrss.storageLocal.getItem("token")
            if (tokenMaybe != null) {
                try {
                    val res = __await(SignInWithTokenRequest.send(tokenMaybe!!))
                    userMaybe = res.user
//                        ui.startLiveStatusPolling()
                } catch (e: Throwable) {
                    // Pretend no one was signed in.
                    // User will be able to see actual rejection reason (ban or something) on subsequent sign in attempt.
                    console.warn("Failed to private_getUserInfo", e)
                    tokenMaybe = undefined
                    typedStorageLocal.clear()
//                    hrss.storageLocal.clear()
                } finally {
//                    ExternalGlobus.makeSignInNavbarLinkVisible()
                }
            }

//            js("$")(global.document.head).append("<style id='css'>${apsCSS()}</style>")

            topNavbarElement = Shitus.updatableElement(json(), elementCtor@{update: dynamic ->
                updateNavbar = update
                return@elementCtor render@{
                    var pathname: dynamic = null
                    if (Globus.location.pathname == "/test.html") {
                        if (prevPathname == null) Shitus.raise("I want prevPathname")
                        pathname = prevPathname
                    } else {
                        pathname = Globus.location.pathname
                    }

                    var highlightedItem = Regex("/([^/]*?)\\.html").find(pathname)?.let {
                        it.groupValues[1]
                    }
                    if (highlightedItem == "sign-up") { // XXX
                        highlightedItem = "sign-in"
                    }
                    return@render renderTopNavbar(highlightedItem = highlightedItem)
                }
            })

            DOMReact.render(topNavbarElement, Shitus.byid0("topNavbarContainer"))

            initDynamicFooter()

            __await<dynamic>(loadPageForURL())
        } finally { Shitus.endTrain() }
        return __asyncResult(Unit)
    }

    private fun initDynamicFooter() {
        val jqFooter = jq("#footer")
        jqFooter.append("<div id='${const.elementID.dynamicFooter}'></div>")
        footer = DynamicFooter(this)
        DOMReact.render(footer.toReactElement(), byid0ForSure("dynamicFooter"))
    }

    fun setPage(def: Page) {
        js("$")(global.document).scrollTop(0)
        setRootContent(Shitus.updatableElement(json(), elementCtor@{update: dynamic ->
            //                            TestGlobal.updatePage = update
//                            ui.updatePage = TestGlobal.updatePage
            updatePage = update

            return@elementCtor {Shitus.diva(json("className" to "container", "style" to json("position" to "relative"), "onKeyDown" to def.onKeyDown),
                                            Shitus.updatableElement(json(), {update: dynamic ->
                                                updatePageHeader = update
                                                {Shitus.diva(json(),
                                                             {def.header.toReactElement()},
                                                             if (def.headerControls != null) Shitus.diva(json("style" to json("position" to "absolute", "right" to 14, "top" to 16)), {def.headerControls.toReactElement()}) else null)}
                                            }),
                                            {def.body.toReactElement()}
            )}
        }))

        currentPage = def
    }

    fun loadPageForURL(): Promise<Unit> = async {
        val noise = DebugNoise("loadPageForURL", mute = false, style = DebugNoise.Style.COLON)
        noise.clog(Globus.location.href)

        await(TestGlobal.loadPageForURLLock.sutPause1())

        val user = userMaybe
        val firstRun = loadPageForURLFirstRun
        loadPageForURLFirstRun = false
        urlQuery = parseQueryString(Globus.location.href)
        val pathname = Globus.location.pathname

        var ultimateName =
            if (pathname.endsWith(".html"))
                pathname.substring(pathname.lastIndexOf("/") + 1, pathname.lastIndexOf("."))
            else { // Root of the site (/), otherwise we wouldn't have reached here because of 404
                when (user) {
                    null -> "index"
                    else -> when (user.state) {
                        UserState.COOL -> {
                            Globus.location.pushState(null, "", "/dashboard.html")
                            "dashboard"
                        }
                        UserState.PROFILE_REJECTED,
                        UserState.PROFILE_PENDING,
                        UserState.PROFILE_APPROVAL_PENDING,
                        UserState.BANNED -> {
                            Globus.location.pushState(null, "", "/profile.html")
                            "profile"
                        }
                    }
                }
            }

        var loader by notNull<() -> Promise<Unit>>()

        fun isStaticPage(name: String) = !isDynamicPage(name)

        if (isStaticPage(ultimateName)) {
            fun staticLoader(): Promise<Unit> = async {
                val url = Globus.location.baseWithoutSlash + "/$ultimateName.html"
                var content = await(fetchFromURL("GET", url, null, {it}))
                content = content.substring(content.indexOf("<!-- BEGIN CONTENT -->"), content.indexOf("<!-- END CONTENT -->"))
                setRootContent(rawHTML(content))
            }
            loader = ::staticLoader
        } else {
            if (user == null && ultimateName != "sign-in" && ultimateName != "sign-up" && !ultimateName.startsWith("debug")) {
                Globus.location.replaceState(null, "", "sign-in.html")
                ultimateName = "sign-in"
            }

            if (ultimateName == "sign-in") {
                loader = this::loadSignInPage
            } else if (ultimateName == "sign-up") {
                loader = {loadSignUpPage()}
            } else if (user != null || (Globus.mode == Mode.DEBUG && ultimateName.startsWith("debug"))) {
                loader = privatePageLoader(ultimateName)
            }
        }

        val skipBodyRendering =
            firstRun && // JS has just loaded
                isStaticPage(ultimateName) &&
                typedStorageLocal.token == null
//            localStorage.getItem("token") == null

        if (!skipBodyRendering) {
            global.window.disposeStaticShit()

            footer.setBurgerMenu(null)
            await<dynamic>(loader())

            js("$")(global.document).scrollTop(0)
            global.window.initStaticShit()
        }

//            if (token != null) {
//                await<dynamic>(ui.pollLiveStatus())
//            }

        updateNavbar()
        return@async Unit
    }

    fun loadSignUpPage(): Promise<Unit> {"__async"
        return __await(KotlinShit.loadSignUpPage(this)) /ignora
    }

    fun setRootContent(newRootContent: dynamic) {
        rootContent = newRootContent

        if (updateRoot == null) {
            rootElement = Shitus.updatableElement(json(), {update: dynamic ->
                updateRoot = update
                {Shitus.diva(json(), rootContent)}
            })
            DOMReact.render(rootElement, Shitus.byid0("root"))
        }

        updateRoot()
    }

    fun isDynamicPage(name: String): Boolean {
        return KotlinShit.isDynamicPage(name)
    }

    fun privatePageLoader(name: String): () -> Promise<Unit> {
        return when (name) {
            "dashboard" -> ({"__async"
                __await(KotlinShit.loadDashboardPage(this)) /ignora
            })
            "admin-users" -> ({"__async"
                __await(KotlinShit.loadAdminUsersPage(this)) /ignora
            })
            "profile" -> ({"__async"
                __await(KotlinShit.loadProfilePage(this)) /ignora
            })
            "debug" -> ({"__async"
                __reawait(DebugPage(this).load())
            })
            "orders" -> ({
                when (Globus.clientKind) {
                    ClientKind.UA_CUSTOMER -> CustomerUAOrdersPage(this).load()
                    ClientKind.UA_WRITER -> WriterOrdersPage(this).load()
                }
            })
            "order" -> ({
                when (Globus.clientKind) {
                    ClientKind.UA_CUSTOMER -> CustomerSingleUAOrderPage(this).load()
                    ClientKind.UA_WRITER -> imf()
                }
            })
            else -> wtf("privatePageLoader for [$name]")
        }
    }

    fun renderTopNavbar(highlightedItem: String?): dynamic {
        fun _t(en: String, ua: String) = ua
        return renderTopNavbar(Globus.clientKind, ::_t, highlightedItem = highlightedItem, ui = this)
    }

    fun unmountShit() {
        DOMReact.unmountComponentAtNode(Shitus.byid0("topNavbarContainer"))
        DOMReact.unmountComponentAtNode(Shitus.byid0("dynamicFooter"))
        DOMReact.unmountComponentAtNode(Shitus.byid0("root"))
    }

//    fun mountShit() {
//        DOMReact.render(topNavbarElement, Shitus.byid0("topNavbarContainer"))
//        DOMReact.render(rootElement, Shitus.byid0("root"))
//    }

}

class Page(
    val header: ToReactElementable,
    val headerControls: ToReactElementable? = null,
    val body: ToReactElementable,
    val onKeyDown: ((ReactEvent) -> Unit)? = null
)



