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

import io.morfly.pendant.starlark.element.Assignment
import io.morfly.pendant.starlark.element.DictionaryExpression
import io.morfly.pendant.starlark.element.DictionaryReference
import io.morfly.pendant.starlark.element.Expression
import io.morfly.pendant.starlark.element.ListExpression
import io.morfly.pendant.starlark.element.ListReference
import io.morfly.pendant.starlark.element.NumberLiteral
import io.morfly.pendant.starlark.element.NumberReference
import io.morfly.pendant.starlark.element.StringLiteral
import io.morfly.pendant.starlark.element.StringReference
import io.morfly.pendant.starlark.element.TupleExpression
import io.morfly.pendant.starlark.element.TupleReference
import io.morfly.pendant.starlark.lang.InternalPendantApi
import io.morfly.pendant.starlark.lang.LanguageFeature
import io.morfly.pendant.starlark.lang.ModifiersHolder
import io.morfly.pendant.starlark.lang.StatementsHolder
import io.morfly.pendant.starlark.lang.context.DictionaryContext
import io.morfly.pendant.starlark.lang.invokeModifiers
import io.morfly.pendant.starlark.lang.type.Key
import io.morfly.pendant.starlark.lang.type.NumberType
import io.morfly.pendant.starlark.lang.type.StringType
import io.morfly.pendant.starlark.lang.type.TupleType
import io.morfly.pendant.starlark.lang.type.Value


/**
 * Feature that enables assigning new values to the existing variable.
 */
internal interface ReassignmentsFeature : LanguageFeature,
    StatementsHolder,
    ModifiersHolder {

    /**
     * String assignment operator.
     */
    infix fun StringReference.`=`(value: StringType?): _StringExpressionAccumulator<Assignment> {
        val assignment = Assignment(name, value = Expression(value, ::StringLiteral))
        statements += assignment
        return _StringExpressionAccumulator(assignment)
    }

    /**
     * Number assignment operator.
     */
    infix fun NumberReference.`=`(value: NumberType?): _NumberExpressionAccumulator<Assignment> {
        val assignment = Assignment(name, value = Expression(value, ::NumberLiteral))
        statements += assignment
        return _NumberExpressionAccumulator(assignment)
    }

    /**
     * List assignment operator.
     */
    infix fun <T> ListReference<T>.`=`(value: List<T>?): _ListExpressionAccumulator<T, Assignment> {
        val assignment = Assignment(name, value = Expression(value, ::ListExpression))
        statements += assignment
        return _ListExpressionAccumulator(assignment)
    }

    /**
     * List assignment operator.
     */
    infix fun TupleReference.`=`(value: TupleType?): _TupleExpressionAccumulator<Assignment> {
        val assignment = Assignment(name, value = Expression(value, ::TupleExpression))
        statements += assignment
        return _TupleExpressionAccumulator(assignment)
    }

    /**
     * Dictionary assignment operator.
     */
    infix fun <K : Key, V : Value> DictionaryReference<K, V>.`=`(
        value: Map<Key, Value>?
    ): _DictionaryExpressionAccumulator<K, V, Assignment> {
        val assignment = Assignment(name, value = Expression(value, ::DictionaryExpression))
        statements += assignment
        return _DictionaryExpressionAccumulator(assignment)
    }

    /**
     * Dictionary assignment operator.
     */
    @OptIn(InternalPendantApi::class)
    infix fun <K : Key, V : Value> DictionaryReference<K, V>.`=`(
        body: DictionaryContext.() -> Unit
    ): _DictionaryExpressionAccumulator<K, V, Assignment> {
        val dictionaryContext = DictionaryContext(modifiers).apply(body)
        invokeModifiers(dictionaryContext)
        val value = dictionaryContext.kwargs
        val assignment = Assignment(name, value = DictionaryExpression(value))
        statements += assignment
        return _DictionaryExpressionAccumulator(assignment)
    }
}