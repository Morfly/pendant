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

package io.morfly.pendant.starlark.lang.context

import io.morfly.pendant.starlark.element.StarlarkFile
import io.morfly.pendant.starlark.element.Statement
import io.morfly.pendant.starlark.lang.StatementsHolder


/**
 * Base language context that others must inherit.
 */
abstract class FileContext : Context(), StatementsHolder {

    /**
     * File name
     */
    abstract val name: String

    override val statements = mutableListOf<Statement>()

    abstract fun build(): StarlarkFile
}