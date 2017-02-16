package aps.front

import aps.*
import aps.front.frontSymbols.numberSign
import into.kommon.*
import kotlin.js.json
import kotlin.properties.Delegates.notNull

// TODO:vgrechka Kill plusFormContainer and related shit

class TestDownloadContext {
    val downloadStartedLock by notNullNamed(TestLock(virgin = true))
    val bitsReceivedLock by notNullNamed(TestLock(virgin = true))
    var shit by notNull<DownloadFileResponse>()
}

class UACustomerSingleOrderPageFilesTab(val page: UACustomerSingleOrderPage, val world: World, val order: UAOrderRTO) : CustomerSingleUAOrderPageTab {
    lateinit var meat: ItemsResponse<UAOrderFileRTO>
    lateinit var content: ToReactElementable
    lateinit var stripContent: Control2
    lateinit var plusFormContainer: Control2
    var chunksLoaded = 0

    object urlQuery : URLQueryParamsMarker {
        val ordering by EnumURLParam(Ordering.values(), default = Ordering.DESC)
        val filter by EnumURLParam(CustomerFileFilter.values(), default = CustomerFileFilter.ALL)
        val search by StringURLParam("")
    }

    override val tabSpec = TabSpec(tabs.order.files, t("Files", "Файлы"),
                                   ToReactElementable.from{content},
                                   ToReactElementable.from{stripContent})

