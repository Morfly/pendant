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
    private var body: (WorkspaceContext.() -> Unit)?,
    override val modifiers: MutableMap<String, MutableList<Modifier<*>>> = linkedMapOf()
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

    var invoked = false
        private set

    override val fileName = if (hasExtension) "WORKSPACE.bazel" else "WORKSPACE"

    override fun newContext() = WorkspaceContext(hasExtension, body = null, modifiers)

    fun build(): WorkspaceFile {
        if (!invoked) {
            invoked = true
            body?.invoke(this)
            invokeModifiers(this)
        }
        return WorkspaceFile(
            hasExtension = hasExtension,
            statements = statements.toList()
        )
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