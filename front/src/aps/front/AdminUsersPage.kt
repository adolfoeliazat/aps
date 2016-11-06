/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import kotlin.browser.document
import aps.front.Color.*
import aps.*
import into.kommon.*


class AdminUsersPage(val ui: World) {
    fun load(): Promise<Unit> {"__async"
        __await(Melinda<UserRTO, Nothing, UserFilter>(
            ui,
            urlPath = "admin-users.html",
            procedureName = "getUsers",
            header = {pageHeader0(t("TOTE", "Пользователи"))},
            filterSelectValues = UserFilter.values(),
            defaultFilter = UserFilter.ALL,
            plusFormSpec = FormSpec<AdminCreateUserRequest, GenericResponse>(
                AdminCreateUserRequest(), ui,
                primaryButtonTitle = t("TOTE", "Создать засранца"),
                cancelButtonTitle = defaultCancelButtonTitle),

            renderItem = {index, _user -> object:Placeholder() {
                var user = _user
                val headingID = "hehe-" + puid()

                init {
                    enterDisplayMode()
                }

                fun enterDisplayMode() {
                    setShit(
                        headingActionItems = listOf(
                            faIcon(icon="pencil", tamy="edit", className="hover-color-BLUE_GRAY_800", fontSize="135%", cursor="pointer",
                                    onClick={enterEditMode()})),
                        body = renderProfile(ui, user).toToReactElementable())
                }


                fun enterEditMode() {
                    setShit(
                        headingActionItems = listOf(),
                        body = kdiv(marginBottom=15){o->
                            o- FormMatumba<UpdateUserRequest, GenericResponse>(FormSpec(
                                UpdateUserRequest()-{o->
                                    o.id.value = user.id
                                    o.state.value = user.state
                                    o.profileRejectionReason.value = user.profileRejectionReason.orEmpty()
                                    o.adminNotes.value = user.adminNotes.orEmpty()

                                    o.signUpFields-{o->
                                        o.email.value = user.email
                                        o.firstName.value = user.firstName
                                        o.lastName.value = user.lastName
                                    }

                                    o.profileFields-{o->
                                        o.phone.value = user.phone.orEmpty()
                                        o.aboutMe.value = user.aboutMe.orEmpty()
                                    }
                                },

                                ui,
                                dontShameButtons = true,
                                errorBannerStyle = json("marginTop" to 15),
                                primaryButtonTitle = t("TOTE", "Сохранить"),
                                cancelButtonTitle = t("TOTE", "Передумал"),

                                getInvisibleFieldNames = {
                                    mutableListOf("profileRejectionReason", "banReason").apply {
                                        when (req.state.value) {
                                            UserState.PROFILE_REJECTED -> remove("profileRejectionReason")
                                            UserState.BANNED -> remove("banReason")
                                        }
                                    }
                                },

                                onSuccessa = {res -> "__async"
                                    __await<dynamic>(refreshRecord())
                                    scrollToHeading()
                                    __asyncResult(Unit)
                                },

                                onCancel = {
                                    setPrevContent()
                                    scrollToHeading()
                                },

                                onError = {
                                    scrollToHeading()
                                }
                            ))
                        }
                    )

                    scrollToHeading()
                }

                fun setShit(headingActionItems: List<ToReactElementable>, body: ToReactElementable) {
                    setContent(kdiv(tame=sufindex("item", index)){o->
                        o- kdiv(tame="heading", id=headingID, marginBottom=10, backgroundColor=BLUE_GRAY_50, borderBottom="1px solid ${BLUE_GRAY_100})"){o->
                            o- kspan(fontWeight="normal"){o->
                                o- spanc("title", user.firstName + " " + user.lastName) {style {fontSize = "135%"}}
                                o- spanc("no", "" + Shitus.nostring(json("no" to user.id))) {style {color = GRAY_500; marginLeft(12)}}}

                            o- hor2(float="right", marginTop=4, marginRight=4, color=Color.BLUE_GRAY_600){o->
                                o+ headingActionItems
                            }
                        }

                        o- body
                    })

//                    renderedThing.setContent(diva {tame = sufindex("item", index)
//                        - diva {tame = "heading"; elementID = headingID; style {marginBottom(10); backgroundColor = BLUE_GRAY_50; borderBottom = "1px solid ${BLUE_GRAY_100}"}
//                            - spana {style {fontWeight = "normal"}
//                                - spanc("title", user.firstName + " " + user.lastName) {style {fontSize = "135%"}}
//                                - spanc("no", "" + Shitus.nostring(json("no" to user.id))) {style {color = GRAY_500; marginLeft(12)}}}
//
//                            - asReactElement(Shitus.hor2(json("style" to json("float" to "right", "marginTop" to 4, "marginRight" to 4, "color" to Color.BLUE_GRAY_600),
//                                "items" to headingActionItems)))
//                        }
//
//                        - asReactElement(body)
//                    }.toReactElement())
                }

                fun scrollToHeading() {
                    val top = Shitus.byid(headingID).offset().top - 50 - 15
                    global.requestAnimationFrame { js("$")(document).scrollTop(top) }
                }


                fun refreshRecord(): Promise<Unit> {"__async"
                    val res = __await(GetUserRequest.send(ui.token!!, user.id))
                    return ignora/when (res) {
                        is ZimbabweResponse.Shitty -> setShit(
                            headingActionItems = listOf(),
                            body = Shitus.errorBanner(json("content" to res.error)).toToReactElementable())

                        is ZimbabweResponse.Hunky -> {
                            user = res.meat.user
                            enterDisplayMode()
                        }
                    }
                }
            }
            }
        ).ignita())
        return __asyncResult(Unit)
    }
}

















//    val title: dynamic = arg["title"]
//    val labels: dynamic = arg["labels"]?.let{it} ?: jsArrayOf()
//    val className: dynamic = arg["classname"]?.let{it} ?: ""
//
//    val id = puid()
//
//    val me = json(
//        "render" to render@{
//            return@render Shitus.diva(json("className" to "page-header ${className}", "style" to json("marginTop" to 0, "marginBottom" to 15)),
//                Shitus.h3a.apply(null, jsArrayOf().concat(json("tame" to "pageHeader", "style" to json("marginBottom" to 0)),
//                    Shitus.spancTitle(json("title" to title)),
//                    labels.map({label: dynamic, i: dynamic ->
//            val style = json(
//                "fontSize" to "12px",
//                "fontWeight" to "normal",
//                "position" to "relative",
//                "top" to "-4px",
//                "left" to "8px",
//                "display" to "inline",
//                "padding" to ".2em .6em .3em",
//                "lineHeight" to "1",
//                "color" to "#fff",
//                "textAlign" to "center",
//                "whiteSpace" to "nowrap",
//                "verticalAlign" to "baseline",
//                "borderRadius" to ".25em"
//            )
//            if (label.level == "success") {
//                global.Object.assign(style, json("background" to "" + LIGHT_GREEN_700))
//            } else {
//                Shitus.raise("Weird pageHeader label level: ${label.level}")
//            }
//            return@map Shitus.spana(json("tame" to "label${Shitus.sufindex(i)}", "tattrs" to json("level" to label.level), "style" to style),
//                Shitus.spancTitle(json("title" to label.title)))
//        }))))
//        }
//    )
//
//    return jsFacing_elcl(me)

