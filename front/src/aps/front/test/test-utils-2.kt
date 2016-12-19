package aps.front.testutils

import aps.*
import aps.front.*
import into.kommon.*
import into.mochka.assertEquals
import org.w3c.dom.Storage
import kotlin.browser.document
import kotlin.browser.window

object testconst {
    val filesRoot = "C:\\Users\\Vladimir\\Desktop\\apstestdocs\\"

    object url {
        val customer = "http://aps-ua-customer.local:3012"
    }
}


fun TestScenarioBuilder.initFuckingBrowser(fillStorageLocal: (TypedStorageLocal) -> Unit = {}) {
    act {
        val fakeStorageLocal = object:StorageLocal {
            val map = mutableMapOf<String, String>()
            override fun clear() = map.clear()
            override fun getItem(key: String) = map[key]
            override fun setItem(key: String, value: String) {map[key] = value}
            override fun removeItem(key: String) {map.remove(key)}
        }

        val tsl = TypedStorageLocal(fakeStorageLocal)
        fillStorageLocal(tsl)
        Globus.browser = Browser(
            typedStorageLocal = tsl
        )

        ExternalGlobus.storageLocalForStaticContent = object:Storage {
            override fun getItem(key: String) = fakeStorageLocal.getItem(key)
        }
    }
}

fun TestScenarioBuilder.kindaNavigateToStaticContent(url: String) {
    acta {async{
        val content = measureAndReportToDocumentElement("Loading $url") {
            await(fetchURL(url, "GET", null))
        }

        window.history.pushState(null, "", url)
        val openingHeadTagIndex = content.indexOfOrDie("<head")
        val closingHTMLTagIndex = content.indexOfOrDie("</html>")
        val innerHTMLContent = content.substring(openingHeadTagIndex, closingHTMLTagIndex)
        docInnerHTML = innerHTMLContent

        ExternalGlobus.displayInitialShit()
    }}
}

inline fun <T> measureAndReportToDocumentElement(name: String, block: () -> T): T {
    docInnerHTML += "$name...."
    val res = measure(name, block)
    docInnerHTML += " DONE<br>"
    return res
}

var docInnerHTML: String
    get() = document.documentElement!!.innerHTML
    set(value) {document.documentElement!!.innerHTML = value}

fun TestScenarioBuilder.assertCustomerIndexScreen() {
    assertNavbarHTMLExt("Customer index", "11786d8d-6681-4579-a90b-7f602a59dd2d")
    assertRootHTMLExt("Customer index", "9a386880-c709-4cbc-a97c-41bb5b559a36")
}

fun TestScenarioBuilder.assertCustomerSignInScreen() {
    assertScreenHTML("Customer sign-in screen", "38dcedaf-5aad-43e8-ac3b-685b681fb75c")
}

fun TestScenarioBuilder.assertCustomerSignUpScreen() {
    assertScreenHTML("Customer sign-up screen", "1de9ffb2-3215-42e0-a67c-c9c2856880b8")
}

fun TestScenarioBuilder.checkAssertAndClearEmail(descr: String, expectedTo: String, expectedSubject: String, expectedBody: String) {
    acta {debugCheckEmail()}
    assertMailInFooter(descr, expectedTo, expectedSubject, expectedBody)
    acta {ClearSentEmailsRequest.send()}
    act {debugHideMailbox()}
}

fun TestScenarioBuilder.imposeNextRequestTimestampUTC(stamp: String) {
    acta {ImposeNextRequestTimestampRequest.send(stamp)}
}

fun TestScenarioBuilder.imposeNextGeneratedPassword(password: String) {
    acta {ImposeNextGeneratedPasswordRequest.send(password)}
}

fun TestScenarioBuilder.assertFreshCustomerDashboardScreen() {
    assertScreenHTML("Fresh customer's dashboard screen", "39ffecee-5b3f-4bf0-b9c6-43256a58a663")
}

fun TestScenarioBuilder.assertCustomerBreatheScreen() {
    assertScreenHTML("Customer breathe screen", "5c58a466-1225-444a-abde-6d11def5c00c")
}

inline fun fiddlingWithGlobals(block: () -> Unit) {
    val old_CLIENT_KIND = global.CLIENT_KIND
    try {
        block()
    } finally {
        global.CLIENT_KIND = old_CLIENT_KIND
    }
}

