plugins {
    id("maven-publish")
    id("signing")
    id("io.github.hfhbd.mavencentral")
    id("dev.sigstore.sign")
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        pom {
            name.set("hfhbd kotlin-compiler-testing")
            description.set("hfhbd kotlin-compiler-testing")
            url.set("https://github.com/hfhbd/kotlin-compiler-testing")
            licenses {
                license {
                    name.set("Apache-2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("hfhbd")
                    name.set("Philip Wedemann")
                    email.set("mybztg+mavencentral@icloud.com")
                }
            }
            scm {
                connection.set("scm:git://github.com/hfhbd/kotlin-compiler-testing.git")
                developerConnection.set("scm:git://github.com/hfhbd/kotlin-compiler-testing.git")
                url.set("https://github.com/hfhbd/kotlin-compiler-testing")
            }

            distributionManagement {
                repository {
                    id = "github"
                    name = "GitHub hfhbd Apache Maven Packages"
                    url = "https://maven.pkg.github.com/hfhbd/kotlin-compiler-testing"
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        providers.gradleProperty("signingKey").orNull,
        providers.gradleProperty("signingPassword").orNull,
    )
    isRequired = providers.gradleProperty("signingKey").isPresent
    sign(publishing.publications)
}
