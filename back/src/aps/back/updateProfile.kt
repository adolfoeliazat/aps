package aps.back

import aps.back.generated.jooq.Tables.*
import aps.UpdateProfileRequest
import aps.UserState
import aps.back.MatumbaProcedure.*
import aps.back.generated.jooq.tables.pojos.Users

@RemoteProcedureFactory
fun updateProfile() = MatumbaProcedure(UpdateProfileRequest(), UpdateProfileRequest.Response()) {
    access = Access.USER

    runShit = {
        q.update(USERS)
            .set(USERS.PROFILE_UPDATED_AT, requestTimestamp)
            .set(USERS.PHONE, req.profileFields.phone.value)
            .set(USERS.COMPACT_PHONE, compactPhone(req.profileFields.phone.value))
            .set(USERS.ABOUT_ME, req.profileFields.aboutMe.value)
            .set(USERS.STATE, UserState.PROFILE_APPROVAL_PENDING.name)
            .set(USERS.ASSIGNED_TO, THE_ADMIN_ID)
            .where(USERS.ID.eq(user.id.toLong()))
            .execute()

        val users = q.select().from(USERS).where(USERS.ID.eq(user.id.toLong())).fetch().into(Users::class.java)
        res.newUser = users.first().toRTO(q)
    }
}


//fun updateProfileRemoteProcedure() = MatumbaProcedure(UpdateProfileRequest(), UpdateProfileRequest.Response())

//class UpdateProfileRemoteProcedure() : MatumbaProcedure(UpdateProfileRequest(), UpdateProfileRequest.Response()) {
//    override val access: Access = Access.USER
//    val log by logger()
//
//    val phone = phoneField()
//    val aboutMe = textField("aboutMe", 1, 300)
//
//    override fun doStuff() {
//        q.update(USERS)
//            .set(USERS.PROFILE_UPDATED_AT, requestTimestamp)
//            .set(USERS.PHONE, phone.value)
//            .set(USERS.COMPACT_PHONE, compactPhone(phone.value))
//            .set(USERS.ABOUT_ME, aboutMe.value)
//            .set(USERS.STATE, UserState.PROFILE_APPROVAL_PENDING.name)
//            .set(USERS.ASSIGNED_TO, THE_ADMIN_ID)
//            .where(USERS.ID.eq(user.id.toLong()))
//            .execute()
//
//        val users = q.select().from(USERS).where(USERS.ID.eq(user.id.toLong())).fetch().into(Users::class.java)
//        res.newUser = users.first().toRTO()
//    }

//}

