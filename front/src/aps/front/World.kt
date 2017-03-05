/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import aps.ClientKind.*
import aps.UserState.*
import aps.front.Globus.clientKind
import into.kommon.*
import jquery.jq
import kotlin.js.json

class LoadPageForURLParams(
    val scroll: Scroll = LoadPageForURLParams.Scroll.TOP
) {
    enum class Scroll { TOP, PRESERVE }
}

class World(val name: String) {
    lateinit var rootContent: ReactElement
    lateinit var currentPage: Page
    var tokenMaybe: String? = null
    var signedUpOK: Boolean = false
    var userMaybe: UserRTO? = null
    lateinit var urlQuery: Map<String, String> // TODO:vgrechka @kill
    lateinit var updateNavbar: () -> Unit
    var prevPathname: String? = null
    var prevHref: String? = null
    var firstTimeLoadingPage = true
    lateinit var updatePage: () -> Unit
    lateinit var updatePageHeader: () -> Unit
    lateinit var updateRoot: () -> Unit
    lateinit var initialEmailFieldValueAfterSignUp: String
    lateinit var rootElement: ReactElement
    lateinit var topNavbarElement: ReactElement
    lateinit var footer: DynamicFooter
    var navbarHighlight: PageSpec? = null
    var booted = false

    val token: String get() = tokenMaybe!!
    val user: UserRTO get() = userMaybe.let {it ?: bitch("I want a fucking user")}

    suspend fun boot() {
        dwarnStriking("Booting world", name)
        hrss.browserOld.ui = this
//        KotlinShit.ui = this

        global.onpopstate = {e: dynamic ->
            breatheBanner.show()
            asu {loadPageForURL()}
            breatheBanner.hide()
        }

        hrss.browserOld.impl = this
        KotlinShit.clientImpl = this
//        initEffects()
//        if (MODE == "debug") {
//            Shitus.initDebugFunctionsShit()
//        }
        Globus.worldMaybe = this
        bootKillme()

        if (isTest()) {
            TestLocationBar.update()
        }
        send(PingRequest())
    }

//    fun urlLink(def: dynamic): dynamic {
//        return KotlinShit.urlLink(this, def)
//    }

    fun pageLink(def: dynamic): dynamic {
        return KotlinShit.pageLink(this, def)
    }

    fun getUser(): UserRTO? {
        return userMaybe
    }

    fun setUser(x: dynamic) {
        userMaybe = x
    }

    suspend fun signOut() {
        Globus.currentBrowseroid.typedStorageLocal.clear()
        tokenMaybe = null
        userMaybe = null
        replaceNavigate("/")
    }

    suspend fun pushNavigate(url: String) {
        loc.pushState(null, "", url)
        loadPageForURL()
    }

    suspend fun replaceNavigate(url: String, p: LoadPageForURLParams = LoadPageForURLParams()) {
        loc.replaceState(null, "", url)
        loadPageForURL(p)
    }

    suspend fun bootKillme() {
        val tsl = Globus.currentBrowseroid.typedStorageLocal
        tokenMaybe = tsl.token
        if (tokenMaybe != null) {
            val res = send(tokenMaybe, SignInWithTokenRequest())
            exhaustive/when (res) {
                is FormResponse2.Hunky -> {
                    userMaybe = res.meat.user
                }
                is FormResponse2.Shitty -> {
                    // Pretend no one was signed in.
                    // User will be able to see actual rejection reason (ban or something) on subsequent sign in attempt.
                    tokenMaybe = undefined
                    tsl.clear()
                }
            }
        }

        topNavbarElement = Shitus.updatableElement(json(), elementCtor@{update: dynamic ->
            updateNavbar = update
            return@elementCtor render@{
//                var pathname: dynamic = null
//                if (loc.pathname == "/test.html") {
//                    if (prevPathname == null) Shitus.raise("I want prevPathname")
//                    pathname = prevPathname
//                } else {
//                    pathname = loc.pathname
//                }
//
//                var highlightedItem = Regex("/([^/]*?)\\.html").find(pathname)?.let {
//                    it.groupValues[1]
//                }
//                if (highlightedItem == "sign-up") { // XXX
//                    highlightedItem = "sign-in"
//                }
                return@render renderTopNavbar(clientKind, ::t, highlight = navbarHighlight, ui = this)
            }
        })

        _DOMReact.render(topNavbarElement, navbarContainer())
        initDynamicFooter()
        loadPageForURL()
        booted = true
    }

