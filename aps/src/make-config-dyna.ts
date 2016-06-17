import * as webdriverio from 'webdriverio'
import static 'into-u'

let wio

export async function onKey(key, {buildStaticSites}) {
    if (key === '0') {
        if (wio) {
            wio.end()
        }
        
        await buildStaticSites()
        
        const url = 'http://127.0.0.1:3001'
        dlog('Now running browser on ' + url)
        wio = webdriverio
            .remote({
                desiredCapabilities: {
                    browserName: 'chrome'
                }
            })
            .init()
            .windowHandleMaximize()
            .url(url)
    }
}

export function dispose() {
    // clog('Disposing dynamic shit')
    wio.end()
}
