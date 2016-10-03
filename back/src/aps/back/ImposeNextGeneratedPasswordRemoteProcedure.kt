/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.GenericResponse
import aps.ImposeNextGeneratedPasswordRequest

class ImposeNextGeneratedPasswordRemoteProcedure() : RemoteProcedure<ImposeNextGeneratedPasswordRequest, GenericResponse>() {
    override val access: Access = Access.SYSTEM

    override fun doStuff() {
        TestServerFiddling.nextGeneratedPassword = req.password
    }
}



