package io.morfly.pendant.sample.library

import io.morfly.pendant.starlark.format.format
import io.morfly.pendant.starlark.lang.Argument
import io.morfly.pendant.starlark.lang.FunctionKind
import io.morfly.pendant.starlark.lang.FunctionScope
import io.morfly.pendant.starlark.lang.LibraryFunction
import io.morfly.pendant.starlark.lang.Returns
import io.morfly.pendant.starlark.lang.context.BUILD
import io.morfly.pendant.starlark.lang.type.Label
import io.morfly.pendant.starlark.lang.type.ListType
import io.morfly.pendant.starlark.lang.type.Name
import io.morfly.pendant.starlark.lang.type.StringType

@LibraryFunction(
    name = "custom_android_binary",
    scope = [FunctionScope.Build],
    kind = FunctionKind.Statement
)
private interface CustomAndroidBinary {

    @Argument(required = true)
    val name: Name
    val srcs: ListType<Label?>?
    val custom_package: StringType?
}

@LibraryFunction(
    name = "custom_glob",
    scope = [FunctionScope.Build, FunctionScope.Workspace, FunctionScope.Starlark],
    kind = FunctionKind.Expression
)
private interface CustomGlob {

    @Argument(vararg = true, required = true)
    val include: ListType<Label?>

    @Returns
    val returns: ListType<Label>
}

fun main() {
    val builder = BUILD {

        custom_android_binary(
            name = "app",
            srcs = custom_glob("src/main/kotlin/**/*.kt"),
            custom_package = "io.morfly.pendant",
        )
    }

    val file = builder.build()
    println(file.format())


    /* Output:

    custom_android_binary(
        name = "app",
        srcs = custom_glob(["src/main/kotlin/**/*.kt"]),
        custom_package = "io.morfly.pendant",
    )

    */
}