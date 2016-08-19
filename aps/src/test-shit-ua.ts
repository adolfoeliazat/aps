/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import * as fs from 'fs'
import * as path from 'path'
import {pgConnection, q, imposeNextIDs, imposeRequestTimestamp, resetImposed} from './backend'
import {createDB as createUADB} from './schema-ua'
#import static 'into-u/utils'

let simulateRequest, random

export function setBackendContext(ctx) {
    ;({simulateRequest} = ctx)
}
    
const users = {
    admin: [
        {user: {id: 101, kind: 'admin', first_name: 'Дася', last_name: 'Админовна', email: 'dasja@test.shit.ua',},
         roles: ['support']},
        {user: {id: 102, kind: 'admin', first_name: 'Тодд', last_name: 'Суппортод', email: 'todd@test.shit.ua',},
         roles: ['support']},
        {user: {id: 103, kind: 'admin', first_name: 'Алиса', last_name: 'Планктоновна', email: 'alice@test.shit.ua',},
         roles: ['support']},
        {user: {id: 104, kind: 'admin', first_name: 'Элеанора', last_name: 'Суконская', email: 'eleanor@test.shit.ua',},
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

export function extractSentences({fromFile, toFile}) {
    raise('I should be killed, don’t use me')
    
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


export async function createTestTemplateUA1DB() { // @ctx db template
    resetImposed()
    await createUADB('test-template-ua-1')
    await pgConnection({db: 'test-template-ua-1'}, async function(db) {
        let stackBeforeAwait
        const trace = []
        try {
            const random = new Random(Random.engines.mt19937().seed(123123))
            const eventRandom = new Random(Random.engines.mt19937().seed(234234))
            const password_hash = '$2a$10$x5bq4zVvcyTb2oUb5.fhreJfl/2NqsaH3TcAwm/C1apAazlBJX2t6' // secret
            const stampFormat = 'YYYY-MM-DD HH:mm:ss'
            let nextMoment = moment('2014-03-31 18:15:41', stampFormat)
            const nextIDs = {}
            let nextSupportThreadTopicIndex = 0
            let nextMessageIndex = 0
            
            const mtCreateUsers = measureTime({name: 'Creating users', log: true})
            for (const u of users.admin) {
                #await db.insertInto(s{table: 'users', values: asn({kind: 'admin', lang: 'ua', state: 'cool', password_hash}, u.user)})
                #await db.insertInto(s{table: 'user_tokens', values: {user_id: u.user.id, token: 'temp-' + u.user.id}})
                for (const role of u.roles) {
                    #await db.insertInto(s{table: 'user_roles', values: {user_id: u.user.id, role}})
                }
            }
            for (const u of users.writer) {
                #await db.insertInto(s{table: 'users', values: asn({kind: 'writer', lang: 'ua', state: 'cool', password_hash}, u.user)})
                #await db.insertInto(s{table: 'user_tokens', values: {user_id: u.user.id, token: 'temp-' + u.user.id}})
            }
            for (const u of users.customer) {
                #await db.insertInto(s{table: 'users', values: asn({kind: 'customer', lang: 'ua', state: 'cool', password_hash}, u.user)})
                #await db.insertInto(s{table: 'user_tokens', values: {user_id: u.user.id, token: 'temp-' + u.user.id}})
            }
            mtCreateUsers.point({name: 'END'})
            
            const mtEvents = measureTime({name: 'Events', log: true})
            
            if (!'create support threads and messages') {
                #await simNewSupportThread({threadID: 12, messageID: 432, stamp: '2014-04-10 13:44:55', upon: 'luke',
                    topic: 'И это называется следственной документацией!',
                    message: 'Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.'})
                
                #await simNewSupportThread({threadID: 25, messageID: 563, stamp: '2014-05-06 14:33:34', upon: 'perma',
                    topic: dedent(`
                        В   углу   комнаты   стояли  трое  молодых  людей  -  они разглядывали фотографии фройляйн Бюрстнер,
                        воткнутые в плетеную циновку на стене. На ручке открытого окна висела белая  блузка.`),
                    message: dedent(`
                        В  окно  напротив уже высунулись те же старики, но зрителей там
                        прибавилось:  за  их  спинами  возвышался  огромный  мужчина  в
                        раскрытой  на  груди  рубахе, который все время крутил и вертел
                        свою рыжеватую бородку.
                             - Йозеф К.? - спросил инспектор, должно быть,  только  для
                        того, чтобы обратить на себя рассеянный взгляд К.
                             К. наклонил голову.
                             - Должно  быть,  вас  очень  удивили  события сегодняшнего
                        утра?`)})
                
                #await simNewSupportThread({threadID: 308, messageID: 607, stamp: '2014-05-16 23:33:15', upon: 'kafka',
                    topic: `В пустом зале заседаний. Студент. Канцелярии`,
                    message: `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`})
                #await simNewSupportThreadMessage({threadID: 308, messageID: 610, stamp: '2014-05-16 23:35:27', upon: 'kafka',
                    message: dedent(`
                        Еще хотел бы добавить вот что.
                        
                        - Сегодня заседания нет, - сказала женщина.
                        - Как это - нет заседания? - спросил он, не поверив.`)})
                #await simNewSupportThreadMessage({threadID: 308, messageID: 611, stamp: '2014-05-16 23:36:02', upon: 'kafka',
                    message: `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`})
                
                #await simNewSupportThread({threadID: 321, messageID: 903, stamp: '2014-05-21 07:11:59', upon: 'jane',
                    topic: dedent(`Видно,  собираетесь  навести  здесь  порядок?`),
                    message: dedent(`Я так и догадалась по вашей речи. Мне лично она  очень  понравилась.  Правда,  я  не  все  слышала - начало пропустила, а под конец лежала со студентом на полу.`)})
                
                #await simNewSupportThread({threadID: 405, messageID: 1018, stamp: '2014-05-26 07:51:24', upon: 'sand',
                    topic: dedent(`Ну  конечно  же!`),
                    message: dedent(`Книги были старые, потрепанные, на одной  переплет был переломлен и обе половинки держались на ниточке.`)})
                
                #await simNewSupportThread({threadID: 708, messageID: 2245, stamp: '2014-06-13 11:19:32', upon: 'terkin',
                    topic: dedent(`Какая  тут  везде грязь, - сказал К., покачав головой`),
                    message: dedent(`И женщине пришлось смахнуть пыль фартуком хотя бы сверху,  прежде чем К. мог взяться за книгу.`)})
                #await simNewSupportThreadMessage({threadID: 708, messageID: 2249, stamp: '2014-06-13 11:25:13', upon: 'terkin',
                    message: `Он  открыл  ту,  что  лежала  сверху, и увидел неприличную картинку.`})
                
                #await simNewSupportThread({threadID: 914, messageID: 2357, stamp: '2014-07-16 05:24:17', upon: 'carlos',
                    topic: dedent(`Так вот какие юридические книги тут изучают!`),
                    message: dedent(`
                        - Я вам помогу! - сказала женщина. - Согласны?
                        - Но разве вы и вправду можете мне  помочь,  не  подвергая себя  опасности?  Ведь  вы  сами  сказали,  что ваш муж целиком зависит от своего начальства.
                        - И все же я вам помогу, - сказала  женщина.  Подите  сюда, надо  все обсудить. А о том, что мне грозит опасность, говорить не стоит.  Я  только  тогда  пугаюсь  опасности,  когда  считаю нужным. Идите сюда.
                        - Она показала на подмостки и попросила его сесть  рядом с ней на ступеньки.
                        
                        - У вас чудесные темные глаза, -сказала она, когда они сели, и заглянула К. в лицо. - Говорят, у меня тоже глаза красивые, но ваши куда красивее. Ведь  я  вас сразу  приметила,  еще  в первый раз, как только вы сюда зашли. Из-за вас я и пробралась потом в зал заседаний. Обычно я никогда этого не делаю, мне даже, собственно говоря,  запрещено  ходить сюда.`)})
                
                #await simNewSupportThread({threadID: 933, messageID: 2455, stamp: '2014-07-17 07:33:08', upon: 'telo',
                    topic: dedent(`Если вам так не терпится, можете  уходить`),
                    message: dedent(`Давно  могли уйти,  никто  и  не  заметил бы вашего отсутствия. Да, да, надо было вам уйти, как только я пришел, и уйти сразу, немедленно. В этих словах слышалась не только сдержанная злоба, в  них ясно  чувствовалось высокомерие будущего чиновника по отношению к неприятному для него обвиняемому.`)})
                
                #await simNewSupportThread({threadID: 951, messageID: 2599, stamp: '2014-08-06 01:33:37', upon: 'regina',
                    topic: dedent(`Ничего  не  поделаешь`),
                    message: dedent(`Его за мной прислал следователь, мне с вами идти никак нельзя, этот маленький уродец, - тут  она провела  рукой  по лицу студента, этот маленький уродец меня не отпустит.`)})
                
                #await simNewSupportThread({threadID: 1011, messageID: 2682, stamp: '2014-08-15 10:32:42', upon: 'varso',
                    topic: dedent(`Оба  исчезли за поворотом`),
                    message: dedent(`К. все еще стоял в дверях. Он должен был признаться, что женщина не только обманула его, но и солгала, что ее несут к следователю. Не станет  же  следователь сидеть  на  чердаке  и  дожидаться  ее.`)})
                
                #await simNewSupportThread({threadID: 1134, messageID: 2732, stamp: '2014-10-11 05:15:11', upon: 'fedor',
                    topic: dedent(`Вот видите!`),
                    message: dedent(`Вечно  ее  от  меня уносят.  Сегодня воскресенье, работать я не обязан, а мне вдруг дают совершенно ненужные  поручения,  лишь  бы  услать  отсюда.`)})
                
                #await simNewSupportThread({threadID: 1225, messageID: 2768, stamp: '2014-10-23 15:10:13', upon: 'xeno',
                    topic: dedent(`А разве другого выхода нет?`),
                    message: dedent(`
                        - Другого не вижу, - сказал  служитель.  -  И  главное,  с
                        каждым  днем все хуже: до сих пор он таскал ее только к себе, а
                        сейчас  потащил  к  самому  следователю;   впрочем,   этого   я
                        давным-давно ждал.`)})
                
                #await simNewSupportThread({threadID: 1372, messageID: 3008, stamp: '2014-11-25 03:39:53', upon: 'vaganovich',
                    topic: dedent(`Какой смысл?`),
                    message: dedent(`
                        Да  кто  вы  такой?  Ищете  смысл,  а  творите  такую
                        бессмыслицу, что и не придумаешь. Да тут камни возопят! Сначала
                        эти господа на меня напали, а теперь расселись, стоят и глазеют
                        всем скопом, как я пляшу под вашу  дудку.  И  еще  спрашиваете,
                        какой  смысл  звонить  прокурору,  когда  мне  сказано,  что  я
                        арестован! Хорошо, я не буду звонить!`)})
            
            }
            
            mtEvents.point({name: 'END'})
            
            #await db.query(s{y: q`delete from user_tokens`})
            
            
            async function simNewSupportThread({threadID, messageID, stamp, upon, topic, message}) {
                imposeRequestTimestamp(stamp)
                imposeNextIDs([threadID, messageID])
                const user = userFromPon(upon)
                #await req({LANG: 'ua', clientKind: user.clientKind, token: user.token, fun: 'private_createSupportThread', topic, message})
            }
            
            async function simNewSupportThreadMessage({threadID, messageID, stamp, upon, message}) {
                imposeRequestTimestamp(stamp)
                imposeNextIDs([messageID])
                const user = userFromPon(upon)
                #await req({LANG: 'ua', clientKind: user.clientKind, token: user.token, fun: 'private_createSupportThreadMessage', threadID, message})
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




