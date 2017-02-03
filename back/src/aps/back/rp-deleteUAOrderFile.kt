/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*

@RemoteProcedureFactory fun deleteUAOrderFile() = anyUserProcedure(
    {DeleteUAOrderFileRequest()},
    runShit = fun(ctx, req): DeleteRequest.Response {
        // Thread.sleep(5000)
        TestServerFiddling.nextRequestError.getAndReset()?.let(::bitchExpectedly)

        val orderFileID = req.id.value.toLong()

        UA_ORDER_FILES.let {t->
            ctx.updateShit("Deleting order file", t) {it
                .set(t.DELETED, true)
                .where(t.ID.eq(orderFileID))
                .execute()
            }
        }

        return DeleteRequest.Response()
    }
)
























