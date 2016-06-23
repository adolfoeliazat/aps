/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

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
    dlog({body: req.body, headers: req.headers})
    handle().then(message => {
        res.json(message)
    })
    
    async function handle() {
        const msg = req.body
        const t = makeT(msg.lang)
        
        try {
            if (msg.fun === 'signIn') {
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
                
                return {hunky: 'dory'}
                
                
                function logFailure(reason) {
                    clog('Sign-in failed: ' + reason)
                }
                
                function invalidEmailOrPasswordMessage() {
                    return {error: t('Invalid email or password', 'Неверная почта или пароль')}
                }
            } else if (msg.fun === 'signUp') {
                if (!msg.agreeTerms) return {
                    error: t('You have to agree with terms and conditions', 'Необходимо принять соглашение'),
                    fieldErrors: {
                        agreeTerms: true
                    }}
                return {error: 'implement me'}
            }
            
            return {error: 'WTF is the rpc function?'}
        } catch (fucked) {
            clog('/rpc handle() is fucked up', fucked.stack)
            try {
                return {error: t('Sorry, service is temporarily unavailable', 'Извините, сервис временно недоступен')}
            } catch (reallyFucked) {
                clog('/rpc handle() is so fucked up, it cannot even complain in localized fashion', reallyFucked.stack)
                return {error: 'As a backend, I am really fucked up'}
            }
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
        
    const con = await pgPool.connect()
    try {
        // dlog({sql, args})
        const res = await con.query(sql, args)
        // dlog({res})
        return res.rows
    } finally {
        con.release()
    }
}

