package aps.front.testutils

import aps.*
import aps.front.*
import aps.front.Globus.effectsPane
import into.mochka.assertEquals
import org.w3c.dom.Storage
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import kotlin.browser.document
import kotlin.browser.window
import kotlin.properties.Delegates
import kotlin.properties.Delegates.notNull
import kotlin.reflect.KProperty


external interface IStorage {
    fun getItem(key: String): String?
}

//fun TestScenarioBuilder.initFuckingBrowser(fillTypedStorageLocal: (TypedStorageLocal) -> Unit = {}) {
//    act {
//        _initFuckingBrowser(fillTypedStorageLocal)
//    }
//}


//fun _initFuckingBrowser(fillTypedStorageLocal: (TypedStorageLocal) -> Unit = {}, fillRawStorageLocal: (StorageLocal) -> Unit = {}) {
//    val bro = TestBrowseroid("shit", "http://pizda.com")
//    fillTypedStorageLocal(bro.typedStorageLocal)
//    fillRawStorageLocal(bro.typedStorageLocal.store)
//    Globus.currentBrowseroid = bro
//
//    ExternalGlobus.storageLocalForStaticContent = object : IStorage {
//        override fun getItem(key: String) = bro.typedStorageLocal.store.getItem(key)
//    }
//}

//fun TestScenarioBuilder.kindaNavigateToStaticContent(url: String) {
//    acta {_kindaNavigateToStaticContent(url)}
//}

//fun _kindaNavigateToStaticContent(url: String): Promisoid<Unit> {
//    return async {
//        dlog("_kindaNavigateToStaticContent: $url")
//        val content = measureAndReportToDocumentElement("Loading $url") {
//            await(fetchURL(url, "GET", null))
//        }
//
//        val openingHeadTagIndex = content.indexOfOrDie("<head")
//        val closingHTMLTagIndex = content.indexOfOrDie("</html>")
//        val innerHTMLContent = content.substring(openingHeadTagIndex, closingHTMLTagIndex)
//
//        killEverythingVisual()
//        setDocInnerHTML(innerHTMLContent)
//        loadCSS()
//        (currentBrowseroid as TestBrowseroid).replaceWholeURL(url)
//        ExternalGlobus.displayInitialShit()
//    }
//}

inline fun <T> measureAndReportToDocumentElement(name: String, block: () -> T): T {
    setDocInnerHTML(getDocInnerHTML() + "$name....")
    val res = measure(name, block)
    setDocInnerHTML(getDocInnerHTML() + " DONE<br>")
    return res
}

fun getDocInnerHTML() = document.documentElement!!.innerHTML

fun setDocInnerHTML(value: String) {
    document.documentElement!!.innerHTML = value
}

suspend fun assertCustomerStaticIndexScreen() {
    assertScreenHTML("Customer index", "aead9163-e41b-4ec1-9967-47670291dadc")
}

suspend fun assertCustomerSignInScreen() {
    assertScreenHTML("Customer sign-in screen", "38dcedaf-5aad-43e8-ac3b-685b681fb75c")
}

suspend fun assertCustomerSignUpScreen() {
    assertScreenHTML("Customer sign-up screen", "1de9ffb2-3215-42e0-a67c-c9c2856880b8")
}

//fun TestScenarioBuilder.assertEmailThenClear(descr: String, expectedTo: String, expectedSubject: String, expectedBody: String) {
//    act {initDebugMailbox()}
//    acta {debugCheckEmail()}
//    assertMailInFooter(descr, expectedTo, expectedSubject, expectedBody)
//    acta {ClearSentEmailsRequest.send()}
//    act {debugHideMailbox()}
//}

//fun TestScenarioBuilder.imposeNextRequestTimestampUTC(stamp: String) {
//    acta {ImposeNextRequestTimestampRequest.send(stamp)}
//}
//
//fun TestScenarioBuilder.imposeNextGeneratedPassword(password: String) {
//    acta {imposeNextGeneratedPassword2(password)}
//}

suspend fun assertFreshCustomerDashboardScreen() {
    assertScreenHTML("Fresh customer's dashboard screen", "39ffecee-5b3f-4bf0-b9c6-43256a58a663")
}

suspend fun assertCustomerWithTokenStaticIndexScreen() {
    assertScreenHTML("assertCustomerWithTokenStaticIndexScreen", "0281a518-32fe-45d2-b222-332490b8291c")
}

