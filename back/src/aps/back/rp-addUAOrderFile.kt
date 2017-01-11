/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.enums.*
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
    val fileID = FILES.let {
        ctx.insertShit("Insert file", it)
            .set(it.NAME, req.file.fileName)
            .set(it.TITLE, req.title.value)
            .set(it.ADMIN_NOTES, "")
            .set(it.DETAILS, req.details.value)
            .set(it.CONTENT, content)
            .set(it.SIZE_BYTES, content.size)
            .set(it.MIME, "application/octet-stream")
            .set(it.SHA1, Hashing.sha1().hashBytes(content).toString())
            .returnID(it)
    }

    val areaID = UA_ORDER_AREAS.let {
        ctx.q("Select area")
            .select().from(it)
            .where(it.NAME.eq(
                userKindToAreaName(ctx.user.kind)))
            .fetchOne().getValue(it.ID)
    }

    val seenAsFrom = callingUserKind.toJOOQ()

    val orderFileID = UA_ORDER_FILES.let {
        ctx.insertShit("Insert order file", it)
            .set(it.UA_ORDER_ID, req.orderID.value.toLong())
            .set(it.FILE_ID, fileID)
            .set(it.UA_ORDER_AREA_ID, areaID)
            .set(it.SEEN_AS_FROM, seenAsFrom)
            .returnID(it)
    }

    insertFileUserPermission(ctx, fileID, ctx.user.id.toLong())

    return AddUAOrderFileRequestBase.Response(orderFileID.toString())
}
























