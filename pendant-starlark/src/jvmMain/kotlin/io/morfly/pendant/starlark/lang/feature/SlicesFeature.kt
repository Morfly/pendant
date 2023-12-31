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

package io.morfly.pendant.starlark.lang.feature

import io.morfly.pendant.starlark.element.*
import io.morfly.pendant.starlark.lang.type.StringType
import io.morfly.pendant.starlark.lang.LanguageFeature


/**
 * Enables slices for Starlark file.
 */
internal interface SlicesFeature : LanguageFeature {

    /**
     * Slice expression for string type.
     */
    operator fun StringType.get(slice: IntProgression): StringSliceExpression =
        StringSliceExpression(
            expression = Expression(this, ::StringLiteral),
            start = slice.first,
            end = slice.last,
            step = null
        )

    /**
     * Slice expression for list type
     */
    operator fun <T> List<T>.get(slice: IntProgression): ListSliceExpression<T> =
        ListSliceExpression(
            expression = Expression(this, ::ListExpression),
            start = slice.first,
            end = slice.last,
            step = null
        )
}