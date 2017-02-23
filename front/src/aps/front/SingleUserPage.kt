package aps.front

import aps.*
import into.kommon.*

class SingleUserPage {
    var tabitha by notNullOnce<Tabitha<UserRTO>>()

    val user get()= tabitha.entity

    suspend fun load(): PageLoadingError? {
        check(isAdmin()){"b8a7c482-214f-4875-9614-f7e062525d70"}
        tabitha = Tabitha<UserRTO>(
            page = pages.uaAdmin.user,
            tabKeys = tabs.user,
            defaultTab = tabs.user.params,
            loadEntity = {id->
                askRegina(ReginaLoadUser(id))
            },
            renderBelowHeader = {
                NOTRE
            },
            makeTabs = {listOf(
                object:TabithaTab {
                    suspend override fun load(): FormResponse2.Shitty<*>? {
                        return null
                    }

                    override val tabSpec = SimpleTabSpec(
                        key = tabs.user.params,
                        title = t("TOTE", "Параметры"),
                        content = kdiv{o->
                            renderProfile(o, user)
                        }
                    )
                }
            )},
            pageHeaderTitle = {t("TOTE", "Засранец ${user.firstName} ${user.lastName}")},
            subtitle = {null},
            renderBelowSubtitle = fun(): ToReactElementable {
                return NOTRE
            }
        )
        return tabitha.load()
    }
}

