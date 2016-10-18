/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

fun jsFacing_initDebugMailbox() {
    jshit.debugPanes.set(json("name" to "initDebugFunctions-mailbox", "parentJqel" to Shitus.byid("underFooter"), "element" to Shitus.updatableElement(json(
        "renderCtor" to renderCtor@{update: dynamic ->
        var content = null

        jshit.debugCheckEmail = debugCheckEmail@{"__async"
            val emails: List<Email>
            if (jshit.isHotReloading) {
                emails = jshit.debugCheckEmail_cachedEmails
            } else {
                val res = __await<GetSentEmailsRequest.Response>(GetSentEmailsRequest.send())
//                console.warn("qqqqqqqqqq", res)
                emails = res.emails
//                if (emails.size > 0) {
//                    console.warn("wwwwwwwww", emails.first())
//                }
                jshit.debugCheckEmail_cachedEmails = emails
            }
            if (emails.isEmpty()) {
                content = null
                update()
                return@debugCheckEmail
            }

            content = Shitus.diva.apply(null, js("[]").concat(
                json("tame" to "debugMailbox", "style" to json("marginTop" to 10)),
                Shitus.diva(json("style" to json("fontWeight" to "bold", "background" to Color.TEAL_100)), "Mailbox"),
                emails.mapIndexed({emailIndex: Int, email: Email ->
                    Shitus.diva(json("tame" to "email${jshit.sufindex(emailIndex)}", "style" to json("marginTop" to 5, "paddingBottom" to 5, "borderBottom" to "2px dotted ${Color.GRAY_500}")),
                        Shitus.diva(json("style" to json("background" to Color.WHITE, "marginBottom" to 5)),
                            Shitus.hor1(json("tame" to "section-to"),
                                Shitus.spanc(json("tame" to "label", "style" to json("fontWeight" to "bold"), "content" to "To:")),
                                Shitus.spanc(json("tame" to "value", "content" to email.to))),
                            Shitus.hor1(json("tame" to "section-subject"),
                                Shitus.spanc(json("tame" to "label", "style" to json("fontWeight" to "bold"), "content" to t("Subject:"))),
                                Shitus.spanc(json("tame" to "value", "content" to email.subject)))),
                        Shitus.diva(json("tame" to "body", "tattrs" to json("html" to email.html)), jshit.rawHtml(email.html)))
                }).toJSArray()))

            update()
        }

        return@renderCtor { Shitus.diva(json(), content) }
    }))))
}

