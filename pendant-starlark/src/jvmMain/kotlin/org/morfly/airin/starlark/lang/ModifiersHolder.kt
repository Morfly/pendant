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

package org.morfly.airin.starlark.lang

import org.morfly.airin.starlark.format.StarlarkFileFormatter
import org.morfly.airin.starlark.lang.context.BUILD
import org.morfly.airin.starlark.lang.context.BuildContext
import org.morfly.airin.starlark.lang.context.Context
import org.morfly.airin.starlark.lang.context.bazel
import kotlin.reflect.KClass

internal typealias Id = String
internal typealias Checkpoint = String?

data class Modifier<C : Context>(
    val type: KClass<out C>,
    val body: (@UnsafeVariance C) -> Unit
)

interface ModifiersHolder {

    val modifiers: MutableMap<Id, MutableMap<Checkpoint, MutableList<Modifier<*>>>>
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

fun main() {
    val build = BUILD.bazel {
        _id = "build"

        val TEST by 1

        _checkpoint("test")

        val LIST by listOf(1)

        _checkpoint("test1")


        "myFun" {
            _id = "fun"
        }
    }
    build.onContext<BuildContext>("build") {
        "noCheckpointFun" {
            "arg" `=` 5
        }
    }

    build.onContext<BuildContext>(id = "build", checkpoint = "test") {
        "checkpointTestFun" {
            "arg" `=` "check"
        }
    }

    build.onContext<BuildContext>(id = "build", checkpoint = "test1") {
        val CHECKPOINT1 by "check"
    }

    val file = build.build()

    val text = StarlarkFileFormatter.format(file)
    println(text)
 }