class TestShit {
    lateinit var bobulToken: String
    lateinit var fedorToken: String
}

fun TestScenarioBuilder.setUpBobul(testShit: TestShit) {
    acta {async{
        measureAndReportToDocumentElement("Preparing customer: Ivo Bobul") {
            await(ImposeNextGeneratedPasswordRequest.send("bobul-secret"))

            fiddlingWithGlobals {
                global.CLIENT_KIND = ClientKind.CUSTOMER.name

                await(send(null, SignUpRequest()-{o->
                    o.agreeTerms.value = true
                    o.immutableSignUpFields-{o->
                        o.email.value = "bobul@test.shit.ua"
                    }
                    o.mutableSignUpFields-{o->
                        o.firstName.value = "Иво"
                        o.lastName.value = "Бобул"
                    }
                })).orDie()

                testShit.bobulToken = await(sendSafe(null, SignInWithPasswordRequest()-{o->
                    o.email.value = "bobul@test.shit.ua"
                    o.password.value = "bobul-secret"
                })).orDie().token

//                await(send(TestSetUserFieldsRequest()-{o->
//                    o.email.value = "bobul@test.shit.ua"
//                }))
            }
        }
    }}
}

fun TestScenarioBuilder.setUpFedor(testShit: TestShit) {
    acta {async{
        measureAndReportToDocumentElement("Preparing writer: Fedor Dostoevsky") {
            await(ImposeNextGeneratedPasswordRequest.send("fedor-secret"))

            fiddlingWithGlobals {
                global.CLIENT_KIND = ClientKind.WRITER.name

                await(send(null, SignUpRequest()-{o->
                    o.agreeTerms.value = true
                    o.immutableSignUpFields-{o->
                        o.email.value = "fedor@test.shit.ua"
                    }
                    o.mutableSignUpFields-{o->
                        o.firstName.value = "Федор"
                        o.lastName.value = "Достоевский"
                    }
                })).orDie()

                await(send(TestSetUserFieldsRequest()-{o->
                    o.email.value = "fedor@test.shit.ua"
                    o.state.value = UserState.COOL
                }))

                testShit.fedorToken = await(sendSafe(null, SignInWithPasswordRequest()-{o->
                    o.email.value = "fedor@test.shit.ua"
                    o.password.value = "fedor-secret"
                })).orDie().token
            }
        }
    }}
}

//fun TestScenarioBuilder.setUpBobulOrder(testShit: TestShit, addFiles: () -> Unit) {
//
//}

fun setUpFilesByBobul_1(testShit: TestShit, orderID: String): Promise<Unit> = async {
    await(ImposeNextRequestTimestampRequest.send("2016-12-02 12:31:15"))
    await(send(testShit.bobulToken, CustomerAddUAOrderFileRequest()-{o->
        o.orderID.value = orderID
        o.file.testFileOnServer = FileField.TestFileOnServer("fuck you.rtf", "${testconst.filesRoot}fuck you.rtf")
        o.title.value = "A warm word to my writer"
        o.details.value = dedent("""
            Я к вам пишу – чего же боле?
            Что я могу еще сказать?
            Теперь, я знаю, в вашей воле
            Меня презреньем наказать.
        """)
    })).orDie()

    await(ImposeNextRequestTimestampRequest.send("2016-12-02 12:42:18"))
    await(send(testShit.bobulToken, CustomerAddUAOrderFileRequest()-{o->
        o.orderID.value = orderID
        o.file.testFileOnServer = FileField.TestFileOnServer("crazy monster boobs.rtf", "${testconst.filesRoot}crazy monster boobs.rtf")
        o.title.value = "Cool stuff"
        o.details.value = dedent("""
             - Прокурор Гастерер - мой давний друг,- сказал он. - Можно мне позвонить ему?
             - Конечно, - ответил инспектор,- но я  не  знаю,  какой  в этом  смысл,  разве  что вам надо переговорить с ним по личному делу.
             - Какой смысл? -  воскликнул  К.  скорее  озадаченно,  чем сердито.  Да  кто  вы  такой?  Ищете  смысл,  а  творите  такую бессмыслицу, что и не придумаешь. Да тут камни возопят! Сначала эти господа на меня напали, а теперь расселись, стоят и глазеют всем скопом, как я пляшу под вашу  дудку.  И  еще  спрашиваете, какой  смысл  звонить  прокурору,  когда  мне  сказано,  что  я арестован! Хорошо, я не буду звонить!
        """)
    })).orDie()

    await(ImposeNextRequestTimestampRequest.send("2016-12-02 13:02:25"))
    await(send(testShit.bobulToken, CustomerAddUAOrderFileRequest()-{o->
        o.orderID.value = orderID
        o.file.testFileOnServer = FileField.TestFileOnServer("the trial.doc", "${testconst.filesRoot}the trial.doc")
        o.title.value = "Процесс by Кафка"
        o.details.value = dedent("""
            Это чисто на почитать...
        """)
    })).orDie()

    await(fuckingRemoteCall.executeSQL("Add file permissions", """
        insert into file_user_permissions(file_id, user_id) values(100000, 100000);
        insert into file_user_permissions(file_id, user_id) values(100001, 100000);
        insert into file_user_permissions(file_id, user_id) values(100002, 100000);
    """))
}

