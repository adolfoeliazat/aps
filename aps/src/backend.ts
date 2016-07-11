/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

#pragma instrument-ui-rendering

MODE = 'debug'
MAX_NAME = 50

require('regenerator-runtime/runtime')
require('source-map-support').install()
import * as fs from 'fs'
import static 'into-u ./stuff ./test-stuff'

let testGlobalCounter = 0

const app = newExpress()
let mailTransport, sentEmails = [], fixedNextGeneratedPassword, queryLogForUI = [], imposedRequestTimestamp,
    imposedNextID

require('pg').types.setTypeParser(1114, s => { // timestamp without timezone
    return s
})

app.post('/rpc', (req, res) => {
    // dlog({body: req.body, headers: req.headers})
    handle().then(message => {
        res.json(message)
    })
    
    async function handle() {
        let requestTimestamp = moment.tz('UTC').format('YYYY-MM-DD HH:mm:ss.SSSSSS')
        if (imposedRequestTimestamp) {
            requestTimestamp = imposedRequestTimestamp
            imposedRequestTimestamp = undefined
        }
        
        const msg = req.body
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
        
            const [clientDomain, clientPortSuffix] = {
                ua_customer: [DOMAIN_UA_CUSTOMER, PORT_SUFFIX_UA_CUSTOMER],
                ua_writer: [DOMAIN_UA_WRITER, PORT_SUFFIX_UA_WRITER],
                en_customer: [DOMAIN_EN_CUSTOMER, PORT_SUFFIX_EN_CUSTOMER],
                en_writer: [DOMAIN_EN_WRITER, PORT_SUFFIX_EN_WRITER],
            }[msg.LANG + '_' + msg.CLIENT_KIND] || [undefined, undefined]
            if (!clientDomain && msg.CLIENT_KIND !== 'devenv' && msg.CLIENT_KIND !== 'debug') raise('WTF is the clientKind?')
            
            return await pgTransaction(async function(tx) {
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
                    imposedNextID = msg.id
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
                
                else if (msg.fun === 'danger_setUpTestUsers') {
                    #await tx.query({$tag: '518050b0-dedc-40f1-93e0-c51b1021a6ad'}, q`
                        delete from support_thread_messages where sender_id >= 100 and sender_id < 1000`)
                    #await tx.query({$tag: 'c46dbaf9-b7b0-4442-9ac1-acc75ef1330b'}, q`
                        delete from support_threads where supportee_id >= 100 and supportee_id < 1000`)
                    #await tx.query({$tag: '183a4e24-8885-4dcb-b360-5c767893e4bb'}, q`
                        delete from user_tokens where user_id >= 100 and user_id < 1000`)
                    #await tx.query({$tag: 'b8b2d24e-26b4-45b3-b242-0d639742e4da'}, q`
                        delete from user_roles where user_id >= 100 and user_id < 1000`)
                    #await tx.query({$tag: 'b9d25014-36d2-4ae8-a92c-1716253bc1d9'}, q`
                        delete from users where id >= 100 and id < 1000`)
                    #await insertInto({$tag: '334d9edb-8880-4d01-a366-ba4ffd724f37'}, {table: 'users', rows: testdata.users})
                    #await insertInto({$tag: 'd07e2a4b-5cda-4cb1-8578-e2004c70140d'}, {table: 'user_roles', values: {
                        user_id: 100,
                        role: 'support',
                    }})
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
                    const items = #await tx.query({$tag: 'f360d4da-d3ad-4bed-990f-c4f0c4a66176'}, q`
                        select * from support_threads
                                 where supporter_id is null
                                 order by inserted_at asc
                    `)
                    return hunkyDory({items})
                }
                
                else if (msg.fun === 'private_getSupportThreadMessages') {
                    // TODO:vgrechka Reject bogus params
                    // TODO:vgrechka Check access
                    
                    const entities = #await tx.query({$tag: 'f13a4eda-ab97-496d-b037-8b8f2b2599e1'}, q`
                        select * from support_threads
                                 where id = ${msg.entityID}
                    `)
                    // TODO:vgrechka Handle request for non-existing entity
                    const entity = entities[0]

                    const items = #await tx.query({$tag: 'b40d63d7-2cd2-4326-9f08-db3892522d7f'}, q`
                        select * from support_thread_messages
                                 where thread_id = ${msg.entityID}
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
                    // TODO:vgrechka Security
                    loadField({key: 'message', kind: 'message', mandatory: true})

                    if (isEmpty(fieldErrors)) {
                        #await insertInto({$tag: 'a370d299-23e8-43d0-ae77-adf5c4b599fc'}, {table: 'support_thread_messages', values: {
                            thread_id: msg.entityID,
                            sender_id: user.id,
                            message: fields.message,
                        }})
                        
                        return hunkyDory({})
                    }
                    return youFixErrors()
                }
                
                const situation = `WTF is the RPC function ${msg.fun}?`
                clog(situation)
                return {fatal: situation}
                
                // @ctx handle helpers
                
                function pickFromUser() {
                    return pick(user, 'id', 'first_name', 'last_name', 'email', 'state', 'inserted_at',
                                      'profile_updated_at', 'phone', 'kind', 'roles')
                }
                
                
                async function insertInto(meta, {table, rows, values}) {
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
                        if (imposedNextID) {
                            rowValues.id = imposedNextID
                            imposedNextID = undefined
                        }
                        
                        let sql = `insert into "${table}"(`
                        const args = []
                        if (!columnNames.includes('inserted_at')) {
                            sql += 'inserted_at, '
                            args.push(requestTimestamp)
                        }
                        if (!columnNames.includes('updated_at')) {
                            sql += 'updated_at, '
                            args.push(requestTimestamp)
                        }
                        for (const [k, v] of toPairs(rowValues)) {
                            sql += `"${k}", `
                            args.push(v)
                        }
                        sql = sql.slice(0, sql.length - ', '.length)
                        sql += ') values(' + range(args.length).map(x => '$' + (x + 1)).join(', ') + ') returning id'
                        const rows = await tx.query(meta, {q: {sql, args}})
                        if (id === undefined) {
                            id = rows[0].id
                        }
                    }
                    
                    return id
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
                            message: 300,
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
            })
            
        } catch (fucked) {
            const situation = `/rpc handle() is fucked up: ${fucked.stack}`
            clog(situation)
            if (stackBeforeAwait) {
                clog(`Stack before await: ${stackBeforeAwait}`)
            }
            return {fatal: situation, stack: fucked.stack, stackBeforeAwait} // TODO:vgrechka Send stack only if debug mode
        } finally {
            const elapsed = Date.now() - t0
            dlog(`${msg.fun}: ${elapsed}ms`)
        }
    }
})

const port = 3100
app.listen(port, _=> {
    clog(`Backend is spinning on 127.0.0.1:${port}`)
})

let pgPool

