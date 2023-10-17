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

import org.morfly.airin.starlark.elements.Expression
import org.morfly.airin.starlark.elements.StringFunctionCall
import org.morfly.airin.starlark.elements.StringLiteral
import org.morfly.airin.starlark.types.StringType
import org.morfly.airin.starlark.lang.api.ModifiersHolder
import org.morfly.airin.starlark.lang.api.LanguageFeature
import org.morfly.airin.starlark.lang.api.asSet
import org.morfly.airin.starlark.lang.api.invokeModifiers


internal interface StringExtensionsFeature : LanguageFeature,
    ModifiersHolder {

    fun StringType.format(body: FunctionCallContext.() -> Unit): StringType {
        val context = FunctionCallContext(modifiers).apply(body)
        invokeModifiers(context)
        return StringFunctionCall(
            name = "format",
            args = context.fargs.asSet(),
            receiver = Expression(this, ::StringLiteral)
        )
    }
}