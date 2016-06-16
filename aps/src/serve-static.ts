/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import express = require('express')
import static 'into-u'

serve(3001, 'ua-customer')
serve(3002, 'ua-writer')


function serve(port, dir) {
    const app = express()
    app.use(express.static(`${__dirname}/../built/${dir}`))

    app.listen(port, _=> {
        clog(`Serving ${dir} on 127.0.0.1:${port}`)
    })
}


