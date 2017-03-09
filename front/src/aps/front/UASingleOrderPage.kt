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
            loadEntity = {id-> reginaLoadUAOrder(id)},
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
                        UserKind.ADMIN -> acceptOrRejectDolly(
                            message = t("TOTE", "Что будем делать с заказом?"),
                            jokeOptions = listOf("Заказать заказчика", "Купить молока"),
                            blankRejectingRequest = ReturnOrderToCustomerForFixingRequest(),
                            entityID = order.id,
                            tabitha = tabitha,
                            acceptButtonTitle = t("TOTE", "В стор"),
                            sendAcceptingRequest = {entityID-> reginaAdminSendOrderToStore(entityID)})
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
                                            reginaCustomerSendOrderForApprovalAfterFixing(orderID = order.id)
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
                UsualParamsTab<UAOrderRTO, /*HistoryItemRTO=*/ Nothing, UAOrderParamsRequest, GenericResponse>(
                    tabitha,
                    tabKey = tabs.order.params,
                    renderBody = {renderOrderParams(order, RenderOrderParamsStoreEditingParams(tabitha))},
                    hasEditButton = when (Globus.world.user.kind) {
                        UserKind.CUSTOMER -> order.state == UAOrderState.CUSTOMER_DRAFT
                        UserKind.ADMIN -> true
                        UserKind.WRITER -> imf("7ab0701a-df7a-457f-9bca-a2bedc0e5225")
                    },
                    editModalTitle = t("TOTE", "Параметры заказа"),
                    makeFormSpec = {FormSpec<UAOrderParamsRequest, GenericResponse>(
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
                    )}),
                UACustomerSingleOrderPageFilesTab(order)
            )},
            pageHeaderTitle = {t("TOTE", "Заказ $numberSign${order.id}")},
            subtitle = {order.title},
            renderBelowSubtitle = {renderMaybeRejectionReasonBanner(order.whatShouldBeFixedByCustomer)}
        )
        return tabitha.load()
    }

}

class RenderOrderParamsStoreEditingParams(val tabitha: Tabitha<UAOrderRTO>)

fun renderOrderParams(order: UAOrderRTO, storeEditingParams: RenderOrderParamsStoreEditingParams? = null): ToReactElementable {
    val m = MelindaTools
    return kdiv{o->
        o- m.row{o->
            if (order.state != UAOrderState.CUSTOMER_DRAFT) {
                o- m.createdAtCol(3, order.createdAt)
            }
            o- m.col(3, t("TOTE", "Статус"), order.state.title, contentClassName = css.order.stateLabel(order.state))
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

        if (isAdmin() && storeEditingParams != null) {
            o- anotherHeader(
                t("TOTE", "Стор"),
                renderControlsTo = {o->
                    o- Button(icon =  fa.pencil, key = buttons.editStoreParams) {
                        openEditModal(
                            title = t("TOTE", "Стор"),
                            formSpec = FormSpec<UAOrderStoreParamsRequest, GenericResponse>(
                                procedureName = "UAUpdateOrderStoreParams",
                                req = UAOrderStoreParamsRequest().populateCheckingCompleteness{o->
                                    o.orderID.value = order.id
                                    o.uaDocumentCategory.setValue(order.documentCategory)
                                    o.minAllowedPriceOffer.setValue(order.minAllowedPriceOffer)
                                    o.maxAllowedPriceOffer.setValue(order.maxAllowedPriceOffer)
                                    o.minAllowedDurationOffer.setValue(order.minAllowedDurationOffer)
                                    o.maxAllowedDurationOffer.setValue(order.maxAllowedDurationOffer)
                                }
                            ),
                            onSuccessa = {
                                storeEditingParams.tabitha.reloadPage(p = LoadPageForURLParams(
                                    scroll = LoadPageForURLParams.Scroll.PRESERVE))
                            }
                        )
                    }
                })

            o- m.row{o->
                o- m.col(6, fields.uaDocumentCategory.title, order.documentCategory.pathTitle)
            }

            o- renderOrderStoreBoundaries(order)
        }

        o- renderBottomPageSpace()
    }
}

fun renderOrderStoreBoundaries(order: UAOrderRTO): ToReactElementable {
    val m = MelindaTools
    return m.row{o->
        o- m.col(3, fields.minAllowedPriceOffer.title, renderMoney(order.minAllowedPriceOffer))
        o- m.col(3, fields.maxAllowedPriceOffer.title, renderMoney(order.maxAllowedPriceOffer))
        o- m.col(3, fields.minAllowedDurationOffer.title, renderDurationHours(order.minAllowedDurationOffer))
        o- m.col(3, fields.maxAllowedDurationOffer.title, renderDurationHours(order.maxAllowedDurationOffer))
    }
}



















