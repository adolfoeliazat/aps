/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.GenericResponse
import aps.WorldPointRequest

class WorldPointRemoteProcedure() : RemoteProcedure<WorldPointRequest, GenericResponse>() {
    override val access: Access = Access.SYSTEM

    override fun doStuff() {
        val oldRejectAllRequests = TestServerFiddling.rejectAllRequests
        TestServerFiddling.rejectAllRequests = true

        try {
            val snapshotDBName = "world_point_${req.pointName}"

            val databaseToCreate: String; val databaseToUseAsTemplate: String
            when (req.action) {
                WorldPointRequest.Action.SAVE -> {
                    databaseToCreate = snapshotDBName
                    databaseToUseAsTemplate = DB.apsTestOnTestServer.name
                }
                WorldPointRequest.Action.RESTORE -> {
                    databaseToCreate = DB.apsTestOnTestServer.name
                    databaseToUseAsTemplate = snapshotDBName
                }
            }

            DB.apsTestOnTestServer.close()
            DB.postgresOnTestServer.joo{it.execute("""
                drop database if exists "${databaseToCreate}";
                create database "${databaseToCreate}" template = "${databaseToUseAsTemplate}";
            """)}
        } finally {
            TestServerFiddling.rejectAllRequests = oldRejectAllRequests
        }
    }
}



