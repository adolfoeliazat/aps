/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

class HiRequest(var name: String? = null)

class HiResponse {
    var saying: String? = null
    var backendInstance: String? = null

    override fun toString(): String{
        return "HiResponse(saying='$saying', backendInstance='$backendInstance')"
    }
}


