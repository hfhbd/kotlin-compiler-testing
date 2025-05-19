import io.github.hfhbd.kotlincompilertesting.GenerateKotlinCompilerTests

plugins {
    kotlin("jvm")
    id("java-test-fixtures")
}

val annotationsRuntime = configurations.dependencyScope("annotationsRuntime")
val annotationsRuntimeClasspath = configurations.resolvable("annotationsRuntimeClasspath") {
    extendsFrom(annotationsRuntime.get())
    isTransitive = false
}

dependencies {
    compileOnly(kotlin("compiler"))

    testFixturesApi(kotlin("test-junit5"))
    testFixturesApi(kotlin("compiler-internal-test-framework"))
    testFixturesApi(kotlin("compiler"))

    // Dependencies required to run the internal test framework.
    testRuntimeOnly(kotlin("reflect"))
    testRuntimeOnly(kotlin("script-runtime"))
    testRuntimeOnly(kotlin("annotations-jvm"))
}

tasks.test {
    inputs.files(annotationsRuntimeClasspath, configurations.testRuntimeClasspath)

    useJUnitPlatform()

    systemProperty("annotationsRuntime.classpath", annotationsRuntimeClasspath.get().asPath)

    // Properties required to run the internal test framework.
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib", "kotlin-stdlib")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib-jdk8", "kotlin-stdlib-jdk8")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-reflect", "kotlin-reflect")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-test", "kotlin-test")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-script-runtime", "kotlin-script-runtime")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-annotations-jvm", "kotlin-annotations-jvm")
}

val generateTests by tasks.registering(GenerateKotlinCompilerTests::class) {
    val i = layout.projectDirectory.dir("src/testFixtures/resources/testData")
    testData.set(i)
    val o = layout.buildDirectory.dir("generated/kotlinCompilerTests")
    generatedTests.set(o)

    classpath = sourceSets.testFixtures.get().runtimeClasspath

    systemProperty(
        "testData",
        i.asFile.toRelativeString(layout.projectDirectory.asFile)
    )

    systemProperty(
        "generatedTests",
        o.get().asFile.toRelativeString(layout.projectDirectory.asFile)
    )
}

sourceSets {
    test {
        java.srcDir(generateTests)
    }
}

fun Test.setLibraryProperty(propName: String, jarName: String) {
    val path = project.configurations
        .testRuntimeClasspath.get()
        .files
        .find { """$jarName-\d.*jar""".toRegex().matches(it.name) }
        ?.absolutePath
        ?: return
    systemProperty(propName, path)
}
