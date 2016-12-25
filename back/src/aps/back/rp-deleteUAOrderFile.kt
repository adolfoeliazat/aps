/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.tables.pojos.*
import into.kommon.*
import java.util.*

@RemoteProcedureFactory fun deleteUAOrderFile() = anyUserProcedure(
    DeleteUAOrderFileRequest(),
    runShit = fun(ctx, req): DeleteUAOrderFileRequest.Response {
        val orderFileID = req.orderFileID.value.toLong()

        UA_ORDER_FILES.let {t->
            ctx.updateShit("Deleting order file", t)
                .set(t.DELETED, true)
                .where(t.ID.eq(orderFileID))
                .execute()
        }

        return DeleteUAOrderFileRequest.Response()
    }
)
