function pgTransaction(doInTransaction) {
    if (!pgPool) {
        pgPool = new (require('pg').Pool)({
            database: 'aps',
            port: 5432,
            user: 'aps',
            password: 'apssecret',
        })
    }
    
    return new Promise((resolvePgTransaction, rejectPgTransaction) => {
        pgPool.connect(async function(conerr, con, doneWithConnection) {
            if (conerr) {
                clog('PG connection failed', conerr)
                doneWithConnection(conerr)
                return rejectPgTransaction(conerr)
            }
            
            const api = {
                query({$tag, shouldLogForUI=true}, ...xs) {
                    if (!$tag) raise('I want all queries to be tagged')
                    
                    let sql, args
                    if (typeof xs[0] === 'object' && xs[0].q) {
                        ;({sql, args} = xs[0].q)
                    } else {
                        ;[sql, args] = xs
                        if (typeof sql !== 'string') raise('Query should be string or q-thing')
                    }
                    
                    if (MODE !== 'debug') {
                        shouldLogForUI = false
                    }
                    
                    // If args is bad, con.query fails without telling us
                    invariant(args === undefined || isArray(args), 'tx.query wants args to be array or undefined')
                    
                    return new Promise((resolveQuery, rejectQuery) => {
                        let queryLogRecordForUI
                        if (shouldLogForUI) {
                            queryLogRecordForUI = {sql, args, $tag}
                            queryLogForUI.push(queryLogRecordForUI)
                        }
                        
                        con.query(sql, args, (qerr, qres) => {
                            if (qerr) {
                                clog('PG query failed', {sql, args, qerr})
                                if (shouldLogForUI) {
                                    queryLogRecordForUI.err = qerr
                                }
                                
                                return rejectQuery(qerr)
                            }
                            
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
                            
                            resolveQuery(qres.rows)
                        })
                    })
                }
            }
            
            try {
                await _query('start transaction isolation level read committed')
                const ditres = await doInTransaction(api)
                await _query('commit')
                doneWithConnection()
                resolvePgTransaction(ditres)
            } catch (diterr) {
                try {
                    await _query('rollback')
                } catch (e) {
                    clog('PG fuckup, cannot rollback', e.stack)
                }
                doneWithConnection()
                rejectPgTransaction(diterr)
            }
            
            
            function _query(sql, args) {
                return new Promise((resolveQuery, rejectQuery) => {
                    con.query(sql, args, (qerr, qres) => {
                        if (qerr) {
                            clog('PG query failed', {sql, args, qerr})
                            return rejectQuery(qerr)
                        }
                        
                        resolveQuery(qres.rows)
                    })
                })
            }
            
//            doInTransaction(api)
//                .then(ditres => {
//                    doneWithConnection()
//                    resolvePgTransaction(ditres)
//                })
//                .catch(diterr => {
//                    doneWithConnection()
//                    rejectPgTransaction(diterr)
//                })
        })
    })
}

function q(ss, ...substs) {
    let sql = ''
    const args = []
    substs.forEach((subst, i) => {
        sql += ss[i] + '$' + (i + 1)
        args.push(subst)
    })
    sql += ss[substs.length]
    return {q: {sql, args}}
    // return {ss, substs}
}

function heyBackend_sayHelloToMe({askerName}) {
    dlog({askerName})
    return `Hello, ${askerName}`
}

let kindOfState

function heyBackend_changeYourStateTo(state) {
    kindOfState = state
}

function heyBackend_whatsYourState() {
    return kindOfState
}

function generateRandomSupportThreads(count) {
    const rows = []
    range(count).forEach(i => {
        const year = random(2014, 2016)
        const month = prependZero(random(1, 12))
        const day = prependZero(random(1, 25))
        const hour = prependZero(random(0, 23))
        const minute = prependZero(random(0, 59))
        const second = prependZero(random(0, 59))
        const stamp = `${year}-${month}-${day} ${hour}:${minute}:${second}`
        const supportee_id = randomItem(testdata.customerAndWriterUserIDs)
        let topic = randomShortSentences()[i]
        if (topic.endsWith('.')) {
            topic = topic.slice(0, topic.length - 1)
        }
        rows.push({id: undefined, inserted_at: stamp, updated_at: stamp, supportee_id, topic})
    })
    rows = sortBy(rows, 'inserted_at')
    range(count).forEach(i => {
        rows[i].id = i + 1
    })
        
    return rows.map(JSON.stringify).join(',\n')
    
    
    function prependZero(x) {
        x = '' + x
        if (x.length === 1) {
            x = '0' + x
        }
        return x
    }
}

function extractRandomSentences(pilef) {
    const sents = []
    const pile = pilef()
    let start = 0
    const sentenceEndRe = /[.?!]+/g
    let match
    while (match = sentenceEndRe.exec(pile)) {
        if (/\s/.test(pile[match.index - 2])) continue
        let sent = pile.slice(start, match.index + 1).trim()
        if (sent[0] === '-' || sent[0] === '–') {
            sent = sent.slice(1).trim()
        }
        sents.push(sent)
        start = match.index + match[0].length
    }
    
    return sents.map(x => '`' + x + '`,').join('\n')
}