suspend fun assertCustomerWithTokenDynamicIndexScreen() {
    assertScreenHTML("assertCustomerWithTokenDynamicIndexScreen", "1e961b10-1466-48d9-a481-65db5ca537ac")
}

suspend fun assertAnonymousCustomerStaticIndexScreen() {
    assertScreenHTML("assertAnonymousCustomerStaticIndexScreen", "8ec07df0-ed4e-46b2-a33c-23437aaba923")
}

suspend fun assertAnonymousCustomerDynamicIndexScreen() {
    assertScreenHTML("assertAnonymousCustomerDynamicIndexScreen", "56acc98a-f632-4391-8660-c65022f9f14c")
}

suspend fun assertAnonymousWriterStaticIndexScreen() {
    assertScreenHTML("assertAnonymousWriterStaticIndexScreen", "f90c390a-aaf3-4889-9ca0-4c73b9a3d848")
}

suspend fun assertAnonymousWriterDynamicIndexScreen() {
    assertScreenHTML("assertAnonymousWriterDynamicIndexScreen", "ab66c1e0-5509-465b-bfdc-c96c3bfe88c4")
}


inline fun fiddlingWithGlobals(block: () -> Unit) {
    val old_overriddenClientKind = TestGlobal.overriddenClientKind
    try {
        block()
    } finally {
        TestGlobal.overriddenClientKind = old_overriddenClientKind
    }

//    val old_CLIENT_KIND = global.CLIENT_KIND
//    try {
//        block()
//    } finally {
//        global.CLIENT_KIND = old_CLIENT_KIND
//    }
}

class Snapshot(
    val name: String,
    val id: String
)

class TestShit {
    enum class Pauses {NONE, ALL, ONLY}

    var bobulID by notNull<String>()
    var bobulToken by notNull<String>()
    var fedorID by notNull<String>()
    var fedorToken by notNull<String>()
    var dasjaID by notNull<String>()
    var dasjaToken by notNull<String>()
    var nextRequestTimestampIndex = 0
    var pauses = Pauses.NONE
    val snapshots = mutableListOf<Snapshot>()
    var nextInstructionIndex by notNull<Int>()

    val _timestamps by lazy{async{
        // GenerateTimestamps.kt
        val list = mirandaGetGeneratedTestTimestamps()
//        for (i in 1..list.lastIndex) {
//            check(list[i - 1] < list[i]) {"Stamp ${i - 1} (${list[i - 1]}) is not before $i (${list[i]})"}
//        }
        list
    }}

    suspend fun timestamps(): List<String> {
        return await(_timestamps)
    }

    suspend fun imposeNextRequestTimestamp_killme() {
        // Should be automatic
    }

    suspend fun imposeNextRequestTimestamp() {
        if (nextRequestTimestampIndex > timestamps().lastIndex) bitch("Out of fucking timestamps")
        val stamp = timestamps()[nextRequestTimestampIndex++]
        clog("Imposing timestamp: $stamp")
        mirandaImposeNextRequestTimestamp(stamp)
    }

}

fun TestScenarioBuilder.setUpBobul(testShit: TestShit) {
    imf("Reimplement setUpBobul")
//    acta {async{
//        measureAndReportToDocumentElement("Preparing customer: Ivo Bobul") {
//            await(ImposeNextGeneratedPasswordRequest.send("bobul-secret"))
//
//            fiddlingWithGlobals {
//                TestGlobal.overriddenClientKind = ClientKind.UA_CUSTOMER
//
//                testShit.bobulID = await(send(null, SignUpRequest()-{o->
//                    o.agreeTerms.value = true
//                    o.immutableSignUpFields-{o->
//                        o.email.value = "bobul@test.shit.ua"
//                    }
//                    o.mutableSignUpFields-{o->
//                        o.firstName.value = "Иво"
//                        o.lastName.value = "Бобул"
//                    }
//                })).orDie().userID
//
//                testShit.bobulToken = await(sendSafe(null, SignInWithPasswordRequest()-{o->
//                    o.email.value = "bobul@test.shit.ua"
//                    o.password.value = "bobul-secret"
//                })).orDie().token
//
////                await(send(TestSetUserFieldsRequest()-{o->
////                    o.email.value = "bobul@test.shit.ua"
////                }))
//            }
//        }
//    }}
}

