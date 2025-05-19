plugins {
    `kotlin-dsl`
    id("publish")
}

kotlin.jvmToolchain(21)

dependencies {
    implementation(libs.plugins.kotlin.jvm.dep)
}

val Provider<PluginDependency>.dep: Provider<String> get() = map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }

java {
    withSourcesJar()
    withJavadocJar()
}

testing.suites.withType<JvmTestSuite>().configureEach {
    useKotlinTest()
}

gradlePlugin.plugins.configureEach {
    displayName = "Gradle plugin to setup kotlin-compiler-tests"
    description = "Gradle plugin to setup kotlin-compiler-tests"
}

configurations.configureEach {
    if (isCanBeConsumed) {
        attributes {
            attribute(
                GradlePluginApiVersion.GRADLE_PLUGIN_API_VERSION_ATTRIBUTE,
                objects.named(GradleVersion.version("9.0").version)
            )
        }
    }
}
