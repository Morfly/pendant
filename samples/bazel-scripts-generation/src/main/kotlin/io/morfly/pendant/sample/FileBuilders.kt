package io.morfly.pendant.sample

import io.morfly.pendant.starlark.lang.context.BUILD
import io.morfly.pendant.starlark.lang.context.WORKSPACE
import io.morfly.pendant.starlark.lang.context.bazel
import io.morfly.pendant.starlark.lang.context.bzl

fun main() {
    val builder1 = BUILD { }
    println(builder1.fileName)

    val builder2 = BUILD.bazel { }
    println(builder2.fileName)

    val builder3 = WORKSPACE { }
    println(builder3.fileName)

    val builder4 = WORKSPACE.bazel { }
    println(builder4.fileName)

    val builder5 = "my_file".bzl { }
    println(builder5.fileName)


    /* Output:

    BUILD
    BUILD.bazel
    WORKSPACE
    WORKSPACE.bazel
    my_file.bzl

    */
}