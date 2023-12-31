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

@file:Suppress("PropertyName", "unused")

package io.morfly.pendant.starlark

import io.morfly.pendant.starlark.lang.type.Key
import io.morfly.pendant.starlark.lang.type.Label
import io.morfly.pendant.starlark.lang.type.Name
import io.morfly.pendant.starlark.lang.type.Value
import io.morfly.pendant.starlark.lang.Argument
import io.morfly.pendant.starlark.lang.FunctionKind.Statement
import io.morfly.pendant.starlark.lang.FunctionScope.Workspace
import io.morfly.pendant.starlark.lang.LibraryFunction


@LibraryFunction(
    name = "register_toolchains",
    scope = [Workspace],
    kind = Statement
)
private interface RegisterToolchains {

    // TODO introduce Starlark analog of vararg
    @Argument(name = "", required = true)
    val toolchain: Label
}


@LibraryFunction(
    name = "workspace",
    scope = [Workspace],
    kind = Statement
)
interface WorkspaceFunction {

    @Argument(required = true)
    val name: Name
    val managed_directories: Map<Key, Value>?
}