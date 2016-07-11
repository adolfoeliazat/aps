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
                
                else if (msg.fun === 'danger_setUpTestUsers') {
                    #await dangerouslyKillUser({email: 'todd@test.shit.ua'})
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
                    // TODO:vgrechka Do single insert of multiple rows
                    if (!rows) {
                        rows = [values]
                    }
                    
                    let id
                    for (const rowValues of rows) {
                        rowValues = cloneDeep(rowValues)
                        if (imposedNextID) {
                            rowValues.id = imposedNextID
                            imposedNextID = undefined
                        }
                        
                        let sql = `insert into "${table}"(inserted_at, updated_at`
                        const args = [requestTimestamp, requestTimestamp]
                        for (const [k, v] of toPairs(rowValues)) {
                            sql += `, "${k}"`
                            args.push(v)
                        }
                        sql += ') values(' + range(keys(rowValues).length + 2).map(x => '$' + (x + 1)).join(', ') + ') returning id'
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

function generateLoremSupportThreads(count) {
    const rows = []
    range(count).forEach(_=> {
        const year = random(2014, 2016)
        const month = prependZero(random(1, 12))
        const day = prependZero(random(1, 25))
        const hour = prependZero(random(0, 23))
        const minute = prependZero(random(0, 59))
        const second = prependZero(random(0, 59))
        const stamp = `${year}-${month}-${day} ${hour}:${minute}:${second}`
        const supportee_id = randomItem(testdata.customerAndWriterUserIDs)
        rows.push({inserted_at: stamp, updated_at: stamp, supportee_id, topic: randomSentence()})
    })
        
    return deepInspect(rows)
    
    
    function prependZero(x) {
        x = '' + x
        if (x.length === 1) {
            x = '0' + x
        }
        return x
    }
}

let randomSentences

function randomSentence() {
    if (!randomSentences) {
        randomSentences = []
        const pile = somePile()
        let start = 0
        const sentenceEndRe = /[.?!]+/g
        let match
        while (match = sentenceEndRe.exec(pile)) {
            if (/\s/.test(pile[match.index - 2])) continue
            let sent = pile.slice(start, match.index + 1).trim()
            if (sent[0] === '-' || sent[0] === '–') {
                sent = sent.slice(1).trim()
            }
            randomSentences.push(sent)
            start = match.index + match[0].length
        }
    }
    
    return randomItem(randomSentences)
    
    function somePile() {
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
        `
    }
}






















