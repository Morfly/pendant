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

import io.morfly.pendant.starlark.element.BinaryOperator.PLUS
import io.morfly.pendant.starlark.element.BooleanBinaryOperation
import io.morfly.pendant.starlark.element.BooleanLiteral
import io.morfly.pendant.starlark.element.DictionaryBinaryOperation
import io.morfly.pendant.starlark.element.DictionaryExpression
import io.morfly.pendant.starlark.element.Expression
import io.morfly.pendant.starlark.element.ListBinaryOperation
import io.morfly.pendant.starlark.element.ListExpression
import io.morfly.pendant.starlark.element.NumberBinaryOperation
import io.morfly.pendant.starlark.element.NumberLiteral
import io.morfly.pendant.starlark.element.StringBinaryOperation
import io.morfly.pendant.starlark.element.StringLiteral
import io.morfly.pendant.starlark.lang.InternalPendantApi
import io.morfly.pendant.starlark.lang.LanguageFeature
import io.morfly.pendant.starlark.lang.ModifiersHolder
import io.morfly.pendant.starlark.lang.context.DictionaryContext
import io.morfly.pendant.starlark.lang.invokeModifiers
import io.morfly.pendant.starlark.lang.type.BooleanType
import io.morfly.pendant.starlark.lang.type.Key
import io.morfly.pendant.starlark.lang.type.NumberType
import io.morfly.pendant.starlark.lang.type.StringType
import io.morfly.pendant.starlark.lang.type.Value


/**
 * Allows using concatenationss in the code generator.
 */
internal interface BinaryPlusFeature : LanguageFeature,
    ModifiersHolder {

    /**
     * Operator for string concatenations.
     *
     * Generated Starlark code:
     * "value1" + "value2"
     *
     * Kotlin code generator program:
     * "value1" `+` "value2"
     */
    infix fun StringType?.`+`(other: StringType?): StringType =
        StringBinaryOperation(
            left = Expression(this, ::StringLiteral),
            operator = PLUS,
            right = Expression(other, ::StringLiteral)
        )

    /**
     * Operator for number concatenations.
     *
     * Generated Starlark code:
     * 1 + 2
     *
     * Kotlin code generator program:
     * 1 `+` 2
     */
    infix fun NumberType?.`+`(other: NumberType?): NumberType =
        NumberBinaryOperation(
            left = Expression(this, ::NumberLiteral),
            operator = PLUS,
            right = Expression(other, ::NumberLiteral)
        )

    /**
     * Operator for boolean concatenations.
     *
     * Generated Starlark code:
     * "value1" + "value2"
     *
     * Kotlin code generator program:
     * "value1" `+` "value2"
     */
    infix fun BooleanType?.`+`(other: BooleanType?): BooleanType =
        BooleanBinaryOperation(
            left = Expression(this, ::BooleanLiteral),
            operator = PLUS,
            right = Expression(other, ::BooleanLiteral)
        )

    /**
     * Operator for list or tuple concatenations.
     *
     * Generated Starlark code:
     * [1, 2] + [3]
     *
     * Kotlin code generator program:
     * "value1" `+` "value2"
     */
    infix fun <T> List<T>?.`+`(other: List<T>?): List<T> =
        ListBinaryOperation(
            left = Expression(this, ::ListExpression),
            operator = PLUS,
            right = Expression(other, ::ListExpression)
        )

    /**
     * Operator for dictionary concatenations.
     *
     * Generated Starlark code:
     * {"key1": "value1"} + {"key2": "value2"}
     *
     * Kotlin code generator program:
     * dict { "key1" to "value1" } `+` dict { "key2" to "value2" }
     */
    infix fun Map<*, Value>?.`+`(other: Map<*, Value>?): Map<Key, Value> =
        DictionaryBinaryOperation(
            left = Expression(this, ::DictionaryExpression),
            operator = PLUS,
            right = Expression(other, ::DictionaryExpression)
        )

    /**
     * A shortened form of an operator for dictionary concatenations.
     *
     * Generated Starlark code:
     * [1, 2] + [3]
     *
     * Generated Starlark code:
     * {"key1": "value1"} + {"key2": "value2"}
     *
     * Kotlin code generator program:
     * dict { "key1" to "value1" } `+` { "key2" to "value2" }
     */
    @OptIn(InternalPendantApi::class)
    infix fun Map<*, Value>?.`+`(body: DictionaryContext.() -> Unit): Map<Key, Value> {
        val dictionaryContext = DictionaryContext(modifiers).apply(body)
        invokeModifiers(dictionaryContext)
        return DictionaryBinaryOperation(
            left = Expression(this, ::DictionaryExpression),
            operator = PLUS,
            right = DictionaryExpression(dictionaryContext.kwargs)
        )
    }
}