fun TestScenarioBuilder.setUpFedor(testShit: TestShit) {
    imf("Reimplement setUpFedor")
//    acta {async{
//        measureAndReportToDocumentElement("Preparing writer: Fedor Dostoevsky") {
//            await(ImposeNextGeneratedPasswordRequest.send("fedor-secret"))
//
//            fiddlingWithGlobals {
//                TestGlobal.overriddenClientKind = ClientKind.UA_WRITER
//
//                testShit.fedorID = await(send(null, SignUpRequest()-{o->
//                    o.agreeTerms.value = true
//                    o.immutableSignUpFields-{o->
//                        o.email.value = "fedor@test.shit.ua"
//                    }
//                    o.mutableSignUpFields-{o->
//                        o.firstName.value = "Федор"
//                        o.lastName.value = "Достоевский"
//                    }
//                })).orDie().userID
//
//                await(send(TestSetUserFieldsRequest()-{o->
//                    o.email.value = "fedor@test.shit.ua"
//                    o.state.value = UserState.COOL
//                }))
//
//                testShit.fedorToken = await(sendSafe(null, SignInWithPasswordRequest()-{o->
//                    o.email.value = "fedor@test.shit.ua"
//                    o.password.value = "fedor-secret"
//                })).orDie().token
//            }
//        }
//    }}
}

fun TestScenarioBuilder.setUpDasja(testShit: TestShit) {
    imf("Reimplement setUpDasja")
//    acta {async{
//        measureAndReportToDocumentElement("Preparing admin: Dasja") {
//            await(ImposeNextGeneratedPasswordRequest.send("fedor-secret"))
//
//            fiddlingWithGlobals {
//                TestGlobal.overriddenClientKind = ClientKind.UA_WRITER
//
//                testShit.dasjaID = "100000"
//
//                testShit.dasjaToken = await(sendSafe(null, SignInWithPasswordRequest()-{o->
//                    o.email.value = "dasja@test.shit.ua"
//                    o.password.value = "dasjasecret"
//                })).orDie().token
//            }
//        }
//    }}
}

//suspend fun imposeNextGeneratedPassword2(password: String) =
//    send(ImposeNextGeneratedPasswordRequest()-{o->
//        o.password.value = password
//    })

suspend fun imposeNextGeneratedConfirmationSecret(secret: String) =
    mirandaImposeNextGeneratedConfirmationSecret(secret)

suspend fun imposeNextRequestGenericError() =
    mirandaImposeNextRequestError()

fun setUpFilesByBobul_1(testShit: TestShit, orderID: String) = async<Unit> {
    imf("Reimplement setUpFilesByBobul_1")
//    await(testShit.imposeNextRequestTimestamp())
//    await(send(testShit.bobulToken, CustomerAddUAOrderFileRequest()-{o->
//        o.orderID.value = orderID
//        o.file.content = FileField.Content.TestFileOnServer("fuck you.rtf")
//        o.title.value = "A warm word to my writer"
//        o.details.value = dedent("""
//            Я к вам пишу – чего же боле?
//            Что я могу еще сказать?
//            Теперь, я знаю, в вашей воле
//            Меня презреньем наказать.
//        """)
//    })).orDie()
//
//    await(testShit.imposeNextRequestTimestamp())
//    await(send(testShit.bobulToken, CustomerAddUAOrderFileRequest()-{o->
//        o.orderID.value = orderID
//        o.file.content = FileField.Content.TestFileOnServer("crazy monster boobs.rtf")
//        o.title.value = "Cool stuff"
//        o.details.value = dedent("""
//             - Прокурор Гастерер - мой давний друг,- сказал он. - Можно мне позвонить ему?
//             - Конечно, - ответил инспектор,- но я  не  знаю,  какой  в этом  смысл,  разве  что вам надо переговорить с ним по личному делу.
//             - Какой смысл? -  воскликнул  К.  скорее  озадаченно,  чем сердито.  Да  кто  вы  такой?  Ищете  смысл,  а  творите  такую бессмыслицу, что и не придумаешь. Да тут камни возопят! Сначала эти господа на меня напали, а теперь расселись, стоят и глазеют всем скопом, как я пляшу под вашу  дудку.  И  еще  спрашиваете, какой  смысл  звонить  прокурору,  когда  мне  сказано,  что  я арестован! Хорошо, я не буду звонить!
//        """)
//    })).orDie()
//
//    await(testShit.imposeNextRequestTimestamp())
//    await(send(testShit.bobulToken, CustomerAddUAOrderFileRequest()-{o->
//        o.orderID.value = orderID
//        o.file.content = FileField.Content.TestFileOnServer("the trial.doc")
//        o.title.value = "Процесс by Кафка"
//        o.details.value = dedent("""
//            Это чисто на почитать...
//        """)
//    })).orDie()
//
////    await(fuckingRemoteCall.executeSQL("Add file permissions", """
////        insert into file_user_permissions(file_id, user_id) values(100000, 100000);
////        insert into file_user_permissions(file_id, user_id) values(100001, 100000);
////        insert into file_user_permissions(file_id, user_id) values(100002, 100000);
////    """))
}


