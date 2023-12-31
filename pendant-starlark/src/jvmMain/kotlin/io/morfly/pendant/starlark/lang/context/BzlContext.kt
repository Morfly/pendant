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

package io.morfly.pendant.starlark.lang.context

import io.morfly.pendant.starlark.element.BzlFile
import io.morfly.pendant.starlark.lang.InternalPendantApi
import io.morfly.pendant.starlark.lang.LanguageScope
import io.morfly.pendant.starlark.lang.ModifierCollection
import io.morfly.pendant.starlark.lang.StarlarkExpressionsLibrary
import io.morfly.pendant.starlark.lang.StarlarkStatementsLibrary
import io.morfly.pendant.starlark.lang.feature.AssignmentsFeature
import io.morfly.pendant.starlark.lang.feature.BinaryPercentsFeature
import io.morfly.pendant.starlark.lang.feature.BinaryPlusFeature
import io.morfly.pendant.starlark.lang.feature.BinaryPlusTransformationsFeature
import io.morfly.pendant.starlark.lang.feature.BooleanValuesFeature
import io.morfly.pendant.starlark.lang.feature.CollectionsFeature
import io.morfly.pendant.starlark.lang.feature.CommentsFeature
import io.morfly.pendant.starlark.lang.feature.DynamicAssignmentsFeature
import io.morfly.pendant.starlark.lang.feature.DynamicBinaryPlusFeature
import io.morfly.pendant.starlark.lang.feature.DynamicFunctionExpressionsFeature
import io.morfly.pendant.starlark.lang.feature.DynamicFunctionsFeature
import io.morfly.pendant.starlark.lang.feature.EmptyLinesFeature
import io.morfly.pendant.starlark.lang.feature.ListComprehensionsFeature
import io.morfly.pendant.starlark.lang.feature.LoadStatementsFeature
import io.morfly.pendant.starlark.lang.feature.RawTextFeature
import io.morfly.pendant.starlark.lang.feature.ReassignmentsFeature
import io.morfly.pendant.starlark.lang.feature.SlicesFeature
import io.morfly.pendant.starlark.lang.feature.StringExtensionsFeature
import io.morfly.pendant.starlark.lang.invokeModifiers

@LanguageScope
class BzlContext(
    override val fileName: String,
    private var body: (BzlContext.() -> Unit)?,
    override val modifiers: ModifierCollection = linkedMapOf()
) : FileContext(),
    StarlarkStatementsLibrary,
    StarlarkExpressionsLibrary,
    AssignmentsFeature,
    DynamicAssignmentsFeature,
    BinaryPlusFeature,
    DynamicBinaryPlusFeature,
    BinaryPlusTransformationsFeature,
    CollectionsFeature,
    DynamicFunctionsFeature,
    DynamicFunctionExpressionsFeature,
    EmptyLinesFeature,
    RawTextFeature,
    LoadStatementsFeature,
    ListComprehensionsFeature<BzlContext>,
    SlicesFeature,
    BinaryPercentsFeature,
    BooleanValuesFeature,
    StringExtensionsFeature,
    ReassignmentsFeature,
    CommentsFeature {

    override fun newContext() = BzlContext(fileName, body = null, modifiers)

    @OptIn(InternalPendantApi::class)
    override fun build(): BzlFile {
        body?.invoke(this)
        invokeModifiers(this)
        return BzlFile(
            name = fileName,
            statements = statements.toList()
        ).also {
            statements.clear()
        }
    }
}

/**
 * Builder function that allows entering Starlark template engine context and use Kotlin DSL
 */
fun String.bzl(body: BzlContext.() -> Unit): BzlContext =
    BzlContext(
        fileName = "$this.bzl",
        body = body
    )
