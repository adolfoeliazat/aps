@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import aps.const.text.symbols.nbsp
import aps.front.frontSymbols.numberSign
import into.kommon.*

// TODO:vgrechka Safe URLs

interface CustomerSingleUAOrderPageTab {
    val tabSpec: TabSpec
    suspend fun load(): FormResponse2.Shitty<*>?
}

class UACustomerSingleOrderPage(val world: World) {
    object urlQuery : URLQueryParamsMarker {
        val id by MaybeStringURLParam()
        val tab by MaybeStringURLParam()
    }

    var orderID by notNullOnce<String>()
    var hint by notNullOnce<Placeholder>()
    var order by notNullOnce<UAOrderRTO>()

    suspend fun load(): PageLoadingError? {
        orderID = urlQuery.id.get(world) ?: run {
            world.setShittyParamsPage()
            return null
        }
        val defaultTab = tabs.order.params
        val tabKey = tabs.order.items.findSimplyNamed(urlQuery.tab.get(world)) ?: defaultTab

        val res = send(LoadUAOrderRequest()-{o->
            o.id.value = orderID
        })
        order = when (res) {
            is FormResponse2.Shitty -> {
                world.setShittyResponsePage(res)
                return null
            }
            is FormResponse2.Hunky -> res.meat.order
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
            OrderParamsTab(world, order),
            UACustomerSingleOrderPageFilesTab(this, world, order)/*,
            MessagesTab(order)*/
        )
        val tab = tabs.find {it.tabSpec.key == tabKey} ?: tabs.first()

        val error = tab.load()
        error?.let {
            world.setShittyResponsePage(it)
            return null
        }

        world.setPage(Page(
            header = pageHeader3(kdiv{o->
                o- t("TOTE", "Заказ $numberSign${order.id}")
                o- kspan(backgroundColor = order.state.labelBackground, // TODO:vgrechka Extract CSS
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
        return null
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

private class OrderParamsTab(val world: World, val order: UAOrderRTO) : CustomerSingleUAOrderPageTab {
    override suspend fun load(): FormResponse2.Shitty<*>? {
        return null
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
                    renderOrderParams(o, order)
                }

                UserKind.WRITER -> imf()

                UserKind.ADMIN -> imf()
            }
        }
    }


    override val tabSpec = SimpleTabSpec(
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
                                            o.phone.value = order.customerPhone
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
    override suspend fun load(): FormResponse2.Shitty<*>? {
        throw UnsupportedOperationException("Implement me, please, fuck you")
    }

    private val content = kdiv{o->
        o- "fucking messages"
    }

    override val tabSpec = SimpleTabSpec(tabs.order.messages, t("TOTE", "Сообщения"), content)
}

fun renderOrderParams(o: ElementBuilder, order: UAOrderRTO) {
    val m = MelindaTools
    o- m.row{o->
        if (order.state != UAOrderState.CUSTOMER_DRAFT) {
            o- m.createdAtCol(3, order.createdAt)
        }
        o- m.col(3, fields.orderCustomerFirstName.title, order.customerFirstName)
        if (order.customerLastName.isNotBlank()) {
            o- m.col(3, fields.orderCustomerLastName.title, order.customerLastName)
        }
        o- m.col(3, fields.orderCustomerPhone.title, order.customerPhone)
    }

    o- m.row{o->
        o- m.col(3, fields.uaDocumentType.title, order.documentType.title)
        o- m.col(3, fields.numPages.title, order.numPages.toString())
        o- m.col(3, fields.numSources.title, order.numSources.toString())
    }

    o- m.detailsRow(order.details, highlightRanges = listOf(), title = fields.orderDetails.title)
}













