package io.morfly.pendant.starlark.lang.feature

import io.morfly.pendant.starlark.element.Expression
import io.morfly.pendant.starlark.element.ListBinaryOperation
import io.morfly.pendant.starlark.element.ListExpression
import io.morfly.pendant.starlark.lang.type.ListType
import io.morfly.pendant.starlark.lang.type.StringType

interface BinaryPlusTransformationsFeature {

    fun <T> ListType<T>.unwrapped(): ListType<T> {
        if (this !is ListBinaryOperation<T>) return this

        val expressions = mutableListOf<Expression>()
        val items = mutableListOf<T>()

        fun traverse(expression: Expression) {
            @Suppress("UNCHECKED_CAST")
            when (expression) {
                is ListExpression<*> -> items += expression as ListType<T>
                is ListBinaryOperation<*> -> {
                    traverse(expression.left)
                    traverse(expression.right)
                }

                else -> expressions += expression
            }
        }
        traverse(this)

        val listExpression = ListExpression(items)
        if (expressions.isEmpty()) return listExpression

        var lastExpression: Expression = listExpression
        for (expression in expressions.asReversed()) {
            lastExpression = ListBinaryOperation<StringType>(
                left = expression,
                operator = operator,
                right = lastExpression
            )
        }
        @Suppress("UNCHECKED_CAST")
        return lastExpression as ListType<T>
    }
}