//fun setUpFilesByFedor_1(testShit: TestShit, orderID: Long) = async<Unit> {
//    await(testAddFileByFedorAndApproveForBobul(testShit, UACreateOrderFileRequest()-{o->
//        o.orderID.value = orderID
//        o.file.content = FileField.Content.TestFileOnServer("idiot.rtf")
//        o.title.value = "The (Fucking) Idiot"
//        o.details.value = dedent("""
//            26-летний князь Лев Николаевич Мышкин возвращается из санатория в Швейцарии, где провёл несколько лет, лечась от эпилепсии. Князь предстаёт человеком искренним и невинным, хотя и прилично разбирающимся в отношениях между людьми. Он едет в Россию к единственным оставшимся у него родственникам — семье Епанчиных. В поезде он знакомится с молодым купцом Парфёном Рогожиным и отставным чиновником Лебедевым, которым бесхитростно рассказывает свою историю. В ответ он узнаёт подробности жизни Рогожина, который влюблён в Настасью Филипповну, бывшую содержанку богатого дворянина Афанасия Ивановича Тоцкого.
//
//            В доме Епанчиных выясняется, что Настасья Филипповна там хорошо известна. Есть план выдать её за протеже генерала Епанчина Гаврилу Ардалионовича Иволгина, человека амбициозного, но посредственного. Князь Мышкин знакомится со всеми основными персонажами повествования. Это дочери Епанчиных — Александра, Аделаида и Аглая, на которых он производит благоприятное впечатление, оставаясь объектом их немного насмешливого внимания. Это генеральша Лизавета Прокофьевна Епанчина, которая находится в постоянном волнении из-за того, что её муж общается с Настасьей Филипповной, имеющей репутацию падшей. Это Ганя Иволгин, который очень страдает из-за предстоящей ему роли мужа Настасьи Филипповны, хотя ради денег готов на всё, и не может решиться на развитие своих пока очень слабых отношений с Аглаей. Князь Мышкин довольно простодушно рассказывает генеральше и сёстрам Епанчиным о том, что он узнал о Настасье Филипповне от Рогожина, а также поражает их своим повествованием о воспоминаниях и чувствах своего знакомого, который был приговорён к смертной казни, но в последний момент был помилован.
//        """)
//    }))
//
//    await(testAddFileByFedorAndApproveForBobul(testShit, UAWriterCreateOrderFileRequest()-{o->
//        o.orderID.value = orderID
//        o.file.content = FileField.Content.TestFileOnServer("crime and punishment.rtf")
//        o.title.value = "Crime and Punishment"
//        o.details.value = dedent("""
//            Действие романа начинается жарким июльским днём в Петербурге. Студент Родион Романович Раскольников, вынужденный уйти из университета из-за отсутствия денег, направляется в квартиру к процентщице Алёне Ивановне, чтобы сделать «пробу своему предприятию». В сознании героя в течение последнего месяца созревает идея убийства «гадкой старушонки»; одно-единственное преступление, по мнению Раскольникова, изменит его собственную жизнь и избавит сестру Дуню от необходимости выходить замуж за «благодетеля» Петра Петровича Лужина. Несмотря на проведённую «разведку», тщательно продуманный план ломается из-за внутренней паники Родиона Романовича (который после убийства процентщицы долго не может найти у неё ни денег, ни ценных закладов), а также внезапного возвращения домой сестры Алёны Ивановны. Тихая, безобидная, «поминутно беременная» Лизавета, оказавшаяся невольной свидетельницей преступления, становится второй жертвой студента.
//        """)
//    }))
//}

