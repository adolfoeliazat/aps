/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

const webdriverio = require('webdriverio')
 
webdriverio
    .remote({
        desiredCapabilities: {
            browserName: 'chrome'
        }
    })
    .init()
    .url('http://127.0.0.1:3001')
    .getTitle().then(function(title) {
        console.log('Title was: ' + title)
    })
    .windowHandleMaximize()
