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

@file:Suppress("FunctionName", "unused")

package io.morfly.pendant.starlark.lang.feature

import io.morfly.pendant.starlark.element.Assignment
import io.morfly.pendant.starlark.element.BooleanLiteral
import io.morfly.pendant.starlark.element.BooleanReference
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
import io.morfly.pendant.starlark.lang.type.BooleanType
import io.morfly.pendant.starlark.lang.type.Key
import io.morfly.pendant.starlark.lang.type.NumberType
import io.morfly.pendant.starlark.lang.type.StringType
import io.morfly.pendant.starlark.lang.type.Tuple
import io.morfly.pendant.starlark.lang.type.Value
import kotlin.reflect.KProperty


/**
 * Feature that enables assignments for variables with names that can vary based on template arguments.
 */
internal interface DynamicAssignmentsFeature : LanguageFeature,
    ModifiersHolder,
    StatementsHolder {

    /**
     * Allows using string as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE_1 = "value"
     *
     * Kotlin code generator program:
     * val VALUE by "VALUE_$i" `=` "value"
     */
    operator fun _StringExpressionAccumulator<Assignment>.provideDelegate(
        thisRef: AssignmentsFeature?, property: KProperty<*>
    ): StringReference {
        val assignment = this.holder.host
        statements += assignment
        return StringReference(name = assignment.name)
    }

    /**
     * Allows using number as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE_1 = 42
     *
     * Kotlin code generator program:
     * val VALUE by "VALUE_$i" `=` 42
     */
    operator fun _NumberExpressionAccumulator<Assignment>.provideDelegate(
        thisRef: AssignmentsFeature?, property: KProperty<*>
    ): NumberReference {
        val assignment = this.holder.host
        statements += assignment
        return NumberReference(name = assignment.name)
    }

    /**
     * Allows using boolean as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE_1 = True
     *
     * Kotlin code generator program:
     * val VALUE by "VALUE_$i" `=` True
     */
    operator fun _BooleanExpressionAccumulator<Assignment>.provideDelegate(
        thisRef: AssignmentsFeature?, property: KProperty<*>
    ): BooleanReference {
        val assignment = this.holder.host
        statements += assignment
        return BooleanReference(name = assignment.name)
    }

    /**
     * Allows using list as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE_1 = [1, 2, 3]
     *
     * Kotlin code generator program:
     * val VALUE by "VALUE_$i" `=` list[1, 2, 3]
     */
    operator fun <T> _ListExpressionAccumulator<T, Assignment>.provideDelegate(
        thisRef: AssignmentsFeature?, property: KProperty<*>
    ): ListReference<T> {
        val assignment = this.holder.host
        statements += assignment
        return ListReference(name = assignment.name)
    }

    /**
     * Allows using tuple as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE_1 = (1, "value", True)
     *
     * Kotlin code generator program:
     * val VALUE by "VALUE_$i" `=` tuple(1, "value", True)
     */
    operator fun _TupleExpressionAccumulator<Assignment>.provideDelegate(
        thisRef: AssignmentsFeature?, property: KProperty<*>
    ): TupleReference {
        val assignment = this.holder.host
        statements += assignment
        return TupleReference(name = assignment.name)
    }

    /**
     * Allows using dictionary as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE_1 = {"key": "value"}
     *
     * Kotlin code generator program:
     * val VALUE by "VALUE_$i" `=` dict { "key" to "value" }
     */
    operator fun <K : Key, V : Value> _DictionaryExpressionAccumulator<K, V, Assignment>.provideDelegate(
        thisRef: AssignmentsFeature?, property: KProperty<*>
    ): DictionaryReference<K, V> {
        val assignment = this.holder.host
        statements += assignment
        return DictionaryReference(name = assignment.name)
    }

    /**
     * Allows using string as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE_1 = "value"
     *
     * Kotlin code generator program:
     * val VALUE by "VALUE_$i" `=` "value"
     */
    infix fun String.`=`(value: StringType): _StringExpressionAccumulator<Assignment> {
        val assignment = Assignment(name = this, value = Expression(value, ::StringLiteral))
        return _StringExpressionAccumulator(assignment)
    }

    /**
     * Allows using number as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE_1 = 42
     *
     * Kotlin code generator program:
     * val VALUE by "VALUE_$i" `=` 42
     */
    infix fun String.`=`(value: NumberType): _NumberExpressionAccumulator<Assignment> {
        val assignment = Assignment(name = this, value = Expression(value, ::NumberLiteral))
        return _NumberExpressionAccumulator(assignment)
    }

    /**
     * Allows using boolean as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE_1 = True
     *
     * Kotlin code generator program:
     * val VALUE by "VALUE_$i" `=` True
     */
    infix fun String.`=`(value: BooleanType): _BooleanExpressionAccumulator<Assignment> {
        val assignment = Assignment(name = this, value = Expression(value, ::BooleanLiteral))
        return _BooleanExpressionAccumulator(assignment)
    }

    /**
     * Allows using list as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE_1 = [1, 2, 3]
     *
     * Kotlin code generator program:
     * val VALUE by "VALUE_$i" `=` list[1, 2, 3]
     */
    infix fun <T> String.`=`(value: List<T>): _ListExpressionAccumulator<T, Assignment> {
        val assignment = Assignment(name = this, value = Expression(value, ::ListExpression))
        return _ListExpressionAccumulator(assignment)
    }

    /**
     * Allows using tuple as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE_1 = (1, "value", True)
     *
     * Kotlin code generator program:
     * val VALUE by "VALUE_$i" `=` tuple(1, "value", True)
     */
    infix fun <T> String.`=`(value: Tuple): _ListExpressionAccumulator<T, Assignment> {
        val assignment = Assignment(name = this, value = Expression(value, ::TupleExpression))
        return _ListExpressionAccumulator(assignment)
    }

    /**
     * Allows using dictionary as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE_1 = {"key": "value"}
     *
     * Kotlin code generator program:
     * val VALUE by "VALUE_$i" `=` dict { "key" to "value" }
     */
    infix fun <K : Key, V : Value> String.`=`(value: Map<K, V>): _DictionaryExpressionAccumulator<K, V, Assignment> {
        val assignment = Assignment(name = this, value = Expression(value, ::DictionaryExpression))
        return _DictionaryExpressionAccumulator(assignment)
    }

    /**
     * Allows using dictionary as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE_1 = {"key": "value"}
     *
     * Kotlin code generator program:
     * val VALUE by "VALUE_$i" `=` { "key" to "value" }
     */
    @OptIn(InternalPendantApi::class)
    infix fun <K : Key, V : Value> String.`=`(
        body: DictionaryContext.() -> Unit
    ): _DictionaryExpressionAccumulator<K, V, Assignment> {
        val dictionaryContext = DictionaryContext(modifiers).apply(body)
        invokeModifiers(dictionaryContext)
        val value = dictionaryContext.kwargs
        val assignment = Assignment(name = this, value = Expression(value, ::DictionaryExpression))
        return _DictionaryExpressionAccumulator(assignment)
    }
}