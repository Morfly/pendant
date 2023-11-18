package io.morfly.pendant.sample

import io.morfly.pendant.starlark.format.format
import io.morfly.pendant.starlark.lang.context.BUILD
import io.morfly.pendant.starlark.lang.context.bazel
import io.morfly.pendant.starlark.lang.feature.invoke
import io.morfly.pendant.starlark.lang.type.ListType
import io.morfly.pendant.starlark.lang.type.StringType

fun main() {
    val builder = BUILD.bazel {

        // List comprehension
        val CLASSES by list["MainActivity", "MainViewModel"]

        val SRCS by "name" `in` CLASSES take { name -> name `+` ".kt" }

        // Nested list comprehensions
        val MATRIX by list[
            list[1, 2],
            list[3, 4]
        ]

        val NUMBERS by "list" `in` MATRIX `for` { list ->
            "number" `in` list take { number -> number }
        }

        // Nested list comprehensions
        val RANGE by "range"<ListType<StringType>>(5)

        val VALUES by "i" `in` RANGE take { "j" `in` RANGE take { j -> j } }
    }

    val file = builder.build()
    println(file.format())

    /* Output:

    CLASSES = [
        "MainActivity",
        "MainViewModel",
    ]

    SRCS = [
        name + ".kt"
        for name in CLASSES
    ]

    MATRIX = [
        [1, 2],
        [3, 4],
    ]

    NUMBERS = [number for list in MATRIX for number in list]

    RANGE = range(5)

    VALUES = [
        [j for j in RANGE]
        for i in RANGE
    ]

    */
}