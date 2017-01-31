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

@Component @Scope(SCOPE_PROTOTYPE) class ServeUACustomerCreateOrder(
    val repo: UAOrderRepository
) : BitchyProcedure() {
    override fun serve() {
        fuckCustomer(FuckCustomerParams(
            bpc = bpc,
            makeRequest = {UACustomerCreateOrderRequest(it.xlobal)},
            needsUser = NeedsUser.MAYBE,
            runShit = {_, req: UACustomerCreateOrderRequest ->
                repo.save(UAOrder(title = req.documentTitle.value))
                die("cooooooool")
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
