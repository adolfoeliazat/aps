/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.tables.pojos.*
import com.google.common.hash.Hashing
import into.kommon.*
import java.util.*

//@RemoteProcedureFactory fun serveCustomerEditUAOrderFile() = customerProcedure(
//    {CustomerEditUAOrderFileRequest()},
//    runShit = fun(ctx, req): EditUAOrderFileRequestBase.Response {
//        return serveEditUAOrderFile(UserKind.CUSTOMER, ctx, req)
//    }
//)
//
//@RemoteProcedureFactory fun serveWriterEditUAOrderFile() = writerProcedure(
//    {WriterEditUAOrderFileRequest()},
//    runShit = fun(ctx, req): EditUAOrderFileRequestBase.Response {
//        return serveEditUAOrderFile(UserKind.WRITER, ctx, req)
//    }
//)
//
//private fun serveEditUAOrderFile(callingUserKind: UserKind, ctx: ProcedureContext, req: EditUAOrderFileRequestBase): EditUAOrderFileRequestBase.Response {
//    val orderFileID = req.orderFileID.value.toLong()
//
//    val orderFile = tracingSQL("Select order file") {ctx.q
//        .selectFrom(UA_ORDER_FILES)
//        .where(UA_ORDER_FILES.ID.eq(orderFileID))
//        .fetchOne()
//    }
//
//    // TODO:vgrechka Check permissions
//    orderFile.uaOrderAreaId
//
//    FILES.let {t->
//        ctx.updateShit("Update file", t) {it
//            .set(t.TITLE, req.title.value)
//            .set(t.DETAILS, req.details.value)
//            .let {
//                if (req.file.provided) {
//                    val content = Base64.getDecoder().decode(req.file.base64)
//                    it
//                        .set(t.NAME, req.file.fileName)
//                        .set(t.CONTENT, content)
//                        .set(t.SIZE_BYTES, content.size)
//                        .set(t.MIME, "application/octet-stream")
//                        .set(t.SHA1, Hashing.sha1().hashBytes(content).toString())
//                } else it
//            }
//            .where(t.ID.eq(orderFile.fileId))
//            .execute()
//        }
//    }
//
//    UA_ORDER_FILES.let {t-> // Just change updated_at
//        ctx.updateShit("Update order file", t) {it
//            .where(t.ID.eq(orderFileID))
//            .execute()
//        }
//    }
//
//    val updatedOrderFile = selectUAOrderFile(ctx, orderFileID).toRTO(ctx)
//
//    return EditUAOrderFileRequestBase.Response(updatedOrderFile)
//}