function randomLongSentences() {
    return [
        `Кто-то, по-видимому, оклеветал Йозефа К., потому что, не сделав ничего дурного, он попал под арест.`,
        `Кухарка его квартирной хозяйки, фрау Грубах, ежедневно приносившая ему завтрак около восьми, на этот раз не явилась.`,
        `К. немного подождал, поглядел с кровати на старуху, живущую напротив, – она смотрела из окна с каким-то необычным для нее любопытством – и потом, чувствуя и голод, и некоторое недоумение, позвонил.`,
        `Тотчас же раздался стук, и в комнату вошел какой-то человек.`,
        `К. никогда раньше в этой квартире его не видел.`,
        `Он был худощав и вместе с тем крепко сбит, в хорошо пригнанном черном костюме, похожем на дорожное платье – столько на нем было разных вытачек, карманов, пряжек, пуговиц и сзади хлястик, – от этого костюм казался особенно практичным, хотя трудно было сразу сказать, для чего все это нужно.`,
        `Пусть Анна принесет мне завтрак, – сказал К. и стал молча разглядывать этого человека, пытаясь прикинуть и сообразить, кто же он, в сущности, такой?`,
        `Из соседней комнаты послышался короткий смешок; по звуку трудно было угадать, один там человек или их несколько.`,
        `Сейчас взгляну, что там за люди в соседней комнате.`,
        `Посмотрим, как фрау Грубах объяснит это вторжение.`,
        `Правда, он тут же подумал, что не стоило высказывать свои мысли вслух, – выходило так, будто этими словами он в какой-то мере признает за незнакомцем право надзора; впрочем, сейчас это было неважно.`,
        `И не останусь, и разговаривать с вами не желаю, пока вы не скажете, кто вы такой.`,
        `Зря обижаетесь, – сказал незнакомец и сам открыл дверь.`,
        `В соседней комнате, куда К. прошел медленнее, чем ему того хотелось, на первый взгляд со вчерашнего вечера почти ничего не изменилось.`,
        `Это была гостиная фрау Грубах, загроможденная мебелью, коврами, фарфором и фотографиями; пожалуй, в ней сейчас стало немного просторнее, хотя это не сразу было заметно, тем более что главная перемена заключалась в том, что там находился какой-то человек.`,
        `Разве Франц вам ничего не говорил?`,
        `В открытое окно видна была та старуха: в припадке старческого любопытства она уже перебежала к другому окну – посмотреть, что дальше.`,
        `Вот сейчас я спрошу фрау Грубах, – сказал К. И, хотя он стоял поодаль от тех двоих, но сделал движение, словно хотел вырваться у них из рук, и уже пошел было из комнаты.`,
        `Нет, – сказал человек у окна, бросил книжку на столе и встал: – Вам нельзя уходить.`,
        `Похоже на то, – сказал К. и добавил: – А за что?`,
        `Начало вашему делу положено, и в надлежащее время вы все узнаете.`,
        `Я и так нарушаю свои полномочия, разговаривая с вами по-дружески.`,
        `Но надеюсь, что, кроме Франца, никто нас не слышит, а он и сам вопреки всем предписаниям слишком любезен с вами.`,
        `Если вам и дальше так повезет, как повезло с назначением стражи, то можете быть спокойны.`,
        `К. хотел было сесть, но увидел что в комнате, кроме кресла у окна, сидеть не на чем.`,
        `Вы еще поймете – какие это верные слова, сказал Франц, и вдруг оба сразу подступили к нему.`,
        `Второй был много выше ростом, чем К. Он все похлопывал его по плечу.`,
        `Они стали ощупывать ночную рубашку К., приговаривая, что теперь ему придется надеть рубаху куда хуже, но эту рубашку и все остальное его белье они приберегут, и, если дело обернется в его пользу, ему все отдадут обратно.`,
        `Лучше отдайте вещи нам, чем на склад, – говорили они.`,
        `На складе вещи подменяют, а кроме того, через некоторое время все вещи распродают – все равно, окончилось дело или нет.`,
        `А вы знаете, как долго тянутся такие процессы, особенно в нынешнее время!`,
        `Конечно, склад вам в конце концов вернет стоимость вещей, но, во-первых, сама по себе сумма ничтожная, потому что при распродаже цену вещи назначают не по их стоимости, а за взятки, да и вырученные деньги тают, они ведь что ни год переходят из рук в руки.`,
        `Но К. даже не слушал, что ему говорят, ему не важно было, кто получит право распоряжаться его личными вещами, как будто еще принадлежавшими ему; гораздо важнее было уяснить свое положение; но в присутствии этих людей он даже думать как следует не мог: второй страж – кто же они были, как не стражи?`,
        `Ведь К. живет в правовом государстве, всюду царит мир, все законы незыблемы, кто же смеет нападать на него в его собственном жилище?`,
        `Всегда он был склонен относиться ко всему чрезвычайно легко, признавался, что дело плохо, только когда действительно становилось очень плохо, и привык ничего не предпринимать заранее, даже если надвигалась угроза.`,
        `Но сейчас ему показалось, что это неправильно, хотя все происходящее можно было почесть и за шутку, грубую шутку, которую неизвестно почему – может быть, потому, что сегодня ему исполнилось тридцать лет?`,
        `решили с ним сыграть коллеги по банку.`,
        `Да, конечно, это вполне вероятно; по-видимому, следовало бы просто рассмеяться в лицо этим стражам, и они рассмеялись бы вместе с ним; а может, это просто рассыльные, вполне похоже, но почему же тогда при первом взгляде на Франца он твердо решил ни в чем не уступать этим людям?`,
        `Меньше всего К. боялся, что его потом упрекнут в непонимании шуток, зато он отлично помнил – хотя обычно с прошлым опытом и не считался – некоторые случаи, сами по себе незначительные, когда он в отличие от своих друзей сознательно пренебрегал возможными последствиями и вел себя крайне необдуманно и неосторожно, за что и расплачивался полностью.`,
        `Больше этого с ним повториться не должно, хотя бы теперь, а если это комедия, то он им подыграет.`,
        `Позвольте – сказал он и быстро прошел мимо них в свою комнату.`,
        `Видно, разумный малый, – услышал он за спиной.`,
        `В комнате он тотчас же стал выдвигать ящики стола; там был образцовый порядок, но удостоверение личности, которое он искал, он от волнения никак найти не мог.`,
        `Наконец он нашел удостоверение на велосипед и уже хотел идти с ним к стражам, но потом эта бумажка показалась ему неубедительной, и он снова стал искать, пока не нашел свою метрику.`,
        `Когда он возвратился в соседнюю комнату, дверь напротив отворилась, и вышла фрау Грубах.`,
        `Но, увидев К., она остановилась в дверях, явно смутившись, извинилась и очень осторожно прикрыла двери.`,
        `Опять вы за свое, – сказал тот и обмакнул хлеб в баночку с медом.`,
        `Придется ответить,– сказал К. – Вот мои документы, а вы предъявите свои, и первым делом – ордер на арест.`,
        `Почему вы никак не можете примириться со своим положением?`,
        `Нет, вам непременно надо злить нас, и совершенно зря, ведь мы вам сейчас самые близкие люди на свете!`,
        `Вот именно, – сказал Франц, – можете мне поверить, – он посмотрел на К. долгим и, должно быть, многозначительным, но непонятным взглядом поверх чашки с кофе, которую держал в руке.`,
        `Неужто вы думаете, что ваш огромный, страшный процесс закончится скорее, если вы станете спорить с нами, с вашей охраной, о всяких документах, об ордерах на арест?`,
        `Мы – низшие чины, мы и в документах почти ничего не смыслим, наше дело – стеречь вас ежедневно по десять часов и получать за это жалованье.`,
        `К этому мы и приставлены, хотя, конечно, мы вполне можем понять, что высшие власти, которым мы подчиняемся, прежде чем отдать распоряжение об аресте, точно устанавливают и причину ареста, и личность арестованного.`,
        `Наше ведомство – насколько оно мне знакомо, хотя мне там знакомы только низшие чины, – никогда, по моим сведениям, само среди населения виновных не ищет: вина, как сказано в законе, сама притягивает к себе правосудие, и тогда властям приходится посылать нас, то есть стражу.`,
        `Да он и существует только у вас в голове, – сказал К. Ему очень хотелось как-нибудь проникнуть в мысли стражей, изменить их в свою пользу или самому проникнуться этими мыслями.`,
        `Ты совершенно прав, но ему ничего не объяснишь, – сказал тот.`,
        `К. больше не стал с ними разговаривать; неужели, подумал он, я дам сбить себя с толку болтовней этих низших чинов – они сами так себя называют.`,
        `И говорят они о вещах, в которых совсем ничего не смыслят.`,
        `А самоуверенность у них просто от глупости.`,
        `Стоит мне обменяться хотя бы двумя-тремя словами с человеком моего круга, и все станет несравненно понятнее, чем длиннейшие разговоры с этими двумя.`,
        `Он прошелся несколько раз по комнате, увидел, что старуха напротив уже притащила к окну еще более древнего старика и стоит с ним в обнимку.`,
        `Проведите меня к вашему начальству, – сказал он.`,
        `Не раньше, чем начальству будет угодно, – сказал страж, которого звали Виллем.`,
        `А теперь, – добавил он, – я вам советую пройти к себе в комнату и спокойно дожидаться, что с вами решат сделать.`,
        `И наш вам совет: не расходуйте силы на бесполезные рассуждения, лучше соберитесь с мыслями, потому что к вам предъявят большие требования.`,
        `Вы отнеслись к нам не так, как мы заслужили своим обращением, вы забыли, что, кем бы вы ни были, мы по крайней мере по сравнению с вами, люди свободные, а это немалое преимущество.`,
        `Однако, если у вас есть деньги, мы готовы принести вам завтрак из кафе напротив.`,
        `К. немного постоял, но на это предложение ничего не ответил.`,
        `Может быть, если он откроет дверь в соседнюю комнату или даже в прихожую, эти двое не посмеют его остановить; может быть, самое простое решение – пойти напролом?`,
        `Но ведь они могут его схватить, а если он потерпит такое унижение, тогда пропадет его превосходство над ними, которое он в некотором отношении еще сохранил.`,
        `Нет, лучше дождаться развязки – она должна прийти сама собой, в естественном ходе вещей; поэтому К. прошел к себе в комнату, не обменявшись больше со стражами ни единым словом.`,
        `Он бросился на кровать и взял с умывальника прекрасное яблоко – он припас его на завтрак еще с вечера.`,
        `Другого завтрака у него сейчас не было, и откусив большой кусок, он уверил себя, что это куда лучше, чем завтрак из грязного ночного кафе напротив, который он мог бы получить по милости своей стражи.`,
        `Он чувствовал себя хорошо и уверенно; правда, он на полдня опаздывал в банк, где служил, но при своей сравнительно высокой должности, какую он занимал, ему простят это опоздание.`,
        `Если же ему не поверят, чему он нисколько не удивится, то он сможет сослаться на фрау Грубах или на тех стариков напротив – сейчас они, наверно, уже переходят к другому своему окошку.`,
        `К. был удивлен, вернее, он удивлялся, становясь на точку зрения стражи: как это они прогнали его в другую комнату и оставили одного там, где он мог десятком способов покончить с собой?`,
        `Однако он тут же подумал, уже со своей точки зрения: какая же причина могла бы его на это толкнуть?`,
        `Неужели то, что рядом сидят двое и поедают его завтрак?`,
        `Покончить с собой было бы настолько бессмысленно, что при всем желании он не мог бы совершить такой бессмысленный поступок.`,
        `И если бы умственная ограниченность этих стражей не была столь очевидна, то можно было бы предположить, что и они пришли к такому же выводу и поэтому не видят никакой опасности в том, что оставили его одного.`,
        `Пусть бы теперь посмотрели, если им угодно, как он подходит к стенному шкафчику, где спрятан отличный коньяк, опрокидывает первую рюмку взамен завтрака, а потом и вторую – для храбрости, на тот случай, если храбрость понадобится, что, впрочем, маловероятно.`,
        `Но тут он так испугался окрика из соседней комнаты, что зубы лязгнули о стекло.`,
        `Но там его встретили оба стража и сразу, будто так было нужно, загнали обратно в его комнату.`,
        `Напали на человека в кровати, да еще ждут, что он будет во фраке!`,
        `Что ж, если этим можно ускорить дело, я не возражаю, – сказал К.,– сам открыл шкаф, долго рылся в своей многочисленной одежде, выбрал лучшую черную пару – она сидела так ловко, что вызывала прямо-таки восхищение знакомых, – достал свежую рубашку и стал одеваться со всей тщательностью.`,
        `Втайне он подумал, что больше задержек не будет – стража забыла даже заставить его принять ванну.`,
        `Он следил эа ними – а вдруг они все-таки вспомнят, но им, разумеется, и в голову это не пришло, хотя Виллем не забыл послать Франца к инспектору доложить, что К. уже одевается.`,
        `Когда он оделся окончательно, Виллем, идя за ним по пятам, провел его через пустую гостиную в следующую комнату, куда уже широко распахнули двери.`,
        `К. знал точно, что в этой комнате недавно поселилась некая фройляйн Бюрстнер, машинистка; она очень рано уходила на работу, поздно возвращалась домой, и К. только обменивался с ней обычными приветствиями.`,
        `Теперь ее ночной столик был выдвинут для допроса на середину комнаты, и за ним сидел инспектор.`,
        `Он скрестил ноги и закинул одну руку на спинку стула.`,
        `В углу комнаты стояли трое молодых людей – они разглядывали фотографии фройляйн Бюрстнер, воткнутые в плетеную циновку на стене.`,
        `На ручке открытого окна висела белая блузка.`,
        `В окно напротив уже высунулись те же старики, но зрителей там прибавилось: за их спинами возвышался огромный мужчина в раскрытой на груди рубахе, который все время крутил и вертел свою рыжеватую бородку.`,
        `Должно быть, вас очень удивили события сегодняшнего утра?`,
        `спросил инспектор и обеими руками пододвинул к себе немногие вещи, лежавшие на столике, – свечу со спичками, книжку, подушечку для булавок, как будто эти предметы были ему необходимы при опросе.`,
        `Конечно, – сказал К., и его охватило приятное чувство: наконец перед ним разумный человек, с которым можно поговорить о своих делах.`,
        `Конечно, я удивлен, но, впрочем, и не очень удивлен.`,
        `Возможно, что вы не так меня поняли, – заторопился К.– Я только хотел сказать… – Тут он осекся и стал искать, куда бы ему сесть.`,
        `Это не полагается, – ответил инспектор.`,
        `Я только хотел сказать, – продолжал К. без задержки, – что я, конечно, очень удивлен, но когда проживешь тридцать лет на свете, да еще если пришлось самому пробиваться в жизни, как приходилось мне, то поневоле привыкаешь ко всяким неожиданностям и не принимаешь их слишком близко к сердцу.`,
        `Нет, я не говорю, что все считаю шуткой, по-моему, для шутки это слишком далеко зашло.`,
        `Очевидно, в этом принимали участие все обитатели пансиона, да и все вы, а это уже переходит границы шутки.`,
        `Так что не думаю, чтоб это была просто шутка.`,
        `И правильно, – сказал инспектор и посмотрел, сколько спичек осталось в коробке.`,
        `Но, с другой стороны, – продолжал К., обращаясь ко всем присутствующим – ему хотелось привлечь внимание и тех троих, рассматривавших фотографии, – с другой стороны, особого значения все это иметь не может.`,
        `Вывожу я это из того, что меня в чем-то обвиняют, но ни малейшей вины я за собой не чувствую.`,
        `Но и это не имеет значения, главный вопрос – кто меня обвиняет?`,
        `Но на вас нет формы, если только ваш костюм, – тут он обратился к Францу, – не считать формой, но ведь это, скорее, дорожное платье.`,
        `Вот в этом вопросе я требую ясности, и я уверен, что после выяснения мы все расстанемся друзьями.`,
        `Тут инспектор со стуком положил спичечный коробок на стол.`,
        `Вы глубоко заблуждаетесь, – сказал он.`,
        `И эти господа, и я сам – все мы никакого касательства к вашему делу не имеем.`,
        `Больше того, мы о нем почти ничего не знаем.`,
        `Мы могли бы носить самую настоящую форму, и ваше дело от этого ничуть не ухудшилось бы.`,
        `Я даже не могу вам сказать, что вы в чем-то обвиняетесь, вернее, мне об этом ничего не известно.`,
        `Да, вы арестованы, это верно, но больше я ничего не знаю.`,
        `Может быть, вам стража чегонибудь наболтала, но все это пустая болтовня.`,
        `И хотя я не отвечаю на ваши вопросы, но могу вам посоветовать одно: поменьше думайте о нас и о том, что вас ждет, думайте лучше, как вам быть.`,
        `И не кричите вы так о своей невиновности, это нарушает то, в общем неплохое, впечатление, которое вы производите.`,
        `Вообще вам надо быть сдержаннее в разговорах.`,
        `Все, что вы тут наговорили, и без того было ясно из вашего поведения, даже если бы вы произнесли только два слова, а кроме того, все это вам на пользу не идет.`,
        `К. в недоумении смотрел на инспектора.`,
        `Его отчитывают, как школьника, и кто же?`,
        `За откровенность ему приходится выслушивать выговор!`,
        `А о причине ареста, о том, кто велел его арестовать, – ни слова!`,
        `Он даже разволновался, стал ходить взад и вперед по комнате, чему никто не препятствовал.`,
        `Прокурор Гастерер – мой давний друг,– сказал он.`,
        `Конечно, – ответил инспектор,– но я не знаю, какой в этом смысл, разве что вам надо переговорить с ним по личному делу.`,
        `воскликнул К. скорее озадаченно, чем сердито.`,
        `Ищете смысл, а творите такую бессмыслицу, что и не придумаешь.`,
        `Сначала эти господа на меня напали, а теперь расселись, стоят и глазеют всем скопом, как я пляшу под вашу дудку.`,
        `И еще спрашиваете, какой смысл звонить прокурору, когда мне сказано, что я арестован!`,
    ]
}

