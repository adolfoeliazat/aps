/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

MAX_NAME = 50

require('regenerator-runtime/runtime')
require('source-map-support').install()
import express = require('express')
import static 'into-u ./stuff'

const app = express()
var bodyParser = require('body-parser')
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: true }))
app.use((req, res, next) => {
    res.header('Access-Control-Allow-Origin', '*')
    res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept, Cache-Control, Expires, APS-Token')
    next()
})

app.post('/rpc', (req, res) => {
    // dlog({body: req.body, headers: req.headers})
    handle().then(message => {
        res.json(message)
    })
    
    async function handle() {
        const msg = req.body
        const t = makeT(msg.lang)
        
        try {
            const fieldErrors = {}
            
            if (msg.fun === 'eval') {
                checkDangerousToken()
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
                    return {err: e.stack}
                }
            }
            
            else if (msg.fun === 'killWilma') {
                checkDangerousToken()
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
                
                const password = uuid()
                
                if (isEmpty(fieldErrors)) {
                    try {
                        await pgQuery(`insert into users(email, hash, firstName, lastName) values($1, $2, $3, $4)`,
                                [email, await hashPassword(password), firstName, lastName])
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
            
            function hunkyDory() {
                return {hunky: 'dory'}
            }
            
            function checkDangerousToken() {
                const serverToken = process.env.APS_DANGEROUS_TOKEN
                if (!serverToken) raise('I want APS_DANGEROUS_TOKEN configured on server')
                const clientToken = msg.APS_DANGEROUS_TOKEN
                if (clientToken !== serverToken) raise('Fuck you, mister hacker')
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