//private fun testAddFileByFedorAndApproveForBobul(testShit: TestShit, req: UAWriterCreateOrderFileRequest): Promisoid<Unit> {
//    imf("Reimplement testAddFileByFedorAndApproveForBobul")
////    return async<Unit> {
////        await(testShit.imposeNextRequestTimestamp())
////        val res = await(send(testShit.fedorToken, req)).orDie()
////        await(testShit.imposeNextRequestTimestamp())
////        await(testCopyFileToBobul(testShit, res.id))
////    }
//}

private fun testCopyFileToBobul(testShit: TestShit, orderFileID: String): Promisoid<FormResponse2<TestCopyOrderFileToAreaRequest.Response>> {
    return send(testShit.dasjaToken, TestCopyOrderFileToAreaRequest()-{o->
        o.orderFileID.value = orderFileID
        o.areaName.value = const.orderArea.customer
        o.permissionForUserID.value = testShit.bobulID
    })
}

//fun setUpFilesByFedor_2(testShit: TestShit, orderID: Long) = async<Unit> {
//    await(testAddFileByFedorAndApproveForBobul(testShit, UAWriterCreateOrderFileRequest()-{o->
//        o.orderID.value = orderID
//        o.file.content = FileField.Content.TestFileOnServer("crappy release 1.rtf")
//        o.title.value = "Crappy release 1"
//        o.details.value = dedent("""
//            К. прошел дальше по улочке медленным шагом, будто  у  него времени   сколько   угодно;   если  следователь  видит  его  из какого-нибудь окна, значит он знает, что К. явился. Только  что пробило девять. Дом оказался довольно далеко, он был необычайно длинный;   особенно   ворота  были  очень  высокие  и  широкие. Очевидно, они предназначались для фургонов,  развозивших  товар по  разным складам. Сейчас все склады во дворе были заперты, но по вывескам К. узнал некоторые фирмы -  его  банк  вел  с  ними дела.  Вопреки  своему  обыкновению  он  пристально разглядывал окружающее, даже остановился у входа  во  двор.  Неподалеку  на ящике  сидел  босоногий  человек и читал газету. Двое мальчишек качались на тачке.  У  колонки  стояла  болезненная  девушка  в ночной  кофточке,  и,  пока  вода  набиралась  в кувшин, она не сводила глаз с К. В углу двора между  двумя  окнами  натягивали веревку,  на  ней  уже  висело  выстиранное  белье. Внизу стоял человек и, покрикивая, руководил работой.
//        """)
//    }))
//
//    await(testAddFileByFedorAndApproveForBobul(testShit, UAWriterCreateOrderFileRequest()-{o->
//        o.orderID.value = orderID
//        o.file.content = FileField.Content.TestFileOnServer("crappy release 2.rtf")
//        o.title.value = "Crappy release 2"
//        o.details.value = dedent("""
//            К. пошел  было  к  лестнице,  чтобы  подняться  в  кабинет следователя,  но  остановился:  кроме этой лестницы, со двора в дом было еще три входа, а в глубине  двора  виднелся  неширокий проход  во  второй  двор.  К.  рассердился,  оттого  что ему не указали точнее, где этот кабинет; все-таки к нему  отнеслись  с удивительным  невниманием и равнодушием, и он решил, что заявит об этом громко и отчетливо.  Наконец  он  все  же  поднялся  по лестнице,   мысленно  повторяя  выражение  Виллема,  одного  из стражей, что вина сама притягивает к себе правосудие, из  чего, собственно  говоря,  вытекало,  что  кабинет следователя должен находиться именно на той лестнице, куда случайно поднялся К.
//        """)
//    }))
//
//    await(testAddFileByFedorAndApproveForBobul(testShit, UAWriterCreateOrderFileRequest()-{o->
//        o.orderID.value = orderID
//        o.file.content = FileField.Content.TestFileOnServer("crappy release 3.rtf")
//        o.title.value = "Crappy release 3"
//        o.details.value = dedent("""
//            Подымаясь по лестнице, он все время мешал детям,  игравшим там,  и  они  провожали его злыми взглядами. В другой раз, если придется  сюда  идти,  надо  будет  взять  либо  конфет,  чтобы подкупить их, либо палку, чтобы их отколотить, сказал он себе.У второго этажа ему даже пришлось переждать, пока мячик докатится донизу:  двое  мальчишек с хитроватыми лицами взрослых бандитов вцепились в его брюки; стряхнуть их можно было только силой, но К. боялся, что они завопят, если им сделать больно.
//        """)
//    }))
//}

