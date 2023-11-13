package io.morfly.pendant.sample

import io.morfly.pendant.starlark.artifact
import io.morfly.pendant.starlark.element.ListReference
import io.morfly.pendant.starlark.format.format
import io.morfly.pendant.starlark.glob
import io.morfly.pendant.starlark.kt_android_library
import io.morfly.pendant.starlark.lang.context.BUILD
import io.morfly.pendant.starlark.lang.context.bazel

fun main() {
    val builder = BUILD.bazel {
        load("@io_bazel_rules_kotlin//kotlin:android.bzl", "kt_android_library")
        load("@rules_jvm_external//:defs.bzl", "artifact")

        val ARTFIACTS by list[artifact("androidx.compose.ui:ui")]

        kt_android_library(
            name = "app",
            manifest = "src/main/AndroidManifest.xml",
            srcs = glob("src/main/kotlin/**/*.kt"),
            deps = list["my-library"] `+` ARTFIACTS
        )
    }

    val file = builder.build()
    println(file.format())

    ListReference

    /* Output:

    load("@io_bazel_rules_kotlin//kotlin:android.bzl", "kt_android_library")
    load("@rules_jvm_external//:defs.bzl", "artifact")

    ARTFIACTS = [artifact("androidx.compose.ui:ui")]

    kt_android_library(
        name = "app",
        srcs = glob(["src/main/kotlin/**/*.kt"]),
        manifest = "src/main/AndroidManifest.xml",
        deps = ["my-library"] + ARTFIACTS,
    )

    */
}