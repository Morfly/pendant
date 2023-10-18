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

import io.morfly.pendant.starlark.lang.context.DictionaryContext
import io.morfly.pendant.starlark.element.*
import io.morfly.pendant.starlark.element.Argument
import io.morfly.pendant.starlark.lang.ArgumentsHolder
import io.morfly.pendant.starlark.lang.LanguageFeature
import io.morfly.pendant.starlark.lang.ModifiersHolder
import io.morfly.pendant.starlark.lang.append
import io.morfly.pendant.starlark.lang.invokeModifiers
import io.morfly.pendant.starlark.lang.type.*


/**
 * Feature of the Starlark template engine that provides operators for passsing arguments that were not initially
 * specified in Airin.
 */
internal interface DynamicArgumentsFeature :
    LanguageFeature,
    ArgumentsHolder,
    ModifiersHolder {

    /**
     * Operator for passing string argument.
     */
    infix fun String.`=`(value: StringType): _StringExpressionAccumulator<*> {
        val argument = Argument(id = this, value = Expression(value, ::StringLiteral))
        fargs[this] = argument
        return _StringExpressionAccumulator(argument)
    }

    /**
     * Operator for passing integer argument.
     */
    infix fun String.`=`(value: NumberType): _NumberExpressionAccumulator<*> {
        val argument = Argument(id = this, value = Expression(value, ::NumberLiteral))
        fargs[this] = argument
        return _NumberExpressionAccumulator(argument)
    }

    /**
     * Operator for passing float argument.
     */
    infix fun String.`=`(value: BooleanType): _BooleanExpressionAccumulator<*> {
        val argument = Argument(id = this, value = Expression(value, ::BooleanLiteral))
        fargs[this] = argument
        return _BooleanExpressionAccumulator(argument)
    }

    /**
     * Operator for passing list argument.
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
     * Operator for passing tuple argument.
     */
    infix fun String.`=`(value: TupleType): _TupleExpressionAccumulator<*> {
        val argument = Argument(id = this, value = Expression(value, ::TupleExpression))
        fargs[this] = argument
        return _TupleExpressionAccumulator(argument)
    }

    /**
     * Operator for passing dictionary argument.
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
     * Operator for passing dictionary argument.
     */
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
     * Operator for passing null or arguments of any other type.
     */
    infix fun String.`=`(value: Any?): _AnyExpressionAccumulator<*> {
        val argument = Argument(id = this, value = Expression(value))
        fargs[this] = argument
        return _AnyExpressionAccumulator(argument)
    }
}