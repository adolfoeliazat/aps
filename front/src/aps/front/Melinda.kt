/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import kotlin.collections.*

val reactNull = oldShitAsReactElementable(null)

class Melinda<Item, Entity, Filter>(
    val ui: LegacyUIShit,
    val urlPath: String,
    val procedureName: String,
    val entityProcedureName: String? = null,
    val filterSelectValues: Array<Filter>? = null,
    val defaultFilter: Filter,
    val plusIcon: String = "plus",
    val defaultOrdering: Ordering = Ordering.DESC,
    val hasSearchBox: Boolean = true,
    val hasOrderingSelect: Boolean = true,
    val hasHeaderControls: () -> Boolean = {true},
    val urlEntityParamName: String? = null,
    val entityID: String? = null,
    val plusFormSpec: FormSpec<*, *>? = null,
    val editFormSpec: FormSpec<*, *>? = null,
    val renderItem: (Int, Item) -> ToReactElementable,
    val emptyMessage: String? = null,
    val tabsSpec: Any? = null,
    val header: (Me).() -> ToReactElementable,
    val aboveItems: (Me).() -> ToReactElementable = {reactNull})
where Entity : Any, Filter : Enum<Filter>, Filter : Titled {

    typealias Me = Melinda<Item, Entity, Filter>

    lateinit var entity: Entity
    var headerControlsDisabled = false
    var showEmptyLabel = true
    var headerControlsVisible = true
    var headerControlsClass = ""
    var updateHeaderControls = {}
    var cancelForm = {}
//    var items: dynamic = undefined
    var plusShit: ButtonAndForm? = null
    var editShit: ButtonAndForm? = null
    var searchBox: ToReactElementable = reactNull
    var searchBoxInput: Input? = null
    var filterSelect: Select<Filter>? = null
    var orderingSelect: Select<Ordering>? = null

    inner class ButtonAndForm(val name: String, val level: String, val icon: String, val formSpec: FormSpec<*, *>) {
        private var form: ReactElement? = null
        private var formClass = ""

        fun button() = jshit.button(json(
            "tamyShamy" to name, "style" to json("marginLeft" to 0), "level" to level, "icon" to icon, "disabled" to headerControlsDisabled,
            "onClick" to {
                showEmptyLabel = false
                setHeaderControlsDisappearing()
                formClass = "aniFadeIn"

                cancelForm = {
                    setHeaderControlsAppearing()
                    form = null
                    ui.updatePage()
                }

                form = FormMatumba(formSpec.copy(onCancel = {cancelForm()})).toReactElement()

                ui.updatePage()
            }
        ))

        fun form() = form?.let {
            jshit.diva(json("className" to formClass, "style" to json("marginBottom" to 15)), it)
        }
    }

    fun setHeaderControlsDisappearing() {
        headerControlsVisible = false
        headerControlsClass = ""
    }

    fun setHeaderControlsAppearing() {
        headerControlsVisible = true
        headerControlsClass = "aniFadeIn"
        timeoutSet(500) {
            headerControlsClass = ""
            ui.updatePage()
        }
    }

    fun setHeaderControlsDisabled(b: Boolean) {
        headerControlsDisabled = b
        updateHeaderControls()
    }


    fun applyHeaderControls(controlToBlink: Blinkable): Promise<dynamic> {"__async"
        setHeaderControlsDisabled(true)
        controlToBlink.setBlinking(true)

        val urlParamParts = js("[]")

        if (urlEntityParamName != null) {
            urlParamParts.push("${urlEntityParamName}=${entityID}")
        }

        filterSelect?.let {urlParamParts.push("filter=${it.getValue()}")}
        orderingSelect?.let {urlParamParts.push("ordering=${it.getValue()}")}

        searchBoxInput?.let {
            it.value.let {
                if (it.isNotBlank())
                    urlParamParts.push("search=${global.encodeURIComponent(it)}")
            }
        }

        val url = "${urlPath}?${urlParamParts.join("&")}"
        __await<dynamic>(ui.pushNavigate(url))

        setHeaderControlsDisabled(false)
        controlToBlink.setBlinking(false)

        return js("undefined") // Dummy
    }

    fun ignita(): Promise<Unit> {"__async"
        if (entityProcedureName != null) {
            val entityReq = EntityRequest()
            val res = __await(callZimbabwe<EntityResponse<Entity>>(entityProcedureName, entityReq, ui.token))
            entity = when (res) {
                is ZimbabweResponse.Shitty -> return ignora/ ui.setPage(json(
                    "header" to jshit.pageHeader(json("title" to t("TOTE", "Облом"))),
                    "body" to jshit.diva(js("({})"), jshit.errorBanner(json("content" to res.error)))))

                is ZimbabweResponse.Hunky -> res.meat.entity
            }
        }

        val searchString = getURLQueryParam(ui, "search")

        val filter = if (filterSelectValues == null) null else {
            val q = getURLQueryParam(ui, "filter")
            if (q == null) defaultFilter else stringToEnum(q, filterSelectValues)
        }
        val ordering = if (!hasOrderingSelect) null else {
            val q = getURLQueryParam(ui, "ordering")
            if (q == null) defaultOrdering else stringToEnum(q, Ordering.values())
        }

        if (tabsSpec != null) {
            imf("melinda: tabsSpec")
        }
        val tabs: Any? = null

        if (hasSearchBox) {
            searchBoxInput = Input(json(
                "tamyShamy" to "search",
                "style" to json("paddingLeft" to 30, "width" to 160),
                "placeholder" to t("TOTE", "Поиск..."),
                "disabled" to { headerControlsDisabled }, // Yeah, I mean closure here
                // TODO:vgrechka Check if async below is enhanced correctly
                "onKeyDown" to {e: dynamic -> "__async"
                    if (e.keyCode == 13) {
                        preventAndStop(e)
                        __await(applyHeaderControls(searchBoxInput!!))
                    }
                }
            ))

            searchBox = oldShitAsReactElementable(jshit.diva(json("style" to json("position" to "relative")),
                searchBoxInput!!.toReactElement(),
                jshit.faIcon(json("icon" to "search", "style" to json("position" to "absolute", "left" to 10, "top" to 10, "color" to Color.GRAY_500)))
            ))
        }

        if (filterSelectValues != null) {
            filterSelect = Select(filterSelectValues, filter,
                tamyShamy = "filter",
                isAction = true,
                style = json("width" to 160),
                volatileDisabled = {headerControlsDisabled},
                onChanga = {"__async"
                    __await(applyHeaderControls(filterSelect!!))
                }
            )

        }

        if (hasOrderingSelect) {
            orderingSelect = Select(Ordering.values(), ordering,
                tamyShamy = "ordering",
                isAction = true,
                style = json("width" to 160),
                volatileDisabled = {headerControlsDisabled},
                onChanga = {"__async"
                    __await(applyHeaderControls(orderingSelect!!))
                }
            )

        }

        if (plusFormSpec != null) {
            plusShit = ButtonAndForm(name = "plus", level = "primary", icon = plusIcon, formSpec = plusFormSpec)
        }
        if (editFormSpec != null) {
            editShit = ButtonAndForm(name = "edit", level = "default", icon = "edit", formSpec = editFormSpec)
        }

        val itemsReq = ItemsRequest(filterSelectValues!!)
        filterSelect?.let {itemsReq.filter.value = it.value}
        orderingSelect?.let {itemsReq.ordering.value = it.value}
        val res = __await(callZimbabwe<ItemsResponse<Item>>(procedureName, itemsReq, ui.token))
        val itemsRes = when (res) {
            is ZimbabweResponse.Shitty -> return ignora/ ui.setPage(json(
                "header" to jshit.pageHeader(json("title" to t("TOTE", "Облом"))),
                "body" to jshit.diva(js("({})"), jshit.errorBanner(json("content" to res.error)))))

            is ZimbabweResponse.Hunky -> res.meat
        }


        return ignora/ ui.setPage(json(
            "header" to header().toReactElement(),

            "body" to {jshit.diva(json("style" to json("marginBottom" to 15)),
                tabs,
                editShit?.let {it.form()},
                plusShit?.let {it.form()},
                aboveItems().toReactElement(),

                run { // Render items
                    if (itemsRes.items.isNotEmpty())
                        renderMoreable(ui, itemsRes, itemsReq, renderItem)
                    else
                        if (showEmptyLabel)
                            jshit.diva(json("style" to json("marginTop" to 10)),
                                emptyMessage?.let{it} ?: jshit.spanc(json("tame" to "nothingLabel", "content" to t("TOTE", "Савсэм ничего нэт, да..."))))
                        else ""
                }
            )},

            "headerControls" to {jshit.updatableElement(js("({})"), {update: dynamic ->
                updateHeaderControls = update
                render@{
                    if (!hasHeaderControls() || !headerControlsVisible) return@render null
                    jshit.hor2(json(
                        "style" to json("display" to "flex", "marginTop" to if (tabsSpec != null) 55 else 0),
                        "className" to headerControlsClass),

                        searchBox.toReactElement(),
                        filterSelect?.toReactElement(),
                        orderingSelect?.toReactElement(),
                        editShit?.let {it.button()},
                        plusShit?.let {it.button()}
                    )
                }
            })},

            "onKeyDown" to {e: dynamic ->
                if (e.keyCode == 27) {
                    cancelForm()
                }
            }
        ))
    }

}