fun setUpFilesByFedor_1(testShit: TestShit, orderID: String): Promise<Unit> = async {
    await(ImposeNextRequestTimestampRequest.send("2016-12-05 10:15:42"))
    await(send(testShit.fedorToken, WriterAddUAOrderFileRequest()-{o->
        o.orderID.value = orderID
        o.file.testFileOnServer = FileField.TestFileOnServer("idiot.rtf", "${testconst.filesRoot}idiot.rtf")
        o.title.value = "The (Fucking) Idiot"
        o.details.value = dedent("""
            26-летний князь **Лев Николаевич Мышкин** возвращается из санатория в Швейцарии, где провёл несколько лет, лечась от эпилепсии. Князь предстаёт человеком искренним и невинным, хотя и прилично разбирающимся в отношениях между людьми. Он едет в Россию к единственным оставшимся у него родственникам — семье Епанчиных. В поезде он знакомится с молодым купцом Парфёном Рогожиным и отставным чиновником Лебедевым, которым бесхитростно рассказывает свою историю. В ответ он узнаёт подробности жизни Рогожина, который влюблён в Настасью Филипповну, бывшую содержанку богатого дворянина Афанасия Ивановича Тоцкого.

            В доме Епанчиных выясняется, что **Настасья Филипповна** там хорошо известна. Есть план выдать её за протеже генерала Епанчина Гаврилу Ардалионовича Иволгина, человека амбициозного, но посредственного. Князь Мышкин знакомится со всеми основными персонажами повествования. Это дочери Епанчиных — Александра, Аделаида и Аглая, на которых он производит благоприятное впечатление, оставаясь объектом их немного насмешливого внимания. Это генеральша Лизавета Прокофьевна Епанчина, которая находится в постоянном волнении из-за того, что её муж общается с Настасьей Филипповной, имеющей репутацию падшей. Это Ганя Иволгин, который очень страдает из-за предстоящей ему роли мужа Настасьи Филипповны, хотя ради денег готов на всё, и не может решиться на развитие своих пока очень слабых отношений с Аглаей. Князь Мышкин довольно простодушно рассказывает генеральше и сёстрам Епанчиным о том, что он узнал о Настасье Филипповне от Рогожина, а также поражает их своим повествованием о воспоминаниях и чувствах своего знакомого, который был приговорён к смертной казни, но в последний момент был помилован.
        """)
    })).orDie()

    await(ImposeNextRequestTimestampRequest.send("2016-12-07 14:27:03"))
    await(send(testShit.fedorToken, WriterAddUAOrderFileRequest()-{o->
        o.orderID.value = orderID
        o.file.testFileOnServer = FileField.TestFileOnServer("crime and punishment.rtf", "${testconst.filesRoot}crime and punishment.rtf")
        o.title.value = "Crime and Punishment"
        o.details.value = dedent("""
            Действие романа начинается жарким июльским днём в Петербурге. Студент Родион Романович Раскольников, вынужденный уйти из университета из-за отсутствия денег, направляется в квартиру к процентщице Алёне Ивановне, чтобы сделать «пробу своему предприятию». В сознании героя в течение последнего месяца созревает идея убийства «гадкой старушонки»; одно-единственное преступление, по мнению Раскольникова, изменит его собственную жизнь и избавит сестру Дуню от необходимости выходить замуж за «благодетеля» Петра Петровича Лужина. Несмотря на проведённую «разведку», тщательно продуманный план ломается из-за внутренней паники Родиона Романовича (который после убийства процентщицы долго не может найти у неё ни денег, ни ценных закладов), а также внезапного возвращения домой сестры Алёны Ивановны. Тихая, безобидная, «поминутно беременная» Лизавета, оказавшаяся невольной свидетельницей преступления, становится второй жертвой студента.
        """)
    })).orDie()

    await(fuckingRemoteCall.executeSQL("Add file permissions", """
        insert into file_user_permissions(file_id, user_id) values(100003, 100000);
        insert into file_user_permissions(file_id, user_id) values(100004, 100000);
    """))
}

