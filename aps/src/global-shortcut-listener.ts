/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import * as electron from 'electron'
import * as net from 'net'
import static 'into-u'

electron.app.on('ready', _=> {
    for (const key of '0987'.split('')) {
        registerShortcut('CmdOrCtrl+Alt+' + key, _=> {
            clog('Sending key ' + key + '...')
            const socket = new net.Socket
            socket.connect({host: '127.0.0.1', port: parseInt(process.argv[2], 10)}, _=> {
                socket.write(key, 'utf8', _=> socket.end())
            })
        })
    }
    
    clog('Waiting for keys...')
    
    
    function registerShortcut(key, f) {
        if (!electron.globalShortcut.register(key, f)) {
            clog('Failed to register shortcut', key)
            process.exit(1)
        }
    }
})

