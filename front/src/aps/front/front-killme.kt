/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

fun jsFacing_makeFormCtor(ui: dynamic): dynamic {
    fun jsFacing_Form(spec: dynamic): dynamic {
        val primaryButtonTitle = spec.primaryButtonTitle; val cancelButtonTitle = spec.cancelButtonTitle; val autoFocus = spec.autoFocus;
        val fields = spec.fields; val rpcFun = spec.rpcFun; val onSuccess = spec.onSuccess; val onError = spec.onError;
        val className = spec.className; val dontShameButtons = spec.dontShameButtons; val errorBannerStyle = spec.errorBannerStyle;
        val debugName = spec.debugName; val getInvisibleFieldNames = spec.getInvisibleFieldNames; val onCancel = spec.onCancel;

        fun formTicker(): dynamic {
            return jshit.elcl(json(
                "render" to {
                    jshit.diva(json("className" to "progressTicker", "style" to json("float" to "right", "width" to 14, "height" to 28, "backgroundColor" to Color.BLUE_GRAY_600)))
                },
                "componentDidMount" to {
                    global.testGlobal["shitSpins"] = true
                },
                "componentWillUnmount" to {
                    global.testGlobal["shitSpins"] = false
                }
            ))
        }

        return jshit.statefulElement(json("ctor" to statefulElementCtor@{update: dynamic ->
            var working: dynamic = undefined
            var error: dynamic = undefined
            var focusedField: dynamic = undefined
            var actualVisibleFieldNames: dynamic = undefined

            fun figureOutActualVisibleFieldNames() {
                actualVisibleFieldNames = fields.map({x -> x.getName()})
                if (getInvisibleFieldNames) {
                    actualVisibleFieldNames = jshit.utils.without.apply(null, js("[]").concat(jsArrayOf(actualVisibleFieldNames), getInvisibleFieldNames()))
                }
            }

            val me = json(
                "getField" to outta@{name: dynamic ->
                    for (field in jsArrayToList(fields)) {
                        if (field.getName() == name) return@outta field
                    }
                    jshit.utils.raise("No fucking field [${name}] in the form")
                },

                "fieldChanged" to {
                    // @wip rejection
                    if (getInvisibleFieldNames) {
                        val oldVisible = jshit.utils.clone(actualVisibleFieldNames)
                        figureOutActualVisibleFieldNames()
                        if (!jshit.utils.isEqual(oldVisible, actualVisibleFieldNames)) {
                            update()
                        }
                    }
                },

                "fieldFocused" to {arg: dynamic ->
                    val field = arg.field
                    focusedField = field
                },

                "fieldBlurred" to {arg: dynamic ->
                    val field = arg.field
                    focusedField = undefined
                },

                "render" to outta@{
                    figureOutActualVisibleFieldNames()

                    return@outta jshit.diva(json(),
                        jshit.forma.apply(null, js("[]").concat(
                            jsArrayOf(
                                json("className" to className),
                                error && jshit.errorBanner(json("content" to error, "style" to errorBannerStyle))),
                            fields
                                .filter({x: dynamic -> actualVisibleFieldNames.includes(x.getName())})
                                .map({x: dynamic -> x.render(json())}),

                            jshit.diva(json("style" to json("textAlign" to "left")),
                                jshit.button(json("tamy" to "primary", "shamy" to if (dontShameButtons) undefined else "primary",
                                    "level" to "primary", "title" to primaryButtonTitle, "disabled" to working,
                                    "onClick" to {"__async"
                                        jshit.beginTrain(json("name" to "Submit fucking${if (debugName) " " + debugName else ""} form")); try {
                                            for (field in jsArrayToList(fields)) {
                                                field.setError(undefined)
                                                field.setDisabled(true)
                                            }
                                            error = undefined
                                            working = true
                                            update()

                                            val reb = jshit.RequestBuilder(json())
                                            reb.set(json("key" to "fun", "value" to rpcFun))
                                            for (field in jsArrayToList(fields.filter({ x: dynamic -> actualVisibleFieldNames.includes(x.getName())}))) {
                                                field.contributeToRequest(json("reb" to reb))
                                            }
                                            val res = __await<dynamic>(ui.rpcSoft(reb.toMessage(json())))

                                            if (res.error) {
                                                error = res.error
                                                __await<dynamic>(jshit.utils.fova(onError, res))
                                            } else {
                                                error = undefined
                                                __await<dynamic>(onSuccess(res))
                                            }

                                            working = false
                                            for (field in jsArrayToList(fields)) {
                                                field.setError(res.fieldErrors && res.fieldErrors[field.getName()])
                                                field.setDisabled(false)
                                            }
                                            update()
                                        } finally { jshit.endTrain() }
                                    })),
                                cancelButtonTitle && jshit.button(json("tamy" to "cancel", "shamy" to if (dontShameButtons) undefined else "cancel",
                                    "title" to cancelButtonTitle, "disabled" to working, "style" to json("marginLeft" to 10), "onClick" to onCancel)),
                                working && formTicker())
                        )))
                },

                "componentDidMount" to {
                    if (focusedField) {
                        focusedField.focus()
                    }
                },

                "componentDidUpdate" to {
                    if (focusedField) {
                        focusedField.focus()
                    }
                }
            )

            for (field in jsArrayToList(fields)) {
                field.form = me
            }

            return@statefulElementCtor me
        }))
    }

    return ::jsFacing_Form
}

