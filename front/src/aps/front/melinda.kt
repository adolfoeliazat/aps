package aps.front

import aps.*
import aps.const.text.numberSign
import into.kommon.*
import org.w3c.dom.Element
import kotlin.js.json

interface MelindaVaginalInterface<
    ItemRTO,
    Filter,
    out UpdateItemRequest,
    in UpdateItemResponse>
where
    Filter : Enum<Filter>,
    Filter : Titled,
    UpdateItemRequest : RequestMatumba,
    UpdateItemResponse : CommonResponseFields
{
    suspend fun sendItemsRequest(req: ItemsRequest<Filter>): FormResponse2<ItemsResponse<ItemRTO>>
    fun shouldShowFilter(): Boolean
    fun getParentEntityID(): Long?
    val humanItemTypeName: String
    fun makeDeleteItemRequest(): DeleteRequest
    fun makeUpdateItemRequest(item: ItemRTO): UpdateItemRequest
    fun getItemFromUpdateItemResponse(res: UpdateItemResponse): ItemRTO
    fun makeLipsInterface(viewRootID: String, tongue: MelindaTongueInterface<ItemRTO>): MelindaLipsInterface
}

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
    fun getItem(): Item
}

class MelindaBoobs<
    Item,
    Filter,
    CreateRequest,
    CreateResponse,
    UpdateItemRequest,
    UpdateItemResponse>
