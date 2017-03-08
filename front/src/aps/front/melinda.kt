package aps.front

import aps.*
import aps.const.text.numberSign
import into.kommon.*
import org.w3c.dom.Element
import org.w3c.dom.events.MouseEvent
import kotlin.js.json

class MelindaVaginalUpdateParams<ItemRTO, out UpdateItemRequest, in UpdateItemResponse>(
    val makeUpdateItemRequest: (item: ItemRTO) -> UpdateItemRequest,
    val updateItemProcedureNameIfNotDefault: String?,
    val getItemFromUpdateItemResponse: (res: UpdateItemResponse) -> ItemRTO
) where
    UpdateItemRequest : RequestMatumba,
    UpdateItemResponse : CommonResponseFields

class MelindaVagina<
    ItemRTO,
    Filter,
    out UpdateItemRequest,
    in UpdateItemResponse>
(
    val sendItemsRequest: suspend (req: ItemsRequest<Filter>) -> FormResponse2<ItemsResponse<ItemRTO>>,
    val shouldShowFilter: () -> Boolean,
    val getParentEntityID: () -> Long?,
    val humanItemTypeName: String,
    val makeDeleteItemRequest: () -> DeleteRequest,
    val makeLipsInterface: (viewRootID: String, tongue: MelindaTongueInterface<ItemRTO>) -> MelindaLipsInterface,
    val updateParams: MelindaVaginalUpdateParams<ItemRTO, UpdateItemRequest, UpdateItemResponse>?
)
where
    Filter : Enum<Filter>,
    Filter : Titled,
    UpdateItemRequest : RequestMatumba,
    UpdateItemResponse : CommonResponseFields

interface MelindaBoobsInterface {
    val controlsContent: ToReactElementable
    val mainContent: ToReactElementable
    fun getSearchString(): String
}

interface MelindaLipsInterface {
    fun renderItem(): ToReactElementable
}

interface MelindaTongueInterface<out Item> {
    suspend fun onEdit()
    suspend fun onDelete()
    val items: List<Item>
    val itemIndex: Int
}

val <Item> MelindaTongueInterface<Item>.item get() = items[itemIndex]
fun <Item> MelindaTongueInterface<Item>.toItemSupplier() = {item}

class MelindaCreateParams<CreateRequest, CreateResponse>(
    val hasCreateButton: Boolean,
    val createModalTitle: String,
    val makeCreateRequest: () -> CreateRequest,
    val createProcedureNameIfNotDefault: String? = null,
    val makeURLAfterCreation: (CreateResponse) -> String
) where
    CreateRequest : RequestMatumba,
    CreateResponse : CommonResponseFields


class MelindaBoobs<
    Item,
    Filter,
    CreateRequest, CreateResponse,
    UpdateItemRequest, UpdateItemResponse>
