/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.enums.*
import into.kommon.*

@RemoteProcedureFactory fun serveUACustomerCreateOrder() = customerProcedure(
    {UACustomerCreateOrderRequest(it.xlobal)},
    needsUser = NeedsUser.MAYBE,
    runShit = fun(ctx, req): UACustomerCreateOrderRequest.Response {
        val documentType = req.documentType.value

        springctx.getBean(WarmWelcomer::class.java).sayHello()
        springctx.getBean(WarmWelcomer::class.java).sayHello()
        springctx.getBean(WarmWelcomer::class.java).sayHello()

        val repo = springctx.getBean(UAOrderRepository::class.java)
        repo.save(UAOrder()-{o->
            o.title = "boobs2"
        })
        dwarnStriking("Saved shit")

        die()

//        val orderID = UA_ORDERS.let {t->
//            ctx.insertShit("Insert order", t) {it
//                .set(t.CUSTOMER_ID, ctx.user.id.toLong())
//                .set(t.TITLE, req.documentTitle.value)
//                .set(t.ADMIN_NOTES, "")
//                .set(t.DOCUMENT_TYPE, documentType.toJOOQ())
//                .set(t.NUM_PAGES, req.numPages.value)
//                .set(t.NUM_SOURCES, req.numSources.value)
//                .set(t.DETAILS, req.documentDetails.value)
//                .set(t.STATE, JQUaOrderState.LOOKING_FOR_WRITERS)
//                .returnID(t)
//            }
//        }

//        fun createArea(name: String) {
//            tracingSQL("Insert order area: $name") {ctx.q
//                .insertInto(UA_ORDER_AREAS)
//                .set(UA_ORDER_AREAS.INSERTED_AT, ctx.requestTimestamp)
//                .set(UA_ORDER_AREAS.UPDATED_AT, ctx.requestTimestamp)
//                .set(UA_ORDER_AREAS.UA_ORDER_ID, orderID)
//                .set(UA_ORDER_AREAS.NAME, name)
//                .execute()
//            }
//        }
//
//        createArea(const.orderArea.customer)
//        createArea(const.orderArea.writer)

//        return UACustomerCreateOrderRequest.Response(orderID.toString())
    }
)

























