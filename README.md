# Pendant

Pendant — is a declarative Starlark code generator written in Kotlin.
Use Kotlin DSL that replicated Starlark syntax, for generating Bazel scripts in a type-safe fashion.

## Installation

### Gradle

```kotlin
dependencies {
    // Starlark code generator.
    implementation("io.morfly.pendant:pendant-starlark:x.y.z")
    // Library of Bazel functions.
    implementation("io.morfly.pendant:pendant-library-bazel:x.y.z>")

    // Optional. Generator for custom Starlark functions.
    ksp("io.morfly.pendant:pendant-library-compiler:x.y.z")
}
```

## How to Use
Below you can find an example of a simple `BUILD.bazel` file generation with Pendant. The code below is a Kotlin DSL which replicates Starlark syntax as close as possible.
```kotlin
// Kotlin
val builder = BUILD.bazel {
    load("@io_bazel_rules_kotlin//kotlin:android.bzl", "kt_android_library")

    kt_android_library(
        name = "my-library",
        srcs = glob("src/main/kotlin/**/*.kt"),
        custom_package = "io.morfly.mylibrary",
        manifest = "src/main/AndroidManifest.xml",
        resource_files = glob(["src/main/res/**"]),
    )
}
val file = builder.build()
file.write("path/in/file/system")
```

As a result, a `BUILD` file with the following content is generated. Pendant takes care of the code formatting, so you don't have to do it yourself.

```python
# Generated Starlark
load("@io_bazel_rules_kotlin//kotlin:android.bzl", "kt_android_library")

kt_android_library(
    name = "my-library",
    srcs = glob("src/main/kotlin/**/*.kt"),
    custom_package = "io.morfly.mylibrary",
    manifest = "src/main/AndroidManifest.xml",
    resource_files = glob(["src/main/res/**"]),
)
```

## Code generation API
Pendant provides an API for generating different types of Starlark files. Once entered the file context, you can use the Kotlin DSL to generate corresponding Starlark statements.
Depending on the file type, a different set of Starlark syntax features and functions is available.

### Building Starlark files

To generate `BUILD.bazel` files, the following expression must be used.

```kotlin
val builder = BUILD.bazel { ... }
```

Or a shorter form to produce `BUILD` files.

```kotlin
val builder = BUILD { ... }
```

Similarly, Pendant provides an API for generating `WORKSPACE.bazel` files.

```kotlin
val builder = WORKSPACE.bazel { ... }
```

Or a shorter form to produce `WORKSPACE` files.

```kotlin
val builder = WORKSPACE { ... }
```

Additionally, it is possible to generate files with `.bzl` extension.

```kotlin
val builder = "starlark_file".bzl { ... }
```

### Write files to file system
Each function demonstrated above returns a `FileContext` instance that serves as a file builder. In order to write it to file it must be first built using `build()` function.

```kotlin
val file = builder.build()
```
Finally, it could be written to file using the API below.
```kotlin
StarlarkFileWriter.write("path/in/file/system", file)
// or
file.write("path/in/file/system")
```

### Get file contents as string
Alternatively, the formatted contents of a generated file could be returned as a string.

```kotlin
val starlarkCode: String = StarlarkFileFormatter.format(file)
// or
val starlarkCode: String = file.format()
```

## Starlark syntax elements
Now, the most important part. In this section we will take a closer look at Kotlin DSL components that represent corresponding Starlark syntax elements, for code generation. 

### Variable assignments
Variable declaration and assignment is an essential feature of Starlark language. Use `by` operator to do it.
> You might ask, why aren't we using `=` operator in this case? 
> The latter operator will perform a variable assignment operation while compiling the Kotlin code. However, what we need is generating the statement in Starlark rather than executing it in Kotlin.

```kotlin
// Kotlin
val NAME by "app"
```
```python
# Generated Starlark
NAME = "app"
```
What's important is that this operation is type safe, meaning `NAME` is a variable of string type on the Kotlin DSL level and could be used further accordingly.

### List expressions
One of the syntax elements of Starlark are list expressions. There is no equivalent for such an expression in Kotlin. 
However, the `list[]` function could be used to achieve the same result.
```kotlin
// Kotlin
val SRCS by list["io/morfly/Main.kt"]
```
```python
# Generated Starlark
SRCS = ["io/morfly/Main.kt"]
```

Regular `listOf` function from Kotlin standard library also works perfectly well. However, having `list[]` function is convenient when you're copy-pasting actual Starlark code in your Kotlin file. This way you would need to do less editing to make it compilable in your code generator program. 

### Dictionary expressions
Similarly to list expressions, Kotlin does not have dictionary expressions in its syntax. However, the `dict` function could be used to achieve the same result.
```kotlin
// Kotlin
val MANIFEST_VALUES by dict { "minSdkVersion" to "23" }
```

```python
# Generated Starlark
MANIFEST_VALUES = { "minSdkVersion" : "23" }
```
You might notice that `to` operator is used to map dictionary values. However, this is not a usual `to` function from Kotlin standard library but a more powerful version.

For example, you could use composite keys and values with concatenation operation.

