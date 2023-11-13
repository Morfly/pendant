package io.morfly.pendant.sample

import io.morfly.pendant.starlark.android_binary
import io.morfly.pendant.starlark.android_library
import io.morfly.pendant.starlark.format.format
import io.morfly.pendant.starlark.lang.context.BUILD
import io.morfly.pendant.starlark.lang.context.BuildContext
import io.morfly.pendant.starlark.lang.context.bazel
import io.morfly.pendant.starlark.lang.onContext

fun main() {
    val builder = BUILD.bazel {
        _id = "build_file"

        _checkpoint("build_file_before_target")

        android_library(
            name = "my-library",
            deps = list["//another-library"]
        )
    }

    builder.onContext<BuildContext>(id = "build_file", checkpoint = "build_file_before_target") {
        android_binary(
            name = "app",
            deps = list[":my-library"]
        )
    }

    val file = builder.build()
    println(file.format())


    /* Output:

    android_binary(
        name = "app",
        deps = [":my-library"],
    )

    android_library(
        name = "my-library",
        deps = ["//another-library"],
    )

    */
}