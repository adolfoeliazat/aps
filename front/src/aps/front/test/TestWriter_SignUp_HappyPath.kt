/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.front.*
import kotlin.browser.*

class TestWriter_SignUp_HappyPath : WriterBootTestScenario(), WriterTestUtils {
    override val path = "/"

    override fun fillStorageLocal() {
    }

    override fun buildStepsAfterDisplayInitialShit() {
        assert_staticHomePage_rightNavbarSignIn(o)
    }

    override fun buildStepsAfterWorldBoot() {
        goto_signUpForm(o)

        o.setValue("TextField-email.Input", "kafka@test.shit.ua")
        o.setValue("TextField-firstName.Input", "Франц")
        o.setValue("TextField-lastName.Input", "Кафка")
        o.setValue("AgreeTermsField.Checkbox", true)
        o.acta {ImposeNextRequestTimestampRequest.send("2016-12-02 12:24:32")}
        o.acta {ImposeNextGeneratedPasswordRequest.send("secret-big-as-fuck")}
        o.click("button-primary")

        o.acta {debugCheckEmail()}
        o.assertMail("""
            Привет, Франц!<br><br>
            Вот твой пароль: secret-big-as-fuck
            <br><br>
            <a href="http://aps-ua-writer.local:3022/sign-in.html">http://aps-ua-writer.local:3022/sign-in.html</a>
        """)
        o.assertRootHTML("""<div data-reactroot="" id="426"><div class="container" id="428" style="position: relative;"><div id="432"><div class="page-header " id="438" style="margin-top: 0px; margin-bottom: 15px;"><h3 id="436" style="margin-bottom: 0px;"><span id="434" class="">Вход</span></h3></div></div><div id="430"><div id="418" style="margin-bottom: 15px;"><i class="fa fa-check" id="414" style="color: rgb(104, 159, 56);"></i><!-- react-text: 113 -->&nbsp;<!-- /react-text --><!-- react-text: 114 -->&nbsp;<!-- /react-text --><span id="416" class="">Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом.</span></div><div id="464"><form class="" id="462"><div class="form-group" id="448"><label id="444"><span id="442" class="">Почта</span></label><div id="446" style="position: relative;"><input type="text" id="420" rows="5" class="form-control" value="" style="resize: none;"></div></div><div class="form-group" id="456"><label id="452"><span id="450" class="">Пароль</span></label><div id="454" style="position: relative;"><input type="password" id="422" rows="5" class="form-control" value="" style="resize: none;"></div></div><div id="460" style="text-align: left;"><button id="458" class="btn btn-primary "><span id="468">Войти</span></button></div></form></div></div></div></div>""")
        o.act {debugHideMailbox()}

        o.setValue("TextField-email.Input", "kafka@test.shit.ua")
        o.setValue("TextField-password.Input", "secret-big-as-fuck")
        o.click("button-primary")

        o.assertRootHTML("""<div data-reactroot="" id="599"><div class="container" id="601" style="position: relative;"><div id="603"><div class="page-header " id="609" style="margin-top: 0px; margin-bottom: 15px;"><h3 id="607" style="margin-bottom: 0px;"><span id="605" class="">Профиль</span></h3></div></div><div id="596"><div id="584" style="width: 720px; margin-bottom: 15px;"><i class="fa fa-exclamation-triangle" id="580" style="color: rgb(255, 111, 0);"></i><!-- react-text: 156 -->&nbsp;<!-- /react-text --><!-- react-text: 157 -->&nbsp;<!-- /react-text --><span id="582" class="">Сначала заполняешь профиль. Админ связывается с тобой и активирует аккаунт. Потом все остальное.</span></div><div id="651"><form class="" id="649"><div class="form-group" id="619"><label id="615"><span id="613" class="">Имя</span></label><div id="617" style="position: relative;"><input type="text" id="586" rows="5" class="form-control" value="Франц" style="resize: none;"></div></div><div class="form-group" id="627"><label id="623"><span id="621" class="">Фамилия</span></label><div id="625" style="position: relative;"><input type="text" id="588" rows="5" class="form-control" value="Кафка" style="resize: none;"></div></div><div class="form-group" id="635"><label id="631"><span id="629" class="">Телефон</span></label><div id="633" style="position: relative;"><input type="text" id="590" rows="5" class="form-control" value="" style="resize: none;"></div></div><div class="form-group" id="643"><label id="639"><span id="637" class="">Пара ласковых о себе</span></label><div id="641" style="position: relative;"><textarea id="592" rows="5" type="text" class="form-control" style="resize: none;"></textarea></div></div><div id="647" style="text-align: left;"><button id="645" class="btn btn-primary "><span id="657">Отправить на проверку</span></button></div></form></div></div></div></div>""")
        o.setValue("TextField-phone.Input", "1234567")
        o.setValue("TextField-aboutMe.Input", "Пишу всякую хуйню за деньги")
        o.acta {ImposeNextRequestTimestampRequest.send("2016-12-02 12:28:32")}
        o.click("button-primary")

        o.assertRootHTML("""<div data-reactroot="" id="883"><div class="container" id="885" style="position: relative;"><div id="887"><div class="page-header " id="893" style="margin-top: 0px; margin-bottom: 15px;"><h3 id="891" style="margin-bottom: 0px;"><span id="889" class="">Профиль</span></h3></div></div><div id="880"><div id="770" style="margin-bottom: 20px;"><i class="fa fa-hourglass-half  " style="color: rgb(255, 111, 0);"></i><!-- react-text: 219 -->&nbsp;<!-- /react-text --><!-- react-text: 220 -->&nbsp;<!-- /react-text --><span id="768" class="">Админ проверяет профиль, жди извещения почтой</span></div><div id="878"><div class="row" id="820"><div class="col-sm-3" id="782"><div class="form-group" id="780"><label id="776"><span id="774" class="">Имя</span></label><div id="778"><span id="772" class="">Франц</span></div></div></div><div class="col-sm-3" id="794"><div class="form-group" id="792"><label id="788"><span id="786" class="">Фамилия</span></label><div id="790"><span id="784" class="">Кафка</span></div></div></div><div class="col-sm-3" id="806"><div class="form-group" id="804"><label id="800"><span id="798" class="">Почта</span></label><div id="802"><span id="796" class="">kafka@test.shit.ua</span></div></div></div><div class="col-sm-3" id="818"><div class="form-group" id="816"><label id="812"><span id="810" class="">Телефон</span></label><div id="814"><span id="808" class="">1234567</span></div></div></div></div><div class="row" id="862"><div class="col-sm-3" id="836"><div class="form-group" id="834"><label id="830"><span id="828" class="">Тип</span></label><div id="832"><div id="826"><i id="822" class="fa fa-pencil null" style="margin-right: 5px; margin-left: 5px;"></i><span id="824" class="">Писатель</span></div></div></div></div><div class="col-sm-3" id="848"><div class="form-group" id="846"><label id="842"><span id="840" class="">Аккаунт создан</span></label><div id="844"><span id="838" class="">02/12/2016 14:24:32 (Киев)</span></div></div></div><div class="col-sm-3" id="860"><div class="form-group" id="858"><label id="854"><span id="852" class="">Профиль залит</span></label><div id="856"><span id="850" class="">02/12/2016 14:28:32 (Киев)</span></div></div></div></div><div class="row" id="876"><div class="col-sm-12" id="874"><div class="form-group" id="872"><label id="868"><span id="866" class="">Набрехано о себе</span></label><div id="870"><span id="864" class="" style="white-space: pre-wrap;">Пишу всякую хуйню за деньги</span></div></div></div></div></div></div></div></div>""")
    }

}

