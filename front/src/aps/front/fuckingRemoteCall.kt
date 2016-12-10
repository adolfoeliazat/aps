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

    private fun <T> sendShit(request: Json): Promise<T> = async {
        val res = await(send(FuckingRemoteProcedureRequest()-{o->
            o.json.value = JSON.stringify(request)
        }))
        dejsonize(res.json)!!
    }

}

