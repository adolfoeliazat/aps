/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

fun userKindIcon(kind: UserKind): ReactElement {
    return faIcon(tame="icon", marginLeft=5, marginRight=5,
        icon = when (kind) {
            UserKind.CUSTOMER -> "user"
            UserKind.WRITER -> "pencil"
            UserKind.ADMIN -> "cog"
        }).toReactElement()
}

fun renderProfile(ui: World, user: UserRTO): ReactElement {
    val adminLooks = ui.user!!.kind == UserKind.ADMIN

    return Shitus.diva(json("controlTypeName" to "renderProfile", "tame" to "profile"),
        Shitus.diva(json("className" to "row"),
            limpopo("firstName", t("TOTE", "Имя"), colsm = 3)(user.firstName),
            limpopo("lastName", t("TOTE", "Фамилия"), colsm = 3)(user.lastName),
            limpopo("email", t("TOTE", "Почта"), colsm = 3)(user.email),
            if (user.profileUpdatedAt != null) limpopo("phone", t("TOTE", "Телефон"), colsm = 3)(user.phone!!) else null
        ),

        Shitus.diva(json("className" to "row"),
            limpopo("kind", t("TOTE", "Тип"), colsm = 3)(asReactElement(
                Shitus.diva(json("style" to js("({})")),
                    userKindIcon(user.kind),
                    Shitus.spanc(json("tame" to "value", "content" to userKindTitle(user.kind)))))),

            if (adminLooks)
                limpopo("state", t("TOTE", "Статус"), colsm = 3,
                    formGroupStyle =
                        if (user.state == UserState.PROFILE_APPROVAL_PENDING) json("background" to Color.AMBER_200)
                        else if (user.state == UserState.PROFILE_REJECTED) json("background" to Color.DEEP_ORANGE_200)
                        else if (user.state == UserState.BANNED) json("background" to Color.RED_200)
                        else json())(
                    user.state.title) else null,

            limpopo("insertedAt", t("TOTE", "Аккаунт создан"), colsm = 3)(renderStamp(user.insertedAt.value)),

            if (user.profileUpdatedAt != null)
                limpopo("profileUpdatedAt", t("TOTE", "Профиль залит"), colsm = 3)(renderStamp(user.profileUpdatedAt.value))
            else
                limpopo("profileUpdatedAt", t("TOTE", "Профиль"), colsm = 3)(t("TOTE", "Нифига не заполнялся"))
        ),

        if (user.state == UserState.PROFILE_REJECTED) Shitus.diva(json("className" to "row"),
            limpopo("profileRejectionReason", t("TOTE", "Причина отказа"), colsm = 12)(
                user.profileRejectionReason!!, spancStyle = json("whiteSpace" to "pre-wrap"))) else null,

        if (user.state == UserState.BANNED) Shitus.diva(json("className" to "row"),
            limpopo("banReason", t("TOTE", "Причина бана"), colsm = 12)(
                user.banReason!!, spancStyle = json("whiteSpace" to "pre-wrap"))) else null,

        if (user.profileUpdatedAt != null) Shitus.diva(json("className" to "row"),
            limpopo("aboutMe", t("TOTE", "Набрехано о себе"), colsm = 12)(
                user.aboutMe!!, spancStyle = json("whiteSpace" to "pre-wrap"))) else null,

        if (adminLooks && !user.adminNotes.isNullOrBlank()) Shitus.diva(json("className" to "row"),
            limpopo("adminNotes", t("TOTE", "Заметки админа"), colsm = 12)(
                user.adminNotes!!, spancStyle = json("whiteSpace" to "pre-wrap"))) else null
    )
}


















