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

package io.morfly.pendant.starlark.lang.type


/**
 * Representation of a Starlark string type.
 */
typealias StringType = CharSequence

/**
 * Representation of a Starlark number type covering integer and float.
 */
typealias NumberType = Number

typealias BooleanBaseType<T> = Comparable<T>

/**
 * Representation of a Starlark boolean type
 */
typealias BooleanType = BooleanBaseType<Boolean>

/**
 * Representation of a Starlark tuple type.
 */
typealias TupleType = Tuple

/**
 * Representation of a Starlark list type.
 */
typealias ListType<T> = List<T>

/**
 * Representation of a Starlark dictionary type.
 */
typealias DictionaryType<K, V> = Map<K, V>

/**
 * Representation of a Starlark void type mostly used for functions without returned values.
 */
typealias VoidType = Unit

typealias BaseKey = Any

/**
 * Type alias for a dictionary key type.
 */
typealias Key = BaseKey?

typealias BaseValue = Any
/**
 * Type alias for a dictionary value type.
 */
typealias Value = BaseValue?

/**
 * String type alias for the value representing name in Bazel.
 */
typealias Name = StringType

/**
 * String type alias for the value representing Bazel label.
 */
typealias Label = StringType