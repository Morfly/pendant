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

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.morfly.pendant.starlark.element.AnyFunctionCall
import io.morfly.pendant.starlark.element.Argument
import io.morfly.pendant.starlark.element.ListExpression
import io.morfly.pendant.starlark.element.ListFunctionCall
import io.morfly.pendant.starlark.element.PositionMode.CONTINUE_LINE
import io.morfly.pendant.starlark.element.PositionMode.NEW_LINE
import io.morfly.pendant.starlark.element.StringLiteral
import io.morfly.pendant.starlark.lang.type.StringType


class FunctionCallFormatterTests : ShouldSpec({
    val formatter = StarlarkCodeFormatter(indentSize = 4)
    val ___4 = " ".repeat(4) // 1st position indentation
    val _______8 = " ".repeat(8) // 2nd position indentation
    val __________12 = " ".repeat(12) // 3rd position indentation
    val ______________16 = " ".repeat(16) // 4rd position indentation

    context("function call formatter") {

        context("NEW LINE mode") {

            should("format function call without arguments") {
                val call = AnyFunctionCall("function", emptyList())

                val builder = StringBuilder()
                formatter.visit(call, position = 1, NEW_LINE, builder)

                val expectedResult = """
                    |${___4}function()
                """.trimMargin()

                builder.toString() shouldBe expectedResult
            }

            should("format function call with one argument") {
                val call = AnyFunctionCall("function", listOf(Argument("arg", StringLiteral("value"))))

                val builder = StringBuilder()
                formatter.visit(call, position = 1, NEW_LINE, builder)

                val expectedResult = """
                    |${___4}function(arg = "value")
                """.trimMargin()

                builder.toString() shouldBe expectedResult
            }

            should("format function call with one argument without name specified") {
                val call = AnyFunctionCall("function", listOf(Argument("", StringLiteral("value"))))

                val builder = StringBuilder()
                formatter.visit(call, position = 1, NEW_LINE, builder)

                val expectedResult = """
                    |${___4}function("value")
                """.trimMargin()

                builder.toString() shouldBe expectedResult
            }

            should("format function call with one multiline argument") {
                val call = AnyFunctionCall(
                    name = "function",
                    args = listOf(
                        Argument(
                            id = "arg",
                            value = ListExpression<Any>(listOf(StringLiteral("item1"), StringLiteral("item2")))
                        )
                    )
                )

                val builder = StringBuilder()
                formatter.visit(call, position = 1, NEW_LINE, builder)

                val expectedResult = """
                    |${___4}function(arg = [
                    |${_______8}"item1",
                    |${_______8}"item2",
                    |${___4}])
                """.trimMargin()

                builder.toString() shouldBe expectedResult
            }

            should("format function call with one multiline argument without name") {
                val call = AnyFunctionCall(
                    name = "function",
                    args = listOf(
                        Argument(
                            id = "",
                            value = ListExpression<Any>(listOf(StringLiteral("item1"), StringLiteral("item2")))
                        )
                    )
                )

                val builder = StringBuilder()
                formatter.visit(call, position = 1, NEW_LINE, builder)

                val expectedResult = """
                    |${___4}function([
                    |${_______8}"item1",
                    |${_______8}"item2",
                    |${___4}])
                """.trimMargin()

                builder.toString() shouldBe expectedResult
            }

            should("format function call") {
                val call = AnyFunctionCall(
                    name = "function",
                    args = listOf(
                        Argument("", ListExpression<Any>(listOf(StringLiteral("item1"), StringLiteral("item2")))),
                        Argument("arg2", StringLiteral("value"))
                    )
                )

                val builder = StringBuilder()
                formatter.visit(call, position = 1, NEW_LINE, builder)

                val expectedResult = """
                    |${___4}function(
                    |${_______8}[
                    |${__________12}"item1",
                    |${__________12}"item2",
                    |${_______8}],
                    |${_______8}arg2 = "value",
                    |${___4})
                """.trimMargin()

                builder.toString() shouldBe expectedResult
            }

            should("format function call with function calls as values") {
                val callExpression1 = ListFunctionCall<StringType>(
                    name = "glob",
                    args = listOf(
                        Argument("", ListExpression<Any>(listOf(StringLiteral("item1"), StringLiteral("item2"))))
                    )
                )
                val callExpression2 = ListFunctionCall<StringType>(
                    name = "glob",
                    args = listOf(
                        Argument("", ListExpression<Any>(listOf(StringLiteral("item3"), StringLiteral("item4")))),
                        Argument("arg3", StringLiteral("value")),
                    )
                )
                val call = AnyFunctionCall(
                    name = "function",
                    args = listOf(
                        Argument("arg1", callExpression1),
                        Argument("arg2", callExpression2)
                    )
                )

                val builder = StringBuilder()
                formatter.visit(call, position = 1, NEW_LINE, builder)

                val expectedResult = """
                    |${___4}function(
                    |${_______8}arg1 = glob([
                    |${__________12}"item1",
                    |${__________12}"item2",
                    |${_______8}]),
                    |${_______8}arg2 = glob(
                    |${__________12}[
                    |${______________16}"item3",
                    |${______________16}"item4",
                    |${__________12}],
                    |${__________12}arg3 = "value",
                    |${_______8}),
                    |${___4})
                """.trimMargin()

                builder.toString() shouldBe expectedResult
            }
        }

        context("CONTINUE LINE mode") {
            should("format function call without arguments") {
                val call = AnyFunctionCall("function", emptyList())

                val builder = StringBuilder()
                formatter.visit(call, position = 1, CONTINUE_LINE, builder)

                val expectedResult = """
                    |function()
                """.trimMargin()

                builder.toString() shouldBe expectedResult
            }

            should("format function call with one argument") {
                val call = AnyFunctionCall("function", listOf(Argument("arg", StringLiteral("value"))))

                val builder = StringBuilder()
                formatter.visit(call, position = 1, CONTINUE_LINE, builder)

                val expectedResult = """
                    |function(arg = "value")
                """.trimMargin()

                builder.toString() shouldBe expectedResult
            }

            should("format function call with one multiline argument") {
                val call = AnyFunctionCall(
                    name = "function",
                    args = listOf(
                        Argument(
                            id = "arg",
                            value = ListExpression<Any>(listOf(StringLiteral("item1"), StringLiteral("item2")))
                        )
                    )
                )

                val builder = StringBuilder()
                formatter.visit(call, position = 1, CONTINUE_LINE, builder)

                val expectedResult = """
                    |function(arg = [
                    |${_______8}"item1",
                    |${_______8}"item2",
                    |${___4}])
                """.trimMargin()

                builder.toString() shouldBe expectedResult
            }

            should("format function call") {
                val call = AnyFunctionCall(
                    name = "function",
                    args = listOf(
                        Argument("", ListExpression<Any>(listOf(StringLiteral("item1"), StringLiteral("item2")))),
                        Argument("arg2", StringLiteral("value"))
                    )
                )

                val builder = StringBuilder()
                formatter.visit(call, position = 1, CONTINUE_LINE, builder)

                val expectedResult = """
                    |function(
                    |${_______8}[
                    |${__________12}"item1",
                    |${__________12}"item2",
                    |${_______8}],
                    |${_______8}arg2 = "value",
                    |${___4})
                """.trimMargin()

                builder.toString() shouldBe expectedResult
            }
        }
    }
})