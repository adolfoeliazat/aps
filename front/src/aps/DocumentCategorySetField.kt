package aps

import aps.front.*
import into.kommon.*
import kotlin.js.Json
import kotlin.properties.Delegates.notNull

@Front class DocumentCategorySetField(container: RequestMatumba, val spec: DocumentCategorySetFieldSpec): FormFieldFront(container, spec.name) {
    override var disabled = false
    override fun focus() {}
    override var error: String? = null

    private var value by notNull<DocumentCategorySetFieldValue>()
    private val fuckingPlace = Placeholder()
    private var selena by notNullOnce<Selena>()
    private val allCheck = Checkbox(
        key = checkboxes.allCategories,
        titleControl = hor3{o->
            o- t("TOTE", "В любых, я умный шописец")
            o- span(t("TOTE", "(Будет приходить куча спама ${const.text.symbols.emdash} по всем заказам)"), Style(fontStyle = "italic"))
        },
        onChange = {
            fuck()
        })

    private val picker = SelenaPicker(FieldSpecToCtrlKey[spec], this::selectCategory)
    private val pickerPlace by lazy {
        Placeholder().also {async{picker.fuck1(it)}}
    }

    private val pickedCats = mutableListOf<UADocumentCategoryRTO>()
    private val pickedCatsControl = Control2.from {me->
        kdiv(marginTop = if (pickedCats.isNotEmpty()) "1rem" else null, marginBottom = "1rem"){o->
            o- hor3(style = Style(flexWrap = "wrap"), gapSide = HorGapSide.RIGHT){o->
                for (cat in pickedCats) {
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
                                       pickedCats -= cat
                                       me.update()
                                   })
                        }
                    }
                }
            }
        }
    }

    private fun selectCategory(cat: UADocumentCategoryRTO) {
        if (cat !in pickedCats) {
            pickedCats += cat
            pickedCatsControl.update()
        }
    }

    private fun fuck() {
        fuckingPlace.setContent(
            when {
                allCheck.getValue() -> NOTRE
                else -> kdiv{o->
                    o- pickedCatsControl
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
        imf("389f87fd-36d3-4ad9-b130-02936b633341")
//        json[name] = selena.getValue().id.toString()
    }
}

class DocumentCategorySetFieldTester(aid: String) {
    val assert = SerialAsserter(aid)

    suspend fun clickDelete(subscript: Long) {
        tkic.mouseEnterClick(kics.delete_testRef, subscript)
        assert()
    }
}






















