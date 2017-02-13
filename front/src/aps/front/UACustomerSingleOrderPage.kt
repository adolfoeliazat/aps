@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import aps.const.text.symbols.nbsp
import aps.front.frontSymbols.numberSign
import into.kommon.*

interface CustomerSingleUAOrderPageTab {
    val tabSpec: TabSpec
    fun load(): Promisoid<ZimbabweResponse.Shitty<*>?>
}

class UACustomerSingleOrderPage(val world: World) {
    object urlQuery {
        val id by MaybeStringURLParam()
        val tab by MaybeStringURLParam()
    }

    var orderID by notNullOnce<String>()

    suspend fun load() {
        orderID = urlQuery.id.get(world) ?: return world.setShittyParamsPage()
        val defaultTab = tabs.order.params
        val tabKey = tabs.order.items.findSimplyNamed(urlQuery.tab.get(world)) ?: defaultTab

        val res = await(send(world.token, LoadUAOrderRequest()-{o->
            o.id.value = orderID
        }))
        val order = when (res) {
            is ZimbabweResponse.Shitty -> return world.setShittyResponsePage(res)
            is ZimbabweResponse.Hunky -> res.meat.order
        }

        val tabs = listOf(
            ParamsTab(world, order),
            UACustomerSingleOrderPageFilesTab(this, world, order)/*,
            MessagesTab(order)*/
        )
        val tab = tabs.find {it.tabSpec.key == tabKey} ?: tabs.first()

        val error = await(tab.load())
        error?.let {return world.setShittyResponsePage(it)}

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
                if (order.state == UAOrderState.CUSTOMER_DRAFT) {
                    val c = css.orderPage.customer.draftHint
                    o- kdiv(className = c.container){o->
                        o- kdiv(className = c.message){o->
                            o- t("TOTE", "Проверьте / подредактируйте параметры. Загрузите файлы, если нужно. Затем нажмите...")
                        }
                        o- Button(title = t("TOTE", "Отправить на проверку"), level = Button.Level.PRIMARY, key = buttons.sendForApproval)
                    }
                }
                o- h4(marginBottom = "0.7em"){o->
                    o- order.title
                }

                o- Tabs2(
                    initialActiveKey = tab.tabSpec.key,
                    switchOnTabClick = false,
                    onTabClicka = {clickOnTab(it)},
                    tabs = tabs.map {it.tabSpec}
                )
            }
        ))
    }

    suspend fun clickOnTab(key: TabKey) {
        await(effects).blinkOn(byid(key.fqn), BlinkOpts(dwidth = "-0.15rem"))
        TestGlobal.switchTabHalfwayLock.resumeTestAndPauseSutFromSut()
        try {
            world.pushNavigate("order.html?id=$orderID&tab=${simpleName(key.fqn)}")
        } finally {
            TestGlobal.switchTabDoneLock.resumeTestAndPauseSutFromSut()
        }
    }

}

private class ParamsTab(val world: World, val order: UAOrderRTO) : CustomerSingleUAOrderPageTab {
    override fun load(): Promisoid<ZimbabweResponse.Shitty<*>?> = async {
        null
    }

    private val place = Placeholder(renderView())

    private fun renderView(): ToReactElementable {
        return kdiv{o->
            fun label(title: String) = klabel(marginBottom = 0) {it - title}

            fun row(build: (ElementBuilder) -> Unit) =
                kdiv(className = "row", marginBottom = "0.5em"){o->
                    build(o)
                }

            exhaustive / when (world.user.kind) {
                UserKind.CUSTOMER -> {
                    val option = 2
                    if (option == 1) {
                        o- row{o->
                            o- kdiv(className = "col-md-3"){o->
                                o- label(t("Created", "Создан"))
                                o- kdiv{o->
                                    o- formatUnixTime(order.insertedAt)
                                }
                            }
                            o- kdiv(className = "col-md-3"){o->
                                o- label(fields.shebang.ua.documentType.title)
                                o- kdiv{o->
                                    o- order.documentType.title
                                }
                            }
                            o- kdiv(className = "col-md-3"){o->
                                o- label(fields.shebang.numPages.title)
                                o- kdiv{o->
                                    o- order.numPages.toString()
                                }
                            }
                            o- kdiv(className = "col-md-3"){o->
                                o- label(fields.shebang.numSources.title)
                                o- kdiv{o->
                                    o- order.numSources.toString()
                                }
                            }
                        }
                    } else if (option == 2) {
                        o- row{o->
                            o- kdiv(className = "col-md-3"){o->
                                o- label(t("Created", "Создан"))
                                o- kdiv{o->
                                    o- formatUnixTime(order.insertedAt)
                                }
                            }
                            o- kdiv(className = "col-md-3"){o->
                                o- label(fields.shebang.phone.title)
                                o- div(order.phone)
                            }
                        }
                        o- row{o->
                            o- kdiv(className = "col-md-3"){o->
                                o- label(fields.shebang.ua.documentType.title)
                                o- kdiv{o->
                                    o- order.documentType.title
                                }
                            }
                            o- kdiv(className = "col-md-3"){o->
                                o- label(fields.shebang.numPages.title)
                                o- kdiv{o->
                                    o- order.numPages.toString()
                                }
                            }
                            o- kdiv(className = "col-md-3"){o->
                                o- label(fields.shebang.numSources.title)
                                o- kdiv{o->
                                    o- order.numSources.toString()
                                }
                            }
                        }
                    }
                    o- row{o->
                        o- kdiv(className = "col-md-12"){o->
                            o- label(fields.shebang.orderDetails.title)
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
    }

    override val tabSpec = TabSpec(
        key = tabs.order.params,
        title = t("TOTE", "Параметры"),
        content = place,
        stripContent = kdiv{o->
            when (world.user.kind) {
                UserKind.CUSTOMER -> {
                    if (order.state == UAOrderState.CUSTOMER_DRAFT) {
                        o- Button(icon = fa.pencil, level = Button.Level.DEFAULT, key = buttons.edit) {
                            openEditModal(
                                title = t("TOTE", "Параметры заказа"),
                                formSpec = FormSpec<UACustomerUpdateOrderRequest, UACustomerUpdateOrderRequest.Response>(
                                    ui = world,
                                    req = UACustomerUpdateOrderRequest()-{o->
                                        o.entityID.value = order.id
                                        o.fields1-{o->
                                            o.documentType.value = order.documentType
                                            o.documentTitle.value = order.title
                                            o.numPages.setValue(order.numPages)
                                            o.numSources.setValue(order.numSources)
                                            o.documentDetails.value = order.details
                                        }
                                        o.fields2-{o->
                                            o.phone.value = order.phone
                                        }
                                    }
                                ),
                                onSuccessa = {
                                    world.replaceNavigate(makeURL(pages.uaCustomer.order, listOf(
                                        URLParamValue(UACustomerSingleOrderPage.urlQuery.id, order.id.toString())
                                    )))
                                }
                            )
                        }
                    }
                }
                UserKind.WRITER -> {}
                UserKind.ADMIN -> {}
            }
        }
    )
}


private class MessagesTab(val order: UAOrderRTO) : CustomerSingleUAOrderPageTab {
    override fun load(): Promisoid<ZimbabweResponse.Shitty<*>?> = async {
        throw UnsupportedOperationException("Implement me, please, fuck you")
    }

    private val content = kdiv{o->
        o- "fucking messages"
    }

    override val tabSpec = TabSpec(tabs.order.messages, t("TOTE", "Сообщения"), content)
}















