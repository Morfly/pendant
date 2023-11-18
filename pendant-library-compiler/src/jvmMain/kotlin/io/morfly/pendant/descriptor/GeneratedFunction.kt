package io.morfly.pendant.descriptor

import io.morfly.pendant.starlark.lang.BracketsKind
import io.morfly.pendant.starlark.lang.FunctionKind
import io.morfly.pendant.starlark.lang.FunctionScope

data class GeneratedFunction(
    override val shortName: String,
    val annotatedClassName: String,
    val arguments: List<NamedArgument>,
    val vararg: VariadicArgument?,
    val returnType: Type,
    val scope: Set<FunctionScope>,
    val kind: FunctionKind,
    val brackets: Set<BracketsKind>
) : NameHolder {
    override val fullName: String = shortName
    override val qualifiedName: String? = null

    val hasArgs: Boolean get() = arguments.isNotEmpty() || vararg != null
}