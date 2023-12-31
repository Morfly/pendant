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

import io.morfly.pendant.starlark.element.AnyFunctionCall
import io.morfly.pendant.starlark.element.Argument
import io.morfly.pendant.starlark.element.BooleanFunctionCall
import io.morfly.pendant.starlark.element.DictionaryFunctionCall
import io.morfly.pendant.starlark.element.ExpressionStatement
import io.morfly.pendant.starlark.element.ListFunctionCall
import io.morfly.pendant.starlark.element.NumberFunctionCall
import io.morfly.pendant.starlark.element.StringFunctionCall
import io.morfly.pendant.starlark.element.TupleFunctionCall
import io.morfly.pendant.starlark.element.VoidFunctionCall
import io.morfly.pendant.starlark.lang.InternalPendantApi
import io.morfly.pendant.starlark.lang.ModifiersHolder
import io.morfly.pendant.starlark.lang.StatementsHolder
import io.morfly.pendant.starlark.lang.context.FunctionCallContext
import io.morfly.pendant.starlark.lang.invokeModifiers
import io.morfly.pendant.starlark.lang.type.BooleanBaseType
import io.morfly.pendant.starlark.lang.type.BooleanType
import io.morfly.pendant.starlark.lang.type.Key
import io.morfly.pendant.starlark.lang.type.NumberType
import io.morfly.pendant.starlark.lang.type.StringType
import io.morfly.pendant.starlark.lang.type.TupleType
import io.morfly.pendant.starlark.lang.type.Value
import kotlin.reflect.KClass
import kotlin.reflect.typeOf


// ===== Function call statements =====

/**
 * Registers function statement in the Starlark file.
 */
@InternalPendantApi
fun StatementsHolder.registerFunctionCallStatement(name: String, args: List<Argument> = emptyList()) {
    statements += ExpressionStatement(VoidFunctionCall(name, args))
}


/**
 * Registers function statement in the Starlark file.
 */
@InternalPendantApi
inline fun <reified C : FunctionCallContext, H> H.registerFunctionCallStatement(
    name: String, context: C, body: C.() -> Unit
) where H : StatementsHolder, H : ModifiersHolder {
    context.apply(body)
    invokeModifiers(context)
    val args = context.fargs.values.toList()
    registerFunctionCallStatement(name, args)
}

// ===== Function call expressions =====

/**
 * Builds a function call expression that returns string type.
 */
@InternalPendantApi
fun stringFunctionCall(name: String, args: List<Argument> = emptyList()): StringType =
    StringFunctionCall(name, args)

/**
 * Builds a function call expression that returns string type.
 */
@InternalPendantApi
inline fun <reified C : FunctionCallContext> ModifiersHolder.stringFunctionCall(
    name: String, context: C, body: C.() -> Unit
): StringType {
    context.apply(body)
    invokeModifiers(context)
    val args = context.fargs.values.toList()
    return stringFunctionCall(name, args)
}

/**
 * Builds a function call expression that returns list type.
 */
@InternalPendantApi
fun <T> listFunctionCall(name: String, args: List<Argument> = emptyList()): List<T> =
    ListFunctionCall(name, args)

/**
 * Builds a function call expression that returns list type.
 */
@InternalPendantApi
inline fun <T, reified C : FunctionCallContext> ModifiersHolder.listFunctionCall(
    name: String, context: C, body: C.() -> Unit
): List<T> {
    context.apply(body)
    invokeModifiers(context)
    val args = context.fargs.values.toList()
    return listFunctionCall(name, args)
}

/**
 * Builds a function call expression that returns tuple type.
 */
@InternalPendantApi
fun tupleFunctionCall(name: String, args: List<Argument> = emptyList()): TupleType =
    TupleFunctionCall(name, args)

/**
 * Builds a function call expression that returns tuple type.
 */
@InternalPendantApi
inline fun <reified C : FunctionCallContext> ModifiersHolder.tupleFunctionCall(
    name: String, context: C, body: C.() -> Unit
): TupleType {
    context.apply(body)
    invokeModifiers(context)
    val args = context.fargs.values.toList()
    return tupleFunctionCall(name, args)
}

/**
 * Builds a function call expression that returns dictionary type.
 */
@InternalPendantApi
fun dictFunctionCall(name: String, args: List<Argument> = emptyList()): Map<Key, Value> =
    DictionaryFunctionCall(name, args)

/**
 * Builds a function call expression that returns dictionary type.
 */
@InternalPendantApi
inline fun <reified C : FunctionCallContext> ModifiersHolder.dictFunctionCall(
    name: String, context: C, body: C.() -> Unit
): Map<Key, Value> {
    context.apply(body)
    invokeModifiers(context)
    val args = context.fargs.values.toList()
    return dictFunctionCall(name, args)
}

/**
 * Builds a function call expression that returns integer type.
 */
@InternalPendantApi
fun numberFunctionCall(name: String, args: List<Argument> = emptyList()): NumberType =
    NumberFunctionCall(name, args)

/**
 * Builds a function call expression that returns integer type.
 */
@InternalPendantApi
inline fun <reified C : FunctionCallContext> ModifiersHolder.numberFunctionCall(
    name: String, context: C, body: C.() -> Unit
): NumberType {
    context.apply(body)
    invokeModifiers(context)
    val args = context.fargs.values.toList()
    return numberFunctionCall(name, args)
}

/**
 * Builds a function call expression that returns boolean type.
 */
@InternalPendantApi
fun booleanFunctionCall(name: String, args: List<Argument> = emptyList()): BooleanType =
    BooleanFunctionCall(name, args)

/**
 * Builds a function call expression that returns boolean type.
 */
@InternalPendantApi
inline fun <reified C : FunctionCallContext> ModifiersHolder.booleanFunctionCall(
    name: String, context: C, body: C.() -> Unit
): BooleanType {
    context.apply(body)
    invokeModifiers(context)
    val args = context.fargs.values.toList()
    return booleanFunctionCall(name, args)
}

/**
 * Function call expression return type which is specified by the caller.
 *
 * Note: if the function you're building has a specific determined type DO NOT use this builder.
 */
@InternalPendantApi
inline fun <reified T> functionCallExpression(name: String, args: List<Argument> = emptyList()): T =
    when {
        StringType::class.java.isAssignableFrom(T::class.java) -> StringFunctionCall(name, args)
        List::class.java.isAssignableFrom(T::class.java) -> ListFunctionCall<Any?>(name, args)
        Map::class.java.isAssignableFrom(T::class.java) -> DictionaryFunctionCall<Key, Value>(name, args)
        NumberType::class.java.isAssignableFrom(T::class.java) -> NumberFunctionCall(name, args)
        TupleType::class.java.isAssignableFrom(T::class.java) -> TupleFunctionCall(name, args)
        BooleanBaseType::class.java.isAssignableFrom(T::class.java) -> {
            val type = typeOf<T>().arguments.first().type
            when (type?.classifier as? KClass<*>) {
                Boolean::class -> BooleanFunctionCall(name, args)
                else -> AnyFunctionCall(name, args)
            }
        }

        else -> AnyFunctionCall(name, args)
    } as T

/**
 * Function call expression return type which is specified by the caller.
 *
 * Note: if the function you're building has a specific determined type DO NOT use this builder.
 */
@InternalPendantApi
inline fun <reified T, reified C : FunctionCallContext> ModifiersHolder.functionCallExpression(
    name: String, context: C, body: C.() -> Unit
): T {
    context.apply(body)
    invokeModifiers(context)
    val args = context.fargs.values.toList()
    return functionCallExpression(name, args)
}