fun setUpFilesByBobul_2(testShit: TestShit, orderID: String) = async<Unit> {
    imf("Reimplement setUpFilesByBobul_2")
//    await(testShit.imposeNextRequestTimestamp())
//    await(send(testShit.bobulToken, CustomerAddUAOrderFileRequest()-{o->
//        o.orderID.value = orderID
//        o.file.content = FileField.Content.TestFileOnServer("piece of trial 1.rtf")
//        o.title.value = "A piece of... Trial 1"
//        o.details.value = dedent("""
//             - Вы  глубоко заблуждаетесь, - сказал К. сердито, с трудом скрывая раздражение, - и  вообще  вы  неверно  истолковали  мои слова  про  барышню,  я  совсем  не  то хотел сказать. Искренне советую вам ничего ей не говорить. Вы глубоко заблуждаетесь,  я ее  знаю  очень  хорошо,  и  все,  что  вы  говорите, неправда!
//        """)
//    })).orDie()
//
//    await(testShit.imposeNextRequestTimestamp())
//    await(send(testShit.bobulToken, CustomerAddUAOrderFileRequest()-{o->
//        o.orderID.value = orderID
//        o.file.content = FileField.Content.TestFileOnServer("piece of trial 2.rtf")
//        o.title.value = "A piece of... Trial 2"
//        o.details.value = dedent("""
//             Когда  ему  надоело смотреть на пустую улицу, он прилег на кушетку, но сначала  приоткрыл  дверь  в  прихожую,  чтобы,  не вставая,   видеть   всех,  кто  войдет  в  квартиру.  Часов  до одиннадцати он пролежал спокойно на кушетке, покуривая  сигару. Но  потом  не выдержал и вышел в прихожую, как будто этим можно было ускорить приход фройляйн Бюрстнер. У него не было  никакой охоты  ее  видеть,  он  даже  не  мог  точно вспомнить, как она выглядит, но ему нужно было с ней поговорить, и его  раздражало что из-за ее опоздания даже конец дня вышел такой беспокойный и беспорядочный.  Виновата она была и в том, что он не поужинал и пропустил визит к Эльзе, назначенный на сегодня. Конечно, можно было  бы  наверстать  упущенное  и  пойти  в  ресторанчик,  где работала  Эльза.  Он  решил,  что  после  разговора  с фройляйн Бюрстнер он так и сделает.
//
//             Уже  пробило  половину  двенадцатого,  когда  на  лестнице раздались  чьи-то шаги. К. так ушел в свои мысли, что с громким топотом расхаживал по прихожей, как по своей комнате, но тут он торопливо нырнул к себе. В прихожую  вошла  фройляйн  Бюрстнер. Заперев  дверь,  она зябко закутала узкие плечи шелковой шалью. Еще миг, и она  скроется  в  своей  комнате,  куда  К.  в  этот полуночный час, разумеется, войти не мог. Значит, ему надо было заговорить с ней сразу; но, к несчастью, он забыл зажечь свет у себя  в комнате, и, если бы он сейчас вышел оттуда, из темноты, это походило бы на нападение. Во всяком случае,  он  мог  очень напугать   ее.
//        """)
//    })).orDie()
//
//    await(testShit.imposeNextRequestTimestamp())
//    await(send(testShit.bobulToken, CustomerAddUAOrderFileRequest()-{o->
//        o.orderID.value = orderID
//        o.file.content = FileField.Content.TestFileOnServer("piece of trial 3.rtf")
//        o.title.value = "A piece of... Trial 3"
//        o.details.value = dedent("""
//             - Вы  не  подумали  об одном, - сказал он. - Правда, у вас могут быть неприятности, но никакая опасность вам не грозит. Вы знаете, что фрау Грубах - а в этом вопросе она играет  решающую роль,  поскольку капитан доводится ей племянником, - вы знаете, что она меня просто  обожает  и  беспрекословно  верит  каждому моему  слову.  Кстати,  она  и зависит от меня, я ей дал в долг порядочную сумму денег. Я готов принять любое предложенное вами объяснение нашей поздней встречи, если только  оно  будет  хоть немного  правдоподобно, и обязуюсь подействовать на фрау Грубах так, чтобы она не только приняла его официально, но и  поверила безоговорочно  и  искренне.  И пожалуйста, не щадите меня. Если вам угодно распространить слух, что я к  вам  приставал,  то  я именно  так и сообщу фрау Грубах, и она все примет, не теряя ко мне уважения, настолько она меня ценит.
//        """)
//    })).orDie()
//
//    await(testShit.imposeNextRequestTimestamp())
//    await(send(testShit.bobulToken, CustomerAddUAOrderFileRequest()-{o->
//        o.orderID.value = orderID
//        o.file.content = FileField.Content.TestFileOnServer("piece of trial 4.rtf")
//        o.title.value = "A piece of... Trial 4"
//        o.details.value = dedent("""
//                 К. сообщили по  телефону,  что  на  воскресенье  назначено первое  предварительное следствие по его делу. Ему сказали, что его будут вызывать  на  следствия  регулярно;  может  быть,  не каждую  неделю,  но все же довольно часто. С одной стороны, все заинтересованы как  можно  быстрее  закончить  процесс,  но,  с другой  стороны,  следствие  должно  вестись  со всей возможной тщательностью; однако ввиду напряжения, которого  оно  требует, допросы  не  должны  слишком  затягиваться.  Вот почему избрана процедура коротких, часто следующих друг  за  другом  допросов. Воскресный  день  назначен  для  допросов  ради  того, чтобы не нарушать  служебные  обязанности  К.  Предполагается,  что   он согласен  с  намеченной  процедурой,  в  противном  случае ему, поелику  возможно,  постараются  пойти   навстречу.   Например, допросы можно было бы проводить и ночью, но, вероятно, по ночам у  К.  не  совсем  свежая  голова. Во всяком случае, если К. не возражает, решено пока что придерживаться воскресного дня. Само собой понятно,  что  явка  для  него  обязательна,  об  этом  и напоминать  ему  не  стоит.  Был  назван  номер  дома, куда ему следовало  явиться;  дом  находился  на  отдаленной   улице   в предместье, где К. еще никогда не бывал.
//        """)
//    })).orDie()
//
////    await(fuckingRemoteCall.executeSQL("Add file permissions", """
////        insert into file_user_permissions(file_id, user_id) values(100005, 100000);
////        insert into file_user_permissions(file_id, user_id) values(100006, 100000);
////        insert into file_user_permissions(file_id, user_id) values(100007, 100000);
////        insert into file_user_permissions(file_id, user_id) values(100008, 100000);
////    """))
}

