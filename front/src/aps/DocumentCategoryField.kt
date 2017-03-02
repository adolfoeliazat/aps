@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.front.*
import into.kommon.*
import org.w3c.dom.TrackEvent
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
    private val selectables = mutableListOf<Focusable>()
    private var currentSelectable: Focusable? = null

    companion object {
        val instances = mutableMapOf<SelenaKey, Selena>()

        fun instance(key: SelenaKey): Selena {
            return instances[key] ?: bitch("No Selena keyed `${key.fqn}`")
        }
    }

    abstract class Focusable : Control2() {
        protected var selected = false

        fun setSelected(b: Boolean) {
            selected = b
            update()
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
        when (e.keyCode) {
            38 -> { // Up

            }

            40 -> { // Down
                if (selectables.isNotEmpty()) {
                    currentSelectable?.setSelected(false)
                    if (currentSelectable == null) {
                        currentSelectable = selectables.first()
                    } else {
                        val index = selectables.indexOf(bang(currentSelectable))
                        check(index > -1){"0f5b0d2e-ca8e-4507-b9e6-65ce997b4505"}
                        val newIndex = when {
                            index + 1 <= selectables.lastIndex -> index + 1
                            else -> 0
                        }
                        check(newIndex >= 0 && newIndex <= selectables.lastIndex){"9d9b8c3a-45a8-43ea-a9f9-99f8035c895a"}
                        currentSelectable = selectables[newIndex]
                    }
                    bang(currentSelectable).setSelected(true)
                }
            }
        }
    }

    private fun onInputKeyUp(e: KeyboardEvent) {
        if (e.keyCode !in setOf(38, 40, 13)) {
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
                              onKeyDown = this::onInputKeyDown,
                              onKeyUp = this::onInputKeyUp)
                o- input
                o- selectorPlace
            })
        }
    }

    private fun makeListControl(pattern: String): ToReactElementable {
        val filteredItems = mutableListOf<Focusable>()
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

                    filteredItems += object: Focusable() {
                        override fun render(): ToReactElementable {
                            return renderItemTitle(
                                cat,
                                title = highlightedShit(cat.pathTitle, ranges, tag = "span"),
                                underline = false,
                                className = when {
                                    selected -> css.DocumentCategoryField.itemFocused
                                    else -> css.DocumentCategoryField.item
                                })
                        }
                    }
                }
            } else {
                for (child in cat.children) {
                    descend(child)
                }
            }
        }
        descend(bang(rootCategory))

        selectables.clear()
        currentSelectable = null

        if (filteredItems.isEmpty())
            return div(t("Fucking nothing", "Нихера не найдено"), className = css.DocumentCategoryField.nothing)

        val chunkSize = 9
        fun renderChunk(from: Int): ToReactElementable {
            return kdiv{o->
                var indexInItems by notNull<Int>()
                for (indexInChunk in 0 until chunkSize) {
                    indexInItems = from + indexInChunk
                    if (indexInItems > filteredItems.lastIndex) break
                    val item = filteredItems[indexInItems]
                    o- item
                    selectables += item
                }
                if (indexInItems + 1 <= filteredItems.lastIndex) {
                    var showMorePlace by notNullOnce<Placeholder>()

                    val showMoreItem = object: Focusable() {
                        override fun render(): ToReactElementable {
                            return kdiv(
                                Attrs(
                                    className = when {
                                        selected -> css.DocumentCategoryField.showMoreFocused
                                        else -> css.DocumentCategoryField.showMore
                                    },
                                    onClick = {
                                        currentSelectable?.setSelected(false)
                                        val newSelectableIdx = selectables.lastIndex
                                        selectables.remove(selectables.last())
                                        showMorePlace.setContent(renderChunk(from = indexInItems + 1))
                                        currentSelectable = selectables[newSelectableIdx]
                                        bang(currentSelectable).setSelected(true)
                                    })){o->
                                o- t("Show more...", "Показать еще...")
                            }
                        }
                    }

                    showMorePlace = Placeholder(showMoreItem)
                    o- showMorePlace
                    selectables += showMoreItem
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
        return renderItemTitle(cat, span(title), underline, css.DocumentCategoryField.item)
    }

    fun renderItemTitle(cat: UADocumentCategoryRTO, title: ToReactElementable, underline: Boolean, className: String): ElementBuilder {
        return kdiv(
            Attrs(className = className,
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

























