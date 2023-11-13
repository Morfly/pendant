package io.morfly.pendant.sample

import io.morfly.pendant.starlark.android_binary
import io.morfly.pendant.starlark.format.format
import io.morfly.pendant.starlark.lang.context.BUILD
import io.morfly.pendant.starlark.lang.context.FunctionCallContext
import io.morfly.pendant.starlark.lang.context.bazel
import io.morfly.pendant.starlark.lang.feature.invoke
import io.morfly.pendant.starlark.lang.onContext
import io.morfly.pendant.starlark.lang.type.ListType
import io.morfly.pendant.starlark.lang.type.StringType

fun main() {
    val builder = BUILD.bazel {
        // Dynamic function argument
        android_binary {
            name = "my-library"
            deps = list[":my-library"]
            "manifest_values" `=` {
                "minSdkVersion" to "24"
            }
        }

        // Dynamic function call statement
        "android_library" {
            _id = "android_library_target"

            "name" `=` "my-library"
            // Dynamic function call with return value
            "srcs" `=` "glob"<ListType<StringType>>("src/main/kotlin/**/*.kt")
        }

        // Dynamic function call statement with no args
        "custom_function_with_no_args"()
    }

    builder.onContext<FunctionCallContext>(id = "android_library_target") {
        "deps" `=` "//another-library"
    }

    val file = builder.build()
    println(file.format())


    /* Output:

    android_binary(
        name = "my-library",
        deps = [":my-library"],
        manifest_values = {"minSdkVersion": "24"},
    )

    android_library(
        name = "my-library",
        srcs = glob("src/main/kotlin/**/*.kt"),
        deps = "//another-library",
    )

    custom_function_with_no_args()

    */
}