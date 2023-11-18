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

package io.morfly.pendant.starlark.format

import io.morfly.pendant.starlark.element.Argument
import io.morfly.pendant.starlark.element.Assignment
import io.morfly.pendant.starlark.element.BinaryOperation
import io.morfly.pendant.starlark.element.BooleanLiteral
import io.morfly.pendant.starlark.element.Comment
import io.morfly.pendant.starlark.element.Comprehension
import io.morfly.pendant.starlark.element.DictionaryComprehension
import io.morfly.pendant.starlark.element.DictionaryExpression
import io.morfly.pendant.starlark.element.DynamicExpression
import io.morfly.pendant.starlark.element.Element
import io.morfly.pendant.starlark.element.ElementVisitor
import io.morfly.pendant.starlark.element.EmptyLineStatement
import io.morfly.pendant.starlark.element.ExpressionStatement
import io.morfly.pendant.starlark.element.FloatLiteral
import io.morfly.pendant.starlark.element.FunctionCall
import io.morfly.pendant.starlark.element.IntegerLiteral
import io.morfly.pendant.starlark.element.ListComprehension
import io.morfly.pendant.starlark.element.ListExpression
import io.morfly.pendant.starlark.element.LoadStatement
import io.morfly.pendant.starlark.element.NoneValue
import io.morfly.pendant.starlark.element.PositionMode
import io.morfly.pendant.starlark.element.PositionMode.CONTINUE_LINE
import io.morfly.pendant.starlark.element.PositionMode.NEW_LINE
import io.morfly.pendant.starlark.element.PositionMode.SINGLE_LINE
import io.morfly.pendant.starlark.element.RawStatement
import io.morfly.pendant.starlark.element.RawText
import io.morfly.pendant.starlark.element.Reference
import io.morfly.pendant.starlark.element.SliceExpression
import io.morfly.pendant.starlark.element.StarlarkFile
import io.morfly.pendant.starlark.element.Statement
import io.morfly.pendant.starlark.element.StringLiteral
import io.morfly.pendant.starlark.element.TupleExpression


/**
 * Default indentation value in a Starlark file.
 */
const val DEFAULT_INDENT_SIZE = 4

/**
 * New line character.
 */
internal val nl: String = System.getProperty("line.separator")

/**
 * Formats a syntax tree of Starlark elements to .
 */
open class StarlarkCodeFormatter(indentSize: Int = DEFAULT_INDENT_SIZE) : ElementVisitor<Appendable>, StarlarkFileFormatter {

    protected val indent = " ".repeat(indentSize)

    protected val indentCache = hashMapOf(0 to "", 1 to indent)

    protected fun indent(position: Int): String {
        require(position >= 0) { "Indent 'position' must be non-negative but was $position." }

        return indentCache[position] ?: run {
            if (position in indentCache) indentCache[position]!!
            else {
                val value = indent.repeat(position)
                indentCache[position] = value
                value
            }
        }
    }


    override fun format(starlarkFile: StarlarkFile): String {
        val accumulator = StringBuilder()
        visit(starlarkFile, position = 0, mode = NEW_LINE, accumulator)
        return accumulator.toString()
    }

    override fun format(starlarkFile: StarlarkFile, accumulator: Appendable) {
        visit(starlarkFile, position = 0, mode = NEW_LINE, accumulator)
    }


    override fun visit(element: Element, position: Int, mode: PositionMode, acc: Appendable) {
        element.accept(this, position, mode, acc)
    }

    override fun visit(element: StarlarkFile, position: Int, mode: PositionMode, acc: Appendable) {
        var prev: Statement? = null
        for (statement in element.statements) {
            if (prev != null) acc += nl
            if (shouldInsertEmptyLine(prev, statement)) {
                acc += nl
            }
            visit(statement, position, mode, acc)
            prev = statement
        }

        // New line at the end of file.
        if (prev != null && prev != EmptyLineStatement) {
            acc += nl
        }
    }

