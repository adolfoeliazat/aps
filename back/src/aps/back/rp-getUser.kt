/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.tables.pojos.JQUsers
import org.jooq.*
import kotlin.reflect.KClass

//@RemoteProcedureFactory fun getUser() = adminProcedure(
//    {GetUserRequest()},
//    runShit = fun(ctx, req): GetUserRequest.Response {
//        return GetUserRequest.Response(tracingSQL("Select user") {ctx.q
//            .select().from(USERS)
//            .where(USERS.ID.eq(req.id.value.toLong()))
//            .fetchOne().into(JQUsers::class.java).toRTO(ctx.q)
//        })
//    }
//)
























