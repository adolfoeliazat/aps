/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*

// TODO:vgrechka Test case: User unchecks "subscribed to all categories", but doesn't select anything specific

class ProfilePage {
    suspend fun load(): PageLoadingError? {
        Globus.world.setPage(Page(
            header = usualHeader(t("TOTE", "Профиль")),
            body = kdiv{o->
                exhaustive=when (user().kind) {
                    UserKind.WRITER -> when (user().state) {
                        UserState.COOL -> {
                            o- renderBannerCalmWarning(t("TOTE", "При изменении профиля, пока мы его будем проверять, сервис будет недоступен"))
                        }
                        UserState.PROFILE_PENDING -> {
                            o- renderBanner1(t("TOTE", "Сначала заполняешь профиль. Админ связывается с тобой и активирует аккаунт. Потом все остальное."))
                        }
                        UserState.PROFILE_APPROVAL_PENDING -> {
                            o- renderWaitingBanner(css.profile.writerProfileApprovalPendingBanner,
                                                   t("TOTE", "Мы проверяем твой профайл. Жди звонка"))
                        }
                        UserState.PROFILE_REJECTED -> {
                            o- renderMaybeRejectionReasonBanner(user().profileRejectionReason)
                        }
                        UserState.BANNED -> imf("267e687c-723e-48fe-911b-fbb556e23e9d")
                    }
                    UserKind.CUSTOMER -> imf("f0297187-dd11-48c2-aae9-dbe643a6a784")
                    UserKind.ADMIN -> wtf("709861c7-0d1c-4514-82a3-3943f4753c43")
                }

                if (isWriter() && user().state == UserState.PROFILE_APPROVAL_PENDING) {
                    o- renderProfile(user())
                } else {
                    o- FormMatumba<UpdateProfileRequest, UpdateProfileRequest.Response>(FormSpec(
                        req = UpdateProfileRequest().populateCheckingCompleteness{o->
                            o.firstName.value = user().firstName
                            o.lastName.value = user().lastName
                            o.profilePhone.value = user().profilePhone
                            o.categorySubscriptions.setValue(
                                when {
                                    user().allDocumentCategories -> DocumentCategorySetFieldValue.All()
                                    else -> DocumentCategorySetFieldValue.Specific(user().documentCategories)
                                })
                            o.aboutMe.value = user().aboutMe
                        },
                        primaryButtonTitle = t("TOTE", "Отправить на проверку"),
                        onSuccessa = {res->
                            showingModalIfError({reginaGetMyself()}) {myself->
                                Globus.world.userMaybe = myself
                                Globus.world.replaceNavigate(makeURL(
                                    when (user().kind) {
                                        UserKind.CUSTOMER -> pages.uaCustomer.profile
                                        UserKind.WRITER -> pages.uaWriter.profile
                                        UserKind.ADMIN -> wtf("ede39b9a-a775-4f64-9929-73b1f08b3304")
                                    }, listOf()))
                            }
                        }
                    ))
                }
            }
        ))
        return pageLoadedFineResult
    }
}

fun renderProfile(user: UserRTO, opts: UserRTORenderingOptions = UserRTORenderingOptions()): ToReactElementable {
    val m = MelindaTools
    return kdiv{o->
        o- m.row {o->
            o- m.createdAtCol(3, user.createdAt)
            o- m.col(3, t("Profile updated", "Профайл изменен")){o->
                o- (user.profileUpdatedAt?.let {formatUnixTime(it)} ?: t("TOTE", "Ни разу"))
            }

            o- m.col(3, t("TOTE", "Статус"), user.state.title, textClassName = css.user.stateLabel(user.state), icon = user.state.icon, contentClassName = opts.outlineState.then{css.redOutline})
        }
        o- m.row{o->
            o- m.col(3, fields.signUpFirstName.title, user.firstName, contentClassName = opts.outlineFirstName.then{css.redOutline})
            o- m.col(3, fields.signUpLastName.title, user.lastName, contentClassName = opts.outlineLastName.then{css.redOutline})
            o- m.col(3, fields.profilePhone.title, user.profilePhone, contentClassName = opts.outlinePhone.then{css.redOutline})
        }

        if (user.kind == UserKind.WRITER) {
            o- m.row{o->
                o- m.col(12, fields.writerDocumentCategories.title, contentClassName = opts.outlineWriterDocumentCategories.then{css.redOutline}){o->
                    if (user.allDocumentCategories) {
                        o- fconst.text.inAnyCategory
                    } else {
                        o- hor3(style = Style(flexWrap = "wrap"), gapSide = HorGapSide.RIGHT){o->
                            for (cat in user.documentCategories) {
                                o- kdiv{o->
                                    o- ki(className = fa.caretRight.className, baseStyle = Style(marginRight = "0.5rem"))
                                    o- cat.pathTitle
                                }
                            }
                        }
                    }
                }
            }
        }

        if (user.aboutMe.isNotBlank())
            o- m.detailsRow(user.aboutMe, user.aboutMeHighlightRanges, title = fields.aboutMe.title, contentClassName = opts.outlineAboutMe.then{css.redOutline})
        o- renderAdminNotesIfNeeded(user, opts.adminNotesOptions)
    }
}

class UserRTORenderingOptions(
    val outlinePhone: Boolean = false,
    val outlineAboutMe: Boolean = false,
    val outlineState: Boolean = false,
    val outlineFirstName: Boolean = false,
    val outlineLastName: Boolean = false,
    val outlineWriterDocumentCategories: Boolean = false,
    val adminNotesOptions: RTOWithAdminNotesRenderingOptions = RTOWithAdminNotesRenderingOptions()
)

class RTOWithAdminNotesRenderingOptions(
    val outlineAdminNotes: Boolean = false
) {
    companion object {
        fun fromComparedPair(thisShit: RTOWithAdminNotes, thatShit: RTOWithAdminNotes) = RTOWithAdminNotesRenderingOptions(
            outlineAdminNotes = thisShit.adminNotes != thatShit.adminNotes
        )
    }
}

fun renderUserParamsHistoryItem(item: UserParamsHistoryItemRTO, opts: UserRTORenderingOptions = UserRTORenderingOptions()): ElementBuilder {
    val m = MelindaTools
    return kdiv{o->
        o- kdiv(className = css.history.shit){o->
            o- m.row(marginBottom = null){o->
                o- m.col(6, t("TOTE", "Кто сделал"), contentStyle = Style(display = "flex", alignItems = "center")){o->
                    o- renderUserKindIconWithGap(item.requester.kind)
                    o- span(stringBuild {o ->
                        val currentName = fullName(item.requester)
                        o += currentName
                        val thenName = fullName(item.thenRequester.firstName, item.thenRequester.lastName)
                        if (thenName != currentName)
                            o += " (${t("then", "тогда")} $thenName)"
                    })
                }
            }
        }
        o- renderProfile(item.entity, opts)
    }
}







