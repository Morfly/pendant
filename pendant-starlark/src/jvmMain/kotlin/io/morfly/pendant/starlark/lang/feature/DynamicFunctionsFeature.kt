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

@file:Suppress("FunctionName")

package io.morfly.pendant.starlark.lang.feature

import io.morfly.pendant.starlark.element.Argument
import io.morfly.pendant.starlark.element.Expression
import io.morfly.pendant.starlark.lang.InternalPendantApi
import io.morfly.pendant.starlark.lang.LanguageFeature
import io.morfly.pendant.starlark.lang.ModifiersHolder
import io.morfly.pendant.starlark.lang.StatementsHolder
import io.morfly.pendant.starlark.lang.context.FunctionCallContext
import io.morfly.pendant.starlark.lang.invokeModifiers

/**
 * Allows declaring Starlark function call statements using Kotlin string as a name.
 *
 * Example:
 * ```
 * "java_binary" {
 *     "name" `=` "app"
 * }
 * ```
 */
internal interface DynamicFunctionsFeature : LanguageFeature,
    StatementsHolder,
    ModifiersHolder {

    /**
     * Generates  Starlark function call with a return type dynamically, using a Kotlin string as its name.
     *
     * Generated Starlark code:
     * android_binary(
     *  name = "app",
     *  deps = []
     * )
     *
     * Kotlin code generator program:
     * "android_binary" {
     *  "name" `=` "app",
     *  "deps" `=` list()
     * }
     */
    @OptIn(InternalPendantApi::class)
    operator fun String.invoke(body: FunctionCallContext.() -> Unit) {
        val functionCallContext = FunctionCallContext(modifiers).apply(body)
        invokeModifiers(functionCallContext)
        val args = functionCallContext.fargs.values.toList()
        registerFunctionCallStatement(name = this, args)
    }

    /**
     * Generates  Starlark function call with a return type dynamically, using a Kotlin string as its name.
     *
     * Generated Starlark code:
     * range(1, 5)
     *
     * Kotlin code generator program:
     * "range"(1, 5)
     */
    @OptIn(InternalPendantApi::class)
    operator fun String.invoke(vararg arguments: Any?) {
        val elements = arguments.map {
            Argument(id = "", Expression(it))
        }
        registerFunctionCallStatement(name = this, elements)
    }
}