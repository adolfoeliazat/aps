package aps.front

import aps.*
import into.kommon.*

object pages {
    object uaCustomer : Fuckers<PageSpec>(null) {
        val index by namedFucker {staticPage(it)}
        val why by namedFucker {staticPage(it, t("Why Us?", "Почему мы?"))}
        val prices by namedFucker {staticPage(it, t("Prices", "Цены"))}
        val samples by namedFucker {staticPage(it, t("Samples", "Примеры"))}
        val faq by namedFucker {staticPage(it, t("FAQ", "ЧаВо"))}
        val contact by namedFucker {staticPage(it, t("Contact Us", "Связь"))}
        val blog by namedFucker {staticPage(it, t("Blog", "Блог"))}
        val signIn by namedFucker(::signInPage); val signIn_testRef = TestRef(signIn)
        val signUp by namedFucker(::signUpPage); val signUp_testRef = TestRef(signUp)
        val makeOrder by namedFucker {PageSpec(it, t("Make Order", "Заказать"), skipFirstTimeRendering = false, requiresSignIn = false) {MakeOrderPage(it).load()}}; val makeOrder_testRef = TestRef(makeOrder)
        val confirmOrder by namedFucker {PageSpec(it, navTitle = null, skipFirstTimeRendering = false, requiresSignIn = false) {ConfirmOrderPage(it).load()}}; val confirmOrder_testRef = TestRef(confirmOrder)
        val orders by namedFucker {privatePage(it, t("My Orders", "Мои заказы")) {UACustomerOrdersPage(it).load()}}; val orders_testRef = TestRef(orders)
        val order by namedFucker {privatePage(it) {UASingleOrderPage(it).load()}}; val order_testRef = TestRef(order)
        val support by namedFucker {privatePage(it, t("Support", "Поддержка")) {imf()}}
        val dashboard by namedFucker {privatePage(it) {DashboardPage(it).load()}}
        val profile by namedFucker {PageSpec(it, navTitle = null, skipFirstTimeRendering = false, requiresSignIn = true) {imf()}}
    }

    object uaWriter : Fuckers<PageSpec>(null) {
        val index by namedFucker {staticPage(it, "boobs")}; val index_testRef = TestRef(index)
        val why by namedFucker {staticPage(it, t("Why Us?", "Почему мы?"))}
        val prices by namedFucker {staticPage(it, t("Prices", "Цены"))}
        val samples by namedFucker {staticPage(it, t("Samples", "Примеры"))}
        val faq by namedFucker {staticPage(it, t("FAQ", "ЧаВо"))}
        val orders by namedFucker {privatePage(it, t("My Orders", "Мои заказы")) {imf()}}
        val store by namedFucker {privatePage(it, t("Store", "Стор")) {imf()}}
        val profile by namedFucker {PageSpec(it, navTitle = null, skipFirstTimeRendering = false, requiresSignIn = true) {ProfilePage().load()}}
        val dashboard by namedFucker {privatePage(it) {DashboardPage(it).load()}}
        val signIn by namedFucker(::signInPage); val signIn_testRef = TestRef(signIn)
        val signUp by namedFucker(::signUpPage); val signUp_testRef = TestRef(signUp)
        val debug by namedFucker {PageSpec(it, navTitle = null, skipFirstTimeRendering = false, requiresSignIn = false) {DebugPage(it).load()}} // TODO:vgrechka Remove debug pages from production builds
        val support by namedFucker {privatePage(it, t("Support", "Поддержка")) {imf()}}
    }

    object uaAdmin : Fuckers<PageSpec>(null) {
        val orders by namedFucker {privatePage(it, t("Orders", "Заказы")) {UAAdminOrdersPage().load()}}; val orders_testRef = TestRef(orders)
        val order by namedFucker {privatePage(it) {UASingleOrderPage(it).load()}}; val order_testRef = TestRef(orders)
        val dashboard by namedFucker {privatePage(it) {DashboardPage(it).load()}}; val dashboard_testRef = TestRef(dashboard)
        val users by namedFucker {privatePage(it, t("Users", "Засранцы")) {AdminUsersPage().load()}}; val users_testRef = TestRef(users)
        val user by namedFucker {privatePage(it) {SingleUserPage().load()}}; val user_testRef = TestRef(user)
    }
}

private fun signInPage(fqn: String) = publicPage(fqn, t("Sign In", "Вход")) {SignInPage(it).load()}
private fun signUpPage(fqn: String) = publicPage(fqn, t("Sign Up", "Регистрация")) {SignUpPage().load()}

class PageLoadingError(val msg: String)

val pageLoadedFineResult: PageLoadingError? = null

typealias PageLoader = suspend (world: World) -> PageLoadingError?

class PageSpec (
    val fqn: String,
    val navTitle: String?,
    val skipFirstTimeRendering: Boolean,
    val requiresSignIn: Boolean,
    val load: PageLoader
) : Fucker() {
    val path get()= simpleName(fqn)
}

private fun staticPage(fqn: String, navTitle: String? = null) = PageSpec(
    fqn = fqn,
    navTitle = navTitle,
    load = {world->
        val url = loc.baseWithoutSlash + "/${simpleName(fqn)}.html"
        val content = fetchFromURL("GET", url, null) {it}
        val from = content.indexOfOrDie("<!-- BEGIN CONTENT -->")
        val to = content.indexOfOrDie("<!-- END CONTENT -->")
        world.setRootContent(rawHTML(content.substring(from, to)))
        null
    },
    skipFirstTimeRendering = true,
    requiresSignIn = false
)

private fun privatePage(fqn: String, navTitle: String? = null, load: PageLoader) = PageSpec(
    fqn = fqn,
    navTitle = navTitle,
    load = load,
    skipFirstTimeRendering = false,
    requiresSignIn = true
)

private fun publicPage(fqn: String, navTitle: String? = null, load: PageLoader) = PageSpec(
    fqn = fqn,
    navTitle = navTitle,
    load = load,
    skipFirstTimeRendering = false,
    requiresSignIn = false
)



