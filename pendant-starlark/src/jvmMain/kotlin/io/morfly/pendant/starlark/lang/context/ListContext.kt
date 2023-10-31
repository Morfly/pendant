package io.morfly.pendant.starlark.lang.context

import io.morfly.pendant.starlark.element.Expression
import io.morfly.pendant.starlark.lang.CommonExpressionsLibrary
import io.morfly.pendant.starlark.lang.ItemsHolder
import io.morfly.pendant.starlark.lang.LanguageScope
import io.morfly.pendant.starlark.lang.ModifierCollection
import io.morfly.pendant.starlark.lang.feature.BinaryPlusFeature
import io.morfly.pendant.starlark.lang.feature.BinaryPlusTransformationsFeature
import io.morfly.pendant.starlark.lang.feature.CollectionsFeature
import io.morfly.pendant.starlark.lang.feature.StringExtensionsFeature

@LanguageScope
class ListContext<T>(override val modifiers: ModifierCollection) : Context(),
    CommonExpressionsLibrary,
    ItemsHolder,
    BinaryPlusFeature,
    BinaryPlusTransformationsFeature,
    CollectionsFeature,
    StringExtensionsFeature {

    override val items = mutableListOf<Expression>()

    operator fun T.unaryPlus() {
        item(this)
    }

    fun item(item: T) {
        items += Expression(item)
    }
}
