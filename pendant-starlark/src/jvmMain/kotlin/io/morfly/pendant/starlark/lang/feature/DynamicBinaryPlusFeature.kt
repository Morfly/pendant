/*
 * Copyright 2023 Pavlo Stavytskyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("FunctionName")

package io.morfly.pendant.starlark.lang.feature

import io.morfly.pendant.starlark.element.AnyBinaryOperation
import io.morfly.pendant.starlark.element.BinaryOperator.PLUS
import io.morfly.pendant.starlark.element.DictionaryBinaryOperation
import io.morfly.pendant.starlark.element.DictionaryExpression
import io.morfly.pendant.starlark.element.Element
import io.morfly.pendant.starlark.element.Expression
import io.morfly.pendant.starlark.element.ListBinaryOperation
import io.morfly.pendant.starlark.element.ListExpression
import io.morfly.pendant.starlark.element.NumberBinaryOperation
import io.morfly.pendant.starlark.element.NumberLiteral
import io.morfly.pendant.starlark.element.StringBinaryOperation
import io.morfly.pendant.starlark.element.StringLiteral
import io.morfly.pendant.starlark.element.TupleBinaryOperation
import io.morfly.pendant.starlark.element.TupleExpression
import io.morfly.pendant.starlark.lang.InternalPendantApi
import io.morfly.pendant.starlark.lang.LanguageFeature
import io.morfly.pendant.starlark.lang.ModifiersHolder
import io.morfly.pendant.starlark.lang.context.DictionaryContext
import io.morfly.pendant.starlark.lang.invokeModifiers
import io.morfly.pendant.starlark.lang.type.Key
import io.morfly.pendant.starlark.lang.type.NumberType
import io.morfly.pendant.starlark.lang.type.StringType
import io.morfly.pendant.starlark.lang.type.TupleType
import io.morfly.pendant.starlark.lang.type.Value


/**
 * Feature that enables concatenations that follow a `=` operator with backticks.
 *
 * Example:
 * ```
 * function1 {
 *     arg = "value1" `+` "value2" `+` "value3"
 *         ^           ^            ^
 *        (=)      (first +)    (second +)
 * }
 * ```
 * In the first example an argument is being passed using regular = operator. The order of operations is the following:
 * - first +
 * - second +
 * - =
 *
 * ```
 * function2 {
 *     arg `=` "value1" `+` "value2" `+` "value3"
 *          ^            ^            ^
 *         (=)       (first +)    (second +)
 * }
 * ```
 * However, in the second example a dynamic `=` operator is being used (with backticks). Since it's a regular Kotlin
 * function, the order of operations is the following:
 * - =
 * - first +
 * - second +
 *
 * This feature makes sure the entire concatenation is registered as an argument.
 */
internal interface DynamicBinaryPlusFeature : LanguageFeature,
    ModifiersHolder {

    /**
     * String concatenation operator.
     */
    infix fun <E : Element> _StringExpressionAccumulator<E>.`+`(
        other: StringType?
    ): _StringExpressionAccumulator<E> {
        holder.value = StringBinaryOperation(
            left = holder.value,
            operator = PLUS,
            right = Expression(other, ::StringLiteral)
        )
        return this
    }

    /**
     * String concatenation operator.
     */
    infix fun <E : Element> _NumberExpressionAccumulator<E>.`+`(
        other: NumberType?
    ): _NumberExpressionAccumulator<E> {
        holder.value = NumberBinaryOperation(
            left = holder.value,
            operator = PLUS,
            right = Expression(other, ::NumberLiteral)
        )
        return this
    }

    /**
     * List concatenation operator.
     */
    infix fun <T, E : Element> _ListExpressionAccumulator<T, E>.`+`(
        other: List<T>?
    ): _ListExpressionAccumulator<T, E> {
        holder.value = ListBinaryOperation<T>(
            left = holder.value,
            operator = PLUS,
            right = Expression(other, ::ListExpression)
        )
        return this
    }

    /**
     * Tuple concatenation operator.
     */
    infix fun <E : Element> _TupleExpressionAccumulator<E>.`+`(other: TupleType?): _TupleExpressionAccumulator<E> {
        holder.value = TupleBinaryOperation(
            left = holder.value,
            operator = PLUS,
            right = Expression(other, ::TupleExpression)
        )
        return this
    }

    /**
     * Dictionary concatenation operator.
     */
    infix fun <K, V : Value, E : Element> _DictionaryExpressionAccumulator<K, V, E>.`+`(
        other: Map<*, Value>?
    ): _DictionaryExpressionAccumulator<K, V, E> {
        holder.value = DictionaryBinaryOperation<Key, Value>(
            left = holder.value,
            operator = PLUS,
            right = Expression(other, ::DictionaryExpression)
        )
        return this
    }

    /**
     * Dictionary concatenation operator.
     */
    @OptIn(InternalPendantApi::class)
    infix fun <K, V : Value, E : Element> _DictionaryExpressionAccumulator<K, V, E>.`+`(
        body: DictionaryContext.() -> Unit
    ): _DictionaryExpressionAccumulator<K, V, E> {
        val dictionaryContext = DictionaryContext(modifiers).apply(body)
        invokeModifiers(dictionaryContext)
        holder.value = DictionaryBinaryOperation<Key, Value>(
            left = holder.value,
            operator = PLUS,
            right = DictionaryExpression(dictionaryContext.kwargs)
        )
        return this
    }

    /**
     * Concatenation operator for values for with type does not matter.
     */
    infix fun <E : Element> _AnyExpressionAccumulator<E>.`+`(other: Any?): _AnyExpressionAccumulator<E> {
        holder.value = AnyBinaryOperation(
            left = holder.value,
            operator = PLUS,
            right = Expression(other)
        )
        return this
    }
}