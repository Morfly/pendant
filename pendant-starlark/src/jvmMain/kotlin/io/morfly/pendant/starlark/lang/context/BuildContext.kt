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

import io.morfly.pendant.starlark.element.BuildFile
import io.morfly.pendant.starlark.lang.BuildExpressionsLibrary
import io.morfly.pendant.starlark.lang.BuildStatementsLibrary
import io.morfly.pendant.starlark.lang.Checkpoint
import io.morfly.pendant.starlark.lang.ContextId
import io.morfly.pendant.starlark.lang.LanguageScope
import io.morfly.pendant.starlark.lang.Modifier
import io.morfly.pendant.starlark.lang.invokeModifiers
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


/**
 * Starlark language context that is specific to Bazel BUILD files.
 */
@LanguageScope
class BuildContext(
    val hasExtension: Boolean,
    private var body: (BuildContext.() -> Unit)?,
    override val modifiers: MutableMap<ContextId, MutableMap<Checkpoint, MutableList<Modifier<*>>>> = linkedMapOf()
) : FileContext(),
    BuildStatementsLibrary,
    BuildExpressionsLibrary,
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
    ListComprehensionsFeature<BuildContext>,
    SlicesFeature,
    BinaryPercentsFeature,
    BooleanValuesFeature,
    StringExtensionsFeature,
    ReassignmentsFeature,
    CommentsFeature {

    override val fileName = if (hasExtension) "BUILD.bazel" else "BUILD"

    override fun newContext() = BuildContext(hasExtension, body = null, modifiers)

    override fun build(): BuildFile {
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