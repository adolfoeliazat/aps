/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import kotlin.browser.document
import aps.Color.*

class AdminUsersPage(val ui: dynamic) {
    fun load(): Promise<Unit> {"__async"
        __await(jsFacing_melinda(json(
            "ui" to ui,
            "urlPath" to "admin-users.html",
            "itemsFun" to "private_getUsers",
            "header" to { entityRes: dynamic ->
                jshit.pageHeader(json("title" to t("TOTE", "Пользователи")))
            },

            "hasFilterSelect" to true,
            "filterSelectValues" to global.apsdata.userFilters(),
            "defaultFilter" to "all",

            "plusIcon" to "plus",

            "plusFormDef" to json(
                "primaryButtonTitle" to t("TOTE", "Запостить"),
                "cancelButtonTitle" to t("TOTE", "Передумал"),
                "autoFocus" to "message",
                "fields" to jsArrayOf(
                    ui.HiddenField(json(
                        "name" to "threadID",
                        "value" to ui.urlQuery.thread
                    )),
                    ui.TextField(json(
                        "name" to "message",
                        "kind" to "textarea",
                        "title" to t("TOTE", "Сообщение")
                    ))
                ),
                "rpcFun" to "private_createSupportThreadMessage",
                "onSuccess" to {res: dynamic -> "__async"
                    __await<dynamic>(ui.pushNavigate("support.html?thread=${ui.urlQuery.thread}"))
                }
            ),

            "renderItem" to renderItem@{def: dynamic ->
                // #extract {item: user, index} from def
                var user = def.item; val index = def.index

                object {
                    val headingID = "hehe-" + jshit.utils.puid()
                    val placeholder = jshit.Placeholder()

                    init {
                        enterDisplayMode()
                    }

                    fun enterDisplayMode() {
                        peggy(json(
                            "headingActionItems" to jsArrayOf(
                                jshit.faIcon(json("tamy" to "edit", "className" to "hover-color-BLUE_GRAY_800", "style" to json("fontSize" to "135%", "cursor" to "pointer"), "icon" to "pencil",
                                    "onClick" to { enterEditMode() }))),
                            "body" to jshit.diva(js("({})"), KotlinShit.renderProfile(json("user" to user)))))
                    }

                    fun enterEditMode() {
                        var form: dynamic = null // @workaround
                        form = ui.Form(json(
                            "dontShameButtons" to true,
                            "errorBannerStyle" to json("marginTop" to 15),
                            "primaryButtonTitle" to t("TOTE", "Сохранить"),
                            "cancelButtonTitle" to t("TOTE", "Передумал"),

                            "getInvisibleFieldNames" to getInvisibleFieldNames@{
                                var invisible = jsArrayOf("profileRejectionReason", "banReason")

                                val state = form.getField("state").getValue()
                                if (state == "profile-rejected") {
                                    invisible = jshit.utils.without(invisible, "profileRejectionReason")
                                }
                                else if (state == "banned") {
                                    invisible = jshit.utils.without(invisible, "banReason")
                                }

                                return@getInvisibleFieldNames invisible
                            },

                            "fields" to js("[]").concat(
                                jsArrayOf(
                                    ui.HiddenField(json(
                                        "name" to "id",
                                        "value" to user.id
                                    )),

                                    ui.SelectField(json(
                                        "name" to "state",
                                        "title" to t("TOTE", "Статус"),
                                        "values" to global.apsdata.userStates()
                                    )),

                                    ui.TextField(json(
                                        "name" to "profileRejectionReason",
                                        "kind" to "textarea",
                                        "title" to t("TOTE", "Причина отказа")
                                    )),

                                    ui.TextField(json(
                                        "name" to "banReason",
                                        "kind" to "textarea",
                                        "title" to t("TOTE", "Причина бана")
                                    ))
                                ),

                                ui.makeSignUpFields(js("({})")),
                                KotlinShit.makeProfileFields(js("({})")),

                                jsArrayOf(
                                    ui.TextField(json(
                                        "name" to "adminNotes",
                                        "kind" to "textarea",
                                        "title" to t("TOTE", "Заметки админа")
                                    ))
                                )
                            ),

                            "rpcFun" to "private_updateUser",
                            "onCancel" to {
                                placeholder.setPrevContent()
                                scrollToHeading()
                            },
                            "onSuccess" to {res: dynamic -> "__async"
                                __await<dynamic>(refreshRecord())
                                scrollToHeading()
                            },
                            "onError" to {
                                scrollToHeading()
                            }
                        ))

                        form.getField("state").setValue(user.state)
                        form.getField("email").setValue(user.email)
                        form.getField("firstName").setValue(user.first_name)
                        form.getField("lastName").setValue(user.last_name)
                        form.getField("phone").setValue(user.phone)
                        form.getField("aboutMe").setValue(user.about_me)
                        form.getField("profileRejectionReason").setValue(user.profile_rejection_reason || js("''"))
                        form.getField("adminNotes").setValue(user.admin_notes || js("''"))

                        peggy(json(
                            "headingActionItems" to jsArrayOf(),
                            "body" to jshit.diva(json("style" to json("marginBottom" to 15)), form)))

                        scrollToHeading()
                    }

                    fun peggy(def: dynamic) {
                        // #extract {headingActionItems, body} from def
                        val headingActionItems = def.headingActionItems; val body = def.body

                        placeholder.setContent(diva {tame = sufindex("item", index)
                            - diva {tame = "heading"; elementID = headingID; style {marginBottom(10); backgroundColor = BLUE_GRAY_50; borderBottom = "1px solid ${BLUE_GRAY_100}"}
                                - spana {style {fontWeight = "normal"}
                                    - spanc("title", user.first_name + " " + user.last_name) {style {fontSize = "135%"}}
                                    - spanc("no", "" + jshit.nostring(json("no" to user.id))) {style {color = GRAY_500; marginLeft(12)}}}

                                - asReactElement(jshit.hor2(json("style" to json("float" to "right", "marginTop" to 4, "marginRight" to 4, "color" to jshit.BLUE_GRAY_600),
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
                        val res = __await<dynamic>(ui.rpcSoft(json("fun" to "private_getUser", "id" to user.id)))
                        if (res.error) {
                            peggy(json(
                                "headingActionItems" to jsArrayOf(),
                                "body" to jshit.errorBanner(json("content" to res.error))))
                        } else {
                            user = res.user
                            enterDisplayMode()
                        }

                        return __asyncResult(Unit) // @workaround
                    }

                }.placeholder
            }
        )))

        return __asyncResult(Unit)
    }
}



