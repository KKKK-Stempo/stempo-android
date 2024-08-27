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
        classpath(libs.kotlin.gradle.plugin)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}