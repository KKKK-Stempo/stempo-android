object KotlinDependencies {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesAndroidVersion}"
    const val jsonSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinSerializationJsonVersion}"
    const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:${Versions.kotlinDateTimeVersion}"
}

object AndroidXDependencies {
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtxVersion}"
    const val splashScreen = "androidx.core:core-splashscreen:${Versions.splashVersion}"

    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompatVersion}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayoutVersion}"
    const val startup = "androidx.startup:startup-runtime:${Versions.appStartUpVersion}"
    const val fragment = "androidx.fragment:fragment-ktx:${Versions.fragmentKtxVersion}"
    const val legacy = "androidx.legacy:legacy-support-v4:${Versions.legacySupportVersion}"
    const val security = "androidx.security:security-crypto:${Versions.securityVersion}"

    const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigationVersion}"
    const val navigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.navigationVersion}"

    const val lifeCycleKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleVersion}"
    const val lifecycleJava8 = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycleVersion}"
    const val workManager = "androidx.work:work-runtime-ktx:${Versions.workManagerVersion}"

    const val hiltWorkManager = "androidx.hilt:hilt-work:1.0.0"
    const val hilt = "com.google.dagger:hilt-android:${Versions.hiltVersion}"
    const val ossLicense = "com.google.android.gms:play-services-oss-licenses:${Versions.ossVersion}"
}

object TestDependencies {
    const val jUnit = "junit:junit:${Versions.junitVersion}"
    const val androidTest = "androidx.test.ext:junit:${Versions.androidTestVersion}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espressoVersion}"
}

object MaterialDesignDependencies {
    const val materialDesign = "com.google.android.material:material:${Versions.materialDesignVersion}"
}

object KaptDependencies {
    const val hiltAndroidCompiler = "com.google.dagger:hilt-compiler:${Versions.hiltVersion}"
    const val hiltCompiler = "com.google.dagger:hilt-compiler:${Versions.hiltVersion}"
    const val hiltWorkManagerCompiler = "androidx.hilt:hilt-compiler:1.0.0"
}

object ThirdPartyDependencies {
    const val coil = "io.coil-kt:coil:${Versions.coilVersion}"

    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofitVersion}"
    const val retrofitJsonConverter = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:${Versions.kotlinSerializationConverterVersion}"
    const val okHttpBom = "com.squareup.okhttp3:okhttp-bom:${Versions.okHttpVersion}"
    const val okHttp = "com.squareup.okhttp3:okhttp"
    const val okHttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor"

    const val timber = "com.jakewharton.timber:timber:${Versions.timberVersion}"

    const val ossLicense = "com.google.android.gms:play-services-oss-licenses:${Versions.ossVersion}"

    const val progressView = "com.github.skydoves:progressview:${Versions.progressViewVersion}"
    const val balloon = "com.github.skydoves:balloon:${Versions.balloonVersion}"
    const val lottie = "com.airbnb.android:lottie:${Versions.lottieVersion}"
    const val circularProgressBar = "com.mikhaellopez:circularprogressbar:${Versions.circularProgressBar}"
    const val circleIndicator = "me.relex:circleindicator:${Versions.circleIndicatorVersion}"
    const val shimmer = "com.facebook.shimmer:shimmer:${Versions.shimmerVersion}"
}

object ClassPathPlugins {
    const val gradle = "com.android.tools.build:gradle:${Versions.gradleVersion}"
    const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
    const val hilt = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hiltVersion}"
    const val oss = "com.google.android.gms:oss-licenses-plugin:${Versions.ossPluginVersion}"
}
