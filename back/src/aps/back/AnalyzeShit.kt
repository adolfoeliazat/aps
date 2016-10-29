/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import into.kommon.*
import into.kotlin.AnalyzeKotlinSources
import into.kotlin.AnalyzeKotlinSources.TargetPlatform.JS

class AnalyzeShit {
    class MixableType(val name: String, val props: List<Prop> = mutableListOf())

    class Prop {
        lateinit var name: String
        lateinit var type: String
        lateinit var defaultValue: String

        override fun toString(): String {
            return "Prop(name='$name', type='$type', defaultValue='$defaultValue')"
        }
    }

    val nameToMixableType = mutableMapOf<String, MixableType>()

    init {
        val aks = AnalyzeKotlinSources(JS, arrayOf(
            "$KOMMON_HOME/both-js-junction/src",
            "$KOMMON_HOME/js/src",
            "$APS_HOME/shared-front-junction/src",
            "$APS_HOME/front/src"
        ))
    }
}

