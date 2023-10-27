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

@file:Suppress("ClassName", "SpellCheckingInspection")

package io.morfly.pendant.starlark.lang.feature

import io.morfly.pendant.starlark.element.DictionaryExpression
import io.morfly.pendant.starlark.element.ListExpression
import io.morfly.pendant.starlark.element.TupleExpression
import io.morfly.pendant.starlark.lang.InternalPendantApi
import io.morfly.pendant.starlark.lang.LanguageFeature
import io.morfly.pendant.starlark.lang.ModifiersHolder
import io.morfly.pendant.starlark.lang.context.DictionaryContext
import io.morfly.pendant.starlark.lang.invokeModifiers
import io.morfly.pendant.starlark.lang.type.Key
import io.morfly.pendant.starlark.lang.type.TupleType
import io.morfly.pendant.starlark.lang.type.Value
import io.morfly.pendant.starlark.lang.type.emptyTuple
import io.morfly.pendant.starlark.lang.type.tupleOf


/**
 * Feature that enables list, dictionary and tuple expressions.
 */
internal interface CollectionsFeature : LanguageFeature,
    ModifiersHolder {

    // ===== Lists =====

    object _ListExpressionBuilder

    val list get() = _ListExpressionBuilder

    /**
     * List expreession builder.
     */
    operator fun <T> _ListExpressionBuilder.get(vararg args: T): List<T> =
        ListExpression(listOf(*args))

    /**
     * List expression builder.
     */
    fun <T> list(vararg args: T): List<T> =
        ListExpression(listOf(*args))

    /**
     * Empty list builder.
     */
    fun list(): List<Nothing> =
        ListExpression(emptyList())


    // ===== Dictionaries =====

    /**
     * Dictionary expression builder.
     */
    @OptIn(InternalPendantApi::class)
    fun dict(body: DictionaryContext.() -> Unit): Map<Key, Value> {
        val dictionaryContext = DictionaryContext(modifiers).apply(body)
        invokeModifiers(dictionaryContext)
        val kwargs = dictionaryContext.kwargs
        return DictionaryExpression(kwargs)
    }

    @Deprecated("Replace with dict{} expression builder", replaceWith = ReplaceWith("dict {}"))
    fun <K : Key, V : Value> dict(vararg kwargs: Pair<K, V>): Map<K, V> =
        kwargs.toMap()


    // ===== Tuples =====

    /**
     * Tuple expression builder.
     */
    fun tuple(vararg args: Any?): TupleType =
        TupleExpression(tupleOf(*args))

    /**
     * Empty tuple expression builder.
     */
    fun tuple(): TupleType =
        TupleExpression(emptyTuple())
}