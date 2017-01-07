@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import aps.front.frontSymbols.numberSign
import into.kommon.*
import jquery.jq
import org.w3c.dom.HTMLIFrameElement
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.window

class CustomerSingleUAOrderPage(val world: World) {
    class URLQuery {
        var id: String? = null
        var tab: String? = null
    }

    fun load(): Promise<Unit> = async {
        val urlQuery = typeSafeURLQuery(world){URLQuery()}
        val orderID = urlQuery.id.nullifyBlank() ?: return@async world.setShittyParamsPage()
        val tabID = urlQuery.tab ?: "params"

        val res = await(send(world.token, LoadUAOrderRequest()-{o->
            o.id.value = orderID
        }))
        val order = when (res) {
            is ZimbabweResponse.Shitty -> return@async world.setShittyResponsePage(res)
            is ZimbabweResponse.Hunky -> res.meat.order
        }

        val tabs = listOf(
            ParamsTab(world, order),
            FilesTab(world, order),
            MessagesTab(order)
        )
        val tab = tabs.find {it.tabSpec.id == tabID} ?: tabs.first()

        val error = await(tab.load())
        error?.let {return@async world.setShittyResponsePage(it)}

        world.setPage(Page(
            header = pageHeader3(kdiv{o->
                o- t("TOTE", "Заказ $numberSign${order.id}")
                o- kspan(backgroundColor = order.state.labelBackground,
                         fontSize = "60%",
                         padding = "0.1em 0.3em",
                         borderRadius = "0.3em",
                         marginLeft = "1em",
                         position = "relative",
                         top = "-0.2em"){o->
                    o- order.state.title.replace(" ", symbols.nbsp)
                }
            }),

            body = kdiv{o->
                o- h4(marginBottom = "0.7em"){o->
                    o- order.title
                }

                o- Tabs2(
                    initialActiveID = tab.tabSpec.id,
                    switchOnTabClick = false,
                    tabDomIdPrefix = "tab-",

                    onTabClicka = {id-> async {
                        effects2.blinkOn(byid("tab-$id"), widthCalcSuffix = "- 0.15em")
                        try {
                            await(world.pushNavigate("order.html?id=$orderID&tab=$id"))
                        } finally {
                            effects2.blinkOffFadingOut()
                        }
                    }},

                    tabs = tabs.map {it.tabSpec}
                )
            }
        ))
    }

    class FilesTabURLQuery {
        var ordering: String? = null
        var filter: String? = null
        var search: String? = null
    }

