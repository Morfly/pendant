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

package io.morfly.pendant.starlark.element


/**
 *  A visitor for traversing the elements of a syntax tree in lexical order.
 */
interface ElementVisitor<A> {

    fun visit(element: Element, position: Int, mode: PositionMode, acc: A)

    fun visit(element: StarlarkFile, position: Int, mode: PositionMode, acc: A)

    fun visit(element: NoneValue, position: Int, mode: PositionMode, acc: A)

    fun visit(element: ExpressionStatement, position: Int, mode: PositionMode, acc: A)

    fun visit(element: Argument, position: Int, mode: PositionMode, acc: A)

    fun visit(element: Assignment, position: Int, mode: PositionMode, acc: A)

    fun visit(element: DynamicExpression, position: Int, mode: PositionMode, acc: A)

    fun visit(element: BinaryOperation, position: Int, mode: PositionMode, acc: A)

    fun visit(element: ListExpression<*>, position: Int, mode: PositionMode, acc: A)

    fun visit(element: DictionaryExpression, position: Int, mode: PositionMode, acc: A)

    fun visit(element: TupleExpression, position: Int, mode: PositionMode, acc: A)

    fun visit(element: ListComprehension<*>, position: Int, mode: PositionMode, acc: A)

    fun visit(element: DictionaryComprehension<*, *>, position: Int, mode: PositionMode, acc: A)

    fun visit(element: Comprehension.For, position: Int, mode: PositionMode, acc: A)

    fun visit(element: Comprehension.If, position: Int, mode: PositionMode, acc: A)

    fun visit(element: FunctionCall, position: Int, mode: PositionMode, acc: A)

    fun visit(element: StringLiteral, position: Int, mode: PositionMode, acc: A)

    fun visit(element: IntegerLiteral, position: Int, mode: PositionMode, acc: A)

    fun visit(element: FloatLiteral, position: Int, mode: PositionMode, acc: A)

    fun visit(element: BooleanLiteral, position: Int, mode: PositionMode, acc: A)

    fun visit(element: LoadStatement, position: Int, mode: PositionMode, acc: A)

    fun visit(element: LoadStatement.Symbol, position: Int, mode: PositionMode, acc: A)

    fun visit(element: RawStatement, position: Int, mode: PositionMode, acc: A)

    fun visit(element: Reference, position: Int, mode: PositionMode, acc: A)

    fun visit(element: SliceExpression, position: Int, mode: PositionMode, acc: A)

    fun visit(element: EmptyLineStatement, position: Int, mode: PositionMode, acc: A)

    fun visit(element: RawText, position: Int, mode: PositionMode, acc: A)

    fun visit(element: Comment, position: Int, mode: PositionMode, acc: A)
}