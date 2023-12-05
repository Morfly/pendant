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

import io.morfly.pendant.starlark.element.BinaryOperator.PERCENT
import io.morfly.pendant.starlark.element.Expression
import io.morfly.pendant.starlark.element.NumberBinaryOperation
import io.morfly.pendant.starlark.element.NumberLiteral
import io.morfly.pendant.starlark.element.StringBinaryOperation
import io.morfly.pendant.starlark.element.StringLiteral
import io.morfly.pendant.starlark.lang.LanguageFeature
import io.morfly.pendant.starlark.lang.type.NumberType
import io.morfly.pendant.starlark.lang.type.StringType

/**
 * Allows using percent Starlark operator with the code generator.
 */
internal interface BinaryPercentsFeature : LanguageFeature {

    /**
     * Using percent operator with strings.
     */
    infix fun StringType?.`%`(other: StringType?): StringType =
        StringBinaryOperation(
            left = Expression(this, ::StringLiteral),
            operator = PERCENT,
            right = Expression(other, ::StringLiteral)
        )

    /**
     * Using percent operator with numbers.
     */
    infix fun NumberType?.`%`(other: NumberType): NumberType =
        NumberBinaryOperation(
            left = Expression(this, ::NumberLiteral),
            operator = PERCENT,
            right = Expression(other, ::NumberLiteral)
        )
}