/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.enums.*
import aps.back.generated.jooq.tables.pojos.JQUsers
import org.jooq.*
import kotlin.reflect.KClass

@RemoteProcedureFactory fun customerCreateUAOrder() = customerProcedure(
    CustomerCreateUAOrderRequest(),
    runShit = fun(ctx, req): CustomerCreateUAOrderRequest.Response {
        val documentType = req.documentType.value
        val rec = ctx.q("Insert UA order")
            .insertInto(UA_ORDERS)
            .set(USERS.INSERTED_AT, ctx.requestTimestamp)
            .set(USERS.UPDATED_AT, ctx.requestTimestamp)
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
        return CustomerCreateUAOrderRequest.Response(rec.getValue(UA_ORDERS.ID).toString())
    }
)

























