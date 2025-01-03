plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.compose")
}

android {
    namespace = "com.kkkk.stempo.presentation"
    compileSdk = Constants.compileSdk

    defaultConfig {
        minSdk = Constants.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "VERSION_NAME", "\"${Constants.versionName}\"")
    }

    compileOptions {
        sourceCompatibility = Versions.javaVersion
        targetCompatibility = Versions.javaVersion
    }

    kotlinOptions {
        jvmTarget = Versions.jvmVersion
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composePluginVersion
    }

    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
        compose = true
    }
}

dependencies {
    implementation(project(":core-ui"))
    implementation(project(":domain"))

    KotlinDependencies.run {
        implementation(kotlin)
        implementation(coroutines)
        implementation(jsonSerialization)
        implementation(dateTime)
    }

    AndroidXDependencies.run {
        implementation(coreKtx)
        implementation(appCompat)
        implementation(constraintLayout)
        implementation(fragment)
        implementation(navigationFragment)
        implementation(navigationUi)
        implementation(startup)
        implementation(legacy)
        implementation(security)
        implementation(hilt)
        implementation(lifeCycleKtx)
        implementation(lifecycleJava8)
        implementation(splashScreen)
        implementation(workManager)
        implementation(hiltWorkManager)
        implementation(wearable)
    }

    KaptDependencies.run {
        kapt(hiltCompiler)
        kapt(hiltWorkManagerCompiler)
    }

    implementation(MaterialDesignDependencies.materialDesign)

    TestDependencies.run {
        testImplementation(jUnit)
        androidTestImplementation(androidTest)
        androidTestImplementation(espresso)
    }

    ThirdPartyDependencies.run {
        implementation(coil)
        implementation(timber)
        implementation(ossLicense)
        implementation(progressView)
        implementation(balloon)
        implementation(lottie)
        implementation(circularProgressBar)
        implementation(circleIndicator)
        implementation(phoenix)
    }

    JitPackDependencies.run {
        implementation(mpChart)
    }

    ComposeDependencies.run {
        implementation(androidxComposeBom)
        implementation(androidxComposeMaterial3)
        implementation(androidxComposeUi)
        implementation(androidxComposeUiTooling)
        implementation(androidxComposeUiToolingPreview)
        implementation(androidxComposeNavigation)
        implementation(androidxComposePreview)
        implementation(hiltNavigationCompose)
        implementation(androidxUiGraphics)
    }
}
