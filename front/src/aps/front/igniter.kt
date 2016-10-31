/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*

@JsName("ignite")
fun ignite() {
    println("----- Igniting front Kotlin shit -----")

    global.__asyncResult = {x: dynamic -> x}

    global.DEBUG_ACTION_HAND_DELAY = 1000
    global.ACTION_DELAY_FOR_FANCINESS = 1000
    global.LIVE_STATUS_POLLING_INTERVAL = 30000
    global.MODE = "debug"
    global.DEBUG_SIMULATE_SLOW_NETWORK = true
    global.DEBUG_RPC_LAG_FOR_MANUAL_TESTS = 500
    global.BOOTSTRAP_VERSION = 3
    global.BACKEND_URL = "http://localhost:3100"

    InitAutoReload()

//    global.Error.stackTraceLimit = js("Infinity")

//    global.igniteShit = makeUIShitIgniter()
//    global.igniteShit = makeUIShitIgniter(makeAPSShitImplCtor())

    // For hot reloading
//    global.makeAPSShitImplCtor = ::makeAPSShitImplCtor

    initEffects()
    Shitus.initTrains()

    global.Shitus = Shitus

    igniteShit()
}

fun igniteShit(): Promise<Unit> {"__async"
    hrss.browser = Browser("default") // Kind of production

    hrss.lang = global.LANG
    hrss._t = Shitus.makeT(hrss.lang)

    if (MODE == "debug") {
        Shitus.initDebugFunctionsShit()
    }

    fun makeCleanPairAndBoot(): Promise<Unit> {"__async"
        var impl: dynamic = null

        fun initUIFunctions(ui: ShitPile) {
            val ui2 = UI2(ui)

            val clientKind = global.CLIENT_KIND

            ui.pollLiveStatus = {"__async"
                __asyncResult(__await(KotlinShit.pollLiveStatus(ui)))
            }

            ui.startLiveStatusPolling = {
                KotlinShit.startLiveStatusPolling(ui)
            }

            ui.stopLiveStatusPolling = {
                KotlinShit.stopLiveStatusPolling(ui)
            }

            ui.urlLink = {def: dynamic ->
                KotlinShit.urlLink(ui, def)
            }

            ui.pageLink = {def: dynamic ->
                KotlinShit.pageLink(ui, def)
            }

            ui.getUser = {
                ui.user
            }

            ui.setUser = {x: dynamic ->
                ui.user = x
            }

            ui.boot = {"__async"
                Shitus.beginTrain(json("name" to "boot()")); try {
                    ui.token = hrss.storageLocal.getItem("token")
                    if (ui.token != null) {
                        try {
                            val res = __await(SignInWithTokenRequest.send(ui.token!!))
                            ui.user = res.user
                            ui.startLiveStatusPolling()
                        } catch (e: Throwable) {
                            // Pretend no one was signed in.
                            // User will be able to see actual rejection reason (ban or something) on subsequent sign in attempt.
                            console.log("Failed to private_getUserInfo", e)
                            ui.token = undefined
                            hrss.storageLocal.clear()
                        }
                    }

                    js("$")(global.document.head).append("<style id='css'>${impl.css()}</style>")

                    hrss.browser.topNavbarElement = Shitus.updatableElement(json(), elementCtor@{update: dynamic ->
                        ui.updateNavbar = update
                        return@elementCtor render@{
                            var pathname: dynamic = null
                            if (global.location.pathname == "/test.html") {
                                if (ui.prevPathname == null) Shitus.raise("I want prevPathname")
                                pathname = ui.prevPathname
                            } else {
                                pathname = global.location.pathname
                            }

                            var highlightedItem = pathname.slice(1, pathname.length - ".html".length)
                            if (highlightedItem === "sign-up") { // XXX
                                highlightedItem = "sign-in"
                            }
                            return@render impl.renderTopNavbar(json("highlightedItem" to highlightedItem))
                        }
                    })

                    global.ReactDOM.render(hrss.browser.topNavbarElement, Shitus.byid0("topNavbarContainer"))

                    __await<dynamic>(ui.loadPageForURL())
                } finally { Shitus.endTrain() }
            }

            ui.signOut = {
                hrss.storageLocal.clear()
                ui.token = null
                ui.user = null
                ui.stopLiveStatusPolling()
                ui.replaceNavigate("/")
            }

            ui.pushNavigate = {where: dynamic -> "__async"
                __asyncResult(__await(KotlinShit.jsFacing_pushNavigate(ui, where)))
            }

            ui.replaceNavigate = {where: dynamic -> "__async"
                global.history.replaceState(null, "", where)
                __await<dynamic>(ui.loadPageForURL())
            }

            ui.setPage = {def: dynamic ->
                val header: dynamic = def.header
                val headerControls: dynamic = def.headerControls
                val body: dynamic = def.body
                val onKeyDown: dynamic = def.onKeyDown

                js("$")(global.document).scrollTop(0)
                ui.setRootContent(Shitus.updatableElement(json(), elementCtor@{update: dynamic ->
//                            TestGlobal.updatePage = update
//                            ui.updatePage = TestGlobal.updatePage
                    ui.updatePage = update

                    return@elementCtor {Shitus.diva(json("className" to "container", "style" to json("position" to "relative"), "onKeyDown" to onKeyDown),
                                                    Shitus.updatableElement(json(), {update: dynamic ->
                                                        ui.updatePageHeader = update
                                                        {Shitus.diva(json(),
                                                                     header,
                                                                     headerControls && Shitus.diva(json("style" to json("position" to "absolute", "right" to 14, "top" to 16)), headerControls))}
                                                    }),
                                                    body
                    )}
                }))

                ui.currentPage = def
            }

            ui.loadPageForURL = loadPageForURL@{"__async"
                ui.prevPageStuff = ui.currentPageStuff
                ui.currentPageStuff = json()

                val firstRun = ui.loadPageForURLFirstRun
                ui.loadPageForURLFirstRun = false

                var href: dynamic = null
                var pathname: dynamic = null
                if (global.document.location.pathname == "/test.html") {
                    if (ui.prevHref == null || ui.prevPathname == null) Shitus.raise("I want prevHref and prevPathname")
                    console.log("Loading page against previous URL: ${ui.prevHref}")
                    href = ui.prevHref
                    pathname = ui.prevPathname
                } else {
                    ui.prevHref = global.location.href
                    href = ui.prevHref
                    ui.prevPathname = global.document.location.pathname
                    pathname = ui.prevPathname
                }

                TestGlobal.loadPageForURL_href = href

                ui.urlQuery = parseQueryString(href)

                val path = pathname
                var name: dynamic = null
                if (path.endsWith(".html")) {
                    name = path.slice(path.lastIndexOf("/") + 1, path.lastIndexOf("."))
                } else {
                    name = "home"
                }

                var loader: dynamic = null

                val isDynamic = impl.isDynamicPage(name)
                if (!isDynamic) {
                    loader = {"__async"
                        val href = if (name == "home") "/" else "${name}.html"
                        var content: dynamic = (__await<dynamic>(global.superagent.get(href).send())).text
                        content = content.slice(content.indexOf("<!-- BEGIN CONTENT -->"), content.indexOf("<!-- END CONTENT -->"))
                        ui.setRootContent(rawHtml(content))
                    }
                }

                if (!loader) {
                    if (isDynamic && ui.user == null && name !== "sign-in" && name !== "sign-up" && !name.startsWith("debug-")) {
                        global.history.replaceState(null, "", "sign-in.html")
                        name = "sign-in"
                    }

                    if (name === "sign-in") {
                        loader = ui.loadSignInPage
                    } else if (name === "sign-up") {
                        loader = ui.loadSignUpPage
                    } else if (ui.user != null || (MODE === "debug" && name.startsWith("debug-"))) {
                        loader = impl.privatePageLoader(name)
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

                if (ui.token != null) {
                    __await<dynamic>(ui.pollLiveStatus())
                }

                ui.updateNavbar()
            }

            ui.loadSignInPage = KotlinShit.loadSignInPageCtor(ui, ui2)

            ui.loadSignUpPage = {"__async"
                __await(KotlinShit.loadSignUpPage())
            }

            ui.setRootContent = {newRootContent: dynamic ->
                ui.rootContent = newRootContent

                if (ui.updateRoot == null) {
                    hrss.browser.rootElement = Shitus.updatableElement(json(), {update: dynamic ->
                        ui.updateRoot = update
                        {Shitus.diva(json(), ui.rootContent)}
                    })
                    global.ReactDOM.render(hrss.browser.rootElement, Shitus.byid0("root"))
                }

                ui.updateRoot()
            }
        }

        fun makeUI(): ShitPile {
            hrss.browser.ui = ShitPile()
            val ui = hrss.browser.ui

            global.onpopstate = {e: dynamic -> "__async"
                breatheBanner.show()
                __await<dynamic>(ui.loadPageForURL())
                breatheBanner.hide()
            }


            initUIFunctions(ui)

            return ui
        }

        val ui: ShitPile = makeUI()

        fun instantiateImpl() {
            hrss.browser.impl = makeAPSShitImplCtor()(json("ui" to ui))
            impl = hrss.browser.impl
        }

        instantiateImpl()
        __await<dynamic>(ui.boot())



//        __await<dynamic>(KotlinShit.initHotCodeShit(
//            impl,
//            ::instantiateImpl,
//            //                def,
//            ui,
//            js("[]")
//        ))

        return __asyncResult(Unit)
    }

    if (global.location.pathname.endsWith("/test.html")) {
        __await(jsFacing_igniteTestShit(::makeCleanPairAndBoot))
    } else {
        global.DB = global.sessionStorage.getItem("DB") ?: "aps-dev" // TODO:vgrechka @security
        __await<dynamic>(makeCleanPairAndBoot())
    }

    return __asyncResult(Unit)
}

val breatheBanner: dynamic by lazy {
    json(
        "show" to {
            js("$")("body").append("""
                <div id="breathe" style="position: fixed; left: 0px; top: 0px; z-index: 1000000; width: 100%; height: 100%; ">
                <div class="container">
                <div style="display: flex; align-items: center; justify-content: center; position: absolute; left: 0px; top: 200px; width: 100%; border-top: 2px dashed #b0bec5; border-bottom: 2px dashed #b0bec5; padding: 50px 0px; background-color: rgba(255,255,255,0.85);">
                <span style="margin-left: 10; font-weight: bold;">Дышите глубоко...</span>
                <div id="wholePageTicker" class="progressTicker" style="background-color: #546e7a; width: 14px; height: 28px; margin-left: 10px; margin-top: -5px"></div>
                </div>
                </div>
                </div>""")
        },

        "hide" to {
            Shitus.byid("breathe").remove()
        }
    )
}

fun makeAPSShitImplCtor(): (dynamic) -> Json {
    return {arg: dynamic ->
        KotlinShit.ui = arg.ui

        val impl = json(

            "isDynamicPage" to KotlinShit.isDynamicPage,

            "css" to {
                KotlinShit.apsCSS()
            },

            "privatePageLoader" to { name: dynamic ->
                val shit: dynamic = json(
                    "dashboard" to {
                        "__async"
                        __await(KotlinShit.loadDashboardPage())
                    },

                    "admin-users" to {
                        "__async"
                        __await(KotlinShit.loadAdminUsersPage())
                    },

                    "profile" to {
                        "__async"
                        __await(KotlinShit.loadProfilePage())
                    },

                    "debug-kotlin-playground" to {
                        KotlinShit.loadDebugKotlinPlaygroundPage()
                    }
                )
                shit[name]
            },

            "renderTopNavbar" to { arg: dynamic ->
                KotlinShit.renderTopNavbar_calledByFuckingUI(KotlinShit.ui, arg)
            }
        )

        KotlinShit.clientImpl = impl
        impl
    }
}




