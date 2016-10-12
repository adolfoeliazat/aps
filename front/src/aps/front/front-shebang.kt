/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

fun customerDynamicPageNames(): dynamic {
    return jsArrayOf("test", "sign-in", "sign-up", "dashboard", "orders", "support")
}

fun writerDynamicPageNames(): dynamic {
    return jsArrayOf(
        "test", "sign-in", "sign-up", "dashboard", "orders", "support", "store", "users", "profile",
        "admin-my-tasks", "admin-heap", "admin-users",
        "debug-perf-render",
        "debug-kotlin-playground"
    )
}

fun jsFacing_isDynamicPage(name: String): Boolean {
    if (global.CLIENT_KIND == UserKind.CUSTOMER.name) return customerDynamicPageNames().indexOf(name)
    return writerDynamicPageNames().indexOf(name)
}

val theClientKind: UserKind get() = UserKind.valueOf(global.CLIENT_KIND)


fun userKindTitle(kind: UserKind) = when (kind) {
    UserKind.CUSTOMER -> t("TOTE", "Заказчик")
    UserKind.WRITER -> t("TOTE", "Писатель")
    UserKind.ADMIN -> t("TOTE", "Админ")
}

fun requiredToken(ui: LegacyUIShit): String = ui.token ?: bitch("I want a token")

fun oldShitAsReactElementable(someShit: Any?): ToReactElementable =
    object:ToReactElementable {
        override fun toReactElement() = asReactElement(someShit)
    }


class limpopo(val tame: String, val label: String, val colsm: Int?, val formGroupStyle: Any? = null) {
    operator fun invoke(content: ReactElement): ReactElement {
        val res = jshit.diva(json("controlTypeName" to "limpopo", "tame" to tame, "className" to "form-group", "style" to formGroupStyle),
            jshit.labe(json("content" to label)),
            jshit.diva(json(), content))
        return if (colsm == null) res else jshit.diva(json("className" to "col-sm-${colsm}"), res)
    }

    operator fun invoke(value: String, spancStyle: Any? = null): ReactElement {
        return this(asReactElement(jshit.spanc(json(
            "tame" to "value",
            "style" to spancStyle,
            "content" to value))))
    }
}

fun renderStamp(stamp: String, includeTZ: Boolean = true): String {
    return jshit.timestampString(stamp, json("includeTZ" to includeTZ))
}

fun pushNavigate(ui: LegacyUIShit, url: String): Promise<Unit> {"__async"
    __dlog.pushNavigate(url)
    ui.currentPage = null

    global.history.pushState(null, "", url)
    return __await(ui.loadPageForURL()) /ignora
}

interface Blinkable {
    fun setBlinking(b: Boolean)
}


















