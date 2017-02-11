/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import aps.const.text.symbols.rightDoubleAngleQuotationSpaced
import into.kommon.*
import org.w3c.dom.events.KeyboardEvent
import kotlin.collections.*
import kotlin.js.json

val reactNull = oldShitAsToReactElementable(null)

enum class HeaderMode {
    BROWSING, CREATING
}

fun usualHeader(title: String): ToReactElementable =
    pageHeader0(title)

class Melinda<Item, Entity, Filter>(
    val ui: World,
    val urlPath: String,
    val procedureName: String,
    val entityProcedureName: String? = null,
    val filterSelectValues: Array<Filter>? = null,
    val defaultFilter: Filter,
    val plusIcon: IconClass = fa.plus,
    val defaultOrdering: Ordering = Ordering.DESC,
    val hasSearchBox: Boolean = true,
    val hasOrderingSelect: Boolean = true,
    val hasHeaderControls: () -> Boolean = {true},
    val urlEntityParamName: String? = null,
    val entityID: String? = null,
    val renderItem: (Int, Item) -> ToReactElementable,
    val emptyMessage: String? = null,
    val tabsSpec: Any? = null,
    val header: (Melinda<Item, Entity, Filter>).() -> String,
    val aboveItems: (Melinda<Item, Entity, Filter>).() -> ToReactElementable = {reactNull}
)
where Entity : Any, Filter : Enum<Filter>, Filter : Titled {

//    typealias Me = Melinda<Item, Entity, Filter>

    lateinit var entity: Entity
    var updateHeaderControls = {}
//    var items: dynamic = undefined
    var plusShit: IButtonAndForm? = null
    var editShit: IButtonAndForm? = null
    var searchBox: ToReactElementable = reactNull
    var searchBoxInput: Input? = null
    var filterSelect: Select<Filter>? = null
    var orderingSelect: Select<Ordering>? = null

    private val ebafHost = EbafHost()
    inner class EbafHost : EvaporatingButtonAndFormHost {
        override var showEmptyLabel = true
        override var cancelForm = {}
        override var headerControlsDisabled = false
        override var headerControlsVisible = true
        override var headerControlsClass = ""
        override var headerMode = HeaderMode.BROWSING

        override fun updateShit() {
            ui.updatePage()
        }
    }

    fun <Req : RequestMatumba, Res> specifyPlus(
        plusFormSpec: FormSpec<Req, Res>,
        onPlusFormSuccessa: suspend (Res) -> Unit = {}
    ) {
        plusShit = EvaporatingButtonAndForm(
            ebafHost,
            key = aps.front.fconst.button.plus,
            level = Button.Level.PRIMARY,
            icon = plusIcon,
            formSpec = plusFormSpec,
            onSuccessa = onPlusFormSuccessa)
    }

    fun <Req : RequestMatumba, Res> specifyEdit(
        editFormSpec: FormSpec<Req, Res>,
        onEditFormSuccessa: suspend (Res) -> Unit = {}
    ) {
        editShit = EvaporatingButtonAndForm(
            ebafHost,
            key = aps.front.fconst.button.edit,
            level = Button.Level.DEFAULT,
            icon = fa.edit,
            formSpec = editFormSpec,
            onSuccessa = onEditFormSuccessa)
    }


    fun setHeaderControlsDisabled(b: Boolean) {
        ebafHost.headerControlsDisabled = b
        updateHeaderControls()
    }


    suspend fun applyHeaderControls(controlToBlink: Blinkable) {
        setHeaderControlsDisabled(true)
        controlToBlink.setBlinking(true)

        val urlParamParts = js("[]")

        if (urlEntityParamName != null) {
            urlParamParts.push("${urlEntityParamName}=${entityID}")
        }

        filterSelect?.let {urlParamParts.push("filter=${it.value}")}
        orderingSelect?.let {urlParamParts.push("ordering=${it.value}")}

        searchBoxInput?.let {
            it.value.let {
                if (it.isNotBlank())
                    urlParamParts.push("search=${global.encodeURIComponent(it)}")
            }
        }

        val url = "${urlPath}?${urlParamParts.join("&")}"
        ui.pushNavigate(url)

        setHeaderControlsDisabled(false)
        controlToBlink.setBlinking(false)
    }

    suspend fun ignita() {
        if (entityProcedureName != null) {
            val entityReq = EntityRequest()
            val res = await(callZimbabwe<EntityResponse<Entity>>(entityProcedureName, entityReq, ui.tokenMaybe))
            entity = when (res) {
                is ZimbabweResponse.Shitty -> return ui.setPage(Page(
                    header = oldShitAsToReactElementable(Shitus.pageHeader(json("title" to t("TOTE", "Облом")))),
                    body = oldShitAsToReactElementable(Shitus.diva(js("({})"), Shitus.errorBanner(json("content" to res.error))))))

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
                "disabled" to { ebafHost.headerControlsDisabled }, // Yeah, I mean closure here
                // TODO:vgrechka Check if async below is enhanced correctly
                "onKeyDown" to {e: KeyboardEvent -> async {
                    if (e.keyCode == 13) {
                        preventAndStop(e)
                        applyHeaderControls(searchBoxInput!!)
                    }
                }}
            ))

            searchBox = oldShitAsToReactElementable(Shitus.diva(json("style" to json("position" to "relative")),
                                                                searchBoxInput!!.toReactElement(),
                                                                faIcon(icon="search", position="absolute", left=10, top=10, color=Color.GRAY_500).toReactElement()
            ))
        }

        if (filterSelectValues != null) {
            filterSelect = Select(
                key = null,
                attrs = Attrs(tamyShamy = "filter"),
                values = filterSelectValues,
                initialValue = filter,
                isAction = true,
                style = json("width" to 160),
                volatileDisabled = {ebafHost.headerControlsDisabled},
                onChanga = {async{
                    applyHeaderControls(filterSelect!!)
                }}
            )


        }

        if (hasOrderingSelect) {
            orderingSelect = Select(
                key = null,
                attrs = Attrs(tamyShamy = "ordering"),
                values = Ordering.values(),
                initialValue = ordering,
                isAction = true,
                style = json("width" to 160),
                volatileDisabled = {ebafHost.headerControlsDisabled},
                onChanga = {async{
                    applyHeaderControls(orderingSelect!!)
                }}
            )

        }


        val itemsReq = ItemsRequest(filterSelectValues!!)
        filterSelect?.let {itemsReq.filter.value = it.value}
        orderingSelect?.let {itemsReq.ordering.value = it.value}
        val res = await(callZimbabwe<ItemsResponse<Item>>(procedureName, itemsReq, ui.tokenMaybe))
        val itemsRes = when (res) {
            is ZimbabweResponse.Shitty -> return ui.setPage(Page(
                header = oldShitAsToReactElementable(Shitus.pageHeader(json("title" to t("TOTE", "Облом")))),
                body = oldShitAsToReactElementable(Shitus.diva(js("({})"), Shitus.errorBanner(json("content" to res.error))))))

            is ZimbabweResponse.Hunky -> res.meat
        }


        return ui.setPage(Page(
            header = ToReactElementable.volatile {
                usualHeader(header() + when (ebafHost.headerMode) {
                HeaderMode.BROWSING -> ""
                HeaderMode.CREATING -> rightDoubleAngleQuotationSpaced + t("New", "Новый")
            })},
            body = object:ToReactElementable {
                override fun toReactElement(): ReactElement {
                    return Shitus.diva(json("style" to json("marginBottom" to 15)),
                                       tabs,
                                       editShit?.let {it.renderForm()},
                                       plusShit?.let {it.renderForm()},
                                       aboveItems().toReactElement(),

                                       run { // Render items
                                           if (itemsRes.items.isNotEmpty())
                                               renderMoreable(ui, itemsRes, itemsReq, renderItem)
                                           else
                                               if (ebafHost.showEmptyLabel) {
                                                   Shitus.diva(json("style" to json("marginTop" to 10)),
                                                               emptyMessage?.let{it} ?: Shitus.spanc(json("tame" to "nothingLabel", "content" to const.msg.noItems)))
                                               }
                                               else {
                                                   ""
                                               }
                                       }
                    )
                }
            },

            headerControls = object:ToReactElementable {
                override fun toReactElement(): ReactElement {
                    return Shitus.updatableElement(js("({})"), { update: dynamic ->
                        updateHeaderControls = update
                        render@{
                            if (!hasHeaderControls() || !ebafHost.headerControlsVisible) return@render null
                            Shitus.hor2(json(
                                "style" to json("display" to "flex", "marginTop" to if (tabsSpec != null) 55 else 0),
                                "className" to ebafHost.headerControlsClass),

                                        searchBox.toReactElement(),
                                        filterSelect?.toReactElement(),
                                        orderingSelect?.toReactElement(),
                                        editShit?.let {it.renderButton()},
                                        plusShit?.let {it.renderButton()}
                            )
                        }
                    })
                }
            },

            onKeyDown = {e: ReactEvent ->
                if (e.keyCode == 27) {
                    ebafHost.cancelForm()
                }
            }
        ))
    }

}


