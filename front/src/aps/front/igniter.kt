/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

//fun jsFacing_makeUIShitIgniter(def: dynamic): dynamic {
//    fun igniter() {
//
//
//
//
//
//
//
//        jshit.browsers = json()
//        // let ui, impl, browser
//
//        jshit.browser = json() // Kind of production
//
//        jshit.lang = global.LANG
//        jshit._t = jshit.utils.makeT(jshit.lang)
//
//        // TODO:vgrechka Needed?
////        jshit.debugRPC = makeRPC({
////            basicMessage() {
////                return {db: DB, LANG: "en", clientKind: "debug", DANGEROUS_TOKEN}
////            },
////        })
//
//        if (MODE == "debug") {
//            // await initClientStackSourceMapConsumer()
//            jshit.initDebugFunctions()
//        }
//
//        if (!global.location.pathname.endsWith("/test.html")) {
//            global.DB = (global.sessionStorage.getItem("DB") as String?) ?: "aps-dev" // TODO:vgrechka @security
//            __await<dynamic>(makeCleanPairAndBoot())
//
//            __await<dynamic>(jshit.utils.fova(::maybeInvokeSomeDebugShitAfterBootDuringIgnition))
//        } else {
//            __await<dynamic>(KotlinShit.igniteTestShit(::makeCleanPairAndBoot))
//        }
//
//        var hotCodeListener: dynamic = null
//
//        var impl: dynamic = null
//        var ui: dynamic = null
//
//        fun makeCleanPairAndBoot(): Promise<Unit> {"__async"
//            fun instantiateImpl() {
//                jshit.browser.impl = def.Impl(json("ui" to ui))
//                impl = jshit.browser.impl
//            }
//
//            // TODO:vgrechka Was declared hot
//            fun initUIFunctions(ui: dynamic) {
//                val clientKind: dynamic = global.CLIENT_KIND
//
//                // TODO:vgrechka Needed?
////                ui.rpc = makeRPC({
////                    attachToTrain: true,
////                    basicMessage() {
////                        return {db: DB, LANG, clientKind, token: ui.token}
////                    },
////                    callOptions(message) {
////                        if (MODE == "debug" && DEBUG_SIMULATE_SLOW_NETWORK) {
////                            let slowNetworkSimulationDelay = 0
////                            if (isInTestScenario()) {
////                                slowNetworkSimulationDelay = getRPCLagForAutomatedTests()
////                                // dlog({slowNetworkSimulationDelay})
////                            } else {
////                                slowNetworkSimulationDelay = DEBUG_RPC_LAG_FOR_MANUAL_TESTS
////                            }
////                            return {slowNetworkSimulationDelay}
////                        }
////                    },
////                })
//
//                // TODO:vgrechka Needed?
////                ui.liveStatusRPC = makeRPC({
////                    attachToTrain: false,
////                    basicMessage() {
////                        return {db: DB, LANG, clientKind, token: ui.token}
////                    },
////                })
//
//            global.Object.assign(ui, json(
//
//                // TODO:vgrechka Needed?
////                tabs({name, active, tabDefs, makeLinkForTab}) {
////                    active = active || tabDefs[0].name
////                    return ula({className: "nav nav-tabs"}, ...map(tabDefs, (tab, i) => {
////                    const isActive = tab.name == active
////                        return lia({className: isActive ? "active" : "", tame: "tab-${name}${sufindex(i)}`, shame: tab.name, tattrs: {active: isActive || undefined}}, tab.content)
////                }))
////                },
////
////                taby(def) {
////                    #extract {title, liveStatusFieldName, url} from def
////
////                    if (url) {
////                        const me = {
////                            render() {
////                                return ui.urlLink({
////                                    elementID: me.elementID,
////                                    url,
////                                    blinkOpts: {dleft: 0, dtop: 0, dwidth: -2},
////                                    content: spana({},
////                                    spancTitle({title}),
////                                    liveStatusFieldName && ui.liveBadge2({liveStatusFieldName}))})
////                            },
////                        }
////
////                        me.controlTypeName = "taby"
////                        implementControlShit({me, def})
////                        return elcl(me)
////                    }
////
////                    raise("WTF the taby link should be like?")
////                },
//
//                // TODO:vgrechka Needed?
////                setToughLuckPage({res}) {
////                    return ui.setPage({
////                        header: pageHeader({title: t(`Tough Luck`, `Облом")}),
////                        body: diva({}, errorBanner(s{content: res.error}))
////                    })
////                },
//
////                busyButton(def) {
////                    #extract {onClick} from def
////                    def.id = def.id || puid()
////
////                    let working
////
////                        return button({async onClick() {
////                            if (working) return
////                            effects.blinkOn({target: byid(def.id), dtop: 0})
////                            if (def.name) {
////                                testGlobal["button_" + def.name + "_blinks"] = true
////                            }
////                            try {
////                                __await<dynamic>(onClick())
////                            } catch (e) {
////                                // TODO:vgrechka Some kind of standard alert if onClick doesn't want to handle shit itself?
////                                throw e
////                            } finally {
////                                effects.blinkOff()
////                                if (def.name) {
////                                    testGlobal["button_" + def.name + "_blinks"] = false
////                                }
////                            }
////                        }}.asn1(def))
////                },
//
////                renderMoreable(def) {
////                    #extract {style, itemsRes, itemsReq, renderItem, chunkName="chunk", chunkIndex=0} from def
////
////                    let bottom
////
////                        if (itemsRes.moreFromID) {
////                            bottom = updatableElement(s{}, update => {
////                                const moreButtonID = puid()
////                                let thing = diva({style}, button({id: moreButtonID, tamyShamy: "showMore", title: t("Show More", "Показать еще"), style: {background: BLUE_GRAY_50, width: "100%", marginTop: 15}, async onClick() {
////                                beginTrain({name: "Show more"}); try {
////                                effects.blinkOn({target: byid(moreButtonID), dtop: -16})
////                                // testGlobal["button_showMore_blinks"] = true
////                                const moreRes = __await<dynamic>(ui.rpcSoft(asn(itemsReq, {fromID: itemsRes.moreFromID})))
////                                if (moreRes.error) { console.error(moreRes.error); return }
////                                // TODO:vgrechka Handle RPC error in Show More button    408e3096-aab1-42f5-9209-a9b35e7b5800
////                                effects.blinkOff()
////                                // testGlobal["button_showMore_blinks"] = false
////                                update(thing = ui.renderMoreable({itemsRes: moreRes, itemsReq, renderItem, chunkName, chunkIndex: chunkIndex + 1}))
////                            } finally { endTrain() }
////                            }
////                            }))
////
////                                return _=> thing
////                            })
////                        }
////
////                    return diva({controlTypeName: "renderMoreable"},
////                        run(_=> {
////                            if (!itemsRes.items.length) return diva({style: {marginTop: 10}}, t("TOTE", "Здесь ничего нет, такие дела..."))
////                            return diva({tame: "${chunkName}${sufindex(chunkIndex)}"}, ...itemsRes.items.map((item, index) => renderItem(s{item, index})))
////                        }),
////                    bottom,
////                    )
////                },
//
////                "liveBadge2" to {def: dynamic ->
////                    KotlinShit.liveBadge2(ui, def)
////                },
////
////
////                liveBadge(def) {
////                    return kot.aps.front.KotlinShit.liveBadge(ui, def)
////                },
//
//
//                "pollLiveStatus" to {"__async"
////                        if (!ui.token) {
////                            // console.error("Trying to poll live status without token")
////                            return
////                        }
////
////                        ui.liveStatus = __await<dynamic>(ui.liveStatusRPC({fun: "private_getLiveStatus"}, {dontSetLastResponse: true}))
////                        values(ui.liveStatusUpdaters).forEach(run)
//                },
//
////                startLiveStatusPolling() {
////                    ui.liveStatusPollingIntervalHandle = setInterval(_=> {
////                        if (!liveStatusPollingViaIntervalDisabled) {
////                            ui.pollLiveStatus()
////                        }
////                    }, LIVE_STATUS_POLLING_INTERVAL)
////                },
////
////                stopLiveStatusPolling() {
////                    clearInterval(ui.liveStatusPollingIntervalHandle)
////                },
//
//                "urlLink" to {def: dynamic ->
//                    #extract {url, delayActionForFanciness, blinkOpts, style} from def
//
//                    const id = puid()
//
//                    return link({controlTypeName: "urlLink", style, id, async onClick() {
//                        effects.blinkOn(asn({target: byid(id), dtop: 3}, blinkOpts))
//                        if (name) {
//                            byid(id).css("text-decoration", "none")
//                            testGlobal["link_" + name + "_blinks"] = true
//                        }
//
//                        if (delayActionForFanciness && !(isInTestScenario() && testSpeed == "fast")) {
//                            __await<dynamic>(delay(ACTION_DELAY_FOR_FANCINESS))
//                        }
//
//                        __await<dynamic>(entraina(s{name: "Navigate via urlLink: ${url}", async act() {
//                            __await<dynamic>(ui.pushNavigate(url))
//                        }}))
//
//                        effects.blinkOff()
//                        if (name) {
//                            byid(id).css("text-decoration", "")
//                            testGlobal["link_" + name + "_blinks"] = false
//                        }
//                    }}.asn1(def))
//                },
//
//
//                pageLink(def) {
//                    raise("kill me please, i don’t deserve living")
//                    #extract {url, delayActionForFanciness, blinkOpts} from def
//
//                    if (!def.id) def.id = puid()
//
//                    return link(asn(def, {async onClick() {
//                        effects.blinkOn(asn({target: byid(def.id), dtop: 3}, blinkOpts))
//                        if (name) {
//                            byid(def.id).css("text-decoration", "none")
//                            testGlobal["link_" + name + "_blinks"] = true
//                        }
//
//                        if (delayActionForFanciness && !(isInTestScenario() && testSpeed == "fast")) {
//                            __await<dynamic>(delay(ACTION_DELAY_FOR_FANCINESS))
//                        }
//                        __await<dynamic>(ui.pushNavigate(url))
//
//                        effects.blinkOff()
//                        if (name) {
//                            byid(def.id).css("text-decoration", "")
//                            testGlobal["link_" + name + "_blinks"] = false
//                        }
//                    }}))
//                },
//
//                getUser() {
//                    return ui.user
//                },
//
//                setUser(x) {
//                    ui.user = x
//                },
//
//                async boot() {
//                    beginTrain({name: "boot()"}); try {
//                    ui.token = storageLocal.getItem("token")
//                    if (ui.token) {
//                        try {
//                            const res = __await<dynamic>(ui.rpc({fun: "private_getUserInfo", token: ui.token}))
//                            ui.user = res.user
//                            ui.startLiveStatusPolling()
//                        } catch (e) {
//                            // Pretend no one was signed in.
//                            // User will be able to see actual rejection reason (ban or something) on subsequent sign in attempt.
//                            dlog("Failed to private_getUserInfo", e)
//                            ui.token = undefined
//                            storageLocal.clear()
//                        }
//                    }
//
//                    $(document.head).append("<style id="css">${impl.css()}</style>")
//
//                    ReactDOM.render(browser.topNavbarElement = updatableElement(s{}, update => {
//                        ui.updateNavbar = update
//                        return _=> {
//                        let pathname
//                            if (location.pathname == "/test.html") {
//                                if (!ui.prevPathname) raise("I want prevPathname")
//                                pathname = ui.prevPathname
//                            } else {
//                                pathname = location.pathname
//                            }
//
//                        let highlightedItem = pathname.slice(1, pathname.length - ".html".length)
//                        if (highlightedItem == "sign-up") { // XXX
//                            highlightedItem = "sign-in"
//                        }
//                        return impl.renderTopNavbar({highlightedItem})
//                    }
//                    }), byid0("topNavbarContainer"))
//
//                    __await<dynamic>(ui.loadPageForURL())
//                } finally { endTrain() }
//                },
//
//                signOut() {
//                    storageLocal.clear()
//                    ui.token = undefined
//                    ui.user = undefined
//                    ui.stopLiveStatusPolling()
//                    replaceNavigate("/")
//                },
//
//                async pushNavigate(where) {
//                    __await<dynamic>(kot.aps.front.KotlinShit.jsFacing_pushNavigate(ui, where))
//                },
//
//                async replaceNavigate(where) {
//                    history.replaceState(null, "", where)
//                    __await<dynamic>(ui.loadPageForURL())
//                },
//
//                setPage(def) {
//                    #extract {header, headerControls, body, onKeyDown} from def
//
//                    $(document).scrollTop(0)
//                    ui.setRootContent(updatableElement(s{}, update => {
//                        ui.updatePage = testGlobal.updatePage = update
//
//                        return _=> diva({className: "container", style: {position: "relative"}, onKeyDown},
//                        updatableElement(s{}, update => {
//                            ui.updatePageHeader = update
//                            return _=> diva({},
//                            header,
//                            headerControls && diva({style: {position: "absolute", right: 14, top: 16}}, headerControls))
//                        }),
//                        body,
//                        )
//                    }))
//
//                    debugStatusBar.setFunctions([])
//
//                    ui.currentPage = orig$def // @ctx forgetmenot-1-3
//                },
//
//                async loadPageForURL() {
//                    ui.prevPageStuff = ui.currentPageStuff
//                    ui.currentPageStuff = {}
//
//                    const firstRun = ui.loadPageForURLFirstRun
//                        ui.loadPageForURLFirstRun = false
//
//                    let href, pathname
//                    if (document.location.pathname == "/test.html") {
//                        if (!ui.prevHref || !ui.prevPathname) raise("I want prevHref and prevPathname")
//                        dlog("Loading page against previous URL: ${ui.prevHref}")
//                        href = ui.prevHref
//                        pathname = ui.prevPathname
//                    } else {
//                        href = ui.prevHref = location.href
//                        pathname = ui.prevPathname = document.location.pathname
//                    }
//
//                    testGlobal.loadPageForURL_href = href
//                    ui.urlObject = url.parse(href)
//                    ui.urlQuery = querystring.parse(ui.urlObject.query)
//                    const path = pathname
//                        let name
//                        if (path.endsWith(".html")) {
//                            name = path.slice(path.lastIndexOf("/") + 1, path.lastIndexOf("."))
//                        } else {
//                            name = "home"
//                        }
//
//                    let loader
//
//                        const isDynamic = impl.isDynamicPage(name)
//                    if (!isDynamic) {
//                        loader = async function() {
//                            const href = name == "home" ? "/" : "${name}.html"
//                            let content = (__await<dynamic>(superagent.get(href).send())).text
//                            content = content.slice(content.indexOf("<!-- BEGIN CONTENT -->"), content.indexOf("<!-- END CONTENT -->"))
//                            ui.setRootContent(rawHtml(content))
//                        }
//                    }
//
//                    if (!loader) {
//                        if (isDynamic && !ui.user && name != "sign-in" && name != "sign-up" && !name.startsWith("debug-")) {
//                            history.replaceState(null, "", "sign-in.html")
//                            name = "sign-in"
//                        }
//
//                        if (name == "sign-in") {
//                            loader = ui.loadSignInPage
//                        } else if (name == "sign-up") {
//                            loader = ui.loadSignUpPage
//                        } else if (ui.user || (MODE == "debug" && name.startsWith("debug-"))) {
//                            loader = impl.privatePageLoader(name)
//                        }
//                    }
//
//                    if (!loader) {
//                        console.error("Can’t determine fucking loader for path ${path}")
//                        return
//                    }
//
//                    const skipBodyRendering = firstRun && !isDynamic && window.staticShitIsRenderedStatically
//                    if (!skipBodyRendering) {
//                        window.disposeStaticShit()
//                        __await<dynamic>(loader+())
//                        $(document).scrollTop(0)
//                        window.initStaticShit()
//                    }
//
//                    if (ui.token) {
//                        __await<dynamic>(ui.pollLiveStatus())
//                    }
//
//                    ui.updateNavbar()
//                },
//
//                loadSignInPage: kot.aps.front.KotlinShit.loadSignInPageCtor(ui),
//
//
//                makeSignUpFields(def) {
//                    return [
//                        ui.TextField(s{
//                            name: "email",
//                            title: t("Email", "Почта"),
//                        }),
//                        ui.TextField(s{
//                            name: "firstName",
//                            title: t("First Name", "Имя"),
//                        }),
//                        ui.TextField(s{
//                            name: "lastName",
//                            title: t("Last Name", "Фамилия"),
//                        }),
//                        ]
//                },
//
//                async loadSignUpPage() {
//                    __await<dynamic>(kot.aps.front.KotlinShit.loadSignUpPage(ui))
//                },
//
//
//                setRootContent(newRootContent) {
//                    ui.rootContent = newRootContent
//
//                    if (!ui.updateRoot) {
//                        ReactDOM.render(browser.rootElement = updatableElement(s{}, update => {
//                            ui.updateRoot = update
//                            return _=> diva({}, ui.rootContent)
//                        }), byid0("root"))
//                    }
//
//                    ui.updateRoot()
//                },
//
//
//                async rpcSoft(message) {
//                    try {
//                        // delete message.$self
//                        // message.$definitionStack$json = getCircularJSON().stringify(message.$definitionStack)
//                        // delete message.$definitionStack
//                        // dlog("message", message)
//
//                        return __await<dynamic>(ui.rpc(message))
//                    } catch (e) {
//                        return {error: t("Sorry, service is temporarily unavailable", "Извините, сервис временно недоступен")}
//                    }
//                },
//
//                HiddenField(def) {
//                    #extract {name, value} from def
//
//                    return {
//                        getName() { return name },
//                        setError(x) {},
//                        setDisabled(x) {},
//                        getTitle() {},
//                        focus() {},
//                        render() {},
//
//                        contributeToRequest(def) {
//                            #extract {reb} from def
//                            reb.set(s{key: name, value})
//                        },
//                    }
//                },
//
//                TextField(def) {
//                    #extract {name, title, type, kind} from def
//
//                    if (!kind) kind = "input"
//
//                    let error
//
//                        const input = Input({
//                        tamy: true, type, kind,
//                        volatileStyle() {
//                            if (error) return {paddingRight: 30}
//                        }
//                    })
//
//                    return {
//                        render(def) {
//                            return diva({controlTypeName: "TextField", tamy: name, className: "form-group"},
//                                title && labela({}, spanc({tame: "label", content: title})),
//                                diva({style: {position: "relative"}},
//                                    input,
//                                    error && errorLabel(s{name, title: error, style: {marginTop: 5, marginRight: 9, textAlign: "right"}}),
//                                    error && diva({style: {width: 15, height: 15, backgroundColor: RED_300, borderRadius: 10, position: "absolute", right: 8, top: 10}})))
//                        },
//
//                        getName() { return name },
//                        setError(x) { error = x },
//                        getError() { return error },
//                        setDisabled(x) { input.setDisabled(x) },
//                        isDisabled() { return input.isDisabled() },
//                        getTitle() { return title },
//                        focus() { input.focus() },
//
//                        contributeToRequest(def) {
//                            #extract {reb} from def
//                            reb.set(s{key: name, value: input.getValue()})
//                        },
//
//                        getValue() { return input.getValue() },
//
//                        setValue(x) {
//                            if (typeof x != "string") raise("Only string values, please, fuck you")
//                            input.setValue(x)
//                        },
//                    }
//                },
//
//
//                AgreeTermsField(def) {
//                    const name = "agreeTerms"
//                    let error
//
//                        const checkbox = Checkbox({tamy: true})
//
//                    return {
//                        render() {
//                            return diva({controlTypeName: "AgreeTermsField", tame: "AgreeTermsField", className: "form-group"},
//                                diva({style: {display: "flex"}},
//                                    checkbox,
//                                    diva({style: {width: 5}}),
//                                    diva({},
//                                        spanc({tame: "prose", content: t("I’ve read and agreed with ", "Я прочитал и принял ")}),
//                                        link({tamy: true, title: t("terms and conditions", "соглашение"), onClick: popupTerms})),
//                                    error && diva({style: {width: 15, height: 15, borderRadius: 10, marginTop: 3, marginRight: 9, marginLeft: "auto", backgroundColor: RED_300}})),
//                                error && errorLabel(s{name, title: error, style: {marginTop: 5, marginRight: 9, textAlign: "right"}}))
//                        },
//
//                        getName() { return name },
//                        setError(x) { error = x },
//                        setDisabled(x) { checkbox.setDisabled(x) },
//                        getTitle() {},
//
//                        contributeToRequest(def) {
//                            #extract {reb} from def
//                            reb.set(s{key: name, value: checkbox.getValue()})
//                        },
//
////                            getValue() { return checkbox.getValue() },
//                    }
//
//                    // TODO:vgrechka Implement popupTerms()    4bac9d8c-8a3c-4978-9efb-7bda3bf1ac84
//                    function popupTerms() {
//                        alert("terms here")
//                    }
//                },
//
//                Form: kot.aps.front.KotlinShit.makeFormCtor(ui),
//
//
//            })
//        }
//
//            const ui = makeUI()
//            let impl
//                instantiateImpl()
//            __await<dynamic>(ui.boot())
//
//            if (MODE == "debug") {
//                if (hotCodeListener) {
//                    hotCodeListener.kill()
//                }
//
//                hotCodeListener = __await<dynamic>(runHotCodeUpdateStuff({ // @ctx hot shit
//                    hotFunctions: [makeOrRemakeExportedShit, def.Impl, initDebugFunctions, /*revealer.revealControl,*/ initUIFunctions, makeOrRemakeArt,
//                    ...hotFunctions.map(hof => hof.fun)],
//
//                    async applyHotCode({updatedHotFunctions}) {
//                        isHotReloading = true
//                        revealStack.calledTimes = 0
//
//                        {
//                            const things = values(thingsToDoBeforeHotUpdate)
//                            thingsToDoBeforeHotUpdate = {}
//                            for (const thing of things) {
//                            __await<dynamic>(thing())
//                        }
//                        }
//
//                        for (const hof of hotFunctions) {
//                        const fun = updatedHotFunctions[hof.fun.hotShitTag]
//                        if (fun) {
//                            fun()
//                            dlog("Hot-refreshed ${fun.name}")
//                        }
//                    }
//
//                        const hotImpl = updatedHotFunctions[def.Impl.hotShitTag]
//                        const hotMakeOrRemakeExportedShit = updatedHotFunctions[makeOrRemakeExportedShit.hotShitTag]
//
//                        // const shouldMakeOrRemakeExportedShit = hotMakeOrRemakeExportedShit || hotImpl
//                        if (true) {
//                            if (hotMakeOrRemakeExportedShit) {
//                                makeOrRemakeExportedShit = hotMakeOrRemakeExportedShit
//                            }
//                            makeOrRemakeExportedShit()
//                            dlog("Hot-refreshed makeOrRemakeExportedShit")
//                        }
//
//
//                        { // @wip kotlin
//                            invalidateKotlinStackSourceMapConsumer()
//
//                            __await<dynamic>(new Promise((resolve, reject) => {
//                            (function(d, script) {
//                                script = d.createElement("script")
//                                script.type = "text/javascript"
//                                script.async = true
//                                script.onload = function() {
//                                    resolve()
//                                }
//                                script.src = "kotlin/front-enhanced.js"
//                                d.getElementsByTagName("head")[0].appendChild(script)
//                            }(document))
//                        }))
//
//                            dlog("Hot-refreshed kotlin/front-enhanced.js")
//
//                            kokoko(!!"hotIgnition")
//                        }
//
//                        const hotMakeOrRemakeArt = updatedHotFunctions[makeOrRemakeArt.hotShitTag]
//                        if (hotMakeOrRemakeArt) {
//                            makeOrRemakeArt = hotMakeOrRemakeArt
//                        }
////                        if (hotMakeOrRemakeArt) {
////                            makeOrRemakeArt = hotMakeOrRemakeArt
//                        const oldStepDescriptions = art.stepDescriptions
//                            makeOrRemakeArt()
//                        art.stepDescriptions = oldStepDescriptions
//
//                        dlog("Hot-refreshed makeOrRemakeArt")
////                        }
//
//
//                        const hotInitUIFunctions = updatedHotFunctions[initUIFunctions.hotShitTag]
//                        if (hotInitUIFunctions) {
//                            initUIFunctions = hotInitUIFunctions
//                            initUIFunctions(ui)
//                            dlog("Hot-refreshed initUIFunctions")
//                        }
//
//                        if (hotImpl) {
//                            def.Impl = hotImpl
//                        }
////                        if (hotImpl) {
//                        impl.stale = true // @ctx forgetmenot-1-4
////                            def.Impl = hotImpl
//                        instantiateImpl()
//                        byid("css").replaceWith("<style id="css">${impl.css()}</style>")
//                        dlog("Hot-refreshed Impl")
////                        }
//
//                        const hotInitDebugFunctions = updatedHotFunctions[initDebugFunctions.hotShitTag]
//                        if (hotInitDebugFunctions) {
//                            hotInitDebugFunctions()
//                            dlog("Hot-refreshed initDebugFunctions")
//                        }
//
////                        const hotRevealControl = updatedHotFunctions[revealer.revealControl.hotShitTag]
////                        if (hotRevealControl) {
////                            revealer.revealControl = hotRevealControl
////                            dlog("Hot-refreshed revealer.revealControl")
////                        }
//
//                        // const shouldReloadPage = hotMakeOrRemakeExportedShit || hotInitUIFunctions || hotImpl || hotMakeOrRemakeArt || hotInitDebugFunctions || hotRevealControl
//                        const shouldReloadPage = true
//
//                        if (shouldReloadPage) {
//                            dlog("----- Reloading page -----")
//                            fov(closeControlRevealer)
//
//                            // debugPanes.deleteAll()
//
//                            const prevDebugSimulateSlowNetwork = DEBUG_SIMULATE_SLOW_NETWORK
//                                DEBUG_SIMULATE_SLOW_NETWORK = false
//                            try {
//                                testGlobal.controls = {}
//                                const scrollTop = $(document).scrollTop()
//                                __await<dynamic>(ui.loadPageForURL())
//                                $(document).scrollTop(scrollTop)
//                                __await<dynamic>(ui.pollLiveStatus())
////                                art.invokeStateContributions()
//                                kot.aps.front.KotlinShit.jsFacing_art_invokeStateContributions()
//                            } finally {
//                                DEBUG_SIMULATE_SLOW_NETWORK = prevDebugSimulateSlowNetwork
//                            }
//
//                            {
//                                const things = values(thingsToDoAfterHotUpdate)
//                                thingsToDoAfterHotUpdate = {}
//                                for (const thing of things) {
//                                __await<dynamic>(thing())
//                            }
//                            }
//
//                            if (testGlobal.explicitMinimalGertrude == undefined) testGlobal.explicitMinimalGertrude = false
//                            testGlobal.minimalGertrude = testGlobal.explicitMinimalGertrude
//
//                            if (reassertUIState) {
//                                dlog("Reasserting fucking UI state")
//                                __await<dynamic>(reassertUIState({scrollThere: false, thisIsReassertion: true}))
//                                noException: art.openTestPassedPane(openTestPassedPaneArgs)
//                            }
//                        }
//                    }
//                }))
//            }
//
//            function makeUI() {
//                const ui = art.ui = browser.ui = {liveStatus: {}, liveStatusUpdaters: {}, prevPageStuff: {}, currentPageStuff: {}}
//
//                window.onpopstate = async function(e) {
//                    breatheBanner.show()
//                    __await<dynamic>(ui.loadPageForURL())
//                    breatheBanner.hide()
//                }
//
//
//                initUIFunctions(ui)
//
//                return ui
//            }
//        }
//
//    }
//
//    return ::igniter
//}
//
//
//
//fun maybeInvokeSomeDebugShitAfterBootDuringIgnition(): Promise<Unit> {"__async"
//    return __asyncResult(Unit)
//}


