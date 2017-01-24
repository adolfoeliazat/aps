package aps.front

import aps.*
import aps.front.frontSymbols.numberSign
import into.kommon.*
import jquery.jq
import org.w3c.dom.HTMLIFrameElement
import kotlin.properties.Delegates.notNull

private val moveItemToTopOnEdit = true

//private class FilesTabURLQuery {
//    var ordering: String? = null
//    var filter: String? = null
//    var search: String? = null
//}

class CustomerSingleUAOrderPageFilesTab(val page: CustomerSingleUAOrderPage, val world: World, val order: UAOrderRTO) : CustomerSingleUAOrderPageTab {
//    lateinit var ordering: Ordering
//    lateinit var filter: CustomerFileFilter
//    lateinit var search: String
    lateinit var meat: ItemsResponse<UAOrderFileRTO>
    lateinit var content: ToReactElementable
    lateinit var stripContent: Control2
    lateinit var plusFormContainer: Control2
    var chunksLoaded = 0
    var urlQuery by notNull<FilesTabURLQuery>()

    override val tabSpec = TabSpec("files", t("Files", "Файлы"),
                                   ToReactElementable.from{content},
                                   ToReactElementable.from{stripContent})

    inner class FilesTabURLQuery : URLQueryBase(world) {
        val ordering by enumURLParam(Ordering.values(), default = Ordering.DESC)
        val filter by enumURLParam(CustomerFileFilter.values(), default = CustomerFileFilter.ALL)
        val search by stringURLParam()
    }

    override fun load(): Promisoid<ZimbabweResponse.Shitty<*>?> = async {
        urlQuery = FilesTabURLQuery()

        val res = await(requestChunk(null))
        when (res) {
            is ZimbabweResponse.Shitty -> res
            is ZimbabweResponse.Hunky -> {
                meat = res.meat
                stripContent = StripContent()

                plusFormContainer = Control2.from {kdiv{o->
                    o- ebafPlus.renderForm()
                }}

                val items = makeItemsControl(meat, noItemsMessage = true, chunkIndex = chunksLoaded - 1)
                content = ToReactElementable.from {kdiv{o->
                    o- plusFormContainer
                    o- items
                }}

                null
            }
        }
    }

    private fun requestChunk(fromID: String?): Promisoid<ZimbabweResponse<ItemsResponse<UAOrderFileRTO>>> = async {
        val res = await(sendCustomerGetUAOrderFiles(world.token, ItemsRequest(CustomerFileFilter.values())-{o->
            o.entityID.value = order.id
            o.filter.value = urlQuery.filter
            o.ordering.value = urlQuery.ordering
            o.searchString.value = urlQuery.search
            o.fromID.value = fromID
        }))
        ++chunksLoaded
        res
    }

    val ebafHost = EBAFHost()
    inner class EBAFHost : EvaporatingButtonAndFormHost {
        override var showEmptyLabel = true
        override var cancelForm = {}
        override var headerControlsDisabled = false
        override var headerControlsVisible = true
        override var headerControlsClass = ""

        override fun updateShit() {
            stripContent.update()
            plusFormContainer.update()
        }
    }

    val ebafPlus = EvaporatingButtonAndForm(
        host = ebafHost, key = fconst.key.plus.decl, level = Button.Level.PRIMARY, icon = fa.plus,
        formSpec = FormSpec<CustomerAddUAOrderFileRequest, AddUAOrderFileRequestBase.Response>(
            CustomerAddUAOrderFileRequest()-{o->
                o.orderID.value = order.id
            }, world,
            primaryButtonTitle = t("Add", "Добавить"),
            cancelButtonTitle = const.defaultCancelButtonTitle
        ),
        onSuccessa = {res->
            world.pushNavigate("order.html?id=${order.id}&tab=files")
        }
    )

