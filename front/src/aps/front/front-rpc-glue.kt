package aps.front

import aps.*
import into.kommon.*

fun send(req: RecreateTestDatabaseSchemaRequest): Promisoid<GenericResponse> = callDangerousMatumba(req)
fun send(req: ResetTestDatabaseRequest): Promisoid<GenericResponse> = callDangerousMatumba(req)
fun send(req: ImposeNextRequestErrorRequest): Promisoid<GenericResponse> = callDangerousMatumba(req)
fun send(token: String, req: LoadUAOrderRequest): Promisoid<ZimbabweResponse<LoadUAOrderRequest.Response>> = callZimbabwe(req, token)
fun send(token: String?, req: SignUpRequest): Promisoid<FormResponse2<SignUpRequest.Response>> = _send(token, req)
fun sendSafe(token: String?, req: SignUpRequest): Promisoid<FormResponse2<GenericResponse>> = _sendSafe(token, req)
fun send(token: String?, req: SignInWithPasswordRequest): Promisoid<FormResponse2<SignInResponse>> = _send(token, req)
fun sendSafe(token: String?, req: SignInWithPasswordRequest): Promisoid<FormResponse2<SignInResponse>> = _sendSafe(token, req)
fun send(token: String?, req: UACustomerCreateOrderRequest): Promisoid<FormResponse2<UACustomerCreateOrderRequest.Response>> = _send(token, req)
fun send(token: String?, req: CustomerAddUAOrderFileRequest): Promisoid<FormResponse2<AddUAOrderFileRequestBase.Response>> = _send(token, req)
fun send(token: String?, req: WriterAddUAOrderFileRequest): Promisoid<FormResponse2<AddUAOrderFileRequestBase.Response>> = _send(token, req)
fun send(req: PingRequest): Promisoid<FormResponse2<GenericResponse>> = _send(Globus.worldMaybe?.tokenMaybe, req)
fun send(req: DeleteRequest): Promisoid<ZimbabweResponse<DeleteRequest.Response>> = callZimbabwe(req, Globus.world.token)
fun send(req: VisualShitCapturedRequest): Promisoid<VisualShitCapturedRequest.Response> = sendDangerousJSONProcedure(req)
fun sendp(req: SaveCapturedVisualShitRequest): Promisoid<SaveCapturedVisualShitRequest.Response> = sendDangerousJSONProcedure(req)
fun send(req: CapturedVisualShitExistsRequest): Promisoid<CapturedVisualShitExistsRequest.Response> = sendDangerousJSONProcedure(req)
fun send(req: GetCapturedVisualShitRequest): Promisoid<GetCapturedVisualShitRequest.Response> = sendDangerousJSONProcedure(req)
fun send(req: GetCurrentCapturedVisualShitRequest): Promisoid<GetCurrentCapturedVisualShitRequest.Response> = sendDangerousJSONProcedure(req)
fun send(req: DiffCapturedVisualShitWithSavedRequest): Promisoid<DiffCapturedVisualShitWithSavedRequest.Response> = sendDangerousJSONProcedure(req)
fun send(req: MoveMouseAwayFromPageRequest): Promisoid<MoveMouseAwayFromPageRequest.Response> = sendDangerousJSONProcedure(req)
fun send(req: ReturnMouseWhereItWasRequest): Promisoid<ReturnMouseWhereItWasRequest.Response> = sendDangerousJSONProcedure(req)
fun send(req: HardenScreenHTMLRequest): Promisoid<HardenScreenHTMLRequest.Response> = sendDangerousJSONProcedure(req)
fun send(token: String, req: TestCopyOrderFileToAreaRequest): Promisoid<FormResponse2<TestCopyOrderFileToAreaRequest.Response>> = _send(token, req)
fun send(req: TestTakeSnapshotRequest): Promisoid<TestTakeSnapshotRequest.Response> = callDangerousMatumba(req)
fun send(req: TestLoadSnapshotRequest): Promisoid<TestLoadSnapshotRequest.Response> = callDangerousMatumba(req)
fun send(req: GetSentEmailsRequest): Promisoid<GetSentEmailsRequest.Response> = callDangerousMatumba(req)
fun send(req: ClearSentEmailsRequest): Promisoid<GenericResponse> = callDangerousMatumba(req)
suspend fun send(req: TestSQLFiddleRequest): TestSQLFiddleRequest.Response = callDangerousMatumba2(req)
suspend fun send(req: ImposeNextGeneratedPasswordRequest): Promisoid<ImposeNextGeneratedPasswordRequest.Response> = callDangerousMatumba(req)
suspend fun send(req: ImposeNextGeneratedConfirmationSecretRequest): Promisoid<ImposeNextGeneratedConfirmationSecretRequest.Response> = callDangerousMatumba(req)
suspend fun send(req: ImposeNextRequestTimestampRequest): Promisoid<ImposeNextRequestTimestampRequest.Response> = callDangerousMatumba(req)
suspend fun send(req: ConfirmOrderRequest): FormResponse2<ConfirmOrderRequest.Response> = _send2(null, req)
suspend fun send(req: SaveCapturedVisualShitRequest): SaveCapturedVisualShitRequest.Response = await(sendDangerousJSONProcedure(req))
suspend fun sendUACustomerGetOrderFiles(token: String, req: ItemsRequest<CustomerFileFilter>): ZimbabweResponse<ItemsResponse<UAOrderFileRTO>> = await(callZimbabwe("UACustomerGetOrderFiles", req, token))
suspend fun send(req: TestTakeDBSnapshotRequest): TestTakeDBSnapshotRequest.Response = await(callDangerousMatumba(req))
suspend fun send(req: TestRestoreDBSnapshotRequest): TestRestoreDBSnapshotRequest.Response = await(callDangerousMatumba(req))
suspend fun send(token: String?, req: SignInWithTokenRequest): FormResponse2<SignInResponse> = _send2(token, req)
suspend fun send(req: TestCodeFiddleRequest): TestCodeFiddleRequest.Response = callDangerousMatumba2(req)


private fun <T, R> sendDangerousJSONProcedure(req: T): Promisoid<R> = async {
    val jpreq = JsonProcedureRequest()-{o->
        o.json.value = jsonize(req)
    }
    val jpres: JsonProcedureRequest.Response = await(callDangerousMatumba(jpreq))
    dejsonize<R>(jpres.json)
}

private suspend fun <Req: RequestMatumba, Meat> _send2(token: String?, req: Req): FormResponse2<Meat> {
    return await(_send(token, req))
}

private suspend fun <Req: RequestMatumba, Meat> _send3(req: Req): FormResponse2<Meat> {
    return _send2(Globus.worldMaybe?.tokenMaybe, req)
}

private fun <Req: RequestMatumba, Meat> _send(token: String?, req: Req) = async<FormResponse2<Meat>> {
    Globus.lastAttemptedRPCName = ctorName(req)
    val res: FormResponse = await(callMatumba(req, token))
    when (res) {
        is FormResponse.Shitty -> {
            FormResponse2.Shitty(res.error, res.fieldErrors)
        }
        is FormResponse.Hunky<*> -> {
            FormResponse2.Hunky(res.meat as Meat)
        }
    }
}

private fun <Req: RequestMatumba, Meat> _sendSafe(token: String?, req: Req): Promisoid<FormResponse2<Meat>> = async {
    try {
        await(_send<Req, Meat>(token, req))
    } catch(e: Throwable) {
        FormResponse2.Shitty<Meat>(const.msg.serviceFuckedUp, listOf())
    }

}

