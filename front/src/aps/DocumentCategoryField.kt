@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.front.*
import into.kommon.*
import org.w3c.dom.events.KeyboardEvent
import kotlin.js.Json
import kotlin.properties.Delegates.notNull

class SelenaKey(override val fqn: String) : Fucker(), FQNed

class Selena(initialValue: UADocumentCategoryRTO, val key: SelenaKey) : Control2() {
    private var value = initialValue
    private val place = Placeholder()
    private var rootCategory: UADocumentCategoryRTO? = null
    private var input by notNull<Input>()
    private var updateTreeControl by notNull<() -> Unit>()
    private var treeControlPlace by notNull<Placeholder>()
    private var unfilteredTreeControl by notNull<Control2>()
    private var filteredTreeControl by notNull<Control2>()

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
        input.testSetValue(s)
        onInputEditingKeyDown()
    }

    private fun onInputKeyDown(e: KeyboardEvent) {
        if (e.keyCode == 13) {
            onInputEnterKeyDown()
        } else {
            onInputEditingKeyDown()
        }
    }

    private fun onInputEditingKeyDown() {
//        async {
//            var unfiltered = true
//            while (true) {
//                clog("unfiltered = $unfiltered")
//                treeControlPlace.setContent(
//                    when {
//                        unfiltered -> unfilteredTreeControl
//                        else -> filteredTreeControl
//                    }
//                )
//                sleep(250)
//                unfiltered = !unfiltered
//            }
//        }

        val pattern = bang(byid(input.elementID).`val`()).trim().toLowerCase()
        dlog("Updating: pattern = [$pattern]")
        if (pattern.isBlank()) {
            treeControlPlace.setContent(unfilteredTreeControl)
        } else {
            treeControlPlace.setContent(filteredTreeControl)
        }
        dlog("Done")
    }

    private fun onInputEnterKeyDown() {
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
            unfilteredTreeControl = makeTreeControl(everythingExpanded = false)
            filteredTreeControl = makeTreeControl(everythingExpanded = true)
            treeControlPlace = Placeholder(unfilteredTreeControl)

            place.setContent(kdiv{o->
                input = Input(autoFocus = true,
                              tabIndex = 0, // Needed for autoFocus to have effect
                              onKeyUp = this::onInputKeyDown)
                o- input
                o- treeControlPlace
            })
        }
    }

    private fun makeTreeControl(everythingExpanded: Boolean): Control2 {
        val shit = kdiv(Style(maxHeight = "20rem", marginTop = "0.5rem", overflow = "auto")){o->
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
                    cat.children.isEmpty() -> {
                        kdiv{o->
                            o- renderItemTitle(underline = false)
                        }
                    }
                    else -> {
                        var collapsed = !everythingExpanded
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
                                        o- ki(className = if (everythingExpanded) fa.square.className else fa.minusSquare.className,
                                              onClick = {
                                                  if (!everythingExpanded) {
                                                      collapsed = true
                                                      me.update()
                                                  }
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

            for (cat in bang(rootCategory).children) {
                o- makeItemControl(cat)
            }
        }

        val html = ReactDOMServer.renderToStaticMarkup(shit.toReactElement())

        return Control2.from {me->
            updateTreeControl = {me.update()}
            rawHTML(html).toToReactElementable()
//            shit
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

























