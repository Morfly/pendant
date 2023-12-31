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

package io.morfly.pendant.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind.INTERFACE
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Variance
import com.google.devtools.ksp.validate
import io.morfly.pendant.FileGenerator
import io.morfly.pendant.descriptor.DynamicType
import io.morfly.pendant.descriptor.GeneratedFile
import io.morfly.pendant.descriptor.GeneratedFunction
import io.morfly.pendant.descriptor.NamedArgument
import io.morfly.pendant.descriptor.SpecifiedType
import io.morfly.pendant.descriptor.Type
import io.morfly.pendant.descriptor.VariadicArgument
import io.morfly.pendant.descriptor.VoidType
import io.morfly.pendant.starlark.lang.Argument
import io.morfly.pendant.starlark.lang.FunctionKind
import io.morfly.pendant.starlark.lang.FunctionKind.Expression
import io.morfly.pendant.starlark.lang.LibraryFunction
import io.morfly.pendant.starlark.lang.PENDANT_ARGUMENT_DEFAULT
import io.morfly.pendant.starlark.lang.ReturnKind
import io.morfly.pendant.starlark.lang.Returns
import io.morfly.pendant.starlark.lang.type.ListType
import io.morfly.pendant.toBracketsKind
import io.morfly.pendant.toDisplayableString
import io.morfly.pendant.toFunctionCallKind
import io.morfly.pendant.toFunctionScope
import io.morfly.pendant.toMap
import io.morfly.pendant.toReturnKind
import io.morfly.pendant.valueAs
import io.morfly.pendant.valueAsOrNull


private typealias FilePath = String

