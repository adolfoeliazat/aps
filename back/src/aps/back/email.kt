package aps.back

import aps.*
import into.kommon.*
import java.util.*

object EmailMatumba {
    val sentEmails = Collections.synchronizedList(mutableListOf<Email>())

    fun send(email: Email) {
        if (requestShit.commonRequestFields.fakeEmail) {
            return sentEmails.add(email).toUnit()
        }

        Postmark.send(email)
    }
}


