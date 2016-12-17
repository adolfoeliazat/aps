/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import java.util.*

@RemoteProcedureFactory fun customerAddUAOrderFile() = customerProcedure(
    CustomerAddUAOrderFileRequest(),
    runShit = fun(ctx, req): CustomerAddUAOrderFileRequest.Response {
        val content = Base64.getDecoder().decode(req.file.base64)
        val fileID = FILES.let {
            ctx.insertShit("Insert file", it)
                .set(it.NAME, req.file.fileName)
                .set(it.TITLE, req.title.value)
                .set(it.ADMIN_NOTES, "")
                .set(it.DETAILS, req.details.value)
                .set(it.CONTENT, content)
                .set(it.SIZE_BYTES, content.size)
                .set(it.MIME, "application/octet-stream")
                .returnID(it)
        }

        val areaID = UA_ORDER_AREAS.let {
            ctx.q("Select area")
                .select().from(it)
                .where(it.NAME.eq(
                    when (ctx.user.kind) {
                        UserKind.CUSTOMER -> const.orderArea.customer
                        UserKind.WRITER -> const.orderArea.writer
                        UserKind.ADMIN -> const.orderArea.admin
                    }))
                .fetchOne().getValue(it.ID)
        }

        val orderFileID = UA_ORDER_FILES.let {
            ctx.insertShit("Insert order file", it)
                .set(it.UA_ORDER_ID, req.orderID.value.toLong())
                .set(it.FILE_ID, fileID)
                .set(it.UA_ORDER_AREA_ID, areaID)
                .returnID(it)
        }

        return CustomerAddUAOrderFileRequest.Response(orderFileID.toString())
    }
)

























