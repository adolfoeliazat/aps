package aps.front

import aps.*
import into.kommon.*

class TabKey(override val name: String) : NamedItem

abstract class TabKeyRefs(val group: NamedGroup<TabKey>) {
    protected val key = TabKey(name = qualifyMe(group))
    init {group.items += key}
}

class TabSpec(
    val key: TabKey,
    val title: String,
    val content: ToReactElementable,
    val stripContent: ToReactElementable = kdiv()
)

interface TabFiddling {

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
            return instances[key] ?: bitch("No tab keyed `${key.name}`")
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
                    o- kli(id = tab.key.name,
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

//fun TestScenarioBuilder.tabsClickOnTab(key: String, id: String) {
//    imf("reimplement tabsClickOnTab")
////    acta("Choosing tab `$id`") {
////        Tabs2.instance(key).clickOnTaba(id)
////    }
//}






