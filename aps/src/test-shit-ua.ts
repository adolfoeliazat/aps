/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import * as fs from 'fs'
import * as path from 'path'
#import static 'into-u/utils'

let createDB, pgConnection, Random, simulateRequest, q, imposeNextIDs, imposeRequestTimestamp, resetImposed,
    relog,
    random
    
const users = {
    admin: [
        {user: {id: 101, kind: 'admin', first_name: 'Тодд', last_name: 'Суппортод', email: 'todd@test.shit.ua',},
         roles: ['support']},
        {user: {id: 102, kind: 'admin', first_name: 'Алиса', last_name: 'Планктоновна', email: 'alice@test.shit.ua',},
         roles: ['support']},
        {user: {id: 103, kind: 'admin', first_name: 'Элеанора', last_name: 'Суконская', email: 'eleanor@test.shit.ua',},
         roles: ['support']},
    ],
    
    writer: [
        {user: {id: 201, first_name: 'Франц', last_name: 'Кафка', email: 'kafka@test.shit.ua',}},
        {user: {id: 202, first_name: 'Лев', last_name: 'Толстой', email: 'leo@test.shit.ua',}},
        {user: {id: 203, first_name: 'Николай', last_name: 'Гоголь', email: 'gogol@test.shit.ua',}},
        {user: {id: 204, first_name: 'Федор', last_name: 'Достоевский', email: 'fedor@test.shit.ua',}},
        {user: {id: 205, first_name: 'Александр', last_name: 'Пушкин', email: 'pushkin@test.shit.ua',}},
        {user: {id: 206, first_name: 'Георг', last_name: 'Гегель', email: 'hegel@test.shit.ua',}},
        {user: {id: 207, first_name: 'Иммануил', last_name: 'Кант', email: 'kant@test.shit.ua',}},
        {user: {id: 208, first_name: 'Мигель', last_name: 'Сервантес', email: 'miguel@test.shit.ua',}},
        {user: {id: 209, first_name: 'Карлос', last_name: 'Кастанеда', email: 'carlos@test.shit.ua',}},
        {user: {id: 210, first_name: 'Елена', last_name: 'Блаватская', email: 'blava@test.shit.ua',}},
        {user: {id: 211, first_name: 'Джейн', last_name: 'Остин', email: 'jane@test.shit.ua',}},
        {user: {id: 212, first_name: 'Мэри', last_name: 'Шелли', email: 'mary@test.shit.ua',}},
        {user: {id: 213, first_name: 'Франсуаза', last_name: 'Саган', email: 'francoise@test.shit.ua',}},
        {user: {id: 214, first_name: 'Жорж', last_name: 'Санд', email: 'sand@test.shit.ua',}},
        {user: {id: 215, first_name: 'Агата', last_name: 'Кристи', email: 'agatha@test.shit.ua',}},
        // {user: {id: 20, first_name: '', last_name: '', email: '@test.shit.ua',}},
    ],
    
    customer: [
        {user: {id: 301, first_name: 'Пися', last_name: 'Камушкин', email: 'pisya@test.shit.ua',}},
        {user: {id: 302, first_name: 'Люк', last_name: 'Хуюк', email: 'luke@test.shit.ua',}},
        {user: {id: 303, first_name: 'Павло', last_name: 'Зибров', email: 'zibrov@test.shit.ua',}},
        {user: {id: 304, first_name: 'Василий', last_name: 'Теркин', email: 'terkin@test.shit.ua',}},
        {user: {id: 305, first_name: 'Иво', last_name: 'Бобул', email: 'ivo@test.shit.ua',}},
        {user: {id: 306, first_name: 'Регина', last_name: 'Дубовицкая', email: 'regina@test.shit.ua',}},
        {user: {id: 307, first_name: 'Евгений', last_name: 'Ваганович', email: 'vaganovich@test.shit.ua',}},
        {user: {id: 308, first_name: 'Павел', last_name: 'Дристальский', email: 'paul@test.shit.ua',}},
        {user: {id: 309, first_name: 'Тело', last_name: 'Странное', email: 'telo@test.shit.ua',}},
        {user: {id: 310, first_name: 'Арчибальд', last_name: 'Нелеподлиннаяфамилияуменя', email: 'archie@test.shit.ua',}},
        {user: {id: 311, first_name: 'Даздраперма', last_name: 'Дивизионная', email: 'perma@test.shit.ua',}},
        {user: {id: 312, first_name: 'Уменяреальносамыйдлинный', last_name: 'Ымя', email: 'ymya@test.shit.ua',}},
        {user: {id: 313, first_name: 'Варсоновий', last_name: 'Оптинский', email: 'varso@test.shit.ua',}},
        {user: {id: 314, first_name: 'Евстафий', last_name: 'Антиохийский', email: 'anti@test.shit.ua',}},
        {user: {id: 315, first_name: 'Ксенофонт', last_name: 'Тутанский', email: 'xeno@test.shit.ua',}},
        // {user: {id: 30, first_name: '', last_name: '', email: '@test.shit.ua',}},
    ],
}

