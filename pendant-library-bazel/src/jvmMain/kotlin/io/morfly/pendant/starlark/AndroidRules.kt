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

@file:Suppress("PropertyName", "SpellCheckingInspection", "unused")

package io.morfly.pendant.starlark

import io.morfly.pendant.starlark.lang.type.BooleanType
import io.morfly.pendant.starlark.lang.type.DictionaryType
import io.morfly.pendant.starlark.lang.type.Key
import io.morfly.pendant.starlark.lang.type.Label
import io.morfly.pendant.starlark.lang.type.ListType
import io.morfly.pendant.starlark.lang.type.Name
import io.morfly.pendant.starlark.lang.type.NumberType
import io.morfly.pendant.starlark.lang.type.StringType
import io.morfly.pendant.starlark.lang.type.Value
import io.morfly.pendant.starlark.lang.Argument
import io.morfly.pendant.starlark.lang.FunctionKind.Statement
import io.morfly.pendant.starlark.lang.FunctionScope.Build
import io.morfly.pendant.starlark.lang.FunctionScope.Workspace
import io.morfly.pendant.starlark.lang.LibraryFunction

@LibraryFunction(
    name = "android_library",
    scope = [Build],
    kind = Statement
)
private interface AndroidLibrary {

    @Argument(required = true)
    val name: Name
    val srcs: ListType<Label?>?
    val custom_package: StringType?
    val manifest: Label?
    val exports_manifest: BooleanType?
    val resource_files: ListType<Label?>?
    val enable_data_binding: BooleanType?
    val plugins: ListType<Label?>?
    val deps: ListType<Label?>?
    val visibility: ListType<Label?>?
}


@LibraryFunction(
    name = "android_binary",
    scope = [Build],
    kind = Statement
)
private interface AndroidBinary {

    @Argument(required = true)
    val name: Name
    val custom_package: StringType?
    val manifest: Label?
    val manifest_values: DictionaryType<Key, Value>?
    val debug_key: Label?
    val enable_data_binding: BooleanType?
    val multidex: StringType?
    val incremental_dexing: NumberType?
    val crunch_png: BooleanType?
    val dex_shards: NumberType?
    val resource_files: ListType<Label?>?
    val srcs: ListType<Label?>?
    val plugins: ListType<Label?>?
    val deps: ListType<Label?>?
    val visibility: ListType<Label?>?
    val args: ListType<StringType?>?
    val env: DictionaryType<Key, Value>?
    val output_licenses: ListType<StringType?>?
}


@LibraryFunction(
    name = "aar_import",
    scope = [Build],
    kind = Statement
)
private interface AarImport {

    @Argument(required = true)
    val name: Name
    val aar: Label?
    val exports: ListType<Label?>?
    val srcjar: Label?
    val deps: ListType<Label?>?
    val visibility: ListType<Label?>?
}


@LibraryFunction(
    name = "android_sdk_repository",
    scope = [Workspace],
    kind = Statement
)
private interface AndroidSdkRepository {

    @Argument(required = true)
    val name: Name
    val api_level: NumberType?
    val build_tools_version: StringType?
    val path: StringType?
    val repo_mapping: DictionaryType<Key, Value>?
}


@LibraryFunction(
    name = "android_ndk_repository",
    scope = [Workspace],
    kind = Statement
)
private interface AndroidNdkRepository {

    @Argument(required = true)
    val name: Name
    val api_level: NumberType?
    val path: StringType?
    val repo_mapping: DictionaryType<Key, Value>?
}