    protected fun shouldInsertEmptyLine(prev: Statement?, curr: Statement): Boolean =
        when {
            prev == null -> false
            prev === EmptyLineStatement -> false
            prev is Comment -> false
            prev::class == curr::class -> when {
                curr is ExpressionStatement && curr.expression is FunctionCall -> true
                curr is Assignment -> true
                else -> false
            }

            else -> true
        }

    override fun visit(element: NoneValue, position: Int, mode: PositionMode, acc: Appendable) {
        acc += when (mode) {
            NEW_LINE -> indent(position)
            CONTINUE_LINE -> ""
            SINGLE_LINE -> TODO()
        }
        acc += None
    }

    // TODO test
    override fun visit(element: ExpressionStatement, position: Int, mode: PositionMode, acc: Appendable) {
        visit(element.expression, position, mode, acc)
    }

    override fun visit(element: Argument, position: Int, mode: PositionMode, acc: Appendable) {
        val indent = when (mode) {
            NEW_LINE -> indent(position)
            CONTINUE_LINE -> ""
            SINGLE_LINE -> TODO()
        }

        acc += indent
        if (element.id.isNotBlank())
            acc += element.id + " = "

        visit(element.value, position, CONTINUE_LINE, acc)
    }

    override fun visit(element: Assignment, position: Int, mode: PositionMode, acc: Appendable) {
        require(mode == NEW_LINE) { "Assignment statements must be formatted only in NEW_LINE mode but was $mode." }
        acc += indent(position)
        acc += element.name + " = "
        visit(element.value, position, CONTINUE_LINE, acc)
    }

    override fun visit(element: DynamicExpression, position: Int, mode: PositionMode, acc: Appendable) {
        visit(element.value, position, mode, acc)
    }

    override fun visit(element: BinaryOperation, position: Int, mode: PositionMode, acc: Appendable) {
        visit(element.left, position, mode, acc)
        acc += " ${element.operator} "
        visit(element.right, position, mode = CONTINUE_LINE, acc)
    }

    override fun visit(element: ListExpression<*>, position: Int, mode: PositionMode, acc: Appendable) {
        val indent = indent(position)
        val firstLineIndent = when (mode) {
            NEW_LINE -> indent
            CONTINUE_LINE -> ""
            SINGLE_LINE -> TODO()
        }

        val list = element.value
        when {
            element.shouldFormatSingleLine() -> {
                acc += "$firstLineIndent["
                list.forEachIndexed { i, item ->
                    visit(item, position, CONTINUE_LINE, acc)
                    if (i < list.lastIndex) acc += ", "
                }
                acc += ']'
            }

            else -> {
                acc += "$firstLineIndent[$nl"
                for (item in list) {
                    visit(item, position + 1, NEW_LINE, acc)
                    acc += ",$nl"
                }
                acc += "$indent]"
            }
        }
    }

    override fun visit(element: DictionaryExpression, position: Int, mode: PositionMode, acc: Appendable) {
        val indent = indent(position)
        val firstLineIndent = when (mode) {
            NEW_LINE -> indent
            CONTINUE_LINE -> ""
            SINGLE_LINE -> TODO()
        }

        val dict = element.value.entries
        when (dict.size) {
            0 -> acc += "$firstLineIndent{}"
            1 -> {
                val (key, value) = dict.first()
                acc += "$firstLineIndent{"
                visit(key, position, CONTINUE_LINE, acc)
                acc += ": "
                visit(value, position, CONTINUE_LINE, acc)
                acc += '}'
            }

            else -> {
                acc += "$firstLineIndent{$nl"
                for ((key, value) in dict) {
                    visit(key, position + 1, NEW_LINE, acc)
                    acc += ": "
                    visit(value, position + 1, CONTINUE_LINE, acc)
                    acc += ",$nl"
                }
                acc += "$indent}"
            }
        }
    }

