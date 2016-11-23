/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.imf
import kotlin.browser.*

class TestWriter_Boot_ProfileRejected : WriterBootTestScenario_FuckerToken() {
    override val shortDescription = "Valid token in local storage, user profile is rejected"
    override val path = "/"

    override fun setFuckerFields(o: TestSetUserFieldsRequest) {
        o.state.value = UserState.PROFILE_REJECTED
        o.profileRejectionReason.value = "Отстойный какой-то профиль, придумай что-то получше"
    }

    override fun buildStepsAfterWorldBoot() {
        o.state("Fucker is signed in, system asks to fill profile again because it was rejected")
        o.assertNavbarHTML("""<nav data-reactroot="" class="navbar navbar-default navbar-fixed-top" id="198"><div class="container" id="196"><div class="navbar-header" id="191"><a id="189" class="navbar-brand" href="/">Писец</a></div><div id="194" style="text-align: left;"><ul id="leftNavbar" class="nav navbar-nav" style="float: none; display: inline-block; vertical-align: top;"><li class="dropdown" id="188"><a href="#" class="dropdown-toggle skipClearMenus" data-toggle="dropdown" role="button" id="184"><!-- react-text: 32 -->Проза<!-- /react-text --><span class="caret" id="182" style="margin-left: 5px;"></span></a><ul class="dropdown-menu" id="186"><li id="170" class=""><a id="168" href="why.html"><span id="201" class="">Почему мы?</span></a></li><li id="173" class=""><a id="171" href="prices.html"><span id="206" class="">Цены</span></a></li><li id="176" class=""><a id="174" href="faq.html"><span id="211" class="">ЧаВо</span></a></li></ul></li><li id="179" class="active"><a id="177" href="profile.html"><span id="216" class="">Профиль</span></a></li></ul></div></div></nav>""")
        o.assertRootHTML("""""")
    }

}

class TestWriter_Boot_ProfileRejected_CanSignOut : WriterBootTestScenario_FuckerToken() {
    override val shortDescription = "Valid token in local storage, user profile is rejected"
    override val path = "/"

    override fun setFuckerFields(o: TestSetUserFieldsRequest) {
        imf()
        o.state.value = UserState.PROFILE_REJECTED
        o.profileRejectionReason.value = "Отстойный какой-то профиль, придумай что-то получше"
    }

    override fun buildStepsAfterWorldBoot() {
        o.state("Fucker is signed in, system asks to fill profile again because it was rejected")
        o.assertNavbarHTML("""<nav data-reactroot="" class="navbar navbar-default navbar-fixed-top" id="198"><div class="container" id="196"><div class="navbar-header" id="191"><a id="189" class="navbar-brand" href="/">Писец</a></div><div id="194" style="text-align: left;"><ul id="leftNavbar" class="nav navbar-nav" style="float: none; display: inline-block; vertical-align: top;"><li class="dropdown" id="188"><a href="#" class="dropdown-toggle skipClearMenus" data-toggle="dropdown" role="button" id="184"><!-- react-text: 32 -->Проза<!-- /react-text --><span class="caret" id="182" style="margin-left: 5px;"></span></a><ul class="dropdown-menu" id="186"><li id="170" class=""><a id="168" href="why.html"><span id="201" class="">Почему мы?</span></a></li><li id="173" class=""><a id="171" href="prices.html"><span id="206" class="">Цены</span></a></li><li id="176" class=""><a id="174" href="faq.html"><span id="211" class="">ЧаВо</span></a></li></ul></li><li id="179" class="active"><a id="177" href="profile.html"><span id="216" class="">Профиль</span></a></li></ul></div></div></nav>""")
        o.assertRootHTML("""""")
    }

}




















