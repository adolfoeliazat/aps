package aps.front

import aps.*

fun send(req: RecreateTestDatabaseSchemaRequest): Promise<GenericResponse> =
    callDangerousMatumba(req)

fun send(req: ResetTestDatabaseRequest): Promise<GenericResponse> =
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

fun send(req: DeleteUAOrderFileRequest): Promise<ZimbabweResponse<DeleteUAOrderFileRequest.Response>> =
    callZimbabwe(req, Globus.world.token)


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