    private fun initDynamicFooter() {
        val jqFooter = jq("#footer")
        jqFooter.append("<div id='${const.elementID.dynamicFooter}'></div>")
        footer = DynamicFooter(this)
        Globus.currentBrowseroid.reactoid.mount(footer.toReactElement(), dynamicFooterContainer())
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
                                            {def.body.toReactElement()},
                                            kdiv(height = "1.5rem").toReactElement()
            )}
        }))

        currentPage = def
    }

    suspend fun loadPageForURL(p: LoadPageForURLParams = LoadPageForURLParams()) {
        val user = userMaybe
        dwarnStriking("loadPageForURL", loc.href)
        urlQuery = parseQueryString(loc.href) // TODO:vgrechka @kill
        val pathname = loc.pathname

        var changeURLTo: PageSpec? = null
        var page: PageSpec = when {
            pathname.endsWith(".html") -> {
                val path = pathname.substring(pathname.lastIndexOfOrDie("/") + 1, pathname.lastIndexOfOrDie("."))
                val pages = when (clientKind) {
                    UA_CUSTOMER -> pages.uaCustomer
                    UA_WRITER -> when {
                        user?.kind == UserKind.ADMIN -> pages.uaAdmin
                        else -> pages.uaWriter
                    }
                }
                bang(pages.items.find {it.path == path})
            }

            // TODO:vgrechka This is wrong if pushStating:
            // Root of the site (/). Otherwise we wouldn't have reached here because of 404

            user == null -> {
                when (clientKind) {
                    UA_CUSTOMER -> pages.uaCustomer.index
                    UA_WRITER -> pages.uaWriter.index
                }
            }

            else -> {
                when (user.state) {
                    COOL -> {
                        when (clientKind) {
                            UA_CUSTOMER -> pages.uaCustomer.dashboard
                            UA_WRITER -> pages.uaWriter.dashboard
                        }
                    }
                    PROFILE_REJECTED, PROFILE_PENDING, PROFILE_APPROVAL_PENDING, BANNED -> {
                        when (clientKind) {
                            UA_CUSTOMER -> pages.uaCustomer.profile
                            UA_WRITER -> pages.uaWriter.profile
                        }
                    }
                }.also {
                    changeURLTo = it
                }
            }
        }

        if (page.requiresSignIn && user == null) {
            page = when (clientKind) {
                UA_CUSTOMER -> pages.uaCustomer.signIn
                UA_WRITER -> pages.uaWriter.signIn
            }.also {
                changeURLTo = it
            }
        }

        changeURLTo?.let {loc.pushState(null, "", "/${it.path}.html")}

        val firstRun = firstTimeLoadingPage
        firstTimeLoadingPage = false
        val skipBodyRendering =
            firstRun
            && page.skipFirstTimeRendering
            // && user == null // TODO:vgrechka Is this needed?

        if (!skipBodyRendering) {
            ExternalGlobus.disposeStaticShit()
            footer.setBurgerMenu(null)
            val oldScrollTop = jqbody.scrollTop()
            val error = page.load(this)
            if (error != null) {
                openErrorModal(error.msg)
            } else {
                jqbody.scrollTop(when (p.scroll) {
                                     LoadPageForURLParams.Scroll.TOP -> 0.0
                                     LoadPageForURLParams.Scroll.PRESERVE -> oldScrollTop
                                 })
                ExternalGlobus.initStaticShit()
            }
        }

        navbarHighlight = page
        updateNavbar()
        await(effects).unblinkAll()
        TestGlobal.pageLoadedLock.resumeTestFromSut()
    }

    fun setRootContent(newRootContent: dynamic) {
        rootContent = newRootContent

        if (updateRoot == null) {
            rootElement = Shitus.updatableElement(json(), {update: dynamic ->
                updateRoot = update
                {Shitus.diva(json(), rootContent)}
            })
            _DOMReact.render(rootElement, rootContainer())
        }

        updateRoot()
    }

//    fun renderTopNavbar(highlight: Page?): ReactElement {
//        fun _t(en: String, ua: String) = ua
//        return renderTopNavbar(clientKind, ::_t, highlight = highlight, ui = this)
//    }

    fun shelveVisualShit() {
        _DOMReact.unmountComponentAtNode(navbarContainer())
        _DOMReact.unmountComponentAtNode(rootContainer())
        _DOMReact.unmountComponentAtNode(dynamicFooterContainer())
    }

    fun unshelveVisualShit() {
        _DOMReact.render(rootElement, rootContainer())
        _DOMReact.render(topNavbarElement, navbarContainer())
        _DOMReact.render(rootElement, rootContainer())
    }

    private fun navbarContainer() = bang(Globus.topNavbarContainer)
    private fun rootContainer() = bang(byid0("root"))
    private fun dynamicFooterContainer() = byid0ForSure("dynamicFooter")

    val xlobal = object:Xlobal {
        override val user get()= userMaybe
    }
}

class Page(
    val header: ToReactElementable,
    val headerControls: ToReactElementable? = null,
    val body: ToReactElementable,
    val onKeyDown: ((ReactEvent) -> Unit)? = null
)















