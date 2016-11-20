package aps.front

import aps.*

class TabSpec(val id: String, val content: ToReactElementable, val title: String)

class Tabs2(val tabs: List<TabSpec>, initialActiveID: String? = null): Control2(Attrs()) {
    private var activeID = initialActiveID ?: tabs[0].id

    override fun render() = kdiv{o->
        o- kdiv(position="relative"){o->
            o- kul(className="nav nav-tabs"){o->
                for (tab in tabs) {
                    o- kli(className = if (tab.id == activeID) "active" else ""){o->
                        o- ka(href="#", onClick = {e->
                            preventAndStop(e)
                            activeID = tab.id
                            update()
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
