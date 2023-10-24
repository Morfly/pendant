package io.morfly.pendant.starlark

import io.morfly.pendant.starlark.lang.FunctionKind
import io.morfly.pendant.starlark.lang.FunctionScope
import io.morfly.pendant.starlark.lang.LibraryFunction

@LibraryFunction(
    name = "dagger_rules",
    scope = [FunctionScope.Build],
    kind = FunctionKind.Statement
)
private interface DaggerRules

@LibraryFunction(
    name = "dagger_android_rules",
    scope = [FunctionScope.Build],
    kind = FunctionKind.Statement
)
private interface DaggerAndroidRules

@LibraryFunction(
    name = "hilt_android_rules",
    scope = [FunctionScope.Build],
    kind = FunctionKind.Statement
)
private interface HiltAndroidRules