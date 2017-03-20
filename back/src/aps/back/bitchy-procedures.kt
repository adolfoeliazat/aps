package aps.back

import aps.*
import org.springframework.data.jpa.repository.JpaContext
import java.sql.Timestamp
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class FuckAnonymousParams<Req : RequestMatumba, out Res : CommonResponseFields>(
    val bpc: BitchyProcedureContext,
    val makeRequest: (ProcedureContext) -> Req,
    val runShit: (ProcedureContext, Req) -> Res)

fun <Req : RequestMatumba, Res : CommonResponseFields>
    fuckAnonymous(p: FuckAnonymousParams<Req, Res>)
{
    fuckSomeone(FuckSomeoneParams(
        bpc = p.bpc,
        req = p.makeRequest,
        runShit = p.runShit,
        wrapInFormResponse = true,
        needsDB = true,
        needsDangerousToken = false,
        needsUser = NeedsUser.NO,
        userKinds = setOf(UserKind.CUSTOMER, UserKind.WRITER, UserKind.ADMIN),
        considerNextRequestTimestampFiddling = true,
        logRequestJSON = true
    ))
}

class FuckAnyUserParams<Req : RequestMatumba, out Res : CommonResponseFields>(
    val bpc: BitchyProcedureContext,
    val makeRequest: (ProcedureContext) -> Req,
    val runShit: (ProcedureContext, Req) -> Res)

fun <Req : RequestMatumba, Res : CommonResponseFields>
    fuckAnyUser(p: FuckAnyUserParams<Req, Res>)
{
    fuckSomeone(FuckSomeoneParams(
        bpc = p.bpc,
        req = p.makeRequest,
        runShit = p.runShit,
        wrapInFormResponse = true,
        needsDB = true,
        needsDangerousToken = false,
        needsUser = NeedsUser.YES,
        userKinds = setOf(UserKind.CUSTOMER, UserKind.WRITER, UserKind.ADMIN),
        considerNextRequestTimestampFiddling = true,
        logRequestJSON = true
    ))
}

class FuckAnyUserOrAnonymousParams<Req : RequestMatumba, out Res : CommonResponseFields>(
    val bpc: BitchyProcedureContext,
    val makeRequest: (ProcedureContext) -> Req,
    val runShit: (ProcedureContext, Req) -> Res)

fun <Req : RequestMatumba, Res : CommonResponseFields>
    fuckAnyUserOrAnonymous(p: FuckAnyUserOrAnonymousParams<Req, Res>)
{
    fuckSomeone(FuckSomeoneParams(
        bpc = p.bpc,
        req = p.makeRequest,
        runShit = p.runShit,
        wrapInFormResponse = true,
        needsDB = true,
        needsDangerousToken = false,
        needsUser = NeedsUser.MAYBE,
        userKinds = setOf(UserKind.CUSTOMER, UserKind.WRITER, UserKind.ADMIN),
        considerNextRequestTimestampFiddling = true,
        logRequestJSON = true
    ))
}

class FuckAdminParams<Req : RequestMatumba, out Res : CommonResponseFields>(
    val bpc: BitchyProcedureContext,
    val makeRequest: (ProcedureContext) -> Req,
    val runShit: (ProcedureContext, Req) -> Res)

fun <Req : RequestMatumba, Res : CommonResponseFields>
    fuckAdmin(p: FuckAdminParams<Req, Res>)
{
    fuckSomeone(FuckSomeoneParams(
        bpc = p.bpc,
        req = p.makeRequest,
        runShit = p.runShit,
        wrapInFormResponse = true,
        needsDB = true,
        needsDangerousToken = false,
        needsUser = NeedsUser.YES,
        userKinds = setOf(UserKind.ADMIN),
        considerNextRequestTimestampFiddling = true,
        logRequestJSON = true
    ))
}

class FuckCustomerParams<Req : RequestMatumba, out Res : CommonResponseFields>(
    val bpc: BitchyProcedureContext,
    val makeRequest: (ProcedureContext) -> Req,
    val needsUser: NeedsUser = NeedsUser.YES,
    val runShit: (ProcedureContext, Req) -> Res)

fun <Req : RequestMatumba, Res : CommonResponseFields>
    fuckCustomer(p: FuckCustomerParams<Req, Res>)
{
    fuckSomeone(FuckSomeoneParams(
        bpc = p.bpc,
        req = p.makeRequest,
        runShit = p.runShit,
        wrapInFormResponse = true,
        needsDB = true,
        needsDangerousToken = false,
        needsUser = p.needsUser,
        userKinds = setOf(UserKind.CUSTOMER),
        considerNextRequestTimestampFiddling = true,
        logRequestJSON = true
    ))
}





