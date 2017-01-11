/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.enums.*

@RemoteProcedureFactory fun serveCustomerCreateUAOrder() = customerProcedure(
    CustomerCreateUAOrderRequest(),
    runShit = fun(ctx, req): CustomerCreateUAOrderRequest.Response {
        val documentType = req.documentType.value

        val orderRec = ctx.q("Insert UA order")
            .insertInto(UA_ORDERS)
            .set(UA_ORDERS.INSERTED_AT, ctx.requestTimestamp)
            .set(UA_ORDERS.UPDATED_AT, ctx.requestTimestamp)
            .set(UA_ORDERS.CREATOR_ID, ctx.user.id.toLong())
            .set(UA_ORDERS.CUSTOMER_ID, ctx.user.id.toLong())
            .set(UA_ORDERS.TITLE, req.title.value)
            .set(UA_ORDERS.ADMIN_NOTES, "")
            .set(UA_ORDERS.DOCUMENT_TYPE, documentType.toJOOQ())
            .set(UA_ORDERS.DEADLINE, req.deadline.value)
            .set(UA_ORDERS.NUM_PAGES, req.numPages.value)
            .set(UA_ORDERS.NUM_SOURCES, req.numSources.value)
            .set(UA_ORDERS.DETAILS, req.details.value)
            .set(UA_ORDERS.STATE, JQUaOrderState.LOOKING_FOR_WRITERS)
            .returning(UA_ORDERS.ID)
            .fetchOne()

        fun createArea(name: String) {
            ctx.q("Insert order area: $name")
                .insertInto(UA_ORDER_AREAS)
                .set(UA_ORDER_AREAS.INSERTED_AT, ctx.requestTimestamp)
                .set(UA_ORDER_AREAS.UPDATED_AT, ctx.requestTimestamp)
                .set(UA_ORDER_AREAS.UA_ORDER_ID, orderRec.getValue(UA_ORDERS.ID))
                .set(UA_ORDER_AREAS.NAME, name)
                .execute()
        }

        createArea(const.orderArea.customer)
        createArea(const.orderArea.writer)

        return CustomerCreateUAOrderRequest.Response(orderRec.getValue(UA_ORDERS.ID).toString())
    }
)

























