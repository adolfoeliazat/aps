@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*
import jquery.jq

class CustomerSingleUAOrderPage(val world: World) {
    class URLQuery {
        var id: String? = null
        var tab: String? = null
    }

    fun load(): Promise<Unit> = async {
        val urlQuery = typeSafeURLQuery(world){URLQuery()}
        val orderID = urlQuery.id.nullifyBlank() ?: return@async world.setShittyParamsPage()
        val tabID = urlQuery.tab ?: "params"

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
                o- t("TOTE", "Заказ #${order.id}")
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
                        await(world.pushNavigate("order.html?id=$orderID&tab=$id"))
                        effects2.blinkOffFadingOut()
                    }},

                    tabs = tabs.map {it.tabSpec}
                )
            }
        ))
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

        exhaustive/when (world.userSure.kind) {
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

private class FilesTab(val world: World, val order: UAOrderRTO) : FuckingTab {
    lateinit var meat: ItemsResponse<FileRTO>

    override fun load(): Promise<ZimbabweResponse.Shitty<*>?> = async {
        val res = await(sendGetFiles(world.tokenSure, ItemsRequest(FileFilter.values())-{o->
            o.entityID.value = order.id
            o.filter.value = FileFilter.ALL
            o.ordering.value = Ordering.DESC
            o.searchString.value = ""
//            o.fromID.value = "0"
        }))
        when (res) {
            is ZimbabweResponse.Shitty -> res
            is ZimbabweResponse.Hunky -> {
                meat = res.meat
                null
            }
        }
    }

    val ebafHost = object:EvaporatingButtonAndFormHost {
        override var showEmptyLabel = true
        override var cancelForm = {}
        override var headerControlsDisabled = false
        override var headerControlsVisible = true
        override var headerControlsClass = ""

        override fun updateShit() {
            world.updatePage()
        }
    }

    val ebafPlus = EvaporatingButtonAndForm(
        ebafHost, "plus", Button.Level.PRIMARY, "plus",
        formSpec = FormSpec<CustomerAddUAOrderFileRequest, CustomerAddUAOrderFileRequest.Response>(
            CustomerAddUAOrderFileRequest(), world,
            primaryButtonTitle = t("TOTE", "Добавить"),
            cancelButtonTitle = defaultCancelButtonTitle
        ),
        onSuccessa = {res->
            world.pushNavigate("order.html?id=${order.id}&tab=files")
        }
    )

    val stripContent = object:Control2(Attrs()) {
        override fun render(): ToReactElementable {
            if (!ebafHost.headerControlsVisible) return NOTRE
            return hor2{o->
                o- ebafPlus.renderButton()
            }
        }
    }

    val content = ToReactElementable.from {kdiv{o->
        o- ebafPlus.renderForm()

        if (meat.items.isEmpty()) {
            o- const.noItemsMessage
        } else {
            for (file in meat.items) {
                fun label(title: String) = klabel(marginBottom = 0) {it-title}

                fun row(build: (ElementBuilder) -> Unit) =
                    kdiv(className = "row", marginBottom = "0.5em"){o->
                        build(o)
                    }

                exhaustive/when (world.userSure.kind) {
                    UserKind.CUSTOMER -> {
                        o- kdiv{o->
                            o- row{o->
                                o- kdiv(className = "col-md-12"){o->
                                    o- kdiv(className = "cunt-header"){o->
                                        o- ki(className = "cunt-header-left-icon fa fa-file")
                                        o- (" " + file.title)
                                        o- kic(className = "cunt-header-right-icon fa fa-cloud-download", style = Style(right = 30, top = 6),
                                              onClick = {
                                                  dwarnStriking("pizda")
                                              })
                                        o- ki(className = "cunt-header-right-icon fa fa-pencil")
                                    }
                                }
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
                            o- row{o->
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
        }
    }}

    override val tabSpec = TabSpec("files", t("TOTE", "Файлы"), content, stripContent)
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

















