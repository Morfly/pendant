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

import io.morfly.pendant.starlark.element.Expression
import io.morfly.pendant.starlark.element.StringFunctionCall
import io.morfly.pendant.starlark.element.StringLiteral
import io.morfly.pendant.starlark.lang.InternalPendantApi
import io.morfly.pendant.starlark.lang.LanguageFeature
import io.morfly.pendant.starlark.lang.ModifiersHolder
import io.morfly.pendant.starlark.lang.context.FunctionCallContext
import io.morfly.pendant.starlark.lang.invokeModifiers
import io.morfly.pendant.starlark.lang.type.StringType

/**
 * Adds library functions that serve as extensions to existing types.
 */
internal interface StringExtensionsFeature : LanguageFeature,
    ModifiersHolder {

    /**
     * Generates call of a string formatting function.
     */
    @OptIn(InternalPendantApi::class)
    fun StringType.format(body: FunctionCallContext.() -> Unit): StringType {
        val context = FunctionCallContext(modifiers).apply(body)
        invokeModifiers(context)
        return StringFunctionCall(
            name = "format",
            args = context.fargs.values.toList(),
            receiver = Expression(this, ::StringLiteral)
        )
    }
}