    override fun visit(element: TupleExpression, position: Int, mode: PositionMode, acc: Appendable) {
        val indent = indent(position)
        val firstLineIndent = when (mode) {
            NEW_LINE -> indent
            CONTINUE_LINE -> ""
            SINGLE_LINE -> TODO()
        }

        val list = element.value
        when (list.size) {
            0 -> acc += "$firstLineIndent()"
            1 -> {
                acc += "$firstLineIndent("
                visit(list.first(), position, CONTINUE_LINE, acc)
                acc += ','
                acc += ')'
            }

            else -> {
                acc += "$firstLineIndent($nl"
                for (item in list) {
                    visit(item, position + 1, NEW_LINE, acc)
                    acc += ",$nl"
                }
                acc += "$indent)"
            }
        }
    }

    override fun visit(element: ListComprehension<*>, position: Int, mode: PositionMode, acc: Appendable) {
        val indent = indent(position)
        val firstLineIndent = when (mode) {
            NEW_LINE -> indent
            CONTINUE_LINE -> ""
            SINGLE_LINE -> TODO()
        }

        acc += firstLineIndent
        acc += '['

        val newLine = when (element.body) {
            is BinaryOperation, is Comprehension, is FunctionCall -> nl
            else -> null
        }
        val childrenIndentMode = if (newLine != null) NEW_LINE else CONTINUE_LINE

        acc += newLine ?: ""
        visit(element.body, position + 1, childrenIndentMode, acc)
        acc += newLine ?: " "
        val clauses = element.clauses
        for (i in clauses.indices) {
            visit(clauses[i], position + 1, childrenIndentMode, acc)
            if (i < clauses.lastIndex) acc += newLine ?: " "
        }
        acc += newLine?.let { it + indent } ?: ""
        acc += ']'
    }

    // TODO
    override fun visit(element: DictionaryComprehension<*, *>, position: Int, mode: PositionMode, acc: Appendable) {
        val firstLineIndent = when (mode) {
            NEW_LINE -> indent(position)
            CONTINUE_LINE -> ""
            SINGLE_LINE -> TODO()
        }

        acc += firstLineIndent
        acc += '{'
        acc += '}'
    }

    override fun visit(element: Comprehension.For, position: Int, mode: PositionMode, acc: Appendable) {
        val indent = when (mode) {
            NEW_LINE -> indent(position)
            CONTINUE_LINE -> ""
            SINGLE_LINE -> TODO()
        }
        acc += indent
        acc += "for "
        val variables = element.variables
        for (i in variables.indices) {
            visit(variables[i], position, CONTINUE_LINE, acc)
            if (i < variables.lastIndex) acc += ", "
        }

        acc += " in "
        visit(element.iterable, position, CONTINUE_LINE, acc)
    }

    override fun visit(element: Comprehension.If, position: Int, mode: PositionMode, acc: Appendable) {
        acc += "if "
        visit(element.condition, position, CONTINUE_LINE, acc)
    }

    override fun visit(element: FunctionCall, position: Int, mode: PositionMode, acc: Appendable) {
        val indent = indent(position)
        val firstLineIndent = when (mode) {
            NEW_LINE -> indent
            CONTINUE_LINE -> ""
            SINGLE_LINE -> TODO()
        }

        acc += firstLineIndent
        element.receiver?.let { receiver ->
            visit(receiver, position, CONTINUE_LINE, acc)
            acc += '.'
        }
        val name = element.name
        val args = element.args
        when {
            element.shouldFormatSingleLine() -> {
                acc += "$name("
                args.forEachIndexed { i, item ->
                    visit(item, position, CONTINUE_LINE, acc)
                    if (i < args.lastIndex) acc += ", "
                }
                acc += ')'
            }

            else -> {
                acc += name
                acc += "($nl"
                for (arg in args) {
                    visit(arg, position + 1, NEW_LINE, acc)
                    acc += ",$nl"
                }
                acc += indent
                acc += ')'
            }
        }
    }

    override fun visit(element: StringLiteral, position: Int, mode: PositionMode, acc: Appendable) {
        val indent = indent(position)
        val firstLineIndent = when (mode) {
            NEW_LINE -> indent
            CONTINUE_LINE -> ""
            SINGLE_LINE -> TODO()
        }

        val lines = element.value.lines()
        when (lines.size) {
            1 -> {
                acc += firstLineIndent
                acc += '"'
                acc += lines.first()
                acc += '"'
            }

            else -> {
                acc += firstLineIndent
                acc += "\"\"\""
                acc += nl
                for (line in lines) {
                    acc += indent
                    acc += line
                    acc += nl
                }
                acc += indent
                acc += "\"\"\""
            }
        }
    }