    inner class StripContent : Control2(Attrs()) {
        val filterSelect = Select(
            key = fconst.key.filter.decl,
            values = CustomerFileFilter.values(),
            initialValue = urlQuery.filter,
            isAction = true,
            style = json("width" to 160),
            volatileDisabled = {ebafHost.headerControlsDisabled}
        )

        val orderingSelect = Select(
            key = fconst.key.ordering.decl,
            values = Ordering.values(),
            initialValue = urlQuery.ordering,
            isAction = true,
            style = json("width" to 160),
            volatileDisabled = {ebafHost.headerControlsDisabled}
        )

        val searchInput = Input(
            key = fconst.key.search.decl,
            style = Style(paddingLeft = 30, width = "100%"),
            placeholder = t("Search...", "Поиск..."),
            volatileDisabled  = {ebafHost.headerControlsDisabled}
        )

        init {
            searchInput.setValue(urlQuery.search)

            filterSelect.onChanga = {reload(filterSelect.elementID)}
            orderingSelect.onChanga = {reload(orderingSelect.elementID)}
            searchInput.onKeyDowna = {e-> async {
                if (e.keyCode == 13) {
                    preventAndStop(e)
                    await(reload(searchInput.elementID))
                }
            }}

//                gloshit.updateStripContent = {stripContent.update()}
        }

        override fun render(): ToReactElementable {
            if (!ebafHost.headerControlsVisible) return NOTRE
            return hor2{o->
                o- kdiv(position = "relative"){o->
                    o- searchInput
                    o- ki(className = "${fa.search}", position = "absolute", left = 10, top = 10, color = Color.GRAY_500)
                }
                o- filterSelect
                o- orderingSelect

                val refreshButtonID = puid()
                o- Button(key = fconst.key.refreshPage.decl, id = refreshButtonID, icon = fa.refresh, volatileDisabled = {ebafHost.headerControlsDisabled}) {
                    reload(refreshButtonID)
                }

                o- ebafPlus.renderButton()
            }
        }

        fun reloadFilesTab(): Promisoid<Unit> =
            world.pushNavigate("order.html?id=${order.id}&tab=files"
                                   + "&ordering=${orderingSelect.value.name}"
                                   + "&filter=${filterSelect.value.name}"
                                   + "&search=${encodeURIComponent(searchInput.getValue())}")

        fun reload(elementID: String): Promisoid<Unit> = async {
            await(effects).blinkOn(byid(elementID))
            ebafHost.headerControlsDisabled = true
            stripContent.update()
            await(TestGlobal.reloadPageTickingLock.sutPause())
            try {
                await(reloadFilesTab())
            } finally {
                await(effects).blinkOffFadingOut()
                ebafHost.headerControlsDisabled = false
                stripContent.update() // TODO:vgrechka Redundant?
                await(TestGlobal.loadPageForURLLock.sutPause2())
                await(TestGlobal.reloadPageDoneLock.sutPause())
            }
        }
    }