fun getURLQueryParam(ui: LegacyUIShit, name: String): String? {
    val dy: dynamic = ui.urlQuery
    return dy[name]
}

fun <Item, Filter>
renderMoreable(ui: LegacyUIShit,
               itemsRes: ItemsResponse<Item>,
               itemsReq: ItemsRequest<Filter>,
               renderItem: (Int, Item) -> ToReactElementable,
               chunkName: String = "chunk",
               chunkIndex: Int = 0,
               style: dynamic = undefined)
where Filter : Enum<Filter>, Filter : Titled {
    var bottom: dynamic = undefined

    if (itemsRes.moreFromID != null) {
        bottom = jshit.updatableElement(json(), updatableElementCtor@{update: dynamic ->
            val moreButtonID = puid()
            var thing: dynamic = undefined
            thing = jshit.diva(json("style" to style), jshit.button(json(
                "id" to moreButtonID,
                "tamyShamy" to "showMore",
                "title" to t("Show More", "Показать еще"),
                "style" to json("background" to Color.BLUE_GRAY_50, "width" to "100%", "marginTop" to 15),
                "onClick" to onClick@{"__async"
                    jshit.effects.blinkOn(json("target" to jshit.byid(moreButtonID), "dtop" to -16))
                    // testGlobal['button_showMore_blinks'] = true

                    val moreRes = __await<dynamic>(jshit.ui.rpcSoft(global.Object.assign(itemsReq, json("fromID" to itemsRes.moreFromID))))

                    if (moreRes.error) { console.error(moreRes.error); return@onClick }
                    // TODO:vgrechka Handle RPC error in Show More button    408e3096-aab1-42f5-9209-a9b35e7b5800
                    jshit.effects.blinkOff()
                    // testGlobal['button_showMore_blinks'] = false

                    thing = jshit.ui.renderMoreable(json("itemsRes" to moreRes, "itemsReq" to itemsReq, "renderItem" to renderItem, "chunkName" to chunkName, "chunkIndex" to chunkIndex + 1))

                    update()
                }
            )))

            return@updatableElementCtor {thing}
        })
    }

    return jshit.diva(json("controlTypeName" to "renderMoreable"),
        if (itemsRes.items.isEmpty())
            jshit.diva(json("style" to json("marginTop" to 10)), t("TOTE", "Здесь ничего нет, такие дела..."))
        else {
            jshit.diva.apply(null, js("[]").concat(
                json("tame" to "${chunkName}${jshit.sufindex(chunkIndex)}"),
                itemsRes.items.mapIndexed { index, item -> renderItem(index, item).toReactElement() }.toJSArray()))
        },
        bottom
    )
}


fun <E: Enum<E>> stringToEnum(s: String?, values: Array<E>): E {
    return values.find {it.name == s} ?: wtf("stringToEnum: s = [$s]; values = [${values.joinToString{it.name}}]")
}





















