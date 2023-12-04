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

@file:Suppress("unused")

package io.morfly.pendant.starlark.lang.type

/**
 * Starlark tuple type.
 */
interface Tuple {

    val elements: ListType<Any?>
}

data class TupleImpl(override val elements: ListType<Any?>) : Tuple

/**
 * Converting Starlark tuple to list.
 */
fun Tuple.toList(): ListType<Any?> = elements

/**
 * Converting Starlark list to tuple.
 */
fun ListType<Any?>.toTuple(): Tuple = TupleImpl(this)

/**
 * Builder for a tuple.
 */
fun tupleOf(vararg elements: Any?): Tuple =
    TupleImpl(elements.toList())

/**
 * Builder for an empty tuple.
 */
fun emptyTuple(): Tuple = TupleImpl(emptyList())
