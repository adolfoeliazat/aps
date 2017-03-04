package aps.front

import aps.*

object FieldSpecToCtrlKey {
    private val map = mutableMapOf<FieldSpec, Any>()

    operator fun get(spec: TextFieldSpec) = get(spec, ::InputKey)
    operator fun get(spec: IntFieldSpec) = get(spec, ::InputKey)
    operator fun get(spec: CheckboxFieldSpec) = get(spec, ::CheckboxKey)
    operator fun get(spec: DocumentCategoryFieldSpec) = get(spec, ::SelenaPickerKey)
    operator fun get(spec: DocumentCategorySetFieldSpec) = get(spec, ::SelenaPickerKey)
    operator fun <E> get(spec: SelectFieldSpec<E>) where E : Enum<E>, E : Titled = get(spec, {SelectKey<E>(it)})

    private operator fun <K : Any> get(spec: FieldSpec, makeKey: (String) -> K): K =
        cast(map.getOrPut(spec) {makeKey(spec.name)})
}
















