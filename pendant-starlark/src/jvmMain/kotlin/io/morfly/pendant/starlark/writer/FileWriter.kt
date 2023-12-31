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

import java.io.File


/**
 * Writes string content to the specified file path.
 */
open class FileWriter : Writer<File, String, Unit> {

    override fun write(path: File, content: String) = with(path) {
        try {
            parentFile.mkdirs()
            createNewFile()
            bufferedWriter().use { out -> out.write(content) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Default instance of a file writer.
     */
    companion object Default : FileWriter()
}