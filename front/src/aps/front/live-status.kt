/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

fun jsFacing_pollLiveStatus(ui: dynamic): Promise<Unit> {"__async"
    return __asyncResult(Unit)
}

fun jsFacing_startLiveStatusPolling(ui: dynamic) {
    ui.liveStatusPollingIntervalHandle = global.setInterval({
        if (!hrss.liveStatusPollingViaIntervalDisabled) {
            ui.pollLiveStatus()
        }
    }, global.LIVE_STATUS_POLLING_INTERVAL)
}

fun jsFacing_stopLiveStatusPolling(ui: dynamic) {
    global.clearInterval(ui.liveStatusPollingIntervalHandle)
}

