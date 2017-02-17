package aps.front

import aps.*
import aps.front.frontSymbols.numberSign
import into.kommon.*
import kotlin.js.json
import kotlin.properties.Delegates.notNull

interface LipsInterface {
    fun renderItem(initiallyTransparent: Boolean): ToReactElementable
}

interface TongueInterface<Item> {
    suspend fun onEdit()
    suspend fun onDelete()
    fun getItem(): Item
}

class TestDownloadContext {
    val downloadStartedLock by notNullNamed(TestLock(virgin = true))
    val bitsReceivedLock by notNullNamed(TestLock(virgin = true))
    var shit by notNull<DownloadFileResponse>()
}

class Boobs<CreateRequest,
            CreateResponse,
            Filter,
            Item,
            UpdateItemRequest,
            UpdateItemResponse>
(
    val hasCreateButton: Boolean,
    val plusModalTitle: String,
    val makeCreateRequest: () -> CreateRequest,
    val makeURLAfterCreation: () -> String,
    val makeURLForReload: (List<URLParamValue<*>>) -> String,
    val filterValues: Array<Filter>,
    val defaultFilterValue: Filter,
    val filterSelectKey: SelectKey<Filter>,
    val vaginalInterface: VaginalInterface<Filter,
                                           Item,
                                           UpdateItemRequest,
                                           UpdateItemResponse>
)
    where Filter: Enum<Filter>,
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

    var headerControlsDisabled = false
    var chunksLoaded = 0
    var stripContent by notNullOnce<Control2>()
    var mainContent by notNullOnce<ToReactElementable>()

    fun makeStripContent() {
        stripContent = object:Control2(Attrs()) {
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
                                title = plusModalTitle,
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

    val boobsInterface = object:BoobsInterface {
        override val mainContent get()= this@Boobs.mainContent
        override val stripContent get()= this@Boobs.stripContent
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
        stripContent.update()
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
            stripContent.update()                          // TODO:vgrechka Redundant?

            TestGlobal.shitDoneLock.resumeTestFromSut()
        }
    }

    private suspend fun requestChunk(fromID: Long?): FormResponse2<ItemsResponse<Item>> {
        val res = vaginalInterface.sendItemsRequest(ItemsRequest(filterValues) - {o ->
            o.entityID.value = vaginalInterface.getParentEntityID()
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
                Pizda(_orderFile, o)
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

    // TODO:vgrechka Rename class
    inner class Pizda(initialItem: Item, o: ElementBuilder) {
        var itemPlace = Placeholder()
        var item = initialItem
        val viewRootID = puid()

        val tongueInterface = object:TongueInterface<Item> {
            override fun getItem(): Item = item

            override suspend fun onDelete() {
                val deletionConfirmed = modalConfirmAndDelete(
                    t("TOTE", "Удаляю ${vaginalInterface.humanItemTypeName} $numberSign${item.id}: ${item.title}"),
                    vaginalInterface.makeDeleteItemRequest() - {o ->
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
            return lipsInterface.renderItem(initiallyTransparent = false)
        }

    }
}

interface VaginalInterface<Filter, Item, UpdateItemRequest, UpdateItemResponse>
where Filter : Enum<Filter>, Filter : Titled, UpdateItemRequest : RequestMatumba, UpdateItemResponse : CommonResponseFields
{
    suspend fun sendItemsRequest(req: ItemsRequest<Filter>): FormResponse2<ItemsResponse<Item>>
    fun shouldShowFilter(): Boolean
    fun getParentEntityID(): Long
    val humanItemTypeName: String
    fun makeDeleteItemRequest(): DeleteRequest
    fun makeUpdateItemRequest(item: Item): UpdateItemRequest
    fun getItemFromUpdateItemResponse(res: UpdateItemResponse): Item
    fun makeLipsInterface(viewRootID: String, tongueInterface: TongueInterface<Item>): LipsInterface
}

interface BoobsInterface {
    val stripContent: ToReactElementable
    val mainContent: ToReactElementable
    fun getSearchString(): String
}

class UACustomerSingleOrderPageFilesTab(val page: UACustomerSingleOrderPage, val world: World, val order: UAOrderRTO) : CustomerSingleUAOrderPageTab {
    lateinit var meat: ItemsResponse<UAOrderFileRTO>
    var boobsInterface by notNullOnce<BoobsInterface>()

    override val tabSpec = TabSpec(tabs.order.files, t("Files", "Файлы"),
                                   ToReactElementable.from{boobsInterface.mainContent},
                                   ToReactElementable.from{boobsInterface.stripContent})

    override suspend fun load(): FormResponse2.Shitty<*>? {
        val boobs = Boobs<
            UACreateOrderFileRequest,
            UACreateOrderFileRequest.Response,
            CustomerFileFilter,
            UAOrderFileRTO,
            UAUpdateOrderFileRequest,
            UAUpdateOrderFileRequest.Response
        >(
            hasCreateButton = order.state == UAOrderState.CUSTOMER_DRAFT,
            plusModalTitle = t("TOTE", "Новый файл"),
            makeCreateRequest = {
                UACreateOrderFileRequest() - {o ->
                    o.orderID.value = order.id
                }
            },
            makeURLAfterCreation = {
                val q = UACustomerSingleOrderPage.urlQuery
                makeURL(pages.uaCustomer.order, listOf(
                    URLParamValue(q.id, order.id.toString()),
                    URLParamValue(q.tab, simpleName(tabs.order.files.fqn))
                ))
            },
            makeURLForReload = {paramValuesFromVagina: List<URLParamValue<*>> ->
                val qPage = UACustomerSingleOrderPage.urlQuery
                val paramValues = listOf(
                    URLParamValue(qPage.id, order.id.toString()),
                    URLParamValue(qPage.tab, simpleName(tabs.order.files.fqn)))
                makeURL(pages.uaCustomer.order, paramValues + paramValuesFromVagina)
            },
            filterValues = CustomerFileFilter.values(),
            defaultFilterValue = CustomerFileFilter.ALL,
            filterSelectKey = selects.filter,
            vaginalInterface = vaginalInterface
        )

        boobsInterface = boobs.boobsInterface

        return boobs.load()
    }

    val vaginalInterface = object:VaginalInterface<CustomerFileFilter, UAOrderFileRTO, UAUpdateOrderFileRequest, UAUpdateOrderFileRequest.Response> {

        override fun makeLipsInterface(viewRootID: String, tongueInterface: TongueInterface<UAOrderFileRTO>): LipsInterface {
            return object:LipsInterface {
                val titleRightPlace = Placeholder(renderTitleControls())
                val cloudIconID = puid()

                private val item get()= tongueInterface.getItem()

                fun label(title: String) = klabel(marginBottom = 0) {it - title}

                fun row(build: (ElementBuilder) -> Unit) =
                    kdiv(className = "row", marginBottom = "0.5em"){o->
                        build(o)
                    }

                fun renderTitleControls(downloadActive: Boolean = false): ElementBuilder {
                    val cloudClass: String; val trashClass: String; val pencilClass: String
                    val c = css.cunt.header
                    if (downloadActive) {
                        cloudClass = c.rightIconActive
                        trashClass = c.rightIconDisabled
                        pencilClass = c.rightIconDisabled
                    } else {
                        cloudClass = c.rightIcon
                        trashClass = c.rightIcon
                        pencilClass = c.rightIcon
                    }

                    fun ifNotDownloading(f: suspend () -> Unit) = when {
                        downloadActive -> {{TestGlobal.disabledActionHitLock.resumeTestFromSut()}}
                        else -> f
                    }

                    return hor3(style = Style(position = "absolute", right = 0, top = 0, marginRight = "0.5rem", marginTop = "0.1rem")) {o->
                        o- kic("${fa.cloudDownload} $cloudClass", id = cloudIconID, style = Style(marginTop = "0.45rem"), key = SubscriptKicKey(kics.order.file.download, item.id), onClicka = ifNotDownloading {onDownload()})
                        if (item.editable) {
                            o- kic("${fa.trash} $trashClass", style = Style(), key = SubscriptKicKey(kics.order.file.delete, item.id), onClicka = ifNotDownloading {tongueInterface.onDelete()})
                            o- kic("${fa.pencil} $pencilClass", style = Style(), key = SubscriptKicKey(kics.order.file.edit, item.id), onClicka = ifNotDownloading {tongueInterface.onEdit()})
                        }
                    }
                }

                private suspend fun onDownload() {
                    titleRightPlace.setContent(renderTitleControls(downloadActive = true))
                    val blinker = await(effects).blinkOn(byid(cloudIconID), BlinkOpts())

                    val testCtx = TestGlobal.orderFileIDToDownloadContext[item.id]
                    testCtx?.downloadStartedLock?.resumeTestAndPauseSutFromSut()

                    val res = send(UADownloadOrderFileRequest()-{o->
                        o.fileID.value = item.id
                    })
                    titleRightPlace.setContent(renderTitleControls())
                    exhaustive/when (res) {
                        is FormResponse2.Shitty -> {
                            imf("onDownload fuckup")
                        }
                        is FormResponse2.Hunky -> {
                            val dataURL = "data:application/octet-stream;base64," + res.meat.base64
                            downloadjs(dataURL, res.meat.fileName, "application/octet-stream")
                            blinker.unblink()
                            titleRightPlace.setContent(renderTitleControls(downloadActive = false))
                            testCtx?.let {
                                it.shit = res.meat
                                it.bitsReceivedLock.resumeTestFromSut()
                            }
                        }
                    }
                }

                fun renderFileTitle(item: UAOrderFileRTO): ElementBuilder {
                    val editing = false // TODO:vgrechka @kill
                    return kdiv(className = "col-md-12"){o->
                        o- kdiv(className = if (editing) css.cunt.header.editing else css.cunt.header.viewing){o->
                            o- ki(className = "${if (editing) css.cunt.header.leftIcon.editing else css.cunt.header.leftIcon.viewing} ${fa.file}")
                            o- ki(className = "${if (editing) css.cunt.header.leftOverlayBottomLeftIcon.editing else css.cunt.header.leftOverlayBottomLeftIcon.viewing} " +
                                when (item.seenAsFrom) {
                                    UserKind.CUSTOMER -> fa.user
                                    UserKind.WRITER -> fa.pencil
                                    UserKind.ADMIN -> fa.cog
                                })
                            o- " "
                            o- highlightedShit(item.title, item.titleHighlightRanges, tag = "span")

                            val idColor: Color?; val idBackground: Color?
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
                                    world.user.kind -> t("Mine", "Мой")
                                    UserKind.CUSTOMER -> t("From customer", "От заказчика")
                                    UserKind.WRITER -> t("From writer", "От писателя")
                                    UserKind.ADMIN -> t("From support", "От саппорта")
                                }
                            }

                            o- titleRightPlace
                        }
                    }
                }

                override fun renderItem(initiallyTransparent: Boolean): ToReactElementable {
                    return when (world.user.kind) {
                        UserKind.CUSTOMER -> {
                            kdiv(id = viewRootID,
                                 className = css.item,
                                 opacity = if (initiallyTransparent) 0.0
                                 else 1.0){o->
                                o- row{o->
                                    o- renderFileTitle(item)
                                }
                                o- row{o->
                                    o- kdiv(className = "col-md-3"){o->
                                        o- label(t("Created", "Создан"))
                                        o- kdiv(){o->
                                            o- formatUnixTime(item.createdAt)
                                        }
                                    }
                                    o- kdiv(className = "col-md-3"){o->
                                        o- label(t("Updated", "Изменен"))
                                        o- kdiv(){o->
                                            o- formatUnixTime(item.updatedAt)
                                        }
                                    }
                                    o- kdiv(className = "col-md-3"){o->
                                        o- label(t("File name", "Имя файла"))
                                        o- kdiv(){o->
                                            o- highlightedShit(item.name, item.nameHighlightRanges, tag = "span")
                                        }
                                    }
                                    o- kdiv(className = "col-md-3"){o->
                                        o- label(t("Size", "Размер"))
                                        o- kdiv(){o->
                                            o- formatFileSizeApprox(Globus.lang, item.sizeBytes)
                                        }
                                    }
                                }
                                o- row{o->
                                    o- kdiv(className = "col-md-12"){o->
                                        o- label(t("Details", "Детали"))
                                        o- kdiv(whiteSpace = "pre-wrap"){o->
                                            o- highlightedShit(item.details, item.detailsHighlightRanges)
                                        }
                                    }
                                }
                            }
                        }

                        UserKind.WRITER -> imf()

                        UserKind.ADMIN -> imf()
                    }
                }
            }
        }

        override val humanItemTypeName = t("TOTE", "файл")

        override fun getItemFromUpdateItemResponse(res: UAUpdateOrderFileRequest.Response): UAOrderFileRTO {
            return res.file
        }

        override fun makeUpdateItemRequest(item: UAOrderFileRTO): UAUpdateOrderFileRequest {
            return UAUpdateOrderFileRequest()-{o->
                o.fileID.value = item.id
                o.file.content = FileField.Content.Unchanged(item.name, item.sizeBytes)
                o.fields1-{o->
                    o.title.value = item.title
                    o.details.value = item.details
                }
            }
        }

        override fun makeDeleteItemRequest(): DeleteRequest {
            return UADeleteOrderFileRequest()
        }

        override fun getParentEntityID(): Long {
            return order.id
        }

        override fun shouldShowFilter(): Boolean {
            return when (order.state) {
                    UAOrderState.CUSTOMER_DRAFT, UAOrderState.WAITING_ADMIN_APPROVAL -> false
                    else -> true
                }
        }

        override suspend fun sendItemsRequest(req: ItemsRequest<CustomerFileFilter>)
            : FormResponse2<ItemsResponse<UAOrderFileRTO>>
        {
            return sendUACustomerGetOrderFiles(req)
        }


    }

}




















