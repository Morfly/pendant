package io.morfly.pendant.descriptor

import com.google.devtools.ksp.symbol.KSFile

data class GeneratedFile(
    override val shortName: String,
    val packageName: String,
    val functions: MutableList<GeneratedFunction> = mutableListOf(),
    val originalFile: KSFile
) : NameHolder {
    override val fullName = "$shortName.kt"
    override val qualifiedName: String? = null
}