    override suspend fun load(): FormResponse2.Shitty<*>? {
        val res = requestChunk(null)
        return when (res) {
            is FormResponse2.Shitty -> res
            is FormResponse2.Hunky -> {
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

    private suspend fun requestChunk(fromID: Long?): FormResponse2<ItemsResponse<UAOrderFileRTO>> {
        val res = sendUACustomerGetOrderFiles(ItemsRequest(CustomerFileFilter.values())-{o->
            o.entityID.value = order.id
            o.filter.value = urlQuery.filter.get()
            o.ordering.value = urlQuery.ordering.get()
            o.searchString.value = urlQuery.search.get()
            o.fromID.value = fromID
        })
        ++chunksLoaded
        return res
    }

    val ebafHost = EBAFHost()
    inner class EBAFHost : EvaporatingButtonAndFormHost {
        override var showEmptyLabel = true
        override var cancelForm = {}
        override var headerControlsDisabled = false
        override var headerControlsVisible = true
        override var headerControlsClass = ""
        override var headerMode = HeaderMode.BROWSING

        override fun updateShit() {
            stripContent.update()
            plusFormContainer.update()
        }
    }

    val ebafPlus = EvaporatingButtonAndForm(
        host = ebafHost, key = buttons.plus, level = Button.Level.PRIMARY, icon = fa.plus,
        formSpec = FormSpec<UACreateOrderFileRequest, UACreateOrderFileRequest.Response>(
            req = UACreateOrderFileRequest()-{o->
                o.orderID.value = order.id
            },
            ui = world,
            primaryButtonTitle = t("Add", "Добавить"),
            cancelButtonTitle = const.text.shebang.defaultCancelButtonTitle
        ),
        onSuccessa = {
            val q = UACustomerSingleOrderPage.urlQuery
            world.pushNavigate(makeURL(pages.uaCustomer.order, listOf(
                URLParamValue(q.id, order.id.toString()),
                URLParamValue(q.tab, simpleName(tabs.order.files.fqn))
            )))
        }
    )

    inner class StripContent : Control2(Attrs()) {
        val filterSelect = Select(
            key = selects.filter,
            values = CustomerFileFilter.values(),
            initialValue = urlQuery.filter.get(),
            isAction = true,
            style = json("width" to 160),
            volatileDisabled = {ebafHost.headerControlsDisabled}
        )

        val orderingSelect = Select(
            key = selects.ordering,
            values = Ordering.values(),
            initialValue = urlQuery.ordering.get(),
            isAction = true,
            style = json("width" to 160),
            volatileDisabled = {ebafHost.headerControlsDisabled}
        )

        val searchInput = Input(
            key = inputs.search,
            style = Style(paddingLeft = 30, width = "100%"),
            placeholder = t("Search...", "Поиск..."),
            volatileDisabled  = {ebafHost.headerControlsDisabled}
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

//                gloshit.updateStripContent = {stripContent.update()}
        }

        override fun render(): ToReactElementable {
            if (!ebafHost.headerControlsVisible) return NOTRE
            return hor2{o->
                o- kdiv(position = "relative"){o->
                    o- searchInput
                    o- ki(className = "${fa.search}", position = "absolute", left = 10, top = 10, color = Color.GRAY_500)
                }
                val showFilter = when (order.state) {
                    UAOrderState.CUSTOMER_DRAFT, UAOrderState.WAITING_ADMIN_APPROVAL -> false
                    else -> true
                }
                if (showFilter) {
                    o- filterSelect
                }
                o- orderingSelect

                val refreshButtonID = puid()
                o- Button(id = refreshButtonID, icon = fa.refresh, volatileDisabled = {ebafHost.headerControlsDisabled}, key = buttons.refreshPage) {
                    asu {reload(refreshButtonID)}
                }

                if (order.state == UAOrderState.CUSTOMER_DRAFT) {
                    o- Button(icon = fa.plus, level = Button.Level.PRIMARY, key = buttons.plus) {
                        openEditModal(
                            title = t("TOTE", "Новый файл"),
                            formSpec = FormSpec<UACreateOrderFileRequest, UACreateOrderFileRequest.Response>(
                                ui = world,
                                req = UACreateOrderFileRequest()-{o->
                                    o.orderID.value = order.id
                                }
                            ),
                            onSuccessa = {
                                val q = UACustomerSingleOrderPage.urlQuery
                                world.replaceNavigate(makeURL(pages.uaCustomer.order, listOf(
                                    URLParamValue(q.id, order.id.toString()),
                                    URLParamValue(q.tab, simpleName(tabs.order.files.fqn))
                                )))
                            }
                        )
                    }
                }
            }
        }

        // TODO:vgrechka Use safe page and param refs    96996eef-306a-4b0b-b5a2-60f49b5dee06
        suspend fun reloadFilesTab() {
            val qPage = UACustomerSingleOrderPage.urlQuery
            val qTab = UACustomerSingleOrderPageFilesTab.urlQuery
            world.pushNavigate(makeURL(pages.uaCustomer.order, listOf(
                URLParamValue(qPage.id, order.id.toString()),
                URLParamValue(qPage.tab, simpleName(tabs.order.files.fqn)),
                URLParamValue(qTab.ordering, orderingSelect.value),
                URLParamValue(qTab.filter, filterSelect.value),
                URLParamValue(qTab.search, encodeURIComponent(searchInput.getValue()))
            )))
        }

        suspend fun reload(elementID: String) {
            await(effects).blinkOn(byid(elementID))
            ebafHost.headerControlsDisabled = true
            stripContent.update()
            TestGlobal.shitHalfwayLock.resumeTestAndPauseSutFromSut()
            try {
                reloadFilesTab()
            } finally {
                ebafHost.headerControlsDisabled = false
                stripContent.update() // TODO:vgrechka Redundant?
                TestGlobal.shitDoneLock.resumeTestFromSut()
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


    inner class Pizda(initialOrderFile: UAOrderFileRTO, o: ElementBuilder) {
        var itemPlace = Placeholder()
        var orderFile = initialOrderFile
        val viewRootID = puid()
        val cloudIconID = puid()
        val titleRightPlace = Placeholder(renderTitleControls())

        init {
            enterViewMode()
            o- itemPlace
        }

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
                                    o- formatUnixTime(orderFile.createdAt)
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
                                    o- highlightedShit(orderFile.name, orderFile.nameHighlightRanges, tag = "span")
                                }
                            }
                            o- kdiv(className = "col-md-3"){o->
                                o- label(t("Size", "Размер"))
                                o- kdiv(){o->
                                    o- formatFileSizeApprox(Globus.lang, orderFile.sizeBytes)
                                }
                            }
                        }
                        o- row{o->
                            o- kdiv(className = "col-md-12"){o->
                                o- label(t("Details", "Детали"))
                                o- kdiv(whiteSpace = "pre-wrap"){o->
                                    o- highlightedShit(orderFile.details, orderFile.detailsHighlightRanges)
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
                    o- highlightedShit(orderFile.title, orderFile.titleHighlightRanges, tag = "span")

                    val idColor: Color?; val idBackground: Color?
                    if (urlQuery.search.get().split(Regex("\\s+")).contains(orderFile.id.toString())) {
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

                    o- titleRightPlace
                }
            }
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
                o- kic("${fa.cloudDownload} $cloudClass", id = cloudIconID, style = Style(marginTop = "0.45rem"), key = SubscriptKicKey(kics.order.file.download, orderFile.id), onClicka = ifNotDownloading {onDownload()})
                if (orderFile.editable) {
                    o- kic("${fa.trash} $trashClass", style = Style(), key = SubscriptKicKey(kics.order.file.delete, orderFile.id), onClicka = ifNotDownloading {onDelete()})
                    o- kic("${fa.pencil} $pencilClass", style = Style(), key = SubscriptKicKey(kics.order.file.edit, orderFile.id), onClicka = ifNotDownloading {onEdit()})
                }
            }
        }

        private suspend fun onDownload() {
            titleRightPlace.setContent(renderTitleControls(downloadActive = true))
            val blinker = await(effects).blinkOn(byid(cloudIconID), BlinkOpts())

            val testCtx = TestGlobal.orderFileIDToDownloadContext[orderFile.id]
            testCtx?.downloadStartedLock?.resumeTestAndPauseSutFromSut()

            val res = send(UADownloadOrderFileRequest()-{o->
                o.fileID.value = orderFile.id
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

        private suspend fun onDelete() {
            if (modalConfirmAndDelete(t("TOTE", "Удаляю файл $numberSign${orderFile.id}: ${orderFile.title}"),
                                      UADeleteOrderFileRequest()-{o->
                                          o.id.value = orderFile.id
                                      })) {
                await(effects).fadeOut(viewRootID)
                itemPlace.setContent(NOTRE)
                TestGlobal.shitVanished.resumeTestAndPauseSutFromSut()
            }
        }

        fun label(title: String) = klabel(marginBottom = 0) {it - title}

        fun row(build: (ElementBuilder) -> Unit) =
            kdiv(className = "row", marginBottom = "0.5em"){o->
                build(o)
            }

        private suspend fun onEdit() {
            openEditModal(
                title = t("TOTE", "Файл") + " " + numberSign + orderFile.id,
                formSpec = FormSpec<UAUpdateOrderFileRequest, UAUpdateOrderFileRequest.Response>(
                    ui = world,
                    req = UAUpdateOrderFileRequest()-{o->
                        o.fileID.value = orderFile.id
                        o.file.content = FileField.Content.Unchanged(orderFile.name, orderFile.sizeBytes)
                        o.fields1-{o->
                            o.title.value = orderFile.title
                            o.details.value = orderFile.details
                        }
                    }
                ),
                onSuccessa = {res->
                    orderFile = res.file
                    enterViewMode()
                }
            )
        }
    }
}




















