/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*
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

class World {
    lateinit var rootContent: ReactElement
    lateinit var currentPage: Page
    var token: String? = null
    var signedUpOK: Boolean = false
    var user: UserRTO? = null
    lateinit var urlQuery: Map<String, String>
    lateinit var updateNavbar: () -> Unit
    var prevPathname: String? = null
    var prevHref: String? = null
    var loadPageForURLFirstRun: Boolean = false
    lateinit var updatePage: () -> Unit
    lateinit var updatePageHeader: () -> Unit
    lateinit var updateRoot: () -> Unit

    fun boot(): Promise<Unit> {"__async"
        hrss.browser.ui = this
        KotlinShit.ui = this

        global.onpopstate = {e: dynamic -> "__async"
            breatheBanner.show()
            __await<dynamic>(loadPageForURL())
            breatheBanner.hide()
        }

        hrss.browser.impl = this
        KotlinShit.clientImpl = this
        __await<dynamic>(bootKillme())

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
        setPage(Page(
            header = oldShitAsToReactElementable(pageHeader(t("Sign In", "Вход"))),
            body = kdiv{o->
                o-nif(signedUpOK) {preludeWithGreenCheck(json("title" to t(
                    "Cool. You have an account now. We sent you email with password.",
                    "Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом.")))}

                o-FormMatumba<SignInWithPasswordRequest, SignInResponse>(FormSpec(
                    SignInWithPasswordRequest(),
                    this,
                    primaryButtonTitle = t("Sign In", "Войти"),
                    onError = {
                        spec.req.password.value = ""
                    },

                    onSuccessa = {res -> "__async"
                        user = res.user
//                        ui.startLiveStatusPolling()
                        token = res.token
                        hrss.storageLocal.setItem("token", token!!)

                        __await(pushNavigate(when (res.user.state) {
                                                    UserState.COOL -> "dashboard.html"
                                                    else -> "profile.html"
                                                })) /ignora
                    }
                ))

                o-nif(!signedUpOK) {kdiv{o->
                    o-hr()
                    o-kdiv(textAlign="left"){o->
                        o-t("TOTE", "Как? Еще нет аккаунта? ")
                        o-urlLink(tamyShamy="createAccount",
                                  title=t("TOTE", "Срочно создать!"),
                                  url="sign-up.html",
                                  delayActionForFanciness=true)

//                        o-ui.urlLink(json("tamyShamy" to "createAccount", "title" to t("TOTE", "Срочно создать!"), "url" to "sign-up.html", "delayActionForFanciness" to true))
                    }
                }}
            }
        ))
    }

    fun signOut() {
        hrss.storageLocal.clear()
        token = null
        user = null
        replaceNavigate("/")
    }

    fun pushNavigate(where: dynamic): Promise<Unit> {"__async"
        return __await(KotlinShit.jsFacing_pushNavigate(this, where)) /ignora
    }

    fun replaceNavigate(where: dynamic): Promise<Unit> {"__async"
        global.history.replaceState(null, "", where)
        return __await<dynamic>(loadPageForURL()) /ignora
    }

    fun bootKillme(): Promise<Unit> {"__async"
        Shitus.beginTrain(json("name" to "boot()")); try {
            token = hrss.storageLocal.getItem("token")
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
                    hrss.storageLocal.clear()
                } finally {
                    Globus.makeSignInNavbarLinkVisible()
                }
            }

            js("$")(global.document.head).append("<style id='css'>${css()}</style>")

            hrss.browser.topNavbarElement = Shitus.updatableElement(json(), elementCtor@{update: dynamic ->
                updateNavbar = update
                return@elementCtor render@{
                    var pathname: dynamic = null
                    if (global.location.pathname == "/test.html") {
                        if (prevPathname == null) Shitus.raise("I want prevPathname")
                        pathname = prevPathname
                    } else {
                        pathname = global.location.pathname
                    }

                    var highlightedItem = pathname.slice(1, pathname.length - ".html".length)
                    if (highlightedItem === "sign-up") { // XXX
                        highlightedItem = "sign-in"
                    }
                    return@render renderTopNavbar(json("highlightedItem" to highlightedItem))
                }
            })

            global.ReactDOM.render(hrss.browser.topNavbarElement, Shitus.byid0("topNavbarContainer"))

            __await<dynamic>(loadPageForURL())
        } finally { Shitus.endTrain() }
        return __asyncResult(Unit)
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
        val firstRun = loadPageForURLFirstRun
        loadPageForURLFirstRun = false

        urlQuery = parseQueryString(window.location.href)

        val pathname = window.location.pathname
        var name =
            if (pathname.endsWith(".html"))
                pathname.substring(pathname.lastIndexOf("/") + 1, pathname.lastIndexOf("."))
            else "home"

        var loader: dynamic = null

        fun isStaticPage(name: String) = !isDynamicPage(name)

        if (isStaticPage(name)) {
            loader = {"__async"
                val href = if (name == "home") "/" else "${name}.html"
                // TODO:vgrechka @ditch-superagent
                var content: dynamic = (__await<dynamic>(global.superagent.get(href).send())).text
                content = content.slice(content.indexOf("<!-- BEGIN CONTENT -->"), content.indexOf("<!-- END CONTENT -->"))
                setRootContent(rawHTML(content))
            }
        } else {
            if (user == null && name !== "sign-in" && name !== "sign-up" && !name.startsWith("debug-")) {
                window.history.replaceState(null, "", "sign-in.html")
                name = "sign-in"
            }

            if (name === "sign-in") {
                loader = {loadSignInPage()}
            } else if (name === "sign-up") {
                loader = {loadSignUpPage()}
            } else if (user != null || (MODE === "debug" && name.startsWith("debug-"))) {
                loader = privatePageLoader(name)
            }
        }

        if (!loader) {
            console.error("Can't figure out fucking loader")
            return __asyncResult(Unit)
        }

        val skipBodyRendering =
            firstRun && // JS has just loaded
            isStaticPage(name) &&
            localStorage.getItem("token") == null

        if (!skipBodyRendering) {
            global.window.disposeStaticShit()
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
        return __await(KotlinShit.loadSignUpPage()) /ignora
    }

    fun setRootContent(newRootContent: dynamic) {
        rootContent = newRootContent

        if (updateRoot == null) {
            hrss.browser.rootElement = Shitus.updatableElement(json(), {update: dynamic ->
                updateRoot = update
                {Shitus.diva(json(), rootContent)}
            })
            global.ReactDOM.render(hrss.browser.rootElement, Shitus.byid0("root"))
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
                __await(KotlinShit.loadDashboardPage()) /ignora
            })

            "admin-users" -> ({"__async"
                __await(KotlinShit.loadAdminUsersPage()) /ignora
            })

            "profile" -> ({"__async"
                __await(KotlinShit.loadProfilePage()) /ignora
            })

            "debug-kotlin-playground" -> ({"__async"
                KotlinShit.loadDebugKotlinPlaygroundPage() /ignora
            })

            else -> wtf("privatePageLoader for [$name]")
        }
    }

    fun renderTopNavbar(arg: dynamic): dynamic {
        return KotlinShit.renderTopNavbar_calledByFuckingUI(KotlinShit.ui, arg)
    }
}

class Page(
    val header: ToReactElementable,
    val headerControls: ToReactElementable? = null,
    val body: ToReactElementable,
    val onKeyDown: ((ReactEvent) -> Unit)? = null
)



