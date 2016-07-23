//
// Create and populate test-template-ua-1 database
//
const ts = require('./test-shit-ua')
ts.setScrapContext({relog})
ts.setBackendContext({createDB, pgConnection, simulateRequest, q, imposeNextIDs, imposeRequestTimestamp, resetImposed})
await ts.createTestTemplateUA1DB()
relog('ok')


//
// Extract sentences from kafka-trial-ru.txt
//
const ts = require('./test-shit-ua')
ts.setScrapContext({relog})
ts.extractSentences({fromFile: 'testdata/kafka-trial-ru.txt', toFile: 'testdata/kafka-trial-ru.gen.json'})
relog('ok')


//
// Fiddle with testDB
//
relog(await testDB.query('select * from user_tokens'))


//
// Fiddle with testTemplateUA1DB
//
relog(await testTemplateUA1DB.query('select * from support_threads'))

//
// seenBy
//
await testTemplateUA1DB.query(`update support_thread_messages set data = '{"seenBy": {"302": "some time"}}' where id = 432`)
relog(await testTemplateUA1DB.query(`select * from support_threads where id = 12`))
relog(await testTemplateUA1DB.query(`select * from support_thread_messages where id = 432`))
relog(await testTemplateUA1DB.query(`select count(*) from support_thread_messages where sender_id = 302`))
//
relog('---')
const user = {id: '302'}
relog(await testTemplateUA1DB.query(q`select count(*) from support_thread_messages where sender_id = ${user.id} and data->'seenBy'->${user.id} is null`))


const ts = require('./test-shit-ua')
relog(ts.toUponLines(ts.generateRandomCustomersOrWriters({count: 45})))


const ts = require('./test-shit-ua')
relog(ts.generateStampsInRange({count: 45, fromStamp: '2014-03-31 15:10:34', toStamp: '2016-05-07 23:40:15', minDiffSeconds: 60*60}))


testdata.extractSentences({fromFile: 'testdata/kafka-trial-ru.txt', toFile: 'testdata/sents-kafka-trial-ru.txt'})
relog('ok')

relog(testdata.ua.g1())

relog(testdata.generateStampsInRange({count: 45, fromStamp: '2014-03-31 18:15:41', toStamp: '2016-07-15 20:21:22'}))


relog(await testdb.query(q`select * from users`))
relog('ok')
               
await shutDownPool('test')
               
relog(2 + 3)

await pgTransaction({DB: 'test'}, async function(tx) {
    relog(await tx.query({$tag: 'f97c636a-7c94-40b6-8ba6-7f6f3a2616ac'}, `select now()`))
})


try {
    const res = await handle({fun: 'danger_shitIntoDatabase', DB: 'test', DANGEROUS_TOKEN: process.env.APS_DANGEROUS_TOKEN, CLIENT_KIND: 'debug'})
    if (res.fatal) {
        relog(res.fatal.split('\n').slice(0, 1).join('\n'))
        section('Handle stack'); relog(res.stack)
        section('Handle stack before await'); relog(res.stackBeforeAwait)
        return
    }
    ;
    relog(res)
    section('Users')
    // relog(await testQuery(q`select * from users`))
    // relog(await testQuery(q`select * from support_threads`))
} catch (e) {
    section('My stack'); relog(e.stack)
    section('My stack before await'); relog(stackBeforeAwait)
}
;
function section(title) {
    relog(`\n-------------- ${title} ----------------\n`)
}


await testQuery('commit')
await testQuery('create database foobar1')
relog('ok')

const random = new Random(Random.engines.mt19937().seed(123123))
for (let i = 0; i < 10000; ++i) {
    random.integer(0, 100)
}
relog('ok')


relog(testdata.ua.messages.filter(x => x.length > 300))


relog(findConflict(testdata.ua.supportThreads))
;
function findConflict(items) {
    const pastParsedItems = []
    for (let i = 0; i < items.length; ++i) {
        const parsedItem = {}
        let s = items[i].toLowerCase().replace(/\.|,|\?|!/g, '')
        const words = s.split(/\s+/).filter(x => x.length)
        parsedItem.uniqueWords = uniq(words)
        for (let j = 0; j < pastParsedItems.length; ++j) {
            const error = conflicts(parsedItem, pastParsedItems[j])
            if (error) {
                return `${i} conflicts with ${j}: ${error}\n`
                     + `${i}: ${items[i]}\n`
                     + `${j}: ${items[j]}`
            }
        }
        pastParsedItems.push(parsedItem)
    }
    return '--- All good ---'
    ;
    function conflicts(pi1, pi2) {
        const commonWords = intersection(pi1.uniqueWords, pi2.uniqueWords)
        if (commonWords.length >= 3) return `Too many common words: ${commonWords.join(', ')}`
    }
}

await testQuery(`insert into foobar (foo) values ('aaaaaaa'); insert into foobar (foo) values ('bbbbbbb');`)
relog(await testQuery('select * from foobar'))

relog(run(function gen1() {
    const random = new Random(Random.engines.mt19937().seed(123123))
    const ids = {}
    const stampFormat = 'YYYY-MM-DD HH:mm:ss'
    let nextMoment = moment('2014-03-31 18:15:41', stampFormat)
    ;
    //range(30).forEach(_=> relog(nextStamp()))
    //return
    ;
    ;
    let sql = ''
    range(3).forEach(function generateEvent() {
        const eventType = randomItem(random, ['newSupportThread', 'newSupportThreadMessage'])
        if (eventType === 'newSupportThread') {
            sql += `insert into support_threads(id, inserted_at, updated_at, topic, supportee_id, supporter_id);`
        } else if (eventType === 'newSupportThreadMessage') {
            
        }
    })
    return sql
    ;
    ;
    function nextID(name) {
        if (!ids[name]) {
            ids[name] = 100
        }
        return ++ids[name]
    }
    ;
    function nextStamp() {
        nextMoment.add(random.integer(5 * 60, 5 * 24 * 60 * 60), 'seconds')
        return nextMoment.format(stampFormat)
    }
}))


const askerName = 'Sailor'
relog(heyBackend_sayHelloToMe({askerName}))
heyBackend_changeYourStateTo({foo: 10, bar: 20})
relog('Its state is', heyBackend_whatsYourState())




relog(generateRandomShitForDatabase())

relog(randomShortSentences().length)

relog(randomLongSentences().length)

relog(extractRandomSentences(somePile1))

relog(generateRandomSupportThreads({threadCount: 50}))

relog(generateRandomSupportThreadMessages({threadCount: 50}))


relog(await tx.query({$tag: '03bb8d7f-09e5-4591-98ef-7f4bf6282bef'}, q`insert into foobar(foo) values(${'hello world'}) returning id`))


relog(await tx.query({$tag: '03bb8d7f-09e5-4591-98ef-7f4bf6282bef'}, q`
    select * from users where id in (${2}, ${78})`))


relog(await testPGQuery({$tag: '03bb8d7f-09e5-4591-98ef-7f4bf6282bef'}, `select * from users`))

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


const phone = '1231212'
const user = {name: 'Joe', id: 54}
relog(q`update users set profile_updated_at = now() at time zone 'utc', phone = ${phone} where id = ${user.id}`)


relog(moment.tz('UTC').format('YYYY-MM-DD HH:mm:ss.SSSSSS'))

relog(range(5).map(x => '$' + (x + 1)).join(', '))


relog(last)


relog(defaults({foo: 10}, {bar: 20, baz: 30}))

relog(capitalize('fooBarBaz'))































