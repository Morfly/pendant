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
import io.morfly.pendant.starlark.element.Expression
import io.morfly.pendant.starlark.lang.InternalPendantApi
import io.morfly.pendant.starlark.lang.LanguageFeature
import io.morfly.pendant.starlark.lang.ModifiersHolder
import io.morfly.pendant.starlark.lang.context.FunctionCallContext
import io.morfly.pendant.starlark.lang.invokeModifiers

/**
 * Allows generating Starlark function call expression dynamically, using a Kotlin string as its name.
 */
internal interface DynamicFunctionExpressionsFeature : LanguageFeature

/**
 * Generates  Starlark function call with a return type dynamically, using a Kotlin string as its name.
 *
 * Generated Starlark code:
 * FILES = glob(include = ["src/main/kotlin/**/*.kt"])
 *
 * Kotlin code generator program:
 * import io.morfly.pendant.starlark.lang.feature.invoke
 *
 * val FILES by "glob"<ListType<StringType>> {
 *   "include" `=` list["src/main/kotlin/**/*.kt"]
 * }
 */
context(DynamicFunctionExpressionsFeature, ModifiersHolder)
@OptIn(InternalPendantApi::class)
inline operator fun <reified T> String.invoke(body: FunctionCallContext.() -> Unit): T {
    val functionCallContext = FunctionCallContext(modifiers)
    invokeModifiers(functionCallContext)
    return functionCallExpression<T, FunctionCallContext>(name = this, functionCallContext, body)
}

/**
 * Generates  Starlark function call with a return type dynamically, using a Kotlin string as its name.
 *
 * Generated Starlark code:
 * FILES = glob(["src/main/kotlin/**/*.kt"])
 *
 * Kotlin code generator program:
 * import io.morfly.pendant.starlark.lang.feature.invoke
 *
 * val FILES by "glob"<ListType<StringType>>(list["src/main/kotlin/**/*.kt"])
 */
context(DynamicFunctionExpressionsFeature)
@OptIn(InternalPendantApi::class)
inline operator fun <reified T> String.invoke(vararg arguments: Any?): T {
    val elements = arguments.map {
        Argument(id = "", Expression(it))
    }
    return functionCallExpression(name = this, elements)
}