fun getURLQueryParam(ui: World, name: String): String? {
    return ui.urlQuery[name]
}

fun <Item, Filter>
renderMoreable(ui: World,
               itemsRes: ItemsResponse<Item>,
               itemsReq: ItemsRequest<Filter>,
               renderItem: (Int, Item) -> ToReactElementable,
               chunkName: String = "chunk",
               chunkIndex: Int = 0,
               style: dynamic = undefined)
where Filter : Enum<Filter>, Filter : Titled {
    var bottom: dynamic = undefined

    if (itemsRes.moreFromID != null) {
        bottom = Shitus.updatableElement(json(), updatableElementCtor@{update: dynamic ->
            val moreButtonID = puid()
            var thing: dynamic = undefined
            thing = Shitus.diva(json("style" to style), Shitus.button(json(
                "id" to moreButtonID,
                "tamyShamy" to "showMore",
                "title" to t("Show More", "Показать еще"),
                "style" to json("background" to Color.BLUE_GRAY_50, "width" to "100%", "marginTop" to 15),
                "onClick" to onClick@{async{
                    await(effects).blinkOn(Shitus.byid(moreButtonID), BlinkOpts(dtop = -16))
                    // testGlobal['button_showMore_blinks'] = true

                    imf("renderMoreable")
//                    val moreRes = __await<dynamic>(jshit.ui.rpcSoft(global.Object.assign(itemsReq, json("fromID" to itemsRes.moreFromID))))
//
//                    if (moreRes.error) { console.error(moreRes.error); return@onClick }
//                    // TODO:vgrechka Handle RPC error in Show More button    408e3096-aab1-42f5-9209-a9b35e7b5800
//                    effects.blinkOff()
//                    // testGlobal['button_showMore_blinks'] = false
//
//                    thing = jshit.ui.renderMoreable(json("itemsRes" to moreRes, "itemsReq" to itemsReq, "renderItem" to renderItem, "chunkName" to chunkName, "chunkIndex" to chunkIndex + 1))

                    update()
                }}
            )))

            return@updatableElementCtor {thing}
        })
    }

    return Shitus.diva(json("controlTypeName" to "renderMoreable"),
        if (itemsRes.items.isEmpty())
            Shitus.diva(json("style" to json("marginTop" to 10)), t("TOTE", "Здесь ничего нет, такие дела..."))
        else {
            Shitus.diva.apply(null, js("[]").concat(
                json("tame" to "${chunkName}${Shitus.sufindex(chunkIndex)}"),
                itemsRes.items.mapIndexed { index, item -> renderItem(index, item).toReactElement() }.toJSArray()))
        },
        bottom
    )
}


fun <E: Enum<E>> stringToEnum(s: String?, values: Array<E>): E {
    return values.find {it.name == s} ?: wtf("stringToEnum: s = [$s]; values = [${values.joinToString{it.name}}]")
}




















