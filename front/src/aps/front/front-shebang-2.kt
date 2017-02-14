package aps.front

import aps.*
import kotlin.reflect.KFunction1

object FieldSpecToCtrlKey {
    private val map = mutableMapOf<FieldSpec, Any>()

    operator fun get(spec: TextFieldSpec) = get(spec, ::InputKey)
    operator fun get(spec: IntFieldSpec) = get(spec, ::InputKey)
    operator fun <E> get(spec: SelectFieldSpec<E>) where E : Enum<E>, E : Titled = get(spec, ::SelectKey)

    private operator fun <K : Any> get(spec: FieldSpec,
                                       makeKey: KFunction1<@ParameterName(name = "fqn") String, K>): K =
        cast(map.getOrPut(spec) {makeKey(spec.name)})
}

