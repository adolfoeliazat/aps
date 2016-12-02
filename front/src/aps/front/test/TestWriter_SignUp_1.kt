/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.front.*
import kotlin.browser.*

class TestWriter_SignUp_1 : WriterBootTestScenario() {
    override val path = "/"

    override fun fillStorageLocal() {
    }

    override fun buildStepsAfterDisplayInitialShit() {
        assert_staticHomePage_rightNavbarSignIn()

    }

    override fun buildStepsAfterWorldBoot() {
        assert_staticHomePage_rightNavbarSignIn()
        o.click("TopNavItem-sign-in")
        o.assertRootHTML("""<div data-reactroot="" id="117"><div class="container" id="119" style="position: relative;"><div id="123"><div class="page-header " id="129" style="margin-top: 0px; margin-bottom: 15px;"><h3 id="127" style="margin-bottom: 0px;"><span id="125" class="">Вход</span></h3></div></div><div id="121"><div id="156"><form class="" id="154"><div class="form-group" id="140"><label id="136"><span id="134" class="">Почта</span></label><div id="138" style="position: relative;"><input type="text" id="107" rows="5" class="form-control" value="" style="resize: none;"></div></div><div class="form-group" id="148"><label id="144"><span id="142" class="">Пароль</span></label><div id="146" style="position: relative;"><input type="password" id="109" rows="5" class="form-control" value="" style="resize: none;"></div></div><div id="152" style="text-align: left;"><button id="150" class="btn btn-primary "><span id="160">Войти</span></button></div></form></div><div id="132"><hr><div id="162" style="text-align: left;"><!-- react-text: 30 -->Как? Еще нет аккаунта? <!-- /react-text --><a id="112" href="#"><span id="114" class="">Срочно создать!</span></a></div></div></div></div></div>""")
    }

}

