const askerName = 'Sailor'
relog(heyBackend_sayHelloToMe({askerName}))
heyBackend_changeYourStateTo({foo: 10, bar: 20})
relog('Its state is', heyBackend_whatsYourState())


relog(await pgQuery(`select * from users`))

relog(await pgQuery(`select * from users where email = $1`, ['toor']))

relog(await pgQuery(`insert into users(email, hash, firstName, lastName) values('fred-apstest@mailinator.com', 'qwe', 'zzz', 'yyyy')`))

relog(uuid())

pgQuery(`insert into users(email, hash, firstName, lastName) values($1, $2, $3, $4)`,
        ['foo', 'bar', 'baz', 'qux'])
relog('ok')


var nodemailer = require('nodemailer');
var transporter = nodemailer.createTransport({
    host: '127.0.0.1',
    port: 2525,
    auth: {
        user: 'into/mail',
        pass: 'bigsecret'
    }
})
var mailOptions = {
    from: 'APS <noreply@aps.local>',
    to: 'wilma.blue@test.shit',
    subject: 'Hi there, honey',
    html: '<b>Wat’s up, Wilma?</b><hr>And what’s down?'
}
relog(await new Promise((resolve, reject) => {
    transporter.sendMail(mailOptions, function (error, info) {
        if (error) {
            return reject(error)
            // return relog('ERROR', error)
        }
        resolve('Message sent: ' + info.response)
    })
}))


relog(decodeQuotedPrintable('=D0=9F=D1=80=D0=B8=D0=B2=D0=B5=D1=82, Wilma!<br><br>'))


const file = 'E:/work/aps/aps/src/client.ts'
let code = fs.readFileSync(file, 'utf8')
relog('len = ' + code.length)
relog(code.slice(0, 6697).indexOf(undefined))
relog('-------------')
relog('--------------')
relog(`                field.setError = field.setError || (x => field.control.error = x)
                
                if (field.titleControl === undefined && field.title) {
                    field.titleControl = label(field.title)
                }
`.indexOf(undefined))
//for (let i = 0; i < code.length; ++i) {
//    if (i >= 1 && code[i] === '\n' && code[i-1] !== '\r') {
//        relog(i + '  ')
//    }
//}



const p = new Promise((resolve, reject) => {})
relog(p instanceof Promise)
const f = async function() {}
const q = f()
relog(q instanceof Promise)








































