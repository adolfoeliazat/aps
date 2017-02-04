package aps.front

import aps.*
import into.kommon.*

data class PageSpec (
    val path: String,
    val navTitle: String?,
    val skipFirstTimeRendering: Boolean,
    val requiresSignIn: Boolean,
    val load: suspend (world: World) -> Unit
) : Fucker()

private fun staticPage(ident: String, navTitle: String? = null) = PageSpec(
    path = ident,
    navTitle = navTitle,
    load = {world->
        val url = loc.baseWithoutSlash + "/$ident.html"
        val content = fetchFromURL("GET", url, null) {it}
        val from = content.indexOfOrDie("<!-- BEGIN CONTENT -->")
        val to = content.indexOfOrDie("<!-- END CONTENT -->")
        world.setRootContent(rawHTML(content.substring(from, to)))
    },
    skipFirstTimeRendering = true,
    requiresSignIn = false
)

private fun privatePage(ident: String, navTitle: String? = null, load: suspend (world: World) -> Unit) = PageSpec(
    path = ident,
    navTitle = navTitle,
    load = load,
    skipFirstTimeRendering = false,
    requiresSignIn = true
)

object pageSpecs {
    object uaCustomer : Fuckers<PageSpec>() {
        val index by namedFucker {staticPage(it)}
        val why by namedFucker {staticPage(it, t("Why Us?", "Почему мы?"))}
        val prices by namedFucker {staticPage(it, t("Prices", "Цены"))}
        val samples by namedFucker {staticPage(it, t("Samples", "Примеры"))}
        val faq by namedFucker {staticPage(it, t("FAQ", "ЧаВо"))}
        val contact by namedFucker {staticPage(it, t("Contact Us", "Связь"))}
        val blog by namedFucker {staticPage(it, t("Blog", "Блог"))}
        val makeOrder by namedFucker {PageSpec(it, t("Make Order", "Заказать"), skipFirstTimeRendering = false, requiresSignIn = false) {MakeOrderPage(it).load()}}; val makeOrder_testRef = TestRef(makeOrder)
        val confirmOrder by namedFucker {PageSpec(it, navTitle = null, skipFirstTimeRendering = false, requiresSignIn = false) {ConfirmOrderPage(it).load()}}
        val orders by namedFucker {privatePage(it, t("My Orders", "Мои заказы")) {UACustomerOrdersPage(it).load()}}; val orders_testRef = TestRef(orders)
        val order by namedFucker {privatePage(it) {UACustomerSingleOrderPage(it).load()}}; val order_testRef = TestRef(orders)
        val support by namedFucker {privatePage(it, t("Support", "Поддержка")) {imf()}}
        val dashboard by namedFucker {privatePage(it) {DashboardPage(it).load()}}
        val profile by namedFucker {PageSpec(it, navTitle = null, skipFirstTimeRendering = false, requiresSignIn = true) {imf()}}
        val signIn by namedFucker {privatePage(it, t("Sign In", "Вход")) {SignInPage(it).load()}}; val signIn_testRef = TestRef(signIn)
    }

    object uaWriter : Fuckers<PageSpec>() {
        val index by namedFucker {staticPage(it, "boobs")}
        val why by namedFucker {staticPage(it, t("Why Us?", "Почему мы?"))}
        val prices by namedFucker {staticPage(it, t("Prices", "Цены"))}
        val samples by namedFucker {staticPage(it, t("Samples", "Примеры"))}
        val faq by namedFucker {staticPage(it, t("FAQ", "ЧаВо"))}
        val orders by namedFucker {privatePage(it, t("My Orders", "Мои заказы")) {imf()}}
        val store by namedFucker {privatePage(it, t("Store", "Стор")) {imf()}}
        val profile by namedFucker {PageSpec(it, navTitle = null, skipFirstTimeRendering = false, requiresSignIn = true) {UAWriterProfilePage(it).load()}}
        val dashboard by namedFucker {privatePage(it) {DashboardPage(it).load()}}
        val signIn by namedFucker {privatePage(it, t("Sign In", "Вход")) {SignInPage(it).load()}}
        val debug by namedFucker {PageSpec(it, navTitle = null, skipFirstTimeRendering = false, requiresSignIn = false) {DebugPage(it).load()}} // TODO:vgrechka Remove debug pages from production builds
        val support by namedFucker {privatePage(it, t("Support", "Поддержка")) {imf()}}
    }

//    object uaAdmin : Fuckers<PageSpec>() {
//        val users by namedFucker {PageSpec(it, t("Users", "Засранцы")) {AdminUsersPage(it).load()}}
//    }
}


