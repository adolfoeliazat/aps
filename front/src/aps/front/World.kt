/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*

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

            header = pageHeader(t("Sign In", "Вход")),
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
                    console.log("Failed to private_getUserInfo", e)
                    token = undefined
                    hrss.storageLocal.clear()
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
//            prevPageStuff = currentPageStuff
//            currentPageStuff = json()

        val firstRun = loadPageForURLFirstRun
        loadPageForURLFirstRun = false

        var href: dynamic = null
        var pathname: dynamic = null
        if (global.document.location.pathname == "/test.html") {
            if (prevHref == null || prevPathname == null) Shitus.raise("I want prevHref and prevPathname")
            console.log("Loading page against previous URL: ${prevHref}")
            href = prevHref
            pathname = prevPathname
        } else {
            prevHref = global.location.href
            href = prevHref
            prevPathname = global.document.location.pathname
            pathname = prevPathname
        }

        TestGlobal.loadPageForURL_href = href

        urlQuery = parseQueryString(href)

        val path = pathname
        var name: dynamic = null
        if (path.endsWith(".html")) {
            name = path.slice(path.lastIndexOf("/") + 1, path.lastIndexOf("."))
        } else {
            name = "home"
        }

        var loader: dynamic = null

        val isDynamic = isDynamicPage(name)
        if (!isDynamic) {
            loader = {"__async"
                val href = if (name == "home") "/" else "${name}.html"
                var content: dynamic = (__await<dynamic>(global.superagent.get(href).send())).text
                content = content.slice(content.indexOf("<!-- BEGIN CONTENT -->"), content.indexOf("<!-- END CONTENT -->"))
                setRootContent(rawHtml(content))
            }
        }

        if (!loader) {
            if (isDynamic && user == null && name !== "sign-in" && name !== "sign-up" && !name.startsWith("debug-")) {
                global.history.replaceState(null, "", "sign-in.html")
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
            console.error("Can’t determine fucking loader for path ${path}")
            return@loadPageForURL __asyncResult(Unit)
        }

        val skipBodyRendering = firstRun && !isDynamic && global.window.staticShitIsRenderedStatically
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