fun TestScenarioBuilder.setUpBobulOrder(testShit: TestShit, setUpFiles: (String) -> Promise<Unit>) {
    val o = this
    o.acta {async{
        measureAndReportToDocumentElement("Preparing some orders for Ivo Bobul") {
            fiddlingWithGlobals {
                global.CLIENT_KIND = ClientKind.CUSTOMER.name

                await(ImposeNextRequestTimestampRequest.send("2016-12-02 12:24:32"))
                val createOrderResponse = await(send(testShit.bobulToken, CustomerCreateUAOrderRequest()-{o->
                    o.title.value = "Когнитивно-прагматические аспекты перевода рекламных слоганов с английского"
                    o.documentType.value = UADocumentType.COURSE
                    o.deadline.killmeValue = moment("2016-12-11 18:15:00").valueOf()
                    o.numPages.setValue(30)
                    o.numSources.setValue(5)
                    o.details.value = "В статье рассматривается проблема перевода корпоративных слоганов коммерческой рекламы, оказывающих воздействие на сознание аудитории. Изучаются процессы наделения объектов рекламирования дополнительным символическим содержанием для осуществления имиджевой коммуникации. Наличие конкретной прагматической цели обуславливает широкое использование средств языковой выразительности на всех уровнях организации рекламного текста, создавая необходимость в поиске адекватных способов перевода рекламных посланий. В работе определяются доминанты перевода рекламного текста, предлагаются методы перевода англоязычных слоганов автомобильных компаний для русскоязычной аудитории."
                }))
                createOrderResponse as FormResponse2.Hunky
                val orderID = createOrderResponse.meat.id

                await(setUpFiles(orderID))
            }
        }
    }}
}

fun TestScenarioBuilder.setUpOrderAndFiles1(shit: TestShit) {
    val o = this
    o.setUpOrderFilesTestTemplate_1(
        shit,
        setUpUsers = {
            o.setUpBobul(shit)
        },
        setUpOrders = {
            o.setUpBobulOrder(shit, {oid->
                setUpFilesByBobul_1(shit, oid)
            })
        },
        assertScreen = {o.todo("setUpOrderFiles1 assertScreen")})
}

fun TestScenarioBuilder.setUpOrderFilesTestTemplate_1(shit: TestShit, setUpUsers: () -> Unit, setUpOrders: () -> Unit, assertScreen: () -> Unit) {
    val o = this
    setUpUsers()
    setUpOrders()
    o.initFuckingBrowser(fillStorageLocal = {
        it.token = shit.bobulToken
    })
    o.kindaNavigateToStaticContent("${testconst.url.customer}/order.html?id=100000&tab=files")
    o.assertCustomerBreatheScreen()

    o.acta {
        async {
            val world = World("boobs")
            await(world.boot())
        }
    }

    assertScreen()
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

fun TestScenarioBuilder.expectPieceOfShitDownload(expected: PieceOfShitDownload, buildStepsToDownload: () -> Unit) {
    val o = this
    o.acta {fuckingRemoteCall.resetLastDownloadedFile()}

    buildStepsToDownload()

    o.acta("Waiting for file download: `${expected.name}` (`${expected.id}`)") {
        async {
            for (i in 1..3) {
                await(delay(500))
                await(fuckingRemoteCall.getLastDownloadedFileID())?.let {actual->
                    assertEquals(expected, actual)
                    return@async
                }
            }
            die("Sick of waiting for file download")
        }
    }
}













