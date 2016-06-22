/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

DEBUG_SIMULATE_SLOW_NETWORK = true

require('regenerator-runtime/runtime')
import express = require('express')
import static 'into-u'

const app = express()
app.get('/rpc', (req, res) => {
    res.send('cool')
})

const port = 3100
app.listen(port, _=> {
    clog(`Backend is spinning on 127.0.0.1:${port}`)
})