fun orig_jsFacing_makeFormCtor(ui: dynamic): dynamic {
    fun jsFacing_Form(spec: dynamic): dynamic {
        val primaryButtonTitle = spec.primaryButtonTitle; val cancelButtonTitle = spec.cancelButtonTitle; val autoFocus = spec.autoFocus;
        val fields = spec.fields; val rpcFun = spec.rpcFun; val onSuccess = spec.onSuccess; val onError = spec.onError;
        val className = spec.className; val dontShameButtons = spec.dontShameButtons; val errorBannerStyle = spec.errorBannerStyle;
        val debugName = spec.debugName; val getInvisibleFieldNames = spec.getInvisibleFieldNames; val onCancel = spec.onCancel;

        fun formTicker(): dynamic {
            return jshit.elcl(json(
                "render" to {
                    jshit.diva(json("className" to "progressTicker", "style" to json("float" to "right", "width" to 14, "height" to 28, "backgroundColor" to Color.BLUE_GRAY_600)))
                },
                "componentDidMount" to {
                    global.testGlobal["shitSpins"] = true
                },
                "componentWillUnmount" to {
                    global.testGlobal["shitSpins"] = false
                }
            ))
        }

        return jshit.statefulElement(json("ctor" to statefulElementCtor@{update: dynamic ->
            var working: dynamic = undefined
            var error: dynamic = undefined
            var focusedField: dynamic = undefined
            var actualVisibleFieldNames: dynamic = undefined

            fun figureOutActualVisibleFieldNames() {
                actualVisibleFieldNames = fields.map({x -> x.getName()})
                if (getInvisibleFieldNames) {
                    actualVisibleFieldNames = jshit.utils.without.apply(null, js("[]").concat(jsArrayOf(actualVisibleFieldNames), getInvisibleFieldNames()))
                }
            }

            val me = json(
                "getField" to outta@{name: dynamic ->
                    for (field in jsArrayToList(fields)) {
                        if (field.getName() == name) return@outta field
                    }
                    jshit.utils.raise("No fucking field [${name}] in the form")
                },

                "fieldChanged" to {
                    // @wip rejection
                    if (getInvisibleFieldNames) {
                        val oldVisible = jshit.utils.clone(actualVisibleFieldNames)
                        figureOutActualVisibleFieldNames()
                        if (!jshit.utils.isEqual(oldVisible, actualVisibleFieldNames)) {
                            update()
                        }
                    }
                },

                "fieldFocused" to {arg: dynamic ->
                    val field = arg.field
                    focusedField = field
                },

                "fieldBlurred" to {arg: dynamic ->
                    val field = arg.field
                    focusedField = undefined
                },

                "render" to outta@{
                    figureOutActualVisibleFieldNames()

                    return@outta jshit.diva(json(),
                        jshit.forma.apply(null, js("[]").concat(
                            jsArrayOf(
                                json("className" to className),
                                error && jshit.errorBanner(json("content" to error, "style" to errorBannerStyle))),
                            fields
                                .filter({x: dynamic -> actualVisibleFieldNames.includes(x.getName())})
                                .map({x: dynamic -> x.render(json())}),

                            jshit.diva(json("style" to json("textAlign" to "left")),
                                jshit.button(json("tamy" to "primary", "shamy" to if (dontShameButtons) undefined else "primary",
                                    "level" to "primary", "title" to primaryButtonTitle, "disabled" to working,
                                    "onClick" to {"__async"
                                        jshit.beginTrain(json("name" to "Submit fucking${if (debugName) " " + debugName else ""} form")); try {
                                            for (field in jsArrayToList(fields)) {
                                                field.setError(undefined)
                                                field.setDisabled(true)
                                            }
                                            error = undefined
                                            working = true
                                            update()

                                            val reb = jshit.RequestBuilder(json())
                                            reb.set(json("key" to "fun", "value" to rpcFun))
                                            for (field in jsArrayToList(fields.filter({ x: dynamic -> actualVisibleFieldNames.includes(x.getName())}))) {
                                                field.contributeToRequest(json("reb" to reb))
                                            }
                                            val res = __await<dynamic>(ui.rpcSoft(reb.toMessage(json())))

                                            if (res.error) {
                                                error = res.error
                                                __await<dynamic>(jshit.utils.fova(onError, res))
                                            } else {
                                                error = undefined
                                                __await<dynamic>(onSuccess(res))
                                            }

                                            working = false
                                            for (field in jsArrayToList(fields)) {
                                                field.setError(res.fieldErrors && res.fieldErrors[field.getName()])
                                                field.setDisabled(false)
                                            }
                                            update()
                                        } finally { jshit.endTrain() }
                                    })),
                                cancelButtonTitle && jshit.button(json("tamy" to "cancel", "shamy" to if (dontShameButtons) undefined else "cancel",
                                    "title" to cancelButtonTitle, "disabled" to working, "style" to json("marginLeft" to 10), "onClick" to onCancel)),
                                working && formTicker())
                        )))
                },

                "componentDidMount" to {
                    if (focusedField) {
                        focusedField.focus()
                    }
                },

                "componentDidUpdate" to {
                    if (focusedField) {
                        focusedField.focus()
                    }
                }
            )

            for (field in jsArrayToList(fields)) {
                field.form = me
            }

            return@statefulElementCtor me
        }))
    }

    return ::jsFacing_Form
}

