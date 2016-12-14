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

/*
Boot Sequence
-------------

- HTML is loaded
- If there's a token in localStorage
      - Display breathe banner regardless of whether the page in address bar is static,
        because returning user expects her name to be in the top right corner (and we don't know that name yet)
      - We can store user's name in localStorage too, but it's still not known which non-static menu items
        should be visible to her. Also the user may be banned, etc.
      - Until JS is loaded and user credentials are fetched from backend, top right corner should be empty
        (i.e. no "Sign In" link)
- If page is static, display its content immediately and allow user to browse
- Otherwise display breathe banner
- Start loading JS
- If user clicks any link until JS is loaded and ignited, everything will just start over
  TODO:vgrechka Think about possibility to load and cache JS in separate thread
                (investigate web workers, "progressive" web apps, and all that trendy stuff)
- JS is loaded
- Navbar is updated
- If static content is being displayed, page body shouldn't be redrawn because user may be in the middle
  of scrolling or watching animation, etc. But all links on page are replaced with fancy-blinking ones
- If breathe banner is being displayed, do usual dynamic loading of page corresponding to URL in address bar

Fancy-Blinking Links
--------------------

- Before JS is loaded, all links are usual links
- After JS is loaded and ignited and until manual page reload, all links are fancy-blinking
- Clicking on a fancy-blinking link to a dynamic page:
      - Show fancy blinking underline
      - Make necessary request(s) to backend
      - On retrieving necessary data, hide fancy blinking and redraw page
- Clicking on a fancy-blinking link to a static page:
      - Should be consistent with the look and feel of dynamic page loading
      - Load corresponding static HTML via simple XHR and convert usual links there into fancy-blinking ones,
        then redraw page body with that
*/

class World(val name: String) {
    lateinit var rootContent: ReactElement
    lateinit var currentPage: Page
    var token: String? = null
    var signedUpOK: Boolean = false
    var user: UserRTO? = null
    val userSure: UserRTO get() = user.let {it ?: bitch("I want a fucking user")}
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

