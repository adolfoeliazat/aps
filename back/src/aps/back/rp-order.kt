/*
 * APS
 *
 * (C) Copyright 2015-2017 Vladimir Grechka
 */

package aps.back

import aps.*
import com.google.common.hash.Hashing
import into.kommon.*
import org.springframework.data.repository.findOrDie
import sun.net.www.content.text.Generic
import java.util.*


@Servant class ServeUACreateOrder(
    val repo: UAOrderRepository
) : BitchyProcedure() {
    override fun serve() {
        fuckAnyUserOrAnonymous(FuckAnyUserOrAnonymousParams(
            bpc = bpc,
            makeRequest = {UAOrderParamsRequest(isAdmin = isAdmin(), isUpdate = false)},
            runShit = fun (ctx, req: UAOrderParamsRequest): UACreateOrderResponse {
                // TODO:vgrechka Security
                val user = ctx.user
                val confirmationSecret = TestServerFiddling.nextGeneratedConfirmationSecret.getAndReset()
                    ?: UUID.randomUUID().toString()

                val order = repo.save(UAOrder(
                    order = UAOrderFields(
                        documentType = req.documentType.value,
                        title = req.documentTitle.value,
                        numPages = req.numPages.value,
                        numSources = req.numSources.value,
                        details = req.documentDetails.value,
                        state = UAOrderState.WAITING_EMAIL_CONFIRMATION,
                        confirmationSecret = confirmationSecret,
                        customerPhone = req.phone.value,
                        customer = user,
                        customerFirstName = req.firstName.value,
                        customerLastName = req.lastName.value,
                        customerEmail = req.email.value,
                        adminNotes = adminNotesForCreate(req),
                        minAllowedPriceOffer = -1,
                        maxAllowedPriceOffer = -1,
                        minAllowedDurationOffer = -1,
                        maxAllowedDurationOffer = -1,
                        category = uaDocumentCategoryRepo.findOrDie(const.uaDocumentCategoryID.misc)
                    )
                ))

                val vspacing = "0.5em"
                fun row(title: String, value: Any) = """
                    <tr>
                        <th style='text-align: left; white-space: nowrap; padding: 0; padding-bottom: $vspacing;'>$title</th>
                        <td style='padding: 0; padding-left: 1em; padding-bottom: $vspacing; white-space: pre-wrap;'>${escapeHTML(value.toString())}</td>
                    </tr>"""

                val customerName = stringBuild {s->
                    s += order.order.customerFirstName
                    order.order.customerLastName.let {
                        if (it.isNotBlank())
                            s += " " + it
                    }
                }
                val customerEmail = req.email.value

                // TODO:vgrechka Make `pages` shared (awkward), so it can be referenced here? Probably, not worth it...
                val confirmationURL = ctx.clientRoot + "/confirmOrder.html?secret=$confirmationSecret"
                EmailMatumba.send(Email(
                    to = "$customerName <$customerEmail>",
                    subject = "[${const.productName.uaCustomer}] Подтверждение заказа",
                    html = dedent(t(
                        en = """TOTE""",
                        ua = """
                                <div style='font-family: "Helvetica Neue",Helvetica,Arial,sans-serif;'>
                                    <div style='padding-bottom: 1em;'>Привет, ${escapeHTML(customerName)}!</div>
                                    Нажми <a href="$confirmationURL">сюда</a> для подтверждения заказа.
                                    <h3 style='font-size: 24px; margin-top: 20px; margin-bottom: 10px; line-height: 1.1; font-weight: 500;'>
                                        Заказ №${order.id}
                                    </h3>
                                    <table style='border-spacing: 0; border-collapse: collapse;'>
                                        ${row(fields.uaDocumentType.title, order.order.documentType.title)}
                                        ${row(fields.documentTitle.title, order.order.title)}
                                        ${row(fields.numPages.title, order.order.numPages)}
                                        ${row(fields.numSources.title, order.order.numSources)}
                                    </table>
                                    <div style='font-weight: bold; padding-top: $vspacing; padding-bottom: $vspacing;'>${fields.orderDetails.title}</div>
                                    <div style='white-space: pre-wrap;'>${escapeHTML(order.order.details)}</div>
                                    <div style='padding-top: 2em; font-style: italic;'>${const.productName.uaCustomer}</div>
                                </div>
                            """
                    ))
                ))
                return UACreateOrderResponse(order.id!!)
            }
        ))
    }
}

