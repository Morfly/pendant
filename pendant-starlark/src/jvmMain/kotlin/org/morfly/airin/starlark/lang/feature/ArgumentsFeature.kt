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

package org.morfly.airin.starlark.lang.feature

import org.morfly.airin.starlark.elements.*
import org.morfly.airin.starlark.lang.types.BooleanType
import org.morfly.airin.starlark.lang.types.NumberType
import org.morfly.airin.starlark.lang.types.StringType
import org.morfly.airin.starlark.lang.types.TupleType
import org.morfly.airin.starlark.lang.api.ArgumentsHolder
import org.morfly.airin.starlark.lang.api.LanguageFeature
import org.morfly.airin.starlark.lang.api.append
import kotlin.reflect.KProperty


internal interface ArgumentsFeature : LanguageFeature,
    ArgumentsHolder {

    operator fun <V> Map<String, Argument>.getValue(thisRef: Any?, property: KProperty<*>): V {
        error("Unable to return value from a function argument.")
    }

    operator fun <V : StringType?> Map<String, Argument>.setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        fargs[property.name] = Argument(id = property.name, value = Expression(value, ::StringLiteral))
    }

    operator fun <V : NumberType?> Map<String, Argument>.setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        fargs[property.name] = Argument(id = property.name, value = Expression(value, ::NumberLiteral))
    }

    operator fun <V : BooleanType?> Map<String, Argument>.setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        fargs[property.name] = Argument(id = property.name, value = Expression(value, ::BooleanLiteral))
    }

    operator fun <T, V : List<T>?> Map<String, Argument>.setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        append(
            name = property.name,
            value = Expression(value, ::ListExpression),
            concatenation = { left, op, right -> ListBinaryOperation<T>(left, op, right) }
        )
    }

    operator fun <V : TupleType?> Map<String, Argument>.setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        fargs[property.name] = Argument(id = property.name, value = Expression(value, ::TupleExpression))
    }

    operator fun <K, V, V1 : Map<K, V>?> Map<String, Argument>.setValue(
        thisRef: Any?, property: KProperty<*>, value: V1
    ) {
        append(
            name = property.name,
            value = Expression(value, ::DictionaryExpression),
            concatenation = { left, op, right -> DictionaryBinaryOperation<K, V>(left, op, right) }
        )
    }
}