    override fun visit(element: IntegerLiteral, position: Int, mode: PositionMode, acc: Appendable) {
        val indent = when (mode) {
            NEW_LINE -> indent(position)
            CONTINUE_LINE -> ""
            SINGLE_LINE -> TODO()
        }
        acc += "$indent${element.value}"
    }

    override fun visit(element: FloatLiteral, position: Int, mode: PositionMode, acc: Appendable) {
        val indent = when (mode) {
            NEW_LINE -> indent(position)
            CONTINUE_LINE -> ""
            SINGLE_LINE -> TODO()
        }
        acc += "$indent${element.value}"
    }

    override fun visit(element: BooleanLiteral, position: Int, mode: PositionMode, acc: Appendable) {
        val indent = when (mode) {
            NEW_LINE -> indent(position)
            CONTINUE_LINE -> ""
            SINGLE_LINE -> TODO()
        }
        acc += indent
        acc += when (val value = element.value) {
            true -> True
            false -> False
            else -> value.toString()
        }
    }

    override fun visit(element: LoadStatement, position: Int, mode: PositionMode, acc: Appendable) {
        val symbols = element.symbols
        when {
            symbols.size == 1 -> {
                acc += "load("
                visit(element.file, position = 1, CONTINUE_LINE, acc)
                acc += ", "
                visit(symbols.first(), position = 1, CONTINUE_LINE, acc)
                acc += ')'
            }

            symbols.size > 1 -> {
                acc += "load($nl"
                visit(element.file, position = 1, NEW_LINE, acc)
                acc += ",$nl"
                for (symbol in symbols) {
                    visit(symbol, position = 1, NEW_LINE, acc)
                    acc += ",$nl"
                }
                acc += ')'
            }
        }
    }

    override fun visit(element: LoadStatement.Symbol, position: Int, mode: PositionMode, acc: Appendable) {
        val indent = when (mode) {
            NEW_LINE -> indent(position)
            CONTINUE_LINE -> ""
            SINGLE_LINE -> TODO()
        }

        acc += indent
        if (element.alias != null) {
            acc += element.alias
            acc += " = "
        }
        visit(element.name, position, CONTINUE_LINE, acc)
    }

    override fun visit(element: RawStatement, position: Int, mode: PositionMode, acc: Appendable) {
        val indent = indent(position)
        acc += element.value.lineSequence()
            .map { indent + it }
            .joinToString("\n")
    }

    override fun visit(element: Reference, position: Int, mode: PositionMode, acc: Appendable) {
        val indent = when (mode) {
            NEW_LINE -> indent(position)
            CONTINUE_LINE -> ""
            SINGLE_LINE -> TODO()
        }
        acc += indent
        acc += element.name
    }

    override fun visit(element: SliceExpression, position: Int, mode: PositionMode, acc: Appendable) {
        val indent = when (mode) {
            NEW_LINE -> indent(position)
            CONTINUE_LINE -> ""
            SINGLE_LINE -> TODO()
        }
        acc += indent
        visit(element.expression, position, CONTINUE_LINE, acc)
        acc += '['
        acc += element.start?.toString() ?: ""
        acc += ':'
        acc += element.end?.toString() ?: ""
        if (element.step != null) {
            acc += ':'
            acc += element.step.toString()
        }
        acc += ']'
    }

    override fun visit(element: EmptyLineStatement, position: Int, mode: PositionMode, acc: Appendable) {
        acc += nl
    }

    // FIXME
    override fun visit(element: RawText, position: Int, mode: PositionMode, acc: Appendable) {
        acc += element.value
    }

    override fun visit(element: Comment, position: Int, mode: PositionMode, acc: Appendable) {
        val indent = indent(position)
        element.value.lines().forEachIndexed { i, line ->
            if (i != 0) acc += nl
            acc += "$indent# $line"
        }
    }
}