package aps.back

import aps.*

@RemoteProcedureFactory fun serveTestTakeSnapshot() = testProcedure(
    TestTakeSnapshotRequest(),
    runShit = fun(ctx, req): TestTakeSnapshotRequest.Response {
        dwarnStriking("Taking snapshot: ${req.name.value} @ ${req.url.value}")
        return TestTakeSnapshotRequest.Response()
    }
)


