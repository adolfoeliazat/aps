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
                    fields = UAOrderFields(
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
                        adminNotes = adminNotesForCreate(req)
                    )
                ))

                val vspacing = "0.5em"
                fun row(title: String, value: Any) = """
                    <tr>
                        <th style='text-align: left; white-space: nowrap; padding: 0; padding-bottom: $vspacing;'>$title</th>
                        <td style='padding: 0; padding-left: 1em; padding-bottom: $vspacing; white-space: pre-wrap;'>${escapeHTML(value.toString())}</td>
                    </tr>"""

                val customerName = stringBuild {s->
                    s += order.fields.customerFirstName
                    order.fields.customerLastName.let {
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
                                        ${row(fields.uaDocumentType.title, order.fields.documentType.title)}
                                        ${row(fields.documentTitle.title, order.fields.title)}
                                        ${row(fields.numPages.title, order.fields.numPages)}
                                        ${row(fields.numSources.title, order.fields.numSources)}
                                    </table>
                                    <div style='font-weight: bold; padding-top: $vspacing; padding-bottom: $vspacing;'>${fields.orderDetails.title}</div>
                                    <div style='white-space: pre-wrap;'>${escapeHTML(order.fields.details)}</div>
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

@Servant class ServeUAUpdateOrder(
    val repo: UAOrderRepository
) : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc,
            makeRequest = {UAOrderParamsRequest(isAdmin = requestUser.fields.kind == UserKind.ADMIN,
                                                isUpdate = true)},
            runShit = fun(ctx, req: UAOrderParamsRequest): UAUpdateOrderResponse {
                // TODO:vgrechka Security
                checkingAllFieldsRetrieved(req) {
                    repo.findOrDie(req.orderID.value)-{o->
                        o.fields.documentType = req.documentType.value
                        o.fields.title = req.documentTitle.value
                        o.fields.numPages = req.numPages.value
                        o.fields.numSources = req.numSources.value
                        o.fields.details = req.documentDetails.value
                        o.fields.customerPhone = req.phone.value
                        o.fields.customerFirstName = req.firstName.value
                        o.fields.customerLastName = req.lastName.value
                        o.fields.customerEmail = req.email.value
                        updateAdminNotes(o.fields, req)
                    }
                }
                return UAUpdateOrderResponse()
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
                    fields = UAOrderFileFields(
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
                        forCustomerSeenAsFrom = requestUser.fields.kind, // TODO:vgrechka ...
                        forWriterSeenAsFrom = requestUser.fields.kind // TODO:vgrechka ...
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
            bpc = bpc, makeRequest = {UAOrderFileParamsRequest(isAdmin = requestUser.fields.kind == UserKind.ADMIN,
                                                               isUpdate = true)},
            runShit = fun(ctx, req): UAUpdateOrderFileResponse {
                checkingAllFieldsRetrieved(req) {
                    val file = uaOrderFileRepo.findOrDie(req.fileID.value)
                    // TODO:vgrechka Check permissions
                    file-{o->
                        o.fields.title = req.title.value
                        o.fields.details = req.details.value
                        val requestFile = req.file.value
                        if (requestFile is FileField.Value.Provided) {
                            val content = Base64.getDecoder().decode(requestFile.base64)
                            o.fields.name = requestFile.fileName
                            o.fields.sha256 = Hashing.sha256().hashBytes(content).toString()
                            o.fields.sizeBytes = content.size
                            o.fields.content = content
                        }
                        updateAdminNotes(o.fields, req)
                        o.fields.common.touch()
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
                    fileName = file.fields.name,
                    base64 = Base64.getEncoder().encodeToString(file.fields.content),
                    sha256 = Hashing.sha256().hashBytes(file.fields.content).toString()
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
                order.fields.state = UAOrderState.WAITING_ADMIN_APPROVAL
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
                order.fields.state = UAOrderState.RETURNED_TO_CUSTOMER_FOR_FIXING
                order.fields.whatShouldBeFixedByCustomer = req.rejectionReason.value
                return GenericResponse()
            }
        ))
    }
}

fun serveReginaCustomerSendOrderForApprovalAfterFixing(p: ReginaCustomerSendOrderForApprovalAfterFixing): GenericResponse {
    check(requestUser.fields.kind == UserKind.CUSTOMER){"70630d2d-6796-4af8-8ac6-16e09a8b37e1"}
    // TODO:vgrechka Security
    val order = uaOrderRepo.findOrDie(p.orderID)
    check(order.fields.state == UAOrderState.RETURNED_TO_CUSTOMER_FOR_FIXING){"698dd409-f382-45df-9e65-fff590302dd0"}
    order.fields.state = UAOrderState.WAITING_ADMIN_APPROVAL
    return GenericResponse()
}

fun serveReginaAdminSendOrderToStore(p: ReginaAdminSendOrderToStore): GenericResponse {
    check(requestUser.fields.kind == UserKind.ADMIN){"0af9f1b0-b5fb-4fb2-b3a9-198a0185ee15"}
    // TODO:vgrechka Security
    val order = uaOrderRepo.findOrDie(p.orderID)
    check(order.fields.state in setOf(UAOrderState.WAITING_ADMIN_APPROVAL)){"7af262c7-2a28-43f8-910a-ccf3569142e9"}
    order.fields.whatShouldBeFixedByCustomer = null
    order.fields.state = UAOrderState.IN_STORE
    return GenericResponse()
}

























