@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import aps.front.frontSymbols.numberSign
import into.kommon.*

class UASingleOrderPage {
    var tabitha by notNullOnce<Tabitha<UAOrderRTO>>()

    val order get()= tabitha.entity

    suspend fun load(): PageLoadingError? {
        tabitha = Tabitha(
            page = pages.uaCustomer.order,
            tabKeys = tabs.order,
            defaultTab = tabs.order.params,
            loadEntity = {id->
                send(LoadUAOrderRequest()-{o->
                    o.id.value = id
                })
            },
            renderBelowHeader = {
                when (order.state) {
                    UAOrderState.CUSTOMER_DRAFT -> when (Globus.world.user.kind) {
                        UserKind.CUSTOMER -> Dolly(DollyParams(
                            styles = css.dolly.normal,
                            message = t("TOTE", "Проверьте / подредактируйте параметры. Загрузите файлы, если нужно. Затем нажмите..."),
                            buttons = listOf(
                                DollyButton(
                                    title = t("TOTE", "Отправить на проверку"), level = Button.Level.PRIMARY, key = buttons.sendForApproval,
                                    onClick = sendingDollyButtonHandler(
                                        sendRequest = {
                                            send(UACustomerSendOrderDraftForApprovalRequest()-{o->
                                                o.orderID.value = order.id
                                            })
                                        },
                                        onSuccess = {
                                            tabitha.reloadPage()
                                        })))))
                        UserKind.ADMIN -> NOTRE
                        UserKind.WRITER -> wtf("8eb54f3c-eafe-46dc-8ee9-7ad70adecee7")
                    }

                    UAOrderState.WAITING_ADMIN_APPROVAL -> when (user().kind) {
                        UserKind.CUSTOMER -> {
                            renderWaitingBanner(css.order.forCustomer.waitingApprovalBanner,
                                                t("TOTE", "Мы проверяем заказ и скоро тебе позвоним"))
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
                                            tabitha.reloadPage()
                                    }),
                                DollyButton(
                                    title = t("TOTE", "В стор"), level = Button.Level.PRIMARY, key = buttons.moveToStore,
                                    onClick = sendingDollyButtonHandler(
                                        sendRequest = {
                                            askRegina(ReginaAdminSendOrderToStore(orderID = order.id))
                                        },
                                        onSuccess = {
                                            tabitha.reloadPage()
                                        })))))
                        UserKind.WRITER -> wtf("a1f98aad-8f31-40da-8d93-0f48208bcb5c")
                    }

                    UAOrderState.RETURNED_TO_CUSTOMER_FOR_FIXING -> when (user().kind) {
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
                                            tabitha.reloadPage()
                                        })))))
                        UserKind.ADMIN -> NOTRE
                        UserKind.WRITER -> wtf("56a7d6ae-96c6-41f4-bc67-0780c2c12a2d")
                    }

                    else -> NOTRE
                }
            },
            makeTabs = {listOf(
                UsualParamsTab(
                    tabitha,
                    tabKey = tabs.order.params,
                    content = renderOrderParams(order),
                    hasEditButton = when (Globus.world.user.kind) {
                        UserKind.CUSTOMER -> order.state == UAOrderState.CUSTOMER_DRAFT
                        UserKind.ADMIN -> true
                        UserKind.WRITER -> imf("7ab0701a-df7a-457f-9bca-a2bedc0e5225")
                    },
                    editModalTitle = t("TOTE", "Параметры заказа"),
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
                    )),
                UACustomerSingleOrderPageFilesTab(order)
            )},
            pageHeaderTitle = {t("TOTE", "Заказ $numberSign${order.id}")},
            subtitle = {order.title},
            renderBelowSubtitle = fun(): ToReactElementable {
                if (order.state != UAOrderState.WAITING_ADMIN_APPROVAL) {
                    val whatShouldBeFixed = order.whatShouldBeFixedByCustomer
                    if (whatShouldBeFixed != null) {
                        val c = css.order.whatShouldBeFixed
                        return kdiv(className = c.container){o->
                            o- kdiv(className = c.title){o->
                                o- t("TOTE", "Что нужно исправить")
                            }
                            o- kdiv(className = c.body){o->
                                o- whatShouldBeFixed
                            }
                        }
                    }
                }
                return NOTRE
            }
        )
        return tabitha.load()
    }
}

fun renderOrderParams(order: UAOrderRTO): ToReactElementable {
    val m = MelindaTools
    return kdiv{o->
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
        o- renderAdminNotesIfNeeded(order)
    }
}



















