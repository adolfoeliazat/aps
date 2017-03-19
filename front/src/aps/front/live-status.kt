/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

fun jsFacing_pollLiveStatus(ui: World): Promisoid<Unit> = async {
}

//fun jsFacing_startLiveStatusPolling(ui: World) {
//    ui.liveStatusPollingIntervalHandle = global.setInterval({
//        if (!hrss.liveStatusPollingViaIntervalDisabled) {
//            ui.pollLiveStatus()
//        }
//    }, global.LIVE_STATUS_POLLING_INTERVAL)
//}
//
//fun jsFacing_stopLiveStatusPolling(ui: World) {
//    global.clearInterval(ui.liveStatusPollingIntervalHandle)
//}

