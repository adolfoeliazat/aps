/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import com.google.common.hash.Hashing
import into.kommon.*
import org.springframework.data.repository.findOrDie
import java.util.*

@Servant class ServeUACreateOrderFile(val orderRepo: UAOrderRepository, val fileRepo: UAOrderFileRepository) : BitchyProcedure() {
    override fun serve() {
        fuckCustomer(FuckCustomerParams(
            bpc = bpc, makeRequest = {UACreateOrderFileRequest()},
            runShit = fun(ctx, req): aps.UACreateOrderFileRequest.Response {
                val order = orderRepo.findOrDie(req.orderID.value)
                // TODO:vgrechka @security Check permissions

                val content = Base64.getDecoder().decode(req.file.base64)
                val file = fileRepo.save(UAOrderFile(
                    order = order,
                    name = req.file.fileName,
                    title = req.fields1.title.value,
                    mime = "application/octet-stream",
                    details = req.fields1.details.value,
                    adminNotes = "",
                    sha1 = Hashing.sha1().hashBytes(content).toString(),
                    sizeBytes = content.size,
                    content = content
                ))

                return UACreateOrderFileRequest.Response(file.id!!)
            }
        ))
    }
}

@Servant class ServeUAUpdateOrderFile(val fileRepo: UAOrderFileRepository) : BitchyProcedure() {
    override fun serve() {
        fuckCustomer(FuckCustomerParams(
            bpc = bpc, makeRequest = {UAUpdateOrderFileRequest()},
            runShit = fun(ctx, req): UAUpdateOrderFileRequest.Response {
                val file = fileRepo.findOrDie(req.fileID.value)
                // TODO:vgrechka @security Check permissions

                file.title = req.fields1.title.value
                file.details = req.fields1.details.value
                if (req.file.valueKind == FileFieldValueKind.PROVIDED) {
                    val content = Base64.getDecoder().decode(req.file.base64)
                    file.sha1 = Hashing.sha1().hashBytes(content).toString()
                    file.sizeBytes = content.size
                    file.content = content
                }
                file.touch()

                return UAUpdateOrderFileRequest.Response(file.toRTO())
            }
        ))
    }
}


//@RemoteProcedureFactory fun serveCustomerAddUAOrderFile() = customerProcedure(
//    {CustomerAddUAOrderFileRequest()},
//    runShit = fun(ctx, req): AddUAOrderFileRequestBase.Response {
//        return serveAddUAOrderFile(UserKind.CUSTOMER, ctx, req)
//    }
//)
//
//@RemoteProcedureFactory fun serveWriterAddUAOrderFile() = writerProcedure(
//    {WriterAddUAOrderFileRequest()},
//    runShit = fun(ctx, req): AddUAOrderFileRequestBase.Response {
//        return serveAddUAOrderFile(UserKind.WRITER, ctx, req)
//    }
//)

//private fun serveAddUAOrderFile(callingUserKind: UserKind, ctx: ProcedureContext, req: AddUAOrderFileRequestBase): AddUAOrderFileRequestBase.Response {
//    val content = Base64.getDecoder().decode(req.file.base64)
//    val fileID = FILES.let {t->
//        ctx.insertShit("Insert file", t) {it
//            .set(t.NAME, req.file.fileName)
//            .set(t.TITLE, req.title.value)
//            .set(t.ADMIN_NOTES, "")
//            .set(t.DETAILS, req.details.value)
//            .set(t.CONTENT, content)
//            .set(t.SIZE_BYTES, content.size)
//            .set(t.MIME, "application/octet-stream")
//            .set(t.SHA1, Hashing.sha1().hashBytes(content).toString())
//            .returnID(t)
//        }
//    }
//
//    val orderID = req.orderID.value.toLong()
//
//    val areaID = selectUAOrderAreaByName(ctx, orderID, userKindToAreaName(ctx.user_killme.kind)).id
//
//    val seenAsFrom = callingUserKind.toJOOQ()
//
//    val orderFileID = UA_ORDER_FILES.let {t->
//        ctx.insertShit("Insert order file", t) {it
//            .set(t.UA_ORDER_ID, orderID)
//            .set(t.FILE_ID, fileID)
//            .set(t.UA_ORDER_AREA_ID, areaID)
//            .set(t.SEEN_AS_FROM, seenAsFrom)
//            .returnID(t)
//        }
//    }
//
//    insertFileUserPermission(ctx, fileID, ctx.user_killme.id.toLong())
//
//    return AddUAOrderFileRequestBase.Response(orderFileID.toString())
//}
























