package io.morfly.pendant.sample

import io.morfly.pendant.starlark.AndroidLibraryContext
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

        android_library {
            _id = "android_library_target"

            name = "my-library"
            deps = list["//another-library"]
        }
    }

    builder.onContext<BuildContext>(id = "build_file") {
        android_binary(
            name = "app",
            deps = list[":my-library"]
        )
    }

    builder.onContext<AndroidLibraryContext>(id = "android_library_target") {
        visibility = list["//visibility:public"]
        deps = list["@maven//:androidx_compose_runtime_runtime"]
    }

    val file = builder.build()
    println(file.format())


    /* Output:

    android_library(
        name = "my-library",
        deps = ["//another-library"] + ["@maven//:androidx_compose_runtime_runtime"],
        visibility = ["//visibility:public"],
    )

    android_binary(
        name = "app",
        deps = [":my-library"],
    )

    */
}