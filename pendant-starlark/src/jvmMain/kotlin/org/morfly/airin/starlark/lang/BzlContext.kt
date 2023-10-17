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

import org.morfly.airin.starlark.elements.BzlFile
import org.morfly.airin.starlark.lang.api.*
import org.morfly.airin.starlark.lang.feature.*


/**
 *
 */
@LanguageScope
class BzlContext(
    override val fileName: String,
    private var body: (BzlContext.() -> Unit)?,
    override val modifiers: MutableMap<String, MutableList<Modifier<*>>> = linkedMapOf()
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

    var invoked = false
        private set

    override fun newContext() = BzlContext(fileName, body = null, modifiers)

    fun build(): BzlFile {
        if (!invoked) {
            invoked = true
            body?.invoke(this)
            invokeModifiers(this)
        }
        return BzlFile(
            name = fileName,
            relativePath = "",
            statements = statements.toList()
        )
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
