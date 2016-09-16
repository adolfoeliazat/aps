/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

object art {
    var testInstructions: dynamic = undefined

    fun run(spec: dynamic): Promise<Unit> {"__async"
        // #extract {instructions} from def
        val instructions = spec.instructions

        testInstructions = instructions

        val urlq = jshit.getURLQuery()
        // dlogs({urlq})
        var until = urlq.until
        if (until) until = global.parseInt(until, 10)
        val from = urlq.from || js("'start'")

        var skipping = from != js("'start'")

        val debugRPC = jshit.getDebugRPC()

        __await<dynamic>(debugRPC(json("fun" to "danger_clearSentEmails")))

        var stepIndex = 0
        for (instrIndex in 0 until instructions.length) {
            val instrdef = instructions[instrIndex]
            val opcode = global.Object.keys(instrdef).find({x: dynamic -> x[0] != "$"})
            if (!opcode) jshit.raiseWthMeta(json("message" to "Cannot determine opcode for instruction", "meta" to instrdef))

            if (instrIndex == until) {
                jshit.dlog("Stopping test before instruction ${instrIndex}")
                return __asyncResult(Unit)
            }

            val instr = instrdef[opcode]

            fun getControlForAction(arg: dynamic): dynamic {
                // {implementing}={}
                val implementing = if (arg) arg.implementing else undefined

                val control = global.testGlobal.controls[instr.shame]
                if (!control) jshit.raiseWithMeta(json("message" to "Control shamed ${instr.shame} is not found", "meta" to instrdef))
                if (implementing && !control[implementing]) jshit.raiseWithMeta(json("message" to "Control shamed ${instr.shame} is expected to implement ${implementing}", "meta" to instrdef))
                return control
            }

            if (opcode == "worldPoint") {
                val wpname = jshit.getCurrentTestScenarioName() + " -- " + instr.name
                if (skipping) {
                    if (instr.name == from) {
                        jshit.dlog("Restoring world point ${wpname}")
                        __await<dynamic>(debugRPC(json("db" to undefined, "fun" to "danger_restoreWorldPoint", "wpname" to wpname)))
                        skipping = false
                    }
                } else {
                    jshit.dlog("Saving world point ${wpname}")
                    __await(debugRPC(json("db" to undefined, "fun" to "danger_saveWorldPoint", "wpname" to wpname)))
                }
                continue
            }

            if (skipping) continue

            if (opcode == "do") {
                __await<dynamic>(instr.action())
                continue
            }
            if (opcode == "step") {
                instr.fulfilled = true
                continue
            }
            if (jsArrayOf("beginSection", "endSection").includes(opcode)) {
                continue
            }
            if (opcode == "assert") {
                __await<dynamic>(jshit.art.uiState(instr.asnn(jshit.pick(instrdef, "\$definitionStack"))))
                continue
            }
            if (opcode == "setValue") {
                val control = getControlForAction(json("implementing" to "testSetValue"))
                if (instr.timestamp) {
                    __await<dynamic>(debugRPC(json("fun" to "danger_imposeNextRequestTimestamp", "timestamp" to instr.timestamp)))
                }
                __await<dynamic>(control.testSetValue(json("value" to instr.value)))
                continue
            }
            if (opcode == "click") {
                val control = getControlForAction(json("implementing" to "testClick"))
                if (instr.timestamp) {
                    __await<dynamic>(debugRPC(json("fun" to "danger_imposeNextRequestTimestamp", "timestamp" to instr.timestamp)))
                }
                __await<dynamic>(control.testClick(instr))
                continue
            }
            if (opcode == "keyDown") {
                val control = getControlForAction(json("implementing" to "testKeyDown"))
                if (instr.timestamp) {
                    __await<dynamic>(debugRPC(json("fun" to "danger_imposeNextRequestTimestamp", "timestamp" to instr.timestamp)))
                }
                __await<dynamic>(control.testKeyDown(instr))
                continue
            }
            if (opcode == "actionPlaceholder") {
                // invariant(!art.actionPlaceholderTag, "Action placeholder tag is already set")
                jshit.art.actionPlaceholderTag = instr.`$tag`
                continue
            }

            jshit.raiseWithMeta(json("message" to "Unknown instruction opcode: ${opcode}", "meta" to instrdef))
        }

        if (skipping) {
            console.warn("WTF, I’ve just skipped all test steps")
        } else {
            jshit.dlog("Seems test is passed")
        }

        return __asyncResult(Unit)
    }

}

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

