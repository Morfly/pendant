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

package io.morfly.pendant.starlark.format

import io.morfly.pendant.starlark.elements.StarlarkFile


/**
 * Formats a Starlark syntax tree into a compilable Starlark code.
 */
interface StarlarkFileFormatter {

    /**
     * Formats a Starlark syntax tree and returns a string representation of a compilable code.
     */
    fun format(starlarkFile: StarlarkFile): String

    /**
     * Formats a Starlark syntax tree and posts a string representation of a compilable code to the accumulator.
     */
    fun format(starlarkFile: StarlarkFile, accumulator: Appendable)

    /**
     * Default instance of the formatter.
     */
    companion object Default : StarlarkFileFormatter by StarlarkCodeFormatter()
}