(
    val createParams: MelindaCreateParams<CreateRequest, CreateResponse>?,
    val makeURLForReload: (boobsParams: List<URLParamValue<*>>) -> String,
    val filterValues: Array<Filter>,
    val defaultFilterValue: Filter,
    val filterSelectKey: EnumSelectKey<Filter>,
    val hasRefreshButton: Boolean = false,
    val renderStripStuff: (ElementBuilder) -> Unit = {},
    val vaginalInterface: MelindaVagina<
        Item,
        Filter,
        UpdateItemRequest, UpdateItemResponse>
) where
    Filter: Enum<Filter>, Filter: Titled,
    Item : MelindaItemRTO,
    CreateRequest : RequestMatumba, CreateResponse : CommonResponseFields,
    UpdateItemRequest : RequestMatumba, UpdateItemResponse : CommonResponseFields
{
    val urlQuery = _URLQuery()
    inner class _URLQuery : URLQueryParamsMarker {
        val ordering by EnumURLParam(Ordering.values(), default = Ordering.DESC)
        val filter by EnumURLParam(filterValues, default = defaultFilterValue)
        val search by StringURLParam("")
    }

    private var headerControlsDisabled = false
    private var controlsContent by notNullOnce<Control2>()
    private var mainContent by notNullOnce<ToReactElementable>()
    private var tongues = mutableListOf<MelindaTongue>()

    fun makeStripContent() {
        controlsContent = object:Control2(Attrs()) {
            override fun render(): ToReactElementable {
                return hor2{o->
                    o- kdiv(position = "relative"){o->
                        o- searchInput
                        o- ki(className = "${fa.search}", position = "absolute", left = 10, top = 10, color = Color.GRAY_500)
                    }
                    if (vaginalInterface.shouldShowFilter()) {
                        o- filterSelect
                    }
                    o- orderingSelect

                    if (hasRefreshButton) {
                        val refreshButtonID = puid()
                        o- Button(id = refreshButtonID, icon = fa.refresh, volatileDisabled = {headerControlsDisabled}, key = buttons.refreshPage) {
                            asu {reload(refreshButtonID)}
                        }
                    }

                    if (createParams != null) {
                        o- Button(icon = fa.plus, level = Button.Level.PRIMARY, key = buttons.plus) {
                            openEditModal(
                                title = createParams.createModalTitle,
                                formSpec = FormSpec<CreateRequest, CreateResponse>(
                                    procedureName = createParams.createProcedureNameIfNotDefault,
                                    req = createParams.makeCreateRequest()
                                ),
                                onSuccessa = {res->
                                    Globus.world.replaceNavigate(createParams.makeURLAfterCreation(res))
                                }
                            )
                        }
                    }

                    renderStripStuff(o)
                }
            }
        }
    }

    val boobsInterface = object:MelindaBoobsInterface {
        override val mainContent get()= this@MelindaBoobs.mainContent
        override val controlsContent get()= this@MelindaBoobs.controlsContent
        override fun getSearchString() = urlQuery.search.get()
    }

    val filterSelect = EnumSelect(
        key = filterSelectKey,
        values = filterValues,
        initialValue = urlQuery.filter.get(),
        isAction = true,
        style = json("width" to 160),
        volatileDisabled = {headerControlsDisabled}
    )

    val orderingSelect = EnumSelect(
        key = selects.ordering,
        values = Ordering.values(),
        initialValue = urlQuery.ordering.get(),
        isAction = true,
        style = json("width" to 160),
        volatileDisabled = {headerControlsDisabled}
    )

    val searchInput = Input(
        key = inputs.search,
        style = Style(paddingLeft = 30, width = "100%"),
        placeholder = t("Search...", "Поиск..."),
        volatileDisabled  = {headerControlsDisabled}
    )

    init {
        searchInput.setValue(urlQuery.search.get())

        filterSelect.onChanga = {reload(filterSelect.elementID)}
        orderingSelect.onChanga = {reload(orderingSelect.elementID)}
        searchInput.onKeyDowna = {e->
            if (e.keyCode == 13) {
                preventAndStop(e)
                asu {reload(searchInput.elementID)}
            }
        }
    }

    suspend fun reload(elementID: String) {
        await(effects).blinkOn(byid(elementID))
        headerControlsDisabled = true
        controlsContent.update()
        TestGlobal.shitHalfwayLock.resumeTestAndPauseSutFromSut()
        try {
            val paramValuesFromVagina = listOf(
                URLParamValue(urlQuery.ordering, orderingSelect.value),
                URLParamValue(urlQuery.filter, filterSelect.value),
                URLParamValue(urlQuery.search, encodeURIComponent(searchInput.getValue()))
            )
            Globus.world.pushNavigate(makeURLForReload(paramValuesFromVagina))
        } finally {
            headerControlsDisabled = false    // TODO:vgrechka Redundant?
            controlsContent.update()                          // TODO:vgrechka Redundant?

            TestGlobal.shitDoneLock.resumeTestFromSut()
        }
    }

    private suspend fun requestChunk(fromID: Long?): FormResponse2<ItemsResponse<Item>> {
        val res = vaginalInterface.sendItemsRequest(ItemsRequest(filterValues)-{o->
            o.parentEntityID.value = vaginalInterface.getParentEntityID()
            o.filter.value = urlQuery.filter.get()
            o.ordering.value = urlQuery.ordering.get()
            o.searchString.value = urlQuery.search.get()
            o.fromID.value = fromID
        })
        return res
    }

    suspend fun load(): FormResponse2.Shitty<*>? {
        val res = requestChunk(null)
        return when (res) {
            is FormResponse2.Shitty -> res
            is FormResponse2.Hunky -> {
                val meat = res.meat
                tongues.addAll(meat.items.map {MelindaTongue(it)})

                makeStripContent()

                val itemsControl = makeItemsControl(startIndex = 0, moreFromID = meat.moreFromID)
                mainContent = ToReactElementable.from {kdiv{o->
                    o- itemsControl
                }}

                null
            }
        }
    }

    private fun makeItemsControl(startIndex: Int, moreFromID: Long?, containerID: String? = null): ToReactElementable {
        if (tongues.size <= startIndex) return when {
            startIndex == 0 -> span(const.msg.noItems)
            else -> NOTRE
        }

        return kdiv(id = containerID){o->
            o- kdiv(className = css.lipsItemContainer){o->
                for (i in startIndex..tongues.lastIndex)
                    o- tongues[i].render()
            }

            if (moreFromID != null) {
                val placeholder = Placeholder()
                placeholder.setContent(kdiv(width = "100%", margin = "1em auto 1em auto"){o->
                    val btn = Button(title = t("Show more", "Показать еще"), className = "btn btn-default", style = Style(width = "100%", backgroundColor = Color.BLUE_GRAY_50), key = buttons.showMore)
                    btn.onClicka = {
                        async {
                            val blinker = await(effects).blinkOn(byid(btn.elementID))
                            TestGlobal.showMoreHalfwayLock.resumeTestAndPauseSutFromSut()
                            try {
                                val res = try {
                                    requestChunk(moreFromID)
                                } catch(e: Exception) {
                                    openErrorModal(const.msg.serviceFuckedUp)
                                    null
                                }

                                if (res != null) {
                                    exhaustive / when (res) {
                                        is FormResponse2.Shitty -> openErrorModal(res.error)
                                        is FormResponse2.Hunky -> {
                                            tongues.addAll(res.meat.items.map {MelindaTongue(it)})
                                            val newChunkContainerID = puid()
                                            placeholder.setContent(
                                                makeItemsControl(startIndex = tongues.lastIndex - res.meat.items.size + 1,
                                                                 moreFromID = res.meat.moreFromID,
                                                                 containerID = newChunkContainerID))
                                            await(scrollBodyToShitGradually {byid(newChunkContainerID)})
                                        }
                                    }
                                }
                            } finally {
                                blinker.unblink()
                                TestGlobal.showMoreDoneLock.resumeTestFromSut()
                            }
                        }
                    }
                    o- btn
                })
                o- placeholder
            }
        }
    }

    inner class MelindaTongue(var _item: Item) {
        val itemPlace by lazy {Placeholder(renderView())}
        val viewRootID = puid()

        val tongueInterface = object:MelindaTongueInterface<Item> {
            override val items get() = tongues.map {it._item}
            override val itemIndex get() = tongues.indexOf(this@MelindaTongue)

            override suspend fun onDelete() {
                val executed = modalConfirmAndDelete(
                    t("TOTE", "Удаляю ${vaginalInterface.humanItemTypeName} $numberSign${item.id}: ${item.title}"),
                    vaginalInterface.makeDeleteItemRequest()-{o->
                        o.id.value = item.id
                    }
                )

                if (executed) {
                    tongues.remove(this@MelindaTongue)
                    await(effects).fadeOut(viewRootID)
                    itemPlace.setContent(NOTRE)
                    TestGlobal.shitVanished.resumeTestAndPauseSutFromSut()
                }
            }

            override suspend fun onEdit() {
                val uparams = bang(vaginalInterface.updateParams)
                openEditModal(
                    title = t("TOTE", "Файл") + " " + numberSign + item.id,
                    formSpec = FormSpec<UpdateItemRequest, UpdateItemResponse>(
                        ui = Globus.world,
                        procedureName = uparams.updateItemProcedureNameIfNotDefault,
                        req = uparams.makeUpdateItemRequest(item)
                    ),
                    onSuccessa = {res->
                        _item = uparams.getItemFromUpdateItemResponse(res)
                        itemPlace.setContent(renderView())
                    }
                )
            }
        }

        fun render() = itemPlace

        private fun renderView(): ToReactElementable {
            val lipsInterface = vaginalInterface.makeLipsInterface(viewRootID, tongueInterface)
            return lipsInterface.renderItem()
        }

    }
}

