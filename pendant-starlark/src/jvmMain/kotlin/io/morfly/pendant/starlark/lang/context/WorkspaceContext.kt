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

@file:Suppress("FunctionName", "unused")

package io.morfly.pendant.starlark.lang.context

import io.morfly.pendant.starlark.element.WorkspaceFile
import io.morfly.pendant.starlark.lang.Checkpoint
import io.morfly.pendant.starlark.lang.ContextId
import io.morfly.pendant.starlark.lang.LanguageScope
import io.morfly.pendant.starlark.lang.Modifier
import io.morfly.pendant.starlark.lang.WorkspaceExpressionsLibrary
import io.morfly.pendant.starlark.lang.WorkspaceStatementsLibrary
import io.morfly.pendant.starlark.lang.invokeModifiers
import io.morfly.pendant.starlark.lang.feature.AssignmentsFeature
import io.morfly.pendant.starlark.lang.feature.BinaryPercentsFeature
import io.morfly.pendant.starlark.lang.feature.BinaryPlusFeature
import io.morfly.pendant.starlark.lang.feature.BooleanValuesFeature
import io.morfly.pendant.starlark.lang.feature.CollectionsFeature
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
 * Starlark language context that is specific to Bazel WORKSPACE files.
 */
@LanguageScope
class WorkspaceContext(
    val hasExtension: Boolean,
    private var body: (WorkspaceContext.() -> Unit)?,
    override val modifiers: MutableMap<ContextId, MutableMap<Checkpoint, MutableList<Modifier<*>>>> = linkedMapOf()
) : FileContext(),
    WorkspaceStatementsLibrary,
    WorkspaceExpressionsLibrary,
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
    ListComprehensionsFeature<WorkspaceContext>,
    SlicesFeature,
    BinaryPercentsFeature,
    BooleanValuesFeature,
    StringExtensionsFeature,
    ReassignmentsFeature {

    override val name = if (hasExtension) "WORKSPACE.bazel" else "WORKSPACE"

    override fun newContext() = WorkspaceContext(hasExtension, body = null, modifiers)

    override fun build(): WorkspaceFile {
        body?.invoke(this)
        invokeModifiers(this)
        return WorkspaceFile(
            hasExtension = hasExtension,
            statements = statements.toList()
        ).also {
            statements.clear()
        }
    }
}

/**
 *
 */
fun WORKSPACE(body: WorkspaceContext.() -> Unit): WorkspaceContext =
    WorkspaceContext(
        hasExtension = false,
        body = body
    )

/**
 *
 */
object WORKSPACE

/**
 *
 */
fun WORKSPACE.bazel(body: WorkspaceContext.() -> Unit): WorkspaceContext =
    WorkspaceContext(
        hasExtension = true,
        body = body
    )