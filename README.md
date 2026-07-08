# kotlin-compiler-testing

A Gradle plugin to generate the Kotlin compiler test classes used by the Kotlin compiler test framework under `build`. 

## Usage

```kotlin
plugins {
    id("jvm")
    id("io.github.hfhbd.kotlin-compiler-testing") version "LATEST"
}

kotlinTesting {
  mainClass = "app.softwork.validation.plugin.kotlin.GenerateTestsKt" // the custom entrypoint for your tests

  dependencies {
    annotation(projects.runtime)
  }
}
```