fun TestScenarioBuilder.setUpBobulOrder(testShit: TestShit, setUpFiles: (String) -> Promisoid<Unit>) {
    imf("Reimplement setUpBobulOrder")
//    val o = this
//    o.acta {async{
//        measureAndReportToDocumentElement("Preparing some orders for Ivo Bobul") {
//            fiddlingWithGlobals {
//                TestGlobal.overriddenClientKind = ClientKind.UA_CUSTOMER
//
//                await(testShit.imposeNextRequestTimestamp())
//                val xxx by lazy {imf("xlobal? No-no-no, fuck you...")}
//                val createOrderResponse = await(send(testShit.bobulToken, UACustomerCreateOrderRequest(xxx)-{o->
//                    o.documentTitle.value = "Когнитивно-прагматические аспекты перевода рекламных слоганов с английского"
//                    o.documentType.value = UADocumentType.COURSE
////                    o.deadline.killmeValue = moment("2016-12-11 18:15:00").valueOf()
//                    o.numPages.setValue(30)
//                    o.numSources.setValue(5)
//                    o.documentDetails.value = "В статье рассматривается проблема перевода корпоративных слоганов коммерческой рекламы, оказывающих воздействие на сознание аудитории. Изучаются процессы наделения объектов рекламирования дополнительным символическим содержанием для осуществления имиджевой коммуникации. Наличие конкретной прагматической цели обуславливает широкое использование средств языковой выразительности на всех уровнях организации рекламного текста, создавая необходимость в поиске адекватных способов перевода рекламных посланий. В работе определяются доминанты перевода рекламного текста, предлагаются методы перевода англоязычных слоганов автомобильных компаний для русскоязычной аудитории."
//                }))
//                createOrderResponse as FormResponse2.Hunky
//                val orderID = createOrderResponse.meat.id
//
//                await(setUpFiles(orderID))
//            }
//        }
//    }}
}


fun TestScenarioBuilder.todo(msg: String) {
    TestGlobal.hasScenarioTODOs = true
    val step = TestInstruction.Step.CustomStep(
        msg,
        rowBackgroundColor = Color.DEEP_ORANGE_100,
        label = "TODO",
        labelBackgroundColor = Color.DEEP_ORANGE_500,
        labelColor = Color.WHITE)
    step.passed = true
    instructions.add(step)
}

