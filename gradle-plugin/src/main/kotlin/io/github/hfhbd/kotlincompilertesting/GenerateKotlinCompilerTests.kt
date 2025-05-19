package io.github.hfhbd.kotlincompilertesting

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.work.NormalizeLineEndings

abstract class GenerateKotlinCompilerTests : JavaExec() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:SkipWhenEmpty
    @get:NormalizeLineEndings
    abstract val testData: DirectoryProperty

    @get:OutputDirectory
    abstract val generatedTests: DirectoryProperty
}
