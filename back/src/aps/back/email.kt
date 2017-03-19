package aps.back

import aps.*
import java.util.*

object EmailMatumba {
    val log by logger()
    val sentEmails = Collections.synchronizedList(mutableListOf<Email>())

    fun send(email: Email) {
        if (RequestGlobus.commonRequestFields.fakeEmail) {
            return sentEmails.add(email).toUnit()
        }

        try {
            Postmark.send(email)
        } catch(e: Exception) {
            log.error("Failed to send email: ${e.message}", e)
        }
    }
}


