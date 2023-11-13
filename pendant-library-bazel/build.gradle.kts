plugins {
    alias(libs.plugins.pendant.kotlin.multiplatform.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.pendant.maven.publish)
    alias(libs.plugins.dokka)
}

kotlin {
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(projects.pendantStarlark)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotest.junit5)
                implementation(libs.kotest.assertions)
            }
        }
    }
}

dependencies {
    add("kspJvm", projects.pendantLibraryCompiler)
    add("kspJvmTest", projects.pendantLibraryCompiler)
}