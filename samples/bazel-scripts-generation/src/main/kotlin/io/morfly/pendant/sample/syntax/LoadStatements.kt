package io.morfly.pendant.sample.syntax

import io.morfly.pendant.starlark.element.ListReference
import io.morfly.pendant.starlark.format.format
import io.morfly.pendant.starlark.lang.context.BUILD
import io.morfly.pendant.starlark.lang.context.bazel
import io.morfly.pendant.starlark.lang.type.ListType
import io.morfly.pendant.starlark.lang.type.StringType
import io.morfly.pendant.starlark.maven_install

fun main() {
    val builder = BUILD.bazel {

        // Simple load statement
        load("@io_bazel_rules_kotlin//kotlin:android.bzl", "kt_android_library")

        // Load values
        val (DAGGER_ARTIFACTS, DAGGER_REPOSITORIES) = load(
            "@dagger//:workspace_defs.bzl",
            "DAGGER_ARTIFACTS", "DAGGER_REPOSITORIES"
        ).of<ListType<StringType>, ListType<StringType>>()

        maven_install(
            artifacts = DAGGER_ARTIFACTS,
            repositories = DAGGER_REPOSITORIES
        )

        // Dynamically referenced loaded values
        load("@dagger//:workspace_defs.bzl", "DAGGER_ARTIFACTS", "DAGGER_REPOSITORIES")

        maven_install(
            artifacts = ListReference<StringType>("DAGGER_ARTIFACTS"),
            repositories = ListReference<StringType>("DAGGER_REPOSITORIES")
        )
    }

    val file = builder.build()
    println(file.format())


    /* Output:

    load("@io_bazel_rules_kotlin//kotlin:android.bzl", "kt_android_library")
    load(
        "@dagger//:workspace_defs.bzl",
        "DAGGER_ARTIFACTS",
        "DAGGER_REPOSITORIES",
    )

    maven_install(
        artifacts = DAGGER_ARTIFACTS,
        repositories = DAGGER_REPOSITORIES,
    )

    load(
        "@dagger//:workspace_defs.bzl",
        "DAGGER_ARTIFACTS",
        "DAGGER_REPOSITORIES",
    )

    maven_install(
        artifacts = DAGGER_ARTIFACTS,
        repositories = DAGGER_REPOSITORIES,
    )

    */
}