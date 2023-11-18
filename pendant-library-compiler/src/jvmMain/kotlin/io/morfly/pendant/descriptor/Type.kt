package io.morfly.pendant.descriptor

sealed interface Type

data class SpecifiedType(
    override val shortName: String,
    override val qualifiedName: String,
    val packageName: String,
    val isMarkedNullable: Boolean,
    private val actual: SpecifiedType?,
    val genericArguments: List<SpecifiedType>
) : Type, NameHolder {
    private val nullabilitySuffix = if (isMarkedNullable) "?" else ""

    override val fullName: String = if (genericArguments.isEmpty()) {
        shortName + nullabilitySuffix
    } else shortName + genericArguments.joinToString(prefix = "<", separator = ", ", postfix = ">") {
        it.fullName
    } + nullabilitySuffix

    val actualType = actual ?: this
}

object DynamicType : Type, NameHolder {
    override val shortName = "<dynamic>"
    override val fullName = shortName
    override val qualifiedName: String? = null
}

val VoidType = SpecifiedType(
    shortName = Unit::class.simpleName!!,
    qualifiedName = Unit::class.qualifiedName!!,
    packageName = "kotlin",
    isMarkedNullable = false,
    actual = null,
    genericArguments = emptyList()
)
