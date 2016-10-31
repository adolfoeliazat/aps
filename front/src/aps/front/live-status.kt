/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*

fun jsFacing_pollLiveStatus(ui: ShitPile): Promise<Unit> {"__async"
    return __asyncResult(Unit)
}

fun jsFacing_startLiveStatusPolling(ui: ShitPile) {
    ui.liveStatusPollingIntervalHandle = global.setInterval({
        if (!hrss.liveStatusPollingViaIntervalDisabled) {
            ui.pollLiveStatus()
        }
    }, global.LIVE_STATUS_POLLING_INTERVAL)
}

fun jsFacing_stopLiveStatusPolling(ui: ShitPile) {
    global.clearInterval(ui.liveStatusPollingIntervalHandle)
}

