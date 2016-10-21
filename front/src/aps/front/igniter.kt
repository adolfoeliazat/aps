/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

var global: dynamic = null

@JsName("ignite")
fun ignite() {
    println("----- Igniting front Kotlin shit -----")
    global = js("window")

    global.__asyncResult = {x: dynamic -> x}

    initTestShit()
    igniteRPCShit()

    makeAPSShitIgniter()

    // For hot reloading
    global.makeAPSShitImplCtor = ::makeAPSShitImplCtor

    initEffects()
    Shitus.initTrains()

    global.Shitus = Shitus

    global.igniteShit()
}

fun makeAPSShitIgniter() {
    global.DEBUG_ACTION_HAND_DELAY = 1000
    global.ACTION_DELAY_FOR_FANCINESS = 1000
    global.LIVE_STATUS_POLLING_INTERVAL = 30000
    global.MODE = "debug"
    global.DEBUG_SIMULATE_SLOW_NETWORK = true
    global.DEBUG_RPC_LAG_FOR_MANUAL_TESTS = 500
    global.BOOTSTRAP_VERSION = 3
    global.BACKEND_URL = "http://localhost:3100"

//    global.Error.stackTraceLimit = js("Infinity")

    global.igniteShit = makeUIShitIgniter(json(

        "Impl" to makeAPSShitImplCtor()
    ))
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

fun makeUIShitIgniter(def: dynamic): dynamic {
    fun igniter() {"__async"
        hrss.browsers = json()
        // let ui, impl, browser

        hrss.browser = json() // Kind of production

        hrss.lang = global.LANG
        hrss._t = Shitus.makeT(hrss.lang)

//        debugRPC = makeRPC({
//            basicMessage() {
//                return {db: DB, LANG: 'en', clientKind: 'debug', DANGEROUS_TOKEN}
//            },
//        })

        if (MODE == "debug") {
            // await initClientStackSourceMapConsumer()
            Shitus.initDebugFunctionsShit()
        }

        fun makeCleanPairAndBoot(): Promise<Unit> {"__async"
            var impl: dynamic = null

            fun initUIFunctions(ui: dynamic) {
                val clientKind = global.CLIENT_KIND
                global.Object.assign(ui, json(
                    "pollLiveStatus" to {"__async"
                        __await(KotlinShit.pollLiveStatus(ui))
                    },

                    "startLiveStatusPolling" to {
                        KotlinShit.startLiveStatusPolling(ui)
                    },

                    "stopLiveStatusPolling" to {
                        KotlinShit.stopLiveStatusPolling(ui)
                    },

                    "urlLink" to {def: dynamic ->
                        KotlinShit.urlLink(ui, def)
                    },

                    "pageLink" to {def: dynamic ->
                        KotlinShit.pageLink(ui, def)
                    },

                    "getUser" to {
                        ui.user
                    },

                    "setUser" to {x: dynamic ->
                        ui.user = x
                    },

                    "boot" to {"__async"
                        Shitus.beginTrain(json("name" to "boot()")); try {
                            ui.token = hrss.storageLocal.getItem("token")
                            if (ui.token) {
                                try {
                                    val res = __await<dynamic>(ui.rpc(json("fun" to "private_getUserInfo", "token" to ui.token)))
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
                                        if (!ui.prevPathname) Shitus.raise("I want prevPathname")
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
                    },

                    "signOut" to {
                        hrss.storageLocal.clear()
                        ui.token = null
                        ui.user = null
                        ui.stopLiveStatusPolling()
                        ui.replaceNavigate("/")
                    },

                    "pushNavigate" to {where: dynamic -> "__async"
                        __await(KotlinShit.jsFacing_pushNavigate(ui, where))
                    },

                    "replaceNavigate" to {where: dynamic -> "__async"
                        global.history.replaceState(null, "", where)
                        __await<dynamic>(ui.loadPageForURL())
                    },

                    "setPage" to {def: dynamic ->
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
                    },

                    "loadPageForURL" to loadPageForURL@{"__async"
                        ui.prevPageStuff = ui.currentPageStuff
                        ui.currentPageStuff = json()

                        val firstRun = ui.loadPageForURLFirstRun
                        ui.loadPageForURLFirstRun = false

                        var href: dynamic = null
                        var pathname: dynamic = null
                        if (global.document.location.pathname == "/test.html") {
                            if (!ui.prevHref || !ui.prevPathname) Shitus.raise("I want prevHref and prevPathname")
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

                        ui.urlQuery = KotlinShit.parseQueryString(href)

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
                            if (isDynamic && !ui.user && name !== "sign-in" && name !== "sign-up" && !name.startsWith("debug-")) {
                                global.history.replaceState(null, "", "sign-in.html")
                                name = "sign-in"
                            }

                            if (name === "sign-in") {
                                loader = ui.loadSignInPage
                            } else if (name === "sign-up") {
                                loader = ui.loadSignUpPage
                            } else if (ui.user || (MODE === "debug" && name.startsWith("debug-"))) {
                                loader = impl.privatePageLoader(name)
                            }
                        }

                        if (!loader) {
                            console.error("Can’t determine fucking loader for path ${path}")
                            return@loadPageForURL
                        }

                        val skipBodyRendering = firstRun && !isDynamic && global.window.staticShitIsRenderedStatically
                        if (!skipBodyRendering) {
                            global.window.disposeStaticShit()
                            __await<dynamic>(loader())
                            js("$")(global.document).scrollTop(0)
                            global.window.initStaticShit()
                        }

                        if (ui.token) {
                            __await<dynamic>(ui.pollLiveStatus())
                        }

                        ui.updateNavbar()
                    },

                    "loadSignInPage" to KotlinShit.loadSignInPageCtor(ui),

                    "loadSignUpPage" to {"__async"
                        __await(KotlinShit.loadSignUpPage())
                    },

                    "setRootContent" to {newRootContent: dynamic ->
                        ui.rootContent = newRootContent

                        if (!ui.updateRoot) {
                            hrss.browser.rootElement = Shitus.updatableElement(json(), {update: dynamic ->
                                ui.updateRoot = update
                                {Shitus.diva(json(), ui.rootContent)}
                            })
                            global.ReactDOM.render(hrss.browser.rootElement, Shitus.byid0("root"))
                        }

                        ui.updateRoot()
                    }

                ))
            }

            fun makeUI() {
                hrss.browser.ui = json("liveStatus" to json(), "liveStatusUpdaters" to json(), "prevPageStuff" to json(), "currentPageStuff" to json())
                val ui = hrss.browser.ui

                global.onpopstate = {e: dynamic -> "__async"
                    breatheBanner.show()
                    __await<dynamic>(ui.loadPageForURL())
                    breatheBanner.hide()
                }


                initUIFunctions(ui)

                return ui
            }

            val ui: dynamic = makeUI()

            fun instantiateImpl() {
                hrss.browser.impl = def.Impl(json("ui" to ui))
                impl = hrss.browser.impl
            }

            instantiateImpl()
            __await<dynamic>(ui.boot())

            __await<dynamic>(KotlinShit.initHotCodeShit(
                impl,
                ::instantiateImpl,
                def,
                ui,
                js("[]")
            ))

            return __asyncResult(Unit)
        }

        if (!global.location.pathname.endsWith("/test.html")) {
            global.DB = global.sessionStorage.getItem("DB") ?: "aps-dev" // TODO:vgrechka @security
            __await<dynamic>(makeCleanPairAndBoot())
        } else {
            __await(jsFacing_igniteTestShit(::makeCleanPairAndBoot))

        }

    }

    return ::igniter
}

