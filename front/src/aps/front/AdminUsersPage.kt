/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import kotlin.browser.document
import aps.front.Color.*
import aps.*

fun pageHeader(arg: Json): ReactElement {
    val title: dynamic = arg["title"]
    val labels: dynamic = arg["labels"]?.let{it} ?: jsArrayOf()
    val className: dynamic = arg["classname"]?.let{it} ?: ""

    val id = puid()

    val me = json(
        "render" to render@{
            return@render Shitus.diva(json("className" to "page-header ${className}", "style" to json("marginTop" to 0, "marginBottom" to 15)),
                Shitus.h3a.apply(null, jsArrayOf().concat(json("tame" to "pageHeader", "style" to json("marginBottom" to 0)),
                    Shitus.spancTitle(json("title" to title)),
                    labels.map({label: dynamic, i: dynamic ->
            val style = json(
                "fontSize" to "12px",
                "fontWeight" to "normal",
                "position" to "relative",
                "top" to "-4px",
                "left" to "8px",
                "display" to "inline",
                "padding" to ".2em .6em .3em",
                "lineHeight" to "1",
                "color" to "#fff",
                "textAlign" to "center",
                "whiteSpace" to "nowrap",
                "verticalAlign" to "baseline",
                "borderRadius" to ".25em"
            )
            if (label.level == "success") {
                global.Object.assign(style, json("background" to "" + LIGHT_GREEN_700))
            } else {
                Shitus.raise("Weird pageHeader label level: ${label.level}")
            }
            return@map Shitus.spana(json("tame" to "label${jshit.sufindex(i)}", "tattrs" to json("level" to label.level), "style" to style),
                Shitus.spancTitle(json("title" to label.title)))
        }))))
        }
    )

    return jsFacing_elcl(me)
}

class AdminUsersPage(val ui: LegacyUIShit) {
    fun load(): Promise<Unit> {"__async"
        __await(Melinda<UserRTO, Nothing, UserFilter>(
            ui,
            urlPath = "admin-users.html",
            procedureName = "getUsers",
            header = {oldShitAsReactElementable(pageHeader(json("title" to t("TOTE", "Пользователи"))))},
            filterSelectValues = UserFilter.values(),
            defaultFilter = UserFilter.ALL,
            plusFormSpec = FormSpec<AdminCreateUserRequest, GenericResponse>(
                AdminCreateUserRequest(), ui,
                primaryButtonTitle = t("TOTE", "Создать засранца"),
                cancelButtonTitle = defaultCancelButtonTitle),

            renderItem = {index, _user -> oldShitAsReactElementable(object {
                var user = _user
                val renderedThing = jshit.Placeholder()
                val headingID = "hehe-" + puid()

                init {
                    enterDisplayMode()
                }

                fun enterDisplayMode() {
                    peggy(json(
                        "headingActionItems" to jsArrayOf(
                            Shitus.faIcon(json("tamy" to "edit", "className" to "hover-color-BLUE_GRAY_800", "style" to json("fontSize" to "135%", "cursor" to "pointer"), "icon" to "pencil",
                                "onClick" to { enterEditMode() }))),
                        "body" to Shitus.diva(js("({})"), renderProfile(ui, user))))
                }


                fun enterEditMode() {
                    peggy(json(
                        "headingActionItems" to jsArrayOf(),
                        "body" to Shitus.diva(json("style" to json("marginBottom" to 15)),
                            FormMatumba<UpdateUserRequest, GenericResponse>(FormSpec(
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
                                    renderedThing.setPrevContent()
                                    scrollToHeading()
                                },

                                onError = {
                                    scrollToHeading()
                                }

                            )).toReactElement()
                        )))

                    scrollToHeading()
                }

                fun peggy(def: dynamic) {
                    // #extract {headingActionItems, body} from def
                    val headingActionItems = def.headingActionItems; val body = def.body

                    renderedThing.setContent(diva {tame = sufindex("item", index)
                        - diva {tame = "heading"; elementID = headingID; style {marginBottom(10); backgroundColor = BLUE_GRAY_50; borderBottom = "1px solid ${BLUE_GRAY_100}"}
                            - spana {style {fontWeight = "normal"}
                                - spanc("title", user.firstName + " " + user.lastName) {style {fontSize = "135%"}}
                                - spanc("no", "" + jshit.nostring(json("no" to user.id))) {style {color = GRAY_500; marginLeft(12)}}}

                            - asReactElement(jshit.hor2(json("style" to json("float" to "right", "marginTop" to 4, "marginRight" to 4, "color" to Color.BLUE_GRAY_600),
                                "items" to headingActionItems)))
                        }

                        - asReactElement(body)
                    }.toReactElement())
                }

                fun scrollToHeading() {
                    val top = jshit.byid(headingID).offset().top - 50 - 15
                    global.requestAnimationFrame { jshit.utils.jQuery(document).scrollTop(top) }
                }


                fun refreshRecord(): Promise<Unit> {"__async"
                    val res = __await(GetUserRequest.send(ui.token!!, user.id))
                    return ignora/when (res) {
                        is ZimbabweResponse.Shitty -> peggy(json(
                            "headingActionItems" to jsArrayOf(),
                            "body" to jshit.errorBanner(json("content" to res.error))))

                        is ZimbabweResponse.Hunky -> {
                            user = res.meat.user
                            enterDisplayMode()
                        }
                    }
                }
            }.renderedThing)
            }
        ).ignita())
        return __asyncResult(Unit)
    }
}

















