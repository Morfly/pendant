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
import io.morfly.pendant.starlark.lang.BracketsKind.Round
import io.morfly.pendant.starlark.lang.FunctionKind.Expression
import io.morfly.pendant.starlark.lang.FunctionKind.Statement
import io.morfly.pendant.starlark.lang.FunctionScope.Build
import io.morfly.pendant.starlark.lang.FunctionScope.Starlark
import io.morfly.pendant.starlark.lang.FunctionScope.Workspace
import io.morfly.pendant.starlark.lang.LibraryFunction
import io.morfly.pendant.starlark.lang.Returns
import io.morfly.pendant.starlark.lang.type.BooleanType
import io.morfly.pendant.starlark.lang.type.Key
import io.morfly.pendant.starlark.lang.type.Label
import io.morfly.pendant.starlark.lang.type.StringType
import io.morfly.pendant.starlark.lang.type.Value


@LibraryFunction(
    name = "maven_install",
    scope = [Build, Workspace],
    kind = Statement
)
private interface MavenInstall {

    val name: StringType?
    val artifacts: List<StringType?>?
    val repositories: List<StringType?>?
    val fail_on_missing_checksum: BooleanType?
    val fetch_sources: BooleanType?
    val excluded_artifacts: List<StringType?>?
    val override_targets: Map<Key, Value>?
    val generate_compat_repositories: BooleanType?
    val strict_visibility: BooleanType?
    val jetify: BooleanType?
    val jetify_include_list: List<StringType?>?
    val version_conflict_policy: StringType?
}


@LibraryFunction(
    name = "artifact",
    scope = [Build, Workspace, Starlark],
    kind = Expression,
    brackets = [Round]
)
private interface Artifact {

    @Argument(underlyingName = "", required = true)
    val artifact: StringType

    @Returns
    val returns: Label
}

@LibraryFunction(
    name = "rules_jvm_external_deps",
    scope = [Workspace],
    kind = Statement
)
private interface RulesJvmExternalDeps

@LibraryFunction(
    name = "rules_jvm_external_setup",
    scope = [Workspace],
    kind = Statement
)
private interface RulesJvmExternalSetup