function randomShortSentences() {
    return [
        `Вы с ума сошли!`,
        `Не очень?`,
        `Мне можно сесть?`,
        `Хорошо, я не буду звонить!`,
        `Да тут камни возопят!`,
        `Да кто вы такой?`,
        `А я иначе и не думал.`,
        `Какой смысл?`,
        `Можно мне позвонить ему?`,
        `Человек, который, вероятно, моложе его!`,
        `Какое ведомство ведет дело?`,
        `Вы чиновники?`,
        `Что вам, наконец, нужно?`,
        `Ты кто такой?`,
        `Такого случая еще не бывало.`,
        `Вот еще новости!`,
        `Кто же эти люди?`,
        `О чем они говорят?`,
        `Из какого они ведомства?`,
        `Входите же!`,
        `Почему она не вошла?`,
        `Не разрешено, – сказал высокий.`,
        `Ведь вы арестованы.`,
        `То есть как – арестован?`,
        `Разве это так делается?`,
        `Чего вы хотите?`,
        `Но пока что он еще свободен.`,
        `Господи, твоя воля!`,
        `Да какое нам до них дело!`,
        `Таков закон.`,
        `Вас вызывают к инспектору!`,
        `Наконец-то!`,
        `В рубахе идти к инспектору!`,
        `Пустите меня черт побери!`,
        `Ничего не поделаешь!`,
        `Смешные церемонии!`,
        `Те покачали головой.`,
        `Не привести ли в оправдание истинную причину?`,
        `Мы не уполномочены давать объяснения.`,
        `Идите в свою комнату и ждите.`,
        `Мы на такие вопросы не отвечаем.`,
        `Право, вы ведете себя хуже ребенка.`,
        `Тут ошибок не бывает.`,
        `Где же тут могут быть ошибки?`,
        `Надо было прекратить это зрелище.`,
        `Он так и решил сделать.`,
        `Он и вас прикажет высечь, и нас тоже!`,
        `Нужен черный сюртук, – сказали они.`,
        `Особенно такие, как сегодня.`,
        `Почему особенно такие, как сегодня?`,
    ]
}


