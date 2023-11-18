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

import io.morfly.pendant.starlark.lang.type.BaseKey
import io.morfly.pendant.starlark.lang.type.BaseValue
import io.morfly.pendant.starlark.lang.type.BooleanBaseType
import io.morfly.pendant.starlark.lang.type.DictionaryType
import io.morfly.pendant.starlark.lang.type.ListType
import io.morfly.pendant.starlark.lang.type.NumberType
import io.morfly.pendant.starlark.lang.type.StringType
import io.morfly.pendant.starlark.lang.type.TupleType
import io.morfly.pendant.starlark.lang.type.VoidType


private typealias QualifiedName = String


interface TypeValidator {

    val allowedTypes: Set<QualifiedName?>

    val allowedTypeArguments: Map<QualifiedName?, Set<QualifiedName?>>

    fun validate(type: QualifiedName?, typeArgument: QualifiedName? = null): Boolean
}

class TypeValidatorImpl : TypeValidator {

    override val allowedTypes = setOf(
        StringType::class.qualifiedName,
        NumberType::class.qualifiedName,
        ListType::class.qualifiedName,
        DictionaryType::class.qualifiedName,
        TupleType::class.qualifiedName,
        BooleanBaseType::class.qualifiedName,
        VoidType::class.qualifiedName,
        Any::class.qualifiedName,
    )

    override val allowedTypeArguments = mapOf(
        ListType::class.qualifiedName to allowedTypes,
        DictionaryType::class.qualifiedName to setOf(
            BaseKey::class.qualifiedName,
            BaseValue::class.qualifiedName
        )
    )

    override fun validate(type: QualifiedName?, typeArgument: QualifiedName?): Boolean =
        if (typeArgument != null)
            typeArgument in (allowedTypeArguments[type] ?: emptySet())
        else type in allowedTypes
}