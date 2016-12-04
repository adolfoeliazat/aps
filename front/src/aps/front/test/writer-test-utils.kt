package aps.front

import aps.*
import into.kommon.*

interface WriterTestUtils {

    fun assert_staticHomePage_rightNavbarSignIn(o: TestScenarioBuilder) {
        o.section(currentJSFunctionName()) {
            o.assertVisibleText("Приветствуем")
            o.assertNavbarHTML("""<nav data-reactroot="" class="navbar navbar-default navbar-fixed-top" id="75"><div class="container" id="73"><div class="navbar-header" id="67"><a id="65" class="navbar-brand" href="/">Писец</a></div><div id="71" style="text-align: left;"><ul id="leftNavbar" class="nav navbar-nav" style="float: none; display: inline-block; vertical-align: top;"><li id="55" class=""><a id="53" href="why.html"><span id="78" class="">Почему мы?</span></a></li><li id="58" class=""><a id="56" href="prices.html"><span id="83" class="">Цены</span></a></li><li id="61" class=""><a id="59" href="faq.html"><span id="88" class="">ЧаВо</span></a></li></ul><ul id="rightNavbar" class="nav navbar-nav navbar-right"><li id="64" class=""><a id="62" href="sign-in.html"><span id="93" class="">Вход</span></a></li></ul></div></div></nav>""")
        }
    }

    fun goto_signUpForm(o: TestScenarioBuilder) {
        o.section(currentJSFunctionName()) {
            o.click("TopNavItem-sign-in")
            o.assertRootHTML("""<div data-reactroot="" id="117"><div class="container" id="119" style="position: relative;"><div id="123"><div class="page-header " id="129" style="margin-top: 0px; margin-bottom: 15px;"><h3 id="127" style="margin-bottom: 0px;"><span id="125" class="">Вход</span></h3></div></div><div id="121"><div id="156"><form class="" id="154"><div class="form-group" id="140"><label id="136"><span id="134" class="">Почта</span></label><div id="138" style="position: relative;"><input type="text" id="107" rows="5" class="form-control" value="" style="resize: none;"></div></div><div class="form-group" id="148"><label id="144"><span id="142" class="">Пароль</span></label><div id="146" style="position: relative;"><input type="password" id="109" rows="5" class="form-control" value="" style="resize: none;"></div></div><div id="152" style="text-align: left;"><button id="150" class="btn btn-primary "><span id="160">Войти</span></button></div></form></div><div id="132"><hr><div id="162" style="text-align: left;"><!-- react-text: 30 -->Как? Еще нет аккаунта? <!-- /react-text --><a id="112" href="#"><span id="114" class="">Срочно создать!</span></a></div></div></div></div></div>""")
            o.click("urlLink-createAccount")
            o.assertRootHTML("""<div data-reactroot="" id="239"><div class="container" id="241" style="position: relative;"><div id="243"><div class="page-header " id="249" style="margin-top: 0px; margin-bottom: 15px;"><h3 id="247" style="margin-bottom: 0px;"><span id="245" class="">Регистрация</span></h3></div></div><div id="237"><div id="296"><form class="" id="294"><div class="form-group" id="258"><label id="254"><span id="252" class="">Почта</span></label><div id="256" style="position: relative;"><input type="text" id="217" rows="5" class="form-control" value="" style="resize: none;"></div></div><div class="form-group" id="266"><label id="262"><span id="260" class="">Имя</span></label><div id="264" style="position: relative;"><input type="text" id="219" rows="5" class="form-control" value="" style="resize: none;"></div></div><div class="form-group" id="274"><label id="270"><span id="268" class="">Фамилия</span></label><div id="272" style="position: relative;"><input type="text" id="221" rows="5" class="form-control" value="" style="resize: none;"></div></div><div class="form-group" id="288"><div id="286" style="display: flex;"><input type="checkbox" id="223" value="on"><div id="276" style="width: 5px;"></div><div id="284"><span id="278" class="">Я прочитал и принял </span><a id="282" class="" href="#"><span id="280" class="">соглашение</span></a></div></div></div><div id="292" style="text-align: left;"><button id="290" class="btn btn-primary "><span id="304">Вперед</span></button></div></form></div><div id="235"><hr><div id="233" style="text-align: left;"><span id="227" class="">Уже есть аккаунт? Тогда </span><a id="228" class="" href="#"><span id="230" class="">входим сюда</span></a><!-- react-text: 74 -->.<!-- /react-text --></div></div></div></div></div>""")
        }
    }

}