export function setBackendContext(ctx) {
    ;({createDB, pgConnection, simulateRequest, q, imposeNextIDs, imposeRequestTimestamp, resetImposed} = ctx)
}

export function setScrapContext(ctx) {
    ;({relog} = ctx)
}

export function extractSentences({fromFile, toFile}) {
    const random = new Random(Random.engines.mt19937().seed(545644))
    
    const sents = []
    {
        const pile = fs.readFileSync(`${path.dirname(__dirname)}/src/${fromFile}`, 'utf8')
        const sentendre = /[.?!]+/g
        const goodchare = /^[ .,\-?!:;()"a-zA-Zа-яА-Я]$/
        let match, start = 0
        scan: while (match = sentendre.exec(pile)) {
            if (/\s/.test(pile[match.index - 2])) continue
            let sent = pile.slice(start, match.index + match[0].length)
            sent = sent.replace(/\s+/g, ' ').trim()
            const chars = sent.split('')
            for (const c of chars) {
                if (!goodchare.test(c)) raise(`Bad char: [${c}] (code ${c.charCodeAt(0)})`)
            }
            if (sent.startsWith('-')) sent = sent.slice(1).trim()
            
            try {
                if (/[A-ZА-Я]{2,}/.test(sent)) continue scan
                if (/^[a-zа-я]/.test(sent)) continue scan
                if (sent.length < 25) continue scan
                // sent = sent.replace(/([^a-zA-Zа-яА-Я])-([^a-zA-Zа-яА-Я])/g, '$1–$2')
                sents.push(sent)
                // if (sents.length === 20) break
            } finally {
                start = match.index + match[0].length
            }
        }
    }
    
    const randomParas = []
    {
        const randomParaCount = 500, minParaSents = 3, maxParaSents = 7
        let start = 0
        while (randomParas.length < randomParaCount) {
            const count = random.integer(minParaSents, maxParaSents)
            if (start + count - 1 > sents.length - 1) raise('Out of sentences for random paras')
            randomParas.push(sents.slice(start, start + count).join(' '))
            start += count
        }
    }
    
    const out = {
        sentsSortedByLenght: sortBy(sents, x => x.length),
        randomParasSortedByLength: sortBy(randomParas, x => x.length),
    }
    
    const outs = JSON.stringify(out).replace(/,"/g, '\n,"')
    // relog(outs.slice(0, 300))
    fs.writeFileSync(`${path.dirname(__dirname)}/src/${toFile}`, outs)
}

export function generateStampsInRange({count, fromStamp, toStamp, minDiffSeconds}) {
    resetRandom()
    const stampFormat = 'YYYY-MM-DD HH:mm:ss'
    const unixStampFrom = moment(fromStamp, stampFormat)
    const unixStampTo = moment(toStamp, stampFormat)
    let unixStamps = []
    times(count * 2, _=> {
        if (unixStamps.length === count) return
        const unixStamp = random.integer(unixStampFrom, unixStampTo)
        for (const existing of unixStamps) {
            if (Math.abs(existing - unixStamp) < minDiffSeconds) return
        }
        unixStamps.push(unixStamp)
    })
    
    if (unixStamps.length < count) raise('generateStampsInRange: failed to generate enough stuff')
    
    unixStamps = sortBy(unixStamps)
    return unixStamps.map(unixStamp => moment(unixStamp).format(stampFormat))
}

function resetRandom() {
    random = new Random(Random.engines.mt19937().seed(54511))
}

export function generateRandomCustomersOrWriters({count}) {
    const random = new Random(Random.engines.mt19937().seed(54512))
    
    const items = [].concat(users.customer, users.writer)
    return times(count, _=> {
        const item = randomItem(random, items)
        return item.user.email.slice(0, item.user.email.indexOf('@'))
    })
}

export function toUponLines(items) {
    let lines = items.map(x => `upon: '${x}'`)
    let maxLen = max(lines.map(x => x.length))
    lines = lines.map(x => pad(x, maxLen - x.length) + ',')
    return lines.join('\n')
}


export async function createTestTemplateUA1DB() {
    resetImposed()
    await createDB('test-template-ua-1')
    await pgConnection({db: 'test-template-ua-1'}, async function(db) {
        let stackBeforeAwait
        try {
            const random = new Random(Random.engines.mt19937().seed(123123))
            const eventRandom = new Random(Random.engines.mt19937().seed(234234))
            const password_hash = '$2a$10$x5bq4zVvcyTb2oUb5.fhreJfl/2NqsaH3TcAwm/C1apAazlBJX2t6' // secret
            const stampFormat = 'YYYY-MM-DD HH:mm:ss'
            let nextMoment = moment('2014-03-31 18:15:41', stampFormat)
            const nextIDs = {}
            let nextSupportThreadTopicIndex = 0
            let nextMessageIndex = 0
            
            let mt
            mt = measureTime('Creating users')
            for (const u of users.admin) {
                #await insertInto({table: 'users', values: asn({kind: 'admin', lang: 'ua', state: 'cool', password_hash}, u.user)})
                #await insertInto({table: 'user_tokens', values: {user_id: u.user.id, token: 'temp-' + u.user.id}})
                for (const role of u.roles) {
                    #await insertInto({table: 'user_roles', values: {user_id: u.user.id, role}})
                }
            }
            for (const u of users.writer) {
                #await insertInto({table: 'users', values: asn({kind: 'writer', lang: 'ua', state: 'cool', password_hash}, u.user)})
                #await insertInto({table: 'user_tokens', values: {user_id: u.user.id, token: 'temp-' + u.user.id}})
            }
            for (const u of users.customer) {
                #await insertInto({table: 'users', values: asn({kind: 'customer', lang: 'ua', state: 'cool', password_hash}, u.user)})
                #await insertInto({table: 'user_tokens', values: {user_id: u.user.id, token: 'temp-' + u.user.id}})
            }
            mt.dlog('END')
            
            const mtEvents = measureTime('Events')
            
            //---------- Support threads and messages ----------
            let threadID
            #await simNewSupportThread({stamp: '2014-04-10 13:44:55', upon: 'luke', topic: 'И это называется следственной документацией!', message: 'Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.'})
            #await simNewSupportThread({stamp: '2014-05-06 14:33:34', upon: 'perma', topic: 'В   углу   комнаты   стояли  трое  молодых  людей  -  они разглядывали фотографии фройляйн Бюрстнер, воткнутые в плетеную циновку на стене. На ручке открытого окна висела белая  блузка.', message: 'В  окно  напротив уже высунулись те же старики, но зрителей там прибавилось:  за  их  спинами  возвышался  огромный  мужчина  в раскрытой  на  груди  рубахе, который все время крутил и вертел свою рыжеватую бородку. - Йозеф К.? - спросил инспектор, должно быть,  только  для того, чтобы обратить на себя рассеянный взгляд К.'})
            
//            #await simNewSupportThread({stamp: '2014-05-16 23:33:15', upon: 'kafka', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2014-05-21 07:11:59', upon: 'jane', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2014-05-26 07:51:24', upon: 'sand', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2014-06-13 11:19:32', upon: 'terkin', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2014-07-16 05:24:17', upon: 'luke', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2014-07-17 07:33:08', upon: 'telo', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2014-08-06 01:33:37', upon: 'fedor', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2014-08-15 10:32:42', upon: 'luke', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2014-10-11 05:15:11', upon: 'fedor', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2014-10-23 15:10:13', upon: 'sand', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2014-11-25 03:39:53', upon: 'fedor', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2014-12-13 06:52:03', upon: 'varso', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2014-12-18 05:55:41', upon: 'miguel', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2014-12-28 05:48:46', upon: 'zibrov', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-01-01 22:28:49', upon: 'hegel', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-01-04 05:42:21', upon: 'francoise', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-01-26 07:25:44', upon: 'miguel', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-02-24 13:39:50', upon: 'blava', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-03-04 01:10:51', upon: 'jane', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-04-23 05:24:50', upon: 'kant', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-05-20 10:13:30', upon: 'regina', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-05-24 22:32:28', upon: 'perma', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-06-03 22:25:52', upon: 'kant', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-06-29 11:21:13', upon: 'miguel', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-07-01 06:30:24', upon: 'varso', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-07-03 17:41:52', upon: 'carlos', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-07-13 03:40:40', upon: 'blava', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-08-05 20:02:57', upon: 'perma', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-08-07 04:57:37', upon: 'terkin', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-08-08 02:31:35', upon: 'regina', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-08-17 20:42:27', upon: 'varso', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-09-19 12:12:19', upon: 'archie', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-09-27 17:25:54', upon: 'mary', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-10-19 17:39:21', upon: 'paul', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-10-30 18:39:37', upon: 'varso', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-12-01 20:05:53', upon: 'francoise', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-12-05 18:25:20', upon: 'archie', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-12-07 03:10:09', upon: 'telo', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-12-17 17:36:39', upon: 'ivo', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2015-12-31 19:35:03', upon: 'varso', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2016-02-17 08:05:43', upon: 'leo', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2016-03-01 09:22:47', upon: 'hegel', topic: '', message: ''})
//            #await simNewSupportThread({stamp: '2016-04-13 18:28:06', upon: 'kant', topic: '', message: ''})

            mtEvents.dlog('END')
            
            #await query(q`delete from user_tokens`)
            
            
            async function simNewSupportThread({stamp, upon, topic, message}) {
                imposeRequestTimestamp(stamp)
                threadID = nextID('support_threads')
                imposeNextIDs([threadID, nextID('support_thread_messages')])
                const user = userFromPon(upon)
                #await req({LANG: 'ua', CLIENT_KIND: user.clientKind, token: user.token, fun: 'private_createSupportThread', topic, message})
            }
            
            async function query(opts) {
                return await db.query({$tag: 'd0aa115c-dd8c-46c8-ae47-bff3f5906512'}, opts)
            }
            
            async function insertInto(opts) {
                return await db.insertInto({$tag: '6eb80cdd-8b9f-4857-937a-f5828dd6ed71'}, opts)
            }
            
            async function req(msg) {
                requestTimeLoggingDisabled = true // TODO:vgrechka @fix
                try {
                    return await simulateRequest(asn({db: 'test-template-ua-1', LANG: 'ua'}, msg))
                } finally {
                    requestTimeLoggingDisabled = false
                }
            }
            
            function nextMessage() {
                if (nextMessageIndex > testdata.ua.messages.length - 1) raise('Out of messages')
                return testdata.ua.messages[nextMessageIndex++]
            }
            
            function nextStamp() {
                nextMoment.add(random.integer(30 * 60, 5 * 24 * 60 * 60), 'seconds')
                return nextMoment.format(stampFormat)
            }
            
            function randomCustomer() {
                return userFromID(randomItem(random, testdata.ua.users.customer).user.id)
            }
            
            function randomWriter() {
                return userFromID(randomItem(random, testdata.ua.users.writer).user.id)
            }
            
            function randomCustomerOrWriter() {
                if (random.integer(0, 1) === 0) return randomCustomer()
                else return randomWriter()
            }
            
            function randomSupporter() {
                const admins = testdata.ua.users.admin
                const supporters = admins.filter(x => x.roles.includes('support'))
                return userFromID(randomItem(random, supporters).user.id)
            }
            
            function userFromPon(upon) {
                const item = [].concat(users.admin, users.customer, users.writer).find(x => x.user.email.startsWith(`${upon}@`))
                if (!item) raise(`Cannot find user for upon: ${upon}`)
                return userFromID(item.user.id)
            }
            
            function userFromID(id) {
                id = parseInt(id, 10)
                let clientKind
                if (id >= 100 && id < 300) clientKind = 'writer'
                else if (id >= 300 && id < 400) clientKind = 'customer'
                else raise(`Weird ID for a test user: ${id}`)
                return {clientKind, token: 'temp-' + id}
            }
            
            function nextID(table) {
                if (!nextIDs[table]) {
                    nextIDs[table] = 101
                }
                const id = nextIDs[table]++
                if (id >= 100000) raise(`Out of IDs for table ${table}`)
                return id
            }
        } catch (e) {
            e.stackBeforeAwait = stackBeforeAwait
            throw e
        }
    })
}




