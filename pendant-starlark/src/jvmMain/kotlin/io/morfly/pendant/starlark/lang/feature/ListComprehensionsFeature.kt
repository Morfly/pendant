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

@file:Suppress("ClassName")

package io.morfly.pendant.starlark.lang.feature

import io.morfly.pendant.starlark.element.*
import io.morfly.pendant.starlark.lang.type.BooleanType
import io.morfly.pendant.starlark.lang.type.NumberType
import io.morfly.pendant.starlark.lang.type.StringType
import io.morfly.pendant.starlark.lang.context.FileContext
import io.morfly.pendant.starlark.lang.context.ContextProvider
import io.morfly.pendant.starlark.lang.LanguageFeature
import io.morfly.pendant.starlark.lang.StatementsHolder


/**
 * Feature that enables list comprehensions.
 */
internal interface ListComprehensionsFeature<C : FileContext> : LanguageFeature,
    ContextProvider<C>,
    StatementsHolder {


    // ==== String items =====

    class _CompWithStringItems(
        internal val variable: StringReference,
        internal val clauses: MutableList<Comprehension.Clause>
    )

    /**
     * Keyword that defines `for` clause in the comprehension where items are of string type.
     *
     * @receiver `for` clause variable name.
     */
    infix fun String.`in`(iterable: List<StringType>): _CompWithStringItems {
        val itemVariable = StringReference(name = this)
        val clauses = mutableListOf<Comprehension.Clause>(
            Comprehension.For(variables = listOf(itemVariable), iterable = Expression(iterable, ::ListExpression))
        )
        return _CompWithStringItems(itemVariable, clauses)
    }

    /**
     * Keyword that defines `if` clause in the comprehension.
     *
     * @param condition the `if` clause condition passed as a raw text.
     */
    infix fun _CompWithStringItems.`if`(condition: String): _CompWithStringItems {
        clauses += Comprehension.If(condition = AnyRawExpression(condition))
        return this
    }

    /**
     * Defines the body of a comprehension.
     *
     * @param body the function which takes a variable reference from a `for` clause and returns the value of a
     * comprehension body.
     */
    infix fun <R> _CompWithStringItems.take(body: C.(StringReference) -> R): ListComprehension<R> =
        buildComprehension(context = newContext(), variable, body, clauses)


    // ===== Integer items =====

    class _CompWithNumberItems(
        internal val variable: NumberReference,
        internal val clauses: MutableList<Comprehension.Clause>
    )

    /**
     * Keyword that defines `for` clause in the comprehension where items are of integer type.
     *
     * @receiver `for` clause variable name.
     */
    infix fun String.`in`(iterable: List<NumberType>): _CompWithNumberItems {
        val itemVariable = NumberReference(name = this)
        val clauses = mutableListOf<Comprehension.Clause>(
            Comprehension.For(variables = listOf(itemVariable), iterable = Expression(iterable, ::ListExpression))
        )
        return _CompWithNumberItems(itemVariable, clauses)
    }

    /**
     * Keyword that defines `if` clause in the comprehension.
     *
     * @param condition the `if` clause condition passed as a raw text.
     */
    infix fun _CompWithNumberItems.`if`(condition: String): _CompWithNumberItems {
        clauses += Comprehension.If(condition = AnyRawExpression(condition))
        return this
    }

    /**
     * Defines the body of a comprehension.
     *
     * @param body the function which takes a variable reference from a `for` clause and returns the value of a
     * comprehension body.
     */
    infix fun <R> _CompWithNumberItems.take(body: C.(NumberReference) -> R): ListComprehension<R> =
        buildComprehension(context = newContext(), variable, body, clauses)


    // ===== Boolean items =====

    class _CompWithBooleanItems(
        internal val variable: BooleanReference,
        internal val clauses: MutableList<Comprehension.Clause>
    )

    /**
     * Keyword that defines `for` clause in the comprehension where items are of boolean type.
     */
    infix fun String.`in`(iterable: List<BooleanType>): _CompWithBooleanItems {
        val itemVariable = BooleanReference(name = this)
        val clauses = mutableListOf<Comprehension.Clause>(
            Comprehension.For(variables = listOf(itemVariable), iterable = Expression(iterable, ::ListExpression))
        )
        return _CompWithBooleanItems(itemVariable, clauses)
    }

    /**
     * Keyword that defines `if` clause in the comprehension.
     *
     * @param condition the `if` clause condition passed as a raw text.
     */
    infix fun _CompWithBooleanItems.`if`(condition: String): _CompWithBooleanItems {
        clauses += Comprehension.If(condition = AnyRawExpression(condition))
        return this
    }

    /**
     * Defines the body of a comprehension.
     *
     * @param body the function which takes a variable reference from a `for` clause and returns the value of a
     * comprehension body.
     */
    infix fun <R> _CompWithBooleanItems.take(body: C.(BooleanReference) -> R): ListComprehension<R> =
        buildComprehension(context = newContext(), variable, body, clauses)


    // ===== List items =====

    class _CompWithListItems<T>(
        internal val variable: ListReference<T>,
        internal val clauses: MutableList<Comprehension.Clause>
    )

    /**
     * Keyword that defines `for` clause in the comprehension where items are of list type.
     */
    infix fun <T> String.`in`(iterable: List<List<T>>): _CompWithListItems<T> {
        val itemVariable = ListReference<T>(name = this)
        val clauses = mutableListOf<Comprehension.Clause>(
            Comprehension.For(variables = listOf(itemVariable), Expression(iterable, ::ListExpression))
        )
        return _CompWithListItems(itemVariable, clauses)
    }

    /**
     * Keyword that defines `if` clause in the comprehension.
     *
     * @param condition the `if` clause condition passed as a raw text.
     */
    infix fun <T> _CompWithListItems<T>.`if`(condition: String): _CompWithListItems<T> {
        clauses += Comprehension.If(condition = AnyRawExpression(condition))
        return this
    }

    /**
     * A `for` clause for nested comprehensions.
     */
    infix fun <T, R> _CompWithListItems<T>.`for`(body: C.(ListReference<T>) -> ListComprehension<R>): ListComprehension<R> {
        val context = newContext()
        val innerComprehension = context.body(variable)
        if (context.statements.isNotEmpty())
            error("Statements are prohibited in `for` clauses of comprehensions.")
        clauses += innerComprehension.clauses
        return ListComprehension(body = innerComprehension.body, clauses)
    }

    /**
     * Defines the body of a comprehension.
     *
     * @param body the function which takes a variable reference from a `for` clause and returns the value of a
     * comprehension body.
     */
    infix fun <T, R> _CompWithListItems<T>.take(body: C.(ListReference<T>) -> List<R>): ListComprehension<List<R>> =
        buildComprehension(context = newContext(), variable, body, clauses)
}

private fun <C : FileContext, V : Reference, R> StatementsHolder.buildComprehension(
    context: C,
    variable: V,
    body: C.(V) -> R,
    clauses: MutableList<Comprehension.Clause>
): ListComprehension<R> {
    val compBody = context.body(variable)

    if (context.statements.isNotEmpty()) {
        val exprStatements = context.statements.filterIsInstance<ExpressionStatement>()
        if (exprStatements.size > 1) error("A comprehension body must not contain more than one statement.")
        val expression = exprStatements.first().expression
        val comprehension = ListComprehension<R>(body = expression, clauses)
        statements += ExpressionStatement(comprehension)
        return comprehension
    }
    return ListComprehension(body = Expression(compBody), clauses)
}