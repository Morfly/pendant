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

import io.morfly.pendant.starlark.lang.Argument
import io.morfly.pendant.starlark.lang.FunctionKind.Statement
import io.morfly.pendant.starlark.lang.FunctionScope.Workspace
import io.morfly.pendant.starlark.lang.LibraryFunction
import io.morfly.pendant.starlark.lang.type.*


@LibraryFunction(
    name = "bind",
    scope = [Workspace],
    kind = Statement
)
private interface Bind {

    @Argument(required = true)
    val name: Name
    val actual: Label?
    val visibility: List<Label?>?
}


@LibraryFunction(
    name = "local_repository",
    scope = [Workspace],
    kind = Statement
)
private interface LocalRepository {

    @Argument(required = true)
    val name: Name
    val path: StringType
    val repo_mapping: Map<Key, Value>?
}


@LibraryFunction(
    name = "new_local_repository",
    scope = [Workspace],
    kind = Statement
)
private interface NewLocalRepository {

    @Argument(required = true)
    val name: Name
    val path: StringType
    val repo_mapping: Map<Key, Value>?
}