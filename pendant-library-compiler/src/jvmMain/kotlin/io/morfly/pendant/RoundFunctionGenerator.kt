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

package io.morfly.pendant

import com.google.devtools.ksp.processing.KSPLogger
import io.morfly.pendant.descriptor.NamedArgument
import io.morfly.pendant.descriptor.DynamicType
import io.morfly.pendant.descriptor.GeneratedFunction
import io.morfly.pendant.descriptor.SpecifiedType
import io.morfly.pendant.descriptor.toNamedArgument
import io.morfly.pendant.starlark.element.*
import io.morfly.pendant.starlark.lang.BracketsKind
import io.morfly.pendant.starlark.lang.type.*
import java.io.OutputStream
import kotlin.reflect.KClass


class RoundFunctionGenerator(
    private val scopeResolver: FunctionScopeResolver,
    private val logger: KSPLogger
) : FunctionGenerator() {

    override fun shouldGenerate(function: GeneratedFunction): Boolean =
        BracketsKind.Round in function.brackets

    override fun generate(file: OutputStream, function: GeneratedFunction) {
        val scopeClasses = scopeResolver.resolve(function.scope, function.kind)
        for (cls in scopeClasses) {
            generate(file, function, cls)
            file += "\n\n"
        }
    }

    private fun generate(file: OutputStream, function: GeneratedFunction, scopeClass: KClass<*>) {
        generateSignature(file, function, scopeClass)
        generateBody(file, function)

        if (function.vararg != null) {
            file += "\n\n"
            val newFunction = function.copy(
                arguments = mutableListOf<NamedArgument>().also {
                    it += function.vararg.toNamedArgument()
                    it += function.arguments
                },
                vararg = null
            )
            generate(file, newFunction, scopeClass)
        }
    }

    private fun generateSignature(file: OutputStream, function: GeneratedFunction, scopeClass: KClass<*>) {
        val funSlot = when (function.returnType) {
            is SpecifiedType -> "fun"
            DynamicType -> "inline fun <reified T>"
        }
        val returnTypeSlot = when (val type = function.returnType) {
            is SpecifiedType -> ": ${type.fullName}"
            DynamicType -> ": T"
        }

        fun SpecifiedType.defaultValue(): String {
            if (UNSPECIFIED_VALUES_MAPPING[this.actualType.qualifiedName] == null) {
                logger.error(this.actualType.qualifiedName)
            }
            val value = UNSPECIFIED_VALUES_MAPPING[this.actualType.qualifiedName]!!.simpleName
            return " = $value"
        }

        file += "$funSlot ${scopeClass.simpleName}.`${function.shortName}`"

        if (!function.hasArgs) {
            file += "()$returnTypeSlot"
        } else {
            file += "(\n"
            if (function.vararg != null) {
                val vararg = function.vararg
                val comma = if (function.arguments.isNotEmpty()) ",\n" else ""
                file += "${indent4}vararg ${vararg.kotlinName}: ${vararg.type.fullName}$comma"
            }
            for (i in function.arguments.indices) {
                val arg = function.arguments[i]
                val comma = if (i < function.arguments.lastIndex) ",\n" else ""

                val defaultValue = if (!arg.isRequired) arg.type.defaultValue() else ""

                file += "${indent4}${arg.kotlinName}: ${arg.type.fullName}$defaultValue$comma"
            }
            file += "\n)$returnTypeSlot"
        }
    }

    private fun generateBody(file: OutputStream, function: GeneratedFunction) {
        if (!function.hasArgs) {
            file += " {\n"
            file += "${indent4}return ${function.builderName}(\"${function.shortName}\", emptyList())\n"
            file += "}"
            return
        }

        file += " {\n"
        val argsName = "_args"
        file += "${indent4}val $argsName = mutableListOf<Argument>().also {\n"

        if (function.vararg != null) {
            val name = function.vararg.kotlinName
            file += "${indent8}it += Argument(\"\", Expression($name.toList(), ::ListExpression))\n"
        }
        for (arg in function.arguments) {
            val argName = arg.kotlinName

            if (!arg.isRequired) {
                val unspecifiedValue = UNSPECIFIED_VALUES_MAPPING[arg.type.actualType.qualifiedName]!!.simpleName
                file += "${indent8}if ($argName !== $unspecifiedValue)\n"
                file += indent4
            }
            val element = ELEMENT_MAPPING[arg.type.actualType.qualifiedName]
            val elementBuilder = element?.let { ", ::$it" } ?: ""
            file += "${indent8}it += Argument(\"${arg.starlarkName}\", Expression($argName$elementBuilder))\n"
        }

        file += "${indent4}}\n"
        file += "${indent4}return ${function.builderName}(\"${function.shortName}\", $argsName)\n"
        file += "}"
    }

    companion object {

        private val UNSPECIFIED_VALUES_MAPPING = mapOf(
            StringType::class.qualifiedName to UnspecifiedString::class,
            NumberType::class.qualifiedName to UnspecifiedNumber::class,
            ListType::class.qualifiedName to UnspecifiedList::class,
            DictionaryType::class.qualifiedName to UnspecifiedDictionary::class,
            TupleType::class.qualifiedName to UnspecifiedTuple::class,
            BooleanBaseType::class.qualifiedName to UnspecifiedBoolean::class,
            Any::class.qualifiedName to UnspecifiedAny::class
        )

        private val ELEMENT_MAPPING = mapOf(
            StringType::class.qualifiedName to StringLiteral::class.simpleName,
            NumberType::class.qualifiedName to ::NumberLiteral.name,
            ListType::class.qualifiedName to ListExpression::class.simpleName,
            DictionaryType::class.qualifiedName to DictionaryExpression::class.simpleName,
            TupleType::class.qualifiedName to TupleExpression::class.simpleName,
            BooleanBaseType::class.qualifiedName to BooleanLiteral::class.simpleName,
        )
    }
}