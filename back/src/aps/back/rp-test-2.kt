package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import com.fasterxml.jackson.databind.JsonNode

@RemoteProcedureFactory fun serveTestTakeSnapshot() = testProcedure(
    TestTakeSnapshotRequest(),
    needsDB = true,
    runShit = fun(ctx, req): TestTakeSnapshotRequest.Response {
        dwarnStriking("Taking snapshot: ${req.name.value} @ ${req.url.value}")
//        KEY_VALUE_STORE.let {t->
//            ctx.insertShit("Store URL", t) {it
//                .set(t.KEY, bconst.kvs.test.snapshotURL)
//                .set(t.VALUE, objectMapper.valueToTree<JsonNode>(req.url.value))
//                .onConflict(t.KEY)
//                .doUpdate()
//                .set(t.VALUE, objectMapper.valueToTree<JsonNode>(req.url.value))
//                .execute()
//            }
//        }
        DB.apsTestSnapshotOnTestServer(req.name.value).recreate(template = DB.apsTestOnTestServer)
        return TestTakeSnapshotRequest.Response()
    }
)

@RemoteProcedureFactory fun serveTestLoadSnapshot() = testProcedure(
    TestLoadSnapshotRequest(),
    needsDB = true,
    runShit = fun(ctx, req): TestLoadSnapshotRequest.Response {
        val rec = tracingSQL("Load snapshot") {
            ctx.q.fetchOne(KEY_VALUE_STORE, KEY_VALUE_STORE.KEY.eq(bconst.kvs.test.snapshotURL))
        }
        val url = objectMapper.treeToValue(rec.value, String::class.java)
        dwarnStriking("Snapshot URL: $url")
        return TestLoadSnapshotRequest.Response(url)
    }
)


