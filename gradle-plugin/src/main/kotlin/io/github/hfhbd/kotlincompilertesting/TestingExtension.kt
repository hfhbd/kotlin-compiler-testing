package io.github.hfhbd.kotlincompilertesting

import org.gradle.api.Action
import org.gradle.api.artifacts.dsl.Dependencies
import org.gradle.api.artifacts.dsl.DependencyCollector
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested

interface TestingExtension {
    val mainClass: Property<String>

    @get:Nested
    val dependencies: TestingDependencies

    fun dependencies(action: Action<TestingDependencies>) {
        action.execute(dependencies)
    }
}

interface TestingDependencies : Dependencies {
    val annotation: DependencyCollector
}
