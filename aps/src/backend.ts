/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

#pragma instrument-ui-rendering

MODE = 'debug'
MORE_CHUNK = 10 + 1
MAX_DISPLAYED_NEW_MESSAGES_IN_UPDATED_SUPPORT_THREAD = 2
THE_ADMIN_ID = 101

PG_MAX_BIGINT = '9223372036854775807'

require('regenerator-runtime/runtime')
require('source-map-support').install()

import * as fs from 'fs'
import * as path from 'path'
import sh = require('shelljs')

// @wip kotlin
global.kotlin = require('E:/work/aps/aps/built/ua-writer/kotlin/lib/kotlin.js')
require('E:/work/aps/aps/built/ua-writer/kotlin/front.js')

// @wip scala
// require('E:/work/aps/aps/built/ua-writer/kotlin/out/production/aps-kotlin/aps-kotlin.js')

import * as testShitUA from './test-shit-ua'
import {apsdata, setCommonT} from './common'
#import static 'into-u ./stuff'

const backendInstanceID = uuid()
let testGlobalCounter = 0, simulateRequest

// TODO:vgrechka @ugly Find a better way to make TS aware of `#imported static` names
// that collide with something in the file
let last

const app = newExpress()
let mailTransport, sentEmails = [], fixedNextGeneratedPassword, queryLogForUI = [], requestLogForUI = [], imposedRequestTimestamp,
    imposedNextIDs = [], requestTimeLoggingDisabled
    
require('pg').types.setTypeParser(1114, s => { // timestamp without timezone
    return s
})

