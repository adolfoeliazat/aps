package aps.back.spike

import aps.*
import aps.back.*

fun main(args: Array<String>) {
    Postmark.send(Email("pizdatest@mailinator.com", "Important message", "Fuck you. Importantly."))
    println("\nOK")
}


//    val session = Session.getInstance(Properties()-{o->
////        o["mail.from"] = "..."
//        o["mail.smtp.host"] = "smtp.postmarkapp.com"
//        o["mail.smtp.port"] = "2525"
////        o["mail.user"] = "...."
//    }, object :Authenticator() {
//        override fun getPasswordAuthentication(): PasswordAuthentication {
//            return PasswordAuthentication("...", "...")
//        }
//    })
//    val msg = MimeMessage(session)
//    msg.setFrom(InternetAddress("..."))
//    msg.setRecipients(Message.RecipientType.TO, "...")
//    msg.subject = "Fucking test"
//    msg.setText("Fucking passed")
//    Transport.send(msg)


