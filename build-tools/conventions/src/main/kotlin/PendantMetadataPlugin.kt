import io.morfly.pendant.buildtools.PendantConventionPlugin

class PendantMetadataPlugin: PendantConventionPlugin({})

object PendantMetadata {
    const val JVM_TOOLCHAIN = 11
    const val KOTLIN_LANGUAGE_VERSION = "1.7"
}