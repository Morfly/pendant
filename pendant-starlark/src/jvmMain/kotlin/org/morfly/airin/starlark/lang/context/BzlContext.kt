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

package org.morfly.airin.starlark.lang.context

import org.morfly.airin.starlark.elements.BzlFile
import org.morfly.airin.starlark.lang.BuildExpressionsLibrary
import org.morfly.airin.starlark.lang.BuildStatementsLibrary
import org.morfly.airin.starlark.lang.Checkpoint
import org.morfly.airin.starlark.lang.Id
import org.morfly.airin.starlark.lang.LanguageScope
import org.morfly.airin.starlark.lang.Modifier
import org.morfly.airin.starlark.lang.invokeModifiers
import org.morfly.airin.starlark.lang.feature.AssignmentsFeature
import org.morfly.airin.starlark.lang.feature.BinaryPercentsFeature
import org.morfly.airin.starlark.lang.feature.BinaryPlusFeature
import org.morfly.airin.starlark.lang.feature.BooleanValuesFeature
import org.morfly.airin.starlark.lang.feature.CollectionsFeature
import org.morfly.airin.starlark.lang.feature.DynamicAssignmentsFeature
import org.morfly.airin.starlark.lang.feature.DynamicBinaryPlusFeature
import org.morfly.airin.starlark.lang.feature.DynamicFunctionExpressionsFeature
import org.morfly.airin.starlark.lang.feature.DynamicFunctionsFeature
import org.morfly.airin.starlark.lang.feature.EmptyLinesFeature
import org.morfly.airin.starlark.lang.feature.ListComprehensionsFeature
import org.morfly.airin.starlark.lang.feature.LoadStatementsFeature
import org.morfly.airin.starlark.lang.feature.RawTextFeature
import org.morfly.airin.starlark.lang.feature.SlicesFeature
import org.morfly.airin.starlark.lang.feature.StringExtensionsFeature


/**
 *
 */
@LanguageScope
class BzlContext(
    override val fileName: String,
    private var body: (BzlContext.() -> Unit)?,
    override val modifiers: MutableMap<Id, MutableMap<Checkpoint, MutableList<Modifier<*>>>> = linkedMapOf()
) : FileContext(),
    BuildStatementsLibrary,
    BuildExpressionsLibrary,
    AssignmentsFeature,
    DynamicAssignmentsFeature,
    BinaryPlusFeature,
    DynamicBinaryPlusFeature,
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
    StringExtensionsFeature {

    override fun newContext() = BzlContext(fileName, body = null, modifiers)

    fun build(): BzlFile {
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
        fileName = this,
        body = body
    )
