package aps.front

import aps.*
import aps.front.testutils.*

// TODO:vgrechka Use paths from pageSpecs in URLs

class Test_UA_CrazyLong_2 : FuckingScenario() {
    // http://aps-ua-writer.local:3022/faq.html?test=Test_UA_CrazyLong_2&stopOnAssertions=true&dontStopOnCorrectAssertions=true&animateUserActions=false&handPauses=true

    override suspend fun run1() {
        val testdata = object {
            val details = "Кто-то, по-видимому, оклеветал Йозефа К., потому  что,  не сделав   ничего  дурного,  он  попал  под  арест.\n\nКухарка  его квартирной хозяйки,  фрау  Грубах,  ежедневно  приносившая  ему завтрак около восьми, на этот раз не явилась. Такого случая еще не  бывало. К. немного подождал, поглядел с кровати на старуху, живущую напротив, - она смотрела из окна с  каким-то  необычным для  нее  любопытством - и потом, чувствуя и голод, и некоторое недоумение, позвонил. Тотчас же  раздался  стук,  и  в  комнату вошел  какой-то  человек. К. никогда раньше в этой квартире его не видел."
        }

        TestGlobal.defaultAssertScreenOpts = AssertScreenOpts(
            bannerVerticalPosition = VerticalPosition.TOP,
            bannerHorizontalPosition = HorizontalPosition.RIGHT)

        forceFast {
            initialTestShit(this)

            val startPoint = 2
            var point = 0

            if (++point >= startPoint) {
                run { // Make order
                    val ivo1 = Morda("ivo1",
                                     url = fconst.test.url.customer,
                                     fillTypedStorageLocal = {},
                                     fillRawStorageLocal = {})
                    ivo1.coitizeAndBootAsserting(assertStatic = {assertAnonymousCustomerStaticIndexScreen()},
                                                 assertDynamic = {assertAnonymousCustomerDynamicIndexScreen()})
                    topNavItemSequence(page = pageSpecs.uaCustomer.makeOrder_testRef,
                                       aid = "00c34b38-a47d-4ae5-a8f3-6cceadb0d481")
                    debugMailboxClear()
                    selectSetValue(fieldSpecs.shebang.ua.documentType.testRef, UADocumentType.PRACTICE)
                    imposeNextGeneratedConfirmationSecret("top-fucking-secret")
                    formSubmissionAttempts(
                        testShit, baseID = "c31b6b5e-aac1-4136-8bef-906cf5be8cdc-1",
                        attempts = eachOrCombinationOfLasts(listOf(
                            badTextFieldValuesThenValid(fieldSpecs.shebang.documentTitle.testRef, "Как я пинал хуи на практике"),
                            badIntFieldValuesThenValid(fieldSpecs.shebang.numPages.testRef, 13),
                            badIntFieldValuesThenValid(fieldSpecs.shebang.numSources.testRef, 5),
                            badTextFieldValuesThenValid(fieldSpecs.shebang.orderDetails.testRef, testdata.details),
                            badTextFieldValuesThenValid(fieldSpecs.shebang.anonymousCustomerName.testRef, "Пися Камушкин"),
                            badTextFieldValuesThenValid(fieldSpecs.shebang.phone.testRef, "+38 (068) 123-45-67"),
                            badTextFieldValuesThenValid(fieldSpecs.shebang.email.testRef, "pisia@test.shit.ua")
                        ))
                    )
                    debugMailboxCheck("b9196719-9e01-45f3-987c-cb8259c7f9e6")
                }

                // TODO:vgrechka Try to access order page before it's confirmed (should say "fuck you" to user)

                run { // Wrong confirmation secret
                    val ivo2 = Morda("ivo2",
                                     url = fconst.test.url.customer + "/confirmOrder.html?secret=wrong-secret",
                                     fillTypedStorageLocal = {},
                                     fillRawStorageLocal = {})
                    ivo2.coitizeAndBootAsserting(assertStatic = {assertScreenHTML("Static confirmOrder", "7d46b2b1-e303-4146-9a3f-a89e02a1fe23")},
                                                 assertDynamic = {assertScreenHTML("Dynamic confirmOrder", "45a5842e-ffe9-4e78-9261-9a5056a3c11f")})
                }

                run { // Correct confirmation secret
                    val ivo3 = Morda("ivo3",
                                     url = fconst.test.url.customer + "/confirmOrder.html?secret=top-fucking-secret",
                                     fillTypedStorageLocal = {},
                                     fillRawStorageLocal = {})
                    ivo3.coitizeAndBootAsserting(assertStatic = {assertScreenHTML("Static confirmOrder", "2acbad6a-e169-4c0d-9938-99fac621fef5")},
                                                 assertDynamic = {assertScreenHTML("Dynamic confirmOrder", "a6a44d05-7c1d-4dbf-82a2-3b42e0ca98f3")})
                }

                run { // Edit params -- cancel
                    step({buttonClick(fconst.key.button.edit.testRef)}, TestGlobal.modalShownLock, "1_9b32c20b-bcdb-4024-b068-5c6a36231944")
                    inputSetValue(fieldSpecs.shebang.documentTitle.testRef, "Хуй")
                    step({buttonClick(fconst.key.button.cancel.testRef)}, TestGlobal.modalHiddenLock, "1_65da1c1a-7b2d-487e-a9cb-e99035eaa04b")
                }

                run { // Edit params -- save
                    step({buttonClick(fconst.key.button.edit.testRef)}, TestGlobal.modalShownLock, "f0386438-99f7-417a-83a6-b29d804a1b1c")
                    selectSetValue(fieldSpecs.shebang.ua.documentType.testRef, UADocumentType.LAB)
                    formSubmissionAttempts(
                        testShit, baseID = "3_beaa5793-9590-415e-8bc9-ca6fec7ead52",
                        attempts = eachOrCombinationOfLasts(listOf(
                            badTextFieldValuesThenValid(fieldSpecs.shebang.documentTitle.testRef, "Как я пинал большие хуи на практике"),
                            badIntFieldValuesThenValid(fieldSpecs.shebang.numPages.testRef, 23),
                            badIntFieldValuesThenValid(fieldSpecs.shebang.numSources.testRef, 7),
                            badTextFieldValuesThenValid(fieldSpecs.shebang.orderDetails.testRef, "Это чисто на почитать... " + testdata.details),
                            badTextFieldValuesThenValid(fieldSpecs.shebang.phone.testRef, "+38 (068) 321-45-67")
                        ))
                    )
                }

                run { // Edit params -- save 2
                    step({buttonClick(fconst.key.button.edit.testRef)}, TestGlobal.modalShownLock, "b556cf5e-0184-4ce0-8560-f083861116e7")
                    selectSetValue(fieldSpecs.shebang.ua.documentType.testRef, UADocumentType.PRACTICE)
                    submitFormSequence(testShit, aid = "6ea13411-892b-4e96-a1b8-c77b23e29567")
                }

                send(TestTakeDBSnapshotRequest()-{o->
                    o.snapshotName.value = "pizda-$point"
                    o.browseroidName.value = TestGlobal.currentMorda.browseroidName
                    o.href.value = Globus.currentBrowseroid.location.href
                    o.token.value = Globus.currentBrowseroid.typedStorageLocal.token
                })
            }

            if (++point >= startPoint) {
                if (point == startPoint) {
                    val state = send(TestRestoreDBSnapshotRequest()-{o->
                        o.snapshotName.value = "pizda-${point - 1}"
                    })
                    dlog("Snapshot response", state)
                    val ivo3 = Morda(state.browseroidName,
                                     url = state.href,
                                     fillTypedStorageLocal = {it.token = state.token},
                                     fillRawStorageLocal = {})
                    ivo3.coitizeAndBoot()
                }
                run { // Add some files
                    // TODO:vgrechka Assert only modal's content, reuse assertion
                    sequence({tabClick(fconst.tab.order.files.testRef)},
                             steps = listOf(
                                 PauseAssertResumeStep(TestGlobal.switchTabHalfwayLock, "f727a9ea-c520-4613-97e0-c154f6506d3a"),
                                 PauseAssertResumeStep(TestGlobal.switchTabDoneLock, "f621673e-7f84-4a53-969f-8844614c4f30")))
                    addFile(fileName = "little pussy.rtf", title = "Задание на практику", details = "- Вам следовало остаться у себя в комнате! Разве Франц вам ничего не говорил?\n\n- Что вам, наконец, нужно? - спросил К., переводя взгляд с нового посетителя на того,  кого  назвали  Франц  (он  стоял  в дверях),  и  снова  на первого.  В  открытое окно видна была та старуха: в припадке старческого любопытства она уже  перебежала к другому окну - посмотреть, что дальше.", aid = "4c30a4f4-5bee-426a-ab7f-960367b2c198")
                    addFile(fileName = "crazy monster boobs.rtf", title = "Шаблон отчета", details = "Меньше всего К. боялся, что  его  потом упрекнут  в  непонимании  шуток,  зато он отлично помнил - хотя обычно с прошлым опытом и не считался - некоторые случаи,  сами по  себе  незначительные,  когда  он  в отличие от своих друзей сознательно пренебрегал возможными  последствиями  и  вел  себя крайне  необдуманно  и  неосторожно,  за  что  и  расплачивался полностью. Больше этого с ним повториться не  должно,  хотя  бы теперь,  а если это комедия, то он им подыграет. Но пока что он еще свободен.", aid = "167277d8-dcae-4de6-ae6c-a39f1981e6f5")
                    addFile(fileName = "pussy story.rtf", title = "Методичка какая-то левая", details = "В комнате он тотчас же стал выдвигать ящики стола; там был образцовый  порядок,  но  удостоверение  личности,  которое  он искал, он от волнения никак найти  не  мог.  Наконец  он  нашел удостоверение на велосипед и уже хотел идти с ним к стражам, но потом  эта  бумажка  показалась  ему неубедительной, и он снова стал искать, пока не нашел свою метрику.", aid = "54b2bb55-17fe-44e6-98b2-c631933c17ea")
                    addFile(fileName = "shitty document 1.rtf", title = "Let Over Lambda", details = "Let Over Lambda is one of the most hardcore computer programming books out there. Starting with the fundamentals, it describes the most advanced features of the most advanced language: Common Lisp. Only the top percentile of programmers use lisp and if you can understand this book you are in the top percentile of lisp programmers. If you are looking for a dry coding manual that re-hashes common-sense techniques in whatever langue du jour, this book is not for you. This book is about pushing the boundaries of what we know about programming. While this book teaches useful skills that can help solve your programming problems today and now, it has also been designed to be entertaining and inspiring. If you have ever wondered what lisp or even programming itself is really about, this is the book you have been looking for.", aid = "89209597-11ef-4d78-8575-bd39bf6fca26")
                    addFile(fileName = "shitty document 2.rtf", title = "Land of Lisp", details = "Lisp has been hailed as the world’s most powerful programming language, but its cryptic syntax and academic reputation can be enough to scare off even experienced programmers. Those dark days are finally over—Land of Lisp brings the power of functional programming to the people!\n\nWith his brilliantly quirky comics and out-of-this-world games, longtime Lisper Conrad Barski teaches you the mysteries of Common Lisp. You’ll start with the basics, like list manipulation, I/O, and recursion, then move on to more complex topics like macros, higher order programming, and domain-specific languages. Then, when your brain overheats, you can kick back with an action-packed comic book interlude! ", aid = "5ac8d7c0-bd39-4e12-bdfa-451074af8f4d")
                    addFile(fileName = "shitty document 3.rtf", title = "Сорочинская ярмарка", details = "Крестьянин Солопий Черевик вместе с красавицей-дочкой Параской и сварливой женой Хавроньей (мачехой Параски) прибывает на ярмарку, чтобы продать десяток мешков пшеницы и старую лошадь. Им встречается Грицко — молодой юноша, влюбившийся в Параску, но тут же поссорившийся с её мачехой. Грицко сватается к Параске, Солопий не против свадьбы, но Хавронья яростно возражает и свадьба отменяется. Тогда Грицко обращается к цыгану, который обещает свадьбу всё-таки устроить.", aid = "32375c92-477b-4d60-ab3a-a6636be02ca8")
                    addFile(fileName = "shitty document 4.rtf", title = "Вечер накануне Ивана Купала", details = "Впервые была напечатана в 1830 году в февральском и мартовском выпусках «Отечественных записок» без подписи автора, под заглавием «Бисаврюк, или Вечер накануне Ивана Купала. Малороссийская повесть (из народного предания), рассказанная дьячком Покровской церкви». Издателем были внесены в неё многочисленные правки. Этим обстоятельством объясняется появившийся в предисловии текст, высмеивающий от имени рассказчика редакторское самоуправство[1].", aid = "6cf2b942-dee8-4880-866b-927cb53c2f7b")
                    addFile(fileName = "shitty document 5.rtf", title = "Майская ночь, или Утопленница", details = "Тихим и ясным вечером девушки с парубками собираются и поют песни. Молодой казак Левко, сын сельского головы, подойдя к одной из хат, песнею вызывает ясноокую Ганну. Ганна появляется не сразу: она боится зависти девушек и дерзости парубков и материнской строгости, и ещё чего-то неясного. Левко не раз просил отца разрешить ему жениться на Ганне, но тот не хотел и по своему давнему обыкновению притворялся глухим. О самом голове «известно, что некогда он сопровождал царицу Екатерину в Крым, о чём любит при случае поминать, ныне крив, суров, важен и вдов, живёт несколько под каблуком своей свояченицы».", aid = "994fa207-d9b0-47cd-9ac8-22c64acfdfeb")
                    addFile(fileName = "shitty document 6.rtf", title = "Пропавшая грамота", details = "Повествование начинается с сетований рассказчика, Фомы Григорьевича, на тех слушательниц, что выпытывают у него «яку-нибудь страховинну казочку», а потом всю ночь дрожат под одеялом. Затем он рассказывает историю, что случилась с его дедом, которого гетман послал с грамотой к царице.\n\nДед, простившись с женой и детьми, уж наутро был в Конотопе, где была ярмарка. С зашитой в шапку грамотой дед пошёл поискать себе огнива и табаку и познакомился с гулякой-запорожцем. Они отправились дальше вместе с приставшим к ним ещё одним гулякой. Запорожец, потчуя весь вечер приятелей диковинными историями, к ночи оробел и наконец открылся, что продал душу дьяволу и этою ночью наступает срок расплаты. Дед обещался не спать ночью, чтоб пособить запорожцу. Путешественники принуждены были остановиться в ближайшем шинке. Уснули вскоре оба дедовых попутчика, так что ему пришлось нести караул в одиночку. Как мог, боролся дед со сном, но мертвый сон сморил его, он проснулся поздним утром и не нашел ни запорожца, ни коней. Пропала и дедова шапка с грамотой, которою вчера поменялся дед с запорожцем. Дед попросил совета у бывших в шинке чумаков, и наконец шинкарь указал деду, где найти чёрта, чтоб вытребовать у него обратно грамоту[1].", aid = "4555ef60-7933-4198-8a8d-75bf5b50267c")
                    addFile(fileName = "shitty document 7.rtf", title = "Ночь перед Рождеством", details = "Действие повести хронологически приурочено к эпизоду царствования Екатерины II — последней депутации запорожцев, состоявшейся в 1775 г. и связанной с работой Комиссии по упразднению Запорожской Сечи.\n\nСюжет произведения разворачивается в Диканьке, на территории современной Украины. Никем не замеченные, в небе кружатся двое: ведьма на метле, которая набирает в рукав звёзды, и чёрт, который прячет месяц в карман, думая, что наступившая тьма удержит дома богатого казака Чуба, приглашенного к дьяку на кутью, и ненавистный чёрту кузнец Вакула (нарисовавший на церковной стене картину Страшного суда и посрамляемого чёрта) не осмелится прийти к Чубовой дочери Оксане.", aid = "ad682561-681e-4193-80f6-22d33bc740e0")
                    addFile(fileName = "shitty document 8.rtf", title = "Страшная месть", details = "Старый есаул женит своего сына. Отец выносит иконы, чтобы благословить ими молодых, но неожиданно один из гостей - казаков превращается в страшного урода. Все в ужасе от него отпрянули, но есаул Горобець догадывается, что это превращение связано с иконами и что перед ним колдун. С помощью икон нечестивец был изгнан. Одними из гостей на свадьбе были названный брат есаула, Данило Бурульбаш и его жена Катерина. Катерина - сирота, её мать умерла, а отец пропал, когда она была совсем маленькой и совсем его не помнила. Но неожиданно после свадьбы в гости приезжает отец Катерины и между зятем и тестем сразу возникает ссора.", aid = "5901fc21-d97f-4db9-a19b-50e49773b9e5")
                    addFile(fileName = "shitty document 9.rtf", title = "Иван Фёдорович Шпонька и его тётушка", details = "«Иван Фёдорович Шпонька и его тётушка» по своему реалистическому характеру, уже во многом близкому к наиболее зрелым произведениям Гоголя, стоит особняком среди остальных повестей «Вечеров на хуторе близ Диканьки». Это обстоятельство позволяет утверждать, что написана она могла быть позже всего цикла украинских повестей. Поэтому наиболее вероятным временем написания «Шпоньки» можно считать конец 1831 года (31 января 1832 г. датировано цензурное разрешение второй части «Вечеров»)[1].", aid = "fb29bd7c-adad-4e31-abbb-34fd12b88e22")
                    addFile(fileName = "shitty document 10.rtf", title = "Заколдованное место", details = "Рассказчик объясняет, что повествование относится к тому времени, когда он был ещё ребёнком. Отец с одним из сыновей уехал в Крым продавать табак, оставив дома жену и трёх сыновей да деда стеречь баштан — дело прибыльное, проезжих много, а всего лучше — чумаки, что рассказывали диковинные истории. Как-то к вечеру приходит несколько возов с чумаками, да все старинными дедовыми знакомцами. Перецеловались, закурили, пошёл разговор, а там и угощение. Потребовал дед, чтоб внуки плясали, гостей потешили, да недолго терпел, сам пошёл. Плясал дед славно, такие кренделя выделывал, что диво, покуда не дошёл до одного места, близ грядки с огурцами. Здесь ноги его стали. Бранился, и снова начинал — без толку. Сзади кто-то засмеялся. Огляделся, а места не узнает: и баштан, и чумаки — всё пропало, вокруг одно гладкое поле. Все ж понял, где он, за поповым огородом, за гумном волостного писаря. «Вот куда затащила нечистая сила!» Стал выбираться, месяца нет, нашёл в темноте дорожку. На могилке поблизости вспыхнул огонёк, и другой чуть поодаль. «Клад!» — решил дед и навалил для приметы изрядную ветку, поскольку заступа при себе не имел. Поздно вернулся он на баштан, чумаков не было, дети спали[2].", aid = "122c1741-19cb-4fcd-aefa-a19e3444d9e2")
                    addFile(fileName = "shitty document 11.rtf", title = "Вий", details = "Три студента киевской бурсы отправились на каникулы заняться репетиторством. По дороге заплутали в темноте и попросили ночлега на отдаленном хуторе. Один из студентов, Хома Брут, по странной воле старухи-хозяйки получил место для ночлега в хлеву. Готовясь в темноте ко сну, Хома был напуган неожиданным визитом хозяйки, та вскочила на него верхом и поскакала по полям и буеракам. Измученный Хома стал читать молитвы и заметил, что чары ведьмы слабеют. Одна из молитв помогла ему освободиться. Хома сам вскочил верхом на старуху и стал погонять её, охаживая поленом. К утру чары окончательно развеялись, и старуха превратилась в прекрасную панночку, в полном изнеможении упавшую на землю.", aid = "dae4f210-c73d-4dc2-98b4-a8dd748f9cf1")
                    addFile(fileName = "shitty document 12.rtf", title = "Повесть о том, как поссорился Иван Иванович с Иваном Никифоровичем", details = "Миргород — идеально — красивый город. В нём всё прекрасно, начиная от «домов и домиков, которые издали можно принять за копны сена» и заканчивая уникальной, неповторимой лужей на площади, «удивительной лужей, единственной, которую вам только удавалось когда видеть». Кроме того там «нет ни воровства, ни мошенничества». И в этом городе живут два друга, являющие собой символ благополучия и стабильности города — Иван Иванович Перерепенко и Иван Никифорович Довгочхун. Оба — прекрасные люди.\n\nКак-то раз «в июле месяце» Иван Иванович обнаружил, что у Ивана Никифоровича есть замечательное ружьё. Ивану Ивановичу «очень захотелось иметь это ружьецо». Он отправляется к Ивану Никифоровичу с целью выпросить его или поменять на бурую свинью. Но Иван Никифорович не захотел отдавать ружья и, поддавшись на провокацию Ивана Ивановича, назвал того гусаком. Иван Иванович страшно обиделся и поссорился с Иваном Никифоровичем.", aid = "ad3d9158-0a25-4d25-8c67-e2f75819d802")
                    addFile(fileName = "shitty document 13.rtf", title = "Старосветские помещики", details = "Афанасий Иванович был высок, ходил всегда в бараньем тулупчике, и практически всегда улыбался. Пульхерия Ивановна почти никогда не смеялась, но «на лице и в глазах её было написано столько доброты, столько готовности угостить вас всем, что было у них лучшего, что вы, верно, нашли бы улыбку уже чересчур приторною для её доброго лица». Афанасий Иванович и Пульхерия Ивановна живут уединённо в одной из отдалённых деревень, называемых в Малороссии старосветскими. Жизнь их так тиха, что гостю, заехавшему ненароком в низенький барский домик, утопающий в зелени сада, страсти и тревожные волнения внешнего мира покажутся не существующими вовсе. Маленькие комнаты домика заставлены всевозможными вещицами, двери поют на разные лады, кладовые заполнены припасами, приготовлением которых беспрестанно заняты дворовые под управлением Пульхерии Ивановны. Несмотря на то, что хозяйство обкрадывается приказчиком и лакеями, благословенная земля производит всего в таком количестве, что Афанасий Иванович и Пульхерия Ивановна совсем не замечают хищений.", aid = "2d571dea-eb90-4c6b-9163-582d6a6d88a5")
                    addFile(fileName = "shitty document 14.rtf", title = "Тарас Бульба", details = "К старому казацкому полковнику Тарасу Бульбе приезжают после выпуска из Киевской академии (Киев с 1569 года по 1654 год входит в состав Речи Посполитой) два его сына — Остап и Андрий. Два дюжих молодца, здоровые и крепкие, лиц которых ещё не касалась бритва, смущены встречей с отцом, подшучивающим над их одеждой недавних семинаристов. Старший, Остап, не выдерживает насмешек отца: «Хоть ты мне и батька, а как будешь смеяться, то, ей-Богу, поколочу!» И отец с сыном, вместо приветствия после давней отлучки, совсем нешуточно мутузят друг друга тумаками. Бледная, худощавая и добрая мать старается образумить буйного своего мужа, который уже и сам останавливается, довольный, что испытал сына. Бульба хочет таким же образом «поприветствовать» и младшего, но того уже обнимает мать, защищая от отца.", aid = "f9cf2708-fccb-439e-8281-4cb458dec91b")
                    addFile(fileName = "shitty document 15.rtf", title = "Невский проспект", details = "Повесть начинается со слов «Нет ничего лучше Невского проспекта, по крайней мере в Петербурге; для него он составляет всё». Далее следует описание того, как Невский проспект меняется с раннего утра до поздней ночи.\n\nДва молодых человека — поручик Пирогов и художник Пискарёв — ухлёстывают вечером за гуляющими по Невскому проспекту дамами. Художник следует за брюнеткой, лелея на её счет романтическую влюблённость. Они доходят до Литейной и, поднявшись на верхний этаж ярко освещенного четырёхэтажного дома, оказываются в комнате, где находятся ещё три женщины, по виду которых Пискарев с ужасом догадывается, что попал в публичный дом.", aid = "99ba625c-d118-499c-856b-95fd217a18ee")
                }
            }
        }
    }

    private suspend fun addFile(fileName: String, title: String, details: String, aid: String) {
        seq({buttonClick(fconst.key.button.plus.testRef)}, TestGlobal.modalShownLock, "d7249410-e04b-421b-90db-2e4b538fab90--emptyAddFileModal")
        fileFieldChoose(fileName, "$aid--1")
        inputSetValue(fieldSpecs.shebang.fileTitle.testRef, title)
        inputSetValue(fieldSpecs.shebang.fileDetails.testRef, details)
        submitFormSequence(testShit, aid = "$aid--2")
    }
}



//send(TestCodeFiddleRequest()-{it.what.value = "fuck1"}) //; waitTillEndOfTime()











