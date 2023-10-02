plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ksp)
}

kotlin {
    jvm {
        jvmToolchain(11)
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(projects.pendantStarlark)

                implementation(libs.ksp.api)
                implementation(libs.autoService.annotations)
            }
        }
    }
}

dependencies {
    add("kspJvm", libs.autoService.ksp)
}
