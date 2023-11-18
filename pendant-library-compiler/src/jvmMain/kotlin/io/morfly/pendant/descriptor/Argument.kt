package io.morfly.pendant.descriptor

interface Argument {
    val type: SpecifiedType
    val kotlinName: String
    val starlarkName: String
}

data class Arg(
    override val kotlinName: String,
    override val starlarkName: String,
    override val type: SpecifiedType,
    val isRequired: Boolean,
) : Argument

data class Vararg(
    override val kotlinName: String,
    override val starlarkName: String,
    override val type: SpecifiedType,
    val fullType: SpecifiedType,
    val isRequired: Boolean,
) : Argument

fun Vararg.toArgument(): Arg =
    Arg(
        kotlinName = kotlinName,
        starlarkName = "",
        type = fullType,
        isRequired = isRequired
    )
