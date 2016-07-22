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
                /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
                #hawait drpc({fun: 'danger_clearSentEmails'})
                
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
                    'TopNavItem-admin-my-tasks': { title: `Мои задачи` },
                    'TopNavItem-admin-heap': { title: `Куча` },
                    'TopNavItem-dashboard': { title: `Тодд` },
                    'liveBadge-topNavItem-admin-heap': `13` 
                }})
                
                testGlobal.controls['TopNavItem-admin-heap'].showHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15}})
                #hawait art.pausePoint({title: 'There is some unassigned work, let’s take a look...', $tag: '20f801f5-657b-4176-bd1a-fb78f5af1811'})
                testGlobal.controls['TopNavItem-admin-heap'].hideHand()
                
                // Action
                #hawait testGlobal.controls['TopNavItem-admin-heap'].click()
                #hawait art.uiState({$tag: '1c3a4a15-4bc4-46f6-afd9-d23433c6d839', expected: {
                    url: `http://aps-ua-writer.local:3022/admin-heap.html`,
                    'liveBadge-supportTab': `13`,
                    'button-showMore': { title: `Показать еще` },
                    'supportThread-12.topic': `И это называется следственной документацией!`,
                    'supportThread-12.button-takeAndReply': { icon: `comment` },
                    'supportThread-12.message-432.userLabel-from': `Люк Хуюк`,
                    'supportThread-12.message-432.to': `В рельсу`,
                    'supportThread-12.message-432.timestamp': `10/04/2014 16:44:55`,
                    'supportThread-12.message-432.message': `Нужно было бы еще многое вам сказать. Пришлось изложить только вкратце. Но я надеюсь, что вы меня поняли.`,
                    'supportThread-25.topic': dedent(`
                          В   углу   комнаты   стояли  трое  молодых  людей  -  они разглядывали фотографии фройляйн Бюрстнер,
                          воткнутые в плетеную циновку на стене. На ручке открытого окна висела белая  блузка.`),
                    'supportThread-25.button-takeAndReply': { icon: `comment` },
                    'supportThread-25.message-563.userLabel-from': `Даздраперма Дивизионная`,
                    'supportThread-25.message-563.to': `В рельсу`,
                    'supportThread-25.message-563.timestamp': `06/05/2014 17:33:34`,
                    'supportThread-25.message-563.message': dedent(`
                          В  окно  напротив уже высунулись те же старики, но зрителей там
                          прибавилось:  за  их  спинами  возвышался  огромный  мужчина  в
                          раскрытой  на  груди  рубахе, который все время крутил и вертел
                          свою рыжеватую бородку.
                               - Йозеф К.? - спросил инспектор, должно быть,  только  для
                          того, чтобы обратить на себя рассеянный взгляд К.
                               К. наклонил голову.
                               - Должно  быть,  вас  очень  удивили  события сегодняшнего
                          утра?`),
                    'supportThread-308.topic': `В пустом зале заседаний. Студент. Канцелярии`,
                    'supportThread-308.button-takeAndReply': { icon: `comment` },
                    'supportThread-308.message-607.userLabel-from': `Франц Кафка`,
                    'supportThread-308.message-607.to': `В рельсу`,
                    'supportThread-308.message-607.timestamp': `17/05/2014 02:33:15`,
                    'supportThread-308.message-607.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                    'supportThread-308.message-610.continuation': `В догонку`,
                    'supportThread-308.message-610.timestamp': `17/05/2014 02:35:27`,
                    'supportThread-308.message-610.message': dedent(`
                          Еще хотел бы добавить вот что.
                          
                          - Сегодня заседания нет, - сказала женщина.
                          - Как это - нет заседания? - спросил он, не поверив.`),
                    'supportThread-308.message-611.continuation': `В догонку`,
                    'supportThread-308.message-611.timestamp': `17/05/2014 02:36:02`,
                    'supportThread-308.message-611.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                    'supportThread-321.topic': `Видно,  собираетесь  навести  здесь  порядок?`,
                    'supportThread-321.button-takeAndReply': { icon: `comment` },
                    'supportThread-321.message-903.userLabel-from': `Джейн Остин`,
                    'supportThread-321.message-903.to': `В рельсу`,
                    'supportThread-321.message-903.timestamp': `21/05/2014 10:11:59`,
                    'supportThread-321.message-903.message': `Я так и догадалась по вашей речи. Мне лично она  очень  понравилась.  Правда,  я  не  все  слышала - начало пропустила, а под конец лежала со студентом на полу.`,
                    'supportThread-405.topic': `Ну  конечно  же!`,
                    'supportThread-405.button-takeAndReply': { icon: `comment` },
                    'supportThread-405.message-1018.userLabel-from': `Жорж Санд`,
                    'supportThread-405.message-1018.to': `В рельсу`,
                    'supportThread-405.message-1018.timestamp': `26/05/2014 10:51:24`,
                    'supportThread-405.message-1018.message': `Книги были старые, потрепанные, на одной  переплет был переломлен и обе половинки держались на ниточке.`,
                    'supportThread-708.topic': `Какая  тут  везде грязь, - сказал К., покачав головой`,
                    'supportThread-708.button-takeAndReply': { icon: `comment` },
                    'supportThread-708.message-2245.userLabel-from': `Василий Теркин`,
                    'supportThread-708.message-2245.to': `В рельсу`,
                    'supportThread-708.message-2245.timestamp': `13/06/2014 14:19:32`,
                    'supportThread-708.message-2245.message': `И женщине пришлось смахнуть пыль фартуком хотя бы сверху,  прежде чем К. мог взяться за книгу.`,
                    'supportThread-708.message-2249.continuation': `В догонку`,
                    'supportThread-708.message-2249.timestamp': `13/06/2014 14:25:13`,
                    'supportThread-708.message-2249.message': `Он  открыл  ту,  что  лежала  сверху, и увидел неприличную картинку.`,
                    'supportThread-914.topic': `Так вот какие юридические книги тут изучают!`,
                    'supportThread-914.button-takeAndReply': { icon: `comment` },
                    'supportThread-914.message-2357.userLabel-from': `Карлос Кастанеда`,
                    'supportThread-914.message-2357.to': `В рельсу`,
                    'supportThread-914.message-2357.timestamp': `16/07/2014 08:24:17`,
                    'supportThread-914.message-2357.message': dedent(`
                          - Я вам помогу! - сказала женщина. - Согласны?
                          - Но разве вы и вправду можете мне  помочь,  не  подвергая себя  опасности?  Ведь  вы  сами  сказали,  что ваш муж целиком зависит от своего начальства.
                          - И все же я вам помогу, - сказала  женщина.  Подите  сюда, надо  все обсудить. А о том, что мне грозит опасность, говорить не стоит.  Я  только  тогда  пугаюсь  опасности,  когда  считаю нужным. Идите сюда.
                          - Она показала на подмостки и попросила его сесть  рядом с ней на ступеньки.
                          
                          - У вас чудесные темные глаза, -сказала она, когда они сели, и заглянула К. в лицо. - Говорят, у меня тоже глаза красивые, но ваши куда красивее. Ведь  я  вас сразу  приметила,  еще  в первый раз, как только вы сюда зашли. Из-за вас я и пробралась потом в зал заседаний. Обычно я никогда этого не делаю, мне даже, собственно говоря,  запрещено  ходить сюда.`),
                    'supportThread-933.topic': `Если вам так не терпится, можете  уходить`,
                    'supportThread-933.button-takeAndReply': { icon: `comment` },
                    'supportThread-933.message-2455.userLabel-from': `Тело Странное`,
                    'supportThread-933.message-2455.to': `В рельсу`,
                    'supportThread-933.message-2455.timestamp': `17/07/2014 10:33:08`,
                    'supportThread-933.message-2455.message': `Давно  могли уйти,  никто  и  не  заметил бы вашего отсутствия. Да, да, надо было вам уйти, как только я пришел, и уйти сразу, немедленно. В этих словах слышалась не только сдержанная злоба, в  них ясно  чувствовалось высокомерие будущего чиновника по отношению к неприятному для него обвиняемому.`,
                    'supportThread-951.topic': `Ничего  не  поделаешь`,
                    'supportThread-951.button-takeAndReply': { icon: `comment` },
                    'supportThread-951.message-2599.userLabel-from': `Регина Дубовицкая`,
                    'supportThread-951.message-2599.to': `В рельсу`,
                    'supportThread-951.message-2599.timestamp': `06/08/2014 04:33:37`,
                    'supportThread-951.message-2599.message': `Его за мной прислал следователь, мне с вами идти никак нельзя, этот маленький уродец, - тут  она провела  рукой  по лицу студента, этот маленький уродец меня не отпустит.`,
                    'supportThread-1011.topic': `Оба  исчезли за поворотом`,
                    'supportThread-1011.button-takeAndReply': { icon: `comment` },
                    'supportThread-1011.message-2682.userLabel-from': `Варсоновий Оптинский`,
                    'supportThread-1011.message-2682.to': `В рельсу`,
                    'supportThread-1011.message-2682.timestamp': `15/08/2014 13:32:42`,
                    'supportThread-1011.message-2682.message': `К. все еще стоял в дверях. Он должен был признаться, что женщина не только обманула его, но и солгала, что ее несут к следователю. Не станет  же  следователь сидеть  на  чердаке  и  дожидаться  ее.`,
                    pageHeader: `Куча работы`,
                    'TopNavItem-admin-my-tasks': { title: `Мои задачи` },
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
                    
                    'supportThread-1134.topic': `Вот видите!`,
                    'supportThread-1134.button-takeAndReply': { icon: `comment` },
                    'supportThread-1134.message-2732.userLabel-from': `Федор Достоевский`,
                    'supportThread-1134.message-2732.to': `В рельсу`,
                    'supportThread-1134.message-2732.timestamp': `11/10/2014 08:15:11`,
                    'supportThread-1134.message-2732.message': `Вечно  ее  от  меня уносят.  Сегодня воскресенье, работать я не обязан, а мне вдруг дают совершенно ненужные  поручения,  лишь  бы  услать  отсюда.`,
                    'supportThread-1225.topic': `А разве другого выхода нет?`,
                    'supportThread-1225.button-takeAndReply': { icon: `comment` },
                    'supportThread-1225.message-2768.userLabel-from': `Ксенофонт Тутанский`,
                    'supportThread-1225.message-2768.to': `В рельсу`,
                    'supportThread-1225.message-2768.timestamp': `23/10/2014 18:10:13`,
                    'supportThread-1225.message-2768.message': dedent(`
                          - Другого не вижу, - сказал  служитель.  -  И  главное,  с
                          каждым  днем все хуже: до сих пор он таскал ее только к себе, а
                          сейчас  потащил  к  самому  следователю;   впрочем,   этого   я
                          давным-давно ждал.`),
                    'supportThread-1372.topic': `Какой смысл?`,
                    'supportThread-1372.button-takeAndReply': { icon: `comment` },
                    'supportThread-1372.message-3008.userLabel-from': `Евгений Ваганович`,
                    'supportThread-1372.message-3008.to': `В рельсу`,
                    'supportThread-1372.message-3008.timestamp': `25/11/2014 05:39:53`,
                    'supportThread-1372.message-3008.message': dedent(`
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
                #hawait testGlobal.controls['supportThread-308.button-takeAndReply'].click()
                // #hawait art.shitBlinksForMax({$tag: 'f146dce3-e5dc-4846-bb65-868c8083b859', kind: 'button', name: 'takeAndReply-308', max: 2000})
                
                #hawait art.uiState({$tag: '404c23b9-e496-41b5-a105-71ae5c44ed5f', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html?thread=308`,
                        'Select-ordering': `desc`,
                        'message-611.userLabel-from': `Франц Кафка`,
                        'message-611.to': `В рельсу`,
                        'message-611.timestamp': `17/05/2014 02:36:02`,
                        'message-611.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                        'message-610.userLabel-from': `Франц Кафка`,
                        'message-610.to': `В рельсу`,
                        'message-610.timestamp': `17/05/2014 02:35:27`,
                        'message-610.message': dedent(`
                              Еще хотел бы добавить вот что.
                              
                              - Сегодня заседания нет, - сказала женщина.
                              - Как это - нет заседания? - спросил он, не поверив.`),
                        'message-607.userLabel-from': `Франц Кафка`,
                        'message-607.to': `В рельсу`,
                        'message-607.timestamp': `17/05/2014 02:33:15`,
                        'message-607.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                        pageHeader: `Запрос в поддержку № 308`,
                        'button-plus': { icon: `comment` },
                        'TopNavItem-admin-my-tasks': { title: `Мои задачи` },
                        'TopNavItem-admin-heap': { title: `Куча` },
                        'TopNavItem-dashboard': { title: `Тодд` },
                        'liveBadge-topNavItem-admin-heap': `12` 
                }})
                
                testGlobal.controls['TopNavItem-admin-heap'].showHand({testActionHandOpts: {pointingFrom: 'right', dleft: -15}})
                #hawait art.pausePoint({title: 'Amount of work in heap decreased, since we’ve just taken one piece from it', $tag: '3ebb103e-7f0a-4669-9a29-2cbb2bbed460'})
                testGlobal.controls['TopNavItem-admin-heap'].hideHand()
                
                #hawait art.pausePoint({title: 'Changing ordering to show old messages first', $tag: '1e77736a-3498-4b5d-8b1e-94c0f7dd6c56'})
                // Action
                #hawait testGlobal.controls['Select-ordering'].setValue({value: 'asc', testActionHandOpts: {pointingFrom: 'left', dtop: 38}})
                #hawait art.uiState({$tag: '73952bab-8024-4351-8656-0966860aa31b', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html?thread=308&ordering=asc`,
                        'Select-ordering': `asc`,
                        'TopNavItem-admin-my-tasks': { title: `Мои задачи` },
                        'TopNavItem-admin-heap': { title: `Куча` },
                        'TopNavItem-dashboard': { title: `Тодд` },
                        'liveBadge-topNavItem-admin-heap': `12`,
                        'message-607.userLabel-from': `Франц Кафка`,
                        'message-607.to': `В рельсу`,
                        'message-607.timestamp': `17/05/2014 02:33:15`,
                        'message-607.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                        'message-610.userLabel-from': `Франц Кафка`,
                        'message-610.to': `В рельсу`,
                        'message-610.timestamp': `17/05/2014 02:35:27`,
                        'message-610.message': dedent(`
                              Еще хотел бы добавить вот что.
                              
                              - Сегодня заседания нет, - сказала женщина.
                              - Как это - нет заседания? - спросил он, не поверив.`),
                        'message-611.userLabel-from': `Франц Кафка`,
                        'message-611.to': `В рельсу`,
                        'message-611.timestamp': `17/05/2014 02:36:02`,
                        'message-611.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                        pageHeader: `Запрос в поддержку № 308`,
                        'button-plus': { icon: `comment` } 
                }})
                
                #hawait art.pausePoint({title: 'Changing it back to conveniently show new stuff first', $tag: '79a2d7fc-5509-41fc-b606-b82bdccd5d68'})
                // Action
                #hawait testGlobal.controls['Select-ordering'].setValue({value: 'desc', testActionHandOpts: {pointingFrom: 'left', dtop: 18}})
                #hawait art.uiState({$tag: '1920f57c-db76-40ff-9f85-8ebdd8faf2f3', expected: {
                    url: `http://aps-ua-writer.local:3022/support.html?thread=308&ordering=desc`,
                        'Select-ordering': `desc`,
                        'TopNavItem-admin-my-tasks': { title: `Мои задачи` },
                        'TopNavItem-admin-heap': { title: `Куча` },
                        'TopNavItem-dashboard': { title: `Тодд` },
                        'liveBadge-topNavItem-admin-heap': `12`,
                        'message-611.userLabel-from': `Франц Кафка`,
                        'message-611.to': `В рельсу`,
                        'message-611.timestamp': `17/05/2014 02:36:02`,
                        'message-611.message': `И еще немного... Чтобы  убедить  его,  женщина  отворила  дверь  в соседнее помещение.`,
                        'message-610.userLabel-from': `Франц Кафка`,
                        'message-610.to': `В рельсу`,
                        'message-610.timestamp': `17/05/2014 02:35:27`,
                        'message-610.message': dedent(`
                              Еще хотел бы добавить вот что.
                              
                              - Сегодня заседания нет, - сказала женщина.
                              - Как это - нет заседания? - спросил он, не поверив.`),
                        'message-607.userLabel-from': `Франц Кафка`,
                        'message-607.to': `В рельсу`,
                        'message-607.timestamp': `17/05/2014 02:33:15`,
                        'message-607.message': `Всю следующую неделю К.  изо  дня  в  день  ожидал  нового вызова,  он  не  мог  поверить,  что его отказ от допроса будет принят буквально, а когда ожидаемый вызов до субботы так  и  не пришел, К. усмотрел в этом молчании приглашение в тот же дом на тот  же  час.  Поэтому в воскресенье он снова отправился туда и прямо прошел по этажам и коридорам  наверх;  некоторые  жильцы, запомнившие его, здоровались с ним у дверей, но ему не пришлось никого  спрашивать,  и  он  сам подошел к нужной двери. На стук открыли сразу, и,  не  оглядываясь  на  уже  знакомую  женщину, остановившуюся у дверей, он хотел пройти в следующую комнату.`,
                        pageHeader: `Запрос в поддержку № 308`,
                        'button-plus': { icon: `comment` } 
                }})
                
                // Action
                #hawait testGlobal.controls['button-plus'].click()
                art.uiStateChange({$tag: 'bd47917a-b212-4d03-8910-5af36b2f7ebc', expected: {
                    $$deleted: ['Select-ordering', 'button-plus'],
                    
                    'Input-message': '',
                    'button-primary': { title: 'Запостить' },
                    'button-cancel': { title: 'Передумал' }
                }})

                
                // /*killme*/ setTestSpeed('slow'); art.respectArtPauses = true
            },
        }
    }
    
}