(
    val hasCreateButton: Boolean,
    val createModalTitle: String,
    val makeCreateRequest: () -> CreateRequest,
    val makeURLAfterCreation: () -> String,
    val makeURLForReload: (boobsParams: List<URLParamValue<*>>) -> String,
    val filterValues: Array<Filter>,
    val defaultFilterValue: Filter,
    val filterSelectKey: SelectKey<Filter>,
    val vaginalInterface: MelindaVaginalInterface<
        Item,
        Filter,
        UpdateItemRequest,
        UpdateItemResponse>
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

                    if (hasCreateButton) {
                        o- Button(icon = fa.plus, level = Button.Level.PRIMARY, key = buttons.plus) {
                            openEditModal( // TODO:vgrechka Replace `Edit` with something more general
                                title = createModalTitle,
                                formSpec = FormSpec<CreateRequest, CreateResponse>(
                                    ui = Globus.world,
                                    req = makeCreateRequest()
                                ),
                                onSuccessa = {
                                    Globus.world.replaceNavigate(makeURLAfterCreation())
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
        if (meat.items.isEmpty()) {
            return if (noItemsMessage) span(const.msg.noItems)
            else NOTRE
        }

        return kdiv(id = containerID){o->
            var topPlace = Placeholder()
            o- topPlace

            for ((fileIndex, _orderFile) in meat.items.withIndex()) {
                MelindaTongue(_orderFile, o)
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

    inner class MelindaTongue(initialItem: Item, o: ElementBuilder) {
        var itemPlace = Placeholder()
        var item = initialItem
        val viewRootID = puid()

        val tongueInterface = object: MelindaTongueInterface<Item> {
            override fun getItem(): Item = item

            override suspend fun onDelete() {
                val deletionConfirmed = modalConfirmAndDelete(
                    t("TOTE", "Удаляю ${vaginalInterface.humanItemTypeName} $numberSign${item.id}: ${item.title}"),
                    vaginalInterface.makeDeleteItemRequest()-{o->
                        o.id.value = item.id
                    }
                )

                if (deletionConfirmed) {
                    await(effects).fadeOut(viewRootID)
                    itemPlace.setContent(NOTRE)
                    TestGlobal.shitVanished.resumeTestAndPauseSutFromSut()
                }
            }

            override suspend fun onEdit() {
                openEditModal(
                    title = t("TOTE", "Файл") + " " + numberSign + item.id,
                    formSpec = FormSpec<UpdateItemRequest, UpdateItemResponse>(
                        ui = Globus.world,
                        req = vaginalInterface.makeUpdateItemRequest(item)
                    ),
                    onSuccessa = {res->
                        item = vaginalInterface.getItemFromUpdateItemResponse(res)
                        enterViewMode()
                    }
                )
            }
        }

        val lipsInterface = vaginalInterface.makeLipsInterface(viewRootID = viewRootID, tongue = tongueInterface)

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
    fun row(build: (ElementBuilder) -> Unit) =
        kdiv(className = "row", marginBottom = "0.5em"){o->
            build(o)
        }

    fun col(size: Int, title: String, contentStyle: Style? = null, contentClassName: String? = null, build: (ElementBuilder) -> Unit) =
        kdiv(className = "col-md-$size"){o->
            o- klabel(marginBottom = 0) {it - title}
            o- kdiv(Attrs(className = contentClassName), contentStyle ?: Style()){o->
                build(o)
            }
        }

    fun col(size: Int, title: String, value: String, className: String? = null) =
        col(3, title, contentClassName = className){o->
            o- value
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
                o- highlightedShit(value, highlightRanges)
            }
        }
    }
}

fun <ItemRTO : MelindaItemRTO, LipsState> makeUsualMelindaLips(
    tongueInterface: MelindaTongueInterface<ItemRTO>,
    viewRootID: String,
    boobsInterface: MelindaBoobsInterface,
    smallOverlayIcon: () -> IconClass? = {null},
    tinySubtitle: () -> String? = {null},
    renderAdditionalControls: (ElementBuilder, LipsState, updateTitleControls: (LipsState) -> Unit) -> Unit = {_,_,_->},
    renderContent: (ElementBuilder) -> Unit,
    initialState: LipsState,
    controlsDisabled: (LipsState) -> Boolean = {false},
    icon: IconClass,
    titleLinkURL: String?,
    hasEditControl: (ItemRTO) -> Boolean,
    hasDeleteControl: (ItemRTO) -> Boolean
)
    : MelindaLipsInterface
{
    return object:MelindaLipsInterface {
        private val titleControlsPlace = Placeholder(renderTitleControls(initialState))

        override fun renderItem(): ToReactElementable {
            val m = MelindaTools
            val item = tongueInterface.getItem()
            return kdiv(id = viewRootID, className = css.item, opacity = 1.0){o->
                o- m.row{o->
                    o- kdiv(className = "col-md-12"){o->
                        val c = css.cunt.header
                        o- kdiv(className = c.bar){o->
                            o- kdiv(className = c.titleAndStuff){o->
                                o- ki(className = "${c.leftIcon} $icon")
                                val theSmallOverlayIcon = smallOverlayIcon()
                                if (theSmallOverlayIcon != null) {
                                    o- ki(className = "${c.leftOverlayBottomLeftIcon} $theSmallOverlayIcon")
                                }
                                o- " "
                                val titleContent = highlightedShit(item.title, item.titleHighlightRanges, tag = "span")
                                if (titleLinkURL != null)
                                    o- urlLink(url = titleLinkURL, content = titleContent, className = c.titleLink, key = SubscriptLinkKey(links.lips, item.id))
                                else
                                    o- titleContent

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

                                val theTinySubtitle = tinySubtitle()
                                if (theTinySubtitle != null) {
                                    o- kspan(marginLeft = "0.5em", fontSize = "75%", color = Color.GRAY_500){o->
                                        o- theTinySubtitle
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
            val item = tongueInterface.getItem()
            val disabled = controlsDisabled(state)

            val trashClass: String
            val pencilClass: String
            val c = css.cunt.header
            if (disabled) {
                trashClass = c.rightIconDisabled
                pencilClass = c.rightIconDisabled
            } else {
                trashClass = c.rightIcon
                pencilClass = c.rightIcon
            }

            return hor3(Attrs(className = c.controls)){o->
                val updateTitleControls = {state: LipsState ->
                    titleControlsPlace.setContent(renderTitleControls(state))
                }
                renderAdditionalControls(o, state, updateTitleControls)

                if (item.editable) {
                    if (hasDeleteControl(item))
                        o- kic("${fa.trash} $trashClass",
                               style = Style(),
                               key = SubscriptKicKey(kics.order.file.delete, item.id),
                               onClicka = disableableHandler(disabled) {tongueInterface.onDelete()})
                    if (hasEditControl(item))
                        o- kic("${fa.pencil} $pencilClass",
                               style = Style(),
                               key = SubscriptKicKey(kics.order.file.edit, item.id),
                               onClicka = disableableHandler(disabled) {tongueInterface.onEdit()})
                }
            }
        }
    }
}










