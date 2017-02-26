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

val <Item> MelindaTongueInterface<Item>.item: Item get() = items[itemIndex]

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
    val filterSelectKey: SelectKey<Filter>,
    val vaginalInterface: MelindaVagina<
        Item,
        Filter,
        UpdateItemRequest, UpdateItemResponse>
) where
    Filter: Enum<Filter>,
    Filter: Titled,
    Item : MelindaItemRTO,
    CreateRequest : RequestMatumba,
    CreateResponse : CommonResponseFields,
    UpdateItemRequest : RequestMatumba,
    UpdateItemResponse : CommonResponseFields
{
    val urlQuery = _URLQuery()
    inner class _URLQuery : URLQueryParamsMarker {
        val ordering by EnumURLParam(Ordering.values(), default = Ordering.DESC)
        val filter by EnumURLParam(filterValues, default = defaultFilterValue)
        val search by StringURLParam("")
    }

    private var headerControlsDisabled = false
    private var chunksLoaded = 0
    private var controlsContent by notNullOnce<Control2>()
    private var mainContent by notNullOnce<ToReactElementable>()
    private var liveItems by notNullOnce<MutableList<Item>>() // Items can change in here as result of user editing shit, without the need to reload everything

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

                    val refreshButtonID = puid()
                    o- Button(id = refreshButtonID, icon = fa.refresh, volatileDisabled = {headerControlsDisabled}, key = buttons.refreshPage) {
                        asu {reload(refreshButtonID)}
                    }

                    if (createParams != null) {
                        o- Button(icon = fa.plus, level = Button.Level.PRIMARY, key = buttons.plus) {
                            openEditModal( // TODO:vgrechka Replace `Edit` with something more general
                                title = createParams.createModalTitle,
                                formSpec = FormSpec<CreateRequest, CreateResponse>(
                                    ui = Globus.world,
                                    procedureName = createParams.createProcedureNameIfNotDefault,
                                    req = createParams.makeCreateRequest()
                                ),
                                onSuccessa = {res->
                                    Globus.world.replaceNavigate(createParams.makeURLAfterCreation(res))
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    val boobsInterface = object:MelindaBoobsInterface {
        override val mainContent get()= this@MelindaBoobs.mainContent
        override val controlsContent get()= this@MelindaBoobs.controlsContent
        override fun getSearchString() = urlQuery.search.get()
    }

    val filterSelect = Select(
        key = filterSelectKey,
        values = filterValues,
        initialValue = urlQuery.filter.get(),
        isAction = true,
        style = json("width" to 160),
        volatileDisabled = {headerControlsDisabled}
    )

    val orderingSelect = Select(
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
        ++chunksLoaded
        return res
    }

    suspend fun load(): FormResponse2.Shitty<*>? {
        val res = requestChunk(null)
        return when (res) {
            is FormResponse2.Shitty -> res
            is FormResponse2.Hunky -> {
                val meat = res.meat
                liveItems = meat.items.toMutableList()

                makeStripContent()

                val items = makeItemsControl(meat, noItemsMessage = true, chunkIndex = chunksLoaded - 1)
                mainContent = ToReactElementable.from {kdiv{o->
                    o- items
                }}

                null
            }
        }
    }

    private fun makeItemsControl(meat: ItemsResponse<Item>, noItemsMessage: Boolean, chunkIndex: Int, containerID: String? = null): ToReactElementable {
        if (liveItems.isEmpty()) {
            return if (noItemsMessage) span(const.msg.noItems)
            else NOTRE
        }

        return kdiv(id = containerID){o->
            o- kdiv(className = css.lipsItemContainer){o->
                for (i in 0..liveItems.lastIndex)
                    MelindaTongue(i, o)
            }

            meat.moreFromID?.let {moreFromID ->
                moreFromID
                val placeholder = Placeholder()
                placeholder.setContent(kdiv(width = "100%", margin = "1em auto 1em auto"){o->
                    val btn = Button(title = t("Show more", "Показать еще"), className = "btn btn-default", style = Style(width = "100%", backgroundColor = Color.BLUE_GRAY_50), key = buttons.showMore)
                    btn.onClicka = {
                        async {
                            val blinker = await(effects).blinkOn(byid(btn.elementID))
                            TestGlobal.showMoreHalfwayLock.resumeTestAndPauseSutFromSut()
                            try {
                                val res = try {
                                    requestChunk(meat.moreFromID)
                                } catch(e: Exception) {
                                    openErrorModal(const.msg.serviceFuckedUp)
                                    null
                                }

                                if (res != null) {
                                    exhaustive / when (res) {
                                        is FormResponse2.Shitty -> openErrorModal(res.error)
                                        is FormResponse2.Hunky -> {
                                            val newChunkContainerID = puid()
                                            placeholder.setContent(
                                                makeItemsControl(res.meat,
                                                                 noItemsMessage = false,
                                                                 chunkIndex = chunksLoaded - 1,
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

    inner class MelindaTongue(itemIndex: Int, o: ElementBuilder) {
        var itemPlace = Placeholder()
        val viewRootID = puid()

        val tongueInterface = object:MelindaTongueInterface<Item> {
            override val items = liveItems
            override val itemIndex = itemIndex

            override suspend fun onDelete() {
                val executed = modalConfirmAndDelete(
                    t("TOTE", "Удаляю ${vaginalInterface.humanItemTypeName} $numberSign${item.id}: ${item.title}"),
                    vaginalInterface.makeDeleteItemRequest()-{o->
                        o.id.value = item.id
                    }
                )

                if (executed) {
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
                        liveItems[itemIndex] = uparams.getItemFromUpdateItemResponse(res)
                        enterViewMode()
                    }
                )
            }
        }

        val lipsInterface = vaginalInterface.makeLipsInterface(viewRootID, tongueInterface)

        init {
            enterViewMode()
            o- itemPlace
        }

        fun enterViewMode() {
            itemPlace.setContent(renderView())
        }

        fun renderView(initiallyTransparent: Boolean = false): ToReactElementable {
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

    fun detailsRow(value: String, highlightRanges: List<IntRangeRTO>, title: String? = null): ToReactElementable {
        return row{o->
            o- col(12, title ?: t("Details", "Детали"), Style(whiteSpace = "pre-wrap")){o->
                o- highlightedShit(value, highlightRanges, style = Style(marginBottom = "0px"))
            }
        }
    }
}

fun <ItemRTO : MelindaItemRTO, LipsState> makeUsualMelindaLips(
    tongueInterface: MelindaTongueInterface<ItemRTO>,
    viewRootID: String,
    boobsInterface: MelindaBoobsInterface,
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
    hasEditControl: (ItemRTO) -> Boolean,
    hasDeleteControl: (ItemRTO) -> Boolean,
    burgerMenu: () -> Menu? = {null}
)
    : MelindaLipsInterface
{
    return object:MelindaLipsInterface {
        private val titleControlsPlace = Placeholder(renderTitleControls(initialLipsState))

        override fun renderItem(): ToReactElementable {
            val m = MelindaTools
            val item = tongueInterface.item
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
                                        o- urlLink(url = titleLinkURL, content = titleContent, className = c.titleLink, key = SubscriptLinkKey(links.lips, item.id))
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
                                        if (boobsInterface.getSearchString().split(Regex("\\s+")).contains(item.id.toString())) {
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
            val item = tongueInterface.item
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
                    if (hasDeleteControl(item))
                        o- kic("${fa.trash} $iconClass",
                               style = Style(),
                               key = SubscriptKicKey(kics.delete, item.id),
                               onClicka = disableableHandler(disabled) {tongueInterface.onDelete()})
                    if (hasEditControl(item))
                        o- kic("${fa.pencil} $iconClass",
                               style = Style(),
                               key = SubscriptKicKey(kics.edit, item.id),
                               onClicka = disableableHandler(disabled) {tongueInterface.onEdit()})
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
                                        reactCreateElement("a", json(
                                            "href" to "#",
                                            "onClick" to {e: MouseEvent -> async {
                                                preventAndStop(e)
                                                item.act()}}),
                                            listOf(
                                                item.title.asReactElement()))))})
                        }
                    }
                }
            }
        }
    }
}