class LibraryGenerator(
    private val options: Map<String, String>,
    private val fileGenerator: FileGenerator,
    private val typeValidator: TypeValidator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private var invoked = false

    private val libraryFiles = mutableMapOf<FilePath, GeneratedFile>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) return emptyList()

        val symbols = resolver.getSymbolsWithAnnotation(LibraryFunction::class.qualifiedName!!)

        symbols.filterIsInstance<KSClassDeclaration>()
            .forEach { it.accept(Visitor(), Unit) }

        for (file in libraryFiles.values) {
            validate(file)
            fileGenerator.generate(file)
        }

        invoked = true
        return emptyList()
    }

    private fun validate(file: GeneratedFile) {
        val functionNames = mutableSetOf<String>()

        for (func in file.functions) {
            if (func.shortName in functionNames) {
                val message = "Duplicate function declarations found with name '${func.shortName}'"
                logger.error(message, file.originalFile)
            }
            functionNames += func.shortName
        }
    }

    private inner class Visitor : KSVisitorVoid() {
        private val resolvedTypesCache = mutableMapOf<KSTypeReference, KSType>()

        private lateinit var functionKind: FunctionKind
        private val functionArguments = mutableListOf<NamedArgument>()
        private var returnType: Type? = null
        private var varargArgument: VariadicArgument? = null

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            if (classDeclaration.classKind != INTERFACE) {
                val message = "@${LibraryFunction::class.simpleName} must target only interfaces."
                logger.error(message, classDeclaration)
                return
            }

            val annotation = classDeclaration.annotations.first {
                it.shortName.asString() == LibraryFunction::class.simpleName
            }

            val arguments = annotation.arguments.toMap()
            val defaults = annotation.defaultArguments.toMap()

            val name = arguments.valueAs<String>(LibraryFunction::name.name)
            val scope = arguments.valueAs<List<KSType>>(LibraryFunction::scope.name).map(KSType::toFunctionScope)
            functionKind = arguments.valueAs<KSType>(LibraryFunction::kind.name).toFunctionCallKind()
            val brackets =
                arguments.valueAsOrNull<List<KSType>>(LibraryFunction::brackets.name)?.map(KSType::toBracketsKind)
                    ?: defaults.valueAs<List<KSType>>(LibraryFunction::brackets.name).map(KSType::toBracketsKind)

            classDeclaration.getAllProperties()
                .filter { it.validate() }
                .forEach { visitPropertyDeclaration(it, Unit) }

            val file = classDeclaration.containingFile!!
            val generatedFile = libraryFiles.getOrPut(file.filePath) {
                GeneratedFile(
                    shortName = file.fileName.removeSuffix(".kt"),
                    packageName = file.packageName.asString(),
                    originalFile = file
                )
            }

            if (functionKind == Expression) {
                if (returnType == null || returnType == VoidType) {
                    val message = "An 'expression' function must have non-void return type"
                    logger.error(message, classDeclaration)
                    return
                }
            }
            generatedFile.functions += GeneratedFunction(
                shortName = name,
                annotatedClassName = classDeclaration.simpleName.asString(),
                arguments = functionArguments,
                vararg = varargArgument,
                returnType = returnType ?: VoidType,
                scope = scope.toSet(),
                kind = functionKind,
                brackets = brackets.toSet()
            )
        }

        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
            val argAnnotation = property.annotations.firstOrNull {
                it.shortName.asString() == Argument::class.simpleName
            }
            val retAnnotation = property.annotations.firstOrNull {
                it.shortName.asString() == Returns::class.simpleName
            }
            if (functionKind == FunctionKind.Statement && retAnnotation != null) {
                val message = "A 'statement' function must not have specified return type"
                logger.error(message, property)
                return
            }
            if (argAnnotation != null && retAnnotation != null) {
                val arg = Argument::class.simpleName
                val ret = Returns::class.simpleName
                val message = "A property can not be annotated both with @$arg and @$ret."
                logger.error(message, property)
                return
            }

            if (retAnnotation != null)
                visitReturnProperty(property, retAnnotation)
            else visitArgumentProperty(property, argAnnotation)
        }

        private fun visitArgumentProperty(
            property: KSPropertyDeclaration, annotation: KSAnnotation?
        ) {
            val actualQualifiedName = property.type.findActualType()
                .getOrResolve().declaration
                .qualifiedName!!.asString()

            val arguments = annotation?.arguments?.toMap()
            val defaults = annotation?.defaultArguments?.toMap()

            val kotlinName = property.simpleName.asString()
            val starlarkName = arguments?.valueAsOrNull<String>(Argument::name.name)
                ?.takeIf { it != PENDANT_ARGUMENT_DEFAULT }
                ?: kotlinName
            val required = arguments?.valueAsOrNull<Boolean>(Argument::required.name)
                ?: defaults?.valueAs<Boolean>(Argument::required.name)
                ?: false
            val variadic = arguments?.valueAsOrNull<Boolean>(Argument::variadic.name)
                ?: defaults?.valueAs<Boolean>(Argument::variadic.name)
                ?: false
            val implicit = arguments?.valueAsOrNull<Boolean>(Argument::implicit.name)
                ?: defaults?.valueAs<Boolean>(Argument::implicit.name)
                ?: false

            val isVararg = if (variadic) {
                when (actualQualifiedName) {
                    ListType::class.qualifiedName -> {
                        if (varargArgument != null) {
                            val message = "Function must have not more than one vararg argument."
                            logger.error(message, property)
                            return
                        }
                        true
                    }

                    else -> {
                        val message = "Only arguments of list type must be used as vararg."
                        logger.error(message, property)
                        return
                    }
                }
            } else false

            if (isVararg) {
                val typeDescriptor = visitTypeReference(property.type)
                varargArgument = VariadicArgument(
                    kotlinName = kotlinName,
                    starlarkName = starlarkName,
                    type = typeDescriptor.genericArguments.first(),
                    fullType = typeDescriptor,
                    isRequired = required
                )
            } else {
                functionArguments += NamedArgument(
                    kotlinName = kotlinName,
                    starlarkName = if (implicit) "" else starlarkName,
                    type = visitTypeReference(property.type),
                    isRequired = required
                )
            }
        }

        private fun visitReturnProperty(
            property: KSPropertyDeclaration, annotation: KSAnnotation
        ) {
            if (returnType != null) {
                val message = "A function must have return type specified not more than once."
                logger.error(message, property)
                return
            }

            val arguments = annotation.arguments.toMap()
            val defaults = annotation.defaultArguments.toMap()

            val kind = arguments.valueAsOrNull<KSType>(Returns::kind.name)?.toReturnKind()
                ?: defaults.valueAs<KSType>(Returns::kind.name).toReturnKind()

            returnType = when (kind) {
                ReturnKind.Type -> visitTypeReference(property.type)
                ReturnKind.Dynamic -> DynamicType
            }
        }

        private fun visitTypeReference(typeRef: KSTypeReference, visitGenericArguments: Boolean = true): SpecifiedType {
            val typeName = typeRef.findActualType().getOrResolve().declaration.qualifiedName!!.asString()
            if (!typeValidator.validate(typeName)) {
                val allowedTypes = typeValidator.allowedTypes.toDisplayableString()
                val message = "Invalid type $typeName. \nAllowed types are: \n$allowedTypes"
                logger.error(message, typeRef)
            }

            val genericArguments = mutableListOf<SpecifiedType>()
            if (visitGenericArguments) {
                val typeArguments = typeRef.element?.typeArguments ?: emptyList()
                for (arg in typeArguments) {
                    if (arg.variance == Variance.STAR) {
                        val message = "A function argument type must not have a '*' variance."
                        logger.error(message, arg)
                    }
                    arg.type?.let { ref ->
                        val typeArgName = ref.findActualType().getOrResolve().declaration.qualifiedName?.asString()
                        if (!typeValidator.validate(typeName, typeArgName)) {
                            val allowedTypes = typeValidator.allowedTypeArguments.toDisplayableString()
                            val message =
                                "Invalid generic type argument '$typeArgName'. \nAllowed types are: \n${allowedTypes}"
                            logger.error(message, ref)
                        }
                        genericArguments += visitTypeReference(ref, visitGenericArguments = true)
                    }
                }
            }

            val resolvedType = typeRef.getOrResolve()
            val typeDeclaration = resolvedType.declaration
            val actualType = if (typeDeclaration is KSTypeAlias) typeDeclaration.findActualType() else null
            return SpecifiedType(
                shortName = typeDeclaration.simpleName.asString(),
                qualifiedName = typeDeclaration.qualifiedName!!.asString(),
                packageName = typeDeclaration.packageName.asString(),
                isMarkedNullable = resolvedType.isMarkedNullable,
                actual = actualType?.let { visitTypeReference(it, visitGenericArguments = false) },
                genericArguments
            )
        }

        private fun KSTypeReference.getOrResolve(): KSType =
            resolvedTypesCache.getOrPut(this) { resolve() }

        private fun KSTypeAlias.findActualType(): KSTypeReference =
            when (val declaration = this.type.getOrResolve().declaration) {
                is KSTypeAlias -> declaration.findActualType()
                else -> this.type
            }

        private fun KSTypeReference.findActualType(): KSTypeReference =
            when (val declaration = this.getOrResolve().declaration) {
                is KSTypeAlias -> declaration.findActualType()
                else -> this
            }
    }
}