package io.morfly.pendant.sample.library

import io.morfly.pendant.starlark.format.format
import io.morfly.pendant.starlark.lang.Argument
import io.morfly.pendant.starlark.lang.FunctionKind
import io.morfly.pendant.starlark.lang.FunctionScope
import io.morfly.pendant.starlark.lang.LibraryFunction
import io.morfly.pendant.starlark.lang.ReturnKind
import io.morfly.pendant.starlark.lang.Returns
import io.morfly.pendant.starlark.lang.context.BUILD
import io.morfly.pendant.starlark.lang.type.Key
import io.morfly.pendant.starlark.lang.type.Label
import io.morfly.pendant.starlark.lang.type.ListType
import io.morfly.pendant.starlark.lang.type.Name
import io.morfly.pendant.starlark.lang.type.StringType
import io.morfly.pendant.starlark.lang.type.Value

@LibraryFunction(
    name = "custom_android_binary",
    scope = [FunctionScope.Build],
    kind = FunctionKind.Statement
)
private interface CustomAndroidBinary {

    @Argument(required = true)
    val name: Name
    val srcs: ListType<Label?>?
    val deps: ListType<Label?>?
    val custom_package: StringType?
}

@LibraryFunction(
    name = "custom_glob",
    scope = [FunctionScope.Build, FunctionScope.Workspace, FunctionScope.Starlark],
    kind = FunctionKind.Expression
)
private interface CustomGlob {

    @Argument(variadic = true, required = true)
    val include: ListType<Label?>

    @Returns
    val returns: ListType<Label>
}

@LibraryFunction(
    name = "custom_select",
    scope = [FunctionScope.Build],
    kind = FunctionKind.Expression
)
interface CustomSelect {

    @Argument(required = true, implicit = true)
    val select: Map<Key, Value>

    @Returns(kind = ReturnKind.Dynamic)
    val returns: Any
}

fun main() {
    val builder = BUILD {

        custom_android_binary(
            name = "app",
            srcs = custom_glob("src/main/kotlin/**/*.kt"),
            deps = custom_select(dict {
                ":arm_build" to list[":arm_lib"]
                ":x86_debug_build" to list[":x86_dev_lib"]
                "//conditions:default" to list[":generic_lib"]
            }),
        )
    }

    val file = builder.build()
    println(file.format())


    /* Output:

    custom_android_binary(
        name = "app",
        srcs = custom_glob(["src/main/kotlin/**/*.kt"]),
        deps = custom_select({
            ":arm_build": [":arm_lib"],
            ":x86_debug_build": [":x86_dev_lib"],
            "//conditions:default": [":generic_lib"],
        }),
    )

    */
}