/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import into.kommon.*
import org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
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
                val imposedSecret = TestServerFiddling.nextGeneratedConfirmationSecret
                val confirmationSecret = when (imposedSecret) {
                    null -> UUID.randomUUID().toString()
                    else -> {
                        TestServerFiddling.nextGeneratedConfirmationSecret = null
                        imposedSecret
                    }
                }

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
                                        ${row(fieldSpecs.ua.documentType.title, order.documentType.title)}
                                        ${row(fieldSpecs.documentTitle.title, order.title)}
                                        ${row(fieldSpecs.numPages.title, order.numPages)}
                                        ${row(fieldSpecs.numSources.title, order.numSources)}
                                    </table>
                                    <div style='font-weight: bold; padding-top: $vspacing; padding-bottom: $vspacing;'>${fieldSpecs.details.title}</div>
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





















//@RemoteProcedureFactory fun _serveUACustomerCreateOrder() = customerProcedure(
//    {UACustomerCreateOrderRequest(it.xlobal)},
//    needsUser = NeedsUser.MAYBE,
//    runShit = fun(ctx, req): UACustomerCreateOrderRequest.Response {
//        val documentType = req.documentType.value
//
//        springctx.getBean(WarmWelcomer::class.java).sayHello()
//        springctx.getBean(WarmWelcomer::class.java).sayHello()
//        springctx.getBean(WarmWelcomer::class.java).sayHello()
//
//        val repo = springctx.getBean(UAOrderRepository::class.java)
//        repo.save(UAOrder(title = "boobs"))
//        dwarnStriking("Saved shit")
//
//        val shit = repo.findOne(1)
//        dwarnStriking("Found shit", shit.id, shit.title)
//        shit.title = "cunt"
//        repo.save(shit)
//
//        die()
//
////        val orderID = UA_ORDERS.let {t->
////            ctx.insertShit("Insert order", t) {it
////                .set(t.CUSTOMER_ID, ctx.user.id.toLong())
////                .set(t.TITLE, req.documentTitle.value)
////                .set(t.ADMIN_NOTES, "")
////                .set(t.DOCUMENT_TYPE, documentType.toJOOQ())
////                .set(t.NUM_PAGES, req.numPages.value)
////                .set(t.NUM_SOURCES, req.numSources.value)
////                .set(t.DETAILS, req.documentDetails.value)
////                .set(t.STATE, JQUaOrderState.LOOKING_FOR_WRITERS)
////                .returnID(t)
////            }
////        }
//
////        fun createArea(name: String) {
////            tracingSQL("Insert order area: $name") {ctx.q
////                .insertInto(UA_ORDER_AREAS)
////                .set(UA_ORDER_AREAS.INSERTED_AT, ctx.requestTimestamp)
////                .set(UA_ORDER_AREAS.UPDATED_AT, ctx.requestTimestamp)
////                .set(UA_ORDER_AREAS.UA_ORDER_ID, orderID)
////                .set(UA_ORDER_AREAS.NAME, name)
////                .execute()
////            }
////        }
////
////        createArea(const.orderArea.customer)
////        createArea(const.orderArea.writer)
//
////        return UACustomerCreateOrderRequest.Response(orderID.toString())
//    }
//)
