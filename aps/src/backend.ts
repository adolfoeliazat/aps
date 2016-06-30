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
let mailTransport, sentMails = [], fixedNextGeneratedConfirmationCode, fixedNextGeneratedPassword

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
        
        try {
            const fieldErrors = {}
            
            if (msg.fun.startsWith('danger_') || msg.isTesting) {
                const serverToken = process.env.APS_DANGEROUS_TOKEN
                if (!serverToken) raise('I want APS_DANGEROUS_TOKEN configured on server')
                const clientToken = msg.APS_DANGEROUS_TOKEN
                if (clientToken !== serverToken) raise('Fuck you, mister hacker')
            }
        
            const clientProtocol = msg.isTesting ? 'http' : 'https'
            const [clientDomain, clientPortSuffix] = {
                uaCustomer: [DOMAIN_UA_CUSTOMER, PORT_SUFFIX_UA_CUSTOMER],
                uaWriter: [DOMAIN_UA_WRITER, PORT_SUFFIX_UA_WRITER],
                enCustomer: [DOMAIN_EN_CUSTOMER, PORT_SUFFIX_EN_CUSTOMER],
                enWriter: [DOMAIN_EN_WRITER, PORT_SUFFIX_EN_WRITER],
            }[msg.CLIENT_KIND] || [undefined, undefined]
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
            
            else if (msg.fun === 'danger_clearSentMails') {
                sentMails = []
                return hunkyDory()
            }
            
            else if (msg.fun === 'danger_getSentMails') {
                return sentMails
            }
            
            else if (msg.fun === 'danger_killUser') {
                await pgQuery(`delete from users where email = $1`, [msg.email])
                return hunkyDory()
            }
            
            else if (msg.fun === 'danger_fixNextGeneratedPassword') {
                fixedNextGeneratedPassword = msg.password
                return hunkyDory()
            }
            
            else if (msg.fun === 'danger_fixNextGeneratedConfirmationCode') {
                fixedNextGeneratedConfirmationCode = msg.code
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
                    if (!~openBracket || !~closingBracket) raise('I want brackets')
                    const filePart = msg.$sourceLocation.slice(0, openBracket)
                    file = {
                        'aps/src/client.ts': 'E:/work/aps/aps/src/client.ts',
                        'aps/src/backend.ts': 'E:/work/aps/aps/src/backend.ts',
                    }[filePart]
                    if (!file) return {error: 'Weird file in source location'}
                    offset = parseInt(msg.$sourceLocation.slice(openBracket + 1, closingBracket))
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
                const user = rows[0]
                if (!(await comparePassword(msg.password, user.hash))) {
                    logFailure('Invalid password for existing email')
                    return invalidEmailOrPasswordMessage()
                }
                
                return hunkyDory()
                
                
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
                        let confirmationCode = uuid()
                        
                        if (fixedNextGeneratedPassword) {
                            password = fixedNextGeneratedPassword
                            fixedNextGeneratedPassword = undefined
                        }
                        if (fixedNextGeneratedConfirmationCode) {
                            confirmationCode = fixedNextGeneratedConfirmationCode
                            fixedNextGeneratedConfirmationCode = undefined
                        }
                
                        await pgQuery(`insert into users(email, state, passwordHash, confirmationCode, firstName, lastName) values($1, $2, $3, $4, $5, $6)`,
                                      [email, 'awaitingConfirmation', await hashPassword(password), confirmationCode, firstName, lastName])
                        
                        const confirmationLink = `${clientProtocol}://${clientDomain}${clientPortSuffix}/confirm-sign-up.html?code=${encodeURIComponent(confirmationCode)}`
                        
                        await sendMail({
                            to: `${firstName} ${lastName} <${email}>`,
                            subject: _t('APS Sign Up Confirmation', 'Подтверждение регистрации в APS'),
                            html: dedent(_t({
                                en: `
                                    TODO
                                `,
                                ua: `
                                    Привет, ${firstName}!<br><br>
                                    Для подтверждения регистрации перейди по этой ссылке:
                                    <a href="${confirmationLink}">${confirmationLink}</a>
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
            
            else if (msg.fun === 'confirmSignUp') {
                return await pgTransaction(async function(tx) {
                    const rows = await tx.query('select * from users where confirmationCode = $1', [msg.code])
                    if (!rows.length) {
                        return {error: t('Wrong code', 'Неверный код'), errorCode: 'wrong-code'}
                    }

                    return hunkyDory()
                })
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
            
            function hunkyDory() {
                return {hunky: 'dory'}
            }
            
            async function sendMail(it) { // TODO:vgrechka @refactor Extract to foundation/utils-server
                if (msg.isTesting) {
                    sentMails.push(it)
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
                
                const mail = asn({
                    from: `APS <noreply@${clientDomain}>`,
                    it
                })
                await new Promise((resolve, reject) => {
                    mailTransport.sendMail(mail, (err, res) => {
                        if (err) {
                            return reject(err)
                        }
                        resolve('Message sent: ' + res.response)
                    })
                })
            }
            
        } catch (fucked) {
            const situation = `/rpc handle() is fucked up: ${fucked.stack}`
            clog(situation)
            return {fatal: situation}
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
