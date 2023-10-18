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

package org.morfly.airin.starlark.lang

import org.morfly.airin.starlark.elements.BuildFile
import org.morfly.airin.starlark.lang.api.BuildExpressionsLibrary
import org.morfly.airin.starlark.lang.api.BuildStatementsLibrary
import org.morfly.airin.starlark.lang.api.Checkpoint
import org.morfly.airin.starlark.lang.api.FileContext
import org.morfly.airin.starlark.lang.api.Id
import org.morfly.airin.starlark.lang.api.LanguageScope
import org.morfly.airin.starlark.lang.api.Modifier
import org.morfly.airin.starlark.lang.api.invokeModifiers
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
 * Starlark language context that is specific to Bazel BUILD files.
 */
@LanguageScope
class BuildContext(
    val hasExtension: Boolean,
    private var body: (BuildContext.() -> Unit)?,
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
    ListComprehensionsFeature<BuildContext>,
    SlicesFeature,
    BinaryPercentsFeature,
    BooleanValuesFeature,
    StringExtensionsFeature {

    override val fileName = if (hasExtension) "BUILD.bazel" else "BUILD"

    override fun newContext() = BuildContext(hasExtension, body = null, modifiers)

    fun build(): BuildFile {
        body?.invoke(this)
        invokeModifiers(this)
        return BuildFile(
            hasExtension = hasExtension,
            statements = statements.toList()
        ).also {
            statements.clear()
        }
    }
}

/**
 * Builder function that allows entering Starlark template engine context and use Kotlin DSL
 */
fun BUILD(body: BuildContext.() -> Unit): BuildContext =
    BuildContext(
        hasExtension = false,
        body = body
    )

/**
 *
 */
object BUILD

/**
 *
 */
fun BUILD.bazel(body: BuildContext.() -> Unit): BuildContext =
    BuildContext(
        hasExtension = true,
        body = body
    )