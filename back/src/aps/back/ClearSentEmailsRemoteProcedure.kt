/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.GenericResponse
import aps.ImposeNextGeneratedPasswordRequest
import aps.Request

class ClearSentEmailsRemoteProcedure() : RemoteProcedure<Request, GenericResponse>() {
    override val access: Access = Access.SYSTEM

    override fun doStuff() {
        EmailMatumba.sentEmails.clear()
    }
}