```kotlin
// Kotlin
val MANIFEST_VALUES by dict { "minSdk" `+` "Version" to "2" `+` "3"}
```
```python
# Generated Starlark
MANIFEST_VALUES = { "minSdk" + "Version" : "2" + "3" }
```

#### Short form
In addition, in some cases it's possible to use a shorted form of Kotlin DSL for dictionary expressions. If followed by `` `=` `` or `` `+` `` operators (with backticks) `dict` keyword could be omitted.

```kotlin
// Kotlin
val MANIFEST_VALUES by dict { } `+` { "minSdkVersion" to "23" }

android_binary {
    "manifest_values" `=` { "minSdkVersion" to "23" }
}
```

```kotlin
// Generated Starlark
MANIFEST_VALUES = {} + {"minSdkVersion": "23"}

android_binary(
    name = "app",
    manifest_values = {"minSdkVersion": "23"},
)
```

### Concatenations
Using `` `+` `` function with backticks you could generate concatenation expressions. 

> You might ask, why aren't we using a regular `+` operator in this case?
> The latter operator will perform a variable assignment operation while compiling the Kotlin code. However, what we need is generating the statement in Starlark rather than executing it in Kotlin.


```kotlin
// Kotlin
val ARTIFACTS by list["@maven//:androidx_compose_runtime_runtime"]

val DEPS by ARTIFACTS `+` list["//my-library"]
```
```python
# Generated Starlark
ARTIFACTS = ["@maven//:androidx_compose_runtime_runtime"]

DEPS = ARTIFACTS + ["//my-library"]
```
As you can see, you could use concatenations with varouus types of expressions, like list expressions, variable references, list comprehensions, etc. 
This operation is type-safe in all these cases. 

### Function calls

There are different ways to generate a function call with Pendant. 

The easiest way is to use Starlark or Bazel functions available directly in Pendant.

```kotlin
// Kotlin
android_binary(
    name = "app",
    srcs = glob("src/main/kotlin/**/*.kt"),
    deps = ARTIFACTS `+` ["//my-library"],
    manifest = "src/main/AndroidManifest.xml"
)
```
Each function in the library is available in 2 variations: with round brackets `()`, and with curly brackets `{}`.
The latter is especially useful if you need more customization.

For example, you could use custom parameters which are not part of Pendant library. 

```kotlin
// Kotlin
android_binary {
    name = "app"
    "manifest_values" `=` { "minSdkVersion" to "23" }
}
```
Moreover, you could declare calls of functions which are completely absent in Pendant library like shown below.
```kotlin
// Kotlin
"android_binary" {
    "name" `=` "app"
    "manifest_values" `=` { "minSdkVersion" to "23" }
}
```
One more bonus of generating function calls using curly brackets `{}` is that it preserves the order of passed arguments the way you specify them.

Alternatively, dynamic function calls could be used for functions with no arguments.
```kotlin
"glob"()
```

#### Function call expressions
So far we've seen how to generate function calls as standalone statements, meaning they don't return any value.

Additionally, Pendant allows generating functions as expressions that return values.

```kotlin
// Kotlin
val SRCS by glob("src/main/kotlin/**/*.kt")
```

```python
# Generated Starlark
SRCS = glob(["src/main/kotlin/**/*.kt"])
```
You could use dynamic API for these types of functions as well. Make sure to explicitly specify the return type.
```kotlin
// Kotlin
import io.morfly.pendant.starlark.lang.feature.invoke

val SRCS by "glob"<ListType<StringType>>("src/main/kotlin/**/*.kt")
```
```python
# Generated Starlark
SRCS = glob(["src/main/kotlin/**/*.kt"])
```
> You might need to manually import the `io.morfly.pendant.starlark.lang.feature.invoke`function from Pendant for dynamic function calls with return values. 

If you need to generate a function call with named arguments, use curly brackets `{}`.

```kotlin
// Kotlin
import io.morfly.pendant.starlark.lang.feature.invoke

val SRCS by "glob"<ListType<StringType>>{
    "include" `=` list["src/main/kotlin/**/*.kt"]
}
```
```python
# Generated Starlark
SRCS = glob(include = ["src/main/kotlin/**/*.kt"])
```

> Dynamic function calls that return values rely on context receivers, a feature introduced in recent versions of Kotlin. If you need to use it as part of Gradle plugin or scripts, it might not be supported, as Gradle uses older Kotlin versions.  

#### Type-safe API for custom functions
Pendant also allows you to generate a Kotlin DSL for custom Starlark functions. Refer to TODO section to learn more.

### List comprehensions
Another powerful Starlark feature for building lists is list comprehensions. Use combination of `` `in` `` and `take` operators to generate them with Pendant.
```kotlin
// Kotlin
val CLASSES by list["MainActivity", "MainViewModel"]

val SRCS by "name" `in` CLASSES take { name -> name `+` ".kt" }
```

```python
# Generated Starlark
CLASSES = ["MainActivity", "MainViewModel"]

SRCS = [name + ".kt" for name in classes]
```

#### Nested comprehensions
Additionally, you could use use `` `for `` operator for nested comprehensions.
```kotlin
// Kotlin
val MATRIX by list[
    list[1, 2],
    list[3, 4]
]

