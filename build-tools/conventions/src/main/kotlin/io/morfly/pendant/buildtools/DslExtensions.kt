package io.morfly.pendant.buildtools

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

// Makes 'libs' version catalog available for precompiled plugins in a type-safe manner.
// https://github.com/gradle/gradle/issues/15383#issuecomment-1245546796
val Project.libs get() = extensions.getByType<LibrariesForLibs>()

fun Project.kotlin(body: KotlinMultiplatformExtension.() -> Unit): Unit =
    extensions.configure("kotlin", body)