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

package io.morfly.pendant.starlark.lang.feature

import io.morfly.pendant.starlark.element.*
import io.morfly.pendant.starlark.lang.LanguageFeature
import io.morfly.pendant.starlark.lang.StatementsHolder
import io.morfly.pendant.starlark.lang.type.*
import kotlin.reflect.KProperty


/**
 * Allows variable assignments in the code generator.
 */
internal interface AssignmentsFeature : LanguageFeature,
    StatementsHolder {

    /**
     * Allows using string as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE = "value"
     *
     * Kotlin code generator program:
     * val VALUE by "value"
     */
    operator fun StringType.provideDelegate(thisRef: AssignmentsFeature?, property: KProperty<*>): StringReference {
        statements += Assignment(name = property.name, value = Expression(this, ::StringLiteral))
        return StringReference(name = property.name)
    }

    /**
     * Returns [StringReference] as a type of assigned variable in the code generator.
     * Can be referenced further in the code as a variable of string type.
     */
    operator fun StringReference.getValue(
        thisRef: AssignmentsFeature?, property: KProperty<*>
    ): StringReference = this

    /**
     * Allows using number as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE = 42
     *
     * Kotlin code generator program:
     * val VALUE by 42
     */
    operator fun NumberType.provideDelegate(thisRef: AssignmentsFeature?, property: KProperty<*>): NumberReference {
        statements += Assignment(name = property.name, value = Expression(this, ::NumberLiteral))
        return NumberReference(name = property.name)
    }

    /**
     * Returns [NumberReference] as a type of assigned variable in the code generator.
     * Can be referenced further in the code as a variable of number type.
     */
    operator fun NumberReference.getValue(
        thisRef: AssignmentsFeature?, property: KProperty<*>
    ): NumberReference = this


    /**
     * Allows using boolean as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE = True
     *
     * Kotlin code generator program:
     * val VALUE by true
     */
    operator fun BooleanType.provideDelegate(thisRef: AssignmentsFeature?, property: KProperty<*>): BooleanReference {
        statements += Assignment(name = property.name, value = Expression(this, ::BooleanLiteral))
        return BooleanReference(name = property.name)
    }

    /**
     * Returns [BooleanReference] as a type of assigned variable in the code generator.
     * Can be referenced further in the code as a variable of boolean type.
     */
    operator fun BooleanReference.getValue(
        thisRef: AssignmentsFeature?, property: KProperty<*>
    ): BooleanReference = this


    /**
     * Allows using list as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE = [1, 2, 3]
     *
     * Kotlin code generator program:
     * val VALUE by list[1, 2, 3]
     */
    operator fun <T> List<T>.provideDelegate(thisRef: AssignmentsFeature?, property: KProperty<*>): ListReference<T> {
        statements += Assignment(name = property.name, value = Expression(this, ::ListExpression))
        return ListReference(name = property.name)
    }

    /**
     * Returns [ListReference] as a type of assigned variable in the code generator.
     * Can be referenced further in the code as a variable of list type.
     */
    operator fun <T> ListReference<T>.getValue(
        thisRef: AssignmentsFeature?, property: KProperty<*>
    ): ListReference<T> = this

    /**
     * Allows using list as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE = (1, "value", True)
     *
     * Kotlin code generator program:
     * val VALUE by tupleOf(1, "value", True)
     */
    operator fun TupleType.provideDelegate(thisRef: AssignmentsFeature?, property: KProperty<*>): TupleReference {
        statements += Assignment(name = property.name, value = Expression(this, ::TupleExpression))
        return TupleReference(name = property.name)
    }

    /**
     * Returns [TupleReference] as a type of assigned variable in the code generator.
     * Can be referenced further in the code as a variable of tuple type.
     */
    operator fun TupleReference.getValue(
        thisRef: AssignmentsFeature?, property: KProperty<*>
    ): TupleReference = this


    /**
     * Allows using dictionary as a delegate for generating variable assignments.
     *
     * Generated Starlark code:
     * VALUE = {"key": "value}
     *
     * Kotlin code generator program:
     * val VALUE by dict { "key" to "value" }
     */
    operator fun <K : Key, V : Value> Map<K, V>.provideDelegate(
        thisRef: AssignmentsFeature?, property: KProperty<*>
    ): DictionaryReference<K, V> {
        statements += Assignment(name = property.name, value = Expression(this, ::DictionaryExpression))
        return DictionaryReference(name = property.name)
    }

    /**
     * Returns [DictionaryReference] as a type of assigned variable in the code generator.
     * Can be referenced further in the code as a variable of dictionary type.
     */
    operator fun <K : Key, V : Value> DictionaryReference<K, V>.getValue(
        thisRef: AssignmentsFeature?, property: KProperty<*>
    ): DictionaryReference<K, V> = this
}