function somePile1() {
    return `
        Кто-то, по-видимому, оклеветал Йозефа К., потому что, не сделав ничего дурного, он попал под арест. Кухарка его квартирной хозяйки, фрау Грубах, ежедневно приносившая ему завтрак около восьми, на этот раз не явилась. Такого случая еще не бывало. К. немного подождал, поглядел с кровати на старуху, живущую напротив, – она смотрела из окна с каким-то необычным для нее любопытством – и потом, чувствуя и голод, и некоторое недоумение, позвонил. Тотчас же раздался стук, и в комнату вошел какой-то человек. К. никогда раньше в этой квартире его не видел. Он был худощав и вместе с тем крепко сбит, в хорошо пригнанном черном костюме, похожем на дорожное платье – столько на нем было разных вытачек, карманов, пряжек, пуговиц и сзади хлястик, – от этого костюм казался особенно практичным, хотя трудно было сразу сказать, для чего все это нужно.
        – Ты кто такой? – спросил К. и приподнялся на кровати.
        Но тот ничего не ответил, как будто его появление было в порядке вещей, и только спросил:
        – Вы звонили?
        – Пусть Анна принесет мне завтрак, – сказал К. и стал молча разглядывать этого человека, пытаясь прикинуть и сообразить, кто же он, в сущности, такой? Но тот не дал себя особенно рассматривать и, подойдя к двери, немного приоткрыл ее и сказал кому-то, очевидно стоявшему тут же, за порогом:
        – Он хочет, чтобы Анна подала ему завтрак.
        Из соседней комнаты послышался короткий смешок; по звуку трудно было угадать, один там человек или их несколько. И хотя незнакомец явно не мог услыхать ничего для себя нового, он заявил К. официальным тоном:
        – Это не положено!
        – Вот еще новости! – сказал К., соскочил с кровати и торопливо натянул брюки. – Сейчас взгляну, что там за люди в соседней комнате. Посмотрим, как фрау Грубах объяснит это вторжение.
        Правда, он тут же подумал, что не стоило высказывать свои мысли вслух, – выходило так, будто этими словами он в какой-то мере признает за незнакомцем право надзора; впрочем, сейчас это было неважно. Но видно, незнакомец так его и понял, потому что сразу сказал:
        – Может быть, вам лучше остаться тут?
        – И не останусь, и разговаривать с вами не желаю, пока вы не скажете, кто вы такой.
        – Зря обижаетесь, – сказал незнакомец и сам открыл дверь.
        В соседней комнате, куда К. прошел медленнее, чем ему того хотелось, на первый взгляд со вчерашнего вечера почти ничего не изменилось. Это была гостиная фрау Грубах, загроможденная мебелью, коврами, фарфором и фотографиями; пожалуй, в ней сейчас стало немного просторнее, хотя это не сразу было заметно, тем более что главная перемена заключалась в том, что там находился какой-то человек. Он сидел с книгой у открытого окна и сейчас, подняв глаза, сказал:
        – Вам следовало остаться у себя в комнате! Разве Франц вам ничего не говорил?
        – Что вам, наконец, нужно? – спросил К., переводя взгляд с нового посетителя на того, кого назвали Франц (он стоял в дверях), и снова на первого. В открытое окно видна была та старуха: в припадке старческого любопытства она уже перебежала к другому окну – посмотреть, что дальше.
        – Вот сейчас я спрошу фрау Грубах, – сказал К. И, хотя он стоял поодаль от тех двоих, но сделал движение, словно хотел вырваться у них из рук, и уже пошел было из комнаты.
        – Нет, – сказал человек у окна, бросил книжку на столе и встал: – Вам нельзя уходить. Ведь вы арестованы.
        – Похоже на то, – сказал К. и добавил: – А за что?
        – Мы не уполномочены давать объяснения. Идите в свою комнату и ждите. Начало вашему делу положено, и в надлежащее время вы все узнаете. Я и так нарушаю свои полномочия, разговаривая с вами по-дружески. Но надеюсь, что, кроме Франца, никто нас не слышит, а он и сам вопреки всем предписаниям слишком любезен с вами. Если вам и дальше так повезет, как повезло с назначением стражи, то можете быть спокойны.
        К. хотел было сесть, но увидел что в комнате, кроме кресла у окна, сидеть не на чем.
        – Вы еще поймете – какие это верные слова, сказал Франц, и вдруг оба сразу подступили к нему. Второй был много выше ростом, чем К. Он все похлопывал его по плечу. Они стали ощупывать ночную рубашку К., приговаривая, что теперь ему придется надеть рубаху куда хуже, но эту рубашку и все остальное его белье они приберегут, и, если дело обернется в его пользу, ему все отдадут обратно.
        – Лучше отдайте вещи нам, чем на склад, – говорили они. – На складе вещи подменяют, а кроме того, через некоторое время все вещи распродают – все равно, окончилось дело или нет. А вы знаете, как долго тянутся такие процессы, особенно в нынешнее время! Конечно, склад вам в конце концов вернет стоимость вещей, но, во-первых, сама по себе сумма ничтожная, потому что при распродаже цену вещи назначают не по их стоимости, а за взятки, да и вырученные деньги тают, они ведь что ни год переходят из рук в руки.
        Но К. даже не слушал, что ему говорят, ему не важно было, кто получит право распоряжаться его личными вещами, как будто еще принадлежавшими ему; гораздо важнее было уяснить свое положение; но в присутствии этих людей он даже думать как следует не мог: второй страж – кто же они были, как не стражи? – все время толкал его, как будто дружески, толстым животом, но когда К. подымал глаза, он видел совершенно не соответствующее этому толстому туловищу худое, костлявое лицо с крупным, свернутым набок носом и перехватывал взгляд, которым этот человек обменивался через его голову со своим товарищем. Кто же эти люди? О чем они говорят? Из какого они ведомства? Ведь К. живет в правовом государстве, всюду царит мир, все законы незыблемы, кто же смеет нападать на него в его собственном жилище? Всегда он был склонен относиться ко всему чрезвычайно легко, признавался, что дело плохо, только когда действительно становилось очень плохо, и привык ничего не предпринимать заранее, даже если надвигалась угроза. Но сейчас ему показалось, что это неправильно, хотя все происходящее можно было почесть и за шутку, грубую шутку, которую неизвестно почему – может быть, потому, что сегодня ему исполнилось тридцать лет? – решили с ним сыграть коллеги по банку. Да, конечно, это вполне вероятно; по-видимому, следовало бы просто рассмеяться в лицо этим стражам, и они рассмеялись бы вместе с ним; а может, это просто рассыльные, вполне похоже, но почему же тогда при первом взгляде на Франца он твердо решил ни в чем не уступать этим людям? Меньше всего К. боялся, что его потом упрекнут в непонимании шуток, зато он отлично помнил – хотя обычно с прошлым опытом и не считался – некоторые случаи, сами по себе незначительные, когда он в отличие от своих друзей сознательно пренебрегал возможными последствиями и вел себя крайне необдуманно и неосторожно, за что и расплачивался полностью. Больше этого с ним повториться не должно, хотя бы теперь, а если это комедия, то он им подыграет. Но пока что он еще свободен.
        – Позвольте – сказал он и быстро прошел мимо них в свою комнату.
        – Видно, разумный малый, – услышал он за спиной.
        В комнате он тотчас же стал выдвигать ящики стола; там был образцовый порядок, но удостоверение личности, которое он искал, он от волнения никак найти не мог. Наконец он нашел удостоверение на велосипед и уже хотел идти с ним к стражам, но потом эта бумажка показалась ему неубедительной, и он снова стал искать, пока не нашел свою метрику.
        Когда он возвратился в соседнюю комнату, дверь напротив отворилась, и вышла фрау Грубах. Но, увидев К., она остановилась в дверях, явно смутившись, извинилась и очень осторожно прикрыла двери.
        – Входите же! – только и успел сказать К.
        Сам он так и остался стоять посреди комнаты с бумагами в руках, глядя на дверь, которая не открывалась, и только возглас стражей заставил его вздрогнуть,– они сидели за столиком у открытого окна, и К. увидел, что они поглощают его завтрак.
        – Почему она не вошла? – спросил он.
        – Не разрешено, – сказал высокий. – Ведь вы арестованы.
        – То есть как – арестован? Разве это так делается?
        – Опять вы за свое, – сказал тот и обмакнул хлеб в баночку с медом. – Мы на такие вопросы не отвечаем.
        – Придется ответить,– сказал К. – Вот мои документы, а вы предъявите свои, и первым делом – ордер на арест.
        – Господи, твоя воля! – сказал высокий. – Почему вы никак не можете примириться со своим положением? Нет, вам непременно надо злить нас, и совершенно зря, ведь мы вам сейчас самые близкие люди на свете!
        – Вот именно, – сказал Франц, – можете мне поверить, – он посмотрел на К. долгим и, должно быть, многозначительным, но непонятным взглядом поверх чашки с кофе, которую держал в руке.
        Сам того не желая, К. ответил Францу таким же выразительным взглядом, но тут же хлопнул по своим документам и сказал:
        – Вот мои бумаги.
        – Да какое нам до них дело! – крикнул высокий. – Право, вы ведете себя хуже ребенка. Чего вы хотите? Неужто вы думаете, что ваш огромный, страшный процесс закончится скорее, если вы станете спорить с нами, с вашей охраной, о всяких документах, об ордерах на арест? Мы – низшие чины, мы и в документах почти ничего не смыслим, наше дело – стеречь вас ежедневно по десять часов и получать за это жалованье. К этому мы и приставлены, хотя, конечно, мы вполне можем понять, что высшие власти, которым мы подчиняемся, прежде чем отдать распоряжение об аресте, точно устанавливают и причину ареста, и личность арестованного. Тут ошибок не бывает. Наше ведомство – насколько оно мне знакомо, хотя мне там знакомы только низшие чины, – никогда, по моим сведениям, само среди населения виновных не ищет: вина, как сказано в законе, сама притягивает к себе правосудие, и тогда властям приходится посылать нас, то есть стражу. Таков закон. Где же тут могут быть ошибки?
        – Не знаю я такого закона, – сказал К.
        – Тем хуже для вас, – сказал высокий.
        – Да он и существует только у вас в голове, – сказал К. Ему очень хотелось как-нибудь проникнуть в мысли стражей, изменить их в свою пользу или самому проникнуться этими мыслями. Но высокий только отрывисто сказал:
        – Вы его почувствуете на себе.
        Тут вмешался Франц:
        – Вот видишь, Виллем, он признался, что не знает закона, а сам при этом утверждает, что невиновен.
        – Ты совершенно прав, но ему ничего не объяснишь, – сказал тот.
        К. больше не стал с ними разговаривать; неужели, подумал он, я дам сбить себя с толку болтовней этих низших чинов – они сами так себя называют. И говорят они о вещах, в которых совсем ничего не смыслят. А самоуверенность у них просто от глупости. Стоит мне обменяться хотя бы двумя-тремя словами с человеком моего круга, и все станет несравненно понятнее, чем длиннейшие разговоры с этими двумя. Он прошелся несколько раз по комнате, увидел, что старуха напротив уже притащила к окну еще более древнего старика и стоит с ним в обнимку. Надо было прекратить это зрелище.
        – Проведите меня к вашему начальству, – сказал он.
        – Не раньше, чем начальству будет угодно, – сказал страж, которого звали Виллем. – А теперь, – добавил он, – я вам советую пройти к себе в комнату и спокойно дожидаться, что с вами решат сделать. И наш вам совет: не расходуйте силы на бесполезные рассуждения, лучше соберитесь с мыслями, потому что к вам предъявят большие требования. Вы отнеслись к нам не так, как мы заслужили своим обращением, вы забыли, что, кем бы вы ни были, мы по крайней мере по сравнению с вами, люди свободные, а это немалое преимущество. Однако, если у вас есть деньги, мы готовы принести вам завтрак из кафе напротив.
        К. немного постоял, но на это предложение ничего не ответил. Может быть, если он откроет дверь в соседнюю комнату или даже в прихожую, эти двое не посмеют его остановить; может быть, самое простое решение – пойти напролом? Но ведь они могут его схватить, а если он потерпит такое унижение, тогда пропадет его превосходство над ними, которое он в некотором отношении еще сохранил. Нет, лучше дождаться развязки – она должна прийти сама собой, в естественном ходе вещей; поэтому К. прошел к себе в комнату, не обменявшись больше со стражами ни единым словом.
        Он бросился на кровать и взял с умывальника прекрасное яблоко – он припас его на завтрак еще с вечера. Другого завтрака у него сейчас не было, и откусив большой кусок, он уверил себя, что это куда лучше, чем завтрак из грязного ночного кафе напротив, который он мог бы получить по милости своей стражи. Он чувствовал себя хорошо и уверенно; правда, он на полдня опаздывал в банк, где служил, но при своей сравнительно высокой должности, какую он занимал, ему простят это опоздание. Не привести ли в оправдание истинную причину? Он так и решил сделать. Если же ему не поверят, чему он нисколько не удивится, то он сможет сослаться на фрау Грубах или на тех стариков напротив – сейчас они, наверно, уже переходят к другому своему окошку. К. был удивлен, вернее, он удивлялся, становясь на точку зрения стражи: как это они прогнали его в другую комнату и оставили одного там, где он мог десятком способов покончить с собой? Однако он тут же подумал, уже со своей точки зрения: какая же причина могла бы его на это толкнуть? Неужели то, что рядом сидят двое и поедают его завтрак? Покончить с собой было бы настолько бессмысленно, что при всем желании он не мог бы совершить такой бессмысленный поступок. И если бы умственная ограниченность этих стражей не была столь очевидна, то можно было бы предположить, что и они пришли к такому же выводу и поэтому не видят никакой опасности в том, что оставили его одного. Пусть бы теперь посмотрели, если им угодно, как он подходит к стенному шкафчику, где спрятан отличный коньяк, опрокидывает первую рюмку взамен завтрака, а потом и вторую – для храбрости, на тот случай, если храбрость понадобится, что, впрочем, маловероятно.
        Но тут он так испугался окрика из соседней комнаты, что зубы лязгнули о стекло.
        – Вас вызывают к инспектору! – крикнули оттуда.        
        – Наконец-то! – крикнул он, запер стенной шкафчик и побежал в гостиную. Но там его встретили оба стража и сразу, будто так было нужно, загнали обратно в его комнату.
        – Вы с ума сошли! – крикнули они. В рубахе идти к инспектору! Он и вас прикажет высечь, и нас тоже!
        – Пустите меня черт побери! – крикнул К., которого уже оттеснили к самому гардеробу. – Напали на человека в кровати, да еще ждут, что он будет во фраке!
        – Ничего не поделаешь! – сказали оба; всякий раз, когда К. подымал крик, они становились не только совсем спокойными, но даже какими-то грустными, что очень сбивало его с толку, но отчасти и успокаивало.
        – Смешные церемонии! – буркнул он, но сам уже снял пиджак со стула и подержал в руках, словно предоставляя стражам решать, подходит ли он.
        Те покачали головой.
        – Нужен черный сюртук, – сказали они.
        К. бросил пиджак на пол и сказал, сам не зная, в каком смысле он это говорит:
        – Но ведь дело сейчас не слушается?
        Стражи ухмыльнулись, но упрямо повторили:
        – Нужен черный сюртук.
        – Что ж, если этим можно ускорить дело, я не возражаю, – сказал К.,– сам открыл шкаф, долго рылся в своей многочисленной одежде, выбрал лучшую черную пару – она сидела так ловко, что вызывала прямо-таки восхищение знакомых, – достал свежую рубашку и стал одеваться со всей тщательностью. Втайне он подумал, что больше задержек не будет – стража забыла даже заставить его принять ванну. Он следил эа ними – а вдруг они все-таки вспомнят, но им, разумеется, и в голову это не пришло, хотя Виллем не забыл послать Франца к инспектору доложить, что К. уже одевается.
        Когда он оделся окончательно, Виллем, идя за ним по пятам, провел его через пустую гостиную в следующую комнату, куда уже широко распахнули двери. К. знал точно, что в этой комнате недавно поселилась некая фройляйн Бюрстнер, машинистка; она очень рано уходила на работу, поздно возвращалась домой, и К. только обменивался с ней обычными приветствиями. Теперь ее ночной столик был выдвинут для допроса на середину комнаты, и за ним сидел инспектор. Он скрестил ноги и закинул одну руку на спинку стула.
        В углу комнаты стояли трое молодых людей – они разглядывали фотографии фройляйн Бюрстнер, воткнутые в плетеную циновку на стене. На ручке открытого окна висела белая блузка. В окно напротив уже высунулись те же старики, но зрителей там прибавилось: за их спинами возвышался огромный мужчина в раскрытой на груди рубахе, который все время крутил и вертел свою рыжеватую бородку.
        – Йозеф К.? – спросил инспектор, должно быть, только для того, чтобы обратить на себя рассеянный взгляд К.
        К. наклонил голову.
        – Должно быть, вас очень удивили события сегодняшнего утра? – спросил инспектор и обеими руками пододвинул к себе немногие вещи, лежавшие на столике, – свечу со спичками, книжку, подушечку для булавок, как будто эти предметы были ему необходимы при опросе.
        – Конечно, – сказал К., и его охватило приятное чувство: наконец перед ним разумный человек, с которым можно поговорить о своих делах. – Конечно, я удивлен, но, впрочем, и не очень удивлен.
        – Не очень? – переспросил инспектор и, передвинув свечу на середину столика, начал расставлять вокруг нее остальные вещи.
        – Возможно, что вы не так меня поняли, – заторопился К.– Я только хотел сказать… – Тут он осекся и стал искать, куда бы ему сесть. – Мне можно сесть? – спросил он.
        – Это не полагается, – ответил инспектор.
        – Я только хотел сказать, – продолжал К. без задержки, – что я, конечно, очень удивлен, но когда проживешь тридцать лет на свете, да еще если пришлось самому пробиваться в жизни, как приходилось мне, то поневоле привыкаешь ко всяким неожиданностям и не принимаешь их слишком близко к сердцу. Особенно такие, как сегодня.
        – Почему особенно такие, как сегодня?
        – Нет, я не говорю, что все считаю шуткой, по-моему, для шутки это слишком далеко зашло. Очевидно, в этом принимали участие все обитатели пансиона, да и все вы, а это уже переходит границы шутки. Так что не думаю, чтоб это была просто шутка.
        – И правильно, – сказал инспектор и посмотрел, сколько спичек осталось в коробке.
        – Но, с другой стороны, – продолжал К., обращаясь ко всем присутствующим – ему хотелось привлечь внимание и тех троих, рассматривавших фотографии, – с другой стороны, особого значения все это иметь не может. Вывожу я это из того, что меня в чем-то обвиняют, но ни малейшей вины я за собой не чувствую. Но и это не имеет значения, главный вопрос – кто меня обвиняет? Какое ведомство ведет дело? Вы чиновники? Но на вас нет формы, если только ваш костюм, – тут он обратился к Францу, – не считать формой, но ведь это, скорее, дорожное платье. Вот в этом вопросе я требую ясности, и я уверен, что после выяснения мы все расстанемся друзьями.
        Тут инспектор со стуком положил спичечный коробок на стол.
        – Вы глубоко заблуждаетесь, – сказал он. – И эти господа, и я сам – все мы никакого касательства к вашему делу не имеем. Больше того, мы о нем почти ничего не знаем. Мы могли бы носить самую настоящую форму, и ваше дело от этого ничуть не ухудшилось бы. Я даже не могу вам сказать, что вы в чем-то обвиняетесь, вернее, мне об этом ничего не известно. Да, вы арестованы, это верно, но больше я ничего не знаю. Может быть, вам стража чегонибудь наболтала, но все это пустая болтовня. И хотя я не отвечаю на ваши вопросы, но могу вам посоветовать одно: поменьше думайте о нас и о том, что вас ждет, думайте лучше, как вам быть. И не кричите вы так о своей невиновности, это нарушает то, в общем неплохое, впечатление, которое вы производите. Вообще вам надо быть сдержаннее в разговорах. Все, что вы тут наговорили, и без того было ясно из вашего поведения, даже если бы вы произнесли только два слова, а кроме того, все это вам на пользу не идет.
        К. в недоумении смотрел на инспектора. Его отчитывают, как школьника, и кто же? Человек, который, вероятно, моложе его! За откровенность ему приходится выслушивать выговор! А о причине ареста, о том, кто велел его арестовать, – ни слова! Он даже разволновался, стал ходить взад и вперед по комнате, чему никто не препятствовал. Сдвинул под рукав манжеты, поправил манишку, пригладил волосы, сказал, проходя мимо трех молодых людей: «Какая бессмыслица!», на что те обернулись к нему и сочувственно, хотя и строго, посмотрели на него, и наконец остановился перед столиком инспектора.
        – Прокурор Гастерер – мой давний друг,– сказал он. – Можно мне позвонить ему?
        – Конечно, – ответил инспектор,– но я не знаю, какой в этом смысл, разве что вам надо переговорить с ним по личному делу.
        – Какой смысл? – воскликнул К. скорее озадаченно, чем сердито. Да кто вы такой? Ищете смысл, а творите такую бессмыслицу, что и не придумаешь. Да тут камни возопят! Сначала эти господа на меня напали, а теперь расселись, стоят и глазеют всем скопом, как я пляшу под вашу дудку. И еще спрашиваете, какой смысл звонить прокурору, когда мне сказано, что я арестован! Хорошо, я не буду звонить!
    `
}























//                    const columnNames = keys(rows[0])
//                    const expectedColumnConfiguration = columnNames.join(', ')
//                    rows.forEach((row, i) => {
//                        const actualColumnConfiguration = keys(row).join(', ')
//                        if (actualColumnConfiguration !== expectedColumnConfiguration) raise(`Inconsistent column configuration in row ${i}`)
//                    })
//                    
//                    let sql = `insert into "${table}" (`
//                    if (!columnNames.includes('inserted_at')) sql += 'inserted_at, '
//                    if (!columnNames.includes('updated_at')) sql += 'updated_at, '
//                    sql += columnNames.join(', ')
//                    sql += ') values '
//                    rows.forEach((row, rowIndex) => {
//                        if (rowIndex > 0) sql += ', '
//                        sql += '('
//                        
//                        sql += ')'
//                    })
                        
                    