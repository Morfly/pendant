package io.morfly.pendant.sample.syntax

import io.morfly.pendant.starlark.android_binary
import io.morfly.pendant.starlark.format.format
import io.morfly.pendant.starlark.glob
import io.morfly.pendant.starlark.lang.context.BUILD
import io.morfly.pendant.starlark.lang.context.bazel
import io.morfly.pendant.starlark.lang.feature.invoke
import io.morfly.pendant.starlark.lang.type.ListType
import io.morfly.pendant.starlark.lang.type.StringType

fun main() {
    val builder = BUILD.bazel {

        // Function call statement with round brackets ()
        android_binary(
            name = "app",
            // Function call expression with round brackets ()
            srcs = glob("src/main/kotlin/**/*.kt"),
            manifest_values = dict {
                "minSdkVersion" to "24"
            }
        )

        // Function call statement with curly brackets {}
        android_binary {
            name = "app"
            srcs = glob("src/main/kotlin/**/*.kt")
            // Dynamic argument
            "manifest_values" `=` {
                "minSdkVersion" to "24"
            }
        }

        // Dynamic function call statement
        "android_binary" {
            "name" `=` "app"
            // Function call expression with curly brackets {}
            "srcs" `=` glob { include = list["src/main/kotlin/**/*.kt"] }
        }

        android_binary(
            name = "app",
            // Dynamic function call expression
            srcs = "glob"<ListType<StringType>>("src/main/kotlin/**/*.kt"),
        )

        // Dynamic function call statement with no args
        "android_binary"()
    }

    val file = builder.build()
    println(file.format())


    /* Output:

    android_binary(
        name = "app",
        manifest_values = {"minSdkVersion": "24"},
        srcs = glob(["src/main/kotlin/**/*.kt"]),
    )

    android_binary(
        name = "app",
        srcs = glob(["src/main/kotlin/**/*.kt"]),
        manifest_values = {"minSdkVersion": "24"},
    )

    android_binary(
        name = "app",
        srcs = glob(include = ["src/main/kotlin/**/*.kt"]),
    )

    android_binary(
        name = "app",
        srcs = glob("src/main/kotlin/**/*.kt"),
    )

    android_binary()

    */
}