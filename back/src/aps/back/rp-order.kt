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
                    file.name = req.file.fileName
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

@Servant class ServeUADeleteOrderFile(val fileRepo: UAOrderFileRepository) : BitchyProcedure() {
    override fun serve() {
        fuckCustomer(FuckCustomerParams(
            bpc = bpc, makeRequest = {UADeleteOrderFileRequest()},
            runShit = fun(ctx, req): DeleteRequest.Response {
                val file = fileRepo.findOrDie(req.id.value)
                // TODO:vgrechka @security Check permissions
                fileRepo.delete(file)
                return DeleteRequest.Response()
            }
        ))
    }
}

























