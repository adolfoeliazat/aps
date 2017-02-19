/*
 * APS
 *
 * (C) Copyright 2015-2017 Vladimir Grechka
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
                    sha256 = Hashing.sha256().hashBytes(content).toString(),
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
                    file.sha256 = Hashing.sha256().hashBytes(content).toString()
                    file.sizeBytes = content.size
                    file.content = content
                }
                file.touch()

                return UAUpdateOrderFileRequest.Response(file.toRTO(listOf()))
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

@Servant class ServeUADownloadOrderFile(val fileRepo: UAOrderFileRepository) : BitchyProcedure() {
    override fun serve() {
        fuckCustomer(FuckCustomerParams(
            bpc = bpc, makeRequest = {UADownloadOrderFileRequest()},
            runShit = fun(ctx, req): DownloadFileResponse {
                val file = fileRepo.findOrDie(req.fileID.value)
                // TODO:vgrechka @security Check permissions
                return DownloadFileResponse(
                    fileName = file.name,
                    base64 = Base64.getEncoder().encodeToString(file.content),
                    sha256 = Hashing.sha256().hashBytes(file.content).toString()
                )
            }
        ))
    }
}

@Servant class ServeUACustomerSendOrderDraftForApproval(val orderRepo: UAOrderRepository) : BitchyProcedure() {
    override fun serve() {
        fuckCustomer(FuckCustomerParams(
            bpc = bpc, makeRequest = {UACustomerSendOrderDraftForApprovalRequest()},
            runShit = fun(ctx, req): UACustomerSendOrderDraftForApprovalRequest.Response {
                // TODO:vgrechka @security Check permissions
                val order = orderRepo.findOrDie(req.orderID.value)
                order.state = UAOrderState.WAITING_ADMIN_APPROVAL
                return UACustomerSendOrderDraftForApprovalRequest.Response()
            }
        ))
    }
}

@Servant class ServeReturnOrderToCustomerForFixing(val orderRepo: UAOrderRepository) : BitchyProcedure() {
    override fun serve() {
        fuckAdmin(FuckAdminParams(
            bpc = bpc, makeRequest = {ReturnOrderToCustomerForFixingRequest()},
            runShit = fun(ctx, req): ReturnOrderToCustomerForFixingRequest.Response {
                val order = orderRepo.findOrDie(req.orderID.value)
                order.state = UAOrderState.RETURNED_TO_CUSTOMER_FOR_FIXING
                order.whatShouldBeFixedByCustomer = req.rejectionReason.value
                return ReturnOrderToCustomerForFixingRequest.Response()
            }
        ))
    }
}
























