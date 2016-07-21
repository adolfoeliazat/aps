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
                
                sim.selectBrowser('todd')
                
                #hawait sim.navigate('dashboard.html')
                
                art.uiState({$tag: 'c70e18eb-516b-41cb-bf7a-bdf748595ad2', expected: {
                    url: `http://aps-ua-writer.local:3022/sign-in.html`,
                    pageHeader: `Вход`,
                    inputs: { email: { value: `` }, password: { value: `` } },
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {} 
                }})                
                
                // Inputs
                #hawait testGlobal.inputs.email.setValue('todd@test.shit.ua')
                #hawait testGlobal.inputs.password.setValue('secret')
                // Action
                #hawait testGlobal.buttons.primary.click()
                #hawait art.shitSpinsForMax({$tag: '271f3603-1982-4804-b064-b718ee444160', max: 2000})
                #hawait art.uiStateAfterLiveStatusPolling({$tag: '866bef17-2783-40a5-860d-0d2f69966664', expected: {
                    url: `http://aps-ua-writer.local:3022/dashboard.html`,
                    pageHeader: `Панель`,
                    inputs: {},
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {},
                    pageData: 
                     { 'topNavItem.admin-my-tasks.title': `Мои задачи`,
                       'topNavItem.admin-heap.title': `Куча`,
                       'topNavItem.admin-heap.badge': `13` } 
                }})
                
                testGlobal.topNavbarLinks['admin-heap'].showHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15}})
                #hawait art.pausePoint({title: 'There is some unassigned work, let’s take a look...', $tag: '20f801f5-657b-4176-bd1a-fb78f5af1811'})
                testGlobal.topNavbarLinks['admin-heap'].hideHand()
                
                // Action
                #hawait testGlobal.topNavbarLinks['admin-heap'].click()
                #hawait art.uiStateAfterLiveStatusPolling({$tag: '1c3a4a15-4bc4-46f6-afd9-d23433c6d839', expected: {
                    url: `http://aps-ua-writer.local:3022/admin-heap.html`,
                    pageHeader: `Куча работы`,
                    inputs: {},
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {},
                    pageData: 
                     { 'supportThreads-0': 
                        [ { '$$type': `supportThread`,
                            topic: `И это называется следственной документацией!`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Люк Хуюк`,
                                 to: `В рельсу`,
                                 timestamp: `10/04/2014 16:44:55`,
                                 message: `Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.` } ] },
                          { '$$type': `supportThread`,
                            topic: dedent(`
                          В   углу   комнаты   стояли  трое  молодых  людей  -  они разглядывали фотографии фройляйн Бюрстнер,
                          воткнутые в плетеную циновку на стене. На ручке открытого окна висела белая  блузка.`),
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Даздраперма Дивизионная`,
                                 to: `В рельсу`,
                                 timestamp: `06/05/2014 17:33:34`,
                                 message: dedent(`
                          В  окно  напротив уже высунулись те же старики, но зрителей там
                          прибавилось:  за  их  спинами  возвышался  огромный  мужчина  в
                          раскрытой  на  груди  рубахе, который все время крутил и вертел
                          свою рыжеватую бородку.
                               - Йозеф К.? - спросил инспектор, должно быть,  только  для
                          того, чтобы обратить на себя рассеянный взгляд К.
                               К. наклонил голову.
                               - Должно  быть,  вас  очень  удивили  события сегодняшнего
                          утра?`) } ] },
                          { '$$type': `supportThread`,
                            topic: `В пустом зале заседаний. Студент. Канцелярии`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Франц Кафка`,
                                 to: `В рельсу`,
                                 timestamp: `17/05/2014 02:33:15`,
                                 message: `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.` },
                               { '$$type': `supportThreadMessage`,
                                 continuation: `В догонку`,
                                 timestamp: `17/05/2014 02:35:27`,
                                 message: dedent(`
                          Еще хотел бы добавить вот что.
                          
                          - Сегодня заседания нет, - сказала женщина.
                          - Как это - нет заседания? - спросил он, не поверив.`) },
                               { '$$type': `supportThreadMessage`,
                                 continuation: `В догонку`,
                                 timestamp: `17/05/2014 02:36:02`,
                                 message: `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.` } ] },
                          { '$$type': `supportThread`,
                            topic: `Видно,  собираетесь  навести  здесь  порядок?`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Джейн Остин`,
                                 to: `В рельсу`,
                                 timestamp: `21/05/2014 10:11:59`,
                                 message: `Я так и догадалась по вашей речи. Мне лично она  очень  понравилась.  Правда,  я  не  все  слышала - начало пропустила, а под конец лежала со студентом на полу.` } ] },
                          { '$$type': `supportThread`,
                            topic: `Ну  конечно  же!`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Жорж Санд`,
                                 to: `В рельсу`,
                                 timestamp: `26/05/2014 10:51:24`,
                                 message: `Книги были старые, потрепанные, на одной  переплет был переломлен и обе половинки держались на ниточке.` } ] },
                          { '$$type': `supportThread`,
                            topic: `Какая  тут  везде грязь, - сказал К., покачав головой`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Василий Теркин`,
                                 to: `В рельсу`,
                                 timestamp: `13/06/2014 14:19:32`,
                                 message: `И женщине пришлось смахнуть пыль фартуком хотя бы сверху,  прежде чем К. мог взяться за книгу.` },
                               { '$$type': `supportThreadMessage`,
                                 continuation: `В догонку`,
                                 timestamp: `13/06/2014 14:25:13`,
                                 message: `Он  открыл  ту,  что  лежала  сверху, и увидел неприличную картинку.` } ] },
                          { '$$type': `supportThread`,
                            topic: `Так вот какие юридические книги тут изучают!`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Карлос Кастанеда`,
                                 to: `В рельсу`,
                                 timestamp: `16/07/2014 08:24:17`,
                                 message: dedent(`
                          - Я вам помогу! - сказала женщина. - Согласны?
                          - Но разве вы и вправду можете мне  помочь,  не  подвергая себя  опасности?  Ведь  вы  сами  сказали,  что ваш муж целиком зависит от своего начальства.
                          - И все же я вам помогу, - сказала  женщина.  Подите  сюда, надо  все обсудить. А о том, что мне грозит опасность, говорить не стоит.  Я  только  тогда  пугаюсь  опасности,  когда  считаю нужным. Идите сюда.
                          - Она показала на подмостки и попросила его сесть  рядом с ней на ступеньки.
                          
                          - У вас чудесные темные глаза, -сказала она, когда они сели, и заглянула К. в лицо. - Говорят, у меня тоже глаза красивые, но ваши куда красивее. Ведь  я  вас сразу  приметила,  еще  в первый раз, как только вы сюда зашли. Из-за вас я и пробралась потом в зал заседаний. Обычно я никогда этого не делаю, мне даже, собственно говоря,  запрещено  ходить сюда.`) } ] },
                          { '$$type': `supportThread`,
                            topic: `Если вам так не терпится, можете  уходить`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Тело Странное`,
                                 to: `В рельсу`,
                                 timestamp: `17/07/2014 10:33:08`,
                                 message: `Давно  могли уйти,  никто  и  не  заметил бы вашего отсутствия. Да, да, надо было вам уйти, как только я пришел, и уйти сразу, немедленно. В этих словах слышалась не только сдержанная злоба, в  них ясно  чувствовалось высокомерие будущего чиновника по отношению к неприятному для него обвиняемому.` } ] },
                          { '$$type': `supportThread`,
                            topic: `Ничего  не  поделаешь`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Регина Дубовицкая`,
                                 to: `В рельсу`,
                                 timestamp: `06/08/2014 04:33:37`,
                                 message: `Его за мной прислал следователь, мне с вами идти никак нельзя, этот маленький уродец, - тут  она провела  рукой  по лицу студента, этот маленький уродец меня не отпустит.` } ] },
                          { '$$type': `supportThread`,
                            topic: `Оба  исчезли за поворотом`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Варсоновий Оптинский`,
                                 to: `В рельсу`,
                                 timestamp: `15/08/2014 13:32:42`,
                                 message: `К. все еще стоял в дверях. Он должен был признаться, что женщина не только обманула его, но и солгала, что ее несут к следователю. Не станет  же  следователь сидеть  на  чердаке  и  дожидаться  ее.` } ] } ],
                       showMoreButtonPresent: true,
                       'topNavItem.admin-my-tasks.title': `Мои задачи`,
                       'topNavItem.admin-heap.title': `Куча`,
                       'heapTabs.supportBadge': `13`,
                       'topNavItem.admin-heap.badge': `13` } 
                }})
                #hawait art.pausePoint({title: 'A lot of stuff, let’s do some scrolling...', $tag: 'a86e6b75-0140-4347-a961-bf9886937806', locus: 'top-right'})
                #hawait art.scroll({origY: 0, destY: 'bottom'})
                #hawait art.pausePoint({title: 'Before clicking "Show More" button', $tag: 'dcf633b6-0cd6-43bc-81f8-5920ff60a795', locus: 'top-right'})
                
                // Action
                #hawait testGlobal.buttons.showMore.click({testActionHandOpts: {pointingFrom: 'top'}})
                #hawait art.shitBlinksForMax({$tag: 'fe9920f9-8479-452f-afde-5fbab869b0c8', kind: 'button', name: 'showMore', max: 2000})
                #hawait art.uiStateAfterLiveStatusPolling({$tag: 'edb9fdc5-841b-4232-baa2-dfdc39d8d02d', expected: {
                    url: `http://aps-ua-writer.local:3022/admin-heap.html`,
                    pageHeader: `Куча работы`,
                    inputs: {},
                    errorLabels: {},
                    errorBanner: undefined,
                    displayLabels: {},
                    pageData: 
                     { 'supportThreads-0': 
                        [ { '$$type': `supportThread`,
                            topic: `И это называется следственной документацией!`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Люк Хуюк`,
                                 to: `В рельсу`,
                                 timestamp: `10/04/2014 16:44:55`,
                                 message: `Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.` } ] },
                          { '$$type': `supportThread`,
                            topic: dedent(`
                          В   углу   комнаты   стояли  трое  молодых  людей  -  они разглядывали фотографии фройляйн Бюрстнер,
                          воткнутые в плетеную циновку на стене. На ручке открытого окна висела белая  блузка.`),
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Даздраперма Дивизионная`,
                                 to: `В рельсу`,
                                 timestamp: `06/05/2014 17:33:34`,
                                 message: dedent(`
                          В  окно  напротив уже высунулись те же старики, но зрителей там
                          прибавилось:  за  их  спинами  возвышался  огромный  мужчина  в
                          раскрытой  на  груди  рубахе, который все время крутил и вертел
                          свою рыжеватую бородку.
                               - Йозеф К.? - спросил инспектор, должно быть,  только  для
                          того, чтобы обратить на себя рассеянный взгляд К.
                               К. наклонил голову.
                               - Должно  быть,  вас  очень  удивили  события сегодняшнего
                          утра?`) } ] },
                          { '$$type': `supportThread`,
                            topic: `В пустом зале заседаний. Студент. Канцелярии`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Франц Кафка`,
                                 to: `В рельсу`,
                                 timestamp: `17/05/2014 02:33:15`,
                                 message: `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.` },
                               { '$$type': `supportThreadMessage`,
                                 continuation: `В догонку`,
                                 timestamp: `17/05/2014 02:35:27`,
                                 message: dedent(`
                          Еще хотел бы добавить вот что.
                          
                          - Сегодня заседания нет, - сказала женщина.
                          - Как это - нет заседания? - спросил он, не поверив.`) },
                               { '$$type': `supportThreadMessage`,
                                 continuation: `В догонку`,
                                 timestamp: `17/05/2014 02:36:02`,
                                 message: `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.` } ] },
                          { '$$type': `supportThread`,
                            topic: `Видно,  собираетесь  навести  здесь  порядок?`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Джейн Остин`,
                                 to: `В рельсу`,
                                 timestamp: `21/05/2014 10:11:59`,
                                 message: `Я так и догадалась по вашей речи. Мне лично она  очень  понравилась.  Правда,  я  не  все  слышала - начало пропустила, а под конец лежала со студентом на полу.` } ] },
                          { '$$type': `supportThread`,
                            topic: `Ну  конечно  же!`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Жорж Санд`,
                                 to: `В рельсу`,
                                 timestamp: `26/05/2014 10:51:24`,
                                 message: `Книги были старые, потрепанные, на одной  переплет был переломлен и обе половинки держались на ниточке.` } ] },
                          { '$$type': `supportThread`,
                            topic: `Какая  тут  везде грязь, - сказал К., покачав головой`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Василий Теркин`,
                                 to: `В рельсу`,
                                 timestamp: `13/06/2014 14:19:32`,
                                 message: `И женщине пришлось смахнуть пыль фартуком хотя бы сверху,  прежде чем К. мог взяться за книгу.` },
                               { '$$type': `supportThreadMessage`,
                                 continuation: `В догонку`,
                                 timestamp: `13/06/2014 14:25:13`,
                                 message: `Он  открыл  ту,  что  лежала  сверху, и увидел неприличную картинку.` } ] },
                          { '$$type': `supportThread`,
                            topic: `Так вот какие юридические книги тут изучают!`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Карлос Кастанеда`,
                                 to: `В рельсу`,
                                 timestamp: `16/07/2014 08:24:17`,
                                 message: dedent(`
                          - Я вам помогу! - сказала женщина. - Согласны?
                          - Но разве вы и вправду можете мне  помочь,  не  подвергая себя  опасности?  Ведь  вы  сами  сказали,  что ваш муж целиком зависит от своего начальства.
                          - И все же я вам помогу, - сказала  женщина.  Подите  сюда, надо  все обсудить. А о том, что мне грозит опасность, говорить не стоит.  Я  только  тогда  пугаюсь  опасности,  когда  считаю нужным. Идите сюда.
                          - Она показала на подмостки и попросила его сесть  рядом с ней на ступеньки.
                          
                          - У вас чудесные темные глаза, -сказала она, когда они сели, и заглянула К. в лицо. - Говорят, у меня тоже глаза красивые, но ваши куда красивее. Ведь  я  вас сразу  приметила,  еще  в первый раз, как только вы сюда зашли. Из-за вас я и пробралась потом в зал заседаний. Обычно я никогда этого не делаю, мне даже, собственно говоря,  запрещено  ходить сюда.`) } ] },
                          { '$$type': `supportThread`,
                            topic: `Если вам так не терпится, можете  уходить`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Тело Странное`,
                                 to: `В рельсу`,
                                 timestamp: `17/07/2014 10:33:08`,
                                 message: `Давно  могли уйти,  никто  и  не  заметил бы вашего отсутствия. Да, да, надо было вам уйти, как только я пришел, и уйти сразу, немедленно. В этих словах слышалась не только сдержанная злоба, в  них ясно  чувствовалось высокомерие будущего чиновника по отношению к неприятному для него обвиняемому.` } ] },
                          { '$$type': `supportThread`,
                            topic: `Ничего  не  поделаешь`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Регина Дубовицкая`,
                                 to: `В рельсу`,
                                 timestamp: `06/08/2014 04:33:37`,
                                 message: `Его за мной прислал следователь, мне с вами идти никак нельзя, этот маленький уродец, - тут  она провела  рукой  по лицу студента, этот маленький уродец меня не отпустит.` } ] },
                          { '$$type': `supportThread`,
                            topic: `Оба  исчезли за поворотом`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Варсоновий Оптинский`,
                                 to: `В рельсу`,
                                 timestamp: `15/08/2014 13:32:42`,
                                 message: `К. все еще стоял в дверях. Он должен был признаться, что женщина не только обманула его, но и солгала, что ее несут к следователю. Не станет  же  следователь сидеть  на  чердаке  и  дожидаться  ее.` } ] } ],
                       'topNavItem.admin-my-tasks.title': `Мои задачи`,
                       'topNavItem.admin-heap.title': `Куча`,
                       'supportThreads-1': 
                        [ { '$$type': `supportThread`,
                            topic: `Вот видите!`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Федор Достоевский`,
                                 to: `В рельсу`,
                                 timestamp: `11/10/2014 08:15:11`,
                                 message: `Вечно  ее  от  меня уносят.  Сегодня воскресенье, работать я не обязан, а мне вдруг дают совершенно ненужные  поручения,  лишь  бы  услать  отсюда.` } ] },
                          { '$$type': `supportThread`,
                            topic: `А разве другого выхода нет?`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Ксенофонт Тутанский`,
                                 to: `В рельсу`,
                                 timestamp: `23/10/2014 18:10:13`,
                                 message: dedent(`
                          - Другого не вижу, - сказал  служитель.  -  И  главное,  с
                          каждым  днем все хуже: до сих пор он таскал ее только к себе, а
                          сейчас  потащил  к  самому  следователю;   впрочем,   этого   я
                          давным-давно ждал.`) } ] },
                          { '$$type': `supportThread`,
                            topic: `Какой смысл?`,
                            messages: 
                             [ { '$$type': `supportThreadMessage`,
                                 from: `Евгений Ваганович`,
                                 to: `В рельсу`,
                                 timestamp: `25/11/2014 05:39:53`,
                                 message: dedent(`
                          Да  кто  вы  такой?  Ищете  смысл,  а  творите  такую
                          бессмыслицу, что и не придумаешь. Да тут камни возопят! Сначала
                          эти господа на меня напали, а теперь расселись, стоят и глазеют
                          всем скопом, как я пляшу под вашу  дудку.  И  еще  спрашиваете,
                          какой  смысл  звонить  прокурору,  когда  мне  сказано,  что  я
                          арестован! Хорошо, я не буду звонить!`) } ] } ],
                       'heapTabs.supportBadge': `13`,
                       'topNavItem.admin-heap.badge': `13` } 
                }})
                
                #hawait art.scroll({origY: 'current', destY: 'bottom'})
                #hawait art.pausePoint({title: 'Now admin chooses a thread to take', $tag: '3045e8f4-3b18-4681-91e4-bfaa2961f40d', locus: 'top-right'})
                #hawait art.scroll({origY: 'current', destY: 320})
                
                // Action
                #hawait testGlobal.buttons['takeAndReply-308'].click()
                #hawait art.shitBlinksForMax({$tag: 'f146dce3-e5dc-4846-bb65-868c8083b859', kind: 'button', name: 'takeAndReply-308', max: 2000})
                
                #hawait art.uiStateAfterLiveStatusPolling({$tag: '404c23b9-e496-41b5-a105-71ae5c44ed5f', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html?thread=308`,
                        pageHeader: `Запрос в поддержку № 308`,
                        inputs: {},
                        errorLabels: {},
                        errorBanner: undefined,
                        displayLabels: {},
                        pageData: 
                         { 'Select-ordering': `desc`,
                           'supportThreadMessages-0': 
                            [ { '$$type': `supportThreadMessage`,
                                from: `Франц Кафка`,
                                to: `В рельсу`,
                                timestamp: `17/05/2014 02:36:02`,
                                message: `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.` },
                              { '$$type': `supportThreadMessage`,
                                from: `Франц Кафка`,
                                to: `В рельсу`,
                                timestamp: `17/05/2014 02:35:27`,
                                message: dedent(`
                              Еще хотел бы добавить вот что.
                              
                              - Сегодня заседания нет, - сказала женщина.
                              - Как это - нет заседания? - спросил он, не поверив.`) },
                              { '$$type': `supportThreadMessage`,
                                from: `Франц Кафка`,
                                to: `В рельсу`,
                                timestamp: `17/05/2014 02:33:15`,
                                message: `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.` } ],
                           'topNavItem.admin-my-tasks.title': `Мои задачи`,
                           'topNavItem.admin-heap.title': `Куча`,
                           'topNavItem.admin-heap.badge': `12` } 
                }})
                
                testGlobal.topNavbarLinks['admin-heap'].showHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15}})
                #hawait art.pausePoint({title: 'Amount of work in heap decreased, since we’ve just taken one piece from it', $tag: '3ebb103e-7f0a-4669-9a29-2cbb2bbed460'})
                testGlobal.topNavbarLinks['admin-heap'].hideHand()
                
                #hawait art.pausePoint({title: 'Changing ordering to show old messages first', $tag: '1e77736a-3498-4b5d-8b1e-94c0f7dd6c56'})
                // Action
                #hawait testGlobal.controls['ordering'].setValue('asc', {testActionHandOpts: {pointingFrom: 'left', dtop: 38}})
                #hawait art.uiStateAfterLiveStatusPolling({$tag: '73952bab-8024-4351-8656-0966860aa31b', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html?thread=308&ordering=asc`,
                        pageHeader: `Запрос в поддержку № 308`,
                        inputs: {},
                        errorLabels: {},
                        errorBanner: undefined,
                        displayLabels: {},
                        pageData: 
                         { 'topNavItem.admin-my-tasks.title': `Мои задачи`,
                           'topNavItem.admin-heap.title': `Куча`,
                           'Select-ordering': `asc`,
                           'supportThreadMessages-0': 
                            [ { '$$type': `supportThreadMessage`,
                                from: `Франц Кафка`,
                                to: `В рельсу`,
                                timestamp: `17/05/2014 02:33:15`,
                                message: `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.` },
                              { '$$type': `supportThreadMessage`,
                                from: `Франц Кафка`,
                                to: `В рельсу`,
                                timestamp: `17/05/2014 02:35:27`,
                                message: dedent(`
                              Еще хотел бы добавить вот что.
                              
                              - Сегодня заседания нет, - сказала женщина.
                              - Как это - нет заседания? - спросил он, не поверив.`) },
                              { '$$type': `supportThreadMessage`,
                                from: `Франц Кафка`,
                                to: `В рельсу`,
                                timestamp: `17/05/2014 02:36:02`,
                                message: `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.` } ],
                           'topNavItem.admin-heap.badge': `12` } 
                }})
                
                #hawait art.pausePoint({title: 'Changing it back to conveniently show new stuff first', $tag: '79a2d7fc-5509-41fc-b606-b82bdccd5d68'})
                // Action
                #hawait testGlobal.controls['ordering'].setValue('desc', {testActionHandOpts: {pointingFrom: 'left', dtop: 18}})
                #hawait art.uiStateAfterLiveStatusPolling({$tag: '1920f57c-db76-40ff-9f85-8ebdd8faf2f3', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html?thread=308&ordering=desc`,
                        pageHeader: `Запрос в поддержку № 308`,
                        inputs: {},
                        errorLabels: {},
                        errorBanner: undefined,
                        displayLabels: {},
                        pageData: 
                         { 'topNavItem.admin-my-tasks.title': `Мои задачи`,
                           'topNavItem.admin-heap.title': `Куча`,
                           'Select-ordering': `desc`,
                           'supportThreadMessages-0': 
                            [ { '$$type': `supportThreadMessage`,
                                from: `Франц Кафка`,
                                to: `В рельсу`,
                                timestamp: `17/05/2014 02:36:02`,
                                message: `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.` },
                              { '$$type': `supportThreadMessage`,
                                from: `Франц Кафка`,
                                to: `В рельсу`,
                                timestamp: `17/05/2014 02:35:27`,
                                message: dedent(`
                              Еще хотел бы добавить вот что.
                              
                              - Сегодня заседания нет, - сказала женщина.
                              - Как это - нет заседания? - спросил он, не поверив.`) },
                              { '$$type': `supportThreadMessage`,
                                from: `Франц Кафка`,
                                to: `В рельсу`,
                                timestamp: `17/05/2014 02:33:15`,
                                message: `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.` } ],
                           'topNavItem.admin-heap.badge': `12` } 
                }})
                
                // Action
                #hawait testGlobal.buttons['plus'].click()
                art.uiStateChange({$tag: 'bd47917a-b212-4d03-8910-5af36b2f7ebc', expected: {
                    inputs: { message: { value: '' } },
                }})

                
                // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
            },
        }
    }
    
}
