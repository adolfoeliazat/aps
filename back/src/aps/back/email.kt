package aps.back

import aps.*
import into.kommon.*
import java.util.*

object EmailMatumba {
    val log by logger()
    val sentEmails = Collections.synchronizedList(mutableListOf<Email>())

    fun send(email: Email) {
        if (requestShit.commonRequestFields.fakeEmail) {
            return sentEmails.add(email).toUnit()
        }

        try {
            Postmark.send(email)
        } catch(e: Exception) {
            log.error("Failed to send email: ${e.message}", e)
        }
    }
}


