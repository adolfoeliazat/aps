package aps.back

import aps.*
import into.kommon.*
import org.springframework.data.repository.findOrDie
import sun.net.www.content.text.Generic

fun serveReginaLoadUser(p: ReginaLoadUser): SimpleEntityResponse<UserRTO> {
    check(isAdmin()){"14b9cd37-57e6-4c82-a16d-ef37a2e38a4d"}
    return SimpleEntityResponse(userRepo.findOrDie(p.userID).toRTO(searchWords = listOf()))
}

@Servant class ServeUpdateProfile : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc, makeRequest = {UpdateProfileRequest()},
            runShit = fun(ctx, req): UpdateProfileRequest.Response {
                // TODO:vgrechka Security
                val user = ctx.user!!-{o->
                    o.firstName = req.firstName.value
                    o.lastName = req.lastName.value
                    o.profilePhone = req.profilePhone.value
                    o.aboutMe = req.aboutMe.value
                    o.updatedAt = RequestGlobus.stamp
                    o.profileUpdatedAt = RequestGlobus.stamp

                    if (o.kind == UserKind.WRITER) {
                        o.state = UserState.PROFILE_APPROVAL_PENDING
                    }
                }
                return UpdateProfileRequest.Response(user.toRTO(searchWords = listOf()))
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
                            s += " and state = :state"
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

@Servant class ServeUpdateUser : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc,
            makeRequest = {UserParamsRequest(isUpdate = true)},
            runShit = fun(ctx, req: UserParamsRequest): GenericResponse {
                // TODO:vgrechka Security
                check(isAdmin()){"f15046b7-c5ba-471e-836b-36fbaa56a0d6"}
                checkingAllFieldsRetrieved(req) {
                    userRepo.findOrDie(req.userID.value)-{o->
                        o.firstName = req.firstName.value
                        o.lastName = req.lastName.value
                        o.email = req.email.value
                        o.profilePhone = req.phone.value
                        updateAdminNotes(o, req)
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
                    o.state = UserState.PROFILE_REJECTED
                    o.profileRejectionReason = req.rejectionReason.value
                }
                return GenericResponse()
            }
        ))
    }
}

fun serveReginaAcceptProfile(p: ReginaAcceptProfile): GenericResponse {
    check(requestUser.kind == UserKind.ADMIN){"0efef8d0-8598-4056-ba55-cd8bb1910cb8"}
    // TODO:vgrechka Security
    userRepo.findOrDie(p.userID)-{o->
        check(o.state in setOf(UserState.PROFILE_APPROVAL_PENDING)){"7af262c7-2a28-43f8-910a-ccf3569142e9"}
        o.profileRejectionReason = null
        o.state = UserState.COOL
    }
    return GenericResponse()
}




















