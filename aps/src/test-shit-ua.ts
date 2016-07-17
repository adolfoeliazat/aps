/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import * as fs from 'fs'
import * as path from 'path'
#import static 'into-u/utils'

let createDB, pgConnection, Random, simulateRequest, q, imposeNextIDs, imposeRequestTimestamp,
    relog
    
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
    ;({createDB, pgConnection, simulateRequest, q, imposeNextIDs, imposeRequestTimestamp} = ctx)
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


export function g1() {
    resetRandom()
    const count = 45
    const stamps = testdata.generateStampsInRange({count, fromStamp: '2014-03-31 18:15:41', toStamp: '2016-05-15 20:21:22'})
    const users = testdata.ua.generateRandomCustomersOrWriters({count})
    const topics = shuffle(random, testdata.ua.supportThreadTopics.slice(0, count))
    const messages = shuffle(random, testdata.ua.supportThreadMessages.slice(0, count))
    return zip(stamps, users, topics, messages)
          .map(([stamp, user, topic, message]) => ({stamp, user, topic, message}))
          .map(x => `newSupportThreadEvent(${JSON.stringify(x)}),`).join('\n')
}

export function generateRandomCustomersOrWriters({count}) {
    const users = testdata.ua.users.customer.concat(testdata.ua.users.writer)
    return times(count, _=> {
        const user = randomItem(random, users).user
        return user.email.slice(0, user.email.indexOf('@'))
    })
}

export async function createTestTemplateUA1DB() {
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
                for (const role of u.roles) {
                    #await insertInto({table: 'user_roles', values: {user_id: u.user.id, role}})
                    #await insertInto({table: 'user_tokens', values: {user_id: u.user.id, token: 'temp-' + u.user.id}})
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
            #await simNewSupportThread({stamp: '2014-05-06 07:08:09', upon: 'pisya', topic: 'Some topic', message: 'Some message'})
            
            mtEvents.dlog('END')
            
            #await query(q`delete from user_tokens`)
            
            
            
            async function simNewSupportThread({stamp, upon, topic, message}) {
                imposeRequestTimestamp(stamp)
                imposeNextIDs([nextID('support_threads'), nextID('support_thread_messages')])
                const user = userFromPon(upon)
                #await req({LANG: 'ua', CLIENT_KIND: user.clientKind, token: user.token, fun: 'private_createSupportThread', topic, message})
            }
            
//            raise('nooooooooooo')
//            for (let eventIndex = 0; eventIndex < 100; ++eventIndex) {
//                const events = [
//                    
//                    async function adminAssignedToSupportThread() {
//                        const threads = #await query(`select * from support_threads where id < 100000 and supporter_id is null`)
//                        if (!threads.length) return dlog('Skipping event cause there’s no threads needing assignment')
//                        const thread = randomItem(random, threads)
//                        const user = randomSupporter()
//                        const res = #await req({LANG: 'ua', CLIENT_KIND: user.clientKind, token: user.token,
//                            fun: 'private_takeSupportThread', id: thread.id})
//                    },
//                    
//                    async function newSupportThreadMessage() {
//                        const threads = #await query(`select * from support_threads where id < 100000 and supporter_id is null`)
//                        if (!threads.length) return dlog('Skipping event cause there’s no threads yet')
//                        const thread = randomItem(random, threads)
//                        const message = nextMessage()
//                        imposeNextIDs([nextID('support_thread_messages')])
//                        const userOptions = [userFromID(thread.supportee_id)]
//                        if (thread.supporter_id) {
//                            userOptions.push(userFromID(thread.supporter_id))
//                        }
//                        const {clientKind, token} = randomItem(random, userOptions)
//                        const res = #await req({LANG: 'ua', CLIENT_KIND: clientKind, token,
//                            fun: 'private_createSupportThreadMessage', message, containerID: thread.id})
//                    }
//                ]
//                
//                imposeRequestTimestamp(nextStamp())
//                const event = randomItem(eventRandom, events)
//                await event()
//            }
            
            
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




