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
import static 'into-u ./stuff'

const app = newExpress()
let mailTransport, sentEmails = [], fixedNextGeneratedPassword, queryLogForUI = []

app.post('/rpc', (req, res) => {
    // dlog({body: req.body, headers: req.headers})
    handle().then(message => {
        res.json(message)
    })
    
    async function handle() {
        const msg = req.body
        const _t = makeT(msg.LANG)
        function t(meta, ...args) {
            return {meta, meat: _t(...args)}
        }
        
        let stackBeforeAwait, awaitRes
        
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
                    const rows = #await tx.query({$tag: 'cb833ae1-19da-459e-a638-da4c9e7266cc', shouldLogForUI: false}, q`
                        select users.id user_id, * from users, user_tokens
                        where user_tokens.token = ${msg.token} and users.id = user_tokens.user_id`)
                    if (!rows.length) {
                        raise('Invalid token')
                    }
                    user = rows[0]
                    user.id = user.user_id // To tell users.id from user_tokens.id it's selected additionaly as `user_id`
                    failOnClientUserMismatch()
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
                
                else if (msg.fun === 'danger_getSentEmails') {
                    return sentEmails
                }
                
                else if (msg.fun === 'danger_killUser') {
                    #await tx.query({$tag: 'e9622700-e408-4bf9-a3cd-434ddf6fb11b'}, q`
                        delete from user_tokens where user_id = (select id from users where email = ${msg.email})`)
                    #await tx.query({$tag: 'c3cf9e53-2a4e-4423-8869-09a8c7078257'}, q`
                        delete from users where email = ${msg.email}`)
                    return hunkyDory()
                }
                
                else if (msg.fun === 'danger_fixNextGeneratedPassword') {
                    fixedNextGeneratedPassword = msg.password
                    return hunkyDory()
                }
                
                else if (msg.fun === 'danger_openSourceCode') {
                    let file, offset
                    if (msg.$tag) {
                        for (file of ['E:/work/aps/aps/src/client.ts', 'E:/work/aps/aps/src/backend.ts']) {
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
                    
                    return hunkyDory({user: pick(user, 'id', 'first_name', 'last_name', 'state'), token})
                    
                    
                    function logFailure(reason) {
                        clog('Sign-in failed: ' + reason)
                    }
                    
                    function invalidEmailOrPasswordMessage() {
                        return {error: t('Invalid email or password', 'Неверная почта или пароль')}
                    }
                }
                
                else if (msg.fun === 'private_getUserInfo') {
                    return hunkyDory({user: pick(user, 'id', 'first_name', 'last_name', 'state')})
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
                                insert into users (email,           kind,               lang,        state,                password_hash,                   first_name,          last_name)
                                            values(${fields.email}, ${msg.CLIENT_KIND}, ${msg.LANG}, ${'profile-pending'}, ${await hashPassword(password)}, ${fields.firstName}, ${fields.lastName})`)
                            
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
                    const phone = sanitizeString(msg.phone)
                    if (isBlank(phone)) {
                        fieldErrors.phone = t('TOTE', 'Телефон обязателен')
                    } else {
                        fieldErrors.phone = validatePhone(phone)
                    }
//                    } else if (!isValidPhone(phone)) {
//                        fieldErrors.phone = t('TOTE', 'Странный телефон какой-то')
//                    }

                    if (isEmpty(fieldErrors)) {
                        #await tx.query({$tag: '492b9099-44c3-497b-a403-09abd2090be8'}, q`
                            update users set profile_updated_at = now() at time zone 'utc',
                                             phone = ${phone}
                                   where id = ${user.id}`)
                        return hunkyDory()
                    }
                    return youFixErrors()
                }
                
                const situation = `WTF is the RPC function ${msg.fun}?`
                clog(situation)
                return {fatal: situation}
                
                // @ctx handle helpers
                
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
                            _default: t('TOTE', 'Поле обязательно'),
                        })
                        
                        const maxlen = {
                            email: 50,
                            firstName: 50,
                            lastName: 50,
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
            })
            
        } catch (fucked) {
            const situation = `/rpc handle() is fucked up: ${fucked.stack}`
            clog(situation)
            if (stackBeforeAwait) {
                clog(`Stack before await: ${stackBeforeAwait}`)
            }
            return {fatal: situation, stack: fucked.stack, stackBeforeAwait} // TODO:vgrechka Send stack only if debug mode
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



























