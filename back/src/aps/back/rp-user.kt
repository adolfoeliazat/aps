package aps.back

import aps.*
import into.kommon.*
import org.springframework.data.repository.findOrDie

fun serveReginaLoadUser(p: ReginaLoadUser): SimpleEntityResponse<UserRTO> {
    check(isAdmin()){"14b9cd37-57e6-4c82-a16d-ef37a2e38a4d"}
    return SimpleEntityResponse(userRepo.findOrDie(p.userID).toRTO(searchWords = listOf()))
}

@Servant class ServeUpdateProfile : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc, makeRequest = {UpdateProfileRequest()},
            runShit = fun(ctx, req): UpdateProfileRequest.Response {
                checkingAllFieldsRetrieved(req) {
                    // TODO:vgrechka Security
                    val user = ctx.user!!
                    user-{o->
                        o.user.firstName = req.firstName.value
                        o.user.lastName = req.lastName.value
                        o.user.profilePhone = req.profilePhone.value
                        o.user.aboutMe = req.aboutMe.value
                        o.user.common.updatedAt = RequestGlobus.stamp
                        o.user.profileUpdatedAt = RequestGlobus.stamp

                        if (o.user.kind == UserKind.WRITER) {
                            o.user.state = UserState.PROFILE_APPROVAL_PENDING
                        }
                    }
                    return UpdateProfileRequest.Response(user.toRTO(searchWords = listOf()))
                }
            }
        ))
    }
}

@Servant class ServeGetUsers : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc,
            makeRequest = {ItemsRequest(AdminUserFilter.values())},
            runShit = fun(ctx, req): ItemsResponse<UserRTO> {
                return megan(
                    req = req,
                    checkShit = {
                        // TODO:vgrechka Security
                    },
                    table = "users",
                    itemClass = User::class.java,
                    addToWhere = {s, params ->
                        fun filterByState(state: UserState) {
                            s += " and user_state = :state"
                            params += MeganQueryParam("state", state.name)
                        }

                        exhaustive/when (req.filter.value) {
                            AdminUserFilter.ALL -> {}
                            AdminUserFilter.COOL -> filterByState(UserState.COOL)
                            AdminUserFilter.PROFILE_APPROVAL_PENDING -> filterByState(UserState.PROFILE_APPROVAL_PENDING)
                            AdminUserFilter.PROFILE_REJECTED -> filterByState(UserState.PROFILE_REJECTED)
                            AdminUserFilter.BANNED -> filterByState(UserState.BANNED)
                        }
                    }
                )
            }
        ))
    }
}

@Servant class ServeGetUserParamsHistoryItems : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc,
            makeRequest = {ItemsRequest(UserParamsHistoryFilter.values())},
            runShit = fun(ctx, req): ItemsResponse<UserParamsHistoryItemRTO> {
                return megan<UserParamsHistoryItem, UserParamsHistoryItemRTO, UserParamsHistoryFilter>(
                    req = req,
                    checkShit = {
                        // TODO:vgrechka ...
                    },
                    table = "user_params_history_items",
                    itemClass = UserParamsHistoryItem::class.java,
                    parentKey = "history_entityID",
                    addToWhere = {_,_->}
                )
            }
        ))
    }
}

@Servant class ServeUpdateUser : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc,
            makeRequest = {UserParamsRequest(isUpdate = true)},
            runShit = fun(ctx, req: UserParamsRequest): GenericResponse {
                // TODO:vgrechka Security
                check(isAdmin()){"f15046b7-c5ba-471e-836b-36fbaa56a0d6"}
                checkingAllFieldsRetrieved(req) {
                    val user = userRepo.findOrDie(req.userID.value)
                    user-{o->
                        o.user.firstName = req.firstName.value
                        o.user.lastName = req.lastName.value
                        o.user.email = req.email.value
                        o.user.profilePhone = req.phone.value
                        updateAdminNotes(o.user, req)
                    }
                }
                return GenericResponse()
            }
        ))
    }
}

@Servant class ServeRejectProfile : BitchyProcedure() {
    override fun serve() {
        fuckAdmin(FuckAdminParams(
            bpc = bpc, makeRequest = {RejectProfileRequest()},
            runShit = fun(ctx, req): GenericResponse {
                userRepo.findOrDie(req.entityID.value)-{o->
                    o.user.state = UserState.PROFILE_REJECTED
                    o.user.profileRejectionReason = req.rejectionReason.value
                }
                return GenericResponse()
            }
        ))
    }
}

fun serveReginaAcceptProfile(p: ReginaAcceptProfile): GenericResponse {
    check(requestUser.user.kind == UserKind.ADMIN){"0efef8d0-8598-4056-ba55-cd8bb1910cb8"}
    // TODO:vgrechka Security
    userRepo.findOrDie(p.userID)-{o->
        check(o.user.state in setOf(UserState.PROFILE_APPROVAL_PENDING)){"7af262c7-2a28-43f8-910a-ccf3569142e9"}
        o.user.profileRejectionReason = null
        o.user.state = UserState.COOL
    }
    return GenericResponse()
}




















