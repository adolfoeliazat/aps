package aps.back

import aps.*

@RemoteProcedureFactory fun jsonProcedure() = testProcedure(
    {JsonProcedureRequest()},
    runShit = fun(ctx, req): JsonProcedureRequest.Response {
        val reqJSON = req.json.value
        val mr = Regex("^\\{\"\\\$\\\$\\\$class\":\\s*\"([^\"]*)\"").find(reqJSON) ?: wtf("Cannot figure out request class name from JSON")
        val requestClassName = mr.groupValues[1]
        val requestClass = Class.forName(requestClassName)
        val res: Any = when (requestClass) {
            VisualShitCapturedRequest::class.java -> serveVisualShitCapturedRequest(_shittyObjectMapper.readValue(reqJSON, VisualShitCapturedRequest::class.java))
            SaveCapturedVisualShitRequest::class.java -> serveSaveCapturedVisualShitRequest(_shittyObjectMapper.readValue(reqJSON, SaveCapturedVisualShitRequest::class.java))
            GetCapturedVisualShitRequest::class.java -> serveGetCapturedVisualShitRequest(_shittyObjectMapper.readValue(reqJSON, GetCapturedVisualShitRequest::class.java))
            CapturedVisualShitExistsRequest::class.java -> serveCapturedVisualShitExistsRequest(_shittyObjectMapper.readValue(reqJSON, CapturedVisualShitExistsRequest::class.java))
            GetCurrentCapturedVisualShitRequest::class.java -> serveGetCurrentCapturedVisualShitRequest(_shittyObjectMapper.readValue(reqJSON, GetCurrentCapturedVisualShitRequest::class.java))
            DiffCapturedVisualShitWithSavedRequest::class.java -> serveDiffCapturedVisualShitWithSavedRequest(_shittyObjectMapper.readValue(reqJSON, DiffCapturedVisualShitWithSavedRequest::class.java))
            MoveMouseAwayFromPageRequest::class.java -> serveMoveMouseAwayFromPageRequest(_shittyObjectMapper.readValue(reqJSON, MoveMouseAwayFromPageRequest::class.java))
            ReturnMouseWhereItWasRequest::class.java -> serveReturnMouseWhereItWasRequest(_shittyObjectMapper.readValue(reqJSON, ReturnMouseWhereItWasRequest::class.java))
            HardenScreenHTMLRequest::class.java -> serveHardenScreenHTMLRequest(_shittyObjectMapper.readValue(reqJSON, HardenScreenHTMLRequest::class.java))
            else -> wtf("requestClass: $requestClass")
        }
        val resJSON = _shittyObjectMapper.writeValueAsString(res)
        return JsonProcedureRequest.Response(resJSON)
    }
)

