/*
 * Copyright 2021 Pavlo Stavytskyi
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

package io.morfly.pendant

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueArgument
import io.morfly.pendant.starlark.lang.BracketsKind
import io.morfly.pendant.starlark.lang.BracketsKind.Curly
import io.morfly.pendant.starlark.lang.BracketsKind.Round
import io.morfly.pendant.starlark.lang.FunctionKind
import io.morfly.pendant.starlark.lang.FunctionKind.Expression
import io.morfly.pendant.starlark.lang.FunctionKind.Statement
import io.morfly.pendant.starlark.lang.FunctionScope
import io.morfly.pendant.starlark.lang.FunctionScope.*
import io.morfly.pendant.starlark.lang.ReturnKind
import io.morfly.pendant.starlark.lang.ReturnKind.Dynamic
import io.morfly.pendant.starlark.lang.ReturnKind.Type
import java.io.OutputStream


inline fun <reified V> Map<String, *>.valueAsOrNull(name: String): V? =
    get(name) as? V

inline fun <reified V> Map<String, *>.valueAs(name: String): V =
    get(name) as V

fun List<KSValueArgument>.toMap(): Map<String, Any?> =
    associate { it.name!!.asString() to it.value }

fun KSType.toFunctionKindOrNull(): FunctionKind? =
    when (declaration.simpleName.asString()) {
        Statement.name -> Statement
        Expression.name -> Expression
        else -> null
    }

fun KSType.toFunctionCallKind(): FunctionKind =
    toFunctionKindOrNull()!!

fun KSType.toBracketsKindOrNull(): BracketsKind? =
    when (declaration.simpleName.asString()) {
        Round.name -> Round
        Curly.name -> Curly
        else -> null
    }

fun KSType.toBracketsKind(): BracketsKind =
    toBracketsKindOrNull()!!

fun KSType.toFunctionScopeOrNull(): FunctionScope? =
    when (declaration.simpleName.asString()) {
        Starlark.name -> Starlark
        Workspace.name -> Workspace
        Build.name -> Build
        else -> null
    }

fun KSType.toFunctionScope(): FunctionScope =
    toFunctionScopeOrNull()!!

fun KSType.toReturnKindOrNull(): ReturnKind? =
    when (declaration.simpleName.asString()) {
        Type.name -> Type
        Dynamic.name -> Dynamic
        else -> null
    }

fun KSType.toReturnKind(): ReturnKind =
    toReturnKindOrNull()!!

operator fun OutputStream.plusAssign(str: String) {
    write(str.toByteArray())
}

private val snakeRegex = "_[a-zA-Z]".toRegex()

fun String.snakeToCamelCase(firstUppercase: Boolean = true): String {
    val result = snakeRegex.replace(this) {
        it.value.replace("_", "").uppercase()
    }
    return if (firstUppercase)
        result.replaceFirstChar { it.uppercase() }
    else result
}

fun Map<*, Set<*>>.toDisplayableString(): String =
    entries.joinToString(separator = ",\n") { (key, value) ->
        "\t$key:\n" + value.joinToString(separator = ",\n") { "\t\t$it" }
    }

fun Set<*>.toDisplayableString(): String =
    joinToString(separator = ", \n") { "\t$it" }