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
    private var selectorPlace by notNull<Placeholder>()
    private var treeControl by notNull<ToReactElementable>()
    private val focusables = mutableListOf<Focusable>()
    private var currentFocusable: Focusable? = null
    private val itemContainerID = puid()
    private var prevInputValueSeenInKeyUpHandler: String? = null

    init {
        enterViewMode()
    }

    companion object {
        val instances = mutableMapOf<SelenaKey, Selena>()

        fun instance(key: SelenaKey): Selena {
            return instances[key] ?: bitch("No Selena keyed `${key.fqn}`")
        }

        fun instance(field: TestRef<DocumentCategoryFieldSpec>): Selena {
            return instance(FieldSpecToCtrlKey[field.it])
        }

    }

    abstract class Focusable : Control2() {
        abstract fun select()

        protected var selected = false

        fun setFocused(b: Boolean) {
            selected = b
            update()
        }
    }

    fun getValue() = value

    suspend fun testSetInputValue(s: String) {
        prevInputValueSeenInKeyUpHandler = s
        input.testSetValue(s)
        syncTreeWithInput()
    }

    suspend fun testSendSpecialKey(keyCode: Int) {
        onInputKeyDown(fakeKeyboardEvent(keyCode = keyCode))
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

    private fun onInputKeyDown(e: KeyboardEvent) {
        when (e.keyCode) {
            fconst.keyCode.enter -> {
                preventAndStop(e)
                currentFocusable?.select()
            }

            fconst.keyCode.up -> {
                handleVerticalArrowKey(
                    e = e,
                    initialItemIfNothingFocused = {focusables.last()},
                    computeNewIndex = {when{
                        it - 1 >= 0 -> it - 1
                        else -> focusables.lastIndex
                    }})
            }

            fconst.keyCode.down -> {
                handleVerticalArrowKey(
                    e = e,
                    initialItemIfNothingFocused = {focusables.first()},
                    computeNewIndex = {when{
                        it + 1 <= focusables.lastIndex -> it + 1
                        else -> 0
                    }})
            }
        }
    }

    private fun handleVerticalArrowKey(e: KeyboardEvent,
                                       initialItemIfNothingFocused: () -> Focusable,
                                       computeNewIndex: (index: Int) -> Int) {
        preventAndStop(e)
        if (!focusables.isNotEmpty()) return

        currentFocusable?.setFocused(false)

        if (currentFocusable == null) {
            currentFocusable = initialItemIfNothingFocused()
        } else {
            val index = focusables.indexOf(bang(currentFocusable))
            check(index > -1) {"0f5b0d2e-ca8e-4507-b9e6-65ce997b4505"}
            val newIndex = computeNewIndex(index)
            check(newIndex >= 0 && newIndex <= focusables.lastIndex) {"9d9b8c3a-45a8-43ea-a9f9-99f8035c895a"}
            currentFocusable = focusables[newIndex]
        }

        bang(currentFocusable).setFocused(true)

        run { // Adjust scroll
            val container = bang(byid0(itemContainerID))
            val el = bang(currentFocusable)
            val elOffsetTop = bang(byid0(el.elementID)).offsetTop
            val elOffsetHeight = bang(byid0(el.elementID)).offsetHeight
            val containerScrollTop = container.scrollTop
            val containerOffsetHeight = container.offsetHeight
            // dlog("elOffsetTop=$elOffsetTop; elOffsetHeight=$elOffsetHeight; containerScrollTop=$containerScrollTop; containerOffsetHeight=$containerOffsetHeight")
            val dy = elOffsetTop.toDouble() - containerScrollTop
            if (dy >= containerOffsetHeight.toDouble())
                container.scrollTop = elOffsetTop.toDouble() - containerOffsetHeight + elOffsetHeight
            else if (dy < 0)
                container.scrollTop = bang(byid0(el.elementID)).offsetTop.toDouble()
        }
    }

    private fun onInputKeyUp(e: KeyboardEvent) {
        if (input.value != prevInputValueSeenInKeyUpHandler) {
            prevInputValueSeenInKeyUpHandler = input.value
            if (e.keyCode !in setOf(38, 40, 13)) {
                syncTreeWithInput()
            }
        }
    }

    private fun onInputFocus(e: ReactEvent) {
        currentFocusable?.setFocused(true)
    }

    private fun onInputBlur(e: ReactEvent) {
        // dlog("onInputBlur")
        currentFocusable?.setFocused(false)
    }

    private fun syncTreeWithInput() {
        val pattern = input.value.trim().toLowerCase()
        // dlog("pattern = [$pattern]")
        if (pattern.isBlank()) {
            selectorPlace.setContent(treeControl)
        } else {
            selectorPlace.setContent(makeListControl(pattern))
        }
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
                              onKeyUp = this::onInputKeyUp,
                              onFocus = this::onInputFocus,
                              onBlur = this::onInputBlur)
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

                    filteredItems += object:Focusable() {
                        override fun select() {
                            selectCategory(cat)
                        }

                        override fun render(): ToReactElementable {
                            return renderItemTitle(
                                cat,
                                id = elementID,
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

        focusables.clear()
        currentFocusable = null

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
                    focusables += item
                }
                if (indexInItems + 1 <= filteredItems.lastIndex) {
                    var showMorePlace by notNullOnce<Placeholder>()

                    val showMoreItem = object:Focusable() {
                        override fun select() {
                            currentFocusable?.setFocused(false)
                            val newSelectableIdx = focusables.lastIndex
                            focusables.remove(focusables.last())
                            showMorePlace.setContent(renderChunk(from = indexInItems + 1))
                            currentFocusable = focusables[newSelectableIdx]
                            if (input.isFocused()) {
                                bang(currentFocusable).setFocused(true)
                            }

                            async {
                                scrollElementToBottomGradually(byid(itemContainerID))
                                TestGlobal.animationDoneLock.resumeTestFromSut()
                            }
                        }

                        override fun render(): ToReactElementable {
                            return kdiv(
                                Attrs(
                                    id = elementID,
                                    className = when {
                                        selected -> css.DocumentCategoryField.showMoreFocused
                                        else -> css.DocumentCategoryField.showMore
                                    },
                                    onClick = {
                                        select()
                                    })){o->
                                o- t("Show more...", "Показать еще...")
                            }
                        }
                    }

                    showMorePlace = Placeholder(showMoreItem)
                    o- showMorePlace
                    focusables += showMoreItem
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
        return kdiv(id = itemContainerID, className = css.DocumentCategoryField.itemContainer) {build(it)}
    }

    private fun renderItemTitle(cat: UADocumentCategoryRTO, title: String, underline: Boolean): ElementBuilder {
        return renderItemTitle(cat, span(title), underline, css.DocumentCategoryField.item)
    }

    private fun renderItemTitle(cat: UADocumentCategoryRTO, title: ToReactElementable, underline: Boolean, className: String, id: String? = null): ElementBuilder {
        return kdiv(
            Attrs(id = id,
                  className = className,
                  onClick = {
                      selectCategory(cat)
                  }),
            style = when {
                underline -> Style(borderBottom = "1px solid ${Color.GRAY_500}")
                else -> Style()
            }
        ){o->
            o- title
        }
    }

    private fun selectCategory(cat: UADocumentCategoryRTO) {
        value = cat
        enterViewMode()
    }

    private fun makeTreeItemControl(cat: UADocumentCategoryRTO): ToReactElementable {
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
    Selena.instance(field).testSetInputValue(value)
}

suspend fun selenaSendSpecialKey(field: TestRef<DocumentCategoryFieldSpec>, keyCode: Int) {
    Selena.instance(field).testSendSpecialKey(keyCode)
}
























