/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.imf
import kotlin.browser.*

class TestWriter_Boot_ProfilePending : WriterBootTestScenario_FuckerToken() {
    override val shortDescription = "Valid token in local storage, user profile is not filled"
    override val path = "/"

    override fun setFuckerFields(o: TestSetUserFieldsRequest) {
        o.state.value = UserState.PROFILE_PENDING
    }

    override fun buildStepsAfterWorldBoot() {
        o.state("Fucker is signed in, system asks to fill profile")
        o.assertNavbarHTML("""<nav data-reactroot="" class="navbar navbar-default navbar-fixed-top" id="211"><div class="container" id="209"><div class="navbar-header" id="203"><a id="201" class="navbar-brand" href="/">Писец</a></div><div id="207" style="text-align: left;"><ul id="leftNavbar" class="nav navbar-nav" style="float: none; display: inline-block; vertical-align: top;"><li class="dropdown" id="197"><a href="#" class="dropdown-toggle skipClearMenus" data-toggle="dropdown" role="button" id="193"><!-- react-text: 36 -->Проза<!-- /react-text --><span class="caret" id="191" style="margin-left: 5px;"></span></a><ul class="dropdown-menu" id="195"><li id="179" class=""><a id="177" href="why.html"><span id="214" class="">Почему мы?</span></a></li><li id="182" class=""><a id="180" href="prices.html"><span id="219" class="">Цены</span></a></li><li id="185" class=""><a id="183" href="faq.html"><span id="224" class="">ЧаВо</span></a></li></ul></li><li id="188" class="active"><a id="186" href="profile.html"><span id="229" class="">Профиль</span></a></li></ul><ul id="rightNavbar" class="nav navbar-nav navbar-right"><li id="200" class=""><a id="198" href="dashboard.html"><span id="234" class="">Gaylord</span></a></li></ul></div></div></nav>""")
        o.assertRootHTML("""<div data-reactroot="" id="127"><div class="container" id="129" style="position: relative;"><div id="131"><div class="page-header " id="137" style="margin-top: 0px; margin-bottom: 15px;"><h3 id="135" style="margin-bottom: 0px;"><span id="133" class="">Профиль</span></h3></div></div><div id="82"><div id="74" style="width: 720px; margin-bottom: 15px;"><i class="fa fa-exclamation-triangle" id="70" style="color: rgb(255, 111, 0);"></i><!-- react-text: 37 -->&nbsp;<!-- /react-text --><!-- react-text: 38 -->&nbsp;<!-- /react-text --><span id="72" class="">Сначала заполняешь профиль. Админ связывается с тобой и активирует аккаунт. Потом все остальное.</span></div><div id="163"><form class="" id="161"><div class="form-group" id="147"><label id="143"><span id="141" class="">Телефон</span></label><div id="145" style="position: relative;"><input type="text" id="76" rows="5" class="form-control" value="" style="resize: none;"></div></div><div class="form-group" id="155"><label id="151"><span id="149" class="">Пара ласковых о себе</span></label><div id="153" style="position: relative;"><textarea id="78" rows="5" type="text" class="form-control" style="resize: none;"></textarea></div></div><div id="159" style="text-align: left;"><button id="157" class="btn btn-primary "><span id="167">Отправить на проверку</span></button></div></form></div></div></div></div>""")
    }

}

class TestWriter_Boot_ProfilePending_CanSignOut : WriterBootTestScenario_FuckerToken() {
    override val path = "/dashboard.html"

    override fun setFuckerFields(o: TestSetUserFieldsRequest) {
        o.state.value = UserState.PROFILE_PENDING
    }

    override fun buildStepsAfterWorldBoot() {
        o.state("Fucker is signed in, system asks to fill profile")
        o.assertNavbarHTML("""<nav data-reactroot="" class="navbar navbar-default navbar-fixed-top" id="207"><div class="container" id="205"><div class="navbar-header" id="199"><a id="197" class="navbar-brand" href="/">Писец</a></div><div id="203" style="text-align: left;"><ul id="leftNavbar" class="nav navbar-nav" style="float: none; display: inline-block; vertical-align: top;"><li class="dropdown" id="193"><a href="#" class="dropdown-toggle skipClearMenus" data-toggle="dropdown" role="button" id="189"><!-- react-text: 36 -->Проза<!-- /react-text --><span class="caret" id="187" style="margin-left: 5px;"></span></a><ul class="dropdown-menu" id="191"><li id="175" class=""><a id="173" href="why.html"><span id="210" class="">Почему мы?</span></a></li><li id="178" class=""><a id="176" href="prices.html"><span id="215" class="">Цены</span></a></li><li id="181" class=""><a id="179" href="faq.html"><span id="220" class="">ЧаВо</span></a></li></ul></li><li id="184" class=""><a id="182" href="profile.html"><span id="225" class="">Профиль</span></a></li></ul><ul id="rightNavbar" class="nav navbar-nav navbar-right"><li id="196" class="active"><a id="194" href="dashboard.html"><span id="230" class="">Gaylord</span></a></li></ul></div></div></nav>""")
        o.assertRootHTML("""<div data-reactroot="" id="125"><div class="container" id="127" style="position: relative;"><div id="129"><div class="page-header " id="135" style="margin-top: 0px; margin-bottom: 15px;"><h3 id="133" style="margin-bottom: 0px;"><span id="131" class="">Панель</span></h3></div></div><div id="106"><div class="row" id="104"><div class="col-sm-6" id="102"><div id="100"><div id="88" style="background-color: rgb(236, 239, 241); font-weight: bold; padding: 2px 5px; margin-bottom: 10px;">Аккаунт</div><ul class="fa-ul" id="98" style="margin-left: 20px;"><li id="92" style="margin-bottom: 5px;"><i class="fa fa-li fa-chevron-right" id="90" style="color: rgb(84, 110, 122);"></i><a id="82" class="" href="#" style="color: rgb(51, 51, 51);"><span id="80" class="">Выйти прочь</span></a></li><li id="96" style="margin-bottom: 5px;"><i class="fa fa-li fa-chevron-right" id="94" style="color: rgb(84, 110, 122);"></i><a id="86" class="" href="#" style="color: rgb(51, 51, 51);"><span id="84" class="">Сменить пароль</span></a></li></ul></div></div></div></div></div></div>""")
    }

}




