object MelindaTools {
    fun row(marginBottom: String? = "0.5em", build: (ElementBuilder) -> Unit) =
        kdiv(className = "row", marginBottom = marginBottom){o->
            build(o)
        }

    fun col(size: Int, title: String, contentStyle: Style? = null, contentClassName: String? = null, build: (ElementBuilder) -> Unit) =
        kdiv(className = "col-md-$size"){o->
            o- klabel(marginBottom = 0) {it - title}
            o- kdiv(Attrs(className = contentClassName), contentStyle ?: Style()){o->
                build(o)
            }
        }

    fun col(size: Int, title: String, content: ToReactElementable) =
        col(size, title){o->
            o- content
        }

    fun col(size: Int, title: String, value: String, contentClassName: String? = null, textClassName: String? = null, icon: XIcon? = null) =
        col(size, title, contentClassName = contentClassName){o->
            icon?.let {
                o- icon.render(Style(marginRight = "0.5rem", marginTop = "-2px"))
            }
            o- span(value, className = textClassName)
        }


    fun createdAtCol(size: Int, value: Long) =
        col(size, t("Created", "Создан")){o->
            o- formatUnixTime(value)
        }

    fun updatedAtCol(size: Int, value: Long) =
        col(size, t("Updated", "Изменен")){o->
            o- formatUnixTime(value)
        }