@Servant class ServeUAUpdateOrder() : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc,
            makeRequest = {UAOrderParamsRequest(isAdmin = requestUser.user.kind == UserKind.ADMIN,
                                                isUpdate = true)},
            runShit = fun(ctx, req: UAOrderParamsRequest): UAUpdateOrderResponse {
                // TODO:vgrechka Security
                checkingAllFieldsRetrieved(req) {
                    uaOrderRepo.findOrDie(req.orderID.value)-{o->
                        o.order.documentType = req.documentType.value
                        o.order.title = req.documentTitle.value
                        o.order.numPages = req.numPages.value
                        o.order.numSources = req.numSources.value
                        o.order.details = req.documentDetails.value
                        o.order.customerPhone = req.phone.value
                        o.order.customerFirstName = req.firstName.value
                        o.order.customerLastName = req.lastName.value
                        o.order.customerEmail = req.email.value
                        updateAdminNotes(o.order, req)
                    }
                }
                return UAUpdateOrderResponse()
            }
        ))
    }
}

@Servant class ServeUAUpdateOrderStoreParams : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc,
            makeRequest = {UAOrderStoreParamsRequest()},
            runShit = fun(ctx, req: UAOrderStoreParamsRequest): GenericResponse {
                // TODO:vgrechka Security
                imf("d9ef73c0-18a2-4a25-b969-42959a03e50f")
                checkingAllFieldsRetrieved(req) {
                    uaOrderRepo.findOrDie(req.orderID.value)-{o->

                    }
                }
                return GenericResponse()
            }
        ))
    }
}


@Servant class ServeUACreateOrderFile : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc, makeRequest = {UAOrderFileParamsRequest(isAdmin = isAdmin(),
                                                               isUpdate = false)},
            runShit = fun(ctx, req): UACreateOrderFileResponse {
                val order = uaOrderRepo.findOrDie(req.orderID.value)
                // TODO:vgrechka Security

                val requestFile = req.file.value as? FileField.Value.Provided ?: wtf("6ae5b17c-9d66-4569-aa17-4ea142e8f383")

                val content = Base64.getDecoder().decode(requestFile.base64)
                val file = uaOrderFileRepo.save(UAOrderFile(
                    orderFile = UAOrderFileFields(
                        order = order,
                        name = requestFile.fileName,
                        title = req.title.value,
                        mime = "application/octet-stream",
                        details = req.details.value,
                        adminNotes = adminNotesForCreate(req),
                        sha256 = Hashing.sha256().hashBytes(content).toString(),
                        sizeBytes = content.size,
                        content = content,
                        creator = requestUser,
                        forCustomerSeenAsFrom = requestUser.user.kind, // TODO:vgrechka ...
                        forWriterSeenAsFrom = requestUser.user.kind // TODO:vgrechka ...
                    )
                ))

                return UACreateOrderFileResponse(file.id!!)
            }
        ))
    }
}

@Servant class ServeUAUpdateOrderFile : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc, makeRequest = {UAOrderFileParamsRequest(isAdmin = requestUser.user.kind == UserKind.ADMIN,
                                                               isUpdate = true)},
            runShit = fun(ctx, req): UAUpdateOrderFileResponse {
                checkingAllFieldsRetrieved(req) {
                    val file = uaOrderFileRepo.findOrDie(req.fileID.value)
                    // TODO:vgrechka Check permissions
                    file-{o->
                        o.orderFile.title = req.title.value
                        o.orderFile.details = req.details.value
                        val requestFile = req.file.value
                        if (requestFile is FileField.Value.Provided) {
                            val content = Base64.getDecoder().decode(requestFile.base64)
                            o.orderFile.name = requestFile.fileName
                            o.orderFile.sha256 = Hashing.sha256().hashBytes(content).toString()
                            o.orderFile.sizeBytes = content.size
                            o.orderFile.content = content
                        }
                        updateAdminNotes(o.orderFile, req)
                        o.orderFile.common.touch()
                    }
                    return UAUpdateOrderFileResponse(file.toRTO(listOf()))
                }
            }
        ))
    }
}

