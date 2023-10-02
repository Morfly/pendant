import io.morfly.pendant.buildtools.PendantConventionPlugin
import io.morfly.pendant.buildtools.kotlin
import io.morfly.pendant.buildtools.libs
import org.gradle.api.GradleException

class PendantMultiplatformLibraryPlugin : PendantConventionPlugin({
    with(pluginManager) {
        apply(libs.plugins.pendant.kotlin.multiplatform.common.get().pluginId)
    }

    kotlin {
        jvm {
            jvmToolchain(PendantMetadata.JVM_TOOLCHAIN_VERSION)
            withJava()
            testRuns.named("test") {
                executionTask.configure {
                    useJUnitPlatform()
                }
            }
        }
        js {
            browser {
                commonWebpackConfig {
                    cssSupport {
                        enabled.set(true)
                    }
                }
            }
        }
        val hostOs = System.getProperty("os.name")
        val isArm64 = System.getProperty("os.arch") == "aarch64"
        val isMingwX64 = hostOs.startsWith("Windows")
        val nativeTarget = when {
            hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
            hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
            hostOs == "Linux" && isArm64 -> linuxArm64("native")
            hostOs == "Linux" && !isArm64 -> linuxX64("native")
            isMingwX64 -> mingwX64("native")
            else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
        }
    }
})