    inner class FilesTab(val world: World, val order: UAOrderRTO) : FuckingTab {
        lateinit var ordering: Ordering
        lateinit var filter: CustomerFileFilter
        lateinit var search: String
        lateinit var meat: ItemsResponse<UAOrderFileRTO>
        lateinit var content: ToReactElementable
        lateinit var stripContent: Control2
        lateinit var plusFormContainer: Control2
        var chunksLoaded = 0

        override val tabSpec = TabSpec("files", t("TOTE", "Файлы"),
                                       ToReactElementable.from{content},
                                       ToReactElementable.from{stripContent})

        override fun load(): Promise<ZimbabweResponse.Shitty<*>?> = async {
            val filesTabURLQuery = typeSafeURLQuery(world){FilesTabURLQuery()}
            ordering = relaxedStringToEnum(filesTabURLQuery.ordering, Ordering.values(), default = Ordering.DESC)
            filter = relaxedStringToEnum(filesTabURLQuery.filter, CustomerFileFilter.values(), default = CustomerFileFilter.ALL)
            search = (filesTabURLQuery.search ?: "").trim()

            val res = await(requestChunk(null))
            when (res) {
                is ZimbabweResponse.Shitty -> res
                is ZimbabweResponse.Hunky -> {
                    meat = res.meat
                    stripContent = StripContent()

                    plusFormContainer = Control2.from {kdiv{o->
                        o- ebafPlus.renderForm()
                    }}

                    content = ToReactElementable.from {kdiv{o->
                        o- plusFormContainer
                        o- renderItems(meat, noItemsMessage = true, chunkIndex = chunksLoaded - 1)
                    }}

                    null
                }
            }
        }

        private fun requestChunk(fromID: String?): Promise<ZimbabweResponse<ItemsResponse<UAOrderFileRTO>>> = async {
            val res = await(sendCustomerGetUAOrderFiles(world.token, ItemsRequest(CustomerFileFilter.values())-{o->
                o.entityID.value = order.id
                o.filter.value = filter
                o.ordering.value = ordering
                o.searchString.value = search
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
            ebafHost, "plus", Button.Level.PRIMARY, fa.plus,
            formSpec = FormSpec<CustomerAddUAOrderFileRequest, AddUAOrderFileRequestBase.Response>(
                CustomerAddUAOrderFileRequest(), world,
                primaryButtonTitle = t("TOTE", "Добавить"),
                cancelButtonTitle = const.defaultCancelButtonTitle
            ),
            onSuccessa = {res->
                world.pushNavigate("order.html?id=${order.id}&tab=files")
            }
        )

        inner class StripContent : Control2(Attrs()) {
            val filterSelect = Select(
                key = "filter",
                values = CustomerFileFilter.values(),
                initialValue = filter,
                isAction = true,
                style = json("width" to 160),
                volatileDisabled = {ebafHost.headerControlsDisabled}
            )

            val orderingSelect = Select(
                key = "ordering",
                values = Ordering.values(),
                initialValue = ordering,
                isAction = true,
                style = json("width" to 160),
                volatileDisabled = {ebafHost.headerControlsDisabled}
            )

            val searchInput = Input(
                key = "search",
                style = Style(paddingLeft = 30, width = "100%"),
                placeholder = t("TOTE", "Поиск..."),
                volatileDisabled  = {ebafHost.headerControlsDisabled}
            )

            init {
                searchInput.setValue(search)

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
                    o- ebafPlus.renderButton()
                }
            }

            fun reloadFilesTab() =
                world.pushNavigate("order.html?id=${order.id}&tab=files"
                                       + "&ordering=${orderingSelect.value.name}"
                                       + "&filter=${filterSelect.value.name}"
                                       + "&search=${encodeURIComponent(searchInput.getValue())}")

            fun reload(elementID: String): Promise<Unit> = async {
                effects2.blinkOn(byid(elementID))
                ebafHost.headerControlsDisabled = true
                stripContent.update()
                try {
                    await(reloadFilesTab())
                } finally {
                    effects2.blinkOffFadingOut()
                    ebafHost.headerControlsDisabled = false
                    stripContent.update()
                }
            }
        }

        private fun renderItems(meat: ItemsResponse<UAOrderFileRTO>, noItemsMessage: Boolean, chunkIndex: Int): ToReactElementable {
            if (meat.items.isEmpty()) {
                return if (noItemsMessage) span(const.msg.noItems)
                else NOTRE
            }

            return kdiv{o->
                for ((fileIndex, _orderFile) in meat.items.withIndex()) {
                    object {
                        val holder = Placeholder()
                        var orderFile = _orderFile

                        init {
                            enterViewMode()
                            o- holder
                        }

                        val file get() = orderFile.file

                        fun enterViewMode() {
                            holder.setContent(when (world.user.kind) {
                                UserKind.CUSTOMER -> {
                                    kdiv{o->
                                        o- row{o->
                                            o- renderFileTitle(editing = false)
                                        }
                                        o- row{o->
                                            o- kdiv(className = "col-md-4"){o->
                                                o- label(t("TOTE", "Создан"))
                                                o- kdiv(){o->
                                                    o- formatUnixTime(file.insertedAt)
                                                }
                                            }
                                            o- kdiv(className = "col-md-4"){o->
                                                o- label(t("TOTE", "Имя файла"))
                                                o- kdiv(){o->
                                                    o- highlightedShit(file.name, file.nameHighlightRanges, tag = "span")
                                                }
                                            }
                                            o- kdiv(className = "col-md-4"){o->
                                                o- label(t("TOTE", "Размер"))
                                                o- kdiv(){o->
                                                    o- formatFileSizeApprox(Globus.lang, file.sizeBytes)
                                                }
                                            }
                                        }
                                        o- row {o->
                                            o- kdiv(className = "col-md-12"){o->
                                                o- label(t("TOTE", "Детали"))
                                                o- kdiv(whiteSpace = "pre-wrap"){o->
                                                    o- highlightedShit(file.details, file.detailsHighlightRanges)
//                                            o- file.details
                                                }
                                            }
                                        }
                                    }
                                }

                                UserKind.WRITER -> imf()

                                UserKind.ADMIN -> imf()
                            })
                        }

                        fun enterEditMode(): Promise<Unit> = async {
                            val topShitID = puid()
                            holder.setContent(when (world.user.kind) {
                                UserKind.CUSTOMER -> {
                                    kdiv(id = topShitID){o->
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
                                                    onCancel = {
                                                        enterViewMode()
                                                    },
                                                    onSuccess = {res->
                                                        orderFile = res.updatedOrderFile
                                                        enterViewMode()
                                                    }
                                                ))
                                            }
                                        }
                                    }
                                }

                                UserKind.WRITER -> imf()

                                UserKind.ADMIN -> imf()
                            })

                            await(scrollBodyToShitGradually(dy = -10){byid(topShitID)})
                        }

                        fun enterVanishedMode() {
                            holder.setContent(NOTRE)
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
                                    if (search.split(Regex("\\s+")).contains(orderFile.id)) {
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
                                            world.user.kind -> t("TOTE", "Мой")
                                            UserKind.CUSTOMER -> t("TOTE", "От заказчика")
                                            UserKind.WRITER -> t("TOTE", "От писателя")
                                            UserKind.ADMIN -> t("TOTE", "От саппорта")
                                        }
                                    }

                                    if (!editing) {
                                        o- kic("download-${orderFile.id}", className = "${css.cunt.header.rightIcon} ${fa.cloudDownload}", style = Style(right = "6.3rem", top = "0.5rem"),
                                               onClick = {
                                                   val iframeID = puid()
                                                   jq("body").append("<iframe id='$iframeID' style='display: none;'></iframe>")
                                                   val iframe = byid0(iframeID) as HTMLIFrameElement
                                                   gloshit.iframe = iframe
                                                   iframe.onload = {
                                                       iframe.contentWindow?.postMessage(const.windowMessage.whatsUp, "*")
                                                   }
                                                   iframe.src = "$backendURL/file?fileID=${file.id}&databaseID=${ExternalGlobus.DB}&token=${world.tokenMaybe}"
                                               })
                                        o- kic("delete-${orderFile.id}", className = "${css.cunt.header.rightIcon} ${fa.trash}", style = Style(right = "3.3rem"),
                                               onClicka = {async{
                                                   if (await(modalConfirmAndPerformDeletion(
                                                           t("TOTE", "Удаляю файл $numberSign${orderFile.id}: ${orderFile.file.title}"),
                                                           DeleteUAOrderFileRequest()-{o->
                                                               o.id.value = orderFile.id
                                                           }))) {
                                                       enterVanishedMode()
                                                   }
                                               }})
                                        o- kic("edit-${orderFile.id}", className = "${css.cunt.header.rightIcon} ${fa.pencil}", style = Style(right = "0.3rem"),
                                               onClicka = {
                                                   enterEditMode()
                                               })
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
                        val btn = Button("loadMore", title = t("TOTE", "Загрузить еще"), className = "btn btn-default", style = Style(width = "100%", backgroundColor = Color.BLUE_GRAY_50))
                        btn.onClicka = {
                            async {
                                effects2.blinkOn(byid(btn.elementID))
                                try {
                                    val res = await(requestChunk(meat.moreFromID))
                                    exhaustive / when (res) {
                                        is ZimbabweResponse.Shitty -> openErrorModal(res.error)
                                        is ZimbabweResponse.Hunky -> placeholder.setContent(renderItems(res.meat, noItemsMessage = false, chunkIndex = chunksLoaded - 1))
                                    }
                                } catch (e: Throwable) {
                                    openErrorModal(const.msg.serviceFuckedUp)
                                } finally {
                                    effects2.blinkOff()
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


}

private interface FuckingTab {
    val tabSpec: TabSpec
    fun load(): Promise<ZimbabweResponse.Shitty<*>?>
}

private class ParamsTab(val world: World, val order: UAOrderRTO) : FuckingTab {
    override fun load(): Promise<ZimbabweResponse.Shitty<*>?> = async {
        null
    }

    private val content = kdiv{o->
        fun label(title: String) = klabel(marginBottom = 0) {it-title}

        fun row(build: (ElementBuilder) -> Unit) =
            kdiv(className = "row", marginBottom = "0.5em"){o->
                build(o)
            }

        exhaustive/when (world.user.kind) {
            UserKind.CUSTOMER -> {
                o- row{o->
                    o- kdiv(className = "col-md-4"){o->
                        o- label(t("TOTE", "Создан"))
                        o- kdiv(){o->
                            o- formatUnixTime(order.insertedAt)
                        }
                    }
                    o- kdiv(className = "col-md-4"){o->
                        o- label(t("TOTE", "Срок"))
                        o- kdiv(){o->
                            o- formatUnixTime(order.deadline)
                        }
                    }
                }
                o- row{o->
                    o- kdiv(className = "col-md-4"){o->
                        o- label(t("TOTE", "Тип документа"))
                        o- kdiv(){o->
                            o- order.documentType.title
                        }
                    }
                    o- kdiv(className = "col-md-4"){o->
                        o- label(t("TOTE", "Страниц"))
                        o- kdiv(){o->
                            o- order.numPages.toString()
                        }
                    }
                    o- kdiv(className = "col-md-4"){o->
                        o- label(t("TOTE", "Источников"))
                        o- kdiv(){o->
                            o- order.numSource.toString()
                        }
                    }
                }
                order.price?.let {
                    o- kdiv(){o->
                        o- formatUAH(it)
                    }
                }
                o- row{o->
                    o- kdiv(className = "col-md-12"){o->
                        o- label(t("TOTE", "Детали"))
                        o- kdiv(whiteSpace = "pre-wrap"){o->
                            o- order.details
                        }
                    }
                }
            }

            UserKind.WRITER -> imf()

            UserKind.ADMIN -> imf()
        }
    }

    override val tabSpec = TabSpec("params", t("TOTE", "Параметры"), content)
}


private class MessagesTab(val order: UAOrderRTO) : FuckingTab {
    override fun load(): Promise<ZimbabweResponse.Shitty<*>?> = async {
        throw UnsupportedOperationException("Implement me, please, fuck you")
    }

    private val content = kdiv{o->
        o- "fucking messages"
    }

    override val tabSpec = TabSpec("messages", t("TOTE", "Сообщения"), content)
}















