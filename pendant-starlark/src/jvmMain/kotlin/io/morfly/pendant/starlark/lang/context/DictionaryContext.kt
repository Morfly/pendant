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

@file:Suppress("LocalVariableName")

package io.morfly.pendant.starlark.lang.context

import io.morfly.pendant.starlark.element.Expression
import io.morfly.pendant.starlark.lang.Checkpoint
import io.morfly.pendant.starlark.lang.CommonExpressionsLibrary
import io.morfly.pendant.starlark.lang.ContextId
import io.morfly.pendant.starlark.lang.LanguageScope
import io.morfly.pendant.starlark.lang.Modifier
import io.morfly.pendant.starlark.lang.feature.CollectionsFeature
import io.morfly.pendant.starlark.lang.feature.DynamicBinaryPlusFeature
import io.morfly.pendant.starlark.lang.feature.MappingFeature
import io.morfly.pendant.starlark.lang.feature.StringExtensionsFeature


/**
 * Starlark context that enables features specific to dictionary expression builder.
 * @see [CollectionsFeature].
 */
@LanguageScope
class DictionaryContext(
    override val modifiers: MutableMap<ContextId, MutableMap<Checkpoint, MutableList<Modifier<*>>>> = linkedMapOf()
) : Context(),
    CommonExpressionsLibrary,
    MappingFeature,
    DynamicBinaryPlusFeature,
    CollectionsFeature,
    StringExtensionsFeature {

    override val kwargs = mutableMapOf<Expression, Expression>()
}