/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.front.*
import kotlin.browser.*

class TestWriter_Boot1 : WriterBootTestScenario() {
    override val shortDescription = "No token in local storage"

    override fun fillLocalStorage() {}

    override fun buildStepsAfterDisplayInitialShit() {
        o.state("Showing static content")
        o.assertVisibleText("Приветствуем")
        o.assertHTML(under = "#topNavbarContainer",
                     expected = """<nav data-reactroot="" class="navbar navbar-default navbar-fixed-top" id="75"><div class="container" id="73"><div class="navbar-header" id="67"><a id="65" class="navbar-brand" href="/">Писец</a></div><div id="71" style="text-align: left;"><ul id="leftNavbar" class="nav navbar-nav" style="float: none; display: inline-block; vertical-align: top;"><li id="55" class=""><a id="53" href="why.html"><span id="78" class="">Почему мы?</span></a></li><li id="58" class=""><a id="56" href="prices.html"><span id="83" class="">Цены</span></a></li><li id="61" class=""><a id="59" href="faq.html"><span id="88" class="">ЧаВо</span></a></li></ul><ul id="rightNavbar" class="nav navbar-nav navbar-right"><li id="64" class=""><a id="62" href="sign-in.html"><span id="93" class="">Вход</span></a></li></ul></div></div></nav>""",
                     transformLine = {it.replace(Regex(" id=\"MakeStaticSites-\\d+\""), "")})
    }

    override fun buildStepsAfterWorldBoot() {
        o.state("JS arrived, nothing changed visually")
        o.assertVisibleText("Приветствуем")
        o.assertVisibleText("Вход", under = "#topNavbarContainer")
    }
}

