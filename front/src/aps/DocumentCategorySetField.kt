package aps

import aps.front.*
import kotlin.js.Json
import kotlin.properties.Delegates.notNull

// TODO:vgrechka Should select at least one category if  "All" is unchecked

@Front class DocumentCategorySetField(container: RequestMatumba, val spec: DocumentCategorySetFieldSpec): FormFieldFront(container, spec.name) {
    private var value by notNull<DocumentCategorySetFieldValue>()
    private val fuckingPlace = Placeholder()
    private val allCheck = Checkbox(
        key = checkboxes.allCategories,
        titleControl = hor3{o->
            o- fconst.text.inAnyCategory
            o- span(t("TOTE", "(Будет приходить куча спама ${const.text.symbols.emdash} по всем заказам)"), Style(fontStyle = "italic"))
        },
        onChange = {
            setValue(when (it.getValue()) {
                true -> DocumentCategorySetFieldValue.All()
                else -> DocumentCategorySetFieldValue.Specific(listOf())
            })
        })

    private val picker = SelenaPicker(FieldSpecToCtrlKey[spec], this::selectCategory)

    private val pickedCatsControl = Control2.from {me->
        val value = this.value as DocumentCategorySetFieldValue.Specific
        kdiv(marginTop = if (value.categories.isNotEmpty()) "1rem" else null, marginBottom = "1rem"){o->
            o- hor3(style = Style(flexWrap = "wrap"), gapSide = HorGapSide.RIGHT){o->
                for (cat in value.categories) {
                    o- kdiv{o->
                        o- hor1{o->
                            var labelStyle = Style()
                            val titleControl = Control2.from {span(cat.pathTitle, labelStyle)}
                            o- titleControl

                            o- kic(key = SubscriptKicKey(kics.delete, cat.id),
                                   className = "${fa.trash} ${css.selena.pickedItemActionIcon}",
                                   onMouseEnter = {
                                       labelStyle = Style(textDecoration = "line-through")
                                       titleControl.update()
                                   },
                                   onMouseLeave = {
                                       labelStyle = Style()
                                       titleControl.update()
                                   },
                                   onClicka = {
                                       this.value = DocumentCategorySetFieldValue.Specific(value.categories - cat)
                                       me.update()
                                   })
                        }
                    }
                }
            }
        }
    }

    private fun selectCategory(cat: UADocumentCategoryRTO) {
        val value = this.value as DocumentCategorySetFieldValue.Specific
        if (cat !in value.categories) {
            this.value = DocumentCategorySetFieldValue.Specific(value.categories + cat)
            pickedCatsControl.update()
        }
    }

    private fun fuck() {
        fuckingPlace.setContent(
            when (value) {
                is DocumentCategorySetFieldValue.All -> NOTRE
                is DocumentCategorySetFieldValue.Specific -> kdiv{o->
                    o- pickedCatsControl

                    val pickerPlace = Placeholder().also {async{picker.fuck1(it)}}
                    o- pickerPlace
                }
            })
    }

    fun setValue(value: DocumentCategorySetFieldValue) {
        check(include){"Attempt to write front DocumentCategorySetField $name, which is not included    1412eeea-fa39-4a15-a793-13bb8bb2c1a7"}
        this.value = value
        allCheck.setValue(value is DocumentCategorySetFieldValue.All)
        fuck()
        Globus.populatedFields += this
    }

    override fun render() =
        kdiv(className = "form-group"){o->
            o- label(spec.title)
            o- kdiv{o->
                o- allCheck
            }
            o- fuckingPlace
        }.toReactElement()

    override fun populateRemote(json: Json): Promisoid<Unit> = async {
        val value = this.value
        json[name] = when (value) {
            is DocumentCategorySetFieldValue.All -> DocumentCategorySetFieldUtils.allValue
            is DocumentCategorySetFieldValue.Specific -> value.categories.map {it.id.toString()}.toTypedArray()
        }
    }

    override var disabled = false
    override fun focus() {}
    override var error: String? = null

}

class DocumentCategorySetFieldTester(aid: String) {
    val assert = SerialAsserter(aid)

    suspend fun clickDelete(subscript: Long) {
        tkic.mouseEnterClick(kics.delete_testRef, subscript)
        assert()
    }

    suspend fun setAllCheck(value: Boolean, expectingLongOperation: Boolean = false) {
        val action: SFUnit = {tcheckbox.setValue(checkboxes.allCategories_testRef, value)}
        if (expectingLongOperation) {
            seq.halfway_done(action, aid = assert.nextAID())
        } else {
            action()
            assert()
        }
    }
}






















