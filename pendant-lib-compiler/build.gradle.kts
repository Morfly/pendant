import io.morfly.pendant.buildtools.libs

plugins {
    alias(libs.plugins.pendant.kotlin.multiplatform.common)
    alias(libs.plugins.ksp)
}

kotlin {
    jvm {
        jvmToolchain(PendantMetadata.JVM_TOOLCHAIN_VERSION)
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
