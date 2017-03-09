package aps.front

import aps.*
import into.kommon.*

fun send(req: RecreateTestDatabaseSchemaRequest): Promisoid<GenericResponse> = callDangerousMatumba(req)
fun send(req: ResetTestDatabaseRequest): Promisoid<GenericResponse> = callDangerousMatumba(req)
fun send(token: String?, req: SignUpRequest): Promisoid<FormResponse2<SignUpRequest.Response>> = _send(token, req)
fun sendSafe(token: String?, req: SignUpRequest): Promisoid<FormResponse2<GenericResponse>> = _sendSafe(token, req)
fun send(token: String?, req: SignInWithPasswordRequest): Promisoid<FormResponse2<SignInResponse>> = _send(token, req)
fun sendSafe(token: String?, req: SignInWithPasswordRequest): Promisoid<FormResponse2<SignInResponse>> = _sendSafe(token, req)
//fun send(token: String?, req: UACustomerCreateOrderRequest): Promisoid<FormResponse2<UACustomerCreateOrderRequest.Response>> = _send(token, req)
//fun send(token: String?, req: UACreateOrderFileRequest): Promisoid<FormResponse2<UACreateOrderFileRequest.Response>> = _send(token, req)
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
//suspend fun send(req: ImposeNextGeneratedPasswordRequest): ImposeNextGeneratedPasswordRequest.Response = callDangerousMatumba2(req)
suspend fun send(req: ImposeNextRequestErrorRequest): GenericResponse = callDangerousMatumba2(req)
suspend fun send(req: ImposeNextGeneratedConfirmationSecretRequest): ImposeNextGeneratedConfirmationSecretRequest.Response = callDangerousMatumba2(req)
suspend fun send(req: ImposeNextRequestTimestampRequest): ImposeNextRequestTimestampRequest.Response = callDangerousMatumba2(req)
suspend fun send(req: ConfirmOrderRequest): FormResponse2<ConfirmOrderRequest.Response> = _send2(null, req)
suspend fun send(req: SaveCapturedVisualShitRequest): SaveCapturedVisualShitRequest.Response = await(sendDangerousJSONProcedure(req))
suspend fun sendUACustomerGetOrderFiles(req: ItemsRequest): FormResponse2<ItemsResponse<UAOrderFileRTO>> = _send3SofteningShit(req, "UACustomerGetOrderFiles")
suspend fun sendUAAdminGetOrders(req: ItemsRequest): FormResponse2<ItemsResponse<UAOrderRTO>> = _send3SofteningShit(req, "UAAdminGetOrders")
suspend fun sendGetStoreItems(req: ItemsRequest): FormResponse2<ItemsResponse<UAOrderRTO>> = _send3SofteningShit(req, "GetStoreItems")
suspend fun sendGetBids(req: ItemsRequest): FormResponse2<ItemsResponse<BidRTO>> = _send3SofteningShit(req, "GetBids")
suspend fun sendGetUsers(req: ItemsRequest): FormResponse2<ItemsResponse<UserRTO>> = _send3SofteningShit(req, "GetUsers")
suspend fun sendGetUserParamsHistoryItems(req: ItemsRequest): FormResponse2<ItemsResponse<UserParamsHistoryItemRTO>> = _send3SofteningShit(req, "GetUserParamsHistoryItems")
suspend fun send(req: TestTakeTestPointSnapshotRequest): TestTakeTestPointSnapshotRequest.Response = await(callDangerousMatumba(req))
suspend fun send(req: TestRestoreTestPointSnapshotRequest): TestRestoreTestPointSnapshotRequest.Response = await(callDangerousMatumba(req))
suspend fun send(token: String?, req: SignInWithTokenRequest): FormResponse2<SignInResponse> = _send2(token, req)
suspend fun send(req: TestCodeFiddleRequest): TestCodeFiddleRequest.Response = callDangerousMatumba2(req)
suspend fun send(req: TestGetFileUploadDataRequest): TestGetFileUploadDataRequest.Response = callDangerousMatumba2(req)
suspend fun sendUACreateOrderFile(req: UAOrderFileParamsRequest): FormResponse2<UACreateOrderFileResponse> = _send3(req, procName = "UACreateOrderFile")
suspend fun send(req: UADownloadOrderFileRequest): FormResponse2<DownloadFileResponse> = _send3(req)
suspend fun send(req: UACustomerSendOrderDraftForApprovalRequest): FormResponse2<UACustomerSendOrderDraftForApprovalRequest.Response> = _send3(req)
suspend fun send(req: UAAdminGetStuffToDoRequest): FormResponse2<UAAdminGetStuffToDoRequest.Response> = _send3(req)


suspend fun <Res> _askMiranda(p: Any): Res =
    callDangerousMatumba2("Miranda", ObjectRequest()-{o->
        o.params.value = p
    })

suspend fun <Res> _askRegina(p: Any): FormResponse2<Res> =
    _send3SofteningShit(ObjectRequest()-{o->
        o.params.value = p
    }, procName = "Regina")

fun <T> _simplifyFormResponseMeat(res: FormResponse2<SingleValueResponse<T>>): FormResponse2<T> {
    return when (res) {
        is FormResponse2.Shitty -> FormResponse2.Shitty<T>(res.error, res.fieldErrors)
        is FormResponse2.Hunky -> FormResponse2.Hunky<T>(res.meat.value)
    }
}

private fun <T, R> sendDangerousJSONProcedure(req: T): Promisoid<R> = async {
    val jpreq = JsonProcedureRequest()-{o->
        o.json.value = jsonize(req)
    }
    val jpres: JsonProcedureRequest.Response = await(callDangerousMatumba(jpreq))
    dejsonize<R>(jpres.json)
}

private suspend fun <Req: RequestMatumba, Meat> _send2(token: String?, req: Req, procName: String? = null): FormResponse2<Meat> {
    return await(_send(token, req, procName = procName))
}

private suspend fun <Req: RequestMatumba, Meat> _send3(req: Req, procName: String? = null): FormResponse2<Meat> {
    return _send2(Globus.worldMaybe?.tokenMaybe, req, procName = procName)
}

private suspend fun <Req: RequestMatumba, Meat> _send3SofteningShit(req: Req, procName: String? = null): FormResponse2<Meat> {
    return try {
        _send2(Globus.worldMaybe?.tokenMaybe, req, procName = procName)
    } catch (e: dynamic) {
        console.error("_send3SofteningShit (req::class = ${req::class.simpleName}; procName = $procName)", e.message)
        console.error(e)
        FormResponse2.Shitty<Meat>(const.msg.serviceFuckedUp, fieldErrors = listOf())
    }
}

suspend fun imposeNextRequestTimestampIfInTest() {
    if (isTest()) {
        TestGlobal.currentTestShit.imposeNextRequestTimestamp()
    }
}

private fun <Req: RequestMatumba, Meat> _send(token: String?, req: Req, procName: String? = null) = async<FormResponse2<Meat>> {
    imposeNextRequestTimestampIfInTest()
    Globus.lastAttemptedRPCName = ctorName(req)
    val res: FormResponse = await(callMatumba(procName ?: remoteProcedureNameForRequest(req), req, token))
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

