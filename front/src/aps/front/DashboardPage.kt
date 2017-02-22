/*
 * APS
 *
 * (C) Copyright 2015-2017 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*

class DashboardPage(val world: World) {
    suspend fun load(): PageLoadingError? {
        if (world.user.kind != UserKind.ADMIN) imf("DashboardPage for ${world.user.kind}")
        loadForFuckingAdmin()
        return null
    }

    val c = css.dashboard

    private suspend fun loadForFuckingAdmin() {
        val res = send(UAAdminGetStuffToDoRequest())
        exhaustive/when(res) {
            is FormResponse2.Shitty -> {
                setPage(renderErrorBanner(res.error))
            }
            is FormResponse2.Hunky -> {
                setPage(kdiv(className = "row"){o->
                    o- section(t("Stuff to do", "Работенка"), kdiv{o->
                        val r = res.meat

                        class Metric(val contributionToTotal: Long, val render: () -> ToReactElementable)
                        val metrics = listOf(
                            Metric(
                                contributionToTotal = r.ordersToApprove,
                                render = {renderWorkItem(
                                    title = t("TOTE", "Новых заказов рассмотреть"),
                                    amount = r.ordersToApprove,
                                    url = makeURL(pages.uaAdmin.orders, listOf(
                                        URLParamValue(UAAdminOrdersPage().makeBoobs().urlQuery.filter, AdminOrderFilter.TO_APPROVE)
                                    )),
                                    linkKey = links.adminDashboard.ordersToApprove)}),

                            Metric(
                                contributionToTotal = r.writerProfilesToApprove,
                                render = {renderWorkItem(
                                    title = t("TOTE", "Заапрувить профилей писателей"), amount = r.writerProfilesToApprove,
                                    url = makeURL(pages.uaAdmin.users, listOf(
                                        URLParamValue(AdminUsersPage().makeBoobs().urlQuery.filter, AdminUserFilter.PROFILE_APPROVAL_PENDING)
                                    )),
                                    linkKey = links.adminDashboard.writerProfilesToApprove)}))

                        val total = metrics.sumByLong {it.contributionToTotal}
                        when (total) {
                            0L -> o- div(t("TOTE", "Сюшай, савсэм нэт работы..."))
                            else -> {
                                for (metric in metrics) {
                                    if (metric.contributionToTotal > 0) {
                                        o- metric.render()
                                    }
                                }
                            }
                        }
                    })
                    o- section(t("My account", "Мой аккаунт"), kdiv{o->
                        o- chevronLink(t("Change password", "Сменить пароль")) {}
                        o- chevronLink(t("Sign out", "Выйти прочь")) {}
                    })
                })
            }
        }
    }

    private fun renderWorkItem(title: String, amount: Long, url: String, linkKey: LinkKey): ElementBuilder {
        return kdiv(className = c.workItem){o->
            o- kdiv(className = c.workItemDots){o->
                o- ".".repeat(200)
            }
            o- kdiv(className = c.workItemLinkWideContainer){o->
                o- kspan(className = c.workItemLinkNarrowContainer){o->
                    o- urlLink(key = linkKey,
                               url = url,
                               attrs = Attrs(className = c.workItemLink),
                               linkParams = LinkParams(
                                    title = title
                                ))
                }
            }
            o- kdiv(className = c.workItemBadgeContainer){o->
                o- kspan(className = "badge ${c.workItemBadge}"){o->
                    o- amount.toString()
                }
            }
        }
    }

    private fun setPage(body: ToReactElementable) {
        world.setPage(Page(
            header = usualHeader(t("Dashboard", "Панель")),
            body = kdiv{o->
                o- body
            }
        ))
    }

    private fun section(title: String, content: ToReactElementable): ToReactElementable {
        return kdiv(className = "col-sm-6"){o->
            o- kdiv{o->
                o- kdiv(className = c.sectionTitle){o->
                    o- title
                }
                o- content
            }
        }
    }

    private fun chevronLink(title: String, onClicka: suspend (Any) -> Unit): ToReactElementable {
        return kdiv(className = c.sectionItem){o->
            o- ki(className = "${fa.chevronRight} ${c.sectionItemIcon}")
            o- link(title = title, className = c.sectionItemLink, onClicka = onClicka)
        }
    }

    class SectionItem()

    fun renderSection2(title: String, emptyText: String, items: List<SectionItem>): ToReactElementable {
        return kdiv{o->
            o- "renderSection2"
        }
//        return Shitus.diva(json("tame" to "section-${name}", "style" to js("({})")),
//                           Shitus.diva(json("style" to json("backgroundColor" to Color.BLUE_GRAY_50, "fontWeight" to "bold", "padding" to "2px 5px", "marginBottom" to 10)),
//                                       title),
//                           run outta@{
//                               if (!items.length) return@outta emptyItemsText || Shitus.diva(json("style" to js("({})")), t("TOTE", "Савсэм пусто здэсь..."))
//                               return@outta Shitus.ula.apply(null, js("[]").concat(json("className" to "fa-ul", "style" to json("marginLeft" to 20)),
//                                                                                   items.map({item: dynamic ->
//                                                                                                 Shitus.lia(json("style" to json("marginBottom" to 5)),
//                                                                                                            Shitus.ia(json("className" to "fa fa-li fa-chevron-right", "style" to json("color" to Color.BLUE_GRAY_600))),
//                                                                                                            item)})))
//                           }
//        )
    }

}













//                    o- kdiv(className = "col-sm-6"){o->
//                        o- renderSection2(
//                            title = t("Stuff to do", "Работенка"),
//                            emptyText = t("TOTE", "Сюшай, савсэм нэт работы..."),
//                            items = listOf()
//                        )
//                    }









//            body = oldShitAsToReactElementable(Shitus.diva(
//                json(),
//                Shitus.diva(json("className" to "row"),
//                            if (!showWorkSection) null else
//                                Shitus.diva(json("className" to "col-sm-6"),
//                                            section(json(
//                                                "name" to "workPending",
//                                                "title" to t("TOTE", "Работенка"),
//                                                "emptyItemsText" to t("TOTE", "Сюшай, савсэм нэт работы..."),
//
//                                                "items" to js("[]") /*await<dynamic>(Shitus.runa({
//                                async {
//                                    val items = js("[]")
//
//                                    val res = try { await(GetLiveStatusRequest.send(requiredToken(ui)))
//                                    } catch (e: Throwable) {
//                                        // TODO:vgrechka Handle RPC error while updating dashboard    8e69deec-39ba-48d7-8112-57e2bdf91228
//                                        console.error(e)
//                                        return@async js("[]")
//                                    }
//
//                                    fun addMetric(value: String, tame: String, url: String, title: String, noStateContributions: Boolean = false, ignoreInTests: Boolean = false) {
//                                        if (value != "0") {
//                                            items.push(Shitus.diva(json("controlTypeName" to "addMetric", "tame" to tame, "noStateContributions" to noStateContributions, "style" to json("position" to "relative", "overflow" to "hidden"),
//                                                                        "className" to ifOrEmpty(ignoreInTests){"ignoreInTests"}),
//                                                                   Shitus.diva(json("style" to json("position" to "absolute", "zIndex" to -1, "left" to 0, "top" to 0)), Shitus.repeat(".", 210)),
//                                                                   urlLink(url = url, linkParams = LinkParams(title = title), style = Style(backgroundColor = Color.WHITE, paddingRight = 8, color = Color.BLACK_BOOT), blinkOpts = BlinkOpts(dwidth = -8)),
//                                                                   Shitus.diva(json("style" to json("float" to "right", "paddingLeft" to 8, "background" to Color.WHITE)),
//                                                                               Shitus.spana(json("className" to "badge", "style" to json("float" to "right", "backgroundColor" to Color.BLUE_GRAY_400)),
//                                                                                            Shitus.spanc(json("tame" to "badge", "content" to "" + value))))
//                                            ))
//                                        }
//                                    }
//
//                                    when (res) {
//                                        is GetLiveStatusRequest.Response.ForAdmin -> {
//                                            addMetric(value = res.profilesToApprove, tame = "profilesToApprove", url = "admin-users.html?filter=${UserFilter.PROFILE_APPROVAL_PENDING.name}", title = t("TOTE", "Профилей зааппрувить"))
//                                            addMetric(value = res.suka, tame = "suka", noStateContributions = true, url = "suka.html", title = t("TOTE", "Сцуко-метрика"), ignoreInTests = true)
//                                        }
//                                        is GetLiveStatusRequest.Response.ForWriter -> {
//                                            addMetric(value = res.suka, tame = "suka", noStateContributions = true, url = "suka.html", title = t("TOTE", "Сцуко-метрика"), ignoreInTests = true)
//                                        }
//                                        is GetLiveStatusRequest.Response.ForCustomer -> {
//                                            addMetric(value = res.suka, tame = "suka", noStateContributions = true, url = "suka.html", title = t("TOTE", "Сцуко-метрика"), ignoreInTests = true)
//                                        }
//                                    }
//
//                                    return@async items
//                                }
//                            }))*/
//                                            ))
//                                ),
//
//                            Shitus.diva(json("className" to "col-sm-6"),
//                                        section(json(
//                                            "name" to "account",
//                                            "title" to t("TOTE", "Аккаунт"),
//                                            "items" to mutableListOf<ReactElement>().applet{o->
//                                                o+= darkLink(json("tamy" to "signOut", "title" to t("TOTE", "Выйти прочь"), "onClick" to {async{
//                                                    ui.signOut()
//                                                }}))
//
//                                                if (ui.user.state != UserState.BANNED) {
//                                                    o+= darkLink(json("tamy" to "changePassword", "title" to t("TOTE", "Сменить пароль"), "onClick" to {async{
//                                                        console.warn("// TODO:vgrechka Implement changing password    2eb6584b-4ffa-4ae8-95b4-6836b866894a")
//                                                    }}))
//                                                }
//                                            }.toJSArray()
//                                        ))
//                            )
//                )
//            ))

