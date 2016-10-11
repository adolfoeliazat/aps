/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.tables.pojos.Users
import org.jooq.*
import kotlin.reflect.KClass

@RemoteProcedureFactory fun getUser() = adminProcedure(
    GetUserRequest(),
    runShit = {ctx, req ->
        GetUserRequest.Response(ctx.q
            .select().from(USERS)
            .where(USERS.ID.eq(req.id.value.toLong()))
            .fetchOne().into(Users::class.java).toRTO(ctx.q))
    }
)
























