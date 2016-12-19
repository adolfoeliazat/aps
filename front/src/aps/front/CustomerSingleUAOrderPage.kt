@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*
import jquery.jq
import org.w3c.dom.HTMLIFrameElement
import kotlin.browser.window

class CustomerSingleUAOrderPage(val world: World) {
    class URLQuery {
        var id: String? = null
        var tab: String? = null
        var ordering: String? = null
    }

    lateinit var ordering: Ordering

    fun load(): Promise<Unit> = async {
        val urlQuery = typeSafeURLQuery(world){URLQuery()}
        val orderID = urlQuery.id.nullifyBlank() ?: return@async world.setShittyParamsPage()
        val tabID = urlQuery.tab ?: "params"
        ordering = relaxedStringToEnum(urlQuery.ordering, Ordering.values(), default = Ordering.DESC)

        val res = await(send(world.tokenSure, LoadUAOrderRequest()-{o->
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
                    o- order.state.title.replace(" ", nbsp)
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


    inner class FilesTab(val world: World, val order: UAOrderRTO) : FuckingTab {
        lateinit var meat: ItemsResponse<UAOrderFileRTO>

        override fun load(): Promise<ZimbabweResponse.Shitty<*>?> = async {
            val res = await(requestChunk(null))
            when (res) {
                is ZimbabweResponse.Shitty -> res
                is ZimbabweResponse.Hunky -> {
                    meat = res.meat
                    null
                }
            }
        }

        private fun requestChunk(fromID: String?): Promise<ZimbabweResponse<ItemsResponse<UAOrderFileRTO>>> {
            return sendCustomerGetUAOrderFiles(world.tokenSure, ItemsRequest(FileFilter.values())-{o->
                o.entityID.value = order.id
                o.filter.value = FileFilter.ALL
                o.ordering.value = ordering
                o.searchString.value = ""
                o.fromID.value = fromID
            })
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
            ebafHost, "plus", Button.Level.PRIMARY, "plus",
            formSpec = FormSpec<CustomerAddUAOrderFileRequest, AddUAOrderFileRequestBase.Response>(
                CustomerAddUAOrderFileRequest(), world,
                primaryButtonTitle = t("TOTE", "Добавить"),
                cancelButtonTitle = defaultCancelButtonTitle
            ),
            onSuccessa = {res->
                world.pushNavigate("order.html?id=${order.id}&tab=files")
            }
        )

        val stripContent = Control2.from {renderStripContent()}
        fun renderStripContent(): ToReactElementable {
            if (!ebafHost.headerControlsVisible) return NOTRE
            return hor2{o->
                val orderingSelect = Select(
                    Attrs(),
                    Ordering.values(),
                    ordering,
                    isAction = true,
                    style = json("width" to 160),
                    volatileDisabled = {ebafHost.headerControlsDisabled}
                )
                orderingSelect.onChanga = {async{
                    effects2.blinkOn(byid(orderingSelect.elementID))
                    ebafHost.headerControlsDisabled = true
                    stripContent.update()
                    try {
                        await(world.pushNavigate("order.html?id=${order.id}&tab=files&ordering=${orderingSelect.value.name}"))
                    } finally {
                        effects2.blinkOffFadingOut()
                        ebafHost.headerControlsDisabled = false
                        stripContent.update()
                    }
                }}

                o- orderingSelect
                o- ebafPlus.renderButton()
            }
        }

        val plusFormContainer = Control2.from {kdiv{o->
            o- ebafPlus.renderForm()
        }}

        val content = ToReactElementable.from {kdiv{o->
            o- plusFormContainer
            o- renderItems(this.meat, noItemsMessage = true)
        }}

        private fun renderItems(meat: ItemsResponse<UAOrderFileRTO>, noItemsMessage: Boolean): ToReactElementable {
            if (meat.items.isEmpty()) {
                return if (noItemsMessage) span(const.msg.noItems)
                else NOTRE
            }

            return kdiv{o->
                for ((fileIndex, orderFile) in meat.items.withIndex()) {
                    val file = orderFile.file
                    fun label(title: String) = klabel(marginBottom = 0) {it - title}

                    fun row(build: (ElementBuilder) -> Unit) =
                        kdiv(className = "row", marginBottom = "0.5em"){o->
                            build(o)
                        }

                    exhaustive / when (world.user.kind) {
                        UserKind.CUSTOMER -> {
                            o- kdiv {o ->
                                o- row {o ->
                                    o- kdiv(className = "col-md-12"){o->
                                        o- kdiv(className = "cunt-header"){o->
                                            o- ki(className = "cunt-header-left-icon fa fa-file")
                                            o- ki(className = "cunt-header-left-overlay-bottom-left-icon fa " +
                                                when (orderFile.seenAsFrom) {
                                                    UserKind.CUSTOMER -> "fa-user"
                                                    UserKind.WRITER -> "fa-pencil"
                                                    UserKind.ADMIN -> "fa-cog"
                                                })
                                            o- (" " + file.title)
                                            o- kspan(marginLeft = "0.5em", fontSize = "75%", color = Color.GRAY_500){o->
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
                                            o- kic("download-$fileIndex", className = "cunt-header-right-icon fa fa-cloud-download", style = Style(right = 30, top = 6),
                                                   onClick = {
                                                       val iframeID = puid()
                                                       jq("body").append("<iframe id='$iframeID' style='display: none;'></iframe>")
                                                       val iframe = byid0(iframeID) as HTMLIFrameElement
                                                       gloshit.iframe = iframe
                                                       iframe.onload = {
                                                           iframe.contentWindow?.postMessage(const.windowMessage.whatsUp, "*")
                                                       }
                                                       iframe.src = "$backendURL/file?fileID=${file.id}&databaseID=${ExternalGlobus.DB}&token=${world.token}"
                                                   })
                                            o- ki(className = "cunt-header-right-icon fa fa-pencil")
                                        }
                                    }
                                }
                                o- row {o ->
                                    o- kdiv(className = "col-md-4"){o->
                                        o- label(t("TOTE", "Создан"))
                                        o- kdiv(){o->
                                            o- formatUnixTime(file.insertedAt)
                                        }
                                    }
                                    o- kdiv(className = "col-md-4"){o->
                                        o- label(t("TOTE", "Имя файла"))
                                        o- kdiv(){o->
                                            o- file.name
                                        }
                                    }
                                    o- kdiv(className = "col-md-4"){o->
                                        o- label(t("TOTE", "Размер"))
                                        o- kdiv(){o->
                                            o- formatFileSizeApprox(Globus.lang, file.sizeBytes)
                                        }
                                    }
                                }
                                o- row {o ->
                                    o- kdiv(className = "col-md-12"){o->
                                        o- label(t("TOTE", "Детали"))
                                        o- kdiv(whiteSpace = "pre-wrap"){o->
                                            o- file.details
                                        }
                                    }
                                }
                            }
                        }

                        UserKind.WRITER -> imf()

                        UserKind.ADMIN -> imf()
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
                                        is ZimbabweResponse.Hunky -> placeholder.setContent(renderItems(res.meat, noItemsMessage = false))
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

        override val tabSpec = TabSpec("files", t("TOTE", "Файлы"), content, stripContent)
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
















