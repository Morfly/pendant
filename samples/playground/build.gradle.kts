import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }
}

dependencies {
    implementation(projects.pendantStarlark)
    implementation(projects.pendantLibraryBazel)
    ksp(projects.pendantLibraryCompiler)
}