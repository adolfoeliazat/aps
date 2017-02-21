/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import into.kommon.*
import org.springframework.data.repository.findOrDie
import java.util.*

@Servant class ServeUACreateOrder(
    val repo: UAOrderRepository
) : BitchyProcedure() {
    override fun serve() {
        fuckCustomer(FuckCustomerParams(
            bpc = bpc,
            makeRequest = {UAOrderParamsRequest(isAdmin = isAdmin(), isUpdate = false)},
            needsUser = NeedsUser.MAYBE,
            runShit = fun (ctx, req: UAOrderParamsRequest): UACreateOrderResponse {
                val user = ctx.user
                val confirmationSecret = TestServerFiddling.nextGeneratedConfirmationSecret.getAndReset()
                    ?: UUID.randomUUID().toString()

                val order = repo.save(UAOrder(
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
                ))

                val vspacing = "0.5em"
                fun row(title: String, value: Any) = """
                    <tr>
                        <th style='text-align: left; white-space: nowrap; padding: 0; padding-bottom: $vspacing;'>$title</th>
                        <td style='padding: 0; padding-left: 1em; padding-bottom: $vspacing; white-space: pre-wrap;'>${escapeHTML(value.toString())}</td>
                    </tr>"""

                val customerName = stringBuild {s->
                    s += order.customerFirstName
                    order.customerLastName.let {
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
                                        ${row(fields.uaDocumentType.title, order.documentType.title)}
                                        ${row(fields.documentTitle.title, order.title)}
                                        ${row(fields.numPages.title, order.numPages)}
                                        ${row(fields.numSources.title, order.numSources)}
                                    </table>
                                    <div style='font-weight: bold; padding-top: $vspacing; padding-bottom: $vspacing;'>${fields.orderDetails.title}</div>
                                    <div style='white-space: pre-wrap;'>${escapeHTML(order.details)}</div>
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
        fuckCustomer(FuckCustomerParams(
            bpc = bpc,
            makeRequest = {UAOrderParamsRequest(isAdmin = requestUser.kind == UserKind.ADMIN,
                                                isUpdate = true)},
            needsUser = NeedsUser.YES,
            runShit = fun(ctx, req: UAOrderParamsRequest): UAUpdateOrderResponse {
                val order = repo.findOrDie(req.orderID.value)-{o->
                    o.documentType = req.documentType.value
                    o.title = req.documentTitle.value
                    o.numPages = req.numPages.value
                    o.numSources = req.numSources.value
                    o.details = req.documentDetails.value
                    o.customerPhone = req.phone.value
                    updateAdminNotes(o, req)
                }
                repo.save(order)
                return UAUpdateOrderResponse()
            }
        ))
    }
}




















