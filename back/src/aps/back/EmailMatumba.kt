/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import java.util.*

object EmailMatumba {
    val sentEmails = Collections.synchronizedList(mutableListOf<Email>())

    fun send(email: Email) {
        if (GlobalMatumba.mode == GlobalMatumba.Mode.DEBUG) {
            return sentEmails.add(email) /ignore
        }

        imf("Production email sending")
    }
}


class GetSentEmailsRemoteProcedure : RemoteProcedure<Request, GetSentEmailsRequest.Response>() {
    override val access: Access = Access.SYSTEM
    override val needsDBConnection = false

    override fun doStuff() {
        res.emails = EmailMatumba.sentEmails
    }
}

