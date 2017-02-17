package aps.front

import aps.*
import aps.const.text.numberSign
import kotlin.js.json

interface MelindaVaginalInterface<
    Item,
    Filter,
    out UpdateItemRequest,
    in UpdateItemResponse>
where
    Filter : Enum<Filter>,
    Filter : Titled,
    UpdateItemRequest : RequestMatumba,
    UpdateItemResponse : CommonResponseFields
{
    suspend fun sendItemsRequest(req: ItemsRequest<Filter>): FormResponse2<ItemsResponse<Item>>
    fun shouldShowFilter(): Boolean
    fun getParentEntityID(): Long?
    val humanItemTypeName: String
    fun makeDeleteItemRequest(): DeleteRequest
    fun makeUpdateItemRequest(item: Item): UpdateItemRequest
    fun getItemFromUpdateItemResponse(res: UpdateItemResponse): Item
    fun makeLipsInterface(viewRootID: String, tongueInterface: MelindaTongueInterface<Item>): MelindaLipsInterface
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

        val lipsInterface = vaginalInterface.makeLipsInterface(viewRootID = viewRootID, tongueInterface = tongueInterface)

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
    fun label(title: String) =
        klabel(marginBottom = 0) {it - title}

    fun row(build: (ElementBuilder) -> Unit) =
        kdiv(className = "row", marginBottom = "0.5em"){o->
            build(o)
        }

    fun col(size: Int, title: String, contentStyle: Style? = null, build: (ElementBuilder) -> Unit) =
        kdiv(className = "col-md-$size"){o->
            o- label(title)
            o- kdiv(style = contentStyle ?: Style()){o->
                build(o)
            }
        }

    fun titleBar(item: UAOrderFileRTO, boobsInterface: MelindaBoobsInterface, controls: ToReactElementable): ToReactElementable {
        return kdiv(className = "col-md-12"){o->
            o- kdiv(className = css.cunt.header.viewing){o->
                o- ki(className = "${css.cunt.header.leftIcon.viewing} ${fa.file}")
                o- ki(className = "${css.cunt.header.leftOverlayBottomLeftIcon.viewing} " +
                    when (item.seenAsFrom) {
                        UserKind.CUSTOMER -> fa.user
                        UserKind.WRITER -> fa.pencil
                        UserKind.ADMIN -> fa.cog
                    })
                o- " "
                o- highlightedShit(item.title, item.titleHighlightRanges, tag = "span")

                val idColor: Color?;
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

                o- kspan(marginLeft = "0.5em", fontSize = "75%", color = Color.GRAY_500){o->
                    o- when (item.seenAsFrom) {
                        Globus.world.user.kind -> t("Mine", "Мой")
                        UserKind.CUSTOMER -> t("From customer", "От заказчика")
                        UserKind.WRITER -> t("From writer", "От писателя")
                        UserKind.ADMIN -> t("From support", "От саппорта")
                    }
                }

                o- controls
            }
        }
    }

    fun titleControls(item: MelindaItemRTO,
                      tongueInterface: MelindaTongueInterface<UAOrderFileRTO>,
                      disabled: Boolean,
                      renderAdditionalControls: (ElementBuilder) -> Unit): ToReactElementable {
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

        return hor3(style = Style(position = "absolute", right = 0, top = 0, marginRight = "0.5rem", marginTop = "0.1rem")){o->
            renderAdditionalControls(o)
            if (item.editable) {
                o- kic("${fa.trash} $trashClass",
                        style = Style(),
                        key = SubscriptKicKey(kics.order.file.delete, item.id),
                        onClicka = disableableHandler(disabled) {tongueInterface.onDelete()})
                o- kic("${fa.pencil} $pencilClass",
                        style = Style(),
                        key = SubscriptKicKey(kics.order.file.edit, item.id),
                        onClicka = disableableHandler(disabled) {tongueInterface.onEdit()})
            }
        }
    }

}












