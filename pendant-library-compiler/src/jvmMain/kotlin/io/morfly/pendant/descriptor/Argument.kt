package io.morfly.pendant.descriptor

sealed interface Argument {
    val type: SpecifiedType
    val kotlinName: String
    val starlarkName: String
}

data class NamedArgument(
    override val kotlinName: String,
    override val starlarkName: String,
    override val type: SpecifiedType,
    val isRequired: Boolean,
) : Argument

data class VariadicArgument(
    override val kotlinName: String,
    override val starlarkName: String,
    override val type: SpecifiedType,
    val fullType: SpecifiedType,
    val isRequired: Boolean,
) : Argument

fun VariadicArgument.toNamedArgument(): NamedArgument =
    NamedArgument(
        kotlinName = kotlinName,
        starlarkName = "",
        type = fullType,
        isRequired = isRequired
    )
