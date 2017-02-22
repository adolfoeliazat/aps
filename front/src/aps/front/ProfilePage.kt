/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*

class ProfilePage {
    suspend fun load(): PageLoadingError? {
        Globus.world.setPage(Page(
            header = usualHeader(t("TOTE", "Профиль")),
            body = kdiv{o->
                exhaustive/when (user().kind) {
                    UserKind.WRITER -> when (user().state) {
                        UserState.COOL -> {}
                        UserState.PROFILE_PENDING -> {
                            val c = css.profile.writerProfilePendingBanner
                            o- kdiv(className = c.container){o->
                                o- kdiv(className = c.message){o->
                                    o- ki(className = c.icon + " " + fa.chevronRight)
                                    o- t("TOTE", "Сначала заполняешь профиль. Админ связывается с тобой и активирует аккаунт. Потом все остальное.")
                                }
                            }
                        }
                        UserState.PROFILE_APPROVAL_PENDING -> {
                            o- renderWaitingBanner(css.profile.writerProfileApprovalPendingBanner,
                                                   t("TOTE", "Мы проверяем твой профайл. Жди звонка"))
                        }
                        UserState.PROFILE_REJECTED -> imf("bf4e024b-3745-443c-a086-2f7479a35bf0")
                        UserState.BANNED -> imf("267e687c-723e-48fe-911b-fbb556e23e9d")
                    }
                    UserKind.CUSTOMER -> imf("f0297187-dd11-48c2-aae9-dbe643a6a784")
                    UserKind.ADMIN -> wtf("709861c7-0d1c-4514-82a3-3943f4753c43")
                }

                if (isWriter() && user().state == UserState.PROFILE_APPROVAL_PENDING) {
                    renderProfile(o, user())
                } else {
                    o- FormMatumba<UpdateProfileRequest, UpdateProfileRequest.Response>(FormSpec(
                        req = UpdateProfileRequest().populateCheckingCompleteness{o->
                            o.firstName.value = user().firstName
                            o.lastName.value = user().lastName
                            o.profilePhone.value = user().profilePhone
                            o.aboutMe.value = user().aboutMe
                        },
                        primaryButtonTitle = t("TOTE", "Отправить на проверку"),
                        onSuccessa = {res->
                            Globus.world.userMaybe = res.newUser
                            Globus.world.replaceNavigate(makeURL(
                                when (user().kind) {
                                    UserKind.CUSTOMER -> pages.uaCustomer.profile
                                    UserKind.WRITER -> pages.uaWriter.profile
                                    UserKind.ADMIN -> wtf("ede39b9a-a775-4f64-9929-73b1f08b3304")
                                }, listOf()))
                        }
                    ))
                }
            }
        ))
        return pageLoadedFineResult
    }
}

fun renderProfile(o: ElementBuilder, user: UserRTO) {
    val m = MelindaTools
    o- m.row {o->
        o- m.createdAtCol(3, user.createdAt)
        user.profileUpdatedAt?.let {
            o- m.col(3, t("Profile updated", "Профайл изменен")){o->
                o- formatUnixTime(it)
            }
        }
        o- m.col(3, t("TOTE", "Статус"), user.state.title, className = css.user.stateLabel(user.state))
    }
    o- m.row{o->
        o- m.col(3, fields.signUpFirstName.title, user.firstName)
        o- m.col(3, fields.signUpLastName.title, user.lastName)
        o- m.col(3, fields.profilePhone.title, user.profilePhone)
    }

    o- m.detailsRow(user.aboutMe, user.aboutMeHighlightRanges, title = fields.aboutMe.title)
    renderAdminNotesIfNeeded(o, user)
}



