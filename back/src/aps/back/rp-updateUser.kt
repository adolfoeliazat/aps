/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*

@RemoteProcedureFactory fun updateUser() = adminProcedure(
    UpdateUserRequest(),
    runShit = {ctx, req ->
        imf("updateUser")
    }
)


