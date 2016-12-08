package aps.front

import aps.*

class TabSpec(val id: String, val title: String, val content: ToReactElementable)

class Tabs2(
    val tabs: List<TabSpec>,
    initialActiveID: String? = null,
    val switchOnTabClick: Boolean = true,
    val onTabClicka: (id: String) -> Promise<Unit> = {async{}},
    val tabDomIdPrefix: String? = null
): Control2(Attrs()) {
    private var activeID = initialActiveID ?: tabs[0].id

    override fun render() = kdiv{o->
        o- kdiv(position="relative"){o->
            o- kul(className="nav nav-tabs"){o->
                for (tab in tabs) {
                    o- kli(id = tabDomIdPrefix?.let {it + tab.id},
                           className = if (tab.id == activeID) "active" else ""){o->
                        o- ka(href="#", onClick = {e->
                            preventAndStop(e)
                            if (switchOnTabClick) {
                                activeID = tab.id
                                update()
                            }
                            onTabClicka(tab.id)
                        }){o->
                            o- tab.title
                        }
                    }
                }
            }
        }
        o- kdiv(marginTop=5){o->
            o- tabs.first{it.id == activeID}.content
        }
    }

}
