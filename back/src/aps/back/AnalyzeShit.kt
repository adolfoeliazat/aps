/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import into.kotlin.AnalyzeKotlinSources

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
        val aks = AnalyzeKotlinSources("$APS_ROOT/front/src")
    }
}

