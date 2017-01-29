package aps.front

import aps.*

abstract class FuckingScenario : TestScenario() {
    abstract suspend fun run1()

    override fun run0(showTestPassedPane: Boolean): Promisoid<Throwable?> {
        val instrs = listOf(TestInstruction.Do {asu{
            run1()
        }})
        return art.run(testShit, instrs, showTestPassedPane)
    }
}