    fun detailsRow(value: String, highlightRanges: List<IntRangeRTO>, title: String? = null, contentClassName: String? = null): ToReactElementable {
        return row{o->
            o- col(12, title ?: t("Details", "Детали"), Style(whiteSpace = "pre-wrap"), contentClassName = contentClassName){o->
                o- highlightedShit(value, highlightRanges, style = Style(marginBottom = "0px"))
            }
        }
    }
}

class MakeUsualMelindaLipsCallbacks(
    val onDelete: (suspend () -> Unit)? = null,
    val onEdit: (suspend () -> Unit)? = null
)

fun <T> MelindaTongueInterface<T>.toMakeUsualMelindaLipsCallbacks() = MakeUsualMelindaLipsCallbacks(
    onDelete = {this.onDelete()},
    onEdit = {this.onEdit()}
)

fun <ItemRTO : MelindaItemRTO, LipsState> makeUsualMelindaLips(
    viewRootID: String? = null,
    searchString: String,
    smallOverlayIcon: () -> IconClass? = {null},
    secondTitle: () -> String? = {null},
    drawID: () -> Boolean = {true},
    tinySubtitle: () -> String? = {null},
    renderAdditionalControls: (ElementBuilder, LipsState, updateTitleControls: (LipsState) -> Unit) -> Unit = {_,_,_->},
    renderContent: (ElementBuilder) -> Unit,
    initialLipsState: LipsState,
    controlsDisabled: (LipsState) -> Boolean = {false},
    // TODO:vgrechka Remove ItemRTO parameter from lambdas, as it can be obtained from tongueInterface
    icon: (ItemRTO) -> IconClass,
    titleLinkURL: String?,
    getItem: () -> ItemRTO,
    callbacks: MakeUsualMelindaLipsCallbacks = MakeUsualMelindaLipsCallbacks(),
    burgerMenu: () -> Menu? = {null}
)
    : MelindaLipsInterface
{
    return object:MelindaLipsInterface {
        private val titleControlsPlace = Placeholder(renderTitleControls(initialLipsState))

        override fun renderItem(): ToReactElementable {
            val m = MelindaTools
            val item = getItem()
            return kdiv(id = viewRootID, className = css.lipsItem, opacity = 1.0){o->
                o- m.row{o->
                    o- kdiv(className = "col-md-12"){o->
                        val c = css.cunt.header
                        o- kdiv(className = c.bar){o->
                            o- kdiv(className = c.titleAndStuff){o->
                                run { // Icons
                                    o- ki(className = "${c.leftIcon} ${icon(item)}")
                                    val theSmallOverlayIcon = smallOverlayIcon()
                                    if (theSmallOverlayIcon != null) {
                                        o- ki(className = "${c.leftOverlayBottomLeftIcon} $theSmallOverlayIcon")
                                    }
                                    o- " "
                                }

                                run { // Title
                                    val titleContent = highlightedShit(item.title, item.titleHighlightRanges, tag = "span")
                                    if (titleLinkURL != null)
                                        o- urlLink(url = titleLinkURL, content = titleContent, className = c.titleLink, key = SubscriptLinkKey(links.lipsTitle, item.id))
                                    else
                                        o- titleContent
                                }

                                run { // Second title
                                    val theSecondTitle = secondTitle()
                                    if (theSecondTitle != null) {
                                        o- span(theSecondTitle, className = c.secondTitle)
                                    }
                                }

                                run { // ID
                                    if (drawID()) {
                                        val idColor: Color?
                                        val idBackground: Color?
                                        if (searchString.split(Regex("\\s+")).contains(item.id.toString())) {
                                            idColor = Color.GRAY_800
                                            idBackground = Color.AMBER_200
                                        } else {
                                            idColor = Color.GRAY_500
                                            idBackground = null
                                        }
                                        o- kspan(marginLeft = "0.5em", fontSize = "75%", color = idColor, backgroundColor = idBackground){o->
                                            o- "$numberSign${item.id}"
                                        }
                                    }
                                }

                                run { // Tiny subtitle
                                    val theTinySubtitle = tinySubtitle()
                                    if (theTinySubtitle != null) {
                                        o- kspan(marginLeft = "0.5em", fontSize = "75%", color = Color.GRAY_500){o->
                                            o- theTinySubtitle
                                        }
                                    }
                                }
                            }

                            o- titleControlsPlace
                        }
                    }
                }
                renderContent(o)
            }
        }

        private fun renderTitleControls(state: LipsState): ToReactElementable {
            val item = getItem()
            val disabled = controlsDisabled(state)
            val c = css.cunt.header
            val iconClass = when {
                disabled -> c.rightIconDisabled
                else -> c.rightIcon
            }

            return hor3(Attrs(className = c.controls)){o->
                val updateTitleControls = {state: LipsState ->
                    titleControlsPlace.setContent(renderTitleControls(state))
                }
                renderAdditionalControls(o, state, updateTitleControls)

                if (item.editable) {
                    callbacks.onDelete?.let {act->
                        o- kic("${fa.trash} $iconClass",
                               style = Style(),
                               key = SubscriptKicKey(kics.delete, item.id),
                               onClicka = disableableHandler(disabled) {act()})
                    }
                    callbacks.onEdit?.let {act->
                        o- kic("${fa.pencil} $iconClass",
                               style = Style(),
                               key = SubscriptKicKey(kics.edit, item.id),
                               onClicka = disableableHandler(disabled) {act()})
                    }
                }

                burgerMenu()?.let {menu->
                    if (menu.items.isNotEmpty()) {
                        o- kdiv(className = "dropdown"){o->
                            o- kic("${fa.bars} $iconClass",
                                   style = Style(),
                                   dataToggle = "dropdown",
                                   key = SubscriptKicKey(kics.burger, item.id),
                                   onClicka = disableableHandler(disabled) {

                                   })

                            o- reactCreateElement("ul", json(
                                    "className" to "dropdown-menu dropdown-menu-right",
                                    "style" to json("minWidth" to "10rem")),
                                menu.items.map {item->
                                    reactCreateElement("li", json(), listOf(
                                        link2(key = item.linkKey, title = item.title) {
                                            item.act()
                                        }.toReactElement()
                                    ))})
                        }
                    }
                }
            }
        }
    }
}

class BoobyLoader<Item, Filter, CreateRequest, CreateResponse, UpdateItemRequest, UpdateItemResponse>(
    val header: String,
    val makeBoobs: () -> MelindaBoobs<Item, Filter, CreateRequest, CreateResponse, UpdateItemRequest, UpdateItemResponse>
) where
    Filter: Enum<Filter>, Filter: Titled,
    Item : MelindaItemRTO,
    CreateRequest : RequestMatumba, CreateResponse : CommonResponseFields,
    UpdateItemRequest : RequestMatumba, UpdateItemResponse : CommonResponseFields
{
    var bint by notNullOnce<MelindaBoobsInterface>()

    suspend fun load(): PageLoadingError? {
        val boobs = makeBoobs()

        bint = boobs.boobsInterface
        boobs.load()?.let {return PageLoadingError(it.error)}
        Globus.world.setPage(Page(header = usualHeader(header),
                                  headerControls = kdiv {o ->
                                      o - bint.controlsContent
                                  },
                                  body = bint.mainContent))
        return pageLoadedFineResult
    }
}









