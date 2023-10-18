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

package org.morfly.airin.starlark.lang.context

import org.morfly.airin.starlark.elements.Argument
import org.morfly.airin.starlark.lang.api.Checkpoint
import org.morfly.airin.starlark.lang.api.CommonExpressionsLibrary
import org.morfly.airin.starlark.lang.api.Context
import org.morfly.airin.starlark.lang.api.Id
import org.morfly.airin.starlark.lang.api.Modifier
import org.morfly.airin.starlark.lang.api.LanguageScope
import org.morfly.airin.starlark.lang.feature.ArgumentsFeature
import org.morfly.airin.starlark.lang.feature.BinaryPlusFeature
import org.morfly.airin.starlark.lang.feature.BooleanValuesFeature
import org.morfly.airin.starlark.lang.feature.CollectionsFeature
import org.morfly.airin.starlark.lang.feature.DynamicArgumentsFeature
import org.morfly.airin.starlark.lang.feature.DynamicBinaryPlusFeature
import org.morfly.airin.starlark.lang.feature.StringExtensionsFeature


/**
 *
 */
@LanguageScope
open class FunctionCallContext(
    override val modifiers: MutableMap<Id, MutableMap<Checkpoint, MutableList<Modifier<*>>>> = linkedMapOf()
) : Context(),
    CommonExpressionsLibrary,
    ArgumentsFeature,
    DynamicArgumentsFeature,
    BinaryPlusFeature,
    DynamicBinaryPlusFeature,
    CollectionsFeature,
    BooleanValuesFeature,
    StringExtensionsFeature {

    override val fargs = linkedMapOf<String, Argument>()
}