val NUMBERS by "list" `in` MATRIX `for` { list ->
    "number" `in` list take { number -> number }
}
```

```python
# Generated Starlark
MATRIX = [
    [1, 2],
    [3, 4]
]

NUMBERS = [number for list in MATRIX for number in list]
```
Alternatively, Pendant supports another variation of nested comprehensions as shown below.
```kotlin
import io.morfly.pendant.starlark.lang.feature.invoke

val RANGE by "range"<ListType<StringType>>(5)
val SRCS by "i" `in` RANGE take { "j" `in` RANGE take { j -> j } }
```
```python
# Generated Starlark
RANGE = range(5)

SRCS = [
    [j for j in RANGE]
    for i in RANGE
]
```



### Slices

```kotlin
// Kotlin
"abc.kt"[0..-3]
```

```python
# Generated Starlark
"abc.kt"[0:-3]
```

### Load statements

#### Load functions

```kotlin
// Kotlin
load("@rules_java//java:defs.bzl", "java_binary")
```

```python
# Generated Starlark
load("@rules_java//java:defs.bzl", "java_binary")
```

#### Load values
If you need to reference the values impored with `load` you could use `of` function and specify the types of the corresponding values.
```kotlin
// Kotlin
val (DAGGER_ARTIFACTS, DAGGER_REPOSITORIES) = load(
    "@dagger//:workspace_defs.bzl",
    "DAGGER_ARTIFACTS", "DAGGER_REPOSITORIES"
).of<ListType<StringType>, ListType<StringType>>()

maven_install(
    artifacts = DAGGER_ARTIFACTS,
    repositories = DAGGER_REPOSITORIES
)
```
```python
# Generated Starlark
load("@dagger//:workspace_defs.bzl", "DAGGER_ARTIFACTS", "DAGGER_REPOSITORIES")

maven_install(
    artifacts = DAGGER_ARTIFACTS,
    repositories = DAGGER_REPOSITORIES
)
```
Alternatively, you could manually instantiate the reference with the needed type and name.
```kotlin
// Kotlin
load("@dagger//:workspace_defs.bzl", "DAGGER_ARTIFACTS", "DAGGER_REPOSITORIES")

maven_install(
    artifacts = ListReference<StringType>("DAGGER_ARTIFACTS"),
    repositories = ListReference<StringType>("DAGGER_REPOSITORIES")
)
```
You can use `StringReference`, `NumberReference`, `BooleanReference`, `ListReference`, `DicrionaryReference`, `TupleReference` or `AnyReference` to refer to imported values.

### Raw code injection
If you need more freedom with code generation or formatting you could always inject raw strings as part of the generated code.

To do this use `+` unary plus operator or `raw()` extension function on a string that represents a code.
```kotlin
// Kotlin
+"""
SRCS = glob(["src/main/kotlin/**/*.kt"])
""".trimIndent()
```

```kotlin
// Kotlin
"""
SRCS = glob(["src/main/kotlin/**/*.kt"])
""".trimIndent().raw()
```
```python
# Generated Starlark
SRCS = glob(["src/main/kotlin/**/*.kt"])
```

## Modifiers
Modifiers is a flexible mechanism that allows you to externally modify a file builder outside of its body.

Each Kotlin DSL element with a body surrounded by curly brackets `{}` could be modified.
To do so, you need to assign it a unique `_id`.

```kotlin
// Kotlin
val builder = BUILD.bazel {
    _id = "build_file"

    android_library {
        _id = "android_library_target"

        name = "my-library"
        deps = list["//another-library"]
    }
}
```
Then, use `onContext` API to inject additional code in the corresponding DSL context.

```kotlin
// Kotlin
builder.onContext<BuildContext>(id = "build_file") {
    android_binary(
        name = "app",
        deps = list[":my-library"]
    )
}
```
```kotlin
// Kotlin
builder.onContext<AndroidLibraryContext>(id = "android_library_target") {
    visibility = list["//visibility:public"]
    deps = list["@maven//:androidx_compose_runtime_runtime"]
}
```

```python
# Generated Starlark
android_library(
    name = "my-library"
    deps = ["//another-library"] + ["@maven//:androidx_compose_runtime_runtime"],
)
```

### Checkpoints
The code added by modifiers is always added at the end of the context block. However, with checkpoints you can precisely control where the code from modifiers is injected.  

```kotlin
// Kotlin
val builder = BUILD.bazel {
    _id = "build_file"

    val DEPS by list["@maven//:androidx_compose_runtime_runtime"]

    _checkpoint("middle")

    android_library(
        name = "my-library",
        deps = DEPS
    )
}
```

```kotlin
// Kotlin
builder.onContext<BuildContext>(id = "build_file", checkpoint = "middle") {
    android_binary(
        name = "app",
        deps = list["my-library"]
    )
}
```

```python
# Generated Starlark
DEPS = ["@maven//:androidx_compose_runtime_runtime"]

android_binary(
    name = "app",
    deps = ["my-library"],
)

android_library(
    name = "my-library",
    deps = DEPS,
)
```


## Generating DSL for custom functions

