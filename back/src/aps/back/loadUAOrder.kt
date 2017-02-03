/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*

@Servant class ServeLoadUAOrder(
    val orderRepo: UAOrderRepository,
    val userRepo: UserRepository,
    val userTokenRepo: UserTokenRepository
) : BitchyProcedure() {
    override fun serve() {
        fuckCustomer(FuckCustomerParams(
            bpc = bpc,
            makeRequest = {LoadUAOrderRequest()},
            needsUser = NeedsUser.YES,
            runShit = fun(ctx, req: LoadUAOrderRequest): LoadUAOrderRequest.Response {
                fun bitchNotFound(): Nothing = bitchExpectedly(t("TOTE", "Нет такого заказа (по крайней мере, для тебя)"))

                val order = orderRepo.findOne(req.id.value.toLong()) ?: bitchNotFound()
                // TODO:vgrechka Check access
                return LoadUAOrderRequest.Response(order.toRTO())
            }
        ))
    }
}






//@RemoteProcedureFactory fun loadUAOrder() = anyUserProcedure(
//    {LoadUAOrderRequest()},
//    runShit = fun(ctx, req): LoadUAOrderRequest.Response {
//        val rec: Record = tracingSQL("Select UA order") {ctx.q
//            .select().from(UA_ORDERS)
//            .where(UA_ORDERS.ID.eq(req.id.value.toLong()))
//            .fetchOne()
//            ?: bitchExpectedly(t("TOTE", "Нет такого заказа (по крайней мере, для тебя)"))
//        }
//
//        val order = rec.into(JQUaOrders::class.java)
//        val rto = UAOrderRTO(
//            id = order.id.toString(),
//            title = order.title,
//            insertedAt = order.insertedAt.time,
//            customer = ctx.loadUser(order.customerId),
//            documentType = order.documentType.toApp(),
//            deadline = order.deadline.time,
//            price = order.price,
//            numPages = order.numPages,
//            numSource = order.numSources,
//            details = order.details,
//            adminNotes = order.adminNotes,
//            state = order.state.toApp()
//        )
//
//        return LoadUAOrderRequest.Response(rto)
//    }
//)
























