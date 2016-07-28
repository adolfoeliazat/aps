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
            async run() {
                // const runKind = 'first'
                const runKind = 'last'
                const slowly = false
                
                if (slowly) { setTestSpeed('slow'); art.respectArtPauses = true }
                if (runKind === 'first' || runKind === 'all') {
                    await art.resetTestDatabase({templateDB: 'test-template-ua-1', alsoRecreateTemplate: true})
                } else {
                    await art.resetTestDatabase({templateDB: 'test-template-didi'})
                }
                
                if (runKind === 'first' || runKind === 'all') {
                    #hawait todd1()
                    #hawait kafka1()
                    #hawait todd2()
                }
                if (runKind === 'last' || runKind === 'all') {
                    #hawait luke1()
                }
                
                if (runKind === 'first' || runKind === 'all') {
                    openDebugConsole({runFunction: 'captureTestDB'})
                }
                
                
                async function todd1() {
                    #hawait drpc({fun: 'danger_clearSentEmails'})
                    
                    #hawait selectBrowserAndSignIn({$tag: '2855b4f4-63f0-4325-ad8a-f36fdf4e2f5a', clientKind: 'writer', browserName: 'todd1', email: 'todd@test.shit.ua', pausePointTitle: 'Todd, a support admin, comes to his workplace...'})
                    art.uiState({$tag: '866bef17-2783-40a5-860d-0d2f69966664', expected: {
                          'TopNavItem-admin-heap': { title: `Куча` },
                           'TopNavItem-dashboard': { active: true, title: `Тодд` },
                           'TopNavItem-support': { title: `Поддержка` },
                           'link-changePassword': { title: `Сменить пароль` },
                           'link-signOut': { title: `Выйти прочь` },
                           'liveBadge-topNavItem-admin-heap': `13`,
                           pageHeader: `Панель`,
                           url: `http://aps-ua-writer.local:3022/dashboard.html`
                    }})
                    
                    testGlobal.controls['TopNavItem-admin-heap'].showHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15, dtop: 0}})
                    #hawait art.pausePoint({title: 'There is some unassigned work, let’s take a look...', $tag: '20f801f5-657b-4176-bd1a-fb78f5af1811'})
                    testGlobal.controls['TopNavItem-admin-heap'].hideHand()
                    
                    // Action
                    #hawait testGlobal.controls['TopNavItem-admin-heap'].click()
                    art.uiState({$tag: '1c3a4a15-4bc4-46f6-afd9-d23433c6d839', expected: {
                         'TopNavItem-admin-heap': { active: true, title: `Куча` },
                          'TopNavItem-dashboard': { title: `Тодд` },
                          'TopNavItem-support': { title: `Поддержка` },
                          'button-showMore': { title: `Показать еще` },
                          'chunk-0.thread-0.button-takeAndReply': { icon: `comment` },
                          'chunk-0.thread-0.newMessages.message-0.message': `Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.`,
                          'chunk-0.thread-0.newMessages.message-0.timestamp': `10/04/2014 16:44:55`,
                          'chunk-0.thread-0.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-0.newMessages.message-0.userLabel-from': `Люк Хуюк`,
                          'chunk-0.thread-0.topic': `И это называется следственной документацией!`,
                          'chunk-0.thread-1.button-takeAndReply': { icon: `comment` },
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
                          'chunk-0.thread-1.newMessages.message-0.timestamp': `06/05/2014 17:33:34`,
                          'chunk-0.thread-1.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-1.newMessages.message-0.userLabel-from': `Даздраперма Дивизионная`,
                          'chunk-0.thread-1.topic': dedent(`
                                В   углу   комнаты   стояли  трое  молодых  людей  -  они разглядывали фотографии фройляйн Бюрстнер,
                                воткнутые в плетеную циновку на стене. На ручке открытого окна висела белая  блузка.`),
                          'chunk-0.thread-2.button-takeAndReply': { icon: `comment` },
                          'chunk-0.thread-2.newMessages.message-0.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                          'chunk-0.thread-2.newMessages.message-0.timestamp': `17/05/2014 02:33:15`,
                          'chunk-0.thread-2.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-2.newMessages.message-0.userLabel-from': `Франц Кафка`,
                          'chunk-0.thread-2.newMessages.message-1.continuation': `В догонку`,
                          'chunk-0.thread-2.newMessages.message-1.message': dedent(`
                                Еще хотел бы добавить вот что.
                                
                                - Сегодня заседания нет, - сказала женщина.
                                - Как это - нет заседания? - спросил он, не поверив.`),
                          'chunk-0.thread-2.newMessages.message-1.timestamp': `17/05/2014 02:35:27`,
                          'chunk-0.thread-2.newMessages.message-2.continuation': `В догонку`,
                          'chunk-0.thread-2.newMessages.message-2.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                          'chunk-0.thread-2.newMessages.message-2.timestamp': `17/05/2014 02:36:02`,
                          'chunk-0.thread-2.topic': `В пустом зале заседаний. Студент. Канцелярии`,
                          'chunk-0.thread-3.button-takeAndReply': { icon: `comment` },
                          'chunk-0.thread-3.newMessages.message-0.message': `Я так и догадалась по вашей речи. Мне лично она  очень  понравилась.  Правда,  я  не  все  слышала - начало пропустила, а под конец лежала со студентом на полу.`,
                          'chunk-0.thread-3.newMessages.message-0.timestamp': `21/05/2014 10:11:59`,
                          'chunk-0.thread-3.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-3.newMessages.message-0.userLabel-from': `Джейн Остин`,
                          'chunk-0.thread-3.topic': `Видно,  собираетесь  навести  здесь  порядок?`,
                          'chunk-0.thread-4.button-takeAndReply': { icon: `comment` },
                          'chunk-0.thread-4.newMessages.message-0.message': `Книги были старые, потрепанные, на одной  переплет был переломлен и обе половинки держались на ниточке.`,
                          'chunk-0.thread-4.newMessages.message-0.timestamp': `26/05/2014 10:51:24`,
                          'chunk-0.thread-4.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-4.newMessages.message-0.userLabel-from': `Жорж Санд`,
                          'chunk-0.thread-4.topic': `Ну  конечно  же!`,
                          'chunk-0.thread-5.button-takeAndReply': { icon: `comment` },
                          'chunk-0.thread-5.newMessages.message-0.message': `И женщине пришлось смахнуть пыль фартуком хотя бы сверху,  прежде чем К. мог взяться за книгу.`,
                          'chunk-0.thread-5.newMessages.message-0.timestamp': `13/06/2014 14:19:32`,
                          'chunk-0.thread-5.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-5.newMessages.message-0.userLabel-from': `Василий Теркин`,
                          'chunk-0.thread-5.newMessages.message-1.continuation': `В догонку`,
                          'chunk-0.thread-5.newMessages.message-1.message': `Он  открыл  ту,  что  лежала  сверху, и увидел неприличную картинку.`,
                          'chunk-0.thread-5.newMessages.message-1.timestamp': `13/06/2014 14:25:13`,
                          'chunk-0.thread-5.topic': `Какая  тут  везде грязь, - сказал К., покачав головой`,
                          'chunk-0.thread-6.button-takeAndReply': { icon: `comment` },
                          'chunk-0.thread-6.newMessages.message-0.message': dedent(`
                                - Я вам помогу! - сказала женщина. - Согласны?
                                - Но разве вы и вправду можете мне  помочь,  не  подвергая себя  опасности?  Ведь  вы  сами  сказали,  что ваш муж целиком зависит от своего начальства.
                                - И все же я вам помогу, - сказала  женщина.  Подите  сюда, надо  все обсудить. А о том, что мне грозит опасность, говорить не стоит.  Я  только  тогда  пугаюсь  опасности,  когда  считаю нужным. Идите сюда.
                                - Она показала на подмостки и попросила его сесть  рядом с ней на ступеньки.
                                
                                - У вас чудесные темные глаза, -сказала она, когда они сели, и заглянула К. в лицо. - Говорят, у меня тоже глаза красивые, но ваши куда красивее. Ведь  я  вас сразу  приметила,  еще  в первый раз, как только вы сюда зашли. Из-за вас я и пробралась потом в зал заседаний. Обычно я никогда этого не делаю, мне даже, собственно говоря,  запрещено  ходить сюда.`),
                          'chunk-0.thread-6.newMessages.message-0.timestamp': `16/07/2014 08:24:17`,
                          'chunk-0.thread-6.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-6.newMessages.message-0.userLabel-from': `Карлос Кастанеда`,
                          'chunk-0.thread-6.topic': `Так вот какие юридические книги тут изучают!`,
                          'chunk-0.thread-7.button-takeAndReply': { icon: `comment` },
                          'chunk-0.thread-7.newMessages.message-0.message': `Давно  могли уйти,  никто  и  не  заметил бы вашего отсутствия. Да, да, надо было вам уйти, как только я пришел, и уйти сразу, немедленно. В этих словах слышалась не только сдержанная злоба, в  них ясно  чувствовалось высокомерие будущего чиновника по отношению к неприятному для него обвиняемому.`,
                          'chunk-0.thread-7.newMessages.message-0.timestamp': `17/07/2014 10:33:08`,
                          'chunk-0.thread-7.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-7.newMessages.message-0.userLabel-from': `Тело Странное`,
                          'chunk-0.thread-7.topic': `Если вам так не терпится, можете  уходить`,
                          'chunk-0.thread-8.button-takeAndReply': { icon: `comment` },
                          'chunk-0.thread-8.newMessages.message-0.message': `Его за мной прислал следователь, мне с вами идти никак нельзя, этот маленький уродец, - тут  она провела  рукой  по лицу студента, этот маленький уродец меня не отпустит.`,
                          'chunk-0.thread-8.newMessages.message-0.timestamp': `06/08/2014 04:33:37`,
                          'chunk-0.thread-8.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-8.newMessages.message-0.userLabel-from': `Регина Дубовицкая`,
                          'chunk-0.thread-8.topic': `Ничего  не  поделаешь`,
                          'chunk-0.thread-9.button-takeAndReply': { icon: `comment` },
                          'chunk-0.thread-9.newMessages.message-0.message': `К. все еще стоял в дверях. Он должен был признаться, что женщина не только обманула его, но и солгала, что ее несут к следователю. Не станет  же  следователь сидеть  на  чердаке  и  дожидаться  ее.`,
                          'chunk-0.thread-9.newMessages.message-0.timestamp': `15/08/2014 13:32:42`,
                          'chunk-0.thread-9.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-9.newMessages.message-0.userLabel-from': `Варсоновий Оптинский`,
                          'chunk-0.thread-9.topic': `Оба  исчезли за поворотом`,
                          'liveBadge-topNavItem-admin-heap': `13`,
                          pageHeader: `Куча работы`,
                          'tabs-main.support:active.liveBadge-supportTab': `13`,
                          'tabs-main.support:active.title': `Поддержка`,
                          url: `http://aps-ua-writer.local:3022/admin-heap.html`
                    }})
                    #hawait art.pausePoint({title: 'A lot of stuff, let’s do some scrolling...', $tag: 'a86e6b75-0140-4347-a961-bf9886937806', locus: 'top-right'})
                    #hawait art.scroll({origY: 0, destY: 'bottom'})
                    #hawait art.pausePoint({title: 'Clicking "Show More" button at the bottom...', $tag: 'dcf633b6-0cd6-43bc-81f8-5920ff60a795', locus: 'top-right'})
                    
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
                    
                    art.uiState({$tag: '404c23b9-e496-41b5-a105-71ae5c44ed5f', expected: {
                         'Select-ordering': { title: `Сначала новые` },
                          'TopNavItem-admin-heap': { title: `Куча` },
                          'TopNavItem-dashboard': { title: `Тодд` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'button-edit': { icon: `edit` },
                          'button-plus': { icon: `comment` },
                          'chunk-0.message-0.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                          'chunk-0.message-0.newLabel': `Новое`,
                          'chunk-0.message-0.timestamp': `17/05/2014 02:36:02`,
                          'chunk-0.message-0.to': `В рельсу`,
                          'chunk-0.message-0.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-1.message': dedent(`
                                Еще хотел бы добавить вот что.
                                
                                - Сегодня заседания нет, - сказала женщина.
                                - Как это - нет заседания? - спросил он, не поверив.`),
                          'chunk-0.message-1.newLabel': `Новое`,
                          'chunk-0.message-1.timestamp': `17/05/2014 02:35:27`,
                          'chunk-0.message-1.to': `В рельсу`,
                          'chunk-0.message-1.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-2.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                          'chunk-0.message-2.newLabel': `Новое`,
                          'chunk-0.message-2.timestamp': `17/05/2014 02:33:15`,
                          'chunk-0.message-2.to': `В рельсу`,
                          'chunk-0.message-2.userLabel-from': `Франц Кафка`,
                          'liveBadge-topNavItem-admin-heap': `12`,
                          'liveBadge-topNavItem-support': `3/1`,
                          pageHeader: `Запрос в поддержку № 308`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=308`
                    }})
                    
                    // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                    testGlobal.controls['TopNavItem-admin-heap'].showHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15, dtop: 0}})
                    #hawait art.pausePoint({title: 'Amount of work in heap decreased, since we’ve just taken one piece from it', $tag: '3ebb103e-7f0a-4669-9a29-2cbb2bbed460'})
                    testGlobal.controls['TopNavItem-admin-heap'].hideHand()
                    testGlobal.controls['TopNavItem-support'].showHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15, dtop: 0}})
                    #hawait art.pausePoint({title: 'We have three new messages contained in one thread to deal with. This badge will be hanging there until we address it by replying.\nBut first let’s go back to heap and take one more task...', $tag: '348c92fd-d749-4fad-8c4e-eef3754548cf'})
                    testGlobal.controls['TopNavItem-support'].hideHand()
                    
                    // Action
                    #hawait testGlobal.controls['TopNavItem-admin-heap'].click()
                    art.uiState({$tag: '8fc8247d-2b0a-492b-a495-ee782f56eeb4', expected: {
                         'TopNavItem-admin-heap': { active: true, title: `Куча` },
                          'TopNavItem-dashboard': { title: `Тодд` },
                          'TopNavItem-support': { title: `Поддержка` },
                          'button-showMore': { title: `Показать еще` },
                          'chunk-0.thread-0.button-takeAndReply': { icon: `comment` },
                          'chunk-0.thread-0.newMessages.message-0.message': `Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.`,
                          'chunk-0.thread-0.newMessages.message-0.timestamp': `10/04/2014 16:44:55`,
                          'chunk-0.thread-0.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-0.newMessages.message-0.userLabel-from': `Люк Хуюк`,
                          'chunk-0.thread-0.topic': `И это называется следственной документацией!`,
                          'chunk-0.thread-1.button-takeAndReply': { icon: `comment` },
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
                          'chunk-0.thread-1.newMessages.message-0.timestamp': `06/05/2014 17:33:34`,
                          'chunk-0.thread-1.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-1.newMessages.message-0.userLabel-from': `Даздраперма Дивизионная`,
                          'chunk-0.thread-1.topic': dedent(`
                                В   углу   комнаты   стояли  трое  молодых  людей  -  они разглядывали фотографии фройляйн Бюрстнер,
                                воткнутые в плетеную циновку на стене. На ручке открытого окна висела белая  блузка.`),
                          'chunk-0.thread-2.button-takeAndReply': { icon: `comment` },
                          'chunk-0.thread-2.newMessages.message-0.message': `Я так и догадалась по вашей речи. Мне лично она  очень  понравилась.  Правда,  я  не  все  слышала - начало пропустила, а под конец лежала со студентом на полу.`,
                          'chunk-0.thread-2.newMessages.message-0.timestamp': `21/05/2014 10:11:59`,
                          'chunk-0.thread-2.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-2.newMessages.message-0.userLabel-from': `Джейн Остин`,
                          'chunk-0.thread-2.topic': `Видно,  собираетесь  навести  здесь  порядок?`,
                          'chunk-0.thread-3.button-takeAndReply': { icon: `comment` },
                          'chunk-0.thread-3.newMessages.message-0.message': `Книги были старые, потрепанные, на одной  переплет был переломлен и обе половинки держались на ниточке.`,
                          'chunk-0.thread-3.newMessages.message-0.timestamp': `26/05/2014 10:51:24`,
                          'chunk-0.thread-3.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-3.newMessages.message-0.userLabel-from': `Жорж Санд`,
                          'chunk-0.thread-3.topic': `Ну  конечно  же!`,
                          'chunk-0.thread-4.button-takeAndReply': { icon: `comment` },
                          'chunk-0.thread-4.newMessages.message-0.message': `И женщине пришлось смахнуть пыль фартуком хотя бы сверху,  прежде чем К. мог взяться за книгу.`,
                          'chunk-0.thread-4.newMessages.message-0.timestamp': `13/06/2014 14:19:32`,
                          'chunk-0.thread-4.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-4.newMessages.message-0.userLabel-from': `Василий Теркин`,
                          'chunk-0.thread-4.newMessages.message-1.continuation': `В догонку`,
                          'chunk-0.thread-4.newMessages.message-1.message': `Он  открыл  ту,  что  лежала  сверху, и увидел неприличную картинку.`,
                          'chunk-0.thread-4.newMessages.message-1.timestamp': `13/06/2014 14:25:13`,
                          'chunk-0.thread-4.topic': `Какая  тут  везде грязь, - сказал К., покачав головой`,
                          'chunk-0.thread-5.button-takeAndReply': { icon: `comment` },
                          'chunk-0.thread-5.newMessages.message-0.message': dedent(`
                                - Я вам помогу! - сказала женщина. - Согласны?
                                - Но разве вы и вправду можете мне  помочь,  не  подвергая себя  опасности?  Ведь  вы  сами  сказали,  что ваш муж целиком зависит от своего начальства.
                                - И все же я вам помогу, - сказала  женщина.  Подите  сюда, надо  все обсудить. А о том, что мне грозит опасность, говорить не стоит.  Я  только  тогда  пугаюсь  опасности,  когда  считаю нужным. Идите сюда.
                                - Она показала на подмостки и попросила его сесть  рядом с ней на ступеньки.
                                
                                - У вас чудесные темные глаза, -сказала она, когда они сели, и заглянула К. в лицо. - Говорят, у меня тоже глаза красивые, но ваши куда красивее. Ведь  я  вас сразу  приметила,  еще  в первый раз, как только вы сюда зашли. Из-за вас я и пробралась потом в зал заседаний. Обычно я никогда этого не делаю, мне даже, собственно говоря,  запрещено  ходить сюда.`),
                          'chunk-0.thread-5.newMessages.message-0.timestamp': `16/07/2014 08:24:17`,
                          'chunk-0.thread-5.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-5.newMessages.message-0.userLabel-from': `Карлос Кастанеда`,
                          'chunk-0.thread-5.topic': `Так вот какие юридические книги тут изучают!`,
                          'chunk-0.thread-6.button-takeAndReply': { icon: `comment` },
                          'chunk-0.thread-6.newMessages.message-0.message': `Давно  могли уйти,  никто  и  не  заметил бы вашего отсутствия. Да, да, надо было вам уйти, как только я пришел, и уйти сразу, немедленно. В этих словах слышалась не только сдержанная злоба, в  них ясно  чувствовалось высокомерие будущего чиновника по отношению к неприятному для него обвиняемому.`,
                          'chunk-0.thread-6.newMessages.message-0.timestamp': `17/07/2014 10:33:08`,
                          'chunk-0.thread-6.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-6.newMessages.message-0.userLabel-from': `Тело Странное`,
                          'chunk-0.thread-6.topic': `Если вам так не терпится, можете  уходить`,
                          'chunk-0.thread-7.button-takeAndReply': { icon: `comment` },
                          'chunk-0.thread-7.newMessages.message-0.message': `Его за мной прислал следователь, мне с вами идти никак нельзя, этот маленький уродец, - тут  она провела  рукой  по лицу студента, этот маленький уродец меня не отпустит.`,
                          'chunk-0.thread-7.newMessages.message-0.timestamp': `06/08/2014 04:33:37`,
                          'chunk-0.thread-7.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-7.newMessages.message-0.userLabel-from': `Регина Дубовицкая`,
                          'chunk-0.thread-7.topic': `Ничего  не  поделаешь`,
                          'chunk-0.thread-8.button-takeAndReply': { icon: `comment` },
                          'chunk-0.thread-8.newMessages.message-0.message': `К. все еще стоял в дверях. Он должен был признаться, что женщина не только обманула его, но и солгала, что ее несут к следователю. Не станет  же  следователь сидеть  на  чердаке  и  дожидаться  ее.`,
                          'chunk-0.thread-8.newMessages.message-0.timestamp': `15/08/2014 13:32:42`,
                          'chunk-0.thread-8.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-8.newMessages.message-0.userLabel-from': `Варсоновий Оптинский`,
                          'chunk-0.thread-8.topic': `Оба  исчезли за поворотом`,
                          'chunk-0.thread-9.button-takeAndReply': { icon: `comment` },
                          'chunk-0.thread-9.newMessages.message-0.message': `Вечно  ее  от  меня уносят.  Сегодня воскресенье, работать я не обязан, а мне вдруг дают совершенно ненужные  поручения,  лишь  бы  услать  отсюда.`,
                          'chunk-0.thread-9.newMessages.message-0.timestamp': `11/10/2014 08:15:11`,
                          'chunk-0.thread-9.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-9.newMessages.message-0.userLabel-from': `Федор Достоевский`,
                          'chunk-0.thread-9.topic': `Вот видите!`,
                          'liveBadge-topNavItem-admin-heap': `12`,
                          'liveBadge-topNavItem-support': `3/1`,
                          pageHeader: `Куча работы`,
                          'tabs-main.support:active.liveBadge-supportTab': `12`,
                          'tabs-main.support:active.title': `Поддержка`,
                          url: `http://aps-ua-writer.local:3022/admin-heap.html`
                    }})
                    
                    // Action
                    #hawait testGlobal.controls['chunk-0.thread-0.button-takeAndReply'].click()
                    art.uiState({$tag: '9dde55d6-0488-4ec6-9443-a96370c7fd4b', expected: {
                         'Select-ordering': { title: `Сначала новые` },
                          'TopNavItem-admin-heap': { title: `Куча` },
                          'TopNavItem-dashboard': { title: `Тодд` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'button-edit': { icon: `edit` },
                          'button-plus': { icon: `comment` },
                          'chunk-0.message-0.message': `Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.`,
                          'chunk-0.message-0.newLabel': `Новое`,
                          'chunk-0.message-0.timestamp': `10/04/2014 16:44:55`,
                          'chunk-0.message-0.to': `В рельсу`,
                          'chunk-0.message-0.userLabel-from': `Люк Хуюк`,
                          'liveBadge-topNavItem-admin-heap': `11`,
                          'liveBadge-topNavItem-support': `4/2`,
                          pageHeader: `Запрос в поддержку № 12`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=12`
                    }})

                    // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                    testGlobal.controls['TopNavItem-support'].showHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15, dtop: 0}})
                    #hawait art.pausePoint({title: 'Numbers changed again. Now we have to deal with 4 support messages contained in 2 threads.\nBy clicking on Support menu we’ll see what those threads are...', $tag: '182c9720-f9ed-4afe-8c46-5b45c482e9a9'})
                    testGlobal.controls['TopNavItem-support'].hideHand()
                    
                    // Action
                    #hawait testGlobal.controls['TopNavItem-support'].click()
                    art.uiState({$tag: '0020d0bf-5a2c-4287-a4b6-f1eca4c36ca3', expected: {
                         'TopNavItem-admin-heap': { title: `Куча` },
                          'TopNavItem-dashboard': { title: `Тодд` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'chunk-0.thread-0.link-topic': { title: `В пустом зале заседаний. Студент. Канцелярии` },
                          'chunk-0.thread-0.newMessages.link-andMore': { title: `...и еще одно новое сообщение` },
                          'chunk-0.thread-0.newMessages.message-0.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                          'chunk-0.thread-0.newMessages.message-0.newLabel': `Новое`,
                          'chunk-0.thread-0.newMessages.message-0.timestamp': `17/05/2014 02:36:02`,
                          'chunk-0.thread-0.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-0.newMessages.message-0.userLabel-from': `Франц Кафка`,
                          'chunk-0.thread-0.newMessages.message-1.message': dedent(`
                                Еще хотел бы добавить вот что.
                                
                                - Сегодня заседания нет, - сказала женщина.
                                - Как это - нет заседания? - спросил он, не поверив.`),
                          'chunk-0.thread-0.newMessages.message-1.newLabel': `Новое`,
                          'chunk-0.thread-0.newMessages.message-1.timestamp': `17/05/2014 02:35:27`,
                          'chunk-0.thread-0.newMessages.message-1.to': `В рельсу`,
                          'chunk-0.thread-0.newMessages.message-1.userLabel-from': `Франц Кафка`,
                          'chunk-0.thread-1.link-topic': { title: `И это называется следственной документацией!` },
                          'chunk-0.thread-1.newMessages.message-0.message': `Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.`,
                          'chunk-0.thread-1.newMessages.message-0.newLabel': `Новое`,
                          'chunk-0.thread-1.newMessages.message-0.timestamp': `10/04/2014 16:44:55`,
                          'chunk-0.thread-1.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-1.newMessages.message-0.userLabel-from': `Люк Хуюк`,
                          'liveBadge-topNavItem-admin-heap': `11`,
                          'liveBadge-topNavItem-support': `4/2`,
                          pageHeader: `Поддержка`,
                          'tabs-main.updated:active.title': `Новые`,
                          url: `http://aps-ua-writer.local:3022/support.html`
                    }})
                    
                    testGlobal.controls['chunk-0.thread-0.newMessages.link-andMore'].showHand({testActionHandOpts: {pointingFrom: 'left', dleft: -4, dtop: 2}})
                    #hawait art.pausePoint({title: 'To keep the list succinct, maximum two (most recent) new messages are showed per thread.\nIn order to see everything, simply switch to a particular thread. One way of doing which is via this link...', $tag: '27ba7948-693a-4c93-a054-16a534090567'})
                    testGlobal.controls['chunk-0.thread-0.newMessages.link-andMore'].hideHand()
                    
                    // Action
                    #hawait testGlobal.controls['chunk-0.thread-0.newMessages.link-andMore'].click()
                    art.uiState({$tag: 'b82ede90-f409-4dee-b1d6-e66b56e51152', expected: {
                         'Select-ordering': { title: `Сначала новые` },
                          'TopNavItem-admin-heap': { title: `Куча` },
                          'TopNavItem-dashboard': { title: `Тодд` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'button-edit': { icon: `edit` },
                          'button-plus': { icon: `comment` },
                          'chunk-0.message-0.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                          'chunk-0.message-0.newLabel': `Новое`,
                          'chunk-0.message-0.timestamp': `17/05/2014 02:36:02`,
                          'chunk-0.message-0.to': `В рельсу`,
                          'chunk-0.message-0.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-1.message': dedent(`
                                Еще хотел бы добавить вот что.
                                
                                - Сегодня заседания нет, - сказала женщина.
                                - Как это - нет заседания? - спросил он, не поверив.`),
                          'chunk-0.message-1.newLabel': `Новое`,
                          'chunk-0.message-1.timestamp': `17/05/2014 02:35:27`,
                          'chunk-0.message-1.to': `В рельсу`,
                          'chunk-0.message-1.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-2.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                          'chunk-0.message-2.newLabel': `Новое`,
                          'chunk-0.message-2.timestamp': `17/05/2014 02:33:15`,
                          'chunk-0.message-2.to': `В рельсу`,
                          'chunk-0.message-2.userLabel-from': `Франц Кафка`,
                          'liveBadge-topNavItem-admin-heap': `11`,
                          'liveBadge-topNavItem-support': `4/2`,
                          pageHeader: `Запрос в поддержку № 308`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=308`
                    }})


                    // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                    #hawait art.pausePoint({title: 'Changing ordering to show old messages first...', $tag: '1e77736a-3498-4b5d-8b1e-94c0f7dd6c56'})
                    // Action
                    #hawait testGlobal.controls['Select-ordering'].setValue({value: 'asc', testActionHandOpts: {pointingFrom: 'left', dtop: 48}})
                    art.uiState({$tag: '73952bab-8024-4351-8656-0966860aa31b', expected: {
                         'Select-ordering': { title: `Сначала старые` },
                          'TopNavItem-admin-heap': { title: `Куча` },
                          'TopNavItem-dashboard': { title: `Тодд` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'button-edit': { icon: `edit` },
                          'button-plus': { icon: `comment` },
                          'chunk-0.message-0.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                          'chunk-0.message-0.newLabel': `Новое`,
                          'chunk-0.message-0.timestamp': `17/05/2014 02:33:15`,
                          'chunk-0.message-0.to': `В рельсу`,
                          'chunk-0.message-0.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-1.message': dedent(`
                                Еще хотел бы добавить вот что.
                                
                                - Сегодня заседания нет, - сказала женщина.
                                - Как это - нет заседания? - спросил он, не поверив.`),
                          'chunk-0.message-1.newLabel': `Новое`,
                          'chunk-0.message-1.timestamp': `17/05/2014 02:35:27`,
                          'chunk-0.message-1.to': `В рельсу`,
                          'chunk-0.message-1.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-2.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                          'chunk-0.message-2.newLabel': `Новое`,
                          'chunk-0.message-2.timestamp': `17/05/2014 02:36:02`,
                          'chunk-0.message-2.to': `В рельсу`,
                          'chunk-0.message-2.userLabel-from': `Франц Кафка`,
                          'liveBadge-topNavItem-admin-heap': `11`,
                          'liveBadge-topNavItem-support': `4/2`,
                          pageHeader: `Запрос в поддержку № 308`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=308&ordering=asc`
                    }})
                    
                    #hawait art.pausePoint({title: 'Changing it back to conveniently show new stuff first', $tag: '79a2d7fc-5509-41fc-b606-b82bdccd5d68'})
                    // Action
                    #hawait testGlobal.controls['Select-ordering'].setValue({value: 'desc', testActionHandOpts: {pointingFrom: 'left', dtop: 30}})
                    art.uiState({$tag: '1920f57c-db76-40ff-9f85-8ebdd8faf2f3', expected: {
                         'Select-ordering': { title: `Сначала новые` },
                          'TopNavItem-admin-heap': { title: `Куча` },
                          'TopNavItem-dashboard': { title: `Тодд` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'button-edit': { icon: `edit` },
                          'button-plus': { icon: `comment` },
                          'chunk-0.message-0.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                          'chunk-0.message-0.newLabel': `Новое`,
                          'chunk-0.message-0.timestamp': `17/05/2014 02:36:02`,
                          'chunk-0.message-0.to': `В рельсу`,
                          'chunk-0.message-0.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-1.message': dedent(`
                                Еще хотел бы добавить вот что.
                                
                                - Сегодня заседания нет, - сказала женщина.
                                - Как это - нет заседания? - спросил он, не поверив.`),
                          'chunk-0.message-1.newLabel': `Новое`,
                          'chunk-0.message-1.timestamp': `17/05/2014 02:35:27`,
                          'chunk-0.message-1.to': `В рельсу`,
                          'chunk-0.message-1.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-2.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                          'chunk-0.message-2.newLabel': `Новое`,
                          'chunk-0.message-2.timestamp': `17/05/2014 02:33:15`,
                          'chunk-0.message-2.to': `В рельсу`,
                          'chunk-0.message-2.userLabel-from': `Франц Кафка`,
                          'liveBadge-topNavItem-admin-heap': `11`,
                          'liveBadge-topNavItem-support': `4/2`,
                          pageHeader: `Запрос в поддержку № 308`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=308&ordering=desc`
                    }})
                    
                    // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                    #hawait art.pausePoint({title: 'Will respond...', $tag: '3687412d-c52a-4bdf-bc8e-1c588df5da22'})
                    // Action
                    #hawait testGlobal.controls['button-plus'].click()
                    art.uiState({$tag: 'bd47917a-b212-4d03-8910-5af36b2f7ebc', expected: {
                         'Input-message': ``,
                          'TopNavItem-admin-heap': { title: `Куча` },
                          'TopNavItem-dashboard': { title: `Тодд` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'button-cancel': { title: `Передумал` },
                          'button-primary': { title: `Запостить` },
                          'chunk-0.message-0.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                          'chunk-0.message-0.newLabel': `Новое`,
                          'chunk-0.message-0.timestamp': `17/05/2014 02:36:02`,
                          'chunk-0.message-0.to': `В рельсу`,
                          'chunk-0.message-0.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-1.message': dedent(`
                                Еще хотел бы добавить вот что.
                                
                                - Сегодня заседания нет, - сказала женщина.
                                - Как это - нет заседания? - спросил он, не поверив.`),
                          'chunk-0.message-1.newLabel': `Новое`,
                          'chunk-0.message-1.timestamp': `17/05/2014 02:35:27`,
                          'chunk-0.message-1.to': `В рельсу`,
                          'chunk-0.message-1.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-2.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                          'chunk-0.message-2.newLabel': `Новое`,
                          'chunk-0.message-2.timestamp': `17/05/2014 02:33:15`,
                          'chunk-0.message-2.to': `В рельсу`,
                          'chunk-0.message-2.userLabel-from': `Франц Кафка`,
                          'liveBadge-topNavItem-admin-heap': `11`,
                          'liveBadge-topNavItem-support': `4/2`,
                          pageHeader: `Запрос в поддержку № 308`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=308&ordering=desc`
                    }})
                    
                    // Inputs
                    #hawait testGlobal.controls['Input-message'].setValue({value: 'Та я понял, что ты писатель... В чем дело-то?'})
                    // Action
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/23 13:56:26'})
                    #hawait testGlobal.controls['button-primary'].click()
                    art.uiState({$tag: '0bb446ca-4e6e-4bcf-b9c9-687f1f3a2f29', expected: {
                         'Select-ordering': { title: `Сначала новые` },
                          'TopNavItem-admin-heap': { title: `Куча` },
                          'TopNavItem-dashboard': { title: `Тодд` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'button-edit': { icon: `edit` },
                          'button-plus': { icon: `comment` },
                          'chunk-0.message-0.message': `Та я понял, что ты писатель... В чем дело-то?`,
                          'chunk-0.message-0.timestamp': `23/07/2016 16:56:26`,
                          'chunk-0.message-0.userLabel-from': `Тодд Суппортод`,
                          'chunk-0.message-0.userLabel-to': `Франц Кафка`,
                          'chunk-0.message-1.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                          'chunk-0.message-1.newLabel:aniFadeOutDelayed': `Новое`,
                          'chunk-0.message-1.timestamp': `17/05/2014 02:36:02`,
                          'chunk-0.message-1.to': `В рельсу`,
                          'chunk-0.message-1.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-2.message': dedent(`
                                Еще хотел бы добавить вот что.
                                
                                - Сегодня заседания нет, - сказала женщина.
                                - Как это - нет заседания? - спросил он, не поверив.`),
                          'chunk-0.message-2.newLabel:aniFadeOutDelayed': `Новое`,
                          'chunk-0.message-2.timestamp': `17/05/2014 02:35:27`,
                          'chunk-0.message-2.to': `В рельсу`,
                          'chunk-0.message-2.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-3.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                          'chunk-0.message-3.newLabel:aniFadeOutDelayed': `Новое`,
                          'chunk-0.message-3.timestamp': `17/05/2014 02:33:15`,
                          'chunk-0.message-3.to': `В рельсу`,
                          'chunk-0.message-3.userLabel-from': `Франц Кафка`,
                          'liveBadge-topNavItem-admin-heap': `11`,
                          'liveBadge-topNavItem-support': `1/1`,
                          pageHeader: `Запрос в поддержку № 308`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=308`
                    }})
                    
                    testGlobal.controls['TopNavItem-support'].showHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15, dtop: 0}})
                    #hawait art.pausePoint({title: 'Admin replied, so "New" labels faded away and number of support items to address decreased', $tag: 'c5f3e1ee-0621-4d66-beb9-287847a95444'})
                    testGlobal.controls['TopNavItem-support'].hideHand()
                }
                
                async function kafka1() {
                    #hawait selectBrowserAndSignIn({$tag: '24cb9937-d5eb-4b10-a8b0-19428e9db3ef', clientKind: 'writer', browserName: 'kafka1', email: 'kafka@test.shit.ua', pausePointTitle: 'Another user, Franz Kafka, who is a writer, comes into play...'})
                    art.uiState({$tag: '2fcf578d-3b0d-4f70-b3a1-7d4d892ecd91', expected: {
                         'TopNavItem-dashboard': { active: true, title: `Франц` },
                          'TopNavItem-faq': { title: `ЧаВо` },
                          'TopNavItem-orders': { title: `Мои заказы` },
                          'TopNavItem-prices': { title: `Цены` },
                          'TopNavItem-profile': { title: `Профиль` },
                          'TopNavItem-store': { title: `Аукцион` },
                          'TopNavItem-support': { title: `Поддержка` },
                          'TopNavItem-why': { title: `Почему мы?` },
                          'link-changePassword': { title: `Сменить пароль` },
                          'link-signOut': { title: `Выйти прочь` },
                          'liveBadge-topNavItem-support': `1`,
                          pageHeader: `Панель`,
                          url: `http://aps-ua-writer.local:3022/dashboard.html`
                    }})
                    
                    // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                    testGlobal.controls['TopNavItem-support'].showHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15, dtop: 0}})
                    #hawait art.pausePoint({title: 'We have an unread message', $tag: 'ea6d6e4a-c4d6-489c-97c9-9adb34b284d7'})
                    testGlobal.controls['TopNavItem-support'].hideHand()
                    
                    // Action
                    #hawait testGlobal.controls['TopNavItem-support'].click()
                    art.uiState({$tag: 'e03093b1-3834-45e9-9f7c-724536571ecc', expected: {
                         'TopNavItem-dashboard': { title: `Франц` },
                          'TopNavItem-faq': { title: `ЧаВо` },
                          'TopNavItem-orders': { title: `Мои заказы` },
                          'TopNavItem-prices': { title: `Цены` },
                          'TopNavItem-profile': { title: `Профиль` },
                          'TopNavItem-store': { title: `Аукцион` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'TopNavItem-why': { title: `Почему мы?` },
                          'chunk-0.thread-0.link-topic': { title: `В пустом зале заседаний. Студент. Канцелярии` },
                          'chunk-0.thread-0.newMessages.message-0.message': `Та я понял, что ты писатель... В чем дело-то?`,
                          'chunk-0.thread-0.newMessages.message-0.newLabel': `Новое`,
                          'chunk-0.thread-0.newMessages.message-0.timestamp': `23/07/2016 16:56:26`,
                          'chunk-0.thread-0.newMessages.message-0.userLabel-from': `Тодд Суппортод`,
                          'chunk-0.thread-0.newMessages.message-0.userLabel-to': `Франц Кафка`,
                          'chunk-0.thread-0.oldMessages.link-andMore': { title: `...и еще 2 старых сообщения` },
                          'chunk-0.thread-0.oldMessages.message-0.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                          'chunk-0.thread-0.oldMessages.message-0.timestamp': `17/05/2014 02:36:02`,
                          'chunk-0.thread-0.oldMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-0.oldMessages.message-0.userLabel-from': `Франц Кафка`,
                          'liveBadge-topNavItem-support': `1`,
                          pageHeader: `Поддержка`,
                          'tabs-main.updated:active.title': `Новые`,
                          url: `http://aps-ua-writer.local:3022/support.html`
                    }})
                    
                    // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                    testGlobal.controls['chunk-0.thread-0.oldMessages.link-andMore'].showHand({testActionHandOpts: {pointingFrom: 'left', dleft: -5, dtop: 2}})
                    #hawait art.pausePoint({title: 'Let’s look at whole thread...', $tag: 'c36ed08f-1a1d-4307-b53b-bb93d503d535'})
                    testGlobal.controls['chunk-0.thread-0.oldMessages.link-andMore'].hideHand()
                    
                    // Action
                    #hawait testGlobal.controls['chunk-0.thread-0.oldMessages.link-andMore'].click()
                    art.uiState({$tag: '627276cd-3bfe-4932-9e2f-e2845f6015f5', expected: {
                         'Select-ordering': { title: `Сначала новые` },
                          'TopNavItem-dashboard': { title: `Франц` },
                          'TopNavItem-faq': { title: `ЧаВо` },
                          'TopNavItem-orders': { title: `Мои заказы` },
                          'TopNavItem-prices': { title: `Цены` },
                          'TopNavItem-profile': { title: `Профиль` },
                          'TopNavItem-store': { title: `Аукцион` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'TopNavItem-why': { title: `Почему мы?` },
                          'button-edit': { icon: `edit` },
                          'button-plus': { icon: `comment` },
                          'chunk-0.message-0.message': `Та я понял, что ты писатель... В чем дело-то?`,
                          'chunk-0.message-0.newLabel': `Новое`,
                          'chunk-0.message-0.timestamp': `23/07/2016 16:56:26`,
                          'chunk-0.message-0.userLabel-from': `Тодд Суппортод`,
                          'chunk-0.message-0.userLabel-to': `Франц Кафка`,
                          'chunk-0.message-1.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                          'chunk-0.message-1.timestamp': `17/05/2014 02:36:02`,
                          'chunk-0.message-1.to': `В рельсу`,
                          'chunk-0.message-1.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-2.message': dedent(`
                                Еще хотел бы добавить вот что.
                                
                                - Сегодня заседания нет, - сказала женщина.
                                - Как это - нет заседания? - спросил он, не поверив.`),
                          'chunk-0.message-2.timestamp': `17/05/2014 02:35:27`,
                          'chunk-0.message-2.to': `В рельсу`,
                          'chunk-0.message-2.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-3.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                          'chunk-0.message-3.timestamp': `17/05/2014 02:33:15`,
                          'chunk-0.message-3.to': `В рельсу`,
                          'chunk-0.message-3.userLabel-from': `Франц Кафка`,
                          'liveBadge-topNavItem-support': `1`,
                          pageHeader: `Запрос в поддержку № 308`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=308`
                    }})
                    
                    #hawait art.pausePoint({title: 'Will reply...', $tag: 'ae5e858c-baea-4321-a024-4f07629c19c1'})

                    // Action
                    #hawait testGlobal.controls['button-plus'].click()
                    art.uiState({$tag: '3e196890-8de2-4003-8dc7-c34fbec270b2', expected: {
                         'Input-message': ``,
                          'TopNavItem-dashboard': { title: `Франц` },
                          'TopNavItem-faq': { title: `ЧаВо` },
                          'TopNavItem-orders': { title: `Мои заказы` },
                          'TopNavItem-prices': { title: `Цены` },
                          'TopNavItem-profile': { title: `Профиль` },
                          'TopNavItem-store': { title: `Аукцион` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'TopNavItem-why': { title: `Почему мы?` },
                          'button-cancel': { title: `Передумал` },
                          'button-primary': { title: `Запостить` },
                          'chunk-0.message-0.message': `Та я понял, что ты писатель... В чем дело-то?`,
                          'chunk-0.message-0.newLabel': `Новое`,
                          'chunk-0.message-0.timestamp': `23/07/2016 16:56:26`,
                          'chunk-0.message-0.userLabel-from': `Тодд Суппортод`,
                          'chunk-0.message-0.userLabel-to': `Франц Кафка`,
                          'chunk-0.message-1.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                          'chunk-0.message-1.timestamp': `17/05/2014 02:36:02`,
                          'chunk-0.message-1.to': `В рельсу`,
                          'chunk-0.message-1.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-2.message': dedent(`
                                Еще хотел бы добавить вот что.
                                
                                - Сегодня заседания нет, - сказала женщина.
                                - Как это - нет заседания? - спросил он, не поверив.`),
                          'chunk-0.message-2.timestamp': `17/05/2014 02:35:27`,
                          'chunk-0.message-2.to': `В рельсу`,
                          'chunk-0.message-2.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-3.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                          'chunk-0.message-3.timestamp': `17/05/2014 02:33:15`,
                          'chunk-0.message-3.to': `В рельсу`,
                          'chunk-0.message-3.userLabel-from': `Франц Кафка`,
                          'liveBadge-topNavItem-support': `1`,
                          pageHeader: `Запрос в поддержку № 308`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=308`
                    }})

                    // Inputs
                    #hawait testGlobal.controls['Input-message'].setValue({value: 'Ни в чем. Просто пописать. Я ж писатель, ага.'})
                    // Action
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/23 14:08:11'})
                    #hawait testGlobal.controls['button-primary'].click()
                    art.uiState({$tag: '2afc55df-9079-4112-a7c0-da91803923c6', expected: {
                         'Select-ordering': { title: `Сначала новые` },
                          'TopNavItem-dashboard': { title: `Франц` },
                          'TopNavItem-faq': { title: `ЧаВо` },
                          'TopNavItem-orders': { title: `Мои заказы` },
                          'TopNavItem-prices': { title: `Цены` },
                          'TopNavItem-profile': { title: `Профиль` },
                          'TopNavItem-store': { title: `Аукцион` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'TopNavItem-why': { title: `Почему мы?` },
                          'button-edit': { icon: `edit` },
                          'button-plus': { icon: `comment` },
                          'chunk-0.message-0.message': `Ни в чем. Просто пописать. Я ж писатель, ага.`,
                          'chunk-0.message-0.timestamp': `23/07/2016 17:08:11`,
                          'chunk-0.message-0.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-0.userLabel-to': `Тодд Суппортод`,
                          'chunk-0.message-1.message': `Та я понял, что ты писатель... В чем дело-то?`,
                          'chunk-0.message-1.newLabel:aniFadeOutDelayed': `Новое`,
                          'chunk-0.message-1.timestamp': `23/07/2016 16:56:26`,
                          'chunk-0.message-1.userLabel-from': `Тодд Суппортод`,
                          'chunk-0.message-1.userLabel-to': `Франц Кафка`,
                          'chunk-0.message-2.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                          'chunk-0.message-2.timestamp': `17/05/2014 02:36:02`,
                          'chunk-0.message-2.to': `В рельсу`,
                          'chunk-0.message-2.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-3.message': dedent(`
                                Еще хотел бы добавить вот что.
                                
                                - Сегодня заседания нет, - сказала женщина.
                                - Как это - нет заседания? - спросил он, не поверив.`),
                          'chunk-0.message-3.timestamp': `17/05/2014 02:35:27`,
                          'chunk-0.message-3.to': `В рельсу`,
                          'chunk-0.message-3.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-4.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                          'chunk-0.message-4.timestamp': `17/05/2014 02:33:15`,
                          'chunk-0.message-4.to': `В рельсу`,
                          'chunk-0.message-4.userLabel-from': `Франц Кафка`,
                          pageHeader: `Запрос в поддержку № 308`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=308`
                    }})
                }
                
                async function todd2() {
                    #hawait selectBrowserAndSignIn({$tag: 'dbfba9fb-790e-4598-a04e-9b2b6041c1bc', clientKind: 'writer', browserName: 'todd2', email: 'todd@test.shit.ua', pausePointTitle: 'Todd comes back to check stuff...'})
                    art.uiState({$tag: 'b8690567-c212-4877-aa44-8e45313187da', expected: {
                         'TopNavItem-admin-heap': { title: `Куча` },
                          'TopNavItem-dashboard': { active: true, title: `Тодд` },
                          'TopNavItem-support': { title: `Поддержка` },
                          'link-changePassword': { title: `Сменить пароль` },
                          'link-signOut': { title: `Выйти прочь` },
                          'liveBadge-topNavItem-admin-heap': `11`,
                          'liveBadge-topNavItem-support': `2/2`,
                          pageHeader: `Панель`,
                          url: `http://aps-ua-writer.local:3022/dashboard.html`
                    }})
                    
                    testGlobal.controls['TopNavItem-support'].showHand({"testActionHandOpts":{"pointingFrom":"right","dleft":-12,"dtop":1}})
                    #hawait art.pausePoint({title: 'Two messages in two threads.\nOne was just added by Kafka. Second is old one we took but didnt’t bother to answer.', $tag: '27d35540-e345-4bd7-8c32-61e57b82c252'})
                    testGlobal.controls['TopNavItem-support'].hideHand()

                    // Action
                    #hawait testGlobal.controls['TopNavItem-support'].click()
                    art.uiState({$tag: 'c2c7686c-7658-475a-9068-7ad00e47e5d3', expected: {
                         'TopNavItem-admin-heap': { title: `Куча` },
                          'TopNavItem-dashboard': { title: `Тодд` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'chunk-0.thread-0.link-topic': { title: `В пустом зале заседаний. Студент. Канцелярии` },
                          'chunk-0.thread-0.newMessages.message-0.message': `Ни в чем. Просто пописать. Я ж писатель, ага.`,
                          'chunk-0.thread-0.newMessages.message-0.newLabel': `Новое`,
                          'chunk-0.thread-0.newMessages.message-0.timestamp': `23/07/2016 17:08:11`,
                          'chunk-0.thread-0.newMessages.message-0.userLabel-from': `Франц Кафка`,
                          'chunk-0.thread-0.newMessages.message-0.userLabel-to': `Тодд Суппортод`,
                          'chunk-0.thread-0.oldMessages.link-andMore': { title: `...и еще 3 старых сообщения` },
                          'chunk-0.thread-0.oldMessages.message-0.message': `Та я понял, что ты писатель... В чем дело-то?`,
                          'chunk-0.thread-0.oldMessages.message-0.timestamp': `23/07/2016 16:56:26`,
                          'chunk-0.thread-0.oldMessages.message-0.userLabel-from': `Тодд Суппортод`,
                          'chunk-0.thread-0.oldMessages.message-0.userLabel-to': `Франц Кафка`,
                          'chunk-0.thread-1.link-topic': { title: `И это называется следственной документацией!` },
                          'chunk-0.thread-1.newMessages.message-0.message': `Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.`,
                          'chunk-0.thread-1.newMessages.message-0.newLabel': `Новое`,
                          'chunk-0.thread-1.newMessages.message-0.timestamp': `10/04/2014 16:44:55`,
                          'chunk-0.thread-1.newMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-1.newMessages.message-0.userLabel-from': `Люк Хуюк`,
                          'liveBadge-topNavItem-admin-heap': `11`,
                          'liveBadge-topNavItem-support': `2/2`,
                          pageHeader: `Поддержка`,
                          'tabs-main.updated:active.title': `Новые`,
                          url: `http://aps-ua-writer.local:3022/support.html`
                    }})
                    
                    // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                    #hawait art.pausePoint({title: 'First let’s calm down Luke...', $tag: 'e51fe87e-f66b-4411-8028-a166d6c7e3d6'})
                    // Action
                    #hawait testGlobal.controls['chunk-0.thread-1.link-topic'].click()
                    art.uiState({$tag: '7010e87f-efb6-46cd-ab36-0c8f8537f9ea', expected: {
                         'Select-ordering': { title: `Сначала новые` },
                          'TopNavItem-admin-heap': { title: `Куча` },
                          'TopNavItem-dashboard': { title: `Тодд` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'button-edit': { icon: `edit` },
                          'button-plus': { icon: `comment` },
                          'chunk-0.message-0.message': `Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.`,
                          'chunk-0.message-0.newLabel': `Новое`,
                          'chunk-0.message-0.timestamp': `10/04/2014 16:44:55`,
                          'chunk-0.message-0.to': `В рельсу`,
                          'chunk-0.message-0.userLabel-from': `Люк Хуюк`,
                          'liveBadge-topNavItem-admin-heap': `11`,
                          'liveBadge-topNavItem-support': `2/2`,
                          pageHeader: `Запрос в поддержку № 12`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=12`
                    }})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '9c5e6974-bd3f-4278-ad9f-776cfccd040d'})

                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '599e8c7c-30a9-45b4-a22c-c60ae7a4a21b'})
                    // Action
                    #hawait testGlobal.controls['button-plus'].click()
                    art.uiState({$tag: '116f0222-746e-4c95-a81d-d3b88325c2a3', expected: {
                         'Input-message': ``,
                          'TopNavItem-admin-heap': { title: `Куча` },
                          'TopNavItem-dashboard': { title: `Тодд` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'button-cancel': { title: `Передумал` },
                          'button-primary': { title: `Запостить` },
                          'chunk-0.message-0.message': `Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.`,
                          'chunk-0.message-0.newLabel': `Новое`,
                          'chunk-0.message-0.timestamp': `10/04/2014 16:44:55`,
                          'chunk-0.message-0.to': `В рельсу`,
                          'chunk-0.message-0.userLabel-from': `Люк Хуюк`,
                          'liveBadge-topNavItem-admin-heap': `11`,
                          'liveBadge-topNavItem-support': `2/2`,
                          pageHeader: `Запрос в поддержку № 12`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=12`
                    }})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '36751d38-a5bf-4d68-93ba-8ac1e794e686'})


                    // Inputs
                    #hawait testGlobal.controls['Input-message'].setValue({value: 'Take it easy, Luke. We hear your pain and are working hard to find a solution to your problem.\n\n(In other words, fuck you :))'})
                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '8ba7f289-a636-43d6-818e-59ebc6ec4f5c'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/27 15:52:46'})
                    #hawait testGlobal.controls['button-primary'].click()
                    art.uiState({$tag: '81d0cc52-74e1-4eb8-921b-e72969b577eb', expected: {
                         'Select-ordering': { title: `Сначала новые` },
                          'TopNavItem-admin-heap': { title: `Куча` },
                          'TopNavItem-dashboard': { title: `Тодд` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'button-edit': { icon: `edit` },
                          'button-plus': { icon: `comment` },
                          'chunk-0.message-0.message': dedent(`
                                Take it easy, Luke. We hear your pain and are working hard to find a solution to your problem.
                                
                                (In other words, fuck you :))`),
                          'chunk-0.message-0.timestamp': `27/07/2016 18:52:46`,
                          'chunk-0.message-0.userLabel-from': `Тодд Суппортод`,
                          'chunk-0.message-0.userLabel-to': `Люк Хуюк`,
                          'chunk-0.message-1.message': `Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.`,
                          'chunk-0.message-1.newLabel:aniFadeOutDelayed': `Новое`,
                          'chunk-0.message-1.timestamp': `10/04/2014 16:44:55`,
                          'chunk-0.message-1.to': `В рельсу`,
                          'chunk-0.message-1.userLabel-from': `Люк Хуюк`,
                          'liveBadge-topNavItem-admin-heap': `11`,
                          'liveBadge-topNavItem-support': `1/1`,
                          pageHeader: `Запрос в поддержку № 12`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=12`
                    }})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '58d87ddc-5a4d-43b2-a52d-864ab5679b5a'})

                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '4e9bd6bf-3026-4212-bebb-017edeab476d'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/27 15:55:43'})
                    #hawait testGlobal.controls['TopNavItem-support'].click()
                    art.uiState({$tag: '907d4ca6-763a-455f-90fa-e42e28b9b906', expected: {
                         'TopNavItem-admin-heap': { title: `Куча` },
                          'TopNavItem-dashboard': { title: `Тодд` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'chunk-0.thread-0.link-topic': { title: `В пустом зале заседаний. Студент. Канцелярии` },
                          'chunk-0.thread-0.newMessages.message-0.message': `Ни в чем. Просто пописать. Я ж писатель, ага.`,
                          'chunk-0.thread-0.newMessages.message-0.newLabel': `Новое`,
                          'chunk-0.thread-0.newMessages.message-0.timestamp': `23/07/2016 17:08:11`,
                          'chunk-0.thread-0.newMessages.message-0.userLabel-from': `Франц Кафка`,
                          'chunk-0.thread-0.newMessages.message-0.userLabel-to': `Тодд Суппортод`,
                          'chunk-0.thread-0.oldMessages.link-andMore': { title: `...и еще 3 старых сообщения` },
                          'chunk-0.thread-0.oldMessages.message-0.message': `Та я понял, что ты писатель... В чем дело-то?`,
                          'chunk-0.thread-0.oldMessages.message-0.timestamp': `23/07/2016 16:56:26`,
                          'chunk-0.thread-0.oldMessages.message-0.userLabel-from': `Тодд Суппортод`,
                          'chunk-0.thread-0.oldMessages.message-0.userLabel-to': `Франц Кафка`,
                          'liveBadge-topNavItem-admin-heap': `11`,
                          'liveBadge-topNavItem-support': `1/1`,
                          pageHeader: `Поддержка`,
                          'tabs-main.updated:active.title': `Новые`,
                          url: `http://aps-ua-writer.local:3022/support.html`
                    }})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '4bfc7ec4-5464-4f38-9fc8-74a67dd6562d'})

                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: 'ba4cacfd-bab8-4851-995c-a28e4d5113a5'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/27 15:57:57'})
                    #hawait testGlobal.controls['chunk-0.thread-0.link-topic'].click()
                    art.uiState({$tag: '684e0bd4-57fc-4f61-a545-0e04c14e67de', expected: {
                         'Select-ordering': { title: `Сначала новые` },
                          'TopNavItem-admin-heap': { title: `Куча` },
                          'TopNavItem-dashboard': { title: `Тодд` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'button-edit': { icon: `edit` },
                          'button-plus': { icon: `comment` },
                          'chunk-0.message-0.message': `Ни в чем. Просто пописать. Я ж писатель, ага.`,
                          'chunk-0.message-0.newLabel': `Новое`,
                          'chunk-0.message-0.timestamp': `23/07/2016 17:08:11`,
                          'chunk-0.message-0.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-0.userLabel-to': `Тодд Суппортод`,
                          'chunk-0.message-1.message': `Та я понял, что ты писатель... В чем дело-то?`,
                          'chunk-0.message-1.timestamp': `23/07/2016 16:56:26`,
                          'chunk-0.message-1.userLabel-from': `Тодд Суппортод`,
                          'chunk-0.message-1.userLabel-to': `Франц Кафка`,
                          'chunk-0.message-2.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                          'chunk-0.message-2.timestamp': `17/05/2014 02:36:02`,
                          'chunk-0.message-2.to': `В рельсу`,
                          'chunk-0.message-2.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-3.message': dedent(`
                                Еще хотел бы добавить вот что.
                                
                                - Сегодня заседания нет, - сказала женщина.
                                - Как это - нет заседания? - спросил он, не поверив.`),
                          'chunk-0.message-3.timestamp': `17/05/2014 02:35:27`,
                          'chunk-0.message-3.to': `В рельсу`,
                          'chunk-0.message-3.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-4.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                          'chunk-0.message-4.timestamp': `17/05/2014 02:33:15`,
                          'chunk-0.message-4.to': `В рельсу`,
                          'chunk-0.message-4.userLabel-from': `Франц Кафка`,
                          'liveBadge-topNavItem-admin-heap': `11`,
                          'liveBadge-topNavItem-support': `1/1`,
                          pageHeader: `Запрос в поддержку № 308`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=308`
                    }})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '3e980295-4582-4efe-86d0-8ff79c64e18c'})

                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '34c7086c-bfdf-41a7-902f-477e75e9ad44'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/27 15:59:47'})
                    #hawait testGlobal.controls['button-plus'].click()
                    art.uiState({$tag: 'd51ea6a7-dac7-49f4-88d4-731a77d17b61', expected: {
                         'Input-message': ``,
                          'TopNavItem-admin-heap': { title: `Куча` },
                          'TopNavItem-dashboard': { title: `Тодд` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'button-cancel': { title: `Передумал` },
                          'button-primary': { title: `Запостить` },
                          'chunk-0.message-0.message': `Ни в чем. Просто пописать. Я ж писатель, ага.`,
                          'chunk-0.message-0.newLabel': `Новое`,
                          'chunk-0.message-0.timestamp': `23/07/2016 17:08:11`,
                          'chunk-0.message-0.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-0.userLabel-to': `Тодд Суппортод`,
                          'chunk-0.message-1.message': `Та я понял, что ты писатель... В чем дело-то?`,
                          'chunk-0.message-1.timestamp': `23/07/2016 16:56:26`,
                          'chunk-0.message-1.userLabel-from': `Тодд Суппортод`,
                          'chunk-0.message-1.userLabel-to': `Франц Кафка`,
                          'chunk-0.message-2.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                          'chunk-0.message-2.timestamp': `17/05/2014 02:36:02`,
                          'chunk-0.message-2.to': `В рельсу`,
                          'chunk-0.message-2.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-3.message': dedent(`
                                Еще хотел бы добавить вот что.
                                
                                - Сегодня заседания нет, - сказала женщина.
                                - Как это - нет заседания? - спросил он, не поверив.`),
                          'chunk-0.message-3.timestamp': `17/05/2014 02:35:27`,
                          'chunk-0.message-3.to': `В рельсу`,
                          'chunk-0.message-3.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-4.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                          'chunk-0.message-4.timestamp': `17/05/2014 02:33:15`,
                          'chunk-0.message-4.to': `В рельсу`,
                          'chunk-0.message-4.userLabel-from': `Франц Кафка`,
                          'liveBadge-topNavItem-admin-heap': `11`,
                          'liveBadge-topNavItem-support': `1/1`,
                          pageHeader: `Запрос в поддержку № 308`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=308`
                    }})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: 'fc3ce032-7279-46b8-b123-0253c5f5ebc6'})

                    // Inputs
                    #hawait testGlobal.controls['Input-message'].setValue({value: 'Ладно, тогда пиши себе. Я отвечать не буду. Но если, сцуко, слишком сильно наспамишь, то будем тебя банить, учти.'})
                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '115ce0cb-63e8-45d9-a128-986780c4c181'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/27 16:02:27'})
                    #hawait testGlobal.controls['button-primary'].click()
                    art.uiState({$tag: '08e9ecdd-0424-45f0-b39a-644c4621c4af', expected: {
                         'Select-ordering': { title: `Сначала новые` },
                          'TopNavItem-admin-heap': { title: `Куча` },
                          'TopNavItem-dashboard': { title: `Тодд` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'button-edit': { icon: `edit` },
                          'button-plus': { icon: `comment` },
                          'chunk-0.message-0.message': `Ладно, тогда пиши себе. Я отвечать не буду. Но если, сцуко, слишком сильно наспамишь, то будем тебя банить, учти.`,
                          'chunk-0.message-0.timestamp': `27/07/2016 19:02:27`,
                          'chunk-0.message-0.userLabel-from': `Тодд Суппортод`,
                          'chunk-0.message-0.userLabel-to': `Франц Кафка`,
                          'chunk-0.message-1.message': `Ни в чем. Просто пописать. Я ж писатель, ага.`,
                          'chunk-0.message-1.newLabel:aniFadeOutDelayed': `Новое`,
                          'chunk-0.message-1.timestamp': `23/07/2016 17:08:11`,
                          'chunk-0.message-1.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-1.userLabel-to': `Тодд Суппортод`,
                          'chunk-0.message-2.message': `Та я понял, что ты писатель... В чем дело-то?`,
                          'chunk-0.message-2.timestamp': `23/07/2016 16:56:26`,
                          'chunk-0.message-2.userLabel-from': `Тодд Суппортод`,
                          'chunk-0.message-2.userLabel-to': `Франц Кафка`,
                          'chunk-0.message-3.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                          'chunk-0.message-3.timestamp': `17/05/2014 02:36:02`,
                          'chunk-0.message-3.to': `В рельсу`,
                          'chunk-0.message-3.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-4.message': dedent(`
                                Еще хотел бы добавить вот что.
                                
                                - Сегодня заседания нет, - сказала женщина.
                                - Как это - нет заседания? - спросил он, не поверив.`),
                          'chunk-0.message-4.timestamp': `17/05/2014 02:35:27`,
                          'chunk-0.message-4.to': `В рельсу`,
                          'chunk-0.message-4.userLabel-from': `Франц Кафка`,
                          'chunk-0.message-5.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                          'chunk-0.message-5.timestamp': `17/05/2014 02:33:15`,
                          'chunk-0.message-5.to': `В рельсу`,
                          'chunk-0.message-5.userLabel-from': `Франц Кафка`,
                          'liveBadge-topNavItem-admin-heap': `11`,
                          pageHeader: `Запрос в поддержку № 308`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=308`
                    }})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: 'bfacb25b-1acd-40c0-856b-c0162647568c'})

                }
                
                async function luke1() {
                    #hawait selectBrowserAndSignIn({$tag: '10d399b1-6ac3-43dd-b21c-22f40fd11c48', clientKind: 'customer', browserName: 'luke1', email: 'luke@test.shit.ua', pausePointTitle: 'Now it’s Luke’s turn...'})
                    art.uiState({$tag: '0f95a4f1-b8c5-4e33-b1dd-81204d94f2d8', expected: {
                         'TopNavItem-blog': { title: `Блог` },
                          'TopNavItem-contact': { title: `Связь` },
                          'TopNavItem-dashboard': { active: true, title: `Люк` },
                          'TopNavItem-faq': { title: `ЧаВо` },
                          'TopNavItem-orders': { title: `Мои заказы` },
                          'TopNavItem-prices': { title: `Цены` },
                          'TopNavItem-samples': { title: `Примеры` },
                          'TopNavItem-support': { title: `Поддержка` },
                          'TopNavItem-why': { title: `Почему мы?` },
                          'link-changePassword': { title: `Сменить пароль` },
                          'link-signOut': { title: `Выйти прочь` },
                          'liveBadge-topNavItem-support': `1`,
                          pageHeader: `Панель`,
                          url: `http://aps-ua-writer.local:3022/dashboard.html`
                    }})
                    
                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '8089a90c-16e6-40ad-a668-ec4527614efd'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/27 19:16:05'})
                    #hawait testGlobal.controls['TopNavItem-support'].click()
                    art.uiState({$tag: '10b64318-d180-4a84-9b2f-0a37e61b3d4b', expected: {
                         'TopNavItem-blog': { title: `Блог` },
                          'TopNavItem-contact': { title: `Связь` },
                          'TopNavItem-dashboard': { title: `Люк` },
                          'TopNavItem-faq': { title: `ЧаВо` },
                          'TopNavItem-orders': { title: `Мои заказы` },
                          'TopNavItem-prices': { title: `Цены` },
                          'TopNavItem-samples': { title: `Примеры` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'TopNavItem-why': { title: `Почему мы?` },
                          'chunk-0.thread-0.link-topic': { title: `И это называется следственной документацией!` },
                          'chunk-0.thread-0.newMessages.message-0.message': dedent(`
                                Take it easy, Luke. We hear your pain and are working hard to find a solution to your problem.
                                
                                (In other words, fuck you :))`),
                          'chunk-0.thread-0.newMessages.message-0.newLabel': `Новое`,
                          'chunk-0.thread-0.newMessages.message-0.timestamp': `27/07/2016 18:52:46`,
                          'chunk-0.thread-0.newMessages.message-0.userLabel-from': `Тодд Суппортод`,
                          'chunk-0.thread-0.newMessages.message-0.userLabel-to': `Люк Хуюк`,
                          'chunk-0.thread-0.oldMessages.message-0.message': `Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.`,
                          'chunk-0.thread-0.oldMessages.message-0.timestamp': `10/04/2014 16:44:55`,
                          'chunk-0.thread-0.oldMessages.message-0.to': `В рельсу`,
                          'chunk-0.thread-0.oldMessages.message-0.userLabel-from': `Люк Хуюк`,
                          'liveBadge-topNavItem-support': `1`,
                          pageHeader: `Поддержка`,
                          'tabs-main.updated:active.title': `Новые`,
                          url: `http://aps-ua-writer.local:3022/support.html`
                    }})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '7b4d2b2e-202b-4188-afa8-a8136d5f2bd6'})

                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '5aa09ff4-7e41-4fab-bc4f-bf3a1f3cd7e2'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/27 19:45:25'})
                    #hawait testGlobal.controls['chunk-0.thread-0.link-topic'].click()
                    art.uiState({$tag: 'c0950b79-938e-4bc0-b133-42d8a27d005f', expected: {
                         'Select-ordering': 
                           { title: `Сначала новые`,
                             titles: [ `Сначала новые`, `Сначала старые` ] },
                          'TopNavItem-blog': { title: `Блог` },
                          'TopNavItem-contact': { title: `Связь` },
                          'TopNavItem-dashboard': { title: `Люк` },
                          'TopNavItem-faq': { title: `ЧаВо` },
                          'TopNavItem-orders': { title: `Мои заказы` },
                          'TopNavItem-prices': { title: `Цены` },
                          'TopNavItem-samples': { title: `Примеры` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'TopNavItem-why': { title: `Почему мы?` },
                          'button-edit': { icon: `edit` },
                          'button-plus': { icon: `comment` },
                          'chunk-0.message-0.message': dedent(`
                                Take it easy, Luke. We hear your pain and are working hard to find a solution to your problem.
                                
                                (In other words, fuck you :))`),
                          'chunk-0.message-0.newLabel': `Новое`,
                          'chunk-0.message-0.timestamp': `27/07/2016 18:52:46`,
                          'chunk-0.message-0.userLabel-from': `Тодд Суппортод`,
                          'chunk-0.message-0.userLabel-to': `Люк Хуюк`,
                          'chunk-0.message-1.message': `Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.`,
                          'chunk-0.message-1.timestamp': `10/04/2014 16:44:55`,
                          'chunk-0.message-1.to': `В рельсу`,
                          'chunk-0.message-1.userLabel-from': `Люк Хуюк`,
                          'liveBadge-topNavItem-support': `1`,
                          pageHeader: `Запрос в поддержку № 12`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=12`
                    }})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: 'd6984756-b1ae-447d-bff2-08be7bf99d60'})

                    // Action
                    // #hawait art.pausePoint({title: 'TODO: Describe action', $tag: '5e57ddc5-f3ce-48e4-aff8-d319b806c95d'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/27 21:41:50'})
                    #hawait testGlobal.controls['button-edit'].click()
                    art.uiState({$tag: '4c776fc8-f555-47e2-a7e7-3b4a5216e8a6', expected: {
                         'Select-status': { title: `Решён`, titles: [ `Решён` ] },
                          'TopNavItem-blog': { title: `Блог` },
                          'TopNavItem-contact': { title: `Связь` },
                          'TopNavItem-dashboard': { title: `Люк` },
                          'TopNavItem-faq': { title: `ЧаВо` },
                          'TopNavItem-orders': { title: `Мои заказы` },
                          'TopNavItem-prices': { title: `Цены` },
                          'TopNavItem-samples': { title: `Примеры` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'TopNavItem-why': { title: `Почему мы?` },
                          'button-cancel': { title: `Не стоит` },
                          'button-primary': { title: `Сохранить` },
                          'chunk-0.message-0.message': dedent(`
                                Take it easy, Luke. We hear your pain and are working hard to find a solution to your problem.
                                
                                (In other words, fuck you :))`),
                          'chunk-0.message-0.newLabel': `Новое`,
                          'chunk-0.message-0.timestamp': `27/07/2016 18:52:46`,
                          'chunk-0.message-0.userLabel-from': `Тодд Суппортод`,
                          'chunk-0.message-0.userLabel-to': `Люк Хуюк`,
                          'chunk-0.message-1.message': `Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.`,
                          'chunk-0.message-1.timestamp': `10/04/2014 16:44:55`,
                          'chunk-0.message-1.to': `В рельсу`,
                          'chunk-0.message-1.userLabel-from': `Люк Хуюк`,
                          'liveBadge-topNavItem-support': `1`,
                          pageHeader: `Запрос в поддержку № 12`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=12`
                    }})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '49577eb3-936c-43e3-a4c4-7ed6a54a7ff9'})

                    // Inputs
                    #hawait testGlobal.controls['Select-status'].setValue({value: 'resolved'})
                    // Action
                    #hawait art.pausePoint({title: 'Luke discovers he’s fucked and submissively decides to mark the request as resolved', $tag: '4fabecfa-27a8-4b45-a745-ceac79c74464'})
                    #hawait drpc({fun: 'danger_imposeNextRequestTimestamp', timestamp: '2016/07/27 21:42:54'})
                    #hawait testGlobal.controls['button-primary'].click()
                    art.uiState({$tag: '99798601-7dca-4e9c-8136-5052756c685e', expected: {
                         'TopNavItem-blog': { title: `Блог` },
                          'TopNavItem-contact': { title: `Связь` },
                          'TopNavItem-dashboard': { title: `Люк` },
                          'TopNavItem-faq': { title: `ЧаВо` },
                          'TopNavItem-orders': { title: `Мои заказы` },
                          'TopNavItem-prices': { title: `Цены` },
                          'TopNavItem-samples': { title: `Примеры` },
                          'TopNavItem-support': { active: true, title: `Поддержка` },
                          'TopNavItem-why': { title: `Почему мы?` },
                          'chunk-0.message-0.message': dedent(`
                                Take it easy, Luke. We hear your pain and are working hard to find a solution to your problem.
                                
                                (In other words, fuck you :))`),
                          'chunk-0.message-0.newLabel:aniFadeOutDelayed': `Новое`,
                          'chunk-0.message-0.timestamp': `27/07/2016 18:52:46`,
                          'chunk-0.message-0.userLabel-from': `Тодд Суппортод`,
                          'chunk-0.message-0.userLabel-to': `Люк Хуюк`,
                          'chunk-0.message-1.message': `Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.`,
                          'chunk-0.message-1.timestamp': `10/04/2014 16:44:55`,
                          'chunk-0.message-1.to': `В рельсу`,
                          'chunk-0.message-1.userLabel-from': `Люк Хуюк`,
                          pageHeader: `Запрос в поддержку № 12`,
                          'pageHeaderLabel-0:success': `Решён`,
                          url: `http://aps-ua-writer.local:3022/support.html?thread=12`
                    }})
                    // #hawait art.pausePoint({title: 'TODO: Describe situation', $tag: '010ed3fa-bbc9-4e45-922e-c76de43bcbe2'})

                    art.actionPlaceholder({$tag: 'ce417c62-f167-4a0d-aecc-8193057b138c'})
                }
            },
        }
    }
    
    
    async function selectBrowserAndSignIn({$tag, pausePointTitle, browserName, email, clientKind}) {
        CLIENT_KIND = clientKind
        sim.selectBrowser(browserName)
        #hawait sim.navigate('dashboard.html')
        
        if (pausePointTitle) {
            #hawait art.pausePoint({title: pausePointTitle, theme: 'blue', $tag})
        }
        
        if (clientKind === 'writer') {
            art.uiState({$tag: 'c70e18eb-516b-41cb-bf7a-bdf748595ad2', expected: {
                 'Input-email': ``,
                  'Input-password': ``,
                  'TopNavItem-faq': { title: `ЧаВо` },
                  'TopNavItem-prices': { title: `Цены` },
                  'TopNavItem-sign-in': { active: true, title: `Вход` },
                  'TopNavItem-why': { title: `Почему мы?` },
                  'button-primary': { title: `Войти` },
                  'link-createAccount': { title: `Срочно создать!` },
                  pageHeader: `Вход`,
                  url: `http://aps-ua-writer.local:3022/sign-in.html`
            }})                
        } else if (clientKind === 'customer') {
            art.uiState({$tag: '0bd97815-b9b1-4900-80b1-e6f683c41926', expected: {
                 'Input-email': ``,
                  'Input-password': ``,
                  'TopNavItem-blog': { title: `Блог` },
                  'TopNavItem-contact': { title: `Связь` },
                  'TopNavItem-faq': { title: `ЧаВо` },
                  'TopNavItem-prices': { title: `Цены` },
                  'TopNavItem-samples': { title: `Примеры` },
                  'TopNavItem-sign-in': { active: true, title: `Вход` },
                  'TopNavItem-why': { title: `Почему мы?` },
                  'button-primary': { title: `Войти` },
                  'link-createAccount': { title: `Срочно создать!` },
                  pageHeader: `Вход`,
                  url: `http://aps-ua-writer.local:3022/sign-in.html`
            }})                
        } else {
            raise('WTF is the clientKind')
        }
        
        // Inputs
        #hawait testGlobal.controls['Input-email'].setValue({value: email})
        #hawait testGlobal.controls['Input-password'].setValue({value: 'secret'})
        // Action
        #hawait testGlobal.controls['button-primary'].click()
    }
    
}


// /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true

