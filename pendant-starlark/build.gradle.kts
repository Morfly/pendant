import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.pendant.kotlin.multiplatform.library)
    alias(libs.plugins.pendant.maven.publish)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }
}

kotlin {
    sourceSets {
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotest.junit5)
                implementation(libs.kotest.assertions)
            }
        }
    }
}

