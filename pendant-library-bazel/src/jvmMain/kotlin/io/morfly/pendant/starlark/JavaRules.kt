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
import io.morfly.pendant.starlark.lang.FunctionScope
import io.morfly.pendant.starlark.lang.FunctionScope.Build
import io.morfly.pendant.starlark.lang.FunctionScope.Workspace
import io.morfly.pendant.starlark.lang.LibraryFunction
import io.morfly.pendant.starlark.lang.type.*


@LibraryFunction(
    name = "java_library",
    scope = [Build],
    kind = Statement
)
private interface JavaLibrary {

    @Argument(required = true)
    val name: Name
    val srcs: List<Label?>?
    val resources: List<Label?>?
    val exports: List<Label?>?
    val plugins: List<Label?>?
    val exported_plugins: List<Label?>?
    val deps: List<Label?>?
    val visibility: List<Label?>?
    val neverlink: BooleanType?
}


@LibraryFunction(
    name = "java_binary",
    scope = [Build],
    kind = Statement
)
private interface JavaBinary {

    @Argument(required = true)
    val name: Name
    val srcs: List<Label?>?
    val resources: List<Label?>?
    val exports: List<Label?>?
    val plugins: List<Label?>?
    val main_class: StringType?
    val deps: List<Label?>?
    val visibility: List<Label?>?
    val args: List<StringType?>?
    val env: Map<Key, Value>?
    val output_licenses: List<StringType?>?
}


@LibraryFunction(
    name = "java_import",
    scope = [Build],
    kind = Statement
)
private interface JavaImport {

    @Argument(required = true)
    val name: Name
    val jars: List<Label?>?
    val exports: List<Label?>?
    val deps: List<Label?>?
    val visibility: List<Label?>?
    val neverlink: BooleanType?
}


@LibraryFunction(
    name = "java_plugin",
    scope = [Build],
    kind = Statement
)
private interface JavaPlugin {

    @Argument(required = true)
    val name: Name
    val processor_class: StringType?
    val generates_api: BooleanType?
    val deps: List<Label?>?
    val visibility: List<Label?>?
    val neverlink: BooleanType?
}

@LibraryFunction(
    name = "rules_java_dependencies",
    scope = [Workspace],
    kind = Statement
)
private interface RulesJavaDependencies

@LibraryFunction(
    name = "rules_java_toolchains",
    scope = [Workspace],
    kind = Statement
)
private interface RulesJavaToolchains