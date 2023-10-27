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

@file:Suppress("FunctionName", "PropertyName")

package io.morfly.pendant.starlark.lang.context

import io.morfly.pendant.starlark.lang.InternalPendantApi
import io.morfly.pendant.starlark.lang.ModifiersHolder
import io.morfly.pendant.starlark.lang.invokeModifiers

abstract class Context : ModifiersHolder {

    private val checkpoints = mutableSetOf<String>()

    var _id: String? = null
        set(value) {
            if (field == null) {
                require(!value.isNullOrBlank()) { "${this::class.simpleName} id can't be null or blank!" }
                field = value
            } else error("${this::class.simpleName} id can't be reassigned!")
        }

    @OptIn(InternalPendantApi::class)
    fun _checkpoint(checkpoint: String) {
        require(checkpoint.isNotBlank()) { "${this::class.simpleName} id can't be blank!" }
        if (checkpoint in checkpoints) {
            error("Duplicate checkpoint $checkpoint in ${this::class.simpleName}!")
        }
        checkpoints += checkpoint
        invokeModifiers(context = this, checkpoint = checkpoint)
    }
}