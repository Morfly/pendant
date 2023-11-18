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

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import io.morfly.pendant.descriptor.GeneratedFile
import io.morfly.pendant.descriptor.GeneratedFunction
import io.morfly.pendant.descriptor.SpecifiedType
import java.io.OutputStream


interface FileGenerator {
    fun generate(descriptor: GeneratedFile)
}

class FileGeneratorImpl(
    val codeGenerator: CodeGenerator,
    private val functionGenerators: List<FunctionGenerator>,
    private val logger: KSPLogger
) : FileGenerator {

    override fun generate(descriptor: GeneratedFile) {
        val packageName = descriptor.packageName
        val file = codeGenerator
            .createNewFile(Dependencies(true, descriptor.originalFile), packageName, "${descriptor.shortName}Generated")

        file += "// generated file\n\n"
        file += "@file:OptIn(InternalPendantApi::class)\n"
        file += "@file:Suppress(\"REDUNDANT_NULLABLE\")\n\n"

        if (packageName.isNotBlank()) {
            file += "package $packageName\n\n"
        }

        generateImports(file, descriptor.functions)
        file += "\n\n"

        for (func in descriptor.functions) {
            for (i in functionGenerators.indices) {
                val gen = functionGenerators[i]
                if (gen.shouldGenerate(func)) {
                    if (i == 0) file += "// ===== ${func.shortName} =====\n\n"
                    gen.generate(file, func)
                }
            }
        }
        file.close()
    }

    private fun generateImports(file: OutputStream, functions: List<GeneratedFunction>) {
        val imports = mutableSetOf<String>()

        fun addImport(type: SpecifiedType) {
            if (type.packageName !in IGNORED_PACKAGE_NAMES) {
                imports += type.qualifiedName
            }
        }

        for (func in functions) {
            if (func.vararg != null)
                addImport(type = func.vararg.type)

            for (arg in func.arguments)
                addImport(type = arg.type)

            if (func.returnType is SpecifiedType)
                addImport(type = func.returnType)
        }

        imports += DEFAULT_IMPORTS

        for (import in imports) {
            file += "import $import"
            file += "\n"
        }
    }

    companion object {
        private val IGNORED_PACKAGE_NAMES = setOf(
            // common
            "kotlin",
            "kotlin.annotation",
            "kotlin.collections",
            "kotlin.comparisons",
            "kotlin.io",
            "kotlin.ranges",
            "kotlin.sequences",
            "kotlin.text",
            // jvm
            "java.lang",
            "kotlin.jvm",
            // js
            "kotlin.js"
        )

        // FIXME avoid '*' imports in future
        private val DEFAULT_IMPORTS = setOf(
            "io.morfly.pendant.starlark.lang.*",
            "io.morfly.pendant.starlark.lang.feature.*",
            "io.morfly.pendant.starlark.element.Argument",
            "io.morfly.pendant.starlark.element.Expression",
            "io.morfly.pendant.starlark.element.*",
            "io.morfly.pendant.starlark.lang.type.*",
            "io.morfly.pendant.starlark.lang.context.*"
        )
    }
}