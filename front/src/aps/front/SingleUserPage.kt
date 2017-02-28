package aps.front

import aps.*
import into.kommon.*

class SingleUserPage {
    var tabitha by notNullOnce<Tabitha<UserRTO>>()

    val user get()= tabitha.entity

    suspend fun load(): PageLoadingError? {
        check(isAdmin()){"b8a7c482-214f-4875-9614-f7e062525d70"}
        tabitha = Tabitha<UserRTO>(
            page = pages.uaAdmin.user,
            tabKeys = tabs.user,
            defaultTab = tabs.user.params,
            loadEntity = {id->
                askRegina(ReginaLoadUser(id))
            },
            renderBelowHeader = {
                when (user.state) {
                    UserState.PROFILE_APPROVAL_PENDING -> acceptOrRejectDolly(
                        message = t("TOTE", "Что будем делать с засранцем?"),
                        acceptButtonTitle = t("TOTE", "Принять"),
                        blankRejectionRequest = RejectProfileRequest(),
                        entityID = user.id,
                        tabitha = tabitha,
                        makeAcceptanceRequestParams = ::ReginaAcceptProfile,
                        bottomGap = true)
                    UserState.PROFILE_REJECTED -> renderMaybeRejectionReasonBanner(user.profileRejectionReason)
                    else -> NOTRE
                }
            },
            makeTabs = {listOf(
                UsualParamsTab<UserRTO, UserParamsHistoryItemRTO, UserParamsHistoryFilter, UserParamsRequest, GenericResponse>(
                    tabitha,
                    tabKey = tabs.user.params,
                    renderBody = {renderProfile(user)},
                    hasEditButton = true,
                    editModalTitle = t("TOTE", "Параметры засранца"),
                    makeFormSpec = {FormSpec<UserParamsRequest, GenericResponse>(
                        procedureName = "UpdateUser",
                        req = UserParamsRequest(isUpdate = true).populateCheckingCompleteness{o->
                            o.userID.value = user.id
                            o.firstName.value = user.firstName
                            o.lastName.value = user.lastName
                            o.email.value = user.email
                            o.phone.value = user.profilePhone
                            populateWithAdminNotes(o, user)
                        }
                    )},
                    historyParams = HistoryParams<UserParamsHistoryItemRTO, UserParamsHistoryFilter>(
                        historyItemClass = UserParamsHistoryItemRTO::class,
                        renderItem = {thisItem, thatItem ->
                            renderUserParamsHistoryItem(thisItem, when {
                                thatItem == null -> UserRTORenderingOptions()
                                else -> UserRTORenderingOptions(
                                    outlinePhone = thisItem.entity.profilePhone != thatItem.entity.profilePhone,
                                    outlineState = thisItem.entity.state != thatItem.entity.state,
                                    outlineFirstName = thisItem.entity.firstName != thatItem.entity.firstName,
                                    outlineLastName = thisItem.entity.lastName != thatItem.entity.lastName,
                                    outlineAboutMe = thisItem.entity.aboutMe != thatItem.entity.aboutMe,
                                    adminNotesOptions = RTOWithAdminNotesRenderingOptions.fromComparedPair(thisItem.entity, thatItem.entity)
                                )}
                            )
                        },
                        sendHistoryItemsRequest = {req-> sendGetUserParamsHistoryItems(req)},
                        historyFilterValues = UserParamsHistoryFilter.values(),
                        defaultHistoryFilterValue = UserParamsHistoryFilter.ALL,
                        historyFilterSelectKey = selects.userParamsHistoryFilter
                    )
                )
            )},
            pageHeaderTitle = {t("TOTE", "Засранец ${const.text.numberSign}${user.id}: ${user.firstName} ${user.lastName}")},
            pageHeaderTitleLeftIcon = {userKindIcon(user.kind)},
            subtitle = {null},
            renderBelowSubtitle = fun(): ToReactElementable {
                return NOTRE
            }
        )
        return tabitha.load()
    }

}




