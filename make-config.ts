/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

const {spawn} = require('child_process')
const webdriverio = require(`${process.cwd()}/node_modules/webdriverio`)
import static 'into-u'

let runnerProcess, wio

makeConfig = {
    modules: [
        {
            name: 'aps',
            async afterFirstCompile() {
                await buildStaticSites()
            },
            watch() {
                doDyingNoisa(async function() {
                    const port = 3901
                    const server = new net.Server
                    server.on('connection', socket => {
                        socket.on('data', async function(data) {
                            data = data.toString()
                            if (data === '0') {
                                if (wio) {
                                    wio.end()
                                }
                                
                                await buildStaticSites()
                                
                                //### URL FOR KEY 0: http://127.0.0.1:3001
                                const mySource = fs.readFileSync(__filename.replace(/(\\|\/)lib(\\|\/)/, '/src/'), 'utf8')
                                const marker = '//### URL FOR KEY 0: '
                                const fromIndex = mySource.indexOf(marker) + marker.length
                                const toIndex = mySource.indexOf('\n', fromIndex)
                                const url = mySource.slice(fromIndex, toIndex).trim()
                                
                                dlog('now running browser on ' + url)
                                wio = webdriverio
                                    .remote({
                                        desiredCapabilities: {
                                            browserName: 'chrome'
                                        }
                                    })
                                    .init()
                                    .windowHandleMaximize()
                                    .url('http://127.0.0.1:3001')
                            }
                        })
                    })
                    server.on('listening', _=> {
                        clog('Listening to global shortcut messages on ' + port)
                        spawn(process.env.ELECTRON, ['aps/lib/global-shortcut-listener', port], {stdio: 'inherit'})
                    })
                    server.listen({port})
                })
                
//                chokidar.watch(['aps/lib/make-static-sites.js'], {ignoreInitial: true})
//                        .on('all', _=> buildStaticSites())
            },
        }
    ]
}

// @extract-candidate Running external build tool, collecting its output, sending build status to DevUI
function buildStaticSites() {
    return new Promise((resolve, reject) => {
        const makeResult = {maker: 'Static Sites', stdall: ''}
        
        clog('Building static sites...')
        const m = beginMeasure()
        const child = sh.exec('node aps/lib/make-static-sites', {silent: true}, code => {
            const elapsedString = m.end()
            makeResult.code = code
            if (code === 0) {
                clog('Built static sites ' + okWithTimeLabel(elapsedString))
                resolve()
            } else {
                clog('Forget about static sites ' + failedLabel())
                reject(Error(`Static site maker failed with code ${code}`))
            }
            
            const i = findIndex(makeResults, x => x.maker === makeResult.maker)
            if (~i) {
                makeResults[i] = makeResult
            } else {
                makeResults.push(makeResult)
            }
            sendBuildStatus()
        })
        child.stdout.on('data', data => {
            makeResult.stdall += data
            process.stdout.write(data)
        })
        child.stderr.on('data', data => {
            makeResult.stdall += data
            process.stderr.write(data)
        })
    })
}


