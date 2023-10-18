package io.morfly.pendant.starlark.library

import io.morfly.pendant.starlark.lang.types.Name
import io.morfly.pendant.starlark.lang.Argument
import io.morfly.pendant.starlark.lang.FunctionKind.Statement
import io.morfly.pendant.starlark.lang.FunctionScope.Build
import io.morfly.pendant.starlark.lang.LibraryFunction


@LibraryFunction(
    name = "default_java_toolchain",
    scope = [Build],
    kind = Statement
)
private interface DefaultJavaToolchain {

    @Argument(required = true)
    val name: Name
}