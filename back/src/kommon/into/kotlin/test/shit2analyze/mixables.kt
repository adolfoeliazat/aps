/*
 * Into Kotlin
 *
 * (C) Copyright 2016 Vladimir Grechka
 */

package kommon.into.kotlin.test.shit2analyze

@MixableType
data class Attrs(
    val tame: String? = null,
    val tamy: String? = null,
    val shame: String? = null,
    val shamy: String? = null,
    val tamyShamy: String? = null,
    val controlTypeName: String? = null,
    val id: String? = null,
    val tattrs: Json? = null,
    val noStateContributions: Boolean? = null,
    val className: String? = null,
    val onClick: ((MouseEvent) -> Unit)? = null,
    val onClicka: ((MouseEvent) -> Promise<Unit>)? = null,
    val onMouseEnter: ((MouseEvent) -> Unit)? = null,
    val onMouseEntera: ((MouseEvent) -> Promise<Unit>)? = null,
    val onMouseLeave: ((MouseEvent) -> Unit)? = null,
    val onMouseLeava: ((MouseEvent) -> Promise<Unit>)? = null
)

@MixableType
data class Style(
    var marginTop: Any? = null,
    var marginBottom: Any? = null,
    var paddingBottom: Any? = null,
    var padding: Any? = null,
    var color: Any? = null,
    var backgroundColor: Any? = null,
    var borderBottom: String? = null,
    var textAlign: String? = null,
    var fontWeight: String? = null,
    var display: String? = null,
    var justifyContent: String? = null
)

@MixableType
data class LinkParams(
    val content: ToReactElementable? = null,
    val title: String? = null
)

