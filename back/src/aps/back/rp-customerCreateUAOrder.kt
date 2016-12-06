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
    runShit = fun(ctx, req): GenericResponse {
        val documentType = req.documentType.value
        val urgency = req.documentUrgency.value
        val pageCost = uaPageCost(documentType, urgency)
        ctx.q("Insert UA order")
            .insertInto(UA_ORDERS)
            .set(UA_ORDERS.CREATOR_ID, ctx.user.id.toLong())
            .set(UA_ORDERS.CUSTOMER_ID, ctx.user.id.toLong())
            .set(UA_ORDERS.TITLE, req.title.value)
            .set(UA_ORDERS.ADMIN_NOTES, "")
            .set(UA_ORDERS.DOCUMENT_TYPE, documentType.toJOOQ())
            .set(UA_ORDERS.DOCUMENT_URGENCY, urgency.toJOOQ())
            .set(UA_ORDERS.PAGE_COST, pageCost)
            .set(UA_ORDERS.PRICE, pageCost * req.numPages.value)
            .set(UA_ORDERS.NUM_PAGES, req.numPages.value)
            .set(UA_ORDERS.NUM_SOURCES, req.numSources.value)
            .set(UA_ORDERS.DETAILS, req.details.value)
            .set(UA_ORDERS.STATE, JQOrderState.WAITING_FOR_PAYMENT)
            .execute()

        return GenericResponse()
    }
)

























