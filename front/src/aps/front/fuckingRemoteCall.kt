package aps.front

import aps.*

object fuckingRemoteCall {

    fun loadTestShit(id: String): Promisoid<String?> = sendShit(json(
        "proc" to "loadTestShit",
        "id" to id
    ))

    fun updateTestShit(id: String, newValue: String): Promisoid<Unit> = sendShit(json(
        "proc" to "updateTestShit",
        "id" to id,
        "newValue" to newValue
    ))

    fun robotClickOnChrome(): Promisoid<Unit> = sendShit(json(
        "proc" to "robotClickOnChrome"
    ))

    fun robotTypeTextCRIntoWindowTitledOpen(text: String): Promisoid<Unit> = sendShit(json(
        "proc" to "robotTypeTextCRIntoWindowTitledOpen",
        "text" to text
    ))

    fun resetLastDownloadedFile(): Promisoid<Unit> = sendShit(json(
        "proc" to "resetLastDownloadedFile"
    ))

    fun getLastDownloadedPieceOfShit(): Promisoid<PieceOfShitDownload?> = sendShit(json(
        "proc" to "getLastDownloadedPieceOfShit"
    ))

    fun executeSQL(descr: String, sql: String): Promisoid<Unit> = sendShit(json(
        "proc" to "executeSQL",
        "descr" to descr,
        "sql" to sql
    ))

    fun luceneParseRussian(text: String): Promisoid<List<LuceneParseToken>> = sendShit(json(
        "proc" to "luceneParseRussian",
        "text" to text
    ))


    private fun <T> sendShit(request: Json): Promisoid<T> = async {
        val res = await(send(FuckingRemoteProcedureRequest()-{o->
            o.json.value = JSON.stringify(request)
        }))
        dejsonize<T>(res.json, "fuckingRemoteCall: ${request["proc"]}")
    }

}

