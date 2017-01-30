/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.tables.pojos.*
import org.jooq.*

@RemoteProcedureFactory fun loadUAOrder() = anyUserProcedure(
    {LoadUAOrderRequest()},
    runShit = fun(ctx, req): LoadUAOrderRequest.Response {
        val rec: Record = tracingSQL("Select UA order") {ctx.q
            .select().from(UA_ORDERS)
            .where(UA_ORDERS.ID.eq(req.id.value.toLong()))
            .fetchOne()
            ?: bitchExpectedly(t("TOTE", "Нет такого заказа (по крайней мере, для тебя)"))
        }

        val order = rec.into(JQUaOrders::class.java)
        val rto = UAOrderRTO(
            id = order.id.toString(),
            title = order.title,
            insertedAt = order.insertedAt.time,
            customer = ctx.loadUser(order.customerId),
            documentType = order.documentType.toApp(),
            deadline = order.deadline.time,
            price = order.price,
            numPages = order.numPages,
            numSource = order.numSources,
            details = order.details,
            adminNotes = order.adminNotes,
            state = order.state.toApp()
        )

        return LoadUAOrderRequest.Response(rto)
    }
)
























