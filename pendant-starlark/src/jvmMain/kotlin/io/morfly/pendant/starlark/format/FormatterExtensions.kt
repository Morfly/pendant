package io.morfly.pendant.starlark.format

import io.morfly.pendant.starlark.element.BooleanLiteral
import io.morfly.pendant.starlark.element.Expression
import io.morfly.pendant.starlark.element.FloatLiteral
import io.morfly.pendant.starlark.element.FunctionCall
import io.morfly.pendant.starlark.element.IntegerLiteral
import io.morfly.pendant.starlark.element.ListExpression
import io.morfly.pendant.starlark.element.Reference

internal const val SINGLE_LINE_LENGTH_THRESHOLD = 5

internal fun List<*>.isSingleOrEmpty(): Boolean =
    isEmpty() || size == 1

internal fun ListExpression<*>.shouldFormatSingleLine(): Boolean =
    value.isSingleOrEmpty() || value.shouldFormatSingleLine()

internal fun FunctionCall.shouldFormatSingleLine(): Boolean =
    args.isSingleOrEmpty() || args.map { it.value }.shouldFormatSingleLine()

internal fun List<Expression>.shouldFormatSingleLine(): Boolean =
    isBelowSizeThreshold() && all {
        when (it) {
            is IntegerLiteral,
            is FloatLiteral,
            is BooleanLiteral -> true

            is Reference -> it.name.isBelowSizeThreshold()

            else -> false
        }
    }

internal fun Collection<*>.isBelowSizeThreshold(): Boolean =
    size <= SINGLE_LINE_LENGTH_THRESHOLD

internal fun CharSequence.isBelowSizeThreshold(): Boolean =
    length <= SINGLE_LINE_LENGTH_THRESHOLD
