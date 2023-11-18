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
import io.morfly.pendant.descriptor.toArgument
import io.morfly.pendant.starlark.lang.BracketsKind
import java.io.OutputStream
import kotlin.reflect.KClass


class CurlyFunctionGenerator(
    private val scopeResolver: FunctionScopeResolver,
    private val logger: KSPLogger
) : FunctionGenerator() {

    override fun shouldGenerate(function: GeneratedFunction): Boolean =
        BracketsKind.Curly in function.brackets

    override fun generate(file: OutputStream, function: GeneratedFunction) {
        val ctxClassName = generateContext(file, function)

        file += "\n"

        val scopeClasses = scopeResolver.resolve(function.scope, function.kind)
        for (cls in scopeClasses) {
            generate(file, function, cls, ctxClassName)
            file += "\n\n"
        }
    }

    private fun generate(file: OutputStream, function: GeneratedFunction, scopeClass: KClass<*>, ctxClassName: String) {
        val funSlot = when (function.returnType) {
            is SpecifiedType -> "fun"
            DynamicType -> "inline fun <reified T>"
        }
        val returnTypeSlot = when (val type = function.returnType) {
            is SpecifiedType -> ": ${type.fullName}"
            DynamicType -> ": T"
        }

        file += "$funSlot ${scopeClass.simpleName}.`${function.shortName}`(body: $ctxClassName.() -> Unit)$returnTypeSlot ="
        file += "\n"

        val functionBuilderName = function.builderName
        file += "${indent4}$functionBuilderName(\"${function.shortName}\", $ctxClassName(modifiers), body)"
    }

    /**
     * @return name of the context class
     */
    private fun generateContext(file: OutputStream, function: GeneratedFunction): String {
        val ctxClassName = function.annotatedClassName + "Context"
        val allArguments = mutableListOf<NamedArgument>().also {
            if (function.vararg != null && function.vararg.kotlinName.isNotBlank())
                it += function.vararg.toArgument()
            it += function.arguments.filter { arg -> arg.kotlinName.isNotBlank() }
        }

        file += "class $ctxClassName(\n"
        file += "${indent4}modifiers: ModifierCollection = linkedMapOf()"
        file += "\n) : FunctionCallContext(modifiers) {\n"
        for (arg in allArguments) {
            file += indent4
            file += "var ${arg.kotlinName}: ${arg.type.fullName} by fargs\n"
        }
        file += "}\n"

        return ctxClassName
    }
}