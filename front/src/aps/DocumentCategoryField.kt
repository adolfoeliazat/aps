@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.front.*
import into.kommon.*
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.window
import kotlin.js.Json
import kotlin.properties.Delegates.notNull

class SelenaKey(override val fqn: String) : Fucker(), FQNed

class Selena(initialValue: UADocumentCategoryRTO, val key: SelenaKey) : Control2() {
    private var value = initialValue
    private val place = Placeholder()
    private var rootCategory: UADocumentCategoryRTO? = null
    private var input by notNull<Input>()
    private var selectorPlace by notNull<Placeholder>()
    private var treeControl by notNull<ToReactElementable>()

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
        syncTreeWithInput()
    }

    private fun onInputKeyDown(e: KeyboardEvent) {
        if (e.keyCode == 13) {
            onEnterKey()
        } else {
            syncTreeWithInput()
        }
    }

    var pattern by notNull<String>()

    private fun syncTreeWithInput() {
        pattern = input.value.trim().toLowerCase()
        dlog("pattern = [$pattern]")
        if (pattern.isBlank()) {
            selectorPlace.setContent(treeControl)
        } else {
            selectorPlace.setContent(makeListControl(pattern))
        }
        dlog("Done")
    }

    private fun onEnterKey() {
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
            treeControl = makeTreeControl()
            selectorPlace = Placeholder(treeControl)

            place.setContent(kdiv{o->
                input = Input(autoFocus = true,
                              tabIndex = 0, // Needed for autoFocus to have effect
                              onKeyUp = this::onInputKeyDown)
                o- input
                o- selectorPlace
            })
        }
    }

    private fun makeListControl(pattern: String): ToReactElementable {
        val filteredItems = mutableListOf<ToReactElementable>()
        fun descend(cat: UADocumentCategoryRTO) {
            if (cat.children.isEmpty()) {
                if (cat.title.toLowerCase().contains(pattern)) {
                    val ranges = mutableListOf<IntRange>()
                    val separator = const.text.symbols.rightDoubleAngleQuotationSpaced
                    val lastSeparator = cat.pathTitle.lastIndexOf(separator)
                    var potentialRangeStart = when {
                        lastSeparator == -1 -> 0
                        else -> lastSeparator + separator.length
                    }
                    while (potentialRangeStart < cat.pathTitle.length) {
                        val rangeStart = cat.pathTitle.toLowerCase().indexOf(pattern, potentialRangeStart)
                        if (rangeStart == -1) break
                        ranges += IntRange(rangeStart, rangeStart + pattern.length - 1)
                        potentialRangeStart = rangeStart + pattern.length
                    }
                    filteredItems += renderItemTitle(cat, title = highlightedShit(cat.pathTitle, ranges, tag = "span"), underline = false)
                }
            } else {
                for (child in cat.children) {
                    descend(child)
                }
            }
        }
        descend(bang(rootCategory))

        if (filteredItems.isEmpty())
            return div(t("Fucking nothing", "Нихера не найдено"), className = css.DocumentCategoryField.nothing)

        val chunkSize = 9
        fun renderChunk(from: Int): ToReactElementable {
            return kdiv{o->
                var indexInItems by notNull<Int>()
                for (indexInChunk in 0 until chunkSize) {
                    indexInItems = from + indexInChunk
                    if (indexInItems > filteredItems.lastIndex) break
                    o- filteredItems[indexInItems]
                }
                if (indexInItems + 1 <= filteredItems.lastIndex) {
                    var showMorePlace by notNullOnce<Placeholder>()
                    showMorePlace = Placeholder(kdiv(
                        Attrs(className = css.DocumentCategoryField.showMore,
                              onClick = {
                                  showMorePlace.setContent(renderChunk(from = indexInItems + 1))
                              })){o->
                        o- t("Show more...", "Показать еще...")
                    })
                    o- showMorePlace
                }
            }
        }

        return shit{o->
            o- renderChunk(from = 0)
        }
    }

    private fun makeTreeControl(): ToReactElementable {
        return shit{o->
            for (cat in bang(this@Selena.rootCategory).children) {
                o- this@Selena.makeTreeItemControl(cat)
            }
        }
    }

    private fun shit(build: (ElementBuilder) -> Unit): ToReactElementable {
        return kdiv(className = css.DocumentCategoryField.itemContainer) {build(it)}
    }

    fun renderItemTitle(cat: UADocumentCategoryRTO, title: String, underline: Boolean): ElementBuilder {
        return renderItemTitle(cat, span(title), underline)
    }

    fun renderItemTitle(cat: UADocumentCategoryRTO, title: ToReactElementable, underline: Boolean): ElementBuilder {
        return kdiv(
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
            o- title
        }
    }

    fun makeTreeItemControl(cat: UADocumentCategoryRTO): ToReactElementable {

        return when {
            cat.children.isEmpty() -> kdiv{o->
                o- renderItemTitle(cat, title = cat.title, underline = false)
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
                            o- renderItemTitle(cat, title = cat.title, underline = false)
                        }
                        else -> kdiv{o->
                            o- hor2(cellStyle = {if (it == 1) Style(flexGrow = 1) else Style()}){o->
                                o- ki(className = fa.minusSquare.className,
                                      onClick = {
                                          collapsed = true
                                          me.update()
                                      })
                                o- renderItemTitle(cat, title = cat.title, underline = true)
                            }
                            o- kdiv(marginLeft = "2rem"){o->
                                for (child in cat.children) {
                                    o- makeTreeItemControl(child)
                                }
                            }
                        }
                    }
                }
            }
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

























