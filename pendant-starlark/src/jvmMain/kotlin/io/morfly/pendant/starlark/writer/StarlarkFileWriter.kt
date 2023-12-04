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

@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package io.morfly.pendant.starlark.writer

import io.morfly.pendant.starlark.element.StarlarkFile
import io.morfly.pendant.starlark.format.StarlarkFileFormatter
import java.io.File


/**
 * Converts a Starlark file syntax tree to the compilable Starlark file and writes it to the given path.
 */
open class StarlarkFileWriter private constructor(
    private val formatter: StarlarkFileFormatter = StarlarkFileFormatter,
    private val writer: FileWriter = FileWriter
) : Writer<String, StarlarkFile, Unit> {

    /**
     * Write a Starlark file to a desired location in a file system.
     */
    override fun write(dirPath: String, content: StarlarkFile) = with(content) {

        val fullPath = File("$dirPath/$name")
        writer.write(fullPath, formatter.format(content))
    }

    /**
     * Default instance of a starlark file writer.
     */
    companion object Default : StarlarkFileWriter()
}

/**
 * Write a Starlark file to a desired location in a file system.
 */
fun StarlarkFile.write(dirPath: String, writer: StarlarkFileWriter = StarlarkFileWriter.Default) {
    return writer.write(dirPath, this)
}
