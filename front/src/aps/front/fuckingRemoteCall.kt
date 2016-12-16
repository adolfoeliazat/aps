package aps.front

import aps.*

object fuckingRemoteCall {

    fun loadTestShit(id: String): Promise<String> = sendShit(json(
        "proc" to "loadTestShit",
        "id" to id
    ))

    fun updateTestShit(id: String, newValue: String): Promise<Unit> = sendShit(json(
        "proc" to "updateTestShit",
        "id" to id,
        "newValue" to newValue
    ))

    fun robotClickOnChrome(): Promise<Unit> = sendShit(json(
        "proc" to "robotClickOnChrome"
    ))

    fun robotTypeTextCRIntoWindowTitledOpen(text: String): Promise<Unit> = sendShit(json(
        "proc" to "robotTypeTextCRIntoWindowTitledOpen",
        "text" to text
    ))

    fun resetLastDownloadedFile(): Promise<Unit> = sendShit(json(
        "proc" to "resetLastDownloadedFile"
    ))

    fun getLastDownloadedFileID(): Promise<String?> = sendShit(json(
        "proc" to "getLastDownloadedFileID"
    ))

    private fun <T> sendShit(request: Json): Promise<T> = async {
        val res = await(send(FuckingRemoteProcedureRequest()-{o->
            o.json.value = JSON.stringify(request)
        }))
        dejsonize(res.json, "fuckingRemoteCall: ${request["proc"]}")
    }

}

