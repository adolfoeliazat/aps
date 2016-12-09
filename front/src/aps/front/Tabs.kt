package aps.front

import aps.*
import into.kommon.*

class TabSpec(
    val id: String,
    val title: String,
    val content: ToReactElementable,
    val stripContent: ToReactElementable = kdiv()
)

class Tabs2(
    val tabs: List<TabSpec>,
    initialActiveID: String? = null,
    val switchOnTabClick: Boolean = true,
    val onTabClicka: (id: String) -> Promise<Unit> = {async{}},
    val tabDomIdPrefix: String? = null,
    val key: String? = "tabs"
): Control2(Attrs()) {
    companion object {
        val instances = mutableMapOf<String, Tabs2>()

        fun instance(key: String): Tabs2 {
            return instances[key] ?: bitch("No Tabs2 keyed `$key`")
        }
    }

    private var activeID = initialActiveID ?: tabs[0].id

    fun clickOnTaba(id: String): Promise<Unit> = async {
        if (switchOnTabClick) {
            activeID = id
            update()
        }
        await(onTabClicka(id))
    }

    override fun render() = kdiv{o->
        val activeTab = tabs.first {it.id == activeID}

        o- kdiv(position="relative"){o->
            o- kul(className="nav nav-tabs"){o->
                for (tab in tabs) {
                    o- kli(id = tabDomIdPrefix?.let {it + tab.id},
                           className = if (tab.id == activeID) "active" else ""){o->
                        o- ka(href="#", onClick = {e->
                            preventAndStop(e)
                            clickOnTaba(tab.id)
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
