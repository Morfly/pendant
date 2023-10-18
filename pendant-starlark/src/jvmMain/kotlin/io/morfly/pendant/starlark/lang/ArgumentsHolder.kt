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

package io.morfly.pendant.starlark.lang

import io.morfly.pendant.starlark.element.Argument
import io.morfly.pendant.starlark.element.BinaryOperation
import io.morfly.pendant.starlark.element.BinaryOperator
import io.morfly.pendant.starlark.element.Expression

/**
 * Defines an entity that collects argument elements.
 */
internal interface ArgumentsHolder {

    /**
     *
     */
    val fargs: LinkedHashMap<String, Argument>
}

fun LinkedHashMap<String, Argument>.asSet() =
    mapTo(linkedSetOf()) { it.value }

internal fun ArgumentsHolder.append(
    name: String,
    value: Expression,
    concatenation: (Expression, BinaryOperator, Expression) -> BinaryOperation
): Argument {
    val argument = if (name !in fargs) {
        Argument(id = name, value = value)
    } else {
        val leftExpression = fargs[name]!!.value
        Argument(
            id = name,
            value = concatenation(leftExpression, BinaryOperator.PLUS, value)
        )
    }
    fargs[name] = argument
    return argument
}