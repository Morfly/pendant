package io.morfly.pendant.starlark.lang.type

interface Struct {

    val fields: DictionaryType<String, Any?>
}

data class StructImpl(override val fields: DictionaryType<String, Any?>) : Struct

fun Struct.toDictionary(): DictionaryType<String, Any?> = fields

fun DictionaryType<String, Any?>.toStruct(): Struct = StructImpl(this)

fun structOf(vararg fields: Pair<String, Any?>): Struct =
    StructImpl(fields.toMap())

fun emptyStruct(): Struct = StructImpl(emptyMap())
