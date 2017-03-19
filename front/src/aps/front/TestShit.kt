package aps.front

import aps.*
import aps.front.fconst.test.testOffClassSuffix

fun fillTestProfile_Gaylord(o: TestSetUserFieldsRequest) {
    o.insertedAt.value = "2016-11-23 13:15:25"
    o.phone.value = "9110201"
    o.aboutMe.value = "I'm a little fucker, just a tiny little motherfucker"
    o.profileUpdatedAt.value = "2016-11-23 14:23:57"
}

fun takeHTMLForAssertion(under: CSSSelector): String {
    val jqel = jq(under)
    if (jqel.length != 1) wtf("Got ${jqel.length} pieces of shit at $under")

    var rawActual = stripUninterestingElements(jqel).innerHTML
    if (testOpts().addTestOffClassSuffixes)
        rawActual = rawActual.replace(testOffClassSuffix, "")
    return rawActual
}




