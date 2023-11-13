# Pendant

Pendant - is a declarative Starlark code generator written in Kotlin.
Provides a Kotlin DSL that looks exactly like Starlark, for generating Bazel scripts with type-safety in mind.

## Installation

### Gradle

```kotlin
dependencies {
    // Starlark code generator.
    implementation("io.morfly.pendant:pendant-starlark:<version>")
    // Library of Bazel functions.
    implementation("io.morfly.pendant:pendant-library-bazel:<version>")

    // Optional. Annotation processor for generating libraries with custom Starlark functions.
    ksp("io.morfly.pendant:pendant-library-compiler:<version>")
}
```

## How to Use
Let's see how Pendant could be used to generate a simple `BUILD.bazel` file that declares an Android library target with Bazel.

Notice, that the code below is a valid Kotlin DSL which looks almost exactly as Starlark syntax itself. Moreover, the type-safety of the code is fully preserved.
```kotlin
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

As a result, a `BUILD` file with the following content is generated. As you can see, Pendant takes care of the code formatting, so you don't have to do it yourself.

```python
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
Let's take a deeper look at Pendant API and how can you generate files with Starlark code. 

### Building Starlark files
First, let's see how can we enter the DSL context and define what type of file we're generating.

For example, to generate `BUILD.bazel` files, the following expression must be used.

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
Each function demonstrated above returns an instance of a file builder. In order to write it to file it must be first built using `build()` function.

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
Now, the most important part. In this section you're going to see what Kotlin DSL components Pendant provides for a declarative and type-safe Starlark code generation.

### Variable assignments
Variable declaration and assignment is an essential feature of Starlark language. Therefore, Pendant must provide an API for generating it. Use `by` operator to do it.
> You might ask, why aren't we using `=` operator in this case? This is because the latter operator will perform a variable assignment operation while compiling the Kotlin code. Therefore, this operation will be absent in the generated Starlark code.

```kotlin
// Kotlin
val NAME by "my-library"
```
```python
// Generated Starlark
NAME = "my_library"
```
What's important is that this operation is type safe, meaning `NAME` is a variable of string type on the Kotlin DSL level and could be used further accordingly.
As a result, the following Starlark statement is generated

### List expressions
List expressions are part of Starlark syntax. Unfortunately, there is no equivalent for such an expression in Kotlin. 
However, the `list[]` function could be used to achieve the same reuslt.
```kotlin
// Kotlin
val SRCS by list["io/morfly/Main.kt"]
```
```python
// Generated Starlark
SRCS = ["io/morfly/Main.kt"]
```

Regular `listOf` function from Kotlin also works perfectly well. However, having `list[]` function is useful when you're copy-pasting actual Starlark code in your Kotlin file. This way you would need to do less editing to make it compilable in your code generator program. 

### Dictionary expressions
Similarly to list expressions, Kotlin does not have dictionary expressions in its syntax. However, the `dict` function could be used to achieve the same result.
```kotlin
// Kotlin
val MANIFEST_VALUES by dict { "minSdkVersion" to "23" }
```

```python
// Generated Starlark
MANIFEST_VALUES = { "minSdkVersion" : "23" }
```
You might notice that `to` operator is used to map dictionary values. However, this is not a usual `to` function from Kotlin standard library but a more powerful version.

For example, you could use composite keys and values with concatenation operation.

```kotlin
// Kotlin
val MANIFEST_VALUES by dict { "minSdk" `+` "Version" to "2" `+` "3"}
```
```python
// Generated Starlark
MANIFEST_VALUES = { "minSdk" + "Version" : "2" + "3" }
```

### Concatenations
Using `` `+` `` function with backticks you could generate concatenation expressions. 

>You might ask, why aren't we using just a regular `+`operator in this case? This is because the latter operator will perform a concatenation operation while compiling the Kotlin code. Therefore, the `+` operator will be absent in the generated Starlark code. 


```kotlin
// Kotlin
val ARTIFACTS by list["@maven//:androidx_compose_runtime_runtime"]

val DEPS by ARTIFACTS `+` list["//my-library"]
```
```python
// Generated Starlark
ARTIFACTS = ["@maven//:androidx_compose_runtime_runtime"]

DEPS = ARTIFACTS + ["//my-library"]
```
As you can see, you could use concatenations with varouus types of expressions, like list expressions, variable references, list comprehensions, etc. This operation is completely type-safe. 

### Function calls

#### Function call statements
There are different ways to generate a function call with Pendant. 

