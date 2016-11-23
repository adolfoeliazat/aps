package aps.front.test

import aps.TestSetUserFieldsRequest
import aps.UserState
import aps.front.WriterBootTestScenario_FuckerToken

class TestWriter_Boot_Banned : WriterBootTestScenario_FuckerToken() {
    override val shortDescription = "Valid token in local storage, user profile is not filled"
    override val path = "/"

    override fun setFuckerFields(o: TestSetUserFieldsRequest) {
        o.state.value = UserState.PROFILE_PENDING
    }

    override fun buildStepsAfterWorldBoot() {
        o.state("Fucker is signed in, system asks to fill profile")
        o.assertNavbarHTML("""<nav data-reactroot="" class="navbar navbar-default navbar-fixed-top" id="198"><div class="container" id="196"><div class="navbar-header" id="191"><a id="189" class="navbar-brand" href="/">Писец</a></div><div id="194" style="text-align: left;"><ul id="leftNavbar" class="nav navbar-nav" style="float: none; display: inline-block; vertical-align: top;"><li class="dropdown" id="188"><a href="#" class="dropdown-toggle skipClearMenus" data-toggle="dropdown" role="button" id="184"><!-- react-text: 32 -->Проза<!-- /react-text --><span class="caret" id="182" style="margin-left: 5px;"></span></a><ul class="dropdown-menu" id="186"><li id="170" class=""><a id="168" href="why.html"><span id="201" class="">Почему мы?</span></a></li><li id="173" class=""><a id="171" href="prices.html"><span id="206" class="">Цены</span></a></li><li id="176" class=""><a id="174" href="faq.html"><span id="211" class="">ЧаВо</span></a></li></ul></li><li id="179" class="active"><a id="177" href="profile.html"><span id="216" class="">Профиль</span></a></li></ul></div></div></nav>""")
        o.assertRootHTML("""<div data-reactroot="" id="127"><div class="container" id="129" style="position: relative;"><div id="131"><div class="page-header " id="137" style="margin-top: 0px; margin-bottom: 15px;"><h3 id="135" style="margin-bottom: 0px;"><span id="133" class="">Профиль</span></h3></div></div><div id="82"><div id="74" style="width: 720px; margin-bottom: 15px;"><i class="fa fa-exclamation-triangle" id="70" style="color: rgb(255, 111, 0);"></i><!-- react-text: 37 -->&nbsp;<!-- /react-text --><!-- react-text: 38 -->&nbsp;<!-- /react-text --><span id="72" class="">Сначала заполняешь профиль. Админ связывается с тобой и активирует аккаунт. Потом все остальное.</span></div><div id="163"><form class="" id="161"><div class="form-group" id="147"><label id="143"><span id="141" class="">Телефон</span></label><div id="145" style="position: relative;"><input type="text" id="76" rows="5" class="form-control" value="" style="resize: none;"></div></div><div class="form-group" id="155"><label id="151"><span id="149" class="">Пара ласковых о себе</span></label><div id="153" style="position: relative;"><textarea id="78" rows="5" type="text" class="form-control" style="resize: none;"></textarea></div></div><div id="159" style="text-align: left;"><button id="157" class="btn btn-primary "><span id="167">Отправить на проверку</span></button></div></form></div></div></div></div>""")
    }

}

