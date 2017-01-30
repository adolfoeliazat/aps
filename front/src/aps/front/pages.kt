package aps.front

import aps.*
import aps.front.PageKind.*
import into.kommon.*

enum class PageKind {
    STATIC, PUBLIC, PRIVATE
}

class PageSpec (
    val path: String,
    val navTitle: String,
    val kind: PageKind,
    val load: suspend (world: World) -> Unit
)

private fun staticPage(ident: String, navTitle: String) = PageSpec(
    path = ident,
    navTitle = navTitle,
    kind = STATIC,
    load = {world->
        val url = loc.baseWithoutSlash + "/$ident.html"
        val content = fetchFromURL("GET", url, null) {it}
        val from = content.indexOfOrDie("<!-- BEGIN CONTENT -->")
        val to = content.indexOfOrDie("<!-- END CONTENT -->")
        world.setRootContent(rawHTML(content.substring(from, to)))
    }
)

object pages {
    object uaCustomer : Fuckers<PageSpec>() {
        val index by namedFucker {staticPage(it, "boobs")}
        val why by namedFucker {staticPage(it, t("Why Us?", "Почему мы?"))}
        val prices by namedFucker {staticPage(it, t("Prices", "Цены"))}
        val samples by namedFucker {staticPage(it, t("Samples", "Примеры"))}
        val faq by namedFucker {staticPage(it, t("FAQ", "ЧаВо"))}
        val contact by namedFucker {staticPage(it, t("Contact Us", "Связь"))}
        val blog by namedFucker {staticPage(it, t("Blog", "Блог"))}
        val makeOrder by namedFucker {PageSpec(it, t("Make Order", "Заказать"), PUBLIC) {imf()}}
        val orders by namedFucker {PageSpec(it, t("My Orders", "Мои заказы"), PRIVATE) {UACustomerOrdersPage(it).load()}}; val orders_testRef = TestRef(orders)
        val support by namedFucker {PageSpec(it, t("Support", "Поддержка"), PRIVATE) {imf()}}
        val dashboard by namedFucker {PageSpec(it, "boobs", PRIVATE) {DashboardPage(it).load()}}
        val profile by namedFucker {PageSpec(it, t("Profile", "Профиль"), PRIVATE) {imf()}}
        val signIn by namedFucker {PageSpec(it, t("Sign In", "Вход"), PUBLIC) {SignInPage(it).load()}}; val signIn_testRef = TestRef(signIn)
    }

    object uaWriter : Fuckers<PageSpec>() {
        val index by namedFucker {staticPage(it, "boobs")}
        val why by namedFucker {staticPage(it, t("Why Us?", "Почему мы?"))}
        val prices by namedFucker {staticPage(it, t("Prices", "Цены"))}
        val samples by namedFucker {staticPage(it, t("Samples", "Примеры"))}
        val faq by namedFucker {staticPage(it, t("FAQ", "ЧаВо"))}
        val orders by namedFucker {PageSpec(it, t("My Orders", "Мои заказы"), PRIVATE) {imf()}}
        val store by namedFucker {PageSpec(it, t("Store", "Стор"), PRIVATE) {imf()}}
        val profile by namedFucker {PageSpec(it, t("Profile", "Профиль"), PRIVATE) {UAWriterProfilePage(it).load()}}
        val dashboard by namedFucker {PageSpec(it, "boobs", PRIVATE) {DashboardPage(it).load()}}
        val signIn by namedFucker {PageSpec(it, t("Sign In", "Вход"), PUBLIC) {SignInPage(it).load()}}

//                    co.test, co.signIn, co.signUp, co.dashboard, co.orders, support,
//                    store, users, co.profile, adminMyTasks, adminHeap, adminUsers, debugPerfRender,
//                    debugKotlinPlayground, debug)
    }

    object uaAdmin : Fuckers<PageSpec>() {
        val users by namedFucker {PageSpec(it, t("Users", "Засранцы"), PRIVATE) {AdminUsersPage(it).load()}}
    }
}


