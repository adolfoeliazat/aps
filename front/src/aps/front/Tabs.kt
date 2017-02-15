package aps.front

import aps.*
import into.kommon.*

interface FQNed {
    val fqn: String
}

fun Iterable<FQNed>.findSimplyNamed(name: String?) =
    find {simpleName(it.fqn) == name}

class TabKey(override val fqn: String) : Fucker(), FQNed

class TabSpec(
    val key: TabKey,
    val title: String,
    val content: ToReactElementable,
    val stripContent: ToReactElementable = kdiv()
)

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
                o- activeTab.stripContent
            }
        }

        o- kdiv(marginTop=5){o->
            o- activeTab.content
        }
    }


}

suspend fun tabClick(ref: TestRef<TabKey>) {
    Tabs2.instance(ref.it).click()
}

suspend fun tabSequence(tab: TestRef<TabKey>, aidHalfway: String, aidDone: String) {
    sequence({tabClick(tab)},
             steps = listOf(
                 PauseAssertResumeStep(TestGlobal.switchTabHalfwayLock, aidHalfway),
                 PauseAssertResumeStep(TestGlobal.switchTabDoneLock, aidDone)))
}

suspend fun quickTabSequence(tab: TestRef<TabKey>) {
    sequence({tabClick(tab)},
             steps = listOf(
                 PauseResumeStep(TestGlobal.switchTabHalfwayLock),
                 PauseResumeStep(TestGlobal.switchTabDoneLock)))
}






