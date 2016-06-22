/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

const {spawn} = require('child_process')
import static 'into-u'

let dyna

makeConfig = {
    
    requireSourceMapSupport({file}) {
        return ~['aps/src/make-static-sites.ts',
                 'aps/src/global-shortcut-listener.ts'].indexOf(file)
    },
    
    requireRegeneratorRuntime(...args) {
        return makeConfig.requireSourceMapSupport(...args)
    },
    
    modules: [
        {
            name: 'aps',
            browserify: 'true',
            browserifyEntry: 'client.js',
            onBrowserifyBundle() {
                for (const dir of tokens('en-customer ua-customer')) {
                    const target = `aps/built/${dir}`
                    if (pathExists.sync(target)) {
                        sh.cp('aps/lib/bundle.js', target)
                        dlog('Copied bundle to ' + target)
                    }
                }
            },
            
            async afterFirstCompile() {
                await buildStaticSites()
            },
            
            watch() {
                doDyingNoisa(async function() {
                    const port = 3901
                    const server = new net.Server
                    server.on('connection', socket => {
                        socket.on('data', async function(data) {
                            try {
                                data = data.toString()
                                const dynaModulePath = origRequire.resolve(process.cwd() + '/aps/lib/make-config-dyna')
                                if (dyna) {
                                    dyna.dispose()
                                    delete origRequire.cache[dynaModulePath]
                                }
                                dyna = origRequire(dynaModulePath)
                                await dyna.onKey(data, {buildStaticSites})
                                
                            } catch (e) {
                                console.error(e)
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
        const commandResult = {command: 'make-static-sites', stdall: ''}
        
        clog('Building static sites...')
        const m = beginMeasure()
        const child = sh.exec('node aps/lib/make-static-sites', {silent: true}, code => {
            const elapsedString = m.end()
            commandResult.code = code
            if (code === 0) {
                clog('Built static sites ' + okWithTimeLabel(elapsedString))
                resolve()
            } else {
                clog('Forget about static sites ' + failedLabel())
                reject(Error(`Static site maker failed with code ${code}`))
            }
            
            sendBuildStatus({commandResult})
        })
        child.stdout.on('data', data => {
            commandResult.stdall += data
            process.stdout.write(data)
        })
        child.stderr.on('data', data => {
            commandResult.stdall += data
            process.stderr.write(data)
        })
    })
}


