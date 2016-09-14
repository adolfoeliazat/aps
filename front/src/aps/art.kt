/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

fun jsFacing_invokeStateContributions(arg: dynamic) {
    // {actual}={}
    val actual = if (arg) arg.actual else undefined

    jshit.art.stateContributionsByControl = js("new Map()")

    for (contribute in jsArrayToIterable(jshit.values(jshit.art.uiStateContributions))) {
        contribute(json(
            "put" to {arg: dynamic ->
                // {$definitionStack, $callStack, control, key, value}
                val `$definitionStack` = arg.`$definitionStack`; val `$callStack` = arg.`$callStack`
                val control = arg.control; val key = arg.key; val value = arg.value

                jshit.invariant(control, "I want control for state.put()")
                if (jshit.keys(actual).includes(key)) {
                    val message = "uiStateContribution put duplication: key=${key}, value=${value}"

                    runni {"__async"
                        val thisDefinitionStackString = __await(anyControlDefinitionStackString(control, sep = "\n"))
                        val existingDefinitionStackString = __await(anyControlDefinitionStackString(actual[key].control, sep = "\n"))
                        console.error(
                            "$message\n\n" +
                            "This: $thisDefinitionStackString\n\n" +
                            "Existing: $existingDefinitionStackString")
                    }

                    jshit.raiseWithMeta(json(
                        "message" to message,
                        "metaItems" to jsArrayOf(
                            json("titlePrefix" to "This", "meta" to control),
                            json("titlePrefix" to "Existing", "meta" to actual[key].control))))
                }

                if (control) {
                    var contributions = jshit.art.stateContributionsByControl.get(control)
                    if (!contributions) {
                        contributions = js("({})")
                        jshit.art.stateContributionsByControl.set(control, contributions)
                    }
                    contributions[key] = value
                }

                if (actual) {
                    actual[key] = json(
                        "value" to value,
                        "control" to control,
                        "\$definitionStack" to `$definitionStack`,
                        "\$callStack" to `$callStack`)
                }
            }
        ))
    }
}

