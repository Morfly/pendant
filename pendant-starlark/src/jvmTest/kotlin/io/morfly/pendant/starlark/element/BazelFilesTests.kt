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

package io.morfly.pendant.starlark.element

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe


class BazelFilesTests : ShouldSpec({

    context("WORKSPACE file") {
        should("'hasExtension = false' create name without extension") {
            val workspace = WorkspaceFile(
                hasExtension = false,
                statements = emptyList()
            )

            workspace.name shouldBe "WORKSPACE"
        }

        should("'hasExtension = true' create name with extension") {
            val workspace = WorkspaceFile(
                hasExtension = true,
                statements = emptyList()
            )

            workspace.name shouldBe "WORKSPACE.bazel"
        }
    }

    context("BUILD file") {
        should("'hasExtension = false' create name without extension") {
            val workspace = BuildFile(
                hasExtension = false,
                statements = emptyList()
            )

            workspace.name shouldBe "BUILD"
        }

        should("'hasExtension = true' create name with extension") {
            val workspace = BuildFile(
                hasExtension = true,
                statements = emptyList()
            )

            workspace.name shouldBe "BUILD.bazel"
        }
    }

    // TODO StarlarkFile
})
