/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.UserKind
import aps.UserRTO

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

class ProfileFields(form: CoolForm<*>, user: UserRTO?) {
    val phone = TextField(form, "phone", t("TOTE", "Телефон"))
    val aboutMe = TextField(form, "aboutMe", t("TOTE", "Пара ласковых о себе"), kind = TextField.Kind.TEXTAREA)

    init {
        if (user != null) {
            phone.value = user.phone.orEmpty()
            aboutMe.value = user.aboutMe.orEmpty()
        }
    }
}

fun userKindTitle(kind: UserKind) = when (kind) {
    UserKind.CUSTOMER -> t("TOTE", "Заказчик")
    UserKind.WRITER -> t("TOTE", "Писатель")
    UserKind.ADMIN -> t("TOTE", "Админ")
}




















