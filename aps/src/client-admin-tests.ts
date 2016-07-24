/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import static 'into-u/utils-client into-u/ui ./stuff'

module.exports = function({sim}) {
    const drpc = getDebugRPC()
        
    return {
        'UA Admin :: Misc :: 1 839c4909-e1d1-447a-9401-d1599d19598c': {
            templateDB: 'test-template-ua-1',
            async run() {
                #hawait drpc({fun: 'danger_clearSentEmails'})
                
                //------------------------------ Todd ------------------------------
                
                sim.selectBrowser('todd')
                
                #hawait sim.navigate('dashboard.html')
                
                art.uiState({$tag: 'c70e18eb-516b-41cb-bf7a-bdf748595ad2', expected: {
                    url: `http://aps-ua-writer.local:3022/sign-in.html`,
                    'Input-email': ``,
                    'Input-password': ``,
                    'link-createAccount': { title: `Срочно создать!` },
                    pageHeader: `Вход`,
                    'button-primary': { title: `Войти` },
                    'TopNavItem-why': { title: `Почему мы?` },
                    'TopNavItem-prices': { title: `Цены` },
                    'TopNavItem-faq': { title: `ЧаВо` },
                    'TopNavItem-sign-in': { title: `Вход` } 
                }})                
                
                // Inputs
                #hawait testGlobal.controls['Input-email'].setValue({value: 'todd@test.shit.ua'})
                #hawait testGlobal.controls['Input-password'].setValue({value: 'secret'})
                // Action
                #hawait testGlobal.controls['button-primary'].click()
                #hawait art.uiState({$tag: '866bef17-2783-40a5-860d-0d2f69966664', expected: {
                    url: `http://aps-ua-writer.local:3022/dashboard.html`,
                    'link-signOut': { title: `Выйти прочь` },
                    'link-changePassword': { title: `Сменить пароль` },
                    pageHeader: `Панель`,
                    'TopNavItem-admin-heap': { title: `Куча` },
                    'TopNavItem-support': { title: `Поддержка` },
                    'TopNavItem-dashboard': { title: `Тодд` },
                    'liveBadge-topNavItem-admin-heap': `13` 
                }})
                
                testGlobal.controls['TopNavItem-admin-heap'].showHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15, dtop: -2}})
                #hawait art.pausePoint({title: 'There is some unassigned work, let’s take a look...', $tag: '20f801f5-657b-4176-bd1a-fb78f5af1811'})
                testGlobal.controls['TopNavItem-admin-heap'].hideHand()
                
                // Action
                #hawait testGlobal.controls['TopNavItem-admin-heap'].click()
                #hawait art.uiState({$tag: '1c3a4a15-4bc4-46f6-afd9-d23433c6d839', expected: {
                    url: `http://aps-ua-writer.local:3022/admin-heap.html`,
                    'liveBadge-supportTab': `13`,
                    'button-showMore': { title: `Показать еще` },
                    'chunk-0.thread-0.topic': `И это называется следственной документацией!`,
                    'chunk-0.thread-0.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-0.newMessages.message-0.userLabel-from': `Люк Хуюк`,
                    'chunk-0.thread-0.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-0.newMessages.message-0.timestamp': `10/04/2014 16:44:55`,
                    'chunk-0.thread-0.newMessages.message-0.message': `Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.`,
                    'chunk-0.thread-1.topic': dedent(`
                          В   углу   комнаты   стояли  трое  молодых  людей  -  они разглядывали фотографии фройляйн Бюрстнер,
                          воткнутые в плетеную циновку на стене. На ручке открытого окна висела белая  блузка.`),
                    'chunk-0.thread-1.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-1.newMessages.message-0.userLabel-from': `Даздраперма Дивизионная`,
                    'chunk-0.thread-1.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-1.newMessages.message-0.timestamp': `06/05/2014 17:33:34`,
                    'chunk-0.thread-1.newMessages.message-0.message': dedent(`
                          В  окно  напротив уже высунулись те же старики, но зрителей там
                          прибавилось:  за  их  спинами  возвышался  огромный  мужчина  в
                          раскрытой  на  груди  рубахе, который все время крутил и вертел
                          свою рыжеватую бородку.
                               - Йозеф К.? - спросил инспектор, должно быть,  только  для
                          того, чтобы обратить на себя рассеянный взгляд К.
                               К. наклонил голову.
                               - Должно  быть,  вас  очень  удивили  события сегодняшнего
                          утра?`),
                    'chunk-0.thread-2.topic': `В пустом зале заседаний. Студент. Канцелярии`,
                    'chunk-0.thread-2.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-2.newMessages.message-0.userLabel-from': `Франц Кафка`,
                    'chunk-0.thread-2.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-2.newMessages.message-0.timestamp': `17/05/2014 02:33:15`,
                    'chunk-0.thread-2.newMessages.message-0.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                    'chunk-0.thread-2.newMessages.message-1.continuation': `В догонку`,
                    'chunk-0.thread-2.newMessages.message-1.timestamp': `17/05/2014 02:35:27`,
                    'chunk-0.thread-2.newMessages.message-1.message': dedent(`
                          Еще хотел бы добавить вот что.
                          
                          - Сегодня заседания нет, - сказала женщина.
                          - Как это - нет заседания? - спросил он, не поверив.`),
                    'chunk-0.thread-2.newMessages.message-2.continuation': `В догонку`,
                    'chunk-0.thread-2.newMessages.message-2.timestamp': `17/05/2014 02:36:02`,
                    'chunk-0.thread-2.newMessages.message-2.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                    'chunk-0.thread-3.topic': `Видно,  собираетесь  навести  здесь  порядок?`,
                    'chunk-0.thread-3.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-3.newMessages.message-0.userLabel-from': `Джейн Остин`,
                    'chunk-0.thread-3.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-3.newMessages.message-0.timestamp': `21/05/2014 10:11:59`,
                    'chunk-0.thread-3.newMessages.message-0.message': `Я так и догадалась по вашей речи. Мне лично она  очень  понравилась.  Правда,  я  не  все  слышала - начало пропустила, а под конец лежала со студентом на полу.`,
                    'chunk-0.thread-4.topic': `Ну  конечно  же!`,
                    'chunk-0.thread-4.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-4.newMessages.message-0.userLabel-from': `Жорж Санд`,
                    'chunk-0.thread-4.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-4.newMessages.message-0.timestamp': `26/05/2014 10:51:24`,
                    'chunk-0.thread-4.newMessages.message-0.message': `Книги были старые, потрепанные, на одной  переплет был переломлен и обе половинки держались на ниточке.`,
                    'chunk-0.thread-5.topic': `Какая  тут  везде грязь, - сказал К., покачав головой`,
                    'chunk-0.thread-5.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-5.newMessages.message-0.userLabel-from': `Василий Теркин`,
                    'chunk-0.thread-5.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-5.newMessages.message-0.timestamp': `13/06/2014 14:19:32`,
                    'chunk-0.thread-5.newMessages.message-0.message': `И женщине пришлось смахнуть пыль фартуком хотя бы сверху,  прежде чем К. мог взяться за книгу.`,
                    'chunk-0.thread-5.newMessages.message-1.continuation': `В догонку`,
                    'chunk-0.thread-5.newMessages.message-1.timestamp': `13/06/2014 14:25:13`,
                    'chunk-0.thread-5.newMessages.message-1.message': `Он  открыл  ту,  что  лежала  сверху, и увидел неприличную картинку.`,
                    'chunk-0.thread-6.topic': `Так вот какие юридические книги тут изучают!`,
                    'chunk-0.thread-6.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-6.newMessages.message-0.userLabel-from': `Карлос Кастанеда`,
                    'chunk-0.thread-6.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-6.newMessages.message-0.timestamp': `16/07/2014 08:24:17`,
                    'chunk-0.thread-6.newMessages.message-0.message': dedent(`
                          - Я вам помогу! - сказала женщина. - Согласны?
                          - Но разве вы и вправду можете мне  помочь,  не  подвергая себя  опасности?  Ведь  вы  сами  сказали,  что ваш муж целиком зависит от своего начальства.
                          - И все же я вам помогу, - сказала  женщина.  Подите  сюда, надо  все обсудить. А о том, что мне грозит опасность, говорить не стоит.  Я  только  тогда  пугаюсь  опасности,  когда  считаю нужным. Идите сюда.
                          - Она показала на подмостки и попросила его сесть  рядом с ней на ступеньки.
                          
                          - У вас чудесные темные глаза, -сказала она, когда они сели, и заглянула К. в лицо. - Говорят, у меня тоже глаза красивые, но ваши куда красивее. Ведь  я  вас сразу  приметила,  еще  в первый раз, как только вы сюда зашли. Из-за вас я и пробралась потом в зал заседаний. Обычно я никогда этого не делаю, мне даже, собственно говоря,  запрещено  ходить сюда.`),
                    'chunk-0.thread-7.topic': `Если вам так не терпится, можете  уходить`,
                    'chunk-0.thread-7.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-7.newMessages.message-0.userLabel-from': `Тело Странное`,
                    'chunk-0.thread-7.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-7.newMessages.message-0.timestamp': `17/07/2014 10:33:08`,
                    'chunk-0.thread-7.newMessages.message-0.message': `Давно  могли уйти,  никто  и  не  заметил бы вашего отсутствия. Да, да, надо было вам уйти, как только я пришел, и уйти сразу, немедленно. В этих словах слышалась не только сдержанная злоба, в  них ясно  чувствовалось высокомерие будущего чиновника по отношению к неприятному для него обвиняемому.`,
                    'chunk-0.thread-8.topic': `Ничего  не  поделаешь`,
                    'chunk-0.thread-8.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-8.newMessages.message-0.userLabel-from': `Регина Дубовицкая`,
                    'chunk-0.thread-8.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-8.newMessages.message-0.timestamp': `06/08/2014 04:33:37`,
                    'chunk-0.thread-8.newMessages.message-0.message': `Его за мной прислал следователь, мне с вами идти никак нельзя, этот маленький уродец, - тут  она провела  рукой  по лицу студента, этот маленький уродец меня не отпустит.`,
                    'chunk-0.thread-9.topic': `Оба  исчезли за поворотом`,
                    'chunk-0.thread-9.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-9.newMessages.message-0.userLabel-from': `Варсоновий Оптинский`,
                    'chunk-0.thread-9.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-9.newMessages.message-0.timestamp': `15/08/2014 13:32:42`,
                    'chunk-0.thread-9.newMessages.message-0.message': `К. все еще стоял в дверях. Он должен был признаться, что женщина не только обманула его, но и солгала, что ее несут к следователю. Не станет  же  следователь сидеть  на  чердаке  и  дожидаться  ее.`,
                    pageHeader: `Куча работы`,
                    'TopNavItem-support': { title: 'Поддержка' },
                    'TopNavItem-admin-heap': { title: `Куча` },
                    'TopNavItem-dashboard': { title: `Тодд` },
                    'liveBadge-topNavItem-admin-heap': `13` 
                }})
                #hawait art.pausePoint({title: 'A lot of stuff, let’s do some scrolling...', $tag: 'a86e6b75-0140-4347-a961-bf9886937806', locus: 'top-right'})
                #hawait art.scroll({origY: 0, destY: 'bottom'})
                #hawait art.pausePoint({title: 'Before clicking "Show More" button', $tag: 'dcf633b6-0cd6-43bc-81f8-5920ff60a795', locus: 'top-right'})
                
                // Action
                #hawait testGlobal.controls['button-showMore'].click({testActionHandOpts: {pointingFrom: 'top'}})
                #hawait art.uiStateChange({$tag: 'edb9fdc5-841b-4232-baa2-dfdc39d8d02d', expected: {
                    $$deleted: ['button-showMore'],
                    
                    'chunk-1.thread-0.topic': `Вот видите!`,
                    'chunk-1.thread-0.button-takeAndReply': { icon: `comment` },
                    'chunk-1.thread-0.newMessages.message-0.userLabel-from': `Федор Достоевский`,
                    'chunk-1.thread-0.newMessages.message-0.to': `В рельсу`,
                    'chunk-1.thread-0.newMessages.message-0.timestamp': `11/10/2014 08:15:11`,
                    'chunk-1.thread-0.newMessages.message-0.message': `Вечно  ее  от  меня уносят.  Сегодня воскресенье, работать я не обязан, а мне вдруг дают совершенно ненужные  поручения,  лишь  бы  услать  отсюда.`,
                    'chunk-1.thread-1.topic': `А разве другого выхода нет?`,
                    'chunk-1.thread-1.button-takeAndReply': { icon: `comment` },
                    'chunk-1.thread-1.newMessages.message-0.userLabel-from': `Ксенофонт Тутанский`,
                    'chunk-1.thread-1.newMessages.message-0.to': `В рельсу`,
                    'chunk-1.thread-1.newMessages.message-0.timestamp': `23/10/2014 18:10:13`,
                    'chunk-1.thread-1.newMessages.message-0.message': dedent(`
                          - Другого не вижу, - сказал  служитель.  -  И  главное,  с
                          каждым  днем все хуже: до сих пор он таскал ее только к себе, а
                          сейчас  потащил  к  самому  следователю;   впрочем,   этого   я
                          давным-давно ждал.`),
                    'chunk-1.thread-2.topic': `Какой смысл?`,
                    'chunk-1.thread-2.button-takeAndReply': { icon: `comment` },
                    'chunk-1.thread-2.newMessages.message-0.userLabel-from': `Евгений Ваганович`,
                    'chunk-1.thread-2.newMessages.message-0.to': `В рельсу`,
                    'chunk-1.thread-2.newMessages.message-0.timestamp': `25/11/2014 05:39:53`,
                    'chunk-1.thread-2.newMessages.message-0.message': dedent(`
                          Да  кто  вы  такой?  Ищете  смысл,  а  творите  такую
                          бессмыслицу, что и не придумаешь. Да тут камни возопят! Сначала
                          эти господа на меня напали, а теперь расселись, стоят и глазеют
                          всем скопом, как я пляшу под вашу  дудку.  И  еще  спрашиваете,
                          какой  смысл  звонить  прокурору,  когда  мне  сказано,  что  я
                          арестован! Хорошо, я не буду звонить!`) 
                }})
                
                #hawait art.scroll({origY: 'current', destY: 'bottom'})
                #hawait art.pausePoint({title: 'Now admin chooses a thread to take', $tag: '3045e8f4-3b18-4681-91e4-bfaa2961f40d', locus: 'top-right'})
                #hawait art.scroll({origY: 'current', destY: 320})
                
                // Action
                #hawait testGlobal.controls['chunk-0.thread-2.button-takeAndReply'].click()
                // #hawait art.shitBlinksForMax({$tag: 'f146dce3-e5dc-4846-bb65-868c8083b859', kind: 'button', name: 'takeAndReply-308', max: 2000})
                
                #hawait art.uiState({$tag: '404c23b9-e496-41b5-a105-71ae5c44ed5f', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html?thread=308`,
                        'Select-ordering': `desc`,
                        'chunk-0.message-0.userLabel-from': `Франц Кафка`,
                        'chunk-0.message-0.to': `В рельсу`,
                        'chunk-0.message-0.timestamp': `17/05/2014 02:36:02`,
                        'chunk-0.message-0.newLabel': `Новое`,
                        'chunk-0.message-0.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                        'chunk-0.message-1.userLabel-from': `Франц Кафка`,
                        'chunk-0.message-1.to': `В рельсу`,
                        'chunk-0.message-1.timestamp': `17/05/2014 02:35:27`,
                        'chunk-0.message-1.newLabel': `Новое`,
                        'chunk-0.message-1.message': dedent(`
                              Еще хотел бы добавить вот что.
                              
                              - Сегодня заседания нет, - сказала женщина.
                              - Как это - нет заседания? - спросил он, не поверив.`),
                        'chunk-0.message-2.userLabel-from': `Франц Кафка`,
                        'chunk-0.message-2.to': `В рельсу`,
                        'chunk-0.message-2.timestamp': `17/05/2014 02:33:15`,
                        'chunk-0.message-2.newLabel': `Новое`,
                        'chunk-0.message-2.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                        pageHeader: `Запрос в поддержку № 308`,
                        'button-plus': { icon: `comment` },
                        'TopNavItem-admin-heap': { title: `Куча` },
                        'liveBadge-topNavItem-admin-heap': `12`,
                        'TopNavItem-support': { title: `Поддержка` },
                        'liveBadge-topNavItem-support': '3', 
                        'TopNavItem-dashboard': { title: `Тодд` },
                }})
                
                // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                testGlobal.controls['TopNavItem-admin-heap'].showHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15, dtop: -2}})
                #hawait art.pausePoint({title: 'Amount of work in heap decreased, since we’ve just taken one piece from it', $tag: '3ebb103e-7f0a-4669-9a29-2cbb2bbed460'})
                testGlobal.controls['TopNavItem-admin-heap'].hideHand()
                testGlobal.controls['TopNavItem-support'].showHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15, dtop: -2}})
                #hawait art.pausePoint({title: 'Support request we took has three messages in it. This badge will be hanging there until we address them by replying.\nBut first let’s go back to heap and take one more task...', $tag: '348c92fd-d749-4fad-8c4e-eef3754548cf'})
                testGlobal.controls['TopNavItem-support'].hideHand()
                
                // Action
                #hawait testGlobal.controls['TopNavItem-admin-heap'].click()
                art.uiState({$tag: '8fc8247d-2b0a-492b-a495-ee782f56eeb4', expected: {
                    url: `http://aps-ua-writer.local:3022/admin-heap.html`,
                    'liveBadge-supportTab': `12`,
                    'button-showMore': { title: `Показать еще` },
                    'chunk-0.thread-0.topic': `И это называется следственной документацией!`,
                    'chunk-0.thread-0.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-0.newMessages.message-0.userLabel-from': `Люк Хуюк`,
                    'chunk-0.thread-0.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-0.newMessages.message-0.timestamp': `10/04/2014 16:44:55`,
                    'chunk-0.thread-0.newMessages.message-0.message': `Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.`,
                    'chunk-0.thread-1.topic': dedent(`
                          В   углу   комнаты   стояли  трое  молодых  людей  -  они разглядывали фотографии фройляйн Бюрстнер,
                          воткнутые в плетеную циновку на стене. На ручке открытого окна висела белая  блузка.`),
                    'chunk-0.thread-1.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-1.newMessages.message-0.userLabel-from': `Даздраперма Дивизионная`,
                    'chunk-0.thread-1.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-1.newMessages.message-0.timestamp': `06/05/2014 17:33:34`,
                    'chunk-0.thread-1.newMessages.message-0.message': dedent(`
                          В  окно  напротив уже высунулись те же старики, но зрителей там
                          прибавилось:  за  их  спинами  возвышался  огромный  мужчина  в
                          раскрытой  на  груди  рубахе, который все время крутил и вертел
                          свою рыжеватую бородку.
                               - Йозеф К.? - спросил инспектор, должно быть,  только  для
                          того, чтобы обратить на себя рассеянный взгляд К.
                               К. наклонил голову.
                               - Должно  быть,  вас  очень  удивили  события сегодняшнего
                          утра?`),
                    'chunk-0.thread-2.topic': `Видно,  собираетесь  навести  здесь  порядок?`,
                    'chunk-0.thread-2.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-2.newMessages.message-0.userLabel-from': `Джейн Остин`,
                    'chunk-0.thread-2.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-2.newMessages.message-0.timestamp': `21/05/2014 10:11:59`,
                    'chunk-0.thread-2.newMessages.message-0.message': `Я так и догадалась по вашей речи. Мне лично она  очень  понравилась.  Правда,  я  не  все  слышала - начало пропустила, а под конец лежала со студентом на полу.`,
                    'chunk-0.thread-3.topic': `Ну  конечно  же!`,
                    'chunk-0.thread-3.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-3.newMessages.message-0.userLabel-from': `Жорж Санд`,
                    'chunk-0.thread-3.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-3.newMessages.message-0.timestamp': `26/05/2014 10:51:24`,
                    'chunk-0.thread-3.newMessages.message-0.message': `Книги были старые, потрепанные, на одной  переплет был переломлен и обе половинки держались на ниточке.`,
                    'chunk-0.thread-4.topic': `Какая  тут  везде грязь, - сказал К., покачав головой`,
                    'chunk-0.thread-4.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-4.newMessages.message-0.userLabel-from': `Василий Теркин`,
                    'chunk-0.thread-4.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-4.newMessages.message-0.timestamp': `13/06/2014 14:19:32`,
                    'chunk-0.thread-4.newMessages.message-0.message': `И женщине пришлось смахнуть пыль фартуком хотя бы сверху,  прежде чем К. мог взяться за книгу.`,
                    'chunk-0.thread-4.newMessages.message-1.continuation': `В догонку`,
                    'chunk-0.thread-4.newMessages.message-1.timestamp': `13/06/2014 14:25:13`,
                    'chunk-0.thread-4.newMessages.message-1.message': `Он  открыл  ту,  что  лежала  сверху, и увидел неприличную картинку.`,
                    'chunk-0.thread-5.topic': `Так вот какие юридические книги тут изучают!`,
                    'chunk-0.thread-5.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-5.newMessages.message-0.userLabel-from': `Карлос Кастанеда`,
                    'chunk-0.thread-5.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-5.newMessages.message-0.timestamp': `16/07/2014 08:24:17`,
                    'chunk-0.thread-5.newMessages.message-0.message': dedent(`
                          - Я вам помогу! - сказала женщина. - Согласны?
                          - Но разве вы и вправду можете мне  помочь,  не  подвергая себя  опасности?  Ведь  вы  сами  сказали,  что ваш муж целиком зависит от своего начальства.
                          - И все же я вам помогу, - сказала  женщина.  Подите  сюда, надо  все обсудить. А о том, что мне грозит опасность, говорить не стоит.  Я  только  тогда  пугаюсь  опасности,  когда  считаю нужным. Идите сюда.
                          - Она показала на подмостки и попросила его сесть  рядом с ней на ступеньки.
                          
                          - У вас чудесные темные глаза, -сказала она, когда они сели, и заглянула К. в лицо. - Говорят, у меня тоже глаза красивые, но ваши куда красивее. Ведь  я  вас сразу  приметила,  еще  в первый раз, как только вы сюда зашли. Из-за вас я и пробралась потом в зал заседаний. Обычно я никогда этого не делаю, мне даже, собственно говоря,  запрещено  ходить сюда.`),
                    'chunk-0.thread-6.topic': `Если вам так не терпится, можете  уходить`,
                    'chunk-0.thread-6.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-6.newMessages.message-0.userLabel-from': `Тело Странное`,
                    'chunk-0.thread-6.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-6.newMessages.message-0.timestamp': `17/07/2014 10:33:08`,
                    'chunk-0.thread-6.newMessages.message-0.message': `Давно  могли уйти,  никто  и  не  заметил бы вашего отсутствия. Да, да, надо было вам уйти, как только я пришел, и уйти сразу, немедленно. В этих словах слышалась не только сдержанная злоба, в  них ясно  чувствовалось высокомерие будущего чиновника по отношению к неприятному для него обвиняемому.`,
                    'chunk-0.thread-7.topic': `Ничего  не  поделаешь`,
                    'chunk-0.thread-7.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-7.newMessages.message-0.userLabel-from': `Регина Дубовицкая`,
                    'chunk-0.thread-7.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-7.newMessages.message-0.timestamp': `06/08/2014 04:33:37`,
                    'chunk-0.thread-7.newMessages.message-0.message': `Его за мной прислал следователь, мне с вами идти никак нельзя, этот маленький уродец, - тут  она провела  рукой  по лицу студента, этот маленький уродец меня не отпустит.`,
                    'chunk-0.thread-8.topic': `Оба  исчезли за поворотом`,
                    'chunk-0.thread-8.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-8.newMessages.message-0.userLabel-from': `Варсоновий Оптинский`,
                    'chunk-0.thread-8.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-8.newMessages.message-0.timestamp': `15/08/2014 13:32:42`,
                    'chunk-0.thread-8.newMessages.message-0.message': `К. все еще стоял в дверях. Он должен был признаться, что женщина не только обманула его, но и солгала, что ее несут к следователю. Не станет  же  следователь сидеть  на  чердаке  и  дожидаться  ее.`,
                    'chunk-0.thread-9.topic': `Вот видите!`,
                    'chunk-0.thread-9.button-takeAndReply': { icon: `comment` },
                    'chunk-0.thread-9.newMessages.message-0.userLabel-from': `Федор Достоевский`,
                    'chunk-0.thread-9.newMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-9.newMessages.message-0.timestamp': `11/10/2014 08:15:11`,
                    'chunk-0.thread-9.newMessages.message-0.message': `Вечно  ее  от  меня уносят.  Сегодня воскресенье, работать я не обязан, а мне вдруг дают совершенно ненужные  поручения,  лишь  бы  услать  отсюда.`,
                    pageHeader: `Куча работы`,
                    'TopNavItem-admin-heap': { title: `Куча` },
                    'TopNavItem-support': { title: `Поддержка` },
                    'TopNavItem-dashboard': { title: `Тодд` },
                    'liveBadge-topNavItem-admin-heap': `12`,
                    'liveBadge-topNavItem-support': `3` 
                }})
                
                // Action
                #hawait testGlobal.controls['chunk-0.thread-0.button-takeAndReply'].click()
                art.uiState({$tag: '9dde55d6-0488-4ec6-9443-a96370c7fd4b', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html?thread=12`,
                        'Select-ordering': `desc`,
                        'chunk-0.message-0.userLabel-from': `Люк Хуюк`,
                        'chunk-0.message-0.to': `В рельсу`,
                        'chunk-0.message-0.timestamp': `10/04/2014 16:44:55`,
                        'chunk-0.message-0.newLabel': `Новое`,
                        'chunk-0.message-0.message': `Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.`,
                        pageHeader: `Запрос в поддержку № 12`,
                        'button-plus': { icon: `comment` },
                        'TopNavItem-admin-heap': { title: `Куча` },
                        'TopNavItem-support': { title: `Поддержка` },
                        'TopNavItem-dashboard': { title: `Тодд` },
                        'liveBadge-topNavItem-admin-heap': `11`,
                        'liveBadge-topNavItem-support': `4` 
                }})

                testGlobal.controls['TopNavItem-support'].showHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15, dtop: -2}})
                #hawait art.pausePoint({title: 'Numbers changed again. We have to deal with 4 support messages contained in 2 threads.\nBy clicking on Support menu item we’ll see what those threads are...', $tag: '182c9720-f9ed-4afe-8c46-5b45c482e9a9'})
                testGlobal.controls['TopNavItem-support'].hideHand()
                
                // Action
                #hawait testGlobal.controls['TopNavItem-support'].click()
                art.uiState({$tag: '0020d0bf-5a2c-4287-a4b6-f1eca4c36ca3', expected: {
                }})

                
                // #hawait art.pausePoint({title: 'Changing ordering to show old messages first', $tag: '1e77736a-3498-4b5d-8b1e-94c0f7dd6c56'})
                // Action
                #hawait testGlobal.controls['Select-ordering'].setValue({value: 'asc', testActionHandOpts: {pointingFrom: 'left', dtop: 38}})
                #hawait art.uiState({$tag: '73952bab-8024-4351-8656-0966860aa31b', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html?thread=308&ordering=asc`,
                        'Select-ordering': `asc`,
                        'TopNavItem-admin-heap': { title: `Куча` },
                        'TopNavItem-support': { title: `Поддержка` },
                        'TopNavItem-dashboard': { title: `Тодд` },
                        'liveBadge-topNavItem-admin-heap': `12`,
                        'liveBadge-topNavItem-support': `3`,
                        'chunk-0.message-0.userLabel-from': `Франц Кафка`,
                        'chunk-0.message-0.to': `В рельсу`,
                        'chunk-0.message-0.timestamp': `17/05/2014 02:33:15`,
                        'chunk-0.message-0.newLabel': `Новое`,
                        'chunk-0.message-0.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                        'chunk-0.message-1.userLabel-from': `Франц Кафка`,
                        'chunk-0.message-1.to': `В рельсу`,
                        'chunk-0.message-1.timestamp': `17/05/2014 02:35:27`,
                        'chunk-0.message-1.newLabel': `Новое`,
                        'chunk-0.message-1.message': dedent(`
                              Еще хотел бы добавить вот что.
                              
                              - Сегодня заседания нет, - сказала женщина.
                              - Как это - нет заседания? - спросил он, не поверив.`),
                        'chunk-0.message-2.userLabel-from': `Франц Кафка`,
                        'chunk-0.message-2.to': `В рельсу`,
                        'chunk-0.message-2.timestamp': `17/05/2014 02:36:02`,
                        'chunk-0.message-2.newLabel': `Новое`,
                        'chunk-0.message-2.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                        pageHeader: `Запрос в поддержку № 308`,
                        'button-plus': { icon: `comment` } 
                }})
                
                #hawait art.pausePoint({title: 'Changing it back to conveniently show new stuff first', $tag: '79a2d7fc-5509-41fc-b606-b82bdccd5d68'})
                // Action
                #hawait testGlobal.controls['Select-ordering'].setValue({value: 'desc', testActionHandOpts: {pointingFrom: 'left', dtop: 18}})
                #hawait art.uiState({$tag: '1920f57c-db76-40ff-9f85-8ebdd8faf2f3', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html?thread=308&ordering=desc`,
                        'Select-ordering': `desc`,
                        'TopNavItem-admin-heap': { title: `Куча` },
                        'TopNavItem-support': { title: `Поддержка` },
                        'TopNavItem-dashboard': { title: `Тодд` },
                        'liveBadge-topNavItem-admin-heap': `12`,
                        'liveBadge-topNavItem-support': `3`,
                        'chunk-0.message-0.userLabel-from': `Франц Кафка`,
                        'chunk-0.message-0.to': `В рельсу`,
                        'chunk-0.message-0.timestamp': `17/05/2014 02:36:02`,
                        'chunk-0.message-0.newLabel': `Новое`,
                        'chunk-0.message-0.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                        'chunk-0.message-1.userLabel-from': `Франц Кафка`,
                        'chunk-0.message-1.to': `В рельсу`,
                        'chunk-0.message-1.timestamp': `17/05/2014 02:35:27`,
                        'chunk-0.message-1.newLabel': `Новое`,
                        'chunk-0.message-1.message': dedent(`
                              Еще хотел бы добавить вот что.
                              
                              - Сегодня заседания нет, - сказала женщина.
                              - Как это - нет заседания? - спросил он, не поверив.`),
                        'chunk-0.message-2.userLabel-from': `Франц Кафка`,
                        'chunk-0.message-2.to': `В рельсу`,
                        'chunk-0.message-2.timestamp': `17/05/2014 02:33:15`,
                        'chunk-0.message-2.newLabel': `Новое`,
                        'chunk-0.message-2.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                        pageHeader: `Запрос в поддержку № 308`,
                        'button-plus': { icon: `comment` } 
                }})
                
                #hawait art.pausePoint({title: 'Will respond...', $tag: '3687412d-c52a-4bdf-bc8e-1c588df5da22'})
                // Action
                #hawait testGlobal.controls['button-plus'].click()
                art.uiStateChange({$tag: 'bd47917a-b212-4d03-8910-5af36b2f7ebc', expected: {
                    $$deleted: ['Select-ordering', 'button-plus'],
                    
                    'Input-message': '',
                    'button-primary': { title: 'Запостить' },
                    'button-cancel': { title: 'Передумал' }
                }})

                // Inputs
                #hawait testGlobal.controls['Input-message'].setValue({value: 'Та я понял, что ты писатель... В чем дело-то?'})
                // Action
                #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/23 13:56:26'})
                #hawait testGlobal.controls['button-primary'].click()
                art.uiState({$tag: '0bb446ca-4e6e-4bcf-b9c9-687f1f3a2f29', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html?thread=308`,
                        'Select-ordering': `desc`,
                        'chunk-0.message-0.userLabel-from': `Тодд Суппортод`,
                        'chunk-0.message-0.userLabel-to': `Франц Кафка`,
                        'chunk-0.message-0.timestamp': `23/07/2016 16:56:26`,
                        'chunk-0.message-0.message': `Та я понял, что ты писатель... В чем дело-то?`,
                        'chunk-0.message-1.userLabel-from': `Франц Кафка`,
                        'chunk-0.message-1.to': `В рельсу`,
                        'chunk-0.message-1.timestamp': `17/05/2014 02:36:02`,
                        'chunk-0.message-1.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                        'chunk-0.message-2.userLabel-from': `Франц Кафка`,
                        'chunk-0.message-2.to': `В рельсу`,
                        'chunk-0.message-2.timestamp': `17/05/2014 02:35:27`,
                        'chunk-0.message-2.message': dedent(`
                              Еще хотел бы добавить вот что.
                              
                              - Сегодня заседания нет, - сказала женщина.
                              - Как это - нет заседания? - спросил он, не поверив.`),
                        'chunk-0.message-3.userLabel-from': `Франц Кафка`,
                        'chunk-0.message-3.to': `В рельсу`,
                        'chunk-0.message-3.timestamp': `17/05/2014 02:33:15`,
                        'chunk-0.message-3.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                        pageHeader: `Запрос в поддержку № 308`,
                        'button-plus': { icon: `comment` },
                        'TopNavItem-admin-my-tasks': { title: `Мои задачи` },
                        'TopNavItem-admin-heap': { title: `Куча` },
                        'TopNavItem-dashboard': { title: `Тодд` },
                        'liveBadge-topNavItem-admin-heap': `12` 
                }})
                
                //------------------------------ Kafka ------------------------------

                sim.selectBrowser('kafka')
                #hawait sim.navigate('dashboard.html')
                art.uiState({$tag: '4e992916-7cb7-40d3-bbcd-0e9b1a961412', expected: {
                    url: `http://aps-ua-writer.local:3022/sign-in.html`,
                    'Input-email': ``,
                    'Input-password': ``,
                    'link-createAccount': { title: `Срочно создать!` },
                    pageHeader: `Вход`,
                    'button-primary': { title: `Войти` },
                    'TopNavItem-why': { title: `Почему мы?` },
                    'TopNavItem-prices': { title: `Цены` },
                    'TopNavItem-faq': { title: `ЧаВо` },
                    'TopNavItem-sign-in': { title: `Вход` } 
                }})
                
                // Inputs
                #hawait testGlobal.controls['Input-email'].setValue({value: 'kafka@test.shit.ua'})
                #hawait testGlobal.controls['Input-password'].setValue({value: 'secret'})
                // Action
                #hawait testGlobal.controls['button-primary'].click()
                art.uiState({$tag: '2fcf578d-3b0d-4f70-b3a1-7d4d892ecd91', expected: {
                    url: `http://aps-ua-writer.local:3022/dashboard.html`,
                    'link-signOut': { title: `Выйти прочь` },
                    'link-changePassword': { title: `Сменить пароль` },
                    pageHeader: `Панель`,
                    'TopNavItem-why': { title: `Почему мы?` },
                    'TopNavItem-prices': { title: `Цены` },
                    'TopNavItem-faq': { title: `ЧаВо` },
                    'TopNavItem-orders': { title: `Мои заказы` },
                    'TopNavItem-store': { title: `Аукцион` },
                    'TopNavItem-profile': { title: `Профиль` },
                    'TopNavItem-support': { title: `Поддержка` },
                    'TopNavItem-dashboard': { title: `Франц` },
                    'liveBadge-topNavItem-support': '1'
                }})
                
                // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                testGlobal.controls['TopNavItem-support'].showHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15, dtop: -2}})
                #hawait art.pausePoint({title: 'We have an unread message', $tag: 'ea6d6e4a-c4d6-489c-97c9-9adb34b284d7'})
                testGlobal.controls['TopNavItem-support'].hideHand()
                
                // Action
                #hawait testGlobal.controls['TopNavItem-support'].click()
                art.uiState({$tag: 'e03093b1-3834-45e9-9f7c-724536571ecc', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html`,
                    pageHeader: `Поддержка`,
                    'chunk-0.thread-0.link-topic': { title: `В пустом зале заседаний. Студент. Канцелярии` },
                    'chunk-0.thread-0.newMessages.message-0.userLabel-from': `Тодд Суппортод`,
                    'chunk-0.thread-0.newMessages.message-0.userLabel-to': `Франц Кафка`,
                    'chunk-0.thread-0.newMessages.message-0.timestamp': `23/07/2016 16:56:26`,
                    'chunk-0.thread-0.newMessages.message-0.message': `Та я понял, что ты писатель... В чем дело-то?`,
                    'chunk-0.thread-0.newMessages.message-0.newLabel': `Новое`,
                    'chunk-0.thread-0.oldMessages.message-0.userLabel-from': `Франц Кафка`,
                    'chunk-0.thread-0.oldMessages.message-0.to': `В рельсу`,
                    'chunk-0.thread-0.oldMessages.message-0.timestamp': `17/05/2014 02:36:02`,
                    'chunk-0.thread-0.oldMessages.message-0.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                    'chunk-0.thread-0.oldMessages.link-topic': { title: `...и еще 2 старых сообщения` },
                    'TopNavItem-why': { title: `Почему мы?` },
                    'TopNavItem-prices': { title: `Цены` },
                    'TopNavItem-faq': { title: `ЧаВо` },
                    'TopNavItem-orders': { title: `Мои заказы` },
                    'TopNavItem-store': { title: `Аукцион` },
                    'TopNavItem-profile': { title: `Профиль` },
                    'TopNavItem-support': { title: `Поддержка` },
                    'TopNavItem-dashboard': { title: `Франц` },
                    'liveBadge-topNavItem-support': `1` 
                }})
                
                // Action
                #hawait testGlobal.controls['chunk-0.thread-0.oldMessages.link-topic'].click()
                art.uiState({$tag: '627276cd-3bfe-4932-9e2f-e2845f6015f5', expected: {
                }})

                
                // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
            },
        }
    }
    
}
