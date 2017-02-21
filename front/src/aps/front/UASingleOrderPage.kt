@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import aps.front.frontSymbols.numberSign
import into.kommon.*

interface CustomerSingleUAOrderPageTab {
    val tabSpec: TabSpec
    suspend fun load(): FormResponse2.Shitty<*>?
}

class UASingleOrderPage(val world: World) {
    object urlQuery : URLQueryParamsMarker {
        val id by LongURLParam()
        val tab by MaybeStringURLParam()
    }

    var orderID by notNullOnce<Long>()
    var order by notNullOnce<UAOrderRTO>()

    suspend fun load(): PageLoadingError? {
        // TODO:vgrechka Handle shitty params
        orderID = urlQuery.id.get()
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

        val dolly = when (order.state) {
            UAOrderState.CUSTOMER_DRAFT -> when (world.user.kind) {
                UserKind.CUSTOMER -> Dolly(DollyParams(
                    styles = css.dolly.normal,
                    message = t("TOTE", "Проверьте / подредактируйте параметры. Загрузите файлы, если нужно. Затем нажмите..."),
                    buttons = listOf(
                        DollyButton(title = t("TOTE", "Отправить на проверку"), level = Button.Level.PRIMARY, key = buttons.sendForApproval,
                                    onClick = sendingDollyButtonHandler(
                                        sendRequest = {
                                            send(UACustomerSendOrderDraftForApprovalRequest()-{o->
                                                o.orderID.value = order.id
                                            })},
                                        onSuccess = {
                                            reloadOrderPage()
                                        })))))
                UserKind.ADMIN -> NOTRE
                UserKind.WRITER -> wtf("8eb54f3c-eafe-46dc-8ee9-7ad70adecee7")
            }

            UAOrderState.WAITING_ADMIN_APPROVAL -> when (world.user.kind) {
                UserKind.CUSTOMER -> {
                    val c = css.order.forCustomer.waitingApprovalBanner
                    kdiv(className = c.container){o->
                        o- kdiv(className = c.message){o->
                            o- ki(className = c.icon + " " + fa.hourglassHalf)
                            o- t("TOTE", "Мы проверяем заказ и скоро тебе позвоним")
                        }
                    }
                }
                UserKind.ADMIN -> Dolly(DollyParams(
                    styles = css.dolly.normal,
                    message = t("TOTE", "Что будем делать с заказом?"),
                    buttons = listOf(
                        DollyButton(
                            title = t("TOTE", "Завернуть"), level = Button.Level.DANGER, key = buttons.returnToCustomerForFixing,
                            onClick = {
                                val executed = openDangerFormModalAndWaitExecution(
                                    title = t("TOTE", "Возвращаем на доработку"),
                                    primaryButtonTitle = t("TOTE", "Завернуть"),
                                    cancelButtonTitle = t("TOTE", "Не надо"),
                                    req = ReturnOrderToCustomerForFixingRequest()-{o->
                                        o.orderID.value = order.id
                                        o.rejectionReason.value = t("TOTE", "Что заказчику нужно исправить?")
                                    }
                                )
                                if (executed)
                                    reloadOrderPage()
                            }),
                        DollyButton(
                            title = t("TOTE", "В стор"), level = Button.Level.PRIMARY, key = buttons.moveToStore,
                            onClick = sendingDollyButtonHandler(
                                sendRequest = {
                                    askRegina(ReginaAdminSendOrderToStore(orderID = order.id))
                                },
                                onSuccess = {
                                    reloadOrderPage()
                                })))))
                UserKind.WRITER -> wtf("a1f98aad-8f31-40da-8d93-0f48208bcb5c")
            }

            UAOrderState.RETURNED_TO_CUSTOMER_FOR_FIXING -> when (world.user.kind) {
                UserKind.CUSTOMER -> Dolly(DollyParams(
                    styles = css.dolly.normal,
                    message = t("TOTE", "Админ завернул твой заказ на доработку. Когда исправишь, жми..."),
                    buttons = listOf(
                        DollyButton(
                            title = t("TOTE", "Исправил"), level = Button.Level.PRIMARY, key = buttons.sendForApprovalAfterFixing,
                            onClick = sendingDollyButtonHandler(
                                sendRequest = {
                                    askRegina(ReginaCustomerSendOrderForApprovalAfterFixing(orderID = order.id))
                                },
                                onSuccess = {
                                    reloadOrderPage()
                                })))))
                UserKind.ADMIN -> NOTRE
                UserKind.WRITER -> wtf("56a7d6ae-96c6-41f4-bc67-0780c2c12a2d")
            }

            else -> NOTRE
        }

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
//                o- kspan(backgroundColor = order.state.labelBackground,
//                         fontSize = "60%",
//                         padding = "0.1em 0.3em",
//                         borderRadius = "0.3em",
//                         marginLeft = "1em",
//                         position = "relative",
//                         top = "-0.2em"){o->
//                    o- order.state.title.replace(" ", nbsp)
//                }
            }),

            body = kdiv{o->
                o- dolly
                o- h4(marginBottom = "0.7em"){o->
                    o- order.title
                }

                if (order.state != UAOrderState.WAITING_ADMIN_APPROVAL) {
                    val whatShouldBeFixed = order.whatShouldBeFixedByCustomer
                    if (whatShouldBeFixed != null) {
                        val c = css.order.whatShouldBeFixed
                        o- kdiv(className = c.container){o->
                            o- kdiv(className = c.title){o->
                                o- t("TOTE", "Что нужно исправить")
                            }
                            o- kdiv(className = c.body){o->
                                o- whatShouldBeFixed
                            }
                        }
                    }
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


    private suspend fun reloadOrderPage() {
        // TODO:vgrechka Should use replaceNavigate?
        world.pushNavigate(makeURL(pages.uaCustomer.order, listOf(
            URLParamValue(urlQuery.id, orderID)
        )))
    }

    suspend fun clickOnTab(key: TabKey) {
        await(effects).blinkOn(byid(key.fqn), BlinkOpts(dwidth = "-0.15rem"))
        TestGlobal.switchTabHalfwayLock.resumeTestAndPauseSutFromSut()
        try {
            val q = UASingleOrderPage.urlQuery
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
            renderOrderParams(o, order)
        }
    }

    override val tabSpec = SimpleTabSpec(
        key = tabs.order.params,
        title = t("TOTE", "Параметры"),
        content = place,
        stripContent = kdiv{o->
            val hasEditButton = when (Globus.world.user.kind) {
                UserKind.CUSTOMER -> order.state == UAOrderState.CUSTOMER_DRAFT
                UserKind.ADMIN -> true
                UserKind.WRITER -> imf("7ab0701a-df7a-457f-9bca-a2bedc0e5225")
            }
            if (hasEditButton) {
                o- Button(icon = fa.pencil, level = Button.Level.DEFAULT, key = buttons.edit) {
                    openEditModal(
                        title = t("TOTE", "Параметры заказа"),
                        formSpec = FormSpec<UAOrderParamsRequest, GenericResponse>(
                            procedureName = "UAUpdateOrder",
                            req = UAOrderParamsRequest(isAdmin = isAdmin(), isUpdate = true).populateCheckingCompleteness{o->
                                o.orderID.value = order.id
                                o.documentType.value = order.documentType
                                o.documentTitle.value = order.title
                                o.numPages.setValue(order.numPages)
                                o.numSources.setValue(order.numSources)
                                o.documentDetails.value = order.details
                                o.firstName.value = order.customerFirstName
                                o.lastName.value = order.customerLastName
                                o.email.value = order.customerEmail
                                o.phone.value = order.customerPhone
                                populateWithAdminNotes(o, order)
                            }
                        ),
                        onSuccessa = {
                            world.replaceNavigate(makeURL(pages.uaCustomer.order, listOf(
                                URLParamValue(UASingleOrderPage.urlQuery.id, order.id)
                            )))
                        }
                    )
                }
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
    o- m.row {o->
        if (order.state != UAOrderState.CUSTOMER_DRAFT) {
            o- m.createdAtCol(3, order.createdAt)
        }
        o- m.col(3, t("TOTE", "Статус"), order.state.title, className = css.order.stateLabel(order.state))
    }
    o- m.row{o->
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

    o- m.detailsRow(order.details, order.detailsHighlightRanges, title = fields.orderDetails.title)
    renderAdminNotesIfNeeded(o, order)
}



















