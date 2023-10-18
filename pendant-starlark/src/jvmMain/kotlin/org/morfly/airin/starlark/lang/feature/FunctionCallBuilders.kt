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

package org.morfly.airin.starlark.lang.feature

import org.morfly.airin.starlark.lang.context.FunctionCallContext
import org.morfly.airin.starlark.elements.AnyFunctionCall
import org.morfly.airin.starlark.elements.Argument
import org.morfly.airin.starlark.elements.BooleanFunctionCall
import org.morfly.airin.starlark.elements.DictionaryFunctionCall
import org.morfly.airin.starlark.elements.ExpressionStatement
import org.morfly.airin.starlark.elements.ListFunctionCall
import org.morfly.airin.starlark.elements.NumberFunctionCall
import org.morfly.airin.starlark.elements.StringFunctionCall
import org.morfly.airin.starlark.elements.TupleFunctionCall
import org.morfly.airin.starlark.elements.VoidFunctionCall
import org.morfly.airin.starlark.lang.InternalPendantApi
import org.morfly.airin.starlark.lang.ModifiersHolder
import org.morfly.airin.starlark.lang.StatementsHolder
import org.morfly.airin.starlark.lang.types.BooleanBaseType
import org.morfly.airin.starlark.lang.types.BooleanType
import org.morfly.airin.starlark.lang.types.Key
import org.morfly.airin.starlark.lang.types.NumberType
import org.morfly.airin.starlark.lang.types.StringType
import org.morfly.airin.starlark.lang.types.TupleType
import org.morfly.airin.starlark.lang.types.Value
import org.morfly.airin.starlark.lang.asSet
import org.morfly.airin.starlark.lang.invokeModifiers
import kotlin.reflect.KClass
import kotlin.reflect.typeOf


// ===== Function call statements =====

/**
 * Registers function statement in the Starlark file.
 */
@InternalPendantApi
fun StatementsHolder.registerFunctionCallStatement(name: String, args: Set<Argument> = emptySet()) {
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
    val args = context.fargs.asSet()
    registerFunctionCallStatement(name, args)
}

// ===== Function call expressions =====

/**
 * Builds a function call expression that returns string type.
 */
@InternalPendantApi
fun stringFunctionCall(name: String, args: Set<Argument> = emptySet()): StringType =
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
    val args = context.fargs.asSet()
    return stringFunctionCall(name, args)
}

/**
 * Builds a function call expression that returns list type.
 */
@InternalPendantApi
fun <T> listFunctionCall(name: String, args: Set<Argument> = emptySet()): List<T> =
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
    val args = context.fargs.asSet()
    return listFunctionCall(name, args)
}

/**
 * Builds a function call expression that returns tuple type.
 */
@InternalPendantApi
fun tupleFunctionCall(name: String, args: Set<Argument> = emptySet()): TupleType =
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
    val args = context.fargs.asSet()
    return tupleFunctionCall(name, args)
}

/**
 * Builds a function call expression that returns dictionary type.
 */
@InternalPendantApi
fun dictFunctionCall(name: String, args: Set<Argument> = emptySet()): Map<Key, Value> =
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
    val args = context.fargs.asSet()
    return dictFunctionCall(name, args)
}

/**
 * Builds a function call expression that returns integer type.
 */
@InternalPendantApi
fun numberFunctionCall(name: String, args: Set<Argument> = emptySet()): NumberType =
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
    val args = context.fargs.asSet()
    return numberFunctionCall(name, args)
}

/**
 * Builds a function call expression that returns boolean type.
 */
@InternalPendantApi
fun booleanFunctionCall(name: String, args: Set<Argument> = emptySet()): BooleanType =
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
    val args = context.fargs.asSet()
    return booleanFunctionCall(name, args)
}

/**
 * Function call expression return type if which is specified by the caller.
 *
 * Note: if the function you're building has a specific determined type DO NOT use this builder.
 */
@InternalPendantApi
inline fun <reified T> functionCallExpression(name: String, args: Set<Argument> = emptySet()): T =
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
 * Function call expression return type if which is specified by the caller.
 *
 * Note: if the function you're building has a specific determined type DO NOT use this builder.
 */
@InternalPendantApi
inline fun <reified T, reified C : FunctionCallContext> ModifiersHolder.functionCallExpression(
    name: String, context: C, body: C.() -> Unit
): T {
    context.apply(body)
    invokeModifiers(context)
    val args = context.fargs.asSet()
    return functionCallExpression(name, args)
}