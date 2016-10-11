/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

class DashboardPage(val ui: LegacyUIShit) {
    fun load(preserveScroll: Boolean = false): Promise<Unit> {"__async"
        val myPage = json(
            "id" to jshit.utils.puid(),
            "header" to jshit.pageHeader(json("title" to t("Dashboard", "Панель"))),
            "body" to jshit.diva(json(),
                jshit.diva(json("className" to "row"),
                    jshit.diva(json("className" to "col-sm-6"),
                        section(json(
                            "name" to "workPending",
                            "title" to t("TOTE", "Работенка"),
                            "emptyItemsText" to t("TOTE", "Сюшай, савсэм нэт работы..."),

                            "items" to __await<dynamic>(jshit.utils.runa({
                                "__async"
                                val items = js("[]")

                                val res = try { __await(GetLiveStatusRequest.send(requiredToken(ui)))
                                } catch (e: Throwable) {
                                    // TODO:vgrechka Handle RPC error while updating dashboard    8e69deec-39ba-48d7-8112-57e2bdf91228
                                    console.error(e)
                                    return@runa __asyncResult(js("[]"))
                                }

                                fun addMetric(value: String, tame: String, url: String, title: String, noStateContributions: Boolean = false) {
                                    if (value != "0") {
                                        items.push(jshit.diva(json("controlTypeName" to "addMetric", "tame" to tame, "noStateContributions" to noStateContributions, "style" to json("position" to "relative", "overflow" to "hidden")),
                                            jshit.diva(json("style" to json("position" to "absolute", "zIndex" to -1, "left" to 0, "top" to 0)), jshit.utils.repeat(".", 210)),
                                            ui.urlLink(json("tamy" to true, "style" to json("background" to Color.WHITE, "paddingRight" to 8, "color" to Color.BLACK_BOOT), "blinkOpts" to json("dwidth" to -8),
                                                "title" to title, "url" to url, "delayActionForFanciness" to true)),
                                            jshit.diva(json("style" to json("float" to "right", "paddingLeft" to 8, "background" to Color.WHITE)),
                                                jshit.spana(json("className" to "badge", "style" to json("float" to "right", "backgroundColor" to Color.BLUE_GRAY_400)),
                                                    jshit.spanc(json("tame" to "badge", "content" to "" + value))))
                                        ))
                                    }
                                }

                                when (res) {
                                    is GetLiveStatusRequest.Response.ForAdmin -> {
                                        addMetric(value = res.profilesToApprove, tame = "profilesToApprove", url = "admin-users.html?filter=${UserFilter.PROFILE_APPROVAL_PENDING.name}", title = t("TOTE", "Профилей зааппрувить"))
                                        addMetric(value = res.suka, tame = "suka", noStateContributions = true, url = "suka.html", title = t("TOTE", "Сцуко-метрика"))
                                    }
                                    is GetLiveStatusRequest.Response.ForWriter -> {
                                        addMetric(value = res.suka, tame = "suka", noStateContributions = true, url = "suka.html", title = t("TOTE", "Сцуко-метрика"))
                                    }
                                    is GetLiveStatusRequest.Response.ForCustomer -> {
                                        addMetric(value = res.suka, tame = "suka", noStateContributions = true, url = "suka.html", title = t("TOTE", "Сцуко-метрика"))
                                    }
                                }

                                return@runa __asyncResult(items)
                            }))
                        ))
                    ),

                    jshit.diva(json("className" to "col-sm-6"),
                        section(json(
                            "name" to "account",
                            "title" to t("TOTE", "Аккаунт"),
                            "items" to jsArrayOf(
                                jshit.darkLink(json("tamy" to "signOut", "title" to t("TOTE", "Выйти прочь"), "onClick" to {
                                    "__async"
                                    ui.signOut()
                                })),
                                jshit.darkLink(json("tamy" to "changePassword", "title" to t("TOTE", "Сменить пароль"), "onClick" to {
                                    "__async"
                                    console.warn("// TODO:vgrechka Implement changing password    2eb6584b-4ffa-4ae8-95b4-6836b866894a")
                                }))
                            )
                        ))
                    )
                )
            )
        )

        val scrollTop = jshit.utils.jQuery(kotlin.browser.document).scrollTop()
        KotlinShit.ui.setPage(myPage)
        if (preserveScroll) {
            jshit.utils.jQuery(kotlin.browser.document).scrollTop(scrollTop)
        }

        fun scheduleUpdate() {
            jshit.utils.timeoutSet(5000, outta@{"__async" // @ctx forgetmenot-1-1
                if (KotlinShit.clientImpl.stale) return@outta Unit
                if (myPage != KotlinShit.ui.currentPage) return@outta Unit

                // Automatic refreshes should be prevented while something is being investigated via revealer,
                // otherwise elements being looked at might be removed
                if (jshit.controlBeingRevealed) { scheduleUpdate(); return@outta Unit }

                if (jshit.isOrWasInTestScenario() && jshit.getCurrentTestBrowser().ui != KotlinShit.ui) { scheduleUpdate(); return@outta Unit }

                // dlog("currentPage.id = ${currentPage.id}; myPage.id = ${myPage.id}")
                jshit.utils.dlog("Updating dashboard page")

                __asyncResult(__await(DashboardPage(ui).load(preserveScroll = true)))
            })
        }
        scheduleUpdate()

        return __asyncResult(Unit)
    }

    fun section(def: dynamic): dynamic {
        // #extract {name, title, items, emptyItemsText} from def
        val name = def.name; val title = def.title; val items = def.items; val emptyItemsText = def.emptyItemsText

        return jshit.diva(json("tame" to "section-${name}", "style" to js("({})")),
            jshit.diva(json("style" to json("backgroundColor" to Color.BLUE_GRAY_50, "fontWeight" to "bold", "padding" to "2px 5px", "marginBottom" to 10)),
                title),
            run outta@{
                if (!items.length) return@outta emptyItemsText || jshit.diva(json("style" to js("({})")), t("TOTE", "Савсэм пусто здэсь..."))
                return@outta jshit.ula.apply(null, js("[]").concat(json("className" to "fa-ul", "style" to json("marginLeft" to 20)),
                    items.map({item: dynamic ->
                        jshit.lia(json("style" to json("marginBottom" to 5)),
                            jshit.ia(json("className" to "fa fa-li fa-chevron-right", "style" to json("color" to Color.BLUE_GRAY_600))),
                            item)})))
            }
        )
    }
}


