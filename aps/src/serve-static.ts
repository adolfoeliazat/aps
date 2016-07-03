/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

require('regenerator-runtime/runtime')
require('source-map-support').install()
import express = require('express')
import static 'into-u'

serve(3011, 'en-customer', `${__dirname}/../built/en-customer`)
serve(3012, 'ua-customer', `${__dirname}/../built/ua-customer`)
serve(3021, 'en-writer', `${__dirname}/../built/en-writer`)
serve(3022, 'ua-writer', `${__dirname}/../built/ua-writer`)
serve(3031, 'spike', `${__dirname}/../src/spike/static-stuff`)


function serve(port, name, dir) {
    const app = express()
    app.use(express.static(dir))

    app.listen(port, _=> {
        clog(`Serving ${name} on 127.0.0.1:${port}`)
    })
}


