package aps.front

import aps.front.testutils.*

class TestCustomer_SignUp_HappyPath : StepBasedTestScenario() {
    override fun buildSteps() {
        o.initFuckingBrowser()
        o.kindaNavigateToStaticContent(TEST_URL_CUSTOMER)
        o.acta {async{
            val world = World("boobs")
            await(world.boot())
        }}
        o.assertCustomerIndexScreen()

        o.clickDescribingStep("TopNavItem-sign-in")
        o.assertCustomerSignInScreen()
        o.clickDescribingStep("urlLink-createAccount")
        o.assertCustomerSignUpScreen()

        o.setValueDescribingStep("TextField-email.Input", "bobul@test.shit.ua")
        o.setValueDescribingStep("TextField-firstName.Input", "Иво")
        o.setValueDescribingStep("TextField-lastName.Input", "Бобул")
        o.setValueDescribingStep("AgreeTermsField.Checkbox", true)
        o.imposeNextRequestTimestamp("2016-12-02 12:24:32")
        o.imposeNextGeneratedPassword("secret-big-as-fuck")
        o.clickDescribingStep("button-primary")
        o.assertScreenHTML("Success message and sign-in form", "28d67a3e-25e8-4716-870b-705e746b8809")

        o.checkAssertAndClearEmail(
            "Got email with password",
            "Иво Бобул <bobul@test.shit.ua>",
            "Пароль для APS",
            """
                Привет, Иво!<br><br>
                Вот твой пароль: secret-big-as-fuck
                <br><br>
                <a href="http://aps-ua-customer.local:3012/sign-in.html">http://aps-ua-customer.local:3012/sign-in.html</a>
            """)

        o.setValueDescribingStep("TextField-password.Input", "secret-big-as-fuck")
        o.clickDescribingStep("button-primary")
        o.assertFreshCustomerDashboardScreen()
    }
}




