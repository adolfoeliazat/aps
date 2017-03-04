@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.front.*
import into.kommon.*
import org.w3c.dom.events.KeyboardEvent
import kotlin.js.Json
import kotlin.properties.Delegates.notNull

class SelenaPickerKey(override val fqn: String) : Fucker(), FQNed

class SelenaPicker(val key: SelenaPickerKey, val selectCategory: (UADocumentCategoryRTO) -> Unit) {
    // TODO:vgrechka If shit is pasted into search box without keyboard, search is not updated

    private var rootCategory: UADocumentCategoryRTO? = null
    var input by notNull<Input>()
    private var selectorPlace by notNull<Placeholder>()
    private var treeControl by notNull<ToReactElementable>()
    private val focusables = mutableListOf<Focusable>()
    private var currentFocusable: Focusable? = null
    private var prevInputValueSeenInKeyUpHandler: String? = null
    private val itemContainerID = puid()
    private var treeMode = true

    companion object {
        val instances = mutableMapOf<SelenaPickerKey, SelenaPicker>()

        fun instance(key: SelenaPickerKey): SelenaPicker {
            return instances[key] ?: bitch("No Selena keyed `${key.fqn}`")
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

    suspend fun fuck1(place: Placeholder) {
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
                        fuck1(place)
                    }
                }
            })
            TestGlobal.shitDoneLock.resumeTestFromSut()
        } else {
            treeControl = makeTreeControl()
            selectorPlace = Placeholder(treeControl)

            place.setContent(object:Control2() {
                override fun render(): ToReactElementable {
                    return kdiv{o->
                        input = Input(autoFocus = true,
                                      tabIndex = 0, // Needed for autoFocus to have effect
                                      onKeyDown = this@SelenaPicker::onInputKeyDown,
                                      onKeyUp = this@SelenaPicker::onInputKeyUp,
                                      onFocus = this@SelenaPicker::onInputFocus,
                                      onBlur = this@SelenaPicker::onInputBlur)
                        o- input
                        o- selectorPlace
                    }
                }

                override fun componentDidMount() {
                    instances[key] = this@SelenaPicker
                }

                override fun componentWillUnmount() {
                    instances.remove(key)
                }
            })
        }
    }

    private fun makeTreeControl(): ToReactElementable {
        return shit{o->
            for (cat in bang(rootCategory).children) {
                o- makeTreeItemControl(cat)
            }
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

    fun syncTreeWithInput() {
        val pattern = input.value.trim().toLowerCase()
        // dlog("pattern = [$pattern]")
        treeMode = pattern.isBlank()
        if (treeMode) {
            selectorPlace.setContent(treeControl)
        } else {
            selectorPlace.setContent(makeListControl(pattern))
        }
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
        if (treeMode || focusables.isEmpty()) return

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
                                    selected -> css.selena.itemFocused
                                    else -> css.selena.item
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
            return div(t("Fucking nothing", "Нихера не найдено"), className = css.selena.nothing)

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
                                        selected -> css.selena.showMoreFocused
                                        else -> css.selena.showMore
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

    private fun shit(build: (ElementBuilder) -> Unit): ToReactElementable {
        return kdiv(id = itemContainerID, className = css.selena.itemContainer) {build(it)}
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

    private fun renderItemTitle(cat: UADocumentCategoryRTO, title: String, underline: Boolean): ElementBuilder {
        return renderItemTitle(cat, span(title), underline, css.selena.item)
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

    suspend fun testSetInputValue(s: String) {
        prevInputValueSeenInKeyUpHandler = s
        input.testSetValue(s)
        syncTreeWithInput()
    }

    fun testSendSpecialKey(keyCode: Int) {
        onInputKeyDown(fakeKeyboardEvent(keyCode = keyCode))
    }

}

class Selena(initialValue: UADocumentCategoryRTO, pickerKey: SelenaPickerKey) : Control2() {
    private var value = initialValue
    private val place = Placeholder()
    private val picker = SelenaPicker(pickerKey, this::selectCategory)

    init {
        enterViewMode()
    }

    fun getValue() = value

    suspend fun testSetInputValue(s: String) {
        picker.testSetInputValue(s)
    }

    suspend fun testSendSpecialKey(keyCode: Int) {
        picker.testSendSpecialKey(keyCode)
    }

    override fun render(): ToReactElementable {
        return place
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
        picker.fuck1(place)
    }



    private fun selectCategory(cat: UADocumentCategoryRTO) {
        value = cat
        enterViewMode()
    }

}

@Front class DocumentCategoryField(container: RequestMatumba, val spec: DocumentCategoryFieldSpec): FormFieldFront(container, spec.name) {
    override var disabled = false
    override fun focus() {}
    override var error: String? = null

    private var selena by notNullOnce<Selena>()

    fun setValue(value: UADocumentCategoryRTO) {
        check(include){"Attempt to write front DocumentCategoryField $name, which is not included    85452dad-ad55-4d63-b7ae-3811ddcc276a"}
        selena = Selena(initialValue = value, pickerKey = FieldSpecToCtrlKey[spec])
        Globus.populatedFields += this
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

class SelenaTester private constructor (val pickerKey: SelenaPickerKey, val aid: String) {
    var aidx = 1

    companion object {
        fun new(field: TestRef<DocumentCategoryFieldSpec>, aid: String) = SelenaTester(FieldSpecToCtrlKey[field.it], aid)
        fun new(field: TestRef<DocumentCategorySetFieldSpec>, aid: String) = SelenaTester(FieldSpecToCtrlKey[field.it], aid)
    }

    fun nextAID() = "$aid--${aidx++}"

    suspend fun assert() {
        assertScreenHTML(aid = nextAID())
    }

    suspend fun ellipsisButton() {
        seq.halfway_done({buttonClick(buttons.chooseDocumentCategory_testRef)}, nextAID())
    }

    suspend fun searchValue(x: String) {
        SelenaPicker.instance(pickerKey).testSetInputValue(x)
        assert()
    }

    suspend fun specialKey(keyCode: Int, times: Int = 1) {
        for (i in 1..times) {
            sendSpecialKey(keyCode)
            sleep(50)
            assert()
        }
    }

    suspend fun specialKeyThenAnimation(keyCode: Int) {
        step({sendSpecialKey(keyCode)}, TestGlobal.animationDoneLock, nextAID())
    }

    private suspend fun sendSpecialKey(keyCode: Int) {
        SelenaPicker.instance(pickerKey).testSendSpecialKey(keyCode)
    }
}























