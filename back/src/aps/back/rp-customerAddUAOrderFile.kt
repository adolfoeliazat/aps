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
        val rec = ctx.q("Adding UA order file")
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
        return CustomerAddUAOrderFileRequest.Response(rec.getValue(FILES.ID).toString())
    }
)

























