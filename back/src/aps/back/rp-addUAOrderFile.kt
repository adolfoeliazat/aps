/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.enums.*
import aps.back.generated.jooq.tables.records.*
import com.google.common.hash.Hashing
import java.util.*

@RemoteProcedureFactory fun serveCustomerAddUAOrderFile() = customerProcedure(
    CustomerAddUAOrderFileRequest(),
    runShit = fun(ctx, req): AddUAOrderFileRequestBase.Response {
        return serveAddUAOrderFile(UserKind.CUSTOMER, ctx, req)
    }
)

@RemoteProcedureFactory fun serveWriterAddUAOrderFile() = writerProcedure(
    WriterAddUAOrderFileRequest(),
    runShit = fun(ctx, req): AddUAOrderFileRequestBase.Response {
        return serveAddUAOrderFile(UserKind.WRITER, ctx, req)
    }
)

private fun serveAddUAOrderFile(callingUserKind: UserKind, ctx: ProcedureContext, req: AddUAOrderFileRequestBase): AddUAOrderFileRequestBase.Response {
    val content = Base64.getDecoder().decode(req.file.base64)
    val fileID = FILES.let {t->
        ctx.insertShit("Insert file", t) {it
            .set(t.NAME, req.file.fileName)
            .set(t.TITLE, req.title.value)
            .set(t.ADMIN_NOTES, "")
            .set(t.DETAILS, req.details.value)
            .set(t.CONTENT, content)
            .set(t.SIZE_BYTES, content.size)
            .set(t.MIME, "application/octet-stream")
            .set(t.SHA1, Hashing.sha1().hashBytes(content).toString())
            .returnID(t)
        }
    }

    val orderID = req.orderID.value.toLong()

    val areaID = selectUAOrderAreaByName(ctx, orderID, userKindToAreaName(ctx.user.kind)).id

    val seenAsFrom = callingUserKind.toJOOQ()

    val orderFileID = UA_ORDER_FILES.let {t->
        ctx.insertShit("Insert order file", t) {it
            .set(t.UA_ORDER_ID, orderID)
            .set(t.FILE_ID, fileID)
            .set(t.UA_ORDER_AREA_ID, areaID)
            .set(t.SEEN_AS_FROM, seenAsFrom)
            .returnID(t)
        }
    }

    insertFileUserPermission(ctx, fileID, ctx.user.id.toLong())

    return AddUAOrderFileRequestBase.Response(orderFileID.toString())
}
























