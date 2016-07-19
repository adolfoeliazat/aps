/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

#pragma instrument-ui-rendering

MODE = 'debug'
MORE_CHUNK = 10 + 1

require('regenerator-runtime/runtime')
require('source-map-support').install()
import * as fs from 'fs'
import * as testShitUA from './test-shit-ua'
#import static 'into-u ./stuff'

let testGlobalCounter = 0, simulateRequest

// TODO:vgrechka @ugly Find a better way to make TS aware of `#imported static` names
// that collide with something in the file
let last

const app = newExpress()
let mailTransport, sentEmails = [], fixedNextGeneratedPassword, queryLogForUI = [], imposedRequestTimestamp,
    imposedNextIDs = [], requestTimeLoggingDisabled
    
require('pg').types.setTypeParser(1114, s => { // timestamp without timezone
    return s
})

app.post('/rpc', (req, res) => {
    simulateRequest = async function(msg) {
        const res = await handle(msg)
        if (!res.hunky) raise(`Unhunky response from myself’s ${msg.fun}: ${deepInspect(res)}`)
        return res
    }
    
    // dlog({body: req.body, headers: req.headers})
    handle(req.body).then(message => {
        res.json(message)
    })
    
    async function handle(msg) {
        let requestTimestamp = moment.tz('UTC').format('YYYY-MM-DD HH:mm:ss.SSSSSS')
        if (imposedRequestTimestamp) {
            requestTimestamp = imposedRequestTimestamp
            imposedRequestTimestamp = undefined
        }
        
        const _t = makeT(msg.LANG)
        function t(meta, ...args) {
            return {meta, meat: _t(...args)}
        }
        
        let stackBeforeAwait, awaitRes
        
        const t0 = Date.now()
        try {
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
            }[msg.LANG + '_' + msg.CLIENT_KIND] || [undefined, undefined]
            if (!clientDomain && msg.CLIENT_KIND !== 'devenv' && msg.CLIENT_KIND !== 'debug') raise('WTF is the clientKind?')
            
            if (msg.db) {
                return await pgTransaction({db: msg.db}, doStuff)
            } else {
                return await doStuff()
            }
            
            async function doStuff(tx) {
                const fieldErrors = {}, fields = {}
                let user
            
                if (msg.fun.startsWith('private_')) {
                    #await loadUserForToken()
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
                
                else if (msg.fun === 'danger_updateExpectation') {
                    const genfile = 'E:/work/aps/aps/gen/client-expectations.js'
                    const code = fs.readFileSync(genfile, 'utf8')
                    const newLineOfCode = `EXPECTATIONS['${msg.aid}'] = ${deepInspect(msg.actual).replace(/\r|\n/g, ' ')}`
                    const lines = code.split('\n')
                    const updated = lines.some((s, i) => {
                        if (~s.indexOf(msg.aid)) {
                            lines[i] = newLineOfCode
                            return true
                        }
                    })
                    if (!updated) {
                        lines.push(newLineOfCode)
                    }
                    fs.writeFileSync(genfile, lines.join('\n'))
                    return hunkyDory()
                }
                
                else if (msg.fun === 'danger_clearSentEmails') {
                    sentEmails = []
                    return hunkyDory()
                }
                
                else if (msg.fun === 'danger_imposeNextRequestTimestamp') {
                    imposedRequestTimestamp = msg.timestamp
                    return hunkyDory()
                }
                
                else if (msg.fun === 'danger_imposeNextID') {
                    imposedNextIDs = [msg.id]
                    return hunkyDory()
                }
                
                else if (msg.fun === 'danger_getSentEmails') {
                    return sentEmails
                }
                
                else if (msg.fun === 'danger_killUser') {
                    #await dangerouslyKillUser({email: msg.email})
                    return hunkyDory()
                }
                
                else if (msg.fun === 'danger_killSupportThreads') {
                    #await tx.query({$tag: '0b66e201-0353-4d93-9d68-58932de199ca'}, q`
                        delete from support_thread_messages`)
                    #await tx.query({$tag: '4aa830b9-a217-4515-bff7-353baa0eba62'}, q`
                        delete from support_threads`)
                    return hunkyDory()
                }
                
                else if (msg.fun === 'danger_insert') {
                    #await insertInto({$tag: '1454b421-bc76-4abd-8eea-0ec8e8ecf0a3'}, {table: msg.table, rows: msg.rows})
                    return hunkyDory()
                }
                
                else if (msg.fun === 'danger_fixNextGeneratedPassword') {
                    fixedNextGeneratedPassword = msg.password
                    return hunkyDory()
                }
                
                else if (msg.fun === 'danger_openSourceCode') {
                    let file, offset
                    if (msg.$tag) {
                        for (file of ['E:/work/aps/aps/src/client.ts', 'E:/work/aps/aps/src/backend.ts',
                                      'E:/work/aps/aps/src/client-writer-tests.ts', 'E:/work/aps/aps/src/client-admin-tests.ts']) {
                            const code = fs.readFileSync(file, 'utf8')
                            if (~(offset = code.indexOf(msg.$tag))) break
                        }
                        if (!~offset) return {error: 'Tag is not found in code'}
                    } else if (msg.$sourceLocation) {
                        // Source location example: aps/src/backend.ts[7556]:181:35
                        const openBracket = msg.$sourceLocation.indexOf('[' /*]*/)
                        const closingBracket = msg.$sourceLocation.indexOf(/*[*/ ']')
                        const firstColon = msg.$sourceLocation.indexOf(':')
                        const secondColon = msg.$sourceLocation.indexOf(':', firstColon + 1)
                        let filePartEnd
                        if (~openBracket && ~closingBracket) {
                            filePartEnd = openBracket
                        } else if (~firstColon && ~secondColon) {
                            filePartEnd = firstColon
                        } else {
                            return {error: 'I want either brackets or two colons in source location'}
                        }
                        const filePart = msg.$sourceLocation.slice(0, filePartEnd)
                        file = {
                            'aps/src/client.ts': 'E:/work/aps/aps/src/client.ts',
                            'client.ts': 'E:/work/aps/aps/src/client.ts',
                            'aps/src/backend.ts': 'E:/work/aps/aps/src/backend.ts',
                            'backend.ts': 'E:/work/aps/aps/src/backend.ts',
                            'ui.ts': 'E:/work/foundation/u/src/ui.ts',
                            'u/src/ui.ts': 'E:/work/foundation/u/src/ui.ts',
                        }[filePart]
                        if (!file) return {error: `Weird file in source location: [${filePart}]`}
                        if (~openBracket && ~closingBracket) {
                            offset = parseInt(msg.$sourceLocation.slice(openBracket + 1, closingBracket))
                        } else if (~firstColon && ~secondColon) {
                            const line = parseInt(msg.$sourceLocation.slice(firstColon + 1, secondColon), 10) - 1
                            const column = parseInt(msg.$sourceLocation.slice(secondColon + 1), 10) - 1
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
                        }
                    } else {
                        raise('Weird source location descriptor')
                    }
                    
                    await RPCClient({url: 'http://127.0.0.1:4001/rpc'}).call({fun: 'openEditor', file, offset})
                    return hunkyDory()
                }
                
                else if (msg.fun === 'danger_getQueries') {
                    let last = msg.last || 1
                    return queryLogForUI.slice(queryLogForUI.length - last)
                }
                
                else if (msg.fun === 'danger_resetTestDatabase') {
                    if (msg.alsoRecreateTemplate) {
                        if (msg.templateDB === 'test-template-ua-1') {
                            testShitUA.setBackendContext({simulateRequest})
                            await testShitUA.createTestTemplateUA1DB()
                        } else {
                            raise(`I don’t know how to recreate test template DB for ${msg.templateDB}`)
                        }
                    }
                    
                    await shutDownPool('test')
                    await shutDownPool(msg.templateDB)
                    await pgConnection({db: 'test-postgres'}, async function(db) {
                        await db.query({$tag: '8dcb544c-1337-4298-8970-21466bad7c4c'}, `drop database if exists "aps-test"`)
                        await db.query({$tag: '9b569e3e-53b7-4296-97d2-d1ce2a1684b4'}, `create database "aps-test" template = "aps-${msg.templateDB}"`)
                    })
                    return hunkyDory()
                }
                
                
                else if (msg.fun === 'signInWithPassword') {
                    const rows = #await tx.query({$tag: '4281bc8b-7f86-4a7a-86e6-bb4d7587b654'}, q`
                        select * from users where email = ${msg.email}`)
                    if (!rows.length) {
                        logFailure('Non-existing email')
                        return invalidEmailOrPasswordMessage()
                    }
                    user = rows[0]
                    if (!(await comparePassword(msg.password, user.password_hash))) {
                        logFailure('Invalid password for existing email')
                        return invalidEmailOrPasswordMessage()
                    }
                    
                    failOnClientUserMismatch()
                    
                    const token = uuid()
                    #await tx.query({$tag: 'e8ccf032-2c17-4a98-8666-cd18f82326c7'}, q`
                        insert into user_tokens(user_id, token) values(${user.id}, ${token})`)

                    #await loadUserData()
                    return hunkyDory({user: pickFromUser(), token})
                    
                    
                    function logFailure(reason) {
                        clog('Sign-in failed: ' + reason)
                    }
                    
                    function invalidEmailOrPasswordMessage() {
                        return {error: t('Invalid email or password', 'Неверная почта или пароль')}
                    }
                }
                
                else if (msg.fun === 'private_getUserInfo') {
                    return hunkyDory({user: pickFromUser()})
                }
                
                else if (msg.fun === 'private_getLiveStatus') {
                    let heapSize = 0
                    const unassignedSupportThreadCount = parseInt(#await tx.query({$tag: '0c955700-5ef5-4803-bc5f-307be0380259'}, q`
                        select count(*) as value from support_threads where supporter_id is null`)[0].value, 10)
                    heapSize += unassignedSupportThreadCount
                        
                    return hunkyDory({heapSize, unassignedSupportThreadCount})
                }
                
                else if (msg.fun === 'signUp') {
                    if (!msg.agreeTerms) {
                        fieldErrors.agreeTerms = t('You have to agree with terms and conditions', 'Необходимо принять соглашение')
                    }
                    
                    loadField({key: 'email', kind: 'email', mandatory: true})
                    loadField({key: 'firstName', kind: 'firstName', mandatory: true})
                    loadField({key: 'lastName', kind: 'lastName', mandatory: true})
                    
                    if (isEmpty(fieldErrors)) {
                        try {
                            let password = uuid()
                            
                            if (fixedNextGeneratedPassword) {
                                password = fixedNextGeneratedPassword
                                fixedNextGeneratedPassword = undefined
                            }
                    
                            #await tx.query({$tag: 'f1030713-94b1-4626-a5ca-20d5b60fb0cb'}, q`
                                insert into users (inserted_at,         updated_at,          email,           kind,               lang,        state,                password_hash,                   first_name,          last_name)
                                            values(${requestTimestamp}, ${requestTimestamp}, ${fields.email}, ${msg.CLIENT_KIND}, ${msg.LANG}, ${'profile-pending'}, ${await hashPassword(password)}, ${fields.firstName}, ${fields.lastName})`)
                            
                            const signInURL = `http://${clientDomain}${clientPortSuffix}/sign-in.html`
                                
                            let subject
                            if (msg.LANG === 'ua' && msg.CLIENT_KIND === 'customer') {
                                subject = 'Пароль для APS'
                            } else if (msg.LANG === 'ua' && msg.CLIENT_KIND === 'writer') {
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
                            return hunkyDory()
                        } catch (e) {
                            if (e.code === '23505') {
                                fieldErrors.email = t('This email is already registered', 'Такая почта уже зарегистрирована')
                                return youFixErrors()
                            } else {
                                throw e
                            }
                        }
                    }
                    
                    return youFixErrors()
                }
                
                else if (msg.fun === 'private_updateProfile') {
                    loadField({key: 'phone', kind: 'phone', mandatory: true})

                    if (isEmpty(fieldErrors)) {
                        #await tx.query({$tag: '492b9099-44c3-497b-a403-09abd2090be8'}, q`
                            update users set profile_updated_at = ${requestTimestamp},
                                             phone = ${fields.phone},
                                             state = 'profile-approval-pending'
                                   where id = ${user.id}`)
                        #await loadUserForToken()
                        return hunkyDory({newUser: pickFromUser()})
                    }
                    return youFixErrors()
                }
                
                else if (msg.fun === 'private_getSupportThreads') {
                    const items = #await tx.query({$tag: 'e6dd4dfa-8118-4e25-9f71-13665dada843'}, q`
                        select * from support_threads
                                 where supportee_id = ${user.id}
                                 order by inserted_at desc
                    `)
                    return hunkyDory({items})
                }
                
                else if (msg.fun === 'private_getUnassignedSupportThreads') {
                    let fromID = msg.fromID
                    if (fromID === undefined) fromID = 0
                    let items = #await tx.query({$tag: 'f360d4da-d3ad-4bed-990f-c4f0c4a66176'}, q`
                        select * from support_threads
                        where id >= ${fromID} and supporter_id is null
                        order by id asc
                        fetch first MORE_CHUNK rows only`)
                    let moreFromID
                    if (items.length === MORE_CHUNK) {
                        moreFromID = last(items).id
                        items = items.slice(0, MORE_CHUNK - 1)
                    }
                        
                    for (const item of items) {
                        item.messages = []
                        const messages = #await tx.query({$tag: '336d0f95-f1f7-4615-ab35-c47025eb63b6'}, q`
                            select * from support_thread_messages
                            where thread_id = ${item.id}
                            order by id asc`)
                        for (const message of messages) {
                            item.messages.push(#await loadSupportThreadMessage(message))
                        }
                        // item.unreadMessageCount = 1
                    }
                    return hunkyDory({items, moreFromID})
                }
                
                else if (msg.fun === 'private_getSupportThreadMessages') {
                    // TODO:vgrechka Reject bogus params
                    // TODO:vgrechka Check access
                    
                    const entities = #await tx.query({$tag: 'f13a4eda-ab97-496d-b037-8b8f2b2599e1'}, q`
                        select * from support_threads
                                 where id = ${msg.id}
                    `)
                    // TODO:vgrechka Handle request for non-existing entity
                    const entity = entities[0]

                    const items = #await tx.query({$tag: 'b40d63d7-2cd2-4326-9f08-db3892522d7f'}, q`
                        select * from support_thread_messages
                                 where thread_id = ${msg.id}
                                 order by inserted_at desc
                    `)
                    #await fillForeigns(items, 'users', ['sender', 'recipient'])
                    // dlogs({items})
                    return hunkyDory({entity, items})
                    
                    async function fillForeigns(rows, table, columns) {
                        const ids = []
                        for (const row of rows) {
                            for (const column of columns) {
                                const id = row[column + '_id']
                                if (id !== undefined && id !== null) {
                                    ids.push(id)
                                }
                            }
                        }
                        if (!ids.length) return
                        
                        const placeholders = range(ids.length).map(x => '$' + (x + 1)).join(', ')
                        const foreignRows = #await tx.query({$tag: '0afa1c4f-885b-42f0-b102-ba9e862fa3d2'}, {q: {
                            sql: `select * from users where id in (${placeholders})`,
                            args: ids}})
                            
                        for (const row of rows) {
                            for (const column of columns) {
                                let entity
                                for (const fr of foreignRows) {
                                    if (fr.id === row[column + '_id']) {
                                        entity = fr
                                        delete entity.password_hash
                                        break
                                    }
                                }
                                row[column] = entity
                            }
                        }
                    }
                }
                
                else if (msg.fun === 'private_createSupportThread') {
                    loadField({key: 'topic', kind: 'topic', mandatory: true})
                    loadField({key: 'message', kind: 'message', mandatory: true})

                    if (isEmpty(fieldErrors)) {
                        const thread_id = #await insertInto({$tag: 'c8a54fb2-4a92-4c95-a13d-7ae145c7ebe9'}, {table: 'support_threads', values: {
                            topic: fields.topic,
                            supportee_id: user.id,
                        }})
                        
                        #await insertInto({$tag: '44178859-236b-411b-b3df-247ffb47e89e'}, {table: 'support_thread_messages', values: {
                            thread_id,
                            sender_id: user.id,
                            message: fields.message,
                        }})
                        
                        return hunkyDory({entity: {id: thread_id}})
                    }
                    return youFixErrors()
                }
                
                else if (msg.fun === 'private_createSupportThreadMessage') {
                    loadField({key: 'message', kind: 'message', mandatory: true})

                    if (isEmpty(fieldErrors)) {
                        #await insertInto({$tag: 'a370d299-23e8-43d0-ae77-adf5c4b599fc'}, {table: 'support_thread_messages', values: {
                            thread_id: msg.threadID,
                            sender_id: user.id,
                            message: fields.message,
                        }})
                        
                        return hunkyDory({})
                    }
                    return youFixErrors()
                }
                
                else if (msg.fun === 'private_takeSupportThread') {
                    #await tx.query({$tag: 'ea3ae40d-c285-49b0-9219-415203925257'}, q`
                        update support_threads set supporter_id = ${user.id} where id = ${msg.id}`)
                    return hunkyDory()
                }
                
                const situation = `WTF is the RPC function ${msg.fun}?`
                clog(situation)
                return {fatal: situation}
                
                // @ctx handle helpers
                
                function pickFromUser() {
                    return pick(user, 'id', 'first_name', 'last_name', 'email', 'state', 'inserted_at',
                                      'profile_updated_at', 'phone', 'kind', 'roles')
                }
                
                
                function youFixErrors() {
                    return {
                        error: t('Please fix errors below', 'Пожалуйста, исправьте ошибки ниже'),
                        fieldErrors
                    }
                }
                
                function hunkyDory(res) {
                    return asn({hunky: 'dory'}, res)
                }
                
                async function sendEmail(it) { // TODO:vgrechka @refactor Extract to foundation/utils-server
                    if (MODE === 'debug') {
                        sentEmails.push(it)
                    }
                    
                    return
                    
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
                    if (msg.CLIENT_KIND === 'writer' && (user.kind === 'admin' || user.kind === 'root' || user.kind === 'toor')) return
                    if (user.kind !== msg.CLIENT_KIND) raise('Client/user kind mismatch')
                }
                
                function clientKindDescr() {
                    return `client ${msg.LANG} ${msg.CLIENT_KIND}`
                }
                
                function loadField({key, kind, mandatory}) {
                    try {
                        let value = msg[key]
                        if (typeof value !== 'string') raise('Fuck you with you hacky request')
                        value = value.trim()
                        
                        if (mandatory && isBlank(value)) errorByKind({
                            email: t('TOTE', 'Почта обязательна'),
                            firstName: t('TOTE', 'Имя обязательно'),
                            lastName: t('TOTE', 'Фамилия обязательна'),
                            phone: t('TOTE', 'Телефон обязателен'),
                            _default: t('TOTE', 'Поле обязательно'),
                        })
                        
                        const maxlen = {
                            email: 50,
                            firstName: 50,
                            lastName: 50,
                            phone: 20,
                            topic: 300,
                            message: 1000,
                        }[kind]
                        if (!maxlen) raise(`WTF, define maxlen for ${kind}`)
                        if (value.length > maxlen) error(t('TOTE', `Не более ${maxlen} символов`))
                        
                        const minlen = {
                            email: 3,
                        }[kind]
                        if (minlen) {
                            if (value.length < minlen) error(t('TOTE', `Не менее ${minlen} символов`))
                        }
                        
                        if (kind === 'email') {
                            if (!isValidEmail(value)) error(t('TOTE', 'Странная почта какая-то'))
                        } else if (kind === 'phone') {
                            let digitCount = 0
                            for (const c of value.split('')) {
                                if (!/\d| |-|\(|\)/.test(c)) error(t('TOTE', 'Странный телефон какой-то'))
                                if (/\d/.test(c)) {
                                    ++digitCount
                                }
                            }
                            const minDigitCount = 6
                            if (digitCount < minDigitCount) error(t('TOTE', `Не менее ${minDigitCount} цифр`))
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
                
                async function loadUserForToken() {
                    const rows = #await tx.query({$tag: 'cb833ae1-19da-459e-a638-da4c9e7266cc', shouldLogForUI: false}, q`
                        select users.* from users, user_tokens
                        where user_tokens.token = ${msg.token} and users.id = user_tokens.user_id`)
                    if (!rows.length) {
                        raise('Invalid token')
                    }
                    user = rows[0]
                    // user.id = user.user_id // To tell users.id from user_tokens.id it's selected additionaly as `user_id`
                    failOnClientUserMismatch()
                    #await loadUserData()
                }
                
                async function dangerouslyKillUser({email}) {
                    const u = #await tx.query({$tag: '8adf924e-47f3-4098-abf9-e5ceee5c7832'}, q`
                        select * from users where email = ${email}`)[0]
                    if (!u) return
                        
                    #await tx.query({$tag: 'e9622700-e408-4bf9-a3cd-434ddf6fb11b'}, q`
                        delete from user_tokens where user_id = ${u.id}`)
                        
                    const supportThreads = #await tx.query({$tag: 'b0477a7a-68af-4553-94bc-65dad3db6f18'}, q`
                        select * from support_threads where supportee_id = ${u.id}`)
                    for (const thread of supportThreads) {
                        #await tx.query({$tag: '96facfd5-9ee6-4ff9-83c8-fef391908c0c'}, q`
                            delete from support_thread_messages where thread_id = ${thread.id}`)
                        #await tx.query({$tag: 'bf0f5d43-2258-4ad5-842e-388e205ccb98'}, q`
                            delete from support_threads where id = ${thread.id}`)
                    }
                    
                    #await tx.query({$tag: 'ac9a127e-39a0-4ca2-8e91-2be461fe4a9c'}, q`
                        delete from user_roles where user_id = ${u.id}`)
                        
                    #await tx.query({$tag: 'c3cf9e53-2a4e-4423-8869-09a8c7078257'}, q`
                        delete from users where email = ${email}`)
                }
                
                async function loadUserData() {
                    user.roles = {}
                    const rows = #await tx.query({$tag: 'aea627a2-a69a-4715-ad03-761537ada2fc'}, q`
                        select * from user_roles where user_id = ${user.id}`)
                    for (const row of rows) {
                        user.roles[row.role] = true
                    }
                }
                
                async function insertInto(meta, opts) {
                    return await tx.insertInto(meta, asn({requestTimestamp}, opts))
                }
                        
                async function loadSupportThreadMessage(message) {
//                    const message = #await tx.query({$tag: '20211f46-dae4-4b94-a4aa-bb19b4100280'}, q`
//                        select * from support_thread_messages
//                        where id = ${id}`)[0]
                    message.sender = #await loadUser(message.sender_id)
                    if (message.recipient_id) {
                        message.recipient = #await loadUser(message.recipient_id)
                    }
                    return message
                }
                
                async function loadUser(id) {
                    const user = #await tx.query({$tag: 'd206c4b6-84fb-4036-af29-af69f490a51f'}, q`
                        select * from users
                        where id = ${id}`)[0]
                    return user
                }
            }
            
        } catch (fucked) {
            const situation = `/rpc handle() for ${msg.fun} is fucked up: ${fucked.stack}`
            clog(situation)
            if (stackBeforeAwait) {
                clog(`Stack before await: ${stackBeforeAwait}`)
            }
            return {fatal: situation, stack: fucked.stack, stackBeforeAwait} // TODO:vgrechka Send stack only if debug mode
        } finally {
            if (!requestTimeLoggingDisabled && msg.fun !== 'private_getLiveStatus') {
                const elapsed = Date.now() - t0
                dlog(`${msg.fun}: ${elapsed}ms`)
            }
        }
    }
})

const port = 3100
app.listen(port, _=> {
    clog(`Backend is spinning on 127.0.0.1:${port}`)
})


const pgPools = {}

export function resetImposed() {
    imposedRequestTimestamp = undefined
    imposedNextIDs = []
}

export async function shutDownPool(db) {
    const pool = pgPools[db]
    if (pool) {
        await pool.end()
        delete pgPools[db]
    }
}

async function pgTransaction(opts, doInTransaction) {
    return await pgConnection(opts, async function(db) {
        try {
            await db._query('start transaction isolation level read committed')
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
                dev: {database: 'aps-dev', port: 5432, user: 'postgres'},
                test: {database: 'aps-test', port: 5433, user: 'postgres'},
                'test-template-ua-1': {database: 'aps-test-template-ua-1', port: 5433, user: 'postgres'},
                'test-postgres': {database: 'postgres', port: 5433, user: 'postgres'},
            })
        }
        if (!config) raise(`No database config for ${db}`)
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
                /*async*/ _query(...xs) {
                    let sql, args
                    if (typeof xs[0] === 'object' && xs[0].q) {
                        ;({sql, args} = xs[0].q)
                    } else {
                        ;[sql, args] = xs
                        if (typeof sql !== 'string') raise('Query should be string or q-thing')
                    }
                    
                    // If args is bad, con.query fails without telling us
                    invariant(args === undefined || isArray(args), 'tx.query wants args to be array or undefined')
                    
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
                
                async query({$tag, shouldLogForUI=true}, ...xs) {
                    arguments // XXX This fixes TS bug with ...rest params in async functions
                    if (!$tag) raise('I want all queries to be tagged')
                    
                    if (MODE !== 'debug') {
                        shouldLogForUI = false
                    }
                    
                    let queryLogRecordForUI
                    if (shouldLogForUI) {
                        queryLogRecordForUI = {$tag, arguments: xs}
                        queryLogForUI.push(queryLogRecordForUI)
                    }
                    
                    try {
                        const qres = await api._query(...xs)
                        
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
                        
                        return qres.rows
                    } catch (qerr) {
                        if (shouldLogForUI) {
                            queryLogRecordForUI.err = qerr
                        }
                        
                        throw qerr
                    }
                },
                
                async insertInto(meta, {table, rows, values, requestTimestamp}) {
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
                        const rows = await api.query(meta, {q: {sql, args}})
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
        async query(q) {
            return await pgConnection({db}, async function(con) {
                return await con.query({$tag: '9fbde601-86dd-4d69-bce2-05bdcb7fb4be'}, q)
            })
        },
    }
}

export function q(ss, ...substs) {
    let sql = ''
    const args = []
    substs.forEach((subst, i) => {
        sql += ss[i] + '$' + (i + 1)
        args.push(subst)
    })
    sql += ss[substs.length]
    sql = sql.replace(/MORE_CHUNK/g, MORE_CHUNK)
    return {q: {sql, args}}
    // return {ss, substs}
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




















