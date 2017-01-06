package aps.back

import aps.*
import into.kommon.*

@RemoteProcedureFactory fun jsonProcedure() = testProcedure(
    JsonProcedureRequest(),
    runShit = fun(ctx, req): JsonProcedureRequest.Response {
        val reqJSON = req.json.value
        val mr = Regex("^\\{\"\\\$\\\$\\\$class\":\\s*\"([^\"]*)\"").find(reqJSON) ?: wtf("Cannot figure out request class name from JSON")
        val requestClassName = mr.groupValues[1]
        val requestClass = Class.forName(requestClassName)
        val res: Any = when (requestClass) {
            VisualShitCapturedRequest::class.java -> serveVisualShitCapturedRequest(shittyObjectMapper.readValue(reqJSON, VisualShitCapturedRequest::class.java))
            SaveCapturedVisualShitRequest::class.java -> serveSaveCapturedVisualShitRequest(shittyObjectMapper.readValue(reqJSON, SaveCapturedVisualShitRequest::class.java))
            GetCapturedVisualShitRequest::class.java -> serveGetCapturedVisualShitRequest(shittyObjectMapper.readValue(reqJSON, GetCapturedVisualShitRequest::class.java))
            DiffCapturedVisualShitWithSavedRequest::class.java -> serveDiffCapturedVisualShitWithSavedRequest(shittyObjectMapper.readValue(reqJSON, DiffCapturedVisualShitWithSavedRequest::class.java))
            else -> wtf("requestClass: $requestClass")
        }
        val resJSON = shittyObjectMapper.writeValueAsString(res)
        return JsonProcedureRequest.Response(resJSON)
    }
)

