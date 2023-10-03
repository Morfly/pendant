import io.morfly.pendant.buildtools.PendantConventionPlugin
import io.morfly.pendant.buildtools.kotlin
import io.morfly.pendant.buildtools.libs
import io.morfly.pendant.buildtools.sourceSets
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class PendantMultiplatformCommonPlugin : PendantConventionPlugin({
    with(pluginManager) {
        apply(libs.plugins.kotlin.multiplatform.get().pluginId)
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            languageVersion = PendantMetadata.KOTLIN_LANGUAGE_VERSION
        }
    }
    kotlin {
        jvm {
            jvmToolchain(PendantMetadata.JVM_TOOLCHAIN_VERSION)
            testRuns.named("test") {
                executionTask.configure {
                    useJUnitPlatform()
                }
            }
        }

        sourceSets {
            getByName("commonMain") {
                dependencies {
                    implementation(libs.kotlin.compat.stdlib)
                }
            }
            getByName("jvmMain") {
                dependencies {
                    implementation(libs.kotlin.compat.stdlib)
                }
            }
        }
    }
})