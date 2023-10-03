import io.morfly.pendant.buildtools.libs

plugins {
    alias(libs.plugins.pendant.kotlin.multiplatform.common)
    alias(libs.plugins.pendant.maven.publish)
    alias(libs.plugins.ksp)
}

kotlin {
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
