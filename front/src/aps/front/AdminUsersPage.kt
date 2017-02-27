/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import kotlin.browser.document
import aps.Color.*
import aps.*
import into.kommon.*
import kotlin.js.json

class AdminUsersPage {
    private var bint by notNullOnce<MelindaBoobsInterface>()

    // TODO:vgrechka Deduplicate    f5af6cdf-3fa1-4b09-985e-f949e187fa60
    suspend fun load(): PageLoadingError? {
        val boobs = makeBoobs()

        bint = boobs.boobsInterface
        boobs.load()?.let {return PageLoadingError(it.error)}
        Globus.world.setPage(Page(header = usualHeader(t("TOTE", "Засранцы")),
                                  headerControls = kdiv{o->
                                      o- bint.controlsContent
                                  },
                                  body = bint.mainContent))
        return pageLoadedFineResult
    }

    fun makeBoobs(): MelindaBoobs<UserRTO, AdminUserFilter, UserParamsRequest, CreateUserResponse, UserParamsRequest, UpdateUserResponse> {
        return MelindaBoobs(
            createParams = MelindaCreateParams(
                hasCreateButton = false,
                createModalTitle = t("TOTE", "Новый засранец"),
                makeCreateRequest = {UserParamsRequest(isUpdate = false)},
                makeURLAfterCreation = {res->
                    makeURL(pages.uaAdmin.user, listOf(URLParamValue(TabithaURLQuery.id, res.userID)))
                }
            ),
            makeURLForReload = {boobsParams ->
                makeURL(pages.uaAdmin.users, boobsParams)
            },
            filterValues = AdminUserFilter.values(),
            defaultFilterValue = AdminUserFilter.ALL,
            filterSelectKey = selects.adminUserFilter,
            vaginalInterface = MelindaVagina<UserRTO, AdminUserFilter, UserParamsRequest, UpdateUserResponse> (
                sendItemsRequest = {req -> sendGetUsers(req)},
                shouldShowFilter = {true},
                getParentEntityID = {null},
                humanItemTypeName = t("TOTE", "засранец"),
                makeDeleteItemRequest = {DeleteUserRequest()},
                updateParams = null,
                makeLipsInterface = {viewRootID, tongue -> makeUsualMelindaLips(
                    viewRootID,
                    searchString = bint.getSearchString(),
                    icon = {userKindIcon(it.kind)},
                    initialLipsState = Unit,
                    renderContent = {o->
                        o- renderProfile(tongue.item)
                    },
                    titleLinkURL = makeURL(pages.uaAdmin.user, listOf(
                        URLParamValue(TabithaURLQuery.id, tongue.item.id)
                    )),
                    getItem = tongue.toItemSupplier()
                )}
            )
        )
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

