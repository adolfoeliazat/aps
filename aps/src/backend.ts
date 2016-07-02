/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

#pragma instrument-ui-rendering

MAX_NAME = 50

require('regenerator-runtime/runtime')
require('source-map-support').install()
import * as fs from 'fs'
import static 'into-u ./stuff'

const app = newExpress()
let mailTransport, sentEmails = [], fixedNextGeneratedPassword

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
            const fieldErrors = {}
            let user
            
            if (msg.fun.startsWith('danger_') || msg.isTesting) {
                const serverToken = process.env.APS_DANGEROUS_TOKEN
                if (!serverToken) raise('I want APS_DANGEROUS_TOKEN configured on server')
                const clientToken = msg.APS_DANGEROUS_TOKEN
                if (clientToken !== serverToken) raise('Fuck you, mister hacker')
            }
        
            const clientProtocol = msg.isTesting ? 'http' : 'https'
            const [clientDomain, clientPortSuffix] = {
                ua_customer: [DOMAIN_UA_CUSTOMER, PORT_SUFFIX_UA_CUSTOMER],
                ua_writer: [DOMAIN_UA_WRITER, PORT_SUFFIX_UA_WRITER],
                en_customer: [DOMAIN_EN_CUSTOMER, PORT_SUFFIX_EN_CUSTOMER],
                en_writer: [DOMAIN_EN_WRITER, PORT_SUFFIX_EN_WRITER],
            }[msg.LANG + '_' + msg.CLIENT_KIND] || [undefined, undefined]
            if (!clientDomain && msg.CLIENT_KIND !== 'devenv') raise('WTF is the clientKind?')
            
            
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
                await pgQuery(`delete from users where email = $1`, [msg.email])
                return hunkyDory()
            }
            
            else if (msg.fun === 'danger_fixNextGeneratedPassword') {
                fixedNextGeneratedPassword = msg.password
                return hunkyDory()
            }
            
            else if (msg.fun === 'danger_openSourceCode') {
                let file, offset
                if (msg.$tag) {
                    for (file of ['E:/work/aps/aps/src/client.ts', 'E:/work/aps/aps/src/client.ts']) {
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
                        'aps/src/backend.ts': 'E:/work/aps/aps/src/backend.ts',
                        'backend.ts': 'E:/work/aps/aps/src/backend.ts',
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
            
            else if (msg.fun === 'signIn') {
                const rows = await pgQuery('select * from users where email = $1', [msg.email])
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
                
                return hunkyDory({user: pick(user, 'id', 'first_name', 'last_name')})
                
                
                function logFailure(reason) {
                    clog('Sign-in failed: ' + reason)
                }
                
                function invalidEmailOrPasswordMessage() {
                    return {error: t('Invalid email or password', 'Неверная почта или пароль')}
                }
            }
            
            else if (msg.fun === 'signUp') {
                if (!msg.agreeTerms) {
                    fieldErrors.agreeTerms = t('You have to agree with terms and conditions', 'Необходимо принять соглашение')
                }
                
                const email = sanitizeString(msg.email)
                if (isBlank(email)) {
                    fieldErrors.email = t('Email is mandatory', 'Почта обязательна')
                } else if (!isValidEmail(email)) {
                    fieldErrors.email = t('Weird kind of email', 'Интересная почта какая-то')
                }
                
                const firstName = sanitizeString(msg.firstName)
                if (isBlank(firstName)) {
                    fieldErrors.firstName = t('First name is mandatory', 'Имя обязательно')
                } else if (firstName.length > MAX_NAME) {
                    fieldErrors.firstName = t(`No more than ${MAX_NAME} symbols`, `Не более ${MAX_NAME} символов`)
                }
                
                const lastName = sanitizeString(msg.lastName)
                if (isBlank(lastName)) {
                    fieldErrors.lastName = t('Last name is mandatory', 'Фамилия обязательна')
                } else if (lastName.length > MAX_NAME) {
                    fieldErrors.lastName = t(`No more than ${MAX_NAME} symbols`, `Не более ${MAX_NAME} символов`)
                }
                
                if (isEmpty(fieldErrors)) {
                    try {
                        let password = uuid()
                        
                        if (fixedNextGeneratedPassword) {
                            password = fixedNextGeneratedPassword
                            fixedNextGeneratedPassword = undefined
                        }
                
                        await pgQuery(`insert into users(email, kind, lang, state, password_hash, first_name, last_name) values($1, $2, $3, $4, $5, $6, $7)`,
                                      [email, msg.CLIENT_KIND, msg.LANG, 'cool', await hashPassword(password), firstName, lastName])
                        
                        const signInURL = `http://${clientDomain}${clientPortSuffix}/sign-in.html`
                            
                        let subject
                        if (msg.LANG === 'ua' && msg.CLIENT_KIND === 'customer') {
                            subject = 'Пароль для APS'
                        } else if (msg.LANG === 'ua' && msg.CLIENT_KIND === 'writer') {
                            subject = 'Пароль для Writer UA'
                        }
                        if (!subject) raise(`Implement mail subject for the ${clientKindDescr()}`)
                        
                        #await sendEmail({
                            to: `${firstName} ${lastName} <${email}>`,
                            subject,
                            html: dedent(_t({
                                en: `
                                    TODO
                                `,
                                ua: `
                                    Привет, ${firstName}!<br><br>
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
            
            const situation = `WTF is the RPC function ${msg.fun}?`
            clog(situation)
            return {fatal: situation}
            
            
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
                if (msg.isTesting) {
                    sentEmails.push(it)
                    return
                }
                
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
        pgPool.connect((conerr, con, doneWithConnection) => {
            if (conerr) {
                clog('PG connection failed', conerr)
                doneWithConnection(conerr)
                return rejectPgTransaction(conerr)
            }
            
            const api = {
                query(sql, args) {
                    // If args is bad, con.query fails without telling us
                    invariant(args === undefined || isArray(args), 'tx.query wants args to be array or undefined')
                    
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
            }
            
            doInTransaction(api)
                .then(ditres => {
                    doneWithConnection()
                    resolvePgTransaction(ditres)
                })
                .catch(diterr => {
                    doneWithConnection()
                    rejectPgTransaction(diterr)
                })
        })
    })
}

async function pgQuery(sql, args) {
    if (!pgPool) {
        pgPool = new (require('pg').Pool)({
            database: 'aps',
            port: 5432,
            user: 'aps',
            password: 'apssecret',
        })
    }
    
    // If args is bad, con.query fails without telling us
    invariant(args === undefined || isArray(args), 'pgQuery wants args to be array or undefined')
    
    return await new Promise((resolve, reject) => {
        pgPool.connect((err, con, done) => {
            if (err) {
                clog('PG connection failed', err)
                done(err)
                return reject(err)
            }

            con.query(sql, args, (err, res) => {
                done()
                if (err) {
                    clog('PG query failed', {sql, args, err})
                    return reject(err)
                }
                resolve(res.rows)
            })
        })
    })
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

function sanitizeString(s) {
    if (typeof s !== 'string') raise('Fuck you with you hacky request')
    return s.trim()
}
