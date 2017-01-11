/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.tables.pojos.*
import into.kommon.*
import java.util.*

@RemoteProcedureFactory fun customerEditUAOrderFile() = customerProcedure(
    CustomerEditUAOrderFileRequest(),
    runShit = fun(ctx, req): EditUAOrderFileRequestBase.Response {
        return serveEditUAOrderFile(UserKind.CUSTOMER, ctx, req)
    }
)

@RemoteProcedureFactory fun writerEditUAOrderFile() = writerProcedure(
    WriterEditUAOrderFileRequest(),
    runShit = fun(ctx, req): EditUAOrderFileRequestBase.Response {
        return serveEditUAOrderFile(UserKind.WRITER, ctx, req)
    }
)

private fun serveEditUAOrderFile(callingUserKind: UserKind, ctx: ProcedureContext, req: EditUAOrderFileRequestBase): EditUAOrderFileRequestBase.Response {
    val orderFileID = req.orderFileID.value.toLong()

    val orderFile = ctx.q("Select order file")
        .selectFrom(UA_ORDER_FILES)
        .where(UA_ORDER_FILES.ID.eq(orderFileID))
        .fetchOne()

    FILES.let {t->
        ctx.updateShit("Update file", t)
            .set(t.TITLE, req.title.value)
            .set(t.DETAILS, req.details.value)
            .let {
                if (req.file.provided) {
                    val content = Base64.getDecoder().decode(req.file.base64)
                    it
                        .set(t.NAME, req.file.fileName)
                        .set(t.CONTENT, content)
                        .set(t.SIZE_BYTES, content.size)
                        .set(t.MIME, "application/octet-stream")
                } else it
            }
            .where(t.ID.eq(orderFile.fileId))
            .execute()
    }

    UA_ORDER_FILES.let {t-> // Just change updated_at
        ctx.updateShit("Update order file", t)
            .where(t.ID.eq(orderFileID))
            .execute()
    }

    val updatedOrderFile = selectUAOrderFile(ctx, orderFileID).toRTO(ctx.q)

    return EditUAOrderFileRequestBase.Response(updatedOrderFile)
}























