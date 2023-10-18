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

package io.morfly.pendant

import io.morfly.pendant.starlark.lang.BuildExpressionsLibrary
import io.morfly.pendant.starlark.lang.BuildStatementsLibrary
import io.morfly.pendant.starlark.lang.CommonExpressionsLibrary
import io.morfly.pendant.starlark.lang.CommonStatementsLibrary
import io.morfly.pendant.starlark.lang.FunctionKind
import io.morfly.pendant.starlark.lang.StarlarkExpressionsLibrary
import io.morfly.pendant.starlark.lang.StarlarkStatementsLibrary
import io.morfly.pendant.starlark.lang.WorkspaceExpressionsLibrary
import io.morfly.pendant.starlark.lang.WorkspaceStatementsLibrary
import io.morfly.pendant.starlark.lang.FunctionKind.Expression
import io.morfly.pendant.starlark.lang.FunctionKind.Statement
import io.morfly.pendant.starlark.lang.FunctionScope
import io.morfly.pendant.starlark.lang.FunctionScope.*
import kotlin.reflect.KClass


interface FunctionScopeResolver {

    fun resolve(scope: Set<FunctionScope>, kind: FunctionKind): Set<KClass<*>>

    fun resolve(scope: FunctionScope, kind: FunctionKind): KClass<*>
}

class FunctionScopeResolverImpl : FunctionScopeResolver {

    override fun resolve(scope: Set<FunctionScope>, kind: FunctionKind): Set<KClass<*>> {
        val result = mutableSetOf<KClass<*>>()

        if (scope.isEmpty() || scope.size == FunctionScope.values().size)
            result += when (kind) {
                Statement -> CommonStatementsLibrary::class
                Expression -> CommonExpressionsLibrary::class
            }
        else for (s in scope)
            result += resolve(s, kind)

        return result
    }

    override fun resolve(scope: FunctionScope, kind: FunctionKind): KClass<*> =
        when (scope) {
            Starlark -> when (kind) {
                Statement -> StarlarkStatementsLibrary::class
                Expression -> StarlarkExpressionsLibrary::class
            }
            Workspace -> when (kind) {
                Statement -> WorkspaceStatementsLibrary::class
                Expression -> WorkspaceExpressionsLibrary::class
            }
            Build -> when (kind) {
                Statement -> BuildStatementsLibrary::class
                Expression -> BuildExpressionsLibrary::class
            }
        }
}