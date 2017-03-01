@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.front.*
import into.kommon.*
import kotlin.js.Json

class SelenaKey(override val fqn: String) : Fucker(), FQNed

class Selena(initialValue: UADocumentCategoryRTO, val key: SelenaKey) : Control2() {
    private var value = initialValue
    private val place = Placeholder()
    private var rootCategory: UADocumentCategoryRTO? = null
    private var input: Input? = null

    companion object {
        val instances = mutableMapOf<SelenaKey, Selena>()

        fun instance(key: SelenaKey): Selena {
            return instances[key] ?: bitch("No Selena keyed `${key.fqn}`")
        }
    }

    init {
        enterViewMode()
    }

    fun getValue() = value

    suspend fun testSetInputValue(s: String) {
        bang(input).testSetValue(s)
    }

    override fun render(): ToReactElementable {
        return place
    }

    override fun componentDidMount() {
        instances[key] = this
    }

    override fun componentWillUnmount() {
        instances.remove(key)
    }

    private fun enterViewMode() {
        input = null
        place.setContent(hor2(style = Style(alignItems = "center")){o->
            o- value.pathTitle
            o- Button(icon = fa.ellipsisH, key = buttons.chooseDocumentCategory) {
                enterEditMode()
            }
        })
    }

    private suspend fun enterEditMode() {
        val rootCategory = this.rootCategory
        if (rootCategory == null) {
            place.setContent(renderTicker())
            TestGlobal.shitHalfwayLock.resumeTestAndPauseSutFromSut()
            await(async {
                val res = askRegina(ReginaGetDocumentCategories())
                when (res) {
                    is FormResponse2.Shitty -> imf("71191b6a-a183-4203-bee9-e90e43441e8a")
                    is FormResponse2.Hunky -> {
                        this.rootCategory = res.meat.root
                        enterEditMode()
                    }
                }
            })
            TestGlobal.shitDoneLock.resumeTestFromSut()
        } else {
            place.setContent(kdiv{o->
                input = Input(autoFocus = true,
                              tabIndex = 0) // Needed for autoFocus to have effect
                o- input
                o- kdiv(Style(maxHeight = "20rem", marginTop = "0.5rem", overflow = "auto")){o->
                    fun makeItemControl(cat: UADocumentCategoryRTO): ToReactElementable {
                        fun renderItemTitle(underline: Boolean) = kdiv(
                            Attrs(className = css.DocumentCategoryField.item,
                                  onClick = {
                                      value = cat
                                      enterViewMode()
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
            })
        }
    }
}

@Front class DocumentCategoryField(container: RequestMatumba, val spec: DocumentCategoryFieldSpec): FormFieldFront(container, spec.name) {
    override var disabled = false
    override fun focus() {}
    override var error: String? = null

    private var selena by notNullOnce<Selena>()

    fun setValue(value: UADocumentCategoryRTO) {
        selena = Selena(initialValue = value, key = FieldSpecToCtrlKey[spec])
    }

    override fun render() =
        kdiv(className = "form-group"){o->
            o- label(spec.title)
            o- selena
        }.toReactElement()

    override fun populateRemote(json: Json): Promisoid<Unit> = async {
        json[name] = selena.getValue().id.toString()
    }
}

suspend fun selenaSetInputValue(field: TestRef<DocumentCategoryFieldSpec>, value: String) {
    val key = FieldSpecToCtrlKey[field.it]
    Selena.instance(key).testSetInputValue(value)
}

























