/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*

var debugCheckEmail: () -> Promisoid<Unit> = {bitch("Debug mailbox is not initialized")}
var debugHideMailbox: () -> Unit = {bitch("Debug mailbox is not initialized")}


fun initDebugMailbox() {
    die("initDebugMailbox")
//    val PANE_NAME = "initDebugFunctions-mailbox"
//
//    debugPanes.put(PANE_NAME, byid(ELID_UNDER_FOOTER), oldShitAsToReactElementable(Shitus.updatableElement(json(
//        "renderCtor" to renderCtor@{update: dynamic ->
//        var content = null
//
//        debugCheckEmail = {async{
//            val emails: List<Email>
//            if (hrss.isHotReloading) {
//                emails = hrss.debugCheckEmail_cachedEmails
//            } else {
//                val res = await<GetSentEmailsRequest.Response>(GetSentEmailsRequest.send())
////                console.warn("qqqqqqqqqq", res)
//                emails = res.emails
////                if (emails.size > 0) {
////                    console.warn("wwwwwwwww", emails.first())
////                }
//                hrss.debugCheckEmail_cachedEmails = emails
//            }
//            if (emails.isEmpty()) {
//                content = null
//                update()
//                return@async
//            }
//
//            content = Shitus.diva.apply(null, js("[]").concat(
//                json("tame" to "debugMailbox", "style" to json("marginTop" to 10)),
//                Shitus.diva(json("style" to json("fontWeight" to "bold", "background" to Color.TEAL_100)), "Mailbox"),
//                emails.mapIndexed({emailIndex: Int, email: Email ->
//                    Shitus.diva(json("tame" to "email${Shitus.sufindex(emailIndex)}", "style" to json("marginTop" to 5, "paddingBottom" to 5, "borderBottom" to "2px dotted ${Color.GRAY_500}")),
//                        Shitus.diva(json("style" to json("background" to Color.WHITE, "marginBottom" to 5)),
//                            Shitus.hor1(json("tame" to "section-to"),
//                                Shitus.spanc(json("tame" to "label", "style" to json("fontWeight" to "bold"), "content" to "To:")),
//                                Shitus.spanc(json("tame" to "value", "content" to email.to))),
//                            Shitus.hor1(json("tame" to "section-subject"),
//                                Shitus.spanc(json("tame" to "label", "style" to json("fontWeight" to "bold"), "content" to "Subject:")),
//                                Shitus.spanc(json("tame" to "value", "content" to email.subject)))),
//                        Shitus.diva(json("tame" to "body", "tattrs" to json("html" to email.html)), rawHTML(email.html)))
//                }).toJSArray()))
//
//            update()
//        }}
//
//        debugHideMailbox = {
//            debugPanes.remove(PANE_NAME)
//            debugCheckEmail = {bitch("Debug mailbox is not initialized")}
//            debugHideMailbox = {bitch("Debug mailbox is not initialized")}
//        }
//
//        return@renderCtor { Shitus.diva(json(), content) }
//    }))))
}

fun TestScenarioBuilder.debugMailboxClear() {
    acta {async{
        await(send(ClearSentEmailsRequest()))
    }}
}

fun TestScenarioBuilder.debugMailboxCheck(aid: String) {
    var prevMorda by notNullOnce<Morda>()
    val morda = Morda()

    act {prevMorda = TestGlobal.currentMorda}
    acta {morda.coitize(MordaCoitizeParams(
        browseroidName = "debug-mailbox",
        url = fconst.url.test.debugMailbox,
        fillTypedStorageLocal = {},
        fillRawStorageLocal = {}
    ))}
    acta {morda.boot()}
    assertScreenHTML("Checking email", aid)
    // acta {delay(1500)}
    acta {prevMorda.switchTo()}
}






















