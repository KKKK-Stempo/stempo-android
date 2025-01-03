buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }

    dependencies {
        classpath(ClassPathPlugins.gradle)
        classpath(ClassPathPlugins.kotlinGradle)
        classpath(ClassPathPlugins.hilt)
        classpath(ClassPathPlugins.oss)
        classpath(ComposePlugins.composeCompiler)
    }
}

plugins {
    id("org.jetbrains.compose") version "1.5.3" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}