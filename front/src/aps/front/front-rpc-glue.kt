package aps.front

import aps.*
import into.kommon.*

fun send(req: RecreateTestDatabaseSchemaRequest): Promise<GenericResponse> =
    callDangerousMatumba(req)

fun send(req: ResetTestDatabaseRequest): Promise<GenericResponse> =
    callDangerousMatumba(req)

fun send(req: ImposeNextRequestErrorRequest): Promise<GenericResponse> =
    callDangerousMatumba(req)

fun send(token: String, req: LoadUAOrderRequest): Promise<ZimbabweResponse<LoadUAOrderRequest.Response>> =
    callZimbabwe(req, token)

fun sendCustomerGetUAOrderFiles(token: String, req: ItemsRequest<CustomerFileFilter>): Promise<ZimbabweResponse<ItemsResponse<UAOrderFileRTO>>> =
    callZimbabwe("customerGetUAOrderFiles", req, token)

fun send(token: String?, req: SignUpRequest): Promise<FormResponse2<GenericResponse>> {"__async"
    return __asyncResult(__await(_send(token, req)))
}

fun sendSafe(token: String?, req: SignUpRequest): Promise<FormResponse2<GenericResponse>> {"__async"
    return __asyncResult(__await(_sendSafe(token, req)))
}

fun send(token: String?, req: SignInWithPasswordRequest): Promise<FormResponse2<SignInResponse>> {"__async"
    return __asyncResult(__await(_send(token, req)))
}

fun sendSafe(token: String?, req: SignInWithPasswordRequest): Promise<FormResponse2<SignInResponse>> {"__async"
    return __asyncResult(__await(_sendSafe(token, req)))
}

fun send(token: String?, req: CustomerCreateUAOrderRequest): Promise<FormResponse2<CustomerCreateUAOrderRequest.Response>> =
    _send(token, req)

fun send(token: String?, req: CustomerAddUAOrderFileRequest): Promise<FormResponse2<AddUAOrderFileRequestBase.Response>> =
    _send(token, req)

fun send(token: String?, req: WriterAddUAOrderFileRequest): Promise<FormResponse2<AddUAOrderFileRequestBase.Response>> =
    _send(token, req)

fun send(req: PingRequest): Promise<FormResponse2<GenericResponse>> =
    _send(Globus.worldMaybe?.tokenMaybe, req)

fun send(req: DeleteRequest): Promise<ZimbabweResponse<DeleteRequest.Response>> =
    callZimbabwe(req, Globus.world.token)

fun send(req: VisualShitCapturedRequest): Promise<VisualShitCapturedRequest.Response> =
    sendDangerousJSONProcedure(req)

fun send(req: SaveCapturedVisualShitRequest): Promise<SaveCapturedVisualShitRequest.Response> =
    sendDangerousJSONProcedure(req)

fun send(req: GetCapturedVisualShitRequest): Promise<GetCapturedVisualShitRequest.Response> =
    sendDangerousJSONProcedure(req)

fun send(req: GetCurrentCapturedVisualShitRequest): Promise<GetCurrentCapturedVisualShitRequest.Response> =
    sendDangerousJSONProcedure(req)

fun send(req: DiffCapturedVisualShitWithSavedRequest): Promise<DiffCapturedVisualShitWithSavedRequest.Response> =
    sendDangerousJSONProcedure(req)


private fun <T, R> sendDangerousJSONProcedure(req: T): Promise<R> = async {
    val jpreq = JsonProcedureRequest()-{o->
        o.json.value = jsonize(req)
    }
    val jpres: JsonProcedureRequest.Response = await(callDangerousMatumba(jpreq))
    dejsonize<R>(jpres.json)
}


private fun <Req: RequestMatumba, Meat> _send(token: String?, req: Req): Promise<FormResponse2<Meat>> {"__async"
    Globus.lastAttemptedRPCName = ctorName(req)
    val res: FormResponse = __await(callMatumba(req, token))
    return __asyncResult(when (res) {
                             is FormResponse.Shitty -> {
                                 FormResponse2.Shitty(res.error, res.fieldErrors)
                             }
                             is FormResponse.Hunky<*> -> {
                                 FormResponse2.Hunky(res.meat as Meat)
                             }
                         })
}

private fun <Req: RequestMatumba, Meat> _sendSafe(token: String?, req: Req): Promise<FormResponse2<Meat>> {"__async"
    return __asyncResult(
        try {
            __await(_send<Req, Meat>(token, req))
        } catch(e: Throwable) {
            FormResponse2.Shitty<Meat>(const.msg.serviceFuckedUp, listOf())
        })
}

