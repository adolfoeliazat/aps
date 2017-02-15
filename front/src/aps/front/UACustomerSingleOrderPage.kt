@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import aps.const.text.symbols.nbsp
import aps.front.frontSymbols.numberSign
import into.kommon.*

// TODO:vgrechka Safe URLs

interface CustomerSingleUAOrderPageTab {
    val tabSpec: TabSpec
    fun load(): Promisoid<ZimbabweResponse.Shitty<*>?>
}

class UACustomerSingleOrderPage(val world: World) {
    object urlQuery : URLQueryParamsMarker {
        val id by MaybeStringURLParam()
        val tab by MaybeStringURLParam()
    }

    var orderID by notNullOnce<String>()
    var hint by notNullOnce<Placeholder>()
    var order by notNullOnce<UAOrderRTO>()

    suspend fun load() {
        orderID = urlQuery.id.get(world) ?: return world.setShittyParamsPage()
        val defaultTab = tabs.order.params
        val tabKey = tabs.order.items.findSimplyNamed(urlQuery.tab.get(world)) ?: defaultTab

        val res = await(send(world.token, LoadUAOrderRequest()-{o->
            o.id.value = orderID
        }))
        order = when (res) {
            is ZimbabweResponse.Shitty -> return world.setShittyResponsePage(res)
            is ZimbabweResponse.Hunky -> res.meat.order
        }

        hint = Placeholder(
            when (order.state) {
                UAOrderState.CUSTOMER_DRAFT -> renderCustomerDraftHint()

                UAOrderState.WAITING_ADMIN_APPROVAL -> run {
                    val c = css.orderPage.customer.waitingAdminApprovalHint
                    kdiv(className = c.container){o->
                        o- kdiv(className = c.message){o->
                            o- ki(className = c.icon + " " + fa.hourglassHalf)
                            o- t("TOTE", "Мы проверяем заказ и скоро тебе позвоним")
                        }
                    }
                }

                else -> NOTRE
            }
        )

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
                o- hint
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

    private fun renderCustomerDraftHint(busy: Boolean = false): ElementBuilder {
        val c = css.orderPage.customer.draftHint
        return kdiv(className = if (busy) c.containerBusy else c.container){o->
            o- kdiv(className = c.message){o->
                if (busy) {
                    o- renderTicker()
                } else {
                    o- t("TOTE", "Проверьте / подредактируйте параметры. Загрузите файлы, если нужно. Затем нажмите...")
                }
            }
            o- Button(title = t("TOTE", "Отправить на проверку"), disabled = busy, level = Button.Level.PRIMARY, key = buttons.sendForApproval, onClicka = {onSendForApproval()})
        }
    }

    private suspend fun onSendForApproval() {
        hint.setContent(renderCustomerDraftHint(busy = true))
        TestGlobal.shitHalfwayLock.resumeTestAndPauseSutFromSut()

        val res = send(UACustomerSendOrderDraftForApprovalRequest()-{o->
            o.orderID.value = order.id
        })
        exhaustive/when (res) {
            is FormResponse2.Hunky -> {
                world.pushNavigate(makeURL(pages.uaCustomer.order, listOf(
                    URLParamValue(UACustomerSingleOrderPage.urlQuery.id, orderID)
                )))
                TestGlobal.shitDoneLock.resumeTestFromSut()
            }
            is FormResponse2.Shitty -> {
                imf("Handle shitty response in onSendForApproval")
            }
        }
    }

    suspend fun clickOnTab(key: TabKey) {
        await(effects).blinkOn(byid(key.fqn), BlinkOpts(dwidth = "-0.15rem"))
        TestGlobal.switchTabHalfwayLock.resumeTestAndPauseSutFromSut()
        try {
            val q = UACustomerSingleOrderPage.urlQuery
            world.pushNavigate(makeURL(pages.uaCustomer.order, listOf(
                URLParamValue(q.id, orderID),
                URLParamValue(q.tab, simpleName(key.fqn))
            )))
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















