package aps.front.testutils

import aps.*
import aps.front.*
import into.kommon.*
import into.mochka.assertEquals
import org.w3c.dom.Storage
import kotlin.browser.document
import kotlin.browser.window

object testconst {
    val filesRoot = "C:\\Users\\Vladimir\\Desktop\\"

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
                })).orDie

                testShit.bobulToken = await(sendSafe(null, SignInWithPasswordRequest()-{o->
                    o.email.value = "bobul@test.shit.ua"
                    o.password.value = "bobul-secret"
                })).orDie.token

//                await(send(TestSetUserFieldsRequest()-{o->
//                    o.email.value = "bobul@test.shit.ua"
//                }))
            }
        }
    }}
}

//fun TestScenarioBuilder.setUpBobulOrder(testShit: TestShit, addFiles: () -> Unit) {
//
//}

fun setUpFiles1(testShit: TestShit, orderID: String): Promise<Unit> = async {
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
    }))

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
    }))

    await(ImposeNextRequestTimestampRequest.send("2016-12-02 13:02:25"))
    await(send(testShit.bobulToken, CustomerAddUAOrderFileRequest()-{o->
        o.orderID.value = orderID
        o.file.testFileOnServer = FileField.TestFileOnServer("the trial.doc", "${testconst.filesRoot}the trial.doc")
        o.title.value = "Процесс by Кафка"
        o.details.value = dedent("""
                        Это чисто на почитать...
                    """)
    }))

    await(fuckingRemoteCall.executeSQL("Add file permissions", """
                    insert into file_user_permissions(file_id, user_id) values(100000, 100000);
                    insert into file_user_permissions(file_id, user_id) values(100001, 100000);
                    insert into file_user_permissions(file_id, user_id) values(100002, 100000);
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
    o.setUpOrderFilesTestTemplate(
        shit,
        setUpOrders = {o.setUpBobulOrder(shit, {oid-> setUpFiles1(shit, oid)})},
        assertScreen = {o.todo("setUpOrderFiles1 assertScreen")})
}

fun TestScenarioBuilder.setUpOrderFilesTestTemplate(shit: TestShit, setUpOrders: () -> Unit, assertScreen: () -> Unit) {
    val o = this
    o.setUpBobul(shit)
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













