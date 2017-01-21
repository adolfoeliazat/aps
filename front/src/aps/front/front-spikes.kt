@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*
import org.w3c.dom.css.CSSRule
import org.w3c.dom.css.CSSRuleList
import org.w3c.dom.css.CSSStyleDeclaration
import org.w3c.dom.css.CSSStyleSheet
import kotlin.browser.document

fun spikeFindProgressTickerKeyframes() {
    for (sheetIndex in 0 until document.styleSheets.length) {
        val styleSheet = document.styleSheets[sheetIndex] as CSSStyleSheet
        val rules = styleSheet.cssRules
        for (ruleIndex in 0 until rules.length) {
            val rule = rules[ruleIndex]!!
            if (rule.type == CSSRule.KEYFRAMES_RULE) {
                if (rule.asDynamic().name == "progressTicker") {
                    val subRules: CSSRuleList = rule.asDynamic().cssRules
                    val rule100 = subRules[1]!!
                    check(rule100.asDynamic().keyText == "100%") {"keyText"}
                    val rule100Style: CSSStyleDeclaration = rule100.asDynamic().style
                    gloshit.rule100Style = rule100Style
                    dwarnStriking(rule100Style)
                }
            }
        }
    }
}

