/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

const DEBUG_SIMULATE_SLOW_NETWORK = true

import static 'into-u/utils-client'

asn(global, {
    initCustomerUI({lang, pageName}) {
        timeoutSet(DEBUG_SIMULATE_SLOW_NETWORK ? 1000 : 0, _=> {
            $('#wholePageSpinner').hide()
            clog('initializing', {lang, pageName})
        })
    }
})

clog('Client code is kind of loaded')