fun TestScenarioBuilder.assertScreenHTML_todo(descr: String?, id: String) {
    this.todo("assertScreenHTML: $descr")
}

fun TestScenarioBuilder.expectPieceOfShitDownload(expected: PieceOfShitDownload, buildStepsToDownload: () -> Unit) {
    val o = this
    o.acta {fuckingRemoteCall.resetLastDownloadedFile()}

    buildStepsToDownload()

    o.acta("Waiting for file download: `${expected.name}` (`${expected.id}`)") {
        async {
            for (i in 1..3) {
                await(delay(500))
                await(fuckingRemoteCall.getLastDownloadedPieceOfShit())?.let {actual->
                    assertEquals(expected, actual)
                    return@async
                }
            }
            die("Sick of waiting for file download")
        }
    }
}

fun TestScenarioBuilder.pause(shit: TestShit, descr: String = "Cool shit, huh?..", only: Boolean = false) {
    acta {
        Promisoid<Unit> {resolve, _ ->
        val shouldPause = when (shit.pauses) {
            TestShit.Pauses.NONE -> false
            TestShit.Pauses.ALL -> true
            TestShit.Pauses.ONLY -> only
        }

        if (shouldPause) {
            val paneName = "TestScenarioBuilder.pause"

            val onContinue = {
                old_debugPanes.remove(paneName)
                resolve(Unit)
            }

            old_debugPanes.put(paneName, kdiv(className = css.test.popup.pause){o->
                o- descr
                o- Button(icon = fa.play, style = Style(marginLeft = "1rem"), onClick = onContinue)
            })

            window.addEventLis("keydown", selfy {lis-> {
                window.removeEventListener("keydown", lis())
                onContinue()
            }})
        } else {
            resolve(Unit)
        }
    }}
}

fun TestScenarioBuilder.genericRequestSequence(
    buildAction: () -> Unit,
    beforeResponse: () -> Unit,
    afterResponse: () -> Unit,
    responseTimeout: Int = fconst.test.defaultResponseTimeout
) {
    val o = this
    o.act {TestGlobal.requestPause = ResolvableShit<Unit>()}
    buildAction()
    beforeResponse()

    o.act {TestGlobal.responseProcessedSignal = ResolvableShit<Unit>()}
    o.act {TestGlobal.requestPause!!.resolve(Unit)}
    o.await {TestGlobal.responseProcessedSignal.promise.orTestTimeout(responseTimeout)}
    afterResponse()
}

fun TestScenarioBuilder.requestSequence(
    buildAction: () -> Unit,
    assertionDescr: String,
    halfwayAssertionID: String,
    finalAssertionID: String,
    responseTimeout: Int = fconst.test.defaultResponseTimeout,
    halfwayOpts: AssertScreenOpts = AssertScreenOpts(),
    finalOpts: AssertScreenOpts = AssertScreenOpts()
) {
    imf("reimplement requestSequence")
//    val o = this
//    o.genericRequestSequence(
//        buildAction = buildAction,
//        beforeResponse = {
//            o.assertScreenHTML("$assertionDescr (halfway)", halfwayAssertionID, halfwayOpts)
//        },
//        afterResponse = {o.assertScreenHTML(assertionDescr, finalAssertionID, finalOpts)},
//        responseTimeout = responseTimeout
//    )
}

fun TestScenarioBuilder.requestSequenceNoFinalAssertion(
    buildAction: () -> Unit,
    assertionDescr: String,
    halfwayAssertionID: String,
    responseTimeout: Int = fconst.test.defaultResponseTimeout,
    halfwayOpts: AssertScreenOpts = AssertScreenOpts()
) {
    imf("reimplement requestSequenceNoFinalAssertion")
//    val o = this
//    o.genericRequestSequence(
//        buildAction = buildAction,
//        beforeResponse = {
//            o.assertScreenHTML("$assertionDescr (halfway)", halfwayAssertionID, halfwayOpts)
//        },
//        afterResponse = {},
//        responseTimeout = responseTimeout
//    )
}

fun TestScenarioBuilder.boot(worldName: String, aid: String) {
    imf("reimplement TestScenarioBuilder.boot")
//    acta {async{
//        val world = World(worldName)
//        await(world.boot())
//    }}
//    assertScreenHTML("Boot: $worldName", aid)
}







