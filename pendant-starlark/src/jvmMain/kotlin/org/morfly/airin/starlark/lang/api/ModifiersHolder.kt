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

package org.morfly.airin.starlark.lang.api

import kotlin.reflect.KClass

data class Modifier<C : Context>(
    val type: KClass<out C>,
    val body: (@UnsafeVariance C) -> Unit
)

interface ModifiersHolder {

    val modifiers: MutableMap<String, MutableList<Modifier<*>>>
}

@InternalPendantApi
inline fun <reified C : Context> ModifiersHolder.invokeModifiers(context: C) {
    if (context._id != null) {
        modifiers[context._id]?.asSequence()
            ?.filter { (type, _) -> type == C::class }
            ?.forEach { (_, modifier) -> modifier.invoke(context) }
    }
}

inline fun <reified C : Context> ModifiersHolder.onContext(id: String, noinline modifier: C.() -> Unit) {
    modifiers.getOrPut(id, ::mutableListOf) += Modifier(C::class, modifier)
}
