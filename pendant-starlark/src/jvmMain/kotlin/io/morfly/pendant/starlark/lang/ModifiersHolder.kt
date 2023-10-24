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

package io.morfly.pendant.starlark.lang

import io.morfly.pendant.starlark.lang.context.Context
import kotlin.reflect.KClass

typealias ContextId = String
typealias Checkpoint = String?
typealias ModifierCollection = MutableMap<ContextId, MutableMap<Checkpoint, MutableList<Modifier<*>>>>

data class Modifier<C : Context>(
    val type: KClass<out C>,
    val body: (@UnsafeVariance C) -> Unit
)

interface ModifiersHolder {

    val modifiers: ModifierCollection
}

@InternalPendantApi
fun <C : Context> ModifiersHolder.invokeModifiers(context: C, checkpoint: String? = null) {
    context._id?.let { id ->
        modifiers[id]
            ?.get(checkpoint)
            ?.asSequence()
            ?.filter { it.type == context::class }
            ?.forEach { it.body(context) }
    }
}

inline fun <reified C : Context> ModifiersHolder.onContext(
    id: String,
    checkpoint: String? = null,
    noinline modifier: C.() -> Unit
) {
    modifiers
        .getOrPut(id, ::linkedMapOf)
        .getOrPut(checkpoint, ::mutableListOf) += Modifier(C::class, modifier)
}

fun ModifierCollection.append(modifiers: ModifierCollection) {
    for ((id, checkpoints) in modifiers) {
        for ((checkpoint, modifierList) in checkpoints) {
            append(id, checkpoint, modifierList)
        }
    }
}

fun ModifierCollection.append(id: String, checkpoint: String?, modifiers: List<Modifier<*>>) {
    getOrPut(id, ::linkedMapOf).getOrPut(checkpoint, ::mutableListOf) += modifiers
}

fun ModifierCollection.append(id: String, checkpoint: String?, modifier: Modifier<*>) {
    getOrPut(id, ::linkedMapOf).getOrPut(checkpoint, ::mutableListOf) += modifier
}
