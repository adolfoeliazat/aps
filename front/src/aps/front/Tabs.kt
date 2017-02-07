package aps.front

import aps.*
import into.kommon.*

class TabKey(override val name: String) : NamedItem

abstract class TabKeyRefs(val group: NamedGroup<TabKey>) {
    protected val key = TabKey(name = qualifyMe(group))
    init {group.items += @Suppress("LeakingThis") key}
}

class TabSpec(
    val key: TabKey,
    val title: String,
    val content: ToReactElementable,
    val stripContent: ToReactElementable = kdiv()
)

class Tabs2(
    val tabs: List<TabSpec>,
    initialActiveKey: TabKey? = null,
    val switchOnTabClick: Boolean = true,
    val onTabClicka: suspend (id: TabKey) -> Unit = {},
    val tabDomIdPrefix: String? = null,
    val key: String? = "tabs"
): Control2(Attrs()) {

    companion object {
        val instances = mutableMapOf<String, Tabs2>()

        fun instance(key: String): Tabs2 {
            return instances[key] ?: bitch("No Tabs2 keyed `$key`")
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
                    o- kli(id = tabDomIdPrefix?.let {it + tab.key.name},
                           className = if (tab.key == activeID) "active" else ""){o->
                        o- ka(href="#", onClick = {e->
                            preventAndStop(e)
                            asu {clickOnTaba(tab.key)}
                        }){o->
                            o- tab.title
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

    override fun componentDidMount() {
        if (key != null) {
            instances[key] = this
        }
    }

    override fun componentWillUnmount() {
        if (key != null) {
            instances.remove(key)
        }
    }

}

//fun TestScenarioBuilder.tabsClickOnTab(key: String, id: String) {
//    imf("reimplement tabsClickOnTab")
////    acta("Choosing tab `$id`") {
////        Tabs2.instance(key).clickOnTaba(id)
////    }
//}






