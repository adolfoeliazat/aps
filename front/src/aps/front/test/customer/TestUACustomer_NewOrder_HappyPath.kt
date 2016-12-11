package aps.front

import aps.*
import aps.front.testutils.*
import into.kommon.*

class TestUACustomer_NewOrder_HappyPath : StepBasedTestScenario() {
    val shit = TestShit()

    override fun buildSteps() {
        o.prepareBobul(shit)
        o.initFuckingBrowser(fillStorageLocal = {
            it.token = shit.bobulToken
        })
        o.kindaNavigateToStaticContent("$TEST_URL_CUSTOMER/orders.html")
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
        o.typeIntoOpenFileDialog("C:\\Users\\Vladimir\\Desktop\\fuck you.rtf")
        o.fileFieldWaitTillShitChanged("file")
        o.setValueDescribingStep("TextField-title.Input", "A warm word to my writer")
        o.setValueDescribingStep("TextField-details.Input", dedent("""
            Я к вам пишу – чего же боле?
            Что я могу еще сказать?
            Теперь, я знаю, в вашей воле
            Меня презреньем наказать.
        """))
        o.clickDescribingStep("button-primary")
    }
}