    private fun makeItemsControl(meat: ItemsResponse<UAOrderFileRTO>, noItemsMessage: Boolean, chunkIndex: Int, containerID: String? = null): ToReactElementable {
        if (meat.items.isEmpty()) {
            return if (noItemsMessage) span(const.msg.noItems)
            else NOTRE
        }

        return kdiv(id = containerID){o->
            var topPlace = Placeholder()
            o- topPlace

            for ((fileIndex, _orderFile) in meat.items.withIndex()) {
                object {
                    var itemPlace = Placeholder()
                    var orderFile = _orderFile
                    val viewRootID = puid()

                    init {
                        enterViewMode()
                        o- itemPlace
                    }

                    val file get() = orderFile.file

                    fun enterViewMode() {
                        itemPlace.setContent(renderView())
                    }

                    fun renderView(initiallyTransparent: Boolean = false): ElementBuilder {
                        return when (world.user.kind) {
                            UserKind.CUSTOMER -> {
                                kdiv(id = viewRootID,
                                     className = css.item,
                                     opacity = if (initiallyTransparent) 0.0
                                               else 1.0){o->
                                    o- row{o->
                                        o- renderFileTitle(editing = false)
                                    }
                                    o- row{o->
                                        o- kdiv(className = "col-md-3"){o->
                                            o- label(t("Created", "Создан"))
                                            o- kdiv(){o->
                                                o- formatUnixTime(orderFile.insertedAt)
                                            }
                                        }
                                        o- kdiv(className = "col-md-3"){o->
                                            o- label(t("Updated", "Изменен"))
                                            o- kdiv(){o->
                                                o- formatUnixTime(orderFile.updatedAt)
                                            }
                                        }
                                        o- kdiv(className = "col-md-3"){o->
                                            o- label(t("File name", "Имя файла"))
                                            o- kdiv(){o->
                                                o- highlightedShit(file.name, file.nameHighlightRanges, tag = "span")
                                            }
                                        }
                                        o- kdiv(className = "col-md-3"){o->
                                            o- label(t("Size", "Размер"))
                                            o- kdiv(){o->
                                                o- formatFileSizeApprox(Globus.lang, file.sizeBytes)
                                            }
                                        }
                                    }
                                    o- row{o->
                                        o- kdiv(className = "col-md-12"){o->
                                            o- label(t("Details", "Детали"))
                                            o- kdiv(whiteSpace = "pre-wrap"){o->
                                                o- highlightedShit(file.details, file.detailsHighlightRanges)
                    //                                                o- file.details
                                            }
                                        }
                                    }
                                }
                            }

                            UserKind.WRITER -> imf()

                            UserKind.ADMIN -> imf()
                        }
                    }

                    fun enterEditMode(): Promisoid<Unit> = async {
                        val topShitID = puid()
                        itemPlace.setContent(
                            when (world.user.kind) {
                                UserKind.CUSTOMER -> {
                                    kdiv(id = topShitID, className = css.item){o->
                                        o- row{o->
                                            o- renderFileTitle(editing = true)
                                            o- kdiv(className = "col-md-12", marginTop = -1){o->
                                                o- FormMatumba(FormSpec<CustomerEditUAOrderFileRequest, EditUAOrderFileRequestBase.Response>(
                                                    CustomerEditUAOrderFileRequest()-{o->
                                                        o.fieldInstanceKeySuffix = "-${orderFile.id}"
                                                        o.orderFileID.value = orderFile.id
                                                        o.file.content = FileField.Content.ExistingFile(orderFile.file.name, orderFile.file.sizeBytes)
                                                        o.title.value = orderFile.file.title
                                                        o.details.value = orderFile.file.details
                                                    },
                                                    world,
                                                    cancelButtonTitle = const.defaultCancelButtonTitle,
                                                    containerClassName = css.cunt.bodyEditing,
                                                    onCancela = {async{
                                                        await(await(effects).fadeOut(topShitID))
                                                        enterViewMode()
                                                    }},
                                                    onSuccessa = {res-> async<Unit> {
                                                        orderFile = res.updatedOrderFile

                                                        if (moveItemToTopOnEdit) {
                                                            itemPlace.setContent(NOTRE)
                                                            itemPlace = Placeholder(renderView(initiallyTransparent = true))

                                                            await(scrollBodyGradually(0.0))
                                                            val newTopPlace = Placeholder()
                                                            topPlace.setContent(kdiv{o->
                                                                o- newTopPlace
                                                                o- itemPlace
                                                            })
                                                            topPlace = newTopPlace
                                                            await(await(effects).fadeIn(viewRootID))
                                                        } else {
                                                            enterViewMode()
                                                        }
                                                    }}
                                                ))
                                            }
                                        }
                                    }
                                }

                                UserKind.WRITER -> imf()

                                UserKind.ADMIN -> imf()
                            })

                        await(scrollBodyToShitGradually(dontScrollToTopItem = true){byid(topShitID)})
                    }

                    fun enterVanishedMode() = async {
                        await(await(effects).fadeOut(viewRootID))
                        itemPlace.setContent(NOTRE)
                        TestGlobal.shitVanished.resolve()
                    }

                    fun renderFileTitle(editing: Boolean): ElementBuilder {
                        return kdiv(className = "col-md-12"){o->
                            o- kdiv(className = if (editing) css.cunt.header.editing else css.cunt.header.viewing){o->
                                o- ki(className = "${if (editing) css.cunt.header.leftIcon.editing else css.cunt.header.leftIcon.viewing} ${fa.file}")
                                o- ki(className = "${if (editing) css.cunt.header.leftOverlayBottomLeftIcon.editing else css.cunt.header.leftOverlayBottomLeftIcon.viewing} " +
                                    when (orderFile.seenAsFrom) {
                                        UserKind.CUSTOMER -> fa.user
                                        UserKind.WRITER -> fa.pencil
                                        UserKind.ADMIN -> fa.cog
                                    })
                                o- " "
                                o- highlightedShit(file.title, file.titleHighlightRanges, tag = "span")

                                val idColor: Color?; val idBackground: Color?
                                if (urlQuery.search.split(Regex("\\s+")).contains(orderFile.id)) {
                                    idColor = Color.GRAY_800
                                    idBackground = Color.AMBER_200
                                } else {
                                    idColor = Color.GRAY_500
                                    idBackground = null
                                }
                                o- kspan(marginLeft = "0.5em", fontSize = "75%", color = idColor, backgroundColor = idBackground){o->
                                    o- "$numberSign${orderFile.id}"
                                }

                                o- kspan(marginLeft = "0.5em", fontSize = "75%", color = Color.GRAY_500){o->
                                    o- when (orderFile.seenAsFrom) {
                                        world.user.kind -> t("Mine", "Мой")
                                        UserKind.CUSTOMER -> t("From customer", "От заказчика")
                                        UserKind.WRITER -> t("From writer", "От писателя")
                                        UserKind.ADMIN -> t("From support", "От саппорта")
                                    }
                                }

                                if (!editing) {
                                    o- hor3(style = Style(position = "absolute", right = 0, top = 0, marginRight = "0.5rem", marginTop = "0.1rem")) {o->
                                        o- kic("download-${orderFile.id}", className = "${css.cunt.header.rightIcon} ${fa.cloudDownload}", style = Style(marginTop = "0.45rem"),
                                               onClick = {
                                                   val iframeID = puid()
                                                   jq("body").append("<iframe id='$iframeID' style='display: none;'></iframe>")
                                                   val iframe = byid0(iframeID) as HTMLIFrameElement
                                                   aps.gloshit.iframe = iframe
                                                   iframe.onload = {
                                                       iframe.contentWindow?.postMessage(const.windowMessage.whatsUp, "*")
                                                   }
                                                   iframe.src = "$backendURL/file?fileID=${file.id}&databaseID=${ExternalGlobus.DB}&token=${world.tokenMaybe}"
                                               })
                                        if (orderFile.editable) {
                                            o- kic("delete-${orderFile.id}", className = "${css.cunt.header.rightIcon} ${fa.trash}", style = Style(),
                                                   onClicka = {async{
                                                       if (await(modalConfirmAndPerformDeletion(
                                                           t("TOTE", "Удаляю файл $numberSign${orderFile.id}: ${orderFile.file.title}"),
                                                           DeleteUAOrderFileRequest()-{o->
                                                               o.id.value = orderFile.id
                                                           }))) {
                                                           enterVanishedMode()
                                                       }
                                                   }})
                                            o- kic("edit-${orderFile.id}", className = "${css.cunt.header.rightIcon} ${fa.pencil}", style = Style(),
                                                   onClicka = {
                                                       enterEditMode()
                                                   })
                                        }
                                    }
                                }
                            }
                        }
                    }

                    fun label(title: String) = klabel(marginBottom = 0) {it - title}

                    fun row(build: (ElementBuilder) -> Unit) =
                        kdiv(className = "row", marginBottom = "0.5em"){o->
                            build(o)
                        }
                }
            }

            meat.moreFromID?.let {moreFromID ->
                moreFromID
                val placeholder = Placeholder()
                placeholder.setContent(kdiv(width = "100%", margin = "1em auto 1em auto"){o->
                    val btn = Button("loadMore", title = t("Load more", "Загрузить еще"), className = "btn btn-default", style = Style(width = "100%", backgroundColor = Color.BLUE_GRAY_50))
                    btn.onClicka = {
                        async {
                            await(effects).blinkOn(byid(btn.elementID))
                            try {
                                val res = try {
                                    await(requestChunk(meat.moreFromID))
                                } catch(e: Exception) {
                                    openErrorModal(const.msg.serviceFuckedUp)
                                    null
                                }

                                if (res != null) {
                                    exhaustive / when (res) {
                                        is ZimbabweResponse.Shitty -> openErrorModal(res.error)
                                        is ZimbabweResponse.Hunky -> {
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
                                await(effects).blinkOff()
                                responseProcessed()
                            }
                        }
                    }
                    o- btn
                })
                o- placeholder
            }
        }
    }
}



