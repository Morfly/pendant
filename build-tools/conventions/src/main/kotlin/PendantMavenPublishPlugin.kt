import com.vanniktech.maven.publish.SonatypeHost
import io.morfly.pendant.buildtools.PendantConventionPlugin
import io.morfly.pendant.buildtools.libs
import io.morfly.pendant.buildtools.mavenPublishing

class PendantMavenPublishPlugin : PendantConventionPlugin({
    with(pluginManager) {
        apply(libs.plugins.vanniktech.maven.publish.get().pluginId)
        apply(libs.plugins.dokka.get().pluginId)
    }

    mavenPublishing {
        val version: String by properties
        coordinates(
            groupId = PendantMetadata.ARTIFACT_GROUP,
            artifactId = project.name,
            version = version
        )

        pom {
            name.set("Pendant")
            description.set("Declarative Starlark code generator written in Kotlin.")
            inceptionYear.set("2023")
            url.set("https://github.com/Morfly/pendant")
            licenses {
                license {
                    name.set("The MIT License")
                    url.set("https://opensource.org/license/mit/")
                    distribution.set("https://opensource.org/license/mit/")
                }
            }
            developers {
                developer {
                    id.set("Morfly")
                    name.set("Pavlo Stavytskyi")
                    url.set("https://github.com/Morfly")
                }
            }
            scm {
                url.set("https://github.com/Morfly/pendant")
                connection.set("scm:git:git://github.com/Morfly/pendant.git")
                developerConnection.set("scm:git:ssh://git@github.com/Morfly/pendant.git")
            }
        }

        publishToMavenCentral(SonatypeHost.S01)
        signAllPublications()
    }
})

