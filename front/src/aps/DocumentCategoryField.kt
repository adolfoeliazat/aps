@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.front.*
import into.kommon.*
import kotlin.js.Json
import kotlin.properties.Delegates.notNull

@Front class DocumentCategoryField(container: RequestMatumba, val spec: DocumentCategoryFieldSpec): FormFieldFront(container, spec.name) {
    override var disabled = false
    override fun focus() {}
    override var error: String? = null

    private var value by notNull<UADocumentCategoryRTO>()
    private val place: Placeholder by lazy {Placeholder(renderViewMode())}

    fun setValue(value: UADocumentCategoryRTO) {
        this.value = value
    }

    override fun render() =
        kdiv(className = "form-group"){o->
            o- label(spec.title)
            o- place
        }.toReactElement()

    private fun renderViewMode() =
        hor2(style = Style(alignItems = "center")){o->
            o- value.pathTitle
            o- Button(icon = fa.ellipsisH, key = buttons.chooseDocumentCategory) {
                place.setContent(renderEditMode())
            }
        }

    private var loadingRootCategory = false
    private var rootCategory: UADocumentCategoryRTO? = null

    private fun renderEditMode(): ToReactElementable =
        Control2.from {me->
            val rootCategory = this.rootCategory
            if (rootCategory == null) {
                if (!loadingRootCategory) {
                    loadingRootCategory = true
                    async {
                        val res = askRegina(ReginaGetDocumentCategories())
                        loadingRootCategory = false
                        when (res) {
                            is FormResponse2.Shitty -> imf("71191b6a-a183-4203-bee9-e90e43441e8a")
                            is FormResponse2.Hunky -> {
                                this.rootCategory = res.meat.root
                                me.update()
                            }
                        }
                    }
                }
                renderTicker()
            } else {
                kdiv(){o->
                    o- Input(autoFocus = true,
                             tabIndex = 0 // Needed for autoFocus to have effect
                    )
                    o- kdiv(Style(maxHeight = "20rem", marginTop = "0.5rem", overflow = "auto")){o->
                        fun makeItemControl(cat: UADocumentCategoryRTO): ToReactElementable {
                            fun renderItemTitle(underline: Boolean) = kdiv(
                                Attrs(className = css.DocumentCategoryField.item,
                                      onClick = {
                                          value = cat
                                          place.setContent(renderViewMode())
                                      }),
                                style = when {
                                    underline -> Style(borderBottom = "1px solid ${Color.GRAY_500}")
                                    else -> Style()
                                }
                            ){o->
                                o- cat.title
                            }

                            return when {
                                cat.children.isEmpty() -> kdiv{o->
                                    o- renderItemTitle(underline = false)
                                }
                                else -> {
                                    var collapsed = true
                                    Control2.from {me->
                                        when {
                                            collapsed -> hor2(cellStyle = {if (it == 1) Style(flexGrow = 1) else Style()}){o->
                                                o- ki(className = fa.plusSquare.className, onClick = {
                                                    collapsed = false
                                                    me.update()
                                                })
                                                o- renderItemTitle(underline = false)
                                            }
                                            else -> kdiv{o->
                                                o- hor2(cellStyle = {if (it == 1) Style(flexGrow = 1) else Style()}){o->
                                                    o- ki(className = fa.minusSquare.className, onClick = {
                                                        collapsed = true
                                                        me.update()
                                                    })
                                                    o- renderItemTitle(underline = true)
                                                }
                                                o- kdiv(marginLeft = "2rem"){o->
                                                    for (child in cat.children) {
                                                        o- makeItemControl(child)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        for (cat in rootCategory.children) {
                            o- makeItemControl(cat)
                        }
                    }
                }
            }
        }

    override fun populateRemote(json: Json): Promisoid<Unit> = async {
        json[name] = value.id.toString()
    }
}




