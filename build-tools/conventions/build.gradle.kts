plugins {
    `kotlin-dsl`
}

dependencies {
    // Makes 'libs' version catalog visible and type-safe for precompiled plugins.
    // https://github.com/gradle/gradle/issues/15383#issuecomment-1245546796
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    compileOnly(libs.gradlePlugin.kotlin)
    compileOnly(libs.gradlePlugin.mavenPublish)
}

gradlePlugin {
    plugins {
        val pendantMetadata by registering {
            id = "pendant.metadata"
            implementationClass = "PendantMetadataPlugin"
        }
        val kotlinMultiplatformLibrary by registering {
            id = "pendant.kotlin.multiplatform.library"
            implementationClass = "PendantMultiplatformLibraryPlugin"
        }
        val kotlinMultiplatformCommon by registering {
            id = "pendant.kotlin.multiplatform.common"
            implementationClass = "PendantMultiplatformCommonPlugin"
        }
        val mavenPublish by registering {
            id = "pendant.maven.publish"
            implementationClass = "PendantMavenPublishPlugin"
        }
    }
}
