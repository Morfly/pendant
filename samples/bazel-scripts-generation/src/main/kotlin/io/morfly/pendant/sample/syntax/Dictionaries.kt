package io.morfly.pendant.sample.syntax

import io.morfly.pendant.starlark.android_binary
import io.morfly.pendant.starlark.format.format
import io.morfly.pendant.starlark.lang.context.BUILD
import io.morfly.pendant.starlark.lang.context.bazel

fun main() {
    val builder = BUILD.bazel {
        val MANIFEST_VALUES by dict { } `+` { "minSdkVersion" to "23" }

        android_binary {
            name = "app"
            "manifest_values" `=` { "minSdkVersion" to "23" }
        }
    }

    val file = builder.build()
    println(file.format())


    /* Output:

    MANIFEST_VALUES = {} + {"minSdkVersion": "23"}

    android_binary(
        name = "app",
        manifest_values = {"minSdkVersion": "23"},
    )

    */
}