const redis = run(_=> {
    const client = require('redis').createClient()
    
    client.on('error', err => {
        logPizdets({title: 'Redis shitted something at us...', message: err})
    })
    
    const me = {}
    
    for (const command of tokens('get set lpush ltrim lrange')) {
        me[command] = /*async*/ function(...args) {
            return new Promise((resolve, reject) => {
                client[command](...args, (err, res) => {
                    if (err) {
                        reject(err)
                    } else {
                        resolve(res)
                    }
                })
            })
        }
    }
    
    return me
})

                    
app.post('/rpc', async function(req, res) {
    try {
        
    simulateRequest = async function(msg) {
        const res = await handle(msg)
        if (!res.hunky) raise(`Unhunky response from myself’s ${msg.fun}: ${deepInspect(res)}`)
        return res
    }
    
    // dlog({body: req.body, headers: req.headers})
    const mt = measureTime({name: `/rpc ${req.body.fun}`, log: false && typeof req.body.fun === 'string' && !req.body.fun.startsWith('danger_')})
    const message = await handle(req.body)
    mt.point({name: 'handle'})
    ripOffMeta(message)
    mt.point({name: 'ripOffMeta'})
    
//    circularize(message) ///
//    mt.point({name: 'circularize'})
    res.json(message)
    const mtend = mt.point({name: 'done'})
    if (typeof req.body.fun === 'string' && !req.body.fun.startsWith('danger_') && req.body.fun !== 'private_getLiveStatus') {
        dlog(`${req.body.fun}: ${mtend.sinceStart}ms`)
    }
    
    function ripOffMeta(o, path='ROOT') {
        for (const [key, value] of toPairs(o)) {
            if (key === '$trace') {
                o[key] = []
                // raise('Fuck $trace: ' + path + '.' + key)
            }
            
            else if (key === '$meta') {
                if (typeof o.$meta === 'object') {
                    const id = uuid()
                    // idToMeta[id] = getCircularJSON().stringify(o.$meta)
                    
                    const cjson = getCircularJSON().stringify(o.$meta)
                    /*don't await*/ redis.set(`$meta:${id}`, cjson, 'ex', 60*10)
                    
                    delete o.$meta
                    o.$metaID = id
                } else {
                    raise('Shit happened 82adf78f-894b-4b5f-b047-1d72fb124490')
                }
            }
            
            else {
                if (isObject(value)) {
                    ripOffMeta(value, path + '.' + key)
                }
            }
        }
    }
    
    function circularize(o) {
        raise('do not use me')
        for (const [key, value] of toPairs(o)) {
            if ((key === '$trace' || key === '$meta') && typeof value === 'object') { // Check type to not stringify string on subsequent requests
                o[key] = getCircularJSON().stringify(value)
            } else {
                if (isObject(value)) {
                    circularize(value)
                }
            }
        }
    }
    
    let rejectAllFreakingRequests
    
    async function handle(msg) {
        if (rejectAllFreakingRequests) raise('Fuck you. I mean nothing personal, I do this to everyone...')
        
        const $traceStack = []
        const $trace = []
        
        let requestTimestamp = moment.tz('UTC').format('YYYY-MM-DD HH:mm:ss.SSSSSS')
        if (imposedRequestTimestamp && shouldUseImposedRequestTimestamp()) {
            requestTimestamp = imposedRequestTimestamp
            imposedRequestTimestamp = undefined
        }
        function shouldUseImposedRequestTimestamp() {
            return !msg.fun.startsWith('danger_')
        }
        
        const _t = makeT(msg.LANG)
        function t($meta, ...args) {
            return {$meta, meat: _t(...args)}
        }
        setCommonT(t)
        
        let stackBeforeAwait, awaitRes
        
        try {
            if (!msg.fun) raise('Gimme msg.fun, please, fuck you')
        
            // dlog('mmm', msg)
            if (msg.fun.startsWith('danger_')) {
                const serverToken = process.env.APS_DANGEROUS_TOKEN
                if (!serverToken) raise('I want APS_DANGEROUS_TOKEN configured on server')
                const clientToken = msg.DANGEROUS_TOKEN
                if (clientToken !== serverToken) raise('Fuck you, mister hacker')
            }
            
            // - Application can be exercised automatically againts test datbase
            // - Application can be exercised manually againts test datbase
            //   (after test finished, user can continue to do stuff in that tab)
            // - Application can be exercised manually againts dev datbase
            //   (if no tests were executed after browser refresh)
            // - Application can run against production database configured in APS_DB_xxx variables
            //   (if backend's MODE is 'prod')
            // - Backend does not necessarily connect to database for all requests,
            //   e.g. danger_eval might not need it. For those requests where connection is necessary
            //   everything should always be wrapped in start transaction/commit/rollback.
            
            const [clientDomain, clientPortSuffix] = {
                ua_customer: [DOMAIN_UA_CUSTOMER, PORT_SUFFIX_UA_CUSTOMER],
                ua_writer: [DOMAIN_UA_WRITER, PORT_SUFFIX_UA_WRITER],
                en_customer: [DOMAIN_EN_CUSTOMER, PORT_SUFFIX_EN_CUSTOMER],
                en_writer: [DOMAIN_EN_WRITER, PORT_SUFFIX_EN_WRITER],
            }[msg.LANG + '_' + msg.clientKind] || [undefined, undefined]
            if (!clientDomain && msg.clientKind !== 'devenv' && msg.clientKind !== 'debug') raise('WTF is the clientKind?')
            
            if (msg.db) {
                // dlog(`Begin transaction: fun=${msg.fun} db=${msg.db}`)
                const res = await pgTransaction({db: msg.db}, doStuff)
                // dlog(`End transaction: fun=${msg.fun} db=${msg.db}`)
                return res
            } else {
                return await doStuff()
            }
            
            async function doStuff(tx) {
                const fieldErrors = {}, fields = {}
                let user
            
                if (msg.fun.startsWith('private_')) {
                    #await loadUserForToken(s{})
                }
                
                if (msg.fun === 'danger_eval') {
                    try {
                        const lines = msg.text.split('\n')
                        let offsetLine = 0
                        for (let i = 0; i < msg.text.length; ++i) {
                            if (i === msg.offset) break
                            if (msg.text[i] === '\n') ++offsetLine
                        }
                        
                        if (!lines[offsetLine].trim().length) return {err: 'Put the damn caret where some text is'}
                        
                        let fromLine = offsetLine
                        for (;;) {
                            if (fromLine === 0 || !lines[fromLine - 1].trim().length) break
                            --fromLine
                        }
                        let toLine = offsetLine
                        for (;;) {
                            if (toLine === lines.length - 1 || !lines[toLine + 1].trim().length) break
                            ++toLine
                        }
                        
                        const compiler = require(process.env.FOUNDATION_HOME + '/maker/compile.js')
                        let pieceOfCode = lines.slice(fromLine, toLine + 1).join('\n')
                        pieceOfCode = `(async function() { ${pieceOfCode}\n })()`
                        pieceOfCode = compiler.preDecorateJSLikeCode(pieceOfCode)
                        pieceOfCode = compiler.compileTS({fileName: 'snippet.ts', code: pieceOfCode})
                        pieceOfCode = compiler.postDecorateJSLikeCode(pieceOfCode)
                        
                        let log = ''
                        await eval(pieceOfCode)
                        
                        return {res: log}
                        
                        
                        function relog(...args) {
                            log += args.map(x => {
                                if (typeof x === 'string') return x
                                return deepInspect(x)
                            }).join(' ') + '\n'
                        }
                    } catch (e) {
                        return {fatal: e.stack}
                    }
                }
                
                if (msg.fun === 'danger_getMetaByID') {
//                    let cjson
//                    if (typeof meta === 'string') {
//                        cjson = meta
//                    } else {
//                        cjson = idToMeta[msg.id] = getCircularJSON().stringify(meta)
//                    }
                    
                    const cjson = await redis.get(`$meta:${msg.id}`)
                    if (!cjson) return {error: t(`No meta for ID ${msg.id}`)}
                    return hunkyDory({cjson})
                }
                
                if (msg.fun === 'danger_getSoftwareVersion') {
                    const bundleFile = 'E:/work/aps/aps/built/ua-writer/bundle.js'
                    let bundleCtime
                    try {
                        const st = fs.statSync(bundleFile)
                        bundleCtime = st.ctime.getTime()
                    } catch (e) {
                        // Won't include bundleCtime in response
                    }
                    
                    return hunkyDory({backendInstanceID, bundleCtime})
                }
                
                if (msg.fun === 'danger_getHotShitCodeEntries') {
                    const items = []
                    
                    for (const fname of ['E:/work/foundation/u/lib/ui.js', 'E:/work/aps/aps/lib/client.js', 'E:/work/aps/aps/lib/common.js']) { // @ctx hot shit
                        const fileCode = fs.readFileSync(fname, 'utf8')
                        const beginRe = /'\$\$\$hot-shit-begin-(.*?)'/g
                        let match
                        while (match = beginRe.exec(fileCode)) {
                            const hotShitTag = match[1]
                            const beginIndex = match.index
                            const endIndex = fileCode.indexOf(`'$$$hot-shit-end-${hotShitTag}'`)
                            if (!~endIndex) raise(`No hot shit ending marker found for tag [${hotShitTag}]`)
                            
                            let code = fileCode.slice(beginIndex, endIndex)
                            code = code.slice(code.indexOf('function'), /*{*/ code.lastIndexOf('}') + 1)
                            items.push({hotShitTag, code})
                        }
                    }
                    
                    return hunkyDory({items})
                }
                
                if (msg.fun === 'danger_getPieceOfCodeBetweenTags') {
                    if (!msg.tag) raise('Gimme tag')
                    if (!msg.tagPrefix) raise('Gimme tagPrefix')
                    
                    let code, beginIndex, endIndex
                    const fnames = ['E:/work/foundation/u/lib/ui.js', 'E:/work/aps/aps/lib/client.js']
                    for (const fname of fnames) {
                        code = fs.readFileSync(fname, 'utf8')
                        const beginMarker = `'${msg.tagPrefix}begin-${msg.tag}'`
                        beginIndex = code.indexOf(beginMarker)
                        endIndex = code.indexOf(`'${msg.tagPrefix}end-${msg.tag}'`)
                        if (~beginIndex && ~endIndex) break
                    }
                    if (!(~beginIndex && ~endIndex)) return hunkyDory({})
                    
                    let piece = code.slice(beginIndex, endIndex)
                    piece = piece.slice(piece.indexOf('function'), /*{*/ piece.lastIndexOf('}') + 1)
                    return hunkyDory({piece})
                }
                
                if (msg.fun === 'danger_saveWorldPoint' || msg.fun === 'danger_restoreWorldPoint') {
                    // @wip worldPoint
                    rejectAllFreakingRequests = true
                    try {
//                        await pgConnection({db: 'test-postgres'}, async function(db) {
//                            await db.query(s{y: q`
//                                SELECT pg_terminate_backend(pg_stat_activity.pid)
//                                FROM pg_stat_activity
//                                WHERE pg_stat_activity.datname = '"aps-test"' AND pid <> pg_backend_pid();
//                            `})
//                        })
                        await shutDownPool('aps-test')
                        await pgConnection({db: 'test-postgres'}, async function(db) {
                            const wpdbname = `world point ${msg.wpname}`
                            let databaseToCreate, databaseToUseAsTemplate
                            
                            if (msg.fun === 'danger_saveWorldPoint') {
                                databaseToCreate = wpdbname
                                databaseToUseAsTemplate = 'aps-test'
                            }
                            else if (msg.fun === 'danger_restoreWorldPoint') {
                                databaseToCreate = 'aps-test'
                                databaseToUseAsTemplate = wpdbname
                            }
                            
                            await db.query(s{y: q`drop database if exists "${{inline: databaseToCreate}}"`})
                            await db.query(s{y: q`create database "${{inline: databaseToCreate}}" template = "${{inline: databaseToUseAsTemplate}}"`})
                        })
                    } finally {
                        rejectAllFreakingRequests = false
                    }
                    
                    return hunkyDory()
                }
                
//                if (msg.fun === 'danger_captureTestDB_killme') {
//                    const condb = 'test-postgres'
//                    const sourceDBName = 'aps-test'
//                    
//                    await shutDownPool(sourceDBName)
//                    await shutDownPool(msg.newTemplateDBName)
//                    await pgConnection({db: condb}, async function(db) {
//                        await db.query({$tag: 'eba1bdcf-9657-405d-9716-1dbc3c01a65b', y: `drop database if exists "${msg.newTemplateDBName}"`})
//                        await db.query({$tag: 'f31f0e3c-ef04-4391-b5a1-dc489fa4fa9b', y: `create database "${msg.newTemplateDBName}" template = "${sourceDBName}"`})
//                    })
//                    
//                    return hunkyDory({shortMessage: `Captured as ${msg.newTemplateDBName}`})
//                }
                
                if (msg.fun === 'danger_updateAssertionCode') {
                    const ft = findTagInSourceCode(msg.assertionTag)
                    if (!ft) return {error: t('Tag is not found in code')}
                    
                    const bakFile = `c:/tmp/aps-bak/${path.basename(ft.file)}.bak-${moment().format('YYYYMMDD-HHmmss')}`
                    fs.writeFileSync(bakFile, ft.code)
                    
                    const beginningLineOfInlineContent = `${'s'}{assert: {$tag: '${msg.assertionTag}', expected: {` // }}}
//                    const beginningLineOfInlineContent = `art.uiState({$tag: '${msg.assertionTag}', expected: {` // }})
                    const beginningLineOfInlineContentIndex = ft.code.indexOf(beginningLineOfInlineContent)
                    if (~beginningLineOfInlineContentIndex) {
                        let indent = 0, i = beginningLineOfInlineContentIndex - 1
                        while (ft.code[i] === ' ') {
                            ++indent
                            --i
                        }
                        
                        /*{{{*/ let endingLineContentIndex = ft.code.slice(beginningLineOfInlineContentIndex).search(/\s*\}\}\},/)
                        if (!~endingLineContentIndex) return {error: t('Cannot find endingLineContentIndex')}
                        endingLineContentIndex += beginningLineOfInlineContentIndex
                        const newCode = ft.code.slice(0, beginningLineOfInlineContentIndex + beginningLineOfInlineContent.length)
                                      + ('\n' + trimEnd(msg.actualStringForPasting)).replace(/\n/g, '\n' + repeat(' ', indent + 4)) + '\n'
                                      + repeat(' ', indent) + trimStart(ft.code.slice(endingLineContentIndex))
                        
                        // fs.writeFileSync('c:/tmp/shit.txt', newCode)
                        fs.writeFileSync(ft.file, newCode)
                        
                        return hunkyDory()
                    }
                    
                    const lineOfExternalContentReference = `${'s'}{assert: {$tag: '${msg.assertionTag}', expected: '---generated-shit---'` /*}}*/
//                    const lineOfExternalContentReference = `art.uiState({$tag: '${msg.assertionTag}', expected: '---generated-shit---'})`
                    dlogs({lineOfExternalContentReference})
                    const lineOfExternalContentReferenceIndex = ft.code.indexOf(lineOfExternalContentReference)
                    if (!~lineOfExternalContentReferenceIndex) return {error: t('No lineOfExternalContentReferenceIndex')} 
                    
                    const generatedShitFile = 'E:/work/aps/aps/src/generated-shit.ts'
                    const generatedShitCode = fs.readFileSync(generatedShitFile, 'utf8')
                    
                    const beginningLineOfExternalContent = `'${msg.assertionTag}': {` // }
                    const beginningLineOfExternalContentIndex = generatedShitCode.indexOf(beginningLineOfExternalContent)
                    if (~beginningLineOfExternalContentIndex) {
                        let indent = 0, i = beginningLineOfExternalContentIndex - 1
                        while (generatedShitCode[i] === ' ') {
                            ++indent
                            --i
                        }
                        
                        /*{*/ let endingLineOfExternalContentIndex = generatedShitCode.slice(beginningLineOfExternalContentIndex).search(/\}, \/\/ end of generated piece of shit/)
                        if (!~endingLineOfExternalContentIndex) return {error: t('Cannot find endingLineOfExternalContentIndex')}
                        endingLineOfExternalContentIndex += beginningLineOfExternalContentIndex
                        const newCode = generatedShitCode.slice(0, beginningLineOfExternalContentIndex + beginningLineOfExternalContent.length)
                                      + ('\n' + trimEnd(msg.actualStringForPasting)).replace(/\n/g, '\n' + repeat(' ', indent + 4)) + '\n'
                                      + repeat(' ', indent) + trimStart(generatedShitCode.slice(endingLineOfExternalContentIndex))
                        
                        // fs.writeFileSync('c:/tmp/shit.txt', newCode)
                        fs.writeFileSync(generatedShitFile, newCode)
                        
                        return hunkyDory()
                    } else {
                        const placeToInsertNewGeneratedShitLine = `// place to insert new generated piece of shit`
                        const placeToInsertNewGeneratedShitLineIndex = generatedShitCode.indexOf(placeToInsertNewGeneratedShitLine)
                        if (!~placeToInsertNewGeneratedShitLineIndex) return {error: t('Cannot find placeToInsertNewGeneratedShitLineIndex')}
                        let indent = 0, i = placeToInsertNewGeneratedShitLineIndex - 1
                        while (generatedShitCode[i] === ' ') {
                            ++indent
                            --i
                        }
                        
                        const newCode = generatedShitCode.slice(0, placeToInsertNewGeneratedShitLineIndex)
                                      + `'${msg.assertionTag}': {` // }
                                      + ('\n' + trimEnd(msg.actualStringForPasting)).replace(/\n/g, '\n' + repeat(' ', indent + 4)) + '\n'
                                      + repeat(' ', indent) + /*{*/ '}, // end of generated piece of shit'
                                      + '\n\n' + repeat(' ', indent) + placeToInsertNewGeneratedShitLine
                                      + generatedShitCode.slice(placeToInsertNewGeneratedShitLineIndex + placeToInsertNewGeneratedShitLine.length)
                        
                        // fs.writeFileSync('c:/tmp/shit.txt', newCode)
                        fs.writeFileSync(generatedShitFile, newCode)
                        
                        return hunkyDory()
                    }
                
                    return {error: t('WTF I’ve reached here')}
                }
                
                if (msg.fun === 'danger_insertTestActionCode') {
                    const ft = findTagInSourceCode(msg.placeholderTag)
                    if (!ft) return {error: 'Tag is not found in code'}
                    
                    const bakFile = `c:/tmp/aps-bak/${path.basename(ft.file)}.bak-${moment().format('YYYYMMDD-HHmmss')}`
                    fs.writeFileSync(bakFile, ft.code)
                    
                    const beginningLineContent = `${'s'}{actionPlaceholder: {$tag: '${msg.placeholderTag}'}},`
//                    const beginningLineContent = `art.actionPlaceholder({$tag: '${msg.placeholderTag}'})`
                    const beginningLineContentIndex = ft.code.indexOf(beginningLineContent)
                    if (!~beginningLineContentIndex) return {error: 'Cannot find beginningLineContentIndex'}
                    
                    let indent = 0, i = beginningLineContentIndex - 1
                    while (ft.code[i] === ' ') {
                        ++indent
                        --i
                    }
                    
                    const newCode = ft.code.slice(0, beginningLineContentIndex)
                                  + trimStart(('\n' + trimEnd(msg.code)).replace(/\n/g, '\n' + repeat(' ', indent))) + '\n'
                                  + '\n' + repeat(' ', indent) + beginningLineContent
                                  + ft.code.slice(beginningLineContentIndex + beginningLineContent.length)
                    
                    // fs.writeFileSync('c:/tmp/shit.txt', newCode)
                    fs.writeFileSync(ft.file, newCode)
                        
                    return hunkyDory()
                }
                
                if (msg.fun === 'danger_clearSentEmails') {
                    sentEmails = []
                    return hunkyDory()
                }
                
                if (msg.fun === 'danger_imposeNextRequestTimestamp') {
                    imposedRequestTimestamp = msg.timestamp
                    return hunkyDory()
                }
                
                if (msg.fun === 'danger_imposeNextID') {
                    imposedNextIDs = [msg.id]
                    return hunkyDory()
                }
                
                if (msg.fun === 'danger_getSentEmails') {
                    return sentEmails
                }
                
                if (msg.fun === 'danger_killUser') {
                    #await dangerouslyKillUser({email: msg.email})
                    return hunkyDory()
                }
                
                if (msg.fun === 'danger_killSupportThreads') {
                    #await tx.query({$tag: '0b66e201-0353-4d93-9d68-58932de199ca', y: q`
                        delete from support_thread_messages`})
                    #await tx.query({$tag: '4aa830b9-a217-4515-bff7-353baa0eba62', y: q`
                        delete from support_threads`})
                    return hunkyDory()
                }
                
                if (msg.fun === 'danger_insert') {
                    #await insertInto({$tag: '1454b421-bc76-4abd-8eea-0ec8e8ecf0a3', table: msg.table, rows: msg.rows})
                    return hunkyDory()
                }
                
                if (msg.fun === 'danger_imposeNextGeneratedPassword') {
                    fixedNextGeneratedPassword = msg.password
                    return hunkyDory()
                }
                
                if (msg.fun === 'danger_openSourceCode') {
                    let file, line, column, offset, ide
                    if (msg.tag) {
                        const ft = findTagInSourceCode(msg.tag)
                        if (!ft) return {error: 'Tag is not found in code'}
                        ;({file, offset} = ft)
                    } else if (msg.sourceLocation) {
                        // Source location example: aps/src/backend.ts[7556]:181:35
                        dlog(1111111, msg.sourceLocation)
                        
                        const openBracket = msg.sourceLocation.indexOf('[' /*]*/)
                        const closingBracket = msg.sourceLocation.indexOf(/*[*/ ']')
                        const firstColon = msg.sourceLocation.indexOf(':')
                        const secondColon = msg.sourceLocation.indexOf(':', firstColon + 1)
                        let filePartEnd
                        if (~openBracket && ~closingBracket) {
                            filePartEnd = openBracket
                        } else if (~firstColon && ~secondColon) {
                            filePartEnd = firstColon
                        } else {
                            return {error: 'I want either brackets or two colons in source location'}
                        }
                        const filePart = msg.sourceLocation.slice(0, filePartEnd)
                        file = {
                            'aps/src/common.ts': 'E:/work/aps/aps/src/common.ts',
                            'common.ts': 'E:/work/aps/aps/src/common.ts',
                            'aps/src/client.ts': 'E:/work/aps/aps/src/client.ts',
                            'client.ts': 'E:/work/aps/aps/src/client.ts',
                            'aps/src/test-admin-ua.ts': 'E:/work/aps/aps/src/test-admin-ua.ts',
                            'test-admin-ua.ts': 'E:/work/aps/aps/src/test-admin-ua.ts',
                            'aps/src/test-writer-ua.ts': 'E:/work/aps/aps/src/test-writer-ua.ts',
                            'test-writer-ua.ts': 'E:/work/aps/aps/src/test-writer-ua.ts',
                            'aps/src/backend.ts': 'E:/work/aps/aps/src/backend.ts',
                            'backend.ts': 'E:/work/aps/aps/src/backend.ts',
                            'ui.ts': 'E:/work/foundation/u/src/ui.ts',
                            'u/src/ui.ts': 'E:/work/foundation/u/src/ui.ts',
                            'test-shit-ua.ts': 'E:/work/aps/aps/src/test-shit-ua.ts',
                            'front.kt': 'E:\\work\\aps\\front\\src\\front.kt',
                        }[filePart]
                        if (!file) return {error: `Weird file in source location: [${filePart}]`}
                        
                        if (~filePart.indexOf('.kt')) ide = 'idea'
                        else ide = 'eclipse'
                        
                        if (ide === 'idea') {
                            line = parseInt(msg.sourceLocation.slice(firstColon + 1, secondColon), 10)
                            column = parseInt(msg.sourceLocation.slice(secondColon + 1), 10)
                        }
                        else if (ide === 'eclipse') {
                            if (~openBracket && ~closingBracket) {
                                offset = parseInt(msg.sourceLocation.slice(openBracket + 1, closingBracket))
                            } else if (~firstColon && ~secondColon) {
                                line = parseInt(msg.sourceLocation.slice(firstColon + 1, secondColon), 10) - 1
                                column = parseInt(msg.sourceLocation.slice(secondColon + 1), 10) - 1
                                const code = fs.readFileSync(file, 'utf8')
                                let currentLine = 0, currentColumn = 0, feasibleLineStartOffset
                                offset = 0
                                while (offset < code.length) {
                                    if (currentLine === line) {
                                        if (feasibleLineStartOffset === undefined) {
                                            feasibleLineStartOffset = offset
                                        }
                                        if (currentColumn === column) break
                                    }
                                    if (code[offset] === '\r' && code[offset + 1] === '\n') {
                                        if (feasibleLineStartOffset !== undefined) { // Likely, column was mangled by code generation
                                            offset = feasibleLineStartOffset
                                            break
                                        }
                                        offset += 2
                                        currentLine += 1
                                        currentColumn = 0
                                    } else if (code[offset] === '\n') {
                                        offset += 1
                                        currentLine += 1
                                        currentColumn = 0
                                    } else {
                                        offset += 1
                                        currentColumn += 1
                                    }
                                }
                            } else {
                                raise('Weird line/column/offset')
                            }
                        }
                    } else {
                        dlog('Message for weird source location descriptor', msg)
                        raise('Weird source location descriptor')
                    }
                    
                    if (ide === 'idea') {
                        const command = `C:\\opt\\idea-ce\\bin\\idea64.exe e:\\work\\aps --line ${line} ${file}`
                        dlog('Executing external command', command)
                        const child = sh.exec(command, {silent: false}, code => {
                            dlog('External command finished with code', code)
                        })
                    }
                    else if (ide === 'eclipse') {
                        const res = await RPCClient({url: 'http://127.0.0.1:4001/rpc'}).call({fun: 'openEditor', file, offset})
                    }
                    else {
                        raise('WTF is IDE')
                    }
                    return hunkyDory()
                }
                
                if (msg.fun === 'danger_getRequests') {
                    let count = msg.count || 1
                    return #await redis.lrange('requests', 0, count - 1)
                }
                
                if (msg.fun === 'danger_getQueries') {
                    let last = msg.last || 1
                    return queryLogForUI.slice(queryLogForUI.length - last)
                }
                
                if (msg.fun === 'danger_resetTestDatabase') {
                    if (msg.alsoRecreateTemplate) {
                        if (msg.templateDB === 'test-template-ua-1') {
                            testShitUA.setBackendContext({simulateRequest})
                            #await testShitUA.createTestTemplateUA1DB()
                        } else {
                            raise(`I don’t know how to recreate test template DB for ${msg.templateDB}`)
                        }
                    }
                    
                    #await shutDownPool('aps-test')
                    #await shutDownPool(msg.templateDB)
                    await pgConnection({db: 'test-postgres'}, doInTestDB)
                    
                    async function doInTestDB(db) {
                        #await db.query({$tag: '8dcb544c-1337-4298-8970-21466bad7c4c', y: `drop database if exists "aps-test"`})
                        #await db.query({$tag: '9b569e3e-53b7-4296-97d2-d1ce2a1684b4', y: `create database "aps-test" template = "${msg.templateDB}"`})
                    }
                    
                    return hunkyDory()
                }
                
                
                if (msg.fun === 'signInWithPassword') {
                    traceBeginHandler({$tag: '29f48649-bea3-4777-a2cc-e40a98590804'})
                    const rows = #await tx.query({$tag: '4281bc8b-7f86-4a7a-86e6-bb4d7587b654', y: q`
                        select * from users where email = ${msg.email}`})
                    const invalidEmailOrPasswordRet = {error: t('Invalid email or password', 'Неверная почта или пароль')}
                    if (!rows.length) {
                        clog('Sign-in failed: non-existing email')
                        return traceEndHandler({ret: invalidEmailOrPasswordRet, $tag: '486b5752-1005-412a-8616-1fbb2e7fadfb'})
                    }
                    user = rows[0]
                    if (!(await comparePassword(msg.password, user.password_hash))) {
                        clog('Sign-in failed: Invalid password for existing email')
                        return traceEndHandler({ret: invalidEmailOrPasswordRet, $tag: 'cb9f9ad6-dfc3-4b7b-b405-2b43703464cc'})
                    }
                    
                    failOnClientUserMismatch()
                    
                    const token = uuid()
                    #await tx.query(s{$tag: 'e8ccf032-2c17-4a98-8666-cd18f82326c7', y: q`
                        insert into user_tokens(user_id, token) values(${user.id}, ${token})`})

                    #await loadUserData()
                    return traceEndHandler({ret: hunkyDory({user: pickFromUser(s{user}), token}), $tag: '8f0148c4-1020-4ce5-b0c9-4f71777bdd6f'})
                }
                
                if (msg.fun === 'private_getUserInfo') {
                    return hunkyDory({user: pickFromUser(s{user})})
                }
                
                if (msg.fun === 'private_getLiveStatus') {
                    const res = {}
                    
                    if (user.kind === 'admin') {
                        let heapSize = 0
                        let unassignedSupportThreadCount = parseInt(#await tx.query({$tag: '0c955700-5ef5-4803-bc5f-307be0380259', y: q`
                            select count(*) from support_threads where supporter_id is null`})[0].count, 10)
                        heapSize += unassignedSupportThreadCount
                        
                        if (unassignedSupportThreadCount) res.unassignedSupportThreadCount = t(''+unassignedSupportThreadCount)
                        if (heapSize) res.heapSize = t(''+heapSize)
                        
                        res.suka = {count: 'blia-' + puid()}
                        res.profilesToApprove = (#await tx.query(s{y: q`
                            select count(*) from users where state = 'profile-approval-pending'
                                                             and assigned_to = ${user.id}`}))[0]
                    }
                    else if (user.kind === 'writer') {
                        res.suka = {count: 'blia-' + puid()}
                    }
                    else if (user.kind === 'customer') {
                        res.suka = {count: 'blia-' + puid()}
                    }
                    
                    const unseenThreadMessageCount = parseInt(#await tx.query({$tag: 'c2a288a3-1591-42e4-a45a-c50de64c7b18', y: q`
                        select count(*) from support_thread_messages m, support_threads t
                        where (t.supporter_id = ${user.id} or t.supportee_id = ${user.id})
                              and m.thread_id = t.id
                              and m.data->'seenBy'->${user.id} is null`})[0].count, 10)
                              
                    if (unseenThreadMessageCount) {
                        if (user.kind === 'admin') {
                            const unseenThreadCount = parseInt(#await tx.query({$tag: '2404edb2-34b4-4da1-9a7e-9b4af3c0419b', y: q`
                                select count(*) from support_threads t
                                where (t.supporter_id = ${user.id} or t.supportee_id = ${user.id})
                                      and exists (select 1 from support_thread_messages m
                                                  where m.thread_id = t.id
                                                        and m.data->'seenBy'->${user.id} is null)`})[0].count, 10)
                            res.supportMenuBadge = t(`${unseenThreadMessageCount}/${unseenThreadCount}`)
                        } else {
                            res.supportMenuBadge = t(''+unseenThreadMessageCount)
                        }
                    }
                    
                    return hunkyDory(res)
                }
                
                function loadSignUpFields(def) {
                    loadField(s{key: 'email', kind: 'email', mandatory: true})
                    loadField(s{key: 'firstName', kind: 'firstName', mandatory: true})
                    loadField(s{key: 'lastName', kind: 'lastName', mandatory: true})
                }
                
                if (msg.fun === 'signUp') {
                    traceBeginHandler(s{})
                    if (!msg.agreeTerms) {
                        fieldErrors.agreeTerms = t('You have to agree with terms and conditions', 'Необходимо принять соглашение')
                    }
                    
                    loadSignUpFields(s{})
                    
                    if (isEmpty(fieldErrors)) {
                        try {
                            let password = uuid()
                            
                            if (fixedNextGeneratedPassword) {
                                password = fixedNextGeneratedPassword
                                fixedNextGeneratedPassword = undefined
                            }
                    
                            #await tx.query({$tag: 'f1030713-94b1-4626-a5ca-20d5b60fb0cb', y: q`
                                insert into users (inserted_at,         updated_at,          email,           kind,               lang,        state,                password_hash,                   first_name,          last_name)
                                            values(${requestTimestamp}, ${requestTimestamp}, ${fields.email}, ${msg.clientKind}, ${msg.LANG}, ${'profile-pending'}, ${await hashPassword(password)}, ${fields.firstName}, ${fields.lastName})`})
                            
                            const signInURL = `http://${clientDomain}${clientPortSuffix}/sign-in.html`
                                
                            let subject
                            if (msg.LANG === 'ua' && msg.clientKind === 'customer') {
                                subject = 'Пароль для APS'
                            } else if (msg.LANG === 'ua' && msg.clientKind === 'writer') {
                                subject = 'Пароль для Writer UA'
                            }
                            if (!subject) raise(`Implement mail subject for the ${clientKindDescr()}`)
                            
                            #await sendEmail({
                                to: `${fields.firstName} ${fields.lastName} <${fields.email}>`,
                                subject,
                                html: dedent(_t({
                                    en: `
                                        TODO
                                    `,
                                    ua: `
                                        Привет, ${fields.firstName}!<br><br>
                                        Вот твой пароль: ${password}
                                        <br><br>
                                        <a href="${signInURL}">${signInURL}</a>
                                    `
                                }))})
                            return traceEndHandler(s{ret: hunkyDory()})
                        } catch (e) {
                            if (e.code === '23505') {
                                fieldErrors.email = t('This email is already registered', 'Такая почта уже зарегистрирована')
                                return traceEndHandler(s{ret: fixErrorsResult()})
                            } else {
                                throw e
                            }
                        }
                    }
                    
                    return traceEndHandler(s{ret: fixErrorsResult()})
                }
                
                function loadAdminNotesField(def) {
                    loadField(s{key: 'adminNotes', mandatory: false, nullIfBlank: true, maxlen: 5000})
                }
                
                if (msg.fun === 'private_updateUser') {
                    // TODO:vgrechka Check permissions in private_updateUser    262b8f75-1c3d-479f-b5ca-5b1bdcb9ee98 
                    
                    traceBeginHandler(s{})
                    traceBeginSection(s{name: 'Load fields'})
                        loadSignUpFields(s{})
                        loadProfileFields(s{})
                        loadAdminNotesField(s{})
                        loadField(s{key: 'state', mandatory: true, allowedValues: apsdata.userStateValues()})
                        loadReasonField(s{key: 'profileRejectionReason', shouldBePresentIfKeys: 'state', valueEquals: 'profile-rejected'})
                        loadReasonField(s{key: 'banReason', shouldBePresentIfKeys: 'state', valueEquals: 'banned'})
                    traceEndSection(s{})

                    if (isEmpty(fieldErrors)) {
                        #await tx.query(s{y: q`
                            update users set 
                                updated_at = ${requestTimestamp},
                                state = ${fields.state},
                                profile_rejection_reason = ${fields.state === 'profile-rejected' ? fields.profileRejectionReason : null},
                                ban_reason = ${fields.state === 'banned' ? fields.banReason : null},
                                email = ${fields.email},
                                kind = ${msg.clientKind},
                                first_name = ${fields.firstName},
                                last_name = ${fields.lastName},
                                admin_notes = ${fields.adminNotes},
                                phone = ${fields.phone},
                                about_me = ${fields.aboutMe}
                            where id = ${msg.id}`})

                        #await loadUserForToken(s{})
                        return traceEndHandler(s{ret: hunkyDory({newUser: pickFromUser(s{user})})})
                    }
                    
                    return traceEndHandler(s{ret: fixErrorsResult()})
                }
                
                function loadProfileFields(def) {
                    loadField(s{key: 'phone', kind: 'phone', mandatory: true})
                    loadField(s{key: 'aboutMe', mandatory: true, maxlen: 300})
                }
                
                if (msg.fun === 'private_updateProfile') {
                    traceBeginHandler(s{})
                    traceBeginSection(s{name: 'Load fields'})
                        loadProfileFields(s{})
                    traceEndSection(s{})

                    if (isEmpty(fieldErrors)) {
                        #await tx.query(s{y: q`
                            update users set profile_updated_at = ${requestTimestamp},
                                             phone = ${fields.phone},
                                             compact_phone = ${compactPhone(fields.phone)},
                                             about_me = ${fields.aboutMe},
                                             state = 'profile-approval-pending',
                                             assigned_to = ${THE_ADMIN_ID}
                                   where id = ${user.id}`})
                        #await loadUserForToken(s{})
                        return traceEndHandler(s{ret: hunkyDory({newUser: pickFromUser(s{user})})})
                    }
                    return traceEndHandler(s{ret: fixErrorsResult()})
                }
                
                if (msg.fun === 'private_getSupportThreadsChunk') {
                    traceBeginHandler(s{})
                    const chunk = #await selectSupportThreadsChunk(s{filter: msg.filter})
                    return traceEndHandler(s{ret: hunkyDory(chunk)})
                }
                
                if (msg.fun === 'private_getSomeShit') {
                    traceBeginHandler(s{})
                    return traceEndHandler(s{ret: hunkyDory({shit: doGetShit(s{})})})
                    
                    function doGetShit(def) {
                        return doReallyGetShit(s{})
                        
                        function doReallyGetShit(def) {
                            return t('Here is your real shit')
                        }
                    }
                }
                
                if (msg.fun === 'private_getUser') {
                    traceBeginHandler(s{})
                    const user = #await tx.query(s{y: q`
                        select * from users where id = ${msg.id}`})[0]
                    return traceEndHandler(s{ret: hunkyDory({user: pickFromUser(s{user})})})
                }
                
                if (msg.fun === 'private_getUsers') {
                    traceBeginHandler(s{})
                    
                    let actualFilter = msg.filter
                    const saneFilters = apsdata.userFilters().map(x => x.value)
                    if (!saneFilters.includes(actualFilter)) actualFilter = defaultFilter
                        
                    let actualSearchString = getSearchStringParam()
                    
                    const chunk = #await selectChunk(s{table: 'users', loadItem,
                        appendToWhere(qb) {
                            !function considerSearchString() {
                                if (!actualSearchString) return
                                
                                if (/^p /.test(actualSearchString)) {
                                    $trace.push(s{event: '[log] Doing phone search'})
                                    const coph = compactPhone(actualSearchString)
                                    return qb.append(q`and compact_phone = ${coph}`)
                                }
                                
                                if (/^\d+$/.test(actualSearchString)) {
                                    $trace.push(s{event: '[log] Doing ID search'})
                                    return qb.append(q`and id = ${actualSearchString}`)
                                }
                                
                                $trace.push(s{event: '[log] Doing full-text search'})
                                // TODO:vgrechka Handle syntax error in tsquery    c9f27a1f-82bc-4d3c-aabc-52ea8ee6bbf7 
                                
                                // @wip admin-users
                                let ftsQueryString
                                if (/&|!|<->|\|/.test(actualSearchString)) {
                                    ftsQueryString = actualSearchString
                                } else {
                                    actualSearchString = actualSearchString.replace(/\(|\)/g, ' ').trim()
                                    ftsQueryString = actualSearchString.split(/\s+/).join(' & ')
                                }
                                
                                qb.append(q`and tsv @@ to_tsquery('russian', ${ftsQueryString})`)
                            }()
                            
                            !function considerFilter() {
                                if (actualFilter === 'all') return
                                if (actualFilter === 'cool') return qb.append(q`and state = 'cool'`)
                                if (actualFilter === '2approve') return qb.append(q`and state = 'profile-approval-pending'`)
                                if (actualFilter === 'rejected') return qb.append(q`and state = 'profile-rejected'`)
                                if (actualFilter === 'banned') return qb.append(q`and state = 'banned'`)
                                
                                raise(`Weird filter: ${actualFilter}`)
                            }()
                        },
                    })
                    
                    return traceEndHandler(s{ret: hunkyDory(asn({actualFilter, actualSearchString}, chunk))})
                    
                    async function loadItem(def) {
                        #extract {item: user} from def
                        
                        return asn({}, pickFromUser(s{user}))
                    }
                    
//                    async function selectUsersChunk(def) {
//                        #extract {filter, search} from def
//                        
//                        return #await selectChunk(s{table: 'users', loadItem,
//                            appendToWhere(qb) {
//                                const ses = getSearchStringParam()
//                                const isIDSearch = /\d+/.test(ses)
//                                if (isIDSearch) {
//                                    qb.append(q`and id = ${ses}`)
//                                }
//                            },
//                        })
//                        
//                        async function loadItem(def) {
//                            #extract {item: user} from def
//                            
//                            return asn({}, pickFromUser(s{user}))
//                        }
//                    }
                }
                
                if (msg.fun === 'private_getSupportThreads') {
                    traceBeginHandler(s{})
                    const hasUpdated = #await hasUpdatedSupportThreads()
                    const availableFilters = []
                    let filter = msg.filter
                    if (!['updatedOrAll', 'updated', 'all'].includes(filter)) filter = 'all'
                    if (hasUpdated || filter === 'updated') { // If user explicitly requests "updated", we show it even with no items
                        availableFilters.push('updated')
                    }
                    availableFilters.push('all')
                    if (filter === 'updatedOrAll') {
                        filter = hasUpdated ? 'updated' : 'all'
                    }
                    const chunk = #await selectSupportThreadsChunk(s{filter})
                    return traceEndHandler(s{ret: hunkyDory(asn({availableFilters, filter}, chunk))})
                }
                
                async function hasUpdatedSupportThreads() {
                    const qb = QueryBuilder()
                    qb.append(q`select 1 from support_threads where true`)
                    appendWhereSupportThreadIsRelatedToUser(qb)
                    appendWhereSupportThreadHasUnseenMessages(qb)
                    const rows = #await tx.query(s{y: qb.toy()})
                    return rows.length
                }
                
                function appendWhereSupportThreadIsRelatedToUser(qb) {
                    qb.append(q`and (supportee_id = ${user.id} or supporter_id = ${user.id})`)
                }
                
                function appendWhereSupportThreadHasUnseenMessages(qb) {
                    qb.append(q`and exists (select 1 from support_thread_messages
                       where thread_id = support_threads.id
                           and data->'seenBy'->${user.id} is null
                      )`)
                }
                
                async function selectSupportThreadsChunk(def) {
                    #extract {filter} from def
                    
                    return #await selectChunk(s{table: 'support_threads', loadItem,
                        appendToWhere(qb) {
                            appendWhereSupportThreadIsRelatedToUser(qb)
                            if (filter === 'updated') {
                                appendWhereSupportThreadHasUnseenMessages(qb)
                            }
                        },
                    })
                    
                    async function loadItem(def) {
                        #extract {item} from def
                        
                        return asn({}, item, {
                            newMessages: #await load({seenByUserPred: 'is null', max: MAX_DISPLAYED_NEW_MESSAGES_IN_UPDATED_SUPPORT_THREAD}),
                            oldMessages: #await load({seenByUserPred: 'is not null', max: 1}),
                        })
                        
                        async function load({seenByUserPred, max}) {
                            const total = #await tx.query({$tag: 'aab1ae48-366f-480c-aeb8-3bb7a3bd3e28', y: q`
                                select count(*) from support_thread_messages
                                where thread_id = ${item.id} and data->'seenBy'->${user.id} ${{inline: seenByUserPred}}`})[0].count
                            
                            const top = #await tx.query(s{y: q`
                                select * from support_thread_messages
                                where thread_id = ${item.id} and data->'seenBy'->${user.id} ${{inline: seenByUserPred}}
                                order by id desc
                                fetch first ${{inline: max}} rows only`})
                                
                            const loadedTop = []
                            for (const message of top) {
                                loadedTop.push(#await loadSupportThreadMessage({item: message}))
                            }
                            return {total, top: loadedTop}
                        }
                    }
                }
                
                if (msg.fun === 'private_getUnassignedSupportThreads') {
                    return #await handleChunkedSelect(s{$tag: 'b1897c92-90fb-4a04-96c6-02e15620fd4d', table: 'support_threads', loadItem, defaultOrdering: 'asc',
                        appendToWhere(qb) {
                            qb.append(q`and supporter_id is null`)
                        },
                    })
                    
                    async function loadItem(def) {
                        #extract {item} from def
                        
                        // TODO:vgrechka Limit number of messages fetched for an unassigned support thread    497d0498-2a2a-41e8-b7b3-245b99a08f3b
                        const messages = #await tx.query(s{descr: `Select messages for thread ${item.id}`, y: q`
                            select * from support_thread_messages
                            where thread_id = ${item.id}
                            order by id asc`})
                        const loadedMessages = []
                        for (const message of messages) {
                            loadedMessages.push(#await loadSupportThreadMessage({item: message}))
                        }
                        
                        const res = asn({}, item)
                        res.topicWithID = nostring({no: item.id, lang: msg.LANG}) + ' ' + item.topic
                        res.newMessages = {total: loadedMessages.length, top: loadedMessages}
                        res.oldMessages = {total: 0, top: []}
                        return res
                    }
                }
                
                if (msg.fun === 'private_getSupportThread') {
                    const entity = #await tx.query({$tag: 'f13a4eda-ab97-496d-b037-8b8f2b2599e1', y: q`
                        select * from support_threads where id = ${msg.entityID}
                    `})[0]
                    if (!entity) return {error: t(`TOTE`, `Запроса в поддержку с таким номером не существует: ${msg.entityID}`)}
                    // TODO:vgrechka Secure private_getSupportThread    ef81af52-f9fd-4b21-8919-1681f8466d64 
                    
                    entity.supportee = #await loadUser(entity.supportee_id)
                    entity.supporter = #await loadUser(entity.supporter_id)
                    
                    return hunkyDory({entity})
                }
                
                if (msg.fun === 'private_getSupportThreadMessages') {
                    // TODO:vgrechka Secure private_getSupportThreadMessages    c99bc54a-121a-4b3a-b210-a25fa47a43da 
                    return #await handleChunkedSelect({$tag: 'b234f100-78f9-47a0-bafb-ef853b38538b', table: 'support_thread_messages', loadItem: loadSupportThreadMessage,
                        appendToWhere(qb) {
                            qb.append(q`and thread_id = ${msg.entityID}`)
                        }
                    })
                }
                
                if (msg.fun === 'private_createSupportThread') {
                    loadField({key: 'topic', kind: 'topic', mandatory: true})
                    loadField({key: 'message', kind: 'message', mandatory: true})

                    if (isEmpty(fieldErrors)) {
                        const thread_id = #await insertInto(s{table: 'support_threads', values: {
                            tslang: getTSLang(),
                            topic: fields.topic,
                            supportee_id: user.id,
                            status: 'open',
                        }})
                        
                        #await insertInto(s{table: 'support_thread_messages', values: {
                            tslang: getTSLang(),
                            thread_id,
                            sender_id: user.id,
                            message: fields.message,
                            data: {seenBy: {[user.id]: requestTimestamp}},
                        }})
                        
                        return hunkyDory({entity: {id: thread_id}})
                    }
                    return fixErrorsResult()
                }
                
                if (msg.fun === 'private_createSupportThreadMessage') {
                    traceBeginHandler({$tag: 'f32672d7-d67f-49b1-ad40-36477e4c9ba7'})
                    loadField({key: 'message', kind: 'message', mandatory: true})
                    if (!isEmpty(fieldErrors)) return traceEndHandler({ret: fixErrorsResult(), $tag: 'e43bd7b1-bd4c-4866-b2a3-944473187487'})

                    const thread = #await tx.query(s{$tag: 'cfdc3877-f575-4b11-b862-09194528aaea', y: q`
                        select * from support_threads where id = ${msg.threadID}`})[0]
                    let recipient_id
                    if (user.id === thread.supporter_id) recipient_id = thread.supportee_id
                    else if (user.id === thread.supportee_id) recipient_id = thread.supporter_id
                    else return traceEndHandler({res: {error: 'User is not allowed to post into the thread'}, $tag: '157c2563-493a-44cb-b0a6-1e18c73cf0fd'})
                    
                    #await insertInto(s{$tag: 'a370d299-23e8-43d0-ae77-adf5c4b599fc', table: 'support_thread_messages', values: {
                        tslang: getTSLang(),
                        thread_id: msg.threadID,
                        sender_id: user.id,
                        recipient_id,
                        message: fields.message,
                        data: {seenBy: {[user.id]: requestTimestamp}},
                    }})
                    
                    #await updateSupportThreadMessagesSetSeenByUser(msg.threadID)
                    
                    return traceEndHandler({ret: hunkyDory({}), $tag: '8cd70dbd-8bc7-46ac-8636-04eb1a9d0814'})
                }
                
                if (msg.fun === 'private_updateSupportThreadMessage') {
                    traceBeginHandler(s{})
                    
                    const thread = #await tx.query(s{y: q`
                        select * from support_threads where id = ${msg.threadID}`})[0]
                    if (!thread || thread.deleted) return traceEndHandler(s{ret: notFoundResult()})
                    if (user.kind === 'admin') {
                        raise('implement me')
                    } else {
                        if (thread.supportee_id !== user.id) return traceEndHandler(s{ret: forbiddenResult()})
                        if (thread.status !== 'open') return traceEndHandler(s{ret: forbiddenResult()})
                    }
                    
                    const validStatuses = ['open', 'resolved']
                    if (!validStatuses.includes(msg.status)) return traceEndHandler(s{ret: fuckYouResult()})
                    
                    #await tx.query(s{y: q`
                        update support_threads set status = ${msg.status} where id = ${msg.threadID}`})
                        
                    if (msg.status === 'resolved') {
                        #await updateSupportThreadMessagesSetSeenByUser(msg.threadID)
                    } else {
                        raise('TODO:vgrechka Decide when to mark support thread messages as seen    fb2877c6-0957-4e30-bd35-669f1b46ba0a')
                    }
                        
                    return traceEndHandler(s{ret: hunkyDory()})
                }
                
                if (msg.fun === 'private_takeSupportThread') {
                    #await tx.query({$tag: 'ea3ae40d-c285-49b0-9219-415203925257', y: q`
                        update support_threads set supporter_id = ${user.id} where id = ${msg.id}`})
                    return hunkyDory()
                }
                
                const situation = `WTF is the RPC function ${msg.fun}?`
                clog(situation)
                return {fatal: situation}
                
                // @ctx helpers
                
                function compactPhone(s) {
                    return s.replace(/[^0-9]/g, '')
                }
                
                function getTSLang() {
                    if (msg.LANG === 'ua') return 'russian'
                    return 'english'
                }
                    
                async function updateSupportThreadMessagesSetSeenByUser(threadID) {
                    await tx.query(s{y: q`
                        update support_thread_messages
                        set data = data || jsonb_build_object('seenBy', data->'seenBy' || ${{[user.id]: requestTimestamp}})
                        where thread_id = ${msg.threadID}
                              and data->'seenBy'->${user.id} is null`})
                }
                
                function fuckYouResult() {
                    return {error: t('Fuck you, mister hacker', 'Иди в жопу, хацкер-хуяцкер')}
                }
                
                function forbiddenResult() {
                    return {error: t('Forbidden', 'Низя')}
                }
                
                function notFoundResult() {
                    return {error: t('Requested thing is not found', 'Запрошенная штука не найдена')}
                }
                
                function fixErrorsResult() {
                    return {error: t('Please fix errors below', 'Пожалуйста, исправьте ошибки ниже'), fieldErrors}
                }
                
                function hunkyDory(res) {
                    return asn({hunky: 'dory'}, res)
                }
                
                function findTagInSourceCode(tag) {
                    for (file of ['E:/work/aps/aps/src/client.ts', 'E:/work/aps/aps/src/backend.ts',
                                  'E:/work/aps/aps/src/test-writer-ua.ts', 'E:/work/aps/aps/src/test-admin-ua.ts']) {
                        const code = fs.readFileSync(file, 'utf8')
                        const offset = code.indexOf(tag)
                        if (~offset) return {file, code, offset}
                    }
                }
                
                
                async function selectChunk(def) {
                    #extract {table, appendToSelect=noop, appendToWhere=noop, loadItem, defaultOrdering='desc'} from def
                    
                    const actualOrdering = getOrderingParam({defaultValue: defaultOrdering})
                    
                    const fromID = msg.fromID || (actualOrdering === 'asc' ? 0 : PG_MAX_BIGINT)
                    const qb = QueryBuilder()
                    qb.append(q`select *`)
                    appendToSelect(qb)
                    qb.append(q`from ${{inline: table}} where true `)
                    appendToWhere(qb)
                    qb.append(q`and id ${{inline: actualOrdering === 'asc' ? '>=' : '<='}} ${fromID}`)
                    qb.append(q`order by id ${{inline: actualOrdering}}`)
                    qb.append(q`fetch first MORE_CHUNK rows only`)
                    
                    let items = #await tx.query(s{descr: `Select chunk from ${table}`, y: qb.toy()})
                    let moreFromID
                    if (items.length === MORE_CHUNK) {
                        moreFromID = last(items).id
                        items = items.slice(0, MORE_CHUNK - 1)
                    }
                    
                    const loadedItems = []
                    for (const item of items) {
                        loadedItems.push(#await loadItem(s{item}))
                    }
                    
                    return {items: loadedItems, moreFromID, actualOrdering}
                }
                    
                async function handleChunkedSelect(def) {
                    traceBeginHandler({$tag: def.$tag, $sourceLocation: def.$sourceLocation})
                    const res = #await selectChunk(s{}.asn1(def))
                    return traceEndHandler(s{ret: hunkyDory(res)})
                }
                
                function getSearchStringParam() {
                    // TODO:vgrechka Validate searchString?    a0d147b8-c0fc-48e5-a429-2bf46c7e3d99 
                    return (msg.searchString || '').trim()
                }
                    
                
                function getOrderingParam({param='ordering', defaultValue='asc'}={}) {
                    let value = msg[param]
                    if (!['asc', 'desc'].includes(value)) {
                        value = defaultValue
                    }
                    return value
                }
                
                function pickFromUser(def) {
                    #extract {user} from def
                    
                    const strippedUser = omit(user, 'password_hash')
                    // strippedUser.$meta = cloneDeep(user.$meta)
                    // strippedUser.$meta.trace.push(s{event: 'pickFromUser()'})
                    strippedUser.$meta = s{}
                        
                    return strippedUser
                }
                
                
                async function sendEmail(it) { // TODO:vgrechka @refactor Extract to foundation/utils-server
                    if (MODE === 'debug') {
                        sentEmails.push(it)
                    }
                    
                    return // --- cut here ---
                    
                    if (!mailTransport) {
                        const nodemailer = require('nodemailer')
                        mailTransport = nodemailer.createTransport({
                            // TODO:vgrechka Externalize configuration
                            host: '127.0.0.1',
                            port: 2525,
                            auth: {
                                user: 'into/mail',
                                pass: 'bigsecret'
                            }
                        })
                    }
                    
                    const email = asn({
                        from: `APS <noreply@${clientDomain}>`,
                        it
                    })
                    await new Promise((resolve, reject) => {
                        mailTransport.sendMail(email, (err, res) => {
                            if (err) {
                                return reject(err)
                            }
                            resolve('Message sent: ' + res.response)
                        })
                    })
                }
                
                function failOnClientUserMismatch() {
                    if (user.lang !== msg.LANG) raise('Client/user language mismatch')
                    if (msg.clientKind === 'writer' && (user.kind === 'admin' || user.kind === 'root' || user.kind === 'toor')) return
                    if (user.kind !== msg.clientKind) raise('Client/user kind mismatch')
                }
                
                function clientKindDescr() {
                    return `client ${msg.LANG} ${msg.clientKind}`
                }
                
                function loadReasonField(def) {
                    #extract {key, shouldBePresentIfKeys, valueEquals} from def
                    
                    if (msg[shouldBePresentIfKeys] === valueEquals) {
                        loadField(s{key, mandatory: true, maxlen: 5000})
                    } else {
                        if (msg[key] !== undefined) raise(`[${key}] should be undefined when [${shouldBePresentIfKeys} is not [${valueEquals}]`)
                    }
                } 
                
                function loadField(def) {
                    #extract {key, kind, mandatory, mandatoryErrorMessage, maxlen, minlen, nullIfBlank, allowedValues} from def
                    
                    $trace.push(s{event: `Loading field ${key}`})
                    
                    try {
                        let value = msg[key]
                        if (typeof value !== 'string') {
                            dlog(`Bad value type for field [${key}]: `, value)
                            raise('Fuck you with you hacky request')
                        }
                        value = value.trim()
                        
                        if (mandatory && isBlank(value)) {
                            if (mandatoryErrorMessage) {
                                error(mandatoryErrorMessage)
                            } else {
                                errorByKind({
                                    _default: t('TOTE', 'Поле обязательно'),
                                    email: t('TOTE', 'Почта обязательна'),
                                    firstName: t('TOTE', 'Имя обязательно'),
                                    lastName: t('TOTE', 'Фамилия обязательна'),
                                    phone: t('TOTE', 'Телефон обязателен'),
                                })
                            }
                        }
                        
                        if (allowedValues) {
                            if (!allowedValues.includes(value)) error(t(`TOTE`, `Плохое значение для поля ${key}`))
                        }
                        else {
                            if (!maxlen) {
                                maxlen = {
                                    email: 50,
                                    firstName: 50,
                                    lastName: 50,
                                    phone: 20,
                                    topic: 300,
                                    message: 1000,
                                }[kind]
                                if (!maxlen) raise(`WTF, define maxlen for ${kind}`)
                            }
                            
                            if (value.length > maxlen) error(t('TOTE', `Не более ${maxlen} символов`))
                            
                            if (minlen === undefined) {
                                minlen = {
                                    email: 3,
                                }[kind]
                            }
                            if (minlen) {
                                if (value.length < minlen) error(t('TOTE', `Не менее ${minlen} символов`))
                            }
                            
                            if (kind === 'email') {
                                if (!isValidEmail(value)) error(t('TOTE', 'Странная почта какая-то'))
                            }
                            else if (kind === 'phone') {
                                let digitCount = 0
                                for (const c of value.split('')) {
                                    if (!/(\d| |-|\+|\(|\))+/.test(c)) error(t('TOTE', 'Странный телефон какой-то'))
                                    if (/\d/.test(c)) {
                                        ++digitCount
                                    }
                                }
                                const minDigitCount = 6
                                if (digitCount < minDigitCount) error(t('TOTE', `Не менее ${minDigitCount} цифр`))
                            }
                        }
                        
                        if (nullIfBlank && isBlank(value)) {
                            value = null
                        }
                        
                        fields[key] = value
                    } catch (e) {
                        if (e.$$type === 'validation') {
                            fieldErrors[key] = e.message
                        } else {
                            throw e
                        }
                    }
                    
                    
                    function errorByKind(table) {
                        error(table[kind] || table._default || raise(`WTF, implement at least default error for ${kind}`))
                    }
                    
                    function error(message) {
                        throw {$$type: 'validation', message}
                    }
                }
                
                async function loadUserForToken(def) {
                    const rows = #await tx.query(s{shouldLogForUI: false, y: q`
                        select users.* from users, user_tokens
                        where user_tokens.token = ${msg.token} and users.id = user_tokens.user_id`})
                    if (!rows.length) {
                        raise('Invalid token')
                    }
                    user = rows[0]
                    failOnClientUserMismatch()
                    #await loadUserData(s{})
                }
                
                async function dangerouslyKillUser({email}) {
                    const u = #await tx.query({$tag: '8adf924e-47f3-4098-abf9-e5ceee5c7832', y: q`
                        select * from users where email = ${email}`})[0]
                    if (!u) return
                        
                    #await tx.query({$tag: 'e9622700-e408-4bf9-a3cd-434ddf6fb11b', y: q`
                        delete from user_tokens where user_id = ${u.id}`})
                        
                    const supportThreads = #await tx.query({$tag: 'b0477a7a-68af-4553-94bc-65dad3db6f18', y: q`
                        select * from support_threads where supportee_id = ${u.id}`})
                    for (const thread of supportThreads) {
                        #await tx.query({$tag: '96facfd5-9ee6-4ff9-83c8-fef391908c0c', y: q`
                            delete from support_thread_messages where thread_id = ${thread.id}`})
                        #await tx.query({$tag: 'bf0f5d43-2258-4ad5-842e-388e205ccb98', y: q`
                            delete from support_threads where id = ${thread.id}`})
                    }
                    
                    #await tx.query({$tag: 'ac9a127e-39a0-4ca2-8e91-2be461fe4a9c', y: q`
                        delete from user_roles where user_id = ${u.id}`})
                        
                    #await tx.query({$tag: 'c3cf9e53-2a4e-4423-8869-09a8c7078257', y: q`
                        delete from users where email = ${email}`})
                }
                
                async function loadUserData(def) {
                    user.$meta.trace.push(s{event: 'Loading freaking user data'})
                    
                    user.roles = {}
                    const rows = #await tx.query({$tag: 'aea627a2-a69a-4715-ad03-761537ada2fc', y: q`
                        select * from user_roles where user_id = ${user.id}`})
                    for (const row of rows) {
                        user.roles[row.role] = true
                    }
                }
                
                async function insertInto(opts) {
                    return await tx.insertInto(asn({requestTimestamp}, opts))
                }
                
                async function loadSupportThread(thread) {
                    thread.supportee = #await loadUser(thread.supportee)
                    if (thread.supporter_id) {
                        thread.supporter = #await loadUser(thread.supporter_id)
                    }
                    return thread
                }
                        
                async function loadSupportThreadMessage(def) {
                    #extract {item} from def
                    
                    const res = asn({}, item)
                    res.sender = #await loadUser(res.sender_id)
                    if (res.recipient_id) {
                        res.recipient = #await loadUser(res.recipient_id)
                    }
                    return res
                }
                
                async function loadUser(id) {
                    if (nil(id)) return undefined
                    const user = #await tx.query({$tag: 'd206c4b6-84fb-4036-af29-af69f490a51f', y: q`
                        select * from users where id = ${id}`})[0]
                    return pickFromUser(s{user})
                }
            }
            
        } catch (fucked) {
            const situation = `/rpc handle() for ${msg.fun} is fucked up: ${fucked.stack}`
            clog(situation)
            if (stackBeforeAwait) {
                clog(`Stack before await: ${stackBeforeAwait}`)
            }
            if (fucked.stackBeforeAwait) {
                clog(`Fucked stack before await: ${fucked.stackBeforeAwait}`)
            }
            return {fatal: situation, stack: fucked.stack, stackBeforeAwait, fuckedStackBeforeAwait: fucked.stackBeforeAwait} // TODO:vgrechka Send stack only if debug mode
        } finally {
            let shouldLogRequestForUI
            if (MODE === 'debug' && msg.fun && !msg.fun.startsWith('danger_') && msg.fun !== 'private_getLiveStatus') {
                shouldLogRequestForUI = true
            }
            
            if (shouldLogRequestForUI && $trace.length) {
                /*don't await*/ redis.lpush('requests', getCircularJSON().stringify({
                    title: msg.fun,
                    $trace,
                    $clientSourceLocation: msg.$sourceLocation,
                    msg}))
                /*don't await*/ redis.ltrim('requests', 0, 99)
            }
        }
        
        function traceBeginSection(def) {
            #extract {name} from def
            const items = []
            $trace.push({section: {name, items}}.asnn(def))

            $traceStack.push($trace)
            $trace = items
        }
        
        function traceEndSection(def) {
            $trace.push({event: 'End section'}.asnn(def))
            
            if (!$traceStack.length) raise('Stack underflow in traceEndSection')
            $trace = $traceStack.pop()
        }
        
        function traceBeginHandler(def) {
            const preparedMsg = omitMetaShit(omit(msg, 'fun', 'token'))
            $trace.push({event: `Begin handling ${msg.fun}`, msg: preparedMsg, $tag: def.$tag, $sourceLocation: def.$sourceLocation})
//            $trace.push(asn({event: `Begin handling ${msg.fun}`, msg: preparedMsg}, omit(data, '$trace')))
        }
        
        function omitMetaShit(o) {
            return omit(o, '$sourceLocation', '$trace', '$$$captuta', '$definitionStack', '$callStack')
        }
        
        function traceEndHandler(data) {
            invariant(data.ret, 'I want data.ret in traceEndHandler')
            $trace.push(asn({event: `End handling ${msg.fun}`}, omit(data, '$trace')))
            
//            if (msg.fun !== 'danger_getSoftwareVersion') {
//                dlogs(`------- ret of ${msg.fun}`, data.ret)
//            }
            
            return data.ret
        }
    }
    
    } catch (pizdets) {
        logPizdets({title: 'OK, we’ve got some big hairy pizdets here...', message: pizdets.stack})
    }
})

const port = 3100
app.listen(port, _=> {
    clog(`Backend is spinning on 127.0.0.1:${port}`)
})


function logPizdets({title, message}) {
    clog()
    clog('--8<---------------------------------------------------8<--')
    clog(title)
    clog()
    clog(message)
    clog('--8<---------------------------------------------------8<--')
    clog()
}


const pgPools = {}

export function resetImposed() {
    imposedRequestTimestamp = undefined
    imposedNextIDs = []
}

export async function shutDownPool(db) {
    const pool = pgPools[db]
    if (pool) {
        dlog(`Ending pool ${db}...`)
        await pool.end()
        dlog(`Ended pool ${db}`)
        delete pgPools[db]
    }
}

async function pgTransaction(opts, doInTransaction) {
    return await pgConnection(opts, async function(db) {
        try {
            await db._query('begin isolation level repeatable read')
            const ditres = await doInTransaction(db)
            await db._query('commit')
            return ditres
        } catch (diterr) {
            try {
                await db._query('rollback')
            } catch (e) {
                clog('PG fuckup, cannot rollback', e.stack)
            }
            throw diterr
        }
    })
}

export /*async*/ function pgConnection({db}, doWithConnection) {
    let pgPool = pgPools[db]
    if (!pgPool) {
        let config
        if (db === 'prod') {
            raise('TODO get prod DB config from environment')
        } else {
            config = lookup(db, {
                'aps-dev': {database: 'aps-dev', port: 5432, user: 'postgres'},
                'aps-test': {database: 'aps-test', port: 5433, user: 'postgres'},
                'test-template-ua-1': {database: 'test-template-ua-1', port: 5433, user: 'postgres'},
                'test-postgres': {database: 'postgres', port: 5433, user: 'postgres'},
            })
        }
        if (!config) raise(`No database config for ${db}`)
        clog(`Creating PG pool ${db}`)
        pgPool = pgPools[db] = new (require('pg').Pool)(config)
    }
    
    return new Promise((resolvePgConnection, rejectPgConnection) => {
        pgPool.connect(async function(conerr, con, doneWithConnection) {
            if (conerr) {
                clog('PG connection failed', conerr)
                doneWithConnection(conerr)
                return rejectPgConnection(conerr)
            }
            
            const api = {
                /*async*/ _query(y) {
                    let sql, args
                    if (typeof y === 'string') {
                        sql = y
                        args = []
                    } else if (y.sql && y.args) {
                        ;({sql, args} = y)
                    } else {
                        dlog('Bad query type', y)
                        raise('Query should be string or q-thing')
                    }
                    
                    return new Promise((resolveQuery, rejectQuery) => {
                        con.query(sql, args, (qerr, qres) => {
                            if (qerr) {
                                clog('PG query failed', {sql, args, qerr})
                                return rejectQuery(qerr)
                            }
                            
                            resolveQuery(qres)
                        })
                    })
                },
                
                async query(def) { // @ctx function query
                    #extract {$trace, shouldLogForUI=true, y, descr} from def
                    
                    if (!def.$tag && !def.$sourceLocation) raise('I want all queries to be tagged')
                    
                    if (MODE !== 'debug') {
                        shouldLogForUI = false
                    }
                    
                    let queryLogRecordForUI
                    if (shouldLogForUI) {
                        queryLogRecordForUI = def.asnn({y})
                        // queryLogForUI.push(queryLogRecordForUI)
                        if ($trace) {
                            const sql = y.sql ? y.sql : y
                            if (!descr) {
                                descr = trim(sql).split(/\s+/)[0].toUpperCase()
                            }
                            $trace.push(asn({event: `Query: ${descr}`}, queryLogRecordForUI))
                        }
                    }
                    
                    try {
                        const qres = await api._query(y)
                        
                        if (shouldLogForUI) {
                            const prepres = cloneDeep(qres)
                            for (const k of keys(prepres)) {
                                if (k.startsWith('_')) {
                                    delete prepres[k]
                                }
                            }
                            const maxRows = 10
                            if (isArray(prepres.rows) && prepres.rows.length > maxRows) {
                                prepres[`FIRST_${maxRows}_ROWS`] = prepres.rows.slice(0, maxRows)
                                delete prepres.rows
                            }
                            queryLogRecordForUI.res = prepres
                        }
                        
                        const res = qres.rows
                        
                        for (const row of res) {
                            row.$meta = def
                            row.$meta.trace = []
                        }
                        
                        return res
                    } catch (qerr) {
                        if (shouldLogForUI) {
                            queryLogRecordForUI.err = qerr
                        }
                        
                        throw qerr
                    }
                },
                
                async insertInto({$tag, $sourceLocation, shouldLogForUI=true, $trace, table, rows, values, requestTimestamp}) {
                    if (!rows) {
                        rows = [values]
                    }
                    if (!rows.length) return
                    
                    const columnNames = keys(rows[0])
                    const expectedColumnConfiguration = columnNames.join(', ')
                    rows.forEach((row, i) => {
                        const actualColumnConfiguration = keys(row).join(', ')
                        if (actualColumnConfiguration !== expectedColumnConfiguration) raise(`Inconsistent column configuration in row ${i}`)
                    })
                    
                    let id
                    for (const rowValues of rows) {
                        rowValues = cloneDeep(rowValues)
                        const imposedNextID = imposedNextIDs.shift()
                        if (imposedNextID) {
                            rowValues.id = imposedNextID
                            imposedNextID = undefined
                        }
                        
                        let sql = `insert into "${table}"(`
                        const args = []
                        if (requestTimestamp) {
                            if (!columnNames.includes('inserted_at')) {
                                sql += 'inserted_at, '
                                args.push(requestTimestamp)
                            }
                            if (!columnNames.includes('updated_at')) {
                                sql += 'updated_at, '
                                args.push(requestTimestamp)
                            }
                        }
                        for (const [k, v] of toPairs(rowValues)) {
                            sql += `"${k}", `
                            args.push(v)
                        }
                        sql = sql.slice(0, sql.length - ', '.length)
                        sql += ') values(' + range(args.length).map(x => '$' + (x + 1)).join(', ') + ') returning id'
                        const rows = await api.query({$tag, $sourceLocation, shouldLogForUI, $trace, y: {sql, args}})
                        if (id === undefined) {
                            id = rows[0].id
                        }
                    }
                    
                    return id
                }
                
            }
            
            try {
                const dwcres = await doWithConnection(api)
                doneWithConnection()
                resolvePgConnection(dwcres)
            } catch (dwcerr) {
                doneWithConnection()
                rejectPgConnection(dwcerr)
            }
        })
    })
}

const testDB = DBFiddler({db: 'test'})
const testTemplateUA1DB = DBFiddler({db: 'test-template-ua-1'})

function DBFiddler({db}) {
    return {
        async query({y}) {
            return await pgConnection({db}, async function(con) {
                return await con.query({$tag: 'ac9ec6e5-beb8-4b7f-aa07-4d8f70ceb08b', y})
            })
        },
    }
}

export function q(ss, ...substs) {
    let argIndex = 1
    const resWithIndexPlaceholders = it(_=> '$' + argIndex++)
    const resWithARGPlaceHolders = it(_=> 'ARG')
    resWithIndexPlaceholders.sqlWithARGPlaceholders = resWithARGPlaceHolders.sql
    return resWithIndexPlaceholders
    
    function it(makePlaceholder) {
        let sql = ''
        const args = []
        substs.forEach((subst, i) => {
            sql += ss[i]
            if (typeof subst === 'object' && subst !== undefined && subst !== null && subst.inline) {
                sql += subst.inline
            } else {
                sql += makePlaceholder()
                args.push(subst)
            }
        })
        sql += ss[substs.length]
        sql = sql.replace(/MORE_CHUNK/g, MORE_CHUNK)
        return {sql, args}
    }
}

function QueryBuilder() {
    const queries = []

    return {
        toy() {
            let argCount = 0
            const sql = queries.map(x => x.sqlWithARGPlaceholders).join(' ').replace(/\bARG\b/g, _=> '$' + ++argCount)
            const args = [].concat(...queries.map(x => x.args))
            return {sql, args}
        },
        
        append(query) {
            queries.push(query)
        },
    }
}

function heyBackend_sayHelloToMe({askerName}) {
    dlog({askerName})
    return `Hello, ${askerName}`
}

export function imposeNextIDs(x) {
    imposedNextIDs = x
}

export function imposeRequestTimestamp(x) {
    imposedRequestTimestamp = x
}
























