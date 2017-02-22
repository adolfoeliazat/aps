package aps.back

import aps.*

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
                return UpdateProfileRequest.Response(user.toRTO())
            }
        ))
    }
}



