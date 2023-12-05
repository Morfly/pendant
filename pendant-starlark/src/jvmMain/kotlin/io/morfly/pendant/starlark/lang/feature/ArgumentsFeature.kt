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

import io.morfly.pendant.starlark.element.Argument
import io.morfly.pendant.starlark.element.BooleanLiteral
import io.morfly.pendant.starlark.element.DictionaryBinaryOperation
import io.morfly.pendant.starlark.element.DictionaryExpression
import io.morfly.pendant.starlark.element.Expression
import io.morfly.pendant.starlark.element.ListBinaryOperation
import io.morfly.pendant.starlark.element.ListExpression
import io.morfly.pendant.starlark.element.NumberLiteral
import io.morfly.pendant.starlark.element.StringLiteral
import io.morfly.pendant.starlark.element.TupleExpression
import io.morfly.pendant.starlark.lang.ArgumentsHolder
import io.morfly.pendant.starlark.lang.LanguageFeature
import io.morfly.pendant.starlark.lang.append
import io.morfly.pendant.starlark.lang.type.BooleanType
import io.morfly.pendant.starlark.lang.type.NumberType
import io.morfly.pendant.starlark.lang.type.StringType
import io.morfly.pendant.starlark.lang.type.TupleType
import kotlin.reflect.KProperty

/**
 * Allows passing arguments in functions calls with curly brackets {} in the code generator.
 */
internal interface ArgumentsFeature : LanguageFeature,
    ArgumentsHolder {

    operator fun <V> Map<String, Argument>.getValue(thisRef: Any?, property: KProperty<*>): V {
        error("Unable to return value from a function argument.")
    }

    /**
     * Assigning arguments of string type in function calls with curly brackets {}. Assuming the function in Kotlin
     * includes a corresponding parameter.
     *
     * Generated Starlark code:
     * my_function(
     *  value1 = "value",
     *  value2 = STRING_REF
     * )
     *
     * Kotlin code generator program:
     * my_function {
     *  value1 = "value"
     *  value2 = STRING_REF
     * }
     */
    operator fun <V : StringType?> Map<String, Argument>.setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        fargs[property.name] = Argument(id = property.name, value = Expression(value, ::StringLiteral))
    }

    /**
     * Assigning arguments of number type in function calls with curly brackets {}. Assuming the function in Kotlin
     * includes a corresponding parameter.
     *
     * Generated Starlark code:
     * my_function(
     *  value1 = 42,
     *  value2 = NUMBER_REF
     * )
     *
     * Kotlin code generator program:
     * my_function {
     *  value1 = 42
     *  value2 = NUMBER_REF
     * }
     */
    operator fun <V : NumberType?> Map<String, Argument>.setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        fargs[property.name] = Argument(id = property.name, value = Expression(value, ::NumberLiteral))
    }

    /**
     * Assigning arguments of boolean type in function calls with curly brackets {}. Assuming the function in Kotlin
     * includes a corresponding parameter.
     *
     * Generated Starlark code:
     * my_function(
     *  value1 = True,
     *  value2 = BOOLEAN_REF
     * )
     *
     * Kotlin code generator program:
     * my_function {
     *  value1 = true
     *  value2 = BOOLEAN_REF
     * }
     */
    operator fun <V : BooleanType?> Map<String, Argument>.setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        fargs[property.name] = Argument(id = property.name, value = Expression(value, ::BooleanLiteral))
    }

    /**
     * Assigning arguments of list type in function calls with curly brackets {}. Assuming the function in Kotlin
     * includes a corresponding parameter.
     *
     * Generated Starlark code:
     * my_function(
     *  value1 = [1, 2, 3],
     *  value2 = LIST_REF
     * )
     *
     * Kotlin code generator program:
     * my_function {
     *  value1 = list[1, 2, 3]
     *  value2 = LIST_REF
     * }
     */
    operator fun <T, V : List<T>?> Map<String, Argument>.setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        append(
            name = property.name,
            value = Expression(value, ::ListExpression),
            concatenation = { left, op, right -> ListBinaryOperation<T>(left, op, right) }
        )
    }

    /**
     * Assigning arguments of tuple type in function calls with curly brackets {}. Assuming the function in Kotlin
     * includes a corresponding parameter.
     *
     * Generated Starlark code:
     * my_function(
     *  value1 = (1, "value", True),
     *  value2 = TUPLE_REF
     * )
     *
     * Kotlin code generator program:
     * my_function {
     *  value1 = tupleOf(1, "value", True),
     *  value2 = TUPLE_REF
     * }
     */
    operator fun <V : TupleType?> Map<String, Argument>.setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        fargs[property.name] = Argument(id = property.name, value = Expression(value, ::TupleExpression))
    }

    /**
     * Assigning arguments of dictionary type in function calls with curly brackets {}. Assuming the function in Kotlin
     * includes a corresponding parameter.
     *
     * Generated Starlark code:
     * my_function(
     *  value1 = {"key": "value"},
     *  value2 = DICT_REF
     * )
     *
     * Kotlin code generator program:
     * my_function {
     *  value1 = dict { "key" to "value" },
     *  value2 = DICT_REF
     * }
     */
    operator fun <K, V, V1 : Map<K, V>?> Map<String, Argument>.setValue(
        thisRef: Any?, property: KProperty<*>, value: V1
    ) {
        append(
            name = property.name,
            value = Expression(value, ::DictionaryExpression),
            concatenation = { left, op, right -> DictionaryBinaryOperation<K, V>(left, op, right) }
        )
    }
}