//fun jsFacing_melinda(def: dynamic): Promise<Unit> {"__async"
//    val ui: dynamic = def.ui
//    val trainName: dynamic = def.trainName; val urlPath: dynamic = def.urlPath; val urlEntityParamName: dynamic = def.urlEntityParamName;
//    val tabDefs: dynamic = def.tabDefs; val defaultActiveTab: dynamic = def.defaultActiveTab; val header: dynamic = def.header;
//    val entityID: dynamic = def.entityID;val entityFun: dynamic = def.entityFun; val itemsFun: dynamic = def.itemsFun;
//    val emptyMessage: dynamic = def.emptyMessage; val plusFormDef: dynamic = def.plusFormDef; val editFormDef: dynamic = def.editFormDef;
//    val aboveItems: dynamic = def.aboveItems; val renderItem: dynamic = def.renderItem; val hasFilterSelect: dynamic = def.hasFilterSelect;
//    val filterSelectValues: dynamic = def.filterSelectValues; val defaultFilter: dynamic = def.defaultFilter
//
//    val plusIcon: dynamic = if (def.plusIcon != undefined) def.plusIcon else "plus"
//    val defaultOrdering: dynamic = if (def.defaultOrdering != undefined) def.defaultOrdering else "desc"
//    val hasSearchBox: dynamic = if (def.hasSearchBox != undefined) def.hasSearchBox else true
//    val hasOrderingSelect: dynamic = if (def.hasOrderingSelect != undefined) def.hasOrderingSelect else true
//    val hasHeaderControls: dynamic = if (def.hasHeaderControls != undefined) def.hasHeaderControls else true
//
//    if (trainName) jshit.beginTrain(json("name" to trainName)); try {
//        var items: dynamic = undefined; var showEmptyLabel = true; var headerControlsVisible = true
//        var headerControlsClass: dynamic = undefined; var headerControlsDisabled = false
//        var cancelForm: dynamic = undefined; var plusShit: dynamic = undefined; var editShit: dynamic = undefined
//        var updateHeaderControls: dynamic = undefined
//        var filterSelect: dynamic = undefined; var orderingSelect: dynamic = undefined
//        var searchBox: dynamic = undefined; var searchBoxInput: dynamic = undefined
//
//
//        fun setHeaderControlsDisappearing() {
//            headerControlsVisible = false
//            headerControlsClass = undefined
//        }
//
//        fun setHeaderControlsAppearing() {
//            headerControlsVisible = true
//            headerControlsClass = "aniFadeIn"
//            timeoutSet(500) {
//                headerControlsClass = undefined
//                ui.updatePage()
//            }
//        }
//
//        fun setHeaderControlsDisabled(b: Boolean) {
//            headerControlsDisabled = b
//            updateHeaderControls()
//        }
//
//        fun showBadResponse(res: dynamic) {
//            return ui.setPage(json(
//                "header" to jshit.pageHeader(json("title" to t("TOTE", "Облом"))),
//                "body" to jshit.diva(js("({})"), jshit.errorBanner(json("content" to res.error)))))
//        }
//
//        fun applyHeaderControls(arg: dynamic): Promise<dynamic> {"__async"
//            // {controlToBlink}
//            val controlToBlink = arg.controlToBlink
//
//            setHeaderControlsDisabled(true)
//            controlToBlink.setBlinking(true)
//
//            val urlParamParts = js("[]")
//
//            if (urlEntityParamName) {
//                urlParamParts.push("${urlEntityParamName}=${entityID}")
//            }
//
//            urlParamParts.push("filter=${filterSelect.getValue()}")
//            urlParamParts.push("ordering=${orderingSelect.getValue()}")
//
//            val searchString = searchBoxInput.getValue().trim()
//            if (searchString) {
//                urlParamParts.push("search=${global.encodeURIComponent(searchString)}")
//            }
//
//            val url = "${urlPath}?${urlParamParts.join("&")}"
//            __await<dynamic>(ui.pushNavigate(url))
//
//            setHeaderControlsDisabled(false)
//            controlToBlink.setBlinking(false)
//
//            return js("undefined") // Dummy
//        }
//
//        fun makeButtonFormShit(def: dynamic): dynamic {
//            // #extract {name, level, icon, formDef} from def
//            val name = def.name; val level = def.level; val icon = def.icon; val formDef = def.formDef
//
//            var form: dynamic = undefined; var formClass: dynamic = undefined
//
//            return json(
//                "button" to {
//                    jshit.button(json(
//                        "tamyShamy" to name, "style" to json("marginLeft" to 0), "level" to level, "icon" to icon, "disabled" to headerControlsDisabled,
//                        "onClick" to {
//                            showEmptyLabel = false
//                            setHeaderControlsDisappearing()
//                            formClass = "aniFadeIn"
//
//                            cancelForm = {
//                                setHeaderControlsAppearing()
//                                form = undefined
//                                ui.updatePage()
//                            }
//
//                            form = ui.Form(global.Object.assign(formDef, json(
//                                "onCancel" to cancelForm)))
//
//                            ui.updatePage()
//                        }
//                    ))
//                },
//
//                "form" to {
//                    if (form) jshit.diva(json("className" to formClass, "style" to json("marginBottom" to 15)), form)
//                    else undefined
//                }
//            )
//        }
//
//        var entityRes: dynamic = undefined
//        if (entityFun) {
//            entityRes = __await(ui.rpcSoft(json("fun" to entityFun, "entityID" to entityID)))
//            if (entityRes.error) return __asyncResult(showBadResponse(entityRes))
//        }
//
//        val searchString = ui.urlQuery.search
//
//        var filter: dynamic = undefined
//        if (hasFilterSelect) {
//            filter = ui.urlQuery.filter
//            val saneFilters = filterSelectValues.map({x: dynamic -> x.value})
//            if (!saneFilters.includes(filter)) filter = defaultFilter
//        }
//
//        var ordering = ui.urlQuery.ordering
//        if (!js("['asc', 'desc']").includes(ordering)) ordering = defaultOrdering
//
//        var tabs: dynamic = undefined; var activeTab: dynamic = undefined
//        if (tabDefs) {
//            activeTab = ui.urlQuery.tab || defaultActiveTab
//            tabs = ui.tabs(json("name" to "main", "active" to activeTab, "tabDefs" to tabDefs))
//        }
//
//        val itemsReq = json(
//            "fun" to jshit.utils.fov(itemsFun, json("activeTab" to activeTab)),
//            "entityID" to entityID, "filter" to filter, "ordering" to ordering, "searchString" to searchString)
//        val itemsRes = __await<dynamic>(ui.rpcSoft(itemsReq))
//        if (itemsRes.error) return __asyncResult(showBadResponse(itemsRes))
//
//        if (hasSearchBox) {
//            searchBoxInput = jshit.Input(json(
//                "tamyShamy" to "search",
//                "style" to json("paddingLeft" to 30, "width" to 160),
//                "placeholder" to t("TOTE", "Поиск..."),
//                "disabled" to { headerControlsDisabled }, // Yeah, I mean closure here
//                // TODO:vgrechka Check if async below is enhanced correctly
//                "onKeyDown" to {e: dynamic -> "__async"
//                    if (e.keyCode == 13) {
//                        preventAndStop(e)
//                        __await(applyHeaderControls(json("controlToBlink" to searchBoxInput)))
//                    }
//                }
//            ))
//            searchBoxInput.setValueExt(json("value" to itemsRes.actualSearchString, "notify" to false))
//
//            searchBox = jshit.diva(json("style" to json("position" to "relative")),
//                searchBoxInput,
//                jshit.faIcon(json("icon" to "search", "style" to json("position" to "absolute", "left" to 10, "top" to 10, "color" to Color.GRAY_500)))
//            )
//        }
//
//        if (hasFilterSelect) {
//            filterSelect = Select(json(
//                "tamyShamy" to "filter", "isAction" to true, "style" to json("width" to 160),
//                "values" to filterSelectValues,
//                "initialValue" to filter,
//                "disabled" to { headerControlsDisabled }, // Yeah, I mean closure here
//                "onChange" to {"__async"
//                    __await(applyHeaderControls(json("controlToBlink" to filterSelect)))
//                }
//            ))
//
//            filterSelect.setValueExt(json("value" to itemsRes.actualFilter, "notify" to false))
//        }
//
//        if (hasOrderingSelect) {
//            orderingSelect = Select(json(
//                "tamyShamy" to "ordering", "isAction" to true, "style" to json("width" to 160),
//                "values" to jsArrayOf(
//                    json("value" to "desc", "title" to t("TOTE", "Сначала новые")),
//                    json("value" to "asc", "title" to t("TOTE", "Сначала старые"))),
//                "initialValue" to ordering,
//                "disabled" to { headerControlsDisabled }, // Yeah, I mean closure here
//                "onChange" to {"__async"
//                    __await(applyHeaderControls(json("controlToBlink" to orderingSelect)))
//                }
//            ))
//
//            orderingSelect.setValueExt(json("value" to itemsRes.actualOrdering, "notify" to false))
//        }
//
//        if (plusFormDef) {
//            plusShit = makeButtonFormShit(json("name" to "plus", "level" to "primary", "icon" to plusIcon, "formDef" to plusFormDef))
//        }
//        if (editFormDef) {
//            editShit = makeButtonFormShit(json("name" to "edit", "level" to "default", "icon" to "edit", "formDef" to editFormDef))
//        }
//
//        ui.setPage(json(
//            "header" to jshit.utils.fov(header, entityRes),
//
//            "body" to {
//                jshit.diva(json("style" to json("marginBottom" to 15)),
//                    tabs,
//                    if (editShit) editShit.form else null,
//                    if (plusShit) plusShit.form else null,
//                    jshit.utils.fov(aboveItems, entityRes),
//                    run { // Render items
//                        if (itemsRes.items.length)
//                            ui.renderMoreable(json("itemsRes" to itemsRes, "itemsReq" to itemsReq, "renderItem" to renderItem))
//                        else
//                            if (showEmptyLabel)
//                                jshit.diva(json("style" to json("marginTop" to 10)),
//                                    emptyMessage || jshit.spanc(json("tame" to "nothingLabel", "content" to t("TOTE", "Савсэм ничего нэт, да..."))))
//                            else ""
//                    }
//                )
//            },
//
//            "headerControls" to {
//                jshit.updatableElement(js("({})"), { update: dynamic ->
//                    updateHeaderControls = update
//                    { // Yeah, returning closure
//                        if (!jshit.utils.fov(hasHeaderControls, entityRes) || !headerControlsVisible) undefined
//                        else {
//                            jshit.hor2(json(
//                                "style" to json("display" to "flex", "marginTop" to if (tabDefs) 55 else 0),
//                                "className" to headerControlsClass),
//
//                                searchBox,
//                                filterSelect,
//                                orderingSelect,
//                                if (editShit) editShit.button else null,
//                                if (plusShit) plusShit.button else null
//                            )
//                        }
//                    }
//                })
//            },
//
//            "onKeyDown" to {e: dynamic ->
//                if (e.keyCode == 27) {
//                    jshit.utils.fov(cancelForm)
//                }
//            }
//        ))
//    } finally { if (trainName) jshit.endTrain() }
//    return __asyncResult(Unit)
//}

