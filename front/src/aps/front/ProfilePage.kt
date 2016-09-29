/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

class ProfilePage(val ui: dynamic) {
    fun load(): Promise<Unit> {"__async"
        val primaryButtonTitle = t("TOTE", "Отправить на проверку")

        var pageBody: dynamic = undefined
        val user: UserRTO = ui.getUser()
        val userState = user.state

        if (userState == UserState.PROFILE_PENDING || userState == UserState.PROFILE_REJECTED) {
            var prelude: dynamic = undefined
            if (userState == UserState.PROFILE_PENDING) {
                prelude = jshit.preludeWithOrangeTriangle(json("title" to t("TOTE", "Сначала заполняешь профиль. Админ связывается с тобой и активирует аккаунт. Потом все остальное."), "center" to 720))
            } else if (userState == UserState.PROFILE_REJECTED) {
                // @wip rejection
                prelude = jshit.preludeWithBadNews(json(
                    "title" to t("TOTE", "Админ завернул твой профиль"),
                    "quote" to user.profileRejectionReason))
            }

            val form = object:CoolForm<UpdateProfileResponse>("updateProfile", primaryButtonTitle = primaryButtonTitle) {
                val profileFields = ProfileFields(this, user)

                override fun onSuccessa(res: UpdateProfileResponse): Promise<Unit> {"__async"
                    ui.setUser(res.newUser)
                    __await(ui.replaceNavigate("profile.html"))
                    return __asyncResult(Unit)
                }
            }
            form.ui = ui

            pageBody = jshit.diva(json(), prelude, form.toReactElement())
        }
        else if (userState == UserState.PROFILE_APPROVAL_PENDING) {
            pageBody = jshit.diva(json(),
                jshit.preludeWithHourglass(json("content" to jshit.spancTitle(json("title" to t("TOTE", "Админ проверяет профиль, жди извещения почтой"))))),
                KotlinShit.renderProfile(json("user" to user))
                )
        }
        else if (userState == UserState.BANNED) {
            pageBody = jshit.diva(json(),
                jshit.preludeWithVeryBadNews(json("content" to jshit.spancTitle(json("title" to t("TOTE", "Тебя тупо забанили, ОК? Кина не будет."))))),
                KotlinShit.renderProfile(json("user" to user))
                )
        }
        else {
            raise("Weird user state: ${userState}")
        }

        ui.setPage(json(
            "header" to jshit.pageHeader(json("title" to t("Profile", "Профиль"))),
            "body" to pageBody
        ))

        return __asyncResult(Unit)
    }
}



