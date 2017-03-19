package aps.front

import aps.*

interface FQNed {
    val fqn: String
}

fun Iterable<FQNed>.findSimplyNamed(name: String?) =
    find {simpleName(it.fqn) == name}

class TabKey(override val fqn: String) : Fucker(), FQNed

interface TabSpec {
    val key: TabKey
    val title: String
    val renderBody: () -> ToReactElementable
    val renderStrip: () -> ToReactElementable
}

class SimpleTabSpec(
    override val key: TabKey,
    override val title: String,
    override val renderBody: () -> ToReactElementable,
    override val renderStrip: () -> ToReactElementable = {kdiv()}
) : TabSpec

interface TabFiddling {
    fun click()
}

class Tabs2(
    val tabs: List<TabSpec>,
    initialActiveKey: TabKey? = null,
    val switchOnTabClick: Boolean = true,
    val onTabClicka: suspend (id: TabKey) -> Unit = {}
): Control2(Attrs()) {

    companion object {
        val instances = mutableMapOf<TabKey, TabFiddling>()

        fun instance(key: TabKey): TabFiddling {
            return instances[key] ?: bitch("No tab keyed `${key.fqn}`")
        }
    }

    private var activeID = initialActiveKey ?: tabs[0].key

    suspend fun clickOnTaba(id: TabKey) {
        if (switchOnTabClick) {
            activeID = id
            update()
        }
        onTabClicka(id)
    }

    override fun render() = kdiv{o->
        val activeTab = tabs.first {it.key == activeID}

        o- kdiv(position="relative"){o->
            o- kul(className="nav nav-tabs"){o->
                for (tab in tabs) {
                    o- kli(id = tab.key.fqn,
                           className = if (tab.key == activeID) "active" else ""){o->
                        o- object:Control2() {
                            override fun render() =
                                ka(href="#",
                                   onClick = {e->
                                       preventAndStop(e)
                                       asu {clickOnTaba(tab.key)}})
                                {o->
                                    o- tab.title
                                }

                            override fun componentDidMount() {
                                instances[tab.key] = object:TabFiddling {
                                    override fun click() {
                                        async {clickOnTaba(tab.key)}
                                    }
                                }
                            }

                            override fun componentWillUnmount() {
                                instances.remove(tab.key)
                            }
                        }
                    }
                }
            }

            o- kdiv(position = "absolute", right = 0, top = 0){o->
                o- activeTab.renderStrip()
            }
        }

        o- kdiv(marginTop=5){o->
            o- activeTab.renderBody()
        }
    }


}

suspend fun tabClick(ref: TestRef<TabKey>) {
    Tabs2.instance(ref.it).click()
}

class DescribedAssertionID(val descr: String?, val aid: String)

suspend fun tabSequence(tab: TestRef<TabKey>, halfway: DescribedAssertionID, done: DescribedAssertionID) {
    sequence({tabClick(tab)},
             steps = listOf(
                 PauseAssertResumeStep(TestGlobal.switchTabHalfwayLock, halfway),
                 PauseAssertResumeStep(TestGlobal.switchTabDoneLock, done)))
}

suspend fun tabSequence(tab: TestRef<TabKey>, aidHalfway: String, aidDone: String) {
    tabSequence(tab, halfway = DescribedAssertionID(descr = "Describe halfway", aid = aidHalfway),
                     done = DescribedAssertionID(descr = "Describe done", aid = aidDone))
}

suspend fun tabSequence(tab: TestRef<TabKey>, aidHalfway: String, done: DescribedAssertionID) {
    tabSequence(tab, halfway = DescribedAssertionID(descr = "Describe halfway", aid = aidHalfway),
                     done = done)
}

suspend fun quickTabSequence(tab: TestRef<TabKey>) {
    sequence({tabClick(tab)},
             steps = listOf(
                 PauseResumeStep(TestGlobal.switchTabHalfwayLock),
                 PauseResumeStep(TestGlobal.switchTabDoneLock)))
}






