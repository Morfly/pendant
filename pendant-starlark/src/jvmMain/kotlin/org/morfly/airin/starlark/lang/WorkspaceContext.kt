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

package org.morfly.airin.starlark.lang

import org.morfly.airin.starlark.elements.WorkspaceFile
import org.morfly.airin.starlark.lang.api.*
import org.morfly.airin.starlark.lang.feature.*


/**
 * Starlark language context that is specific to Bazel WORKSPACE files.
 */
@LanguageScope
class WorkspaceContext(
    val hasExtension: Boolean,
    override val modifiers: MutableMap<String, MutableList<Modifier<*>>> = mutableMapOf()
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
    StringExtensionsFeature {

    override val fileName = if (hasExtension) "WORKSPACE.bazel" else "WORKSPACE"

    override fun newContext() = WorkspaceContext(hasExtension, modifiers)
}

fun WorkspaceContext.build(): WorkspaceFile =
    WorkspaceFile(
        hasExtension = hasExtension,
        statements = statements.toList()
    )

/**
 *
 */
inline fun WORKSPACE(body: WorkspaceContext.() -> Unit): WorkspaceContext =
    WorkspaceContext(hasExtension = false)
        .apply(body)

/**
 *
 */
object WORKSPACE

/**
 *
 */
inline fun WORKSPACE.bazel(body: WorkspaceContext.() -> Unit): WorkspaceContext =
    WorkspaceContext(hasExtension = true)
        .apply(body)