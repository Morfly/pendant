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

@file:Suppress("SpellCheckingInspection", "FunctionName", "unused")

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
import io.morfly.pendant.starlark.lang.InternalPendantApi
import io.morfly.pendant.starlark.lang.LanguageFeature
import io.morfly.pendant.starlark.lang.ModifiersHolder
import io.morfly.pendant.starlark.lang.append
import io.morfly.pendant.starlark.lang.context.DictionaryContext
import io.morfly.pendant.starlark.lang.invokeModifiers
import io.morfly.pendant.starlark.lang.type.BooleanType
import io.morfly.pendant.starlark.lang.type.Key
import io.morfly.pendant.starlark.lang.type.NumberType
import io.morfly.pendant.starlark.lang.type.StringType
import io.morfly.pendant.starlark.lang.type.TupleType
import io.morfly.pendant.starlark.lang.type.Value


/**
 * Allows passing arguments dynamically in functions calls with curly brackets {} in the code generator.
 */
internal interface DynamicArgumentsFeature :
    LanguageFeature,
    ArgumentsHolder,
    ModifiersHolder {

    /**
     * Assigning arguments of string type in function calls with curly brackets {}.
     *
     * Generated Starlark code:
     * my_function(
     *  value1 = "value",
     *  value2 = STRING_REF
     * )
     *
     * Kotlin code generator program:
     * my_function {
     *  value1 `=` "value"
     *  value2 `=` STRING_REF
     * }
     */
    infix fun String.`=`(value: StringType): _StringExpressionAccumulator<*> {
        val argument = Argument(id = this, value = Expression(value, ::StringLiteral))
        fargs[this] = argument
        return _StringExpressionAccumulator(argument)
    }

    /**
     * Assigning arguments of number type in function calls with curly brackets {}.
     *
     * Generated Starlark code:
     * my_function(
     *  value1 = 42,
     *  value2 = NUMBER_REF
     * )
     *
     * Kotlin code generator program:
     * my_function {
     *  value1 `=` 42
     *  value2 `=` NUMBER_REF
     * }
     */
    infix fun String.`=`(value: NumberType): _NumberExpressionAccumulator<*> {
        val argument = Argument(id = this, value = Expression(value, ::NumberLiteral))
        fargs[this] = argument
        return _NumberExpressionAccumulator(argument)
    }

    /**
     * Assigning arguments of boolean type in function calls with curly brackets {}.
     *
     * Generated Starlark code:
     * my_function(
     *  value1 = True,
     *  value2 = BOOLEAN_REF
     * )
     *
     * Kotlin code generator program:
     * my_function {
     *  value1 `=` true
     *  value2 `=` BOOLEAN_REF
     * }
     */
    infix fun String.`=`(value: BooleanType): _BooleanExpressionAccumulator<*> {
        val argument = Argument(id = this, value = Expression(value, ::BooleanLiteral))
        fargs[this] = argument
        return _BooleanExpressionAccumulator(argument)
    }

    /**
     * Assigning arguments of list type in function calls with curly brackets {}.
     *
     * Generated Starlark code:
     * my_function(
     *  value1 = [1, 2, 3],
     *  value2 = LIST_REF
     * )
     *
     * Kotlin code generator program:
     * my_function {
     *  value1 `=` list[1, 2, 3]
     *  value2 `=` LIST_REF
     * }
     */
    infix fun <T> String.`=`(value: List<T>): _ListExpressionAccumulator<T, *> {
        val argument = append(
            name = this,
            value = Expression(value, ::ListExpression),
            concatenation = { left, op, right -> ListBinaryOperation<T>(left, op, right) }
        )
        return _ListExpressionAccumulator(argument)
    }

    /**
     * Assigning arguments of tuple type in function calls with curly brackets {}.
     *
     * Generated Starlark code:
     * my_function(
     *  value1 = (1, "value", True),
     *  value2 = TUPLE_REF
     * )
     *
     * Kotlin code generator program:
     * my_function {
     *  value1 `=` tupleOf(1, "value", True),
     *  value2 `=` TUPLE_REF
     * }
     */
    infix fun String.`=`(value: TupleType): _TupleExpressionAccumulator<*> {
        val argument = Argument(id = this, value = Expression(value, ::TupleExpression))
        fargs[this] = argument
        return _TupleExpressionAccumulator(argument)
    }

    /**
     * Assigning arguments of dictionary type in function calls with curly brackets {}.
     *
     * Generated Starlark code:
     * my_function(
     *  value1 = {"key": "value"},
     *  value2 = DICT_REF
     * )
     *
     * Kotlin code generator program:
     * my_function {
     *  value1 `=` dict { "key" to "value" },
     *  value2 `=` DICT_REF
     * }
     */
    infix fun <K : Key, V : Value> String.`=`(value: Map<K, V>): _DictionaryExpressionAccumulator<K, V, *> {
        val argument = append(
            name = this,
            value = Expression(value, ::DictionaryExpression),
            concatenation = { left, op, right -> DictionaryBinaryOperation<K, V>(left, op, right) }
        )
        return _DictionaryExpressionAccumulator(argument)
    }

    /**
     * Assigning arguments of dictionary type in function calls with curly brackets {}.
     *
     * Generated Starlark code:
     * my_function(
     *  value1 = {"key": "value"},
     *  value2 = DICT_REF
     * )
     *
     * Kotlin code generator program:
     * my_function {
     *  value1 `=` dict { "key" to "value" },
     *  value2 `=` DICT_REF
     * }
     */
    @OptIn(InternalPendantApi::class)
    infix fun String.`=`(body: DictionaryContext.() -> Unit): _DictionaryExpressionAccumulator<Key, Value, *> {
        val dictionaryContext = DictionaryContext(modifiers).apply(body)
        invokeModifiers(dictionaryContext)
        val value = dictionaryContext.kwargs
        val argument = append(
            name = this,
            value = DictionaryExpression(value),
            concatenation = { left, op, right -> DictionaryBinaryOperation<Key, Value>(left, op, right) }
        )
        return _DictionaryExpressionAccumulator(argument)
    }

    /**
     * Assigning arguments of dictionary type in function calls with curly brackets {}.
     *
     * Generated Starlark code:
     * my_function(
     *  value1 = {"key": "value"},
     *  value2 = DICT_REF
     * )
     *
     * Kotlin code generator program:
     * my_function {
     *  value1 `=` { "key" to "value" },
     *  value2 `=` DICT_REF
     * }
     */
    infix fun String.`=`(value: Any?): _AnyExpressionAccumulator<*> {
        val argument = Argument(id = this, value = Expression(value))
        fargs[this] = argument
        return _AnyExpressionAccumulator(argument)
    }
}