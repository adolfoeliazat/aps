/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

require('regenerator-runtime/runtime')
require('source-map-support').install()
import express = require('express')
import static 'into-u'

serve(3011, 'en-customer')
serve(3012, 'ua-customer')
serve(3021, 'en-writer')
serve(3022, 'ua-writer')


function serve(port, dir) {
    const app = express()
    app.use(express.static(`${__dirname}/../built/${dir}`))

    app.listen(port, _=> {
        clog(`Serving ${dir} on 127.0.0.1:${port}`)
    })
}


