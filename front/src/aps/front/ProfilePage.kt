/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

class ProfilePage(val ui: World) {
    fun load(): Promise<Unit> {"__async"
        val primaryButtonTitle = t("TOTE", "Отправить на проверку")

        var pageBody: dynamic = undefined
        val user: UserRTO = ui.getUser()!!
        val userState = user.state

        if (userState == UserState.PROFILE_PENDING || userState == UserState.PROFILE_REJECTED) {
            var prelude: dynamic = undefined
            if (userState == UserState.PROFILE_PENDING) {
                prelude = preludeWithOrangeTriangle(json("title" to t("TOTE", "Сначала заполняешь профиль. Админ связывается с тобой и активирует аккаунт. Потом все остальное."), "center" to 720))
            } else if (userState == UserState.PROFILE_REJECTED) {
                // @wip rejection
                prelude = preludeWithBadNews(json(
                    "title" to t("TOTE", "Админ завернул твой профиль"),
                    "quote" to user.profileRejectionReason))
            }

            pageBody = Shitus.diva(json(),
                prelude,
                FormMatumba<UpdateProfileRequest, UpdateProfileRequest.Response>(FormSpec(
                    UpdateProfileRequest().apply {
                        with(profileFields) {
                            phone.value = user.phone.orEmpty()
                            aboutMe.value = user.aboutMe.orEmpty()
                        }
                    },
                    ui,
                    primaryButtonTitle = primaryButtonTitle,
                    onSuccessa = {res -> "__async"
                        ui.setUser(res.newUser)
                        // TODO:vgrechka Simplify code await/return like below    b75d3e99-4883-4153-8777-34e568d942e1
                        __await(ui.replaceNavigate("profile.html")) /ignora
                    })).toReactElement())
        }
        else if (userState == UserState.PROFILE_APPROVAL_PENDING) {
            pageBody = Shitus.diva(json(),
                preludeWithHourglass(json("content" to Shitus.spancTitle(json("title" to t("TOTE", "Админ проверяет профиль, жди извещения почтой"))))),
                renderProfile(ui, user)
                )
        }
        else if (userState == UserState.BANNED) {
            pageBody = Shitus.diva(json(),
                preludeWithVeryBadNews(json("content" to Shitus.spancTitle(json("title" to t("TOTE", "Тебя тупо забанили, ОК? Кина не будет."))))),
                renderProfile(ui, user)
                )
        }
        else {
            Shitus.raise("Weird user state: ${userState}")
        }

        ui.setPage(Page(
            header = oldShitAsReactElementable(Shitus.pageHeader(json("title" to t("Profile", "Профиль")))),
            body = oldShitAsReactElementable(pageBody)
        ))

        return __asyncResult(Unit)
    }
}



