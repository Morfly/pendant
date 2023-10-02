import io.morfly.pendant.buildtools.PendantConventionPlugin
import io.morfly.pendant.buildtools.libs
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class PendantMultiplatformCommonPlugin: PendantConventionPlugin({
    with(pluginManager) {
        apply(libs.plugins.kotlin.multiplatform.get().pluginId)
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            languageVersion = PendantMetadata.KOTLIN_LANGUAGE_VERSION
        }
    }
})