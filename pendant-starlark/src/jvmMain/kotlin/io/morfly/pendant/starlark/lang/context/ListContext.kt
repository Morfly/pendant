package io.morfly.pendant.starlark.lang.context

import io.morfly.pendant.starlark.element.Expression
import io.morfly.pendant.starlark.element.Reference
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

    private val addedItems = mutableListOf<T>()
    private var comparator: Comparator<T>? = null

    override val items: MutableList<Expression>
        get() = sortedItems()

    operator fun T.unaryPlus() {
        item(this)
    }

    fun item(item: T) {
        addedItems += item
    }

    fun _sortedWith(comparator: Comparator<T>?) {
        this.comparator = comparator
    }

    private fun sortedItems(): MutableList<Expression> {
        val comparator = comparator ?: return addedItems.map(::Expression).toMutableList()

        val (literals, expressions) = addedItems.partition { it !is Expression }
        val sortedLiterals = literals.sortedWith(comparator)

        val (references, otherExpressions) = expressions.partition { it is Reference }
        val sortedReferences = references.sortedBy { (it as Reference).name }

        return mutableListOf<T>().also {
            it += sortedLiterals
            it += sortedReferences
            it += otherExpressions
        }.map(::Expression).toMutableList()
    }
}
