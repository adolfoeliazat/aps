/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.enums.*
import java.util.*

@RemoteProcedureFactory fun customerAddUAOrderFile() = customerProcedure(
    CustomerAddUAOrderFileRequest(),
    runShit = fun(ctx, req): CustomerAddUAOrderFileRequest.Response {
        val content = Base64.getDecoder().decode(req.file.base64)
        val fileRec = ctx.q("Insert file")
            .insertInto(FILES)
            .set(FILES.INSERTED_AT, ctx.requestTimestamp)
            .set(FILES.UPDATED_AT, ctx.requestTimestamp)
            .set(FILES.CREATOR_ID, ctx.user.id.toLong())
            .set(FILES.NAME, req.file.fileName)
            .set(FILES.TITLE, req.title.value)
            .set(FILES.ADMIN_NOTES, "")
            .set(FILES.DETAILS, req.details.value)
            .set(FILES.CONTENT, content)
            .set(FILES.SIZE_BYTES, content.size)
            .set(FILES.MIME, "application/octet-stream")
            .returning(FILES.ID)
            .fetchOne()

        val areaName = when (ctx.user.kind) {
            UserKind.CUSTOMER -> const.orderArea.customer
            UserKind.WRITER -> const.orderArea.writer
            UserKind.ADMIN -> const.orderArea.admin
        }

        val areaRec = ctx.q("Select area")
            .select().from(UA_ORDER_AREAS)
            .where(UA_ORDER_AREAS.NAME.eq(areaName))
            .fetchOne()

        val orderFileRec = ctx.q("Insert order file")
            .insertInto(UA_ORDER_FILES)
            .set(UA_ORDER_FILES.INSERTED_AT, ctx.requestTimestamp)
            .set(UA_ORDER_FILES.UPDATED_AT, ctx.requestTimestamp)
            .set(UA_ORDER_FILES.CREATOR_ID, ctx.user.id.toLong())
            .set(UA_ORDER_FILES.UA_ORDER_ID, req.orderID.value.toLong())
            .set(UA_ORDER_FILES.FILE_ID, fileRec.getValue(FILES.ID))
            .set(UA_ORDER_FILES.UA_ORDER_AREA_ID, areaRec.getValue(UA_ORDER_AREAS.ID))
            .returning(UA_ORDER_FILES.ID)
            .fetchOne()

        return CustomerAddUAOrderFileRequest.Response(orderFileRec.getValue(UA_ORDER_FILES.ID).toString())
    }
)

