        Globus.world = this
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
        return user
    }

    fun setUser(x: dynamic) {
        user = x
    }

    fun loadSignInPage() {
        SignInPage(this).load()
    }

    fun signOut() {
        typedStorageLocal.clear()
//        hrss.storageLocal.clear()
        token = null
        user = null
        replaceNavigate("/")
    }

    fun pushNavigate(url: String): Promise<Unit> {"__async"
        return __reawait(KotlinShit.jsFacing_pushNavigate(this, url))
    }

    fun replaceNavigate(where: dynamic): Promise<Unit> {"__async"
        global.history.replaceState(null, "", where)
        return __await<dynamic>(loadPageForURL()) /ignora
    }

    fun bootKillme(): Promise<Unit> {"__async"
        Shitus.beginTrain(json("name" to "boot()")); try {
            token = typedStorageLocal.token
//            token = hrss.storageLocal.getItem("token")
            if (token != null) {
                try {
                    val res = __await(SignInWithTokenRequest.send(token!!))
                    user = res.user
//                        ui.startLiveStatusPolling()
                } catch (e: Throwable) {
                    // Pretend no one was signed in.
                    // User will be able to see actual rejection reason (ban or something) on subsequent sign in attempt.
                    console.warn("Failed to private_getUserInfo", e)
                    token = undefined
                    typedStorageLocal.clear()
//                    hrss.storageLocal.clear()
                } finally {
//                    ExternalGlobus.makeSignInNavbarLinkVisible()
                }
            }

            js("$")(global.document.head).append("<style id='css'>${css()}</style>")

            topNavbarElement = Shitus.updatableElement(json(), elementCtor@{update: dynamic ->
                updateNavbar = update
                return@elementCtor render@{
                    var pathname: dynamic = null
                    if (global.location.pathname == "/test.html") {
                        if (prevPathname == null) Shitus.raise("I want prevPathname")
                        pathname = prevPathname
                    } else {
                        pathname = global.location.pathname
                    }

                    var highlightedItem = Regex("/([^/]*?)\\.html").find(pathname)?.let {
                        it.groupValues[1]
                    }
                    if (highlightedItem == "sign-up") { // XXX
                        highlightedItem = "sign-in"
                    }
                    return@render renderTopNavbar(this, json("highlightedItem" to highlightedItem))
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
        jqFooter.append("<div id='dynamicFooter'></div>")
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

    fun loadPageForURL(): Promise<Unit> {"__async"
        val noise = DebugNoise("loadPageForURL", mute = false, style = DebugNoise.Style.COLON)
        noise.clog(window.location.href)

        val user = user
        val firstRun = loadPageForURLFirstRun
        loadPageForURLFirstRun = false
        urlQuery = parseQueryString(window.location.href)
        val pathname = window.location.pathname

        var ultimateName =
            if (pathname.endsWith(".html"))
                pathname.substring(pathname.lastIndexOf("/") + 1, pathname.lastIndexOf("."))
            else { // Root of the site (/), otherwise we wouldn't have reached here because of 404
                when (user) {
                    null -> "index"
                    else -> when (user.state) {
                        UserState.COOL -> {
                            window.history.pushState(null, "", "/dashboard.html")
                            "dashboard"
                        }
                        UserState.PROFILE_REJECTED,
                        UserState.PROFILE_PENDING,
                        UserState.PROFILE_APPROVAL_PENDING,
                        UserState.BANNED -> {
                            window.history.pushState(null, "", "/profile.html")
                            "profile"
                        }
                    }
                }
            }

        var loader: dynamic = null
        fun isStaticPage(name: String) = !isDynamicPage(name)

        if (isStaticPage(ultimateName)) {
            fun staticLoader(): Promise<Unit> = async {
//                val href = if (ultimateName == "index") "/" else "${ultimateName}.html"
                val href = "${ultimateName}.html"
//                var content: dynamic = (await<dynamic>(global.superagent.get(href).send())).text
                var content = await(fetchFromURL("GET", href, null, {it}))
                content = content.substring(content.indexOf("<!-- BEGIN CONTENT -->"), content.indexOf("<!-- END CONTENT -->"))
                setRootContent(rawHTML(content))
            }
            loader = ::staticLoader
        } else {
            if (user == null && ultimateName != "sign-in" && ultimateName != "sign-up" && !ultimateName.startsWith("debug")) {
                window.history.replaceState(null, "", "sign-in.html")
                ultimateName = "sign-in"
            }

            if (ultimateName == "sign-in") {
                loader = {loadSignInPage()}
            } else if (ultimateName == "sign-up") {
                loader = {loadSignUpPage()}
            } else if (user != null || (MODE == "debug" && ultimateName.startsWith("debug"))) {
                loader = privatePageLoader(ultimateName)
            }
        }

        if (!loader) {
            console.error("Can't figure out fucking loader")
            return __asyncResult(Unit)
        }

        val skipBodyRendering =
            firstRun && // JS has just loaded
            isStaticPage(ultimateName) &&
            typedStorageLocal.token == null
//            localStorage.getItem("token") == null

        if (!skipBodyRendering) {
            global.window.disposeStaticShit()

            footer.setBurgerMenu(null)
            __await<dynamic>(loader())

            js("$")(global.document).scrollTop(0)
            global.window.initStaticShit()
        }

//            if (token != null) {
//                __await<dynamic>(ui.pollLiveStatus())
//            }

        updateNavbar()
        return __asyncResult(Unit)
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

    fun css(): String {
        return KotlinShit.apsCSS()
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
                when (theClientKind) {
                    ClientKind.CUSTOMER -> CustomerUAOrdersPage(this).load()
                    ClientKind.WRITER -> WriterOrdersPage(this).load()
                }
            })
            "order" -> ({
                when (theClientKind) {
                    ClientKind.CUSTOMER -> CustomerSingleUAOrderPage(this).load()
                    ClientKind.WRITER -> imf()
                }
            })
            else -> wtf("privatePageLoader for [$name]")
        }
    }

    fun renderTopNavbar(world: World, arg: dynamic): dynamic {
        return KotlinShit.renderTopNavbar_calledByFuckingUI(world, arg)
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

    val tokenSure: String get() = token!!
}

class Page(
    val header: ToReactElementable,
    val headerControls: ToReactElementable? = null,
    val body: ToReactElementable,
    val onKeyDown: ((ReactEvent) -> Unit)? = null
)



