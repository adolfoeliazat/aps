package aps.front

import aps.*
import aps.front.testutils.*
import into.kommon.*

class Test_UACustomer_NewOrder_HappyPath : StepBasedTestScenario() {
    override fun buildSteps() {
        o.setUpBobul(shit)
        o.initFuckingBrowser(fillTypedStorageLocal = {
            it.token = shit.bobulToken
        })
        o.kindaNavigateToStaticContent("${fconst.test.url.customer}/orders.html")
        o.assertCustomerBreatheScreen()

        o.acta {async{
            val world = World("boobs")
            await(world.boot())
        }}

        o.assertScreenHTML("1", "9d4f20ac-0d7f-494c-bd94-807a4afdd2c5")
        o.buttonClick("plus")
        o.assertScreenHTML("New order form", "44a46713-621c-4a69-b6e6-b09db0965f25")

        o.setValueDescribingStep("TextField-title.Input", "Когнитивно-прагматические аспекты перевода рекламных слоганов с английского")
        o.setValueDescribingStep("SelectField-documentType.Select", UADocumentType.COURSE)
        o.dateTimePickerSetValue("deadline", "15.12.2016 10:30")
        o.setValueDescribingStep("IntField-numPages.Input", 30)
        o.setValueDescribingStep("IntField-numSources.Input", 5)
        o.setValueDescribingStep("TextField-details.Input", "В статье рассматривается проблема перевода корпоративных слоганов коммерческой рекламы, оказывающих воздействие на сознание аудитории. Изучаются процессы наделения объектов рекламирования дополнительным символическим содержанием для осуществления имиджевой коммуникации. Наличие конкретной прагматической цели обуславливает широкое использование средств языковой выразительности на всех уровнях организации рекламного текста, создавая необходимость в поиске адекватных способов перевода рекламных посланий. В работе определяются доминанты перевода рекламного текста, предлагаются методы перевода англоязычных слоганов автомобильных компаний для русскоязычной аудитории.")
        o.imposeNextRequestTimestampUTC("2016-12-10 19:16:40")
        o.clickDescribingStep("button-primary")
        o.assertScreenHTML("Order params tab", "799988d1-e763-4906-a860-7fbddaceff70")

        o.tabsClickOnTab("tabs", "files")
        o.buttonClick("plus")
        o.buttonUserInitiatedClick("upload")
        o.typeIntoOpenFileDialog("${fconst.test.filesRoot}fuck you.rtf")
        imf("fix me")
        // o.fileFieldWaitTillShitChanged("file")
        o.setValueDescribingStep("TextField-title.Input", "A warm word to my writer")
        o.setValueDescribingStep("TextField-details.Input", dedent("""
            Я к вам пишу – чего же боле?
            Что я могу еще сказать?
            Теперь, я знаю, в вашей воле
            Меня презреньем наказать.
        """))
        o.clickDescribingStep("button-primary")
        // TODO:vgrechka Assert fucking screen

        o.buttonClick("plus")
        o.buttonUserInitiatedClick("upload")
        o.typeIntoOpenFileDialog("${fconst.test.filesRoot}crazy monster boobs.rtf")
        imf("fix me")
//        o.fileFieldWaitTillShitChanged("file")
        o.setValueDescribingStep("TextField-title.Input", "Cool stuff")
        o.setValueDescribingStep("TextField-details.Input", dedent("""
             - Прокурор Гастерер - мой давний друг,- сказал он. - Можно мне позвонить ему?
             - Конечно, - ответил инспектор,- но я  не  знаю,  какой  в этом  смысл,  разве  что вам надо переговорить с ним по личному делу.
             - Какой смысл? -  воскликнул  К.  скорее  озадаченно,  чем сердито.  Да  кто  вы  такой?  Ищете  смысл,  а  творите  такую бессмыслицу, что и не придумаешь. Да тут камни возопят! Сначала эти господа на меня напали, а теперь расселись, стоят и глазеют всем скопом, как я пляшу под вашу  дудку.  И  еще  спрашиваете, какой  смысл  звонить  прокурору,  когда  мне  сказано,  что  я арестован! Хорошо, я не буду звонить!
        """))
        o.clickDescribingStep("button-primary")
        // TODO:vgrechka Assert fucking screen

        o.buttonClick("plus")
        o.buttonUserInitiatedClick("upload")
        o.typeIntoOpenFileDialog("${fconst.test.filesRoot}the trial.doc")
        imf("fix me")
        // o.fileFieldWaitTillShitChanged("file")
        o.setValueDescribingStep("TextField-title.Input", "Процесс by Кафка")
        o.setValueDescribingStep("TextField-details.Input", dedent("""
            Это чисто на почитать...
        """))
        o.clickDescribingStep("button-primary")
        // TODO:vgrechka Assert fucking screen
    }
}






