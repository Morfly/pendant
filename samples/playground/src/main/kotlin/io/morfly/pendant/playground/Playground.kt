package io.morfly.pendant.playground

import io.morfly.pendant.starlark.format.format
import io.morfly.pendant.starlark.lang.context.BUILD
import io.morfly.pendant.starlark.lang.context.bazel


fun main() {
    val file = BUILD.bazel {

    }

    println(file.build().format())
}