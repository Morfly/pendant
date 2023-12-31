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

package io.morfly.pendant.starlark.format

import io.morfly.pendant.starlark.element.PositionMode.CONTINUE_LINE
import io.morfly.pendant.starlark.element.PositionMode.NEW_LINE
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.morfly.pendant.starlark.element.NoneValue


class ExpressionFormatterTests : ShouldSpec({
    val formatter = StarlarkCodeFormatter(indentSize = 4)
    val ___4 = " ".repeat(4) // 1st position indentation

    context("expression formatter") {

        context("NEW LINE mode") {

            should("format null as expression") {
                val none = NoneValue

                val builder = StringBuilder()
                formatter.visit(none, position = 1, NEW_LINE, builder)

                val expectedResult = "${___4}None"

                builder.toString() shouldBe expectedResult
            }
        }

        context("CONTINUE LINE mode") {

            should("format null as expression") {
                val none = NoneValue

                val builder = StringBuilder()
                formatter.visit(NoneValue, position = 1, CONTINUE_LINE, builder)

                val expectedResult = "None"

                builder.toString() shouldBe expectedResult
            }
        }
    }
})