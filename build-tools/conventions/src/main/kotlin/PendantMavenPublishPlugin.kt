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
            description.set("TODO")
            inceptionYear.set("2023")
            url.set("https://github.com/open-turo/nibel")
            licenses {
                license {
                    name.set("The MIT License")
                    url.set("https://opensource.org/license/mit/")
                    distribution.set("https://opensource.org/license/mit/")
                }
            }
            developers {
                developer {
                    id.set("openturo")
                    name.set("Turo Open Source")
                    url.set("https://github.com/open-turo")
                }
            }
            scm {
                url.set("https://github.com/open-turo/nibel")
                connection.set("scm:git:git://github.com/open-turo/nibel.git")
                developerConnection.set("scm:git:ssh://git@github.com/open-turo/nibel.git")
            }
        }

        publishToMavenCentral(SonatypeHost.S01)
        signAllPublications()
    }
})

