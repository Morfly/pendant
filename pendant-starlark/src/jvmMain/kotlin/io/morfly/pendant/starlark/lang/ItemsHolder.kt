package io.morfly.pendant.starlark.lang

import io.morfly.pendant.starlark.element.Expression

internal interface ItemsHolder {

    val items: MutableList<Expression>
}
