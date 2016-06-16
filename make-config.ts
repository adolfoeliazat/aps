/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import static 'into-u'

makeConfig = {
    modules: [
        {
            name: 'aps',
            async afterFirstCompile() {
                await buildStaticSites()
            },
            watch() {
                chokidar.watch(['aps/lib/make-static-sites.js'], {ignoreInitial: true})
                        .on('all', _=> buildStaticSites())
            },
        }
    ]
}

// @possible-should-extract Running external build tool, collecting its output, sending build status to DevUI
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


