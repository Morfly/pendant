import io.morfly.pendant.buildtools.PendantConventionPlugin

class PendantMetadataPlugin : PendantConventionPlugin({})

object PendantMetadata {
    const val JVM_TOOLCHAIN_VERSION = 8
    const val KOTLIN_LANGUAGE_VERSION = "1.7"

    const val ARTIFACT_GROUP = "io.morfly.pendant"
}