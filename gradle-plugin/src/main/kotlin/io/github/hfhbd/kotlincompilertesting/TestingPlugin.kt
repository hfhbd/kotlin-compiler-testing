package io.github.hfhbd.kotlincompilertesting

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.base.TestingExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

abstract class TestingPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.apply("org.jetbrains.kotlin.jvm")
        target.pluginManager.apply("java-test-fixtures")
        target.pluginManager.apply("jvm-test-suite")

        val kotlinExtension = target.extensions.getByName("kotlin") as KotlinJvmProjectExtension
        kotlinExtension.sourceSets.getByName("main").dependencies {
            compileOnly(kotlin("compiler"))
        }
        kotlinExtension.sourceSets.getByName("testFixtures").dependencies {
            api(kotlin("test-junit5"))
            api(kotlin("compiler-internal-test-framework"))
            api(kotlin("compiler"))
        }
        kotlinExtension.sourceSets.getByName("test").dependencies {
            // Dependencies required to run the internal test framework.
            runtimeOnly(kotlin("reflect"))
            runtimeOnly(kotlin("script-runtime"))
            runtimeOnly(kotlin("annotations-jvm"))
        }

        val extension = target.extensions.create("kotlinTesting", io.github.hfhbd.kotlincompilertesting.TestingExtension::class.java)

        val annotationsRuntimeClasspath = target.configurations.resolvable("annotationsRuntimeClasspath") {
            it.fromDependencyCollector(extension.dependencies.annotation)
        }

        val sourceSets = target.extensions.getByName("sourceSets") as SourceSetContainer

        val generateTests = target.tasks.register("generateTests", GenerateKotlinCompilerTests::class.java) {
            val i = target.layout.projectDirectory.dir("src/testFixtures/resources/testData")
            it.testData.set(i)
            val o = target.layout.buildDirectory.dir("generated/kotlinCompilerTests")
            it.generatedTests.set(o)

            it.classpath = sourceSets.getByName("testFixtures").runtimeClasspath

            it.systemProperty(
                "testData",
                i.asFile.toRelativeString(target.layout.projectDirectory.asFile)
            )

            it.systemProperty(
                "generatedTests",
                o.get().asFile.toRelativeString(target.layout.projectDirectory.asFile)
            )
            it.mainClass.convention(extension.mainClass)
        }

        val testing = target.extensions.getByType(TestingExtension::class.java)
        testing.suites.named("test", JvmTestSuite::class.java) {
            it.useKotlinTest()

            it.sources.java.srcDir(generateTests)

            it.targets.configureEach {
                it.testTask.configure {
                    it.inputs.files(
                        annotationsRuntimeClasspath,
                        it.project.configurations.named("testRuntimeClasspath")
                    )

                    it.systemProperty("annotationsRuntime.classpath", annotationsRuntimeClasspath.get().asPath)

                    // Properties required to run the internal test framework.
                    it.setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib", "kotlin-stdlib")
                    it.setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib-jdk8", "kotlin-stdlib-jdk8")
                    it.setLibraryProperty("org.jetbrains.kotlin.test.kotlin-reflect", "kotlin-reflect")
                    it.setLibraryProperty("org.jetbrains.kotlin.test.kotlin-test", "kotlin-test")
                    it.setLibraryProperty("org.jetbrains.kotlin.test.kotlin-script-runtime", "kotlin-script-runtime")
                    it.setLibraryProperty("org.jetbrains.kotlin.test.kotlin-annotations-jvm", "kotlin-annotations-jvm")

                    it.systemProperty("idea.ignore.disabled.plugins", "true")
                    it.systemProperty("idea.home.path", it.project.layout.projectDirectory.asFile.absolutePath)
                }
            }
        }
    }

    private fun Test.setLibraryProperty(propName: String, jarName: String) {
        val path = project.configurations
            .getByName("testRuntimeClasspath")
            .files
            .find { """$jarName-\d.*jar""".toRegex().matches(it.name) }
            ?.absolutePath
            ?: return
        systemProperty(propName, path)
    }
}