@Servant class ServeUADeleteOrderFile(val fileRepo: UAOrderFileRepository) : BitchyProcedure() {
    override fun serve() {
        fuckCustomer(FuckCustomerParams(
            bpc = bpc, makeRequest = {UADeleteOrderFileRequest()},
            runShit = fun(ctx, req): DeleteRequest.Response {
                val file = fileRepo.findOrDie(req.id.value)
                // TODO:vgrechka @security Check permissions
                fileRepo.delete(file)
                return DeleteRequest.Response()
            }
        ))
    }
}

@Servant class ServeUADownloadOrderFile(val fileRepo: UAOrderFileRepository) : BitchyProcedure() {
    override fun serve() {
        fuckCustomer(FuckCustomerParams(
            bpc = bpc, makeRequest = {UADownloadOrderFileRequest()},
            runShit = fun(ctx, req): DownloadFileResponse {
                val file = fileRepo.findOrDie(req.fileID.value)
                // TODO:vgrechka @security Check permissions
                return DownloadFileResponse(
                    fileName = file.orderFile.name,
                    base64 = Base64.getEncoder().encodeToString(file.orderFile.content),
                    sha256 = Hashing.sha256().hashBytes(file.orderFile.content).toString()
                )
            }
        ))
    }
}

@Servant class ServeUACustomerSendOrderDraftForApproval(val orderRepo: UAOrderRepository) : BitchyProcedure() {
    override fun serve() {
        fuckCustomer(FuckCustomerParams(
            bpc = bpc, makeRequest = {UACustomerSendOrderDraftForApprovalRequest()},
            runShit = fun(ctx, req): UACustomerSendOrderDraftForApprovalRequest.Response {
                // TODO:vgrechka @security Check permissions
                val order = orderRepo.findOrDie(req.orderID.value)
                order.order.state = UAOrderState.WAITING_ADMIN_APPROVAL
                return UACustomerSendOrderDraftForApprovalRequest.Response()
            }
        ))
    }
}

@Servant class ServeReturnOrderToCustomerForFixing(val orderRepo: UAOrderRepository) : BitchyProcedure() {
    override fun serve() {
        fuckAdmin(FuckAdminParams(
            bpc = bpc, makeRequest = {ReturnOrderToCustomerForFixingRequest()},
            runShit = fun(ctx, req): GenericResponse {
                val order = orderRepo.findOrDie(req.entityID.value)
                order.order.state = UAOrderState.RETURNED_TO_CUSTOMER_FOR_FIXING
                order.order.whatShouldBeFixedByCustomer = req.rejectionReason.value
                return GenericResponse()
            }
        ))
    }
}

fun serveReginaCustomerSendOrderForApprovalAfterFixing(p: ReginaCustomerSendOrderForApprovalAfterFixing): GenericResponse {
    check(requestUser.user.kind == UserKind.CUSTOMER){"70630d2d-6796-4af8-8ac6-16e09a8b37e1"}
    // TODO:vgrechka Security
    val order = uaOrderRepo.findOrDie(p.orderID)
    check(order.order.state == UAOrderState.RETURNED_TO_CUSTOMER_FOR_FIXING){"698dd409-f382-45df-9e65-fff590302dd0"}
    order.order.state = UAOrderState.WAITING_ADMIN_APPROVAL
    return GenericResponse()
}

fun serveReginaAdminSendOrderToStore(p: ReginaAdminSendOrderToStore): GenericResponse {
    check(requestUser.user.kind == UserKind.ADMIN){"0af9f1b0-b5fb-4fb2-b3a9-198a0185ee15"}
    // TODO:vgrechka Security
    val order = uaOrderRepo.findOrDie(p.orderID)
    check(order.order.state in setOf(UAOrderState.WAITING_ADMIN_APPROVAL)){"7af262c7-2a28-43f8-910a-ccf3569142e9"}
    order.order.whatShouldBeFixedByCustomer = null
    order.order.state = UAOrderState.IN_STORE
    return GenericResponse()
}

fun serveReginaGetDocumentCategories(p: ReginaGetDocumentCategories): ReginaGetDocumentCategories.Response {
    // TODO:vgrechka Security
    val cat = uaDocumentCategoryRepo.findOrDie(const.uaDocumentCategoryID.root).toRTO(loadChildren = true)
    return ReginaGetDocumentCategories.Response(cat)
}
























