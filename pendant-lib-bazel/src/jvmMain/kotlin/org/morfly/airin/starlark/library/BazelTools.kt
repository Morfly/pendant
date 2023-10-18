package org.morfly.airin.starlark.library

import org.morfly.airin.starlark.lang.types.Name
import org.morfly.airin.starlark.lang.Argument
import org.morfly.airin.starlark.lang.FunctionKind.Statement
import org.morfly.airin.starlark.lang.FunctionScope.Build
import org.morfly.airin.starlark.lang.LibraryFunction


@LibraryFunction(
    name = "default_java_toolchain",
    scope = [Build],
    kind = Statement
)
private interface DefaultJavaToolchain {

    @Argument(required = true)
    val name: Name
}