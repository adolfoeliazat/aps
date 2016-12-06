/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.tables.pojos.JQUsers
import org.jooq.*
import kotlin.reflect.KClass

@RemoteProcedureFactory fun customerCreateUAOrder() = customerProcedure(
    CustomerCreateUAOrderRequest(),
    runShit = fun(ctx, req): GenericResponse {
        return GenericResponse()
    }
)

