The easiest way is to use Starlark or Bazel functions available directly in Pendant.

```kotlin
// Kotlin
kt_android_library(
    name = "my-library",
    srcs = glob("src/main/kotlin/**/*.kt"),
    deps = ARTIFACTS `+` ["//another-library"],
    manifest = "src/main/AndroidManifest.xml"
)
```
Each function in the library is available in 2 variations: with round brackets `()`, and with curly brackets `{}`.
The latter is especially useful if you need more customization.

For example, in the example below, you could use custom parameters which are not part of Pendant library. This might come in handy if a corresponding Starlark function was updated with new arguments which are not present in Pendant.
```kotlin
// Kotlin
kt_android_library {
    name = "my-library"
    "custom_arg" `=` "value"
}
```
Moreover, you could declare calls of functions which are completely absent in Pendant library like whown below.
```kotlin
// Kotlin
"custom_function" {
    "custom_arg" `=` "value"
}
```
One more bonus of generating function calls using curly brackets `{}` is that the order of parameters in the call is preserved the way you specify them on Kotlin level.

Alternatively, dynamic function calls could be used for functions with no arguments.
```kotlin
"custom_function"()
```

#### Function call expressions
So far we've seen how to generate function calls as standalone statements, meaning they don't return any value.

Additionally, Pendant allows generating functions as expressions that return values.

```kotlin
// Kotlin
val SRCS by glob("src/main/kotlin/**/*.kt")
```

```python
// Generated Starlark
SRCS = glob(["src/main/kotlin/**/*.kt"])
```
You could use dynamic API for there types of functions as well, just make sure to explicitly specify the return type as shown below.
```kotlin
// Kotlin
import io.morfly.pendant.starlark.lang.feature.invoke

val SRCS = "glob"<ListType<StringType>>("src/main/kotlin/**/*.kt")
```
```python
// Generated Starlark
SRCS = glob(["src/main/kotlin/**/*.kt"])
```
> You might need to manually import the `io.morfly.pendant.starlark.lang.feature.invoke`function from Pendant for dynamic function calls with return values. 

> Dynamic function calls that return values rely on context receivers, a feature introduced in recent versions of Kotlin. If you need to use it as part of Gradle plugin or scripts, it might not be supported, as Gradle uses older Kotlin versions.  

#### Type-safe API for custom functions
TODO

### List comprehensions
Another powerful Starlark feature for building lists is list comprehensions. Use combination of `` `in` `` and `take` operators to generate them with Pendant.
```kotlin
// Kotlin
val CLASSES by list["MainActivity", "MainViewModel"]

val SRCS by "name" `in` CLASSES take { it `+` ".kt" }
```

```python
// Generated Starlark
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
// Generated Starlark
MATRIX = [
    [1, 2],
    [3, 4]
]

NUMBERS = [number for list in MATRIX for number in list]
```
Alternatively, Pendant supports another type of nested comprehensions as shown below.
```kotlin
import io.morfly.pendant.starlark.lang.feature.invoke

val RANGE by "range"<ListType<StringType>>(5)
val SRCS by "i" `in` RANGE take { "j" `in` RANGE take { j -> j } }
```
```python
// Generated Starlark
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
// Generated Starlark
"abc.kt"[0:-3]
```

### Load statements

#### Load functions

```kotlin
// Kotlin
load("@rules_java//java:defs.bzl", "java_binary")
```

```python
// Generated Starlark
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
// Generated Starlark
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
It is possible to use `StringReference`, `NumberReference`, `BooleanReference`, `ListReference`, `DicrionaryReference`, `TupleReference` or `AnyReference` to refer imported values.

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
// Generated Starlark
SRCS = glob(["src/main/kotlin/**/*.kt"])
```

## Modifiers
Sometimes, you might need to modify the contents of the file you're generating externally
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
```kotlin
builder.onContext<BuildContext>(id = "build_file") {
    android_binary(
        name = "app",
        deps = list[":my-library"]
        ...
    )
}
```
```kotlin
builder.onContext<AndroidLibraryContext>(id = "android_library_target") {
    visibility = list["//visibility:public"]
    deps = list["@maven//:androidx_compose_runtime_runtime"]
}
```
```kotlin
val file = builder.build()
```

```python
// Generated Starlark
android_library(
    name = "my-library"
    deps = ["//another-library"] + ["@maven//:androidx_compose_runtime_runtime"],
)
```

## Generating DSL for custom functions