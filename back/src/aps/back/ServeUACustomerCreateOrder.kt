/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import into.kommon.*
import java.util.*

@Servant class ServeUACustomerCreateOrder(
    val repo: UAOrderRepository
) : BitchyProcedure() {
    override fun serve() {
        fuckCustomer(FuckCustomerParams(
            bpc = bpc,
            makeRequest = {UACustomerCreateOrderRequest(it.xlobal)},
            needsUser = NeedsUser.MAYBE,
            runShit = fun (ctx, req: UACustomerCreateOrderRequest): UACustomerCreateOrderRequest.Response {
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
                    phone = req.phone.value,
                    customer = user,
                    anonymousCustomerEmail = when {
                        user == null -> req.anonymousCustomerEmail.value
                        else -> null
                    },
                    anonymousCustomerName = when {
                        user == null -> req.anonymousCustomerName.value
                        else -> null
                    }
                ))

                val vspacing = "0.5em"
                fun row(title: String, value: Any) = """
                    <tr>
                        <th style='text-align: left; white-space: nowrap; padding: 0; padding-bottom: $vspacing;'>$title</th>
                        <td style='padding: 0; padding-left: 1em; padding-bottom: $vspacing; white-space: pre-wrap;'>${escapeHTML(value.toString())}</td>
                    </tr>"""

                val customerName = when {
                    ctx.user == null -> req.anonymousCustomerName.value
                    else -> imf("ServeUACustomerCreateOrder -- signed-in customer")
                }
                val customerEmail = when {
                    ctx.user == null -> req.anonymousCustomerEmail.value
                    else -> imf("ServeUACustomerCreateOrder -- signed-in customer")
                }

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
                                        ${row(fieldSpecs.shebang.ua.documentType.ref.title, order.documentType.title)}
                                        ${row(fieldSpecs.shebang.documentTitle.ref.title, order.title)}
                                        ${row(fieldSpecs.shebang.numPages.ref.title, order.numPages)}
                                        ${row(fieldSpecs.shebang.numSources.ref.title, order.numSources)}
                                    </table>
                                    <div style='font-weight: bold; padding-top: $vspacing; padding-bottom: $vspacing;'>${fieldSpecs.shebang.details.ref.title}</div>
                                    <div style='white-space: pre-wrap;'>${escapeHTML(order.details)}</div>
                                    <div style='padding-top: 2em; font-style: italic;'>${const.productName.uaCustomer}</div>
                                </div>
                            """
                    ))
                ))
                return UACustomerCreateOrderRequest.Response(order.id.toString())
            }
        ))
    }
}


@Servant class ServeUACustomerUpdateOrder(
    val repo: UAOrderRepository
) : BitchyProcedure() {
    override fun serve() {
        fuckCustomer(FuckCustomerParams(
            bpc = bpc,
            makeRequest = {UACustomerCreateOrderRequest(it.xlobal)},
            needsUser = NeedsUser.YES,
            runShit = fun(ctx, req: UACustomerCreateOrderRequest): UACustomerCreateOrderRequest.UpdateResponse {
                val order = repo.findOne(ctx.fields.entityID)-{o->
                    o.documentType = req.documentType.value
                    o.title = req.documentTitle.value
                    o.numPages = req.numPages.value
                    o.numSources = req.numSources.value
                    o.details = req.documentDetails.value
                    o.phone = req.phone.value
                }
                repo.save(order)
                return UACustomerCreateOrderRequest.UpdateResponse()
            }
        ))
    }
}




















