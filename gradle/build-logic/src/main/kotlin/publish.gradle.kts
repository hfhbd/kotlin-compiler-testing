plugins {
    id("maven-publish")
    id("signing")
    id("io.github.hfhbd.mavencentral")
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

            // https://github.com/gradle/gradle/issues/28759
            this.withXml {
                this.asNode().appendNode("distributionManagement").appendNode("repository").apply {
                    this.appendNode("id", "github")
                    this.appendNode("name", "GitHub hfhbd Apache Maven Packages")
                    this.appendNode("url", "https://maven.pkg.github.com/hfhbd/kotlin-compiler-testing")
                }
            }
        }
    }
}

signing {
    val signingKey = providers.gradleProperty("signingKey")
    if (signingKey.isPresent) {
        useInMemoryPgpKeys(signingKey.get(), providers.gradleProperty("signingPassword").get())
        sign(publishing.publications)
    }
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
    filePermissions {}
    dirPermissions {}
}
