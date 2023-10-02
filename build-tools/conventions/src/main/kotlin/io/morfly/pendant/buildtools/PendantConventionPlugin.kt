package io.morfly.pendant.buildtools

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class PendantConventionPlugin(
    private val body: Project.() -> Unit
) : Plugin<Project> {

    override fun apply(target: Project) = target.body()
}