plugins {
    alias(libs.plugins.pendant.kotlin.multiplatform.library)
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(projects.pendant)
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
    add("kspJvm", projects.pendantCompiler)
    add("kspJvmTest", projects.pendantCompiler)
}