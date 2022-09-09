object Dependencies {

    object GradlePlugin{
        const val kotlin ="org.jetbrains.kotlin:kotlin-gradle-plugin:${Dependencies.kotlin.version}"
        // https://mvnrepository.com/artifact/org.jetbrains.kotlin.android/org.jetbrains.kotlin.android.gradle.plugin
        const val androidKotlin ="org.jetbrains.kotlin.android:org.jetbrains.kotlin.android.gradle.plugin:${Dependencies.kotlin.version}"
        // https://mvnrepository.com/artifact/com.android.tools.build/gradle
        const val android ="com.android.tools.build:gradle:7.2.0"
        // https://mvnrepository.com/artifact/org.jfrog.buildinfo/build-info-extractor-gradle
        const val jfrog ="org.jfrog.buildinfo:build-info-extractor-gradle:4.29.0"
        // https://mvnrepository.com/artifact/org.jetbrains.dokka/dokka-gradle-plugin
        const val dokka ="org.jetbrains.dokka:dokka-gradle-plugin:1.7.10"
    }

    object kotlin {
        const val version = "1.7.10"

        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${version}"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${version}"
        const val stdlib_jdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${version}"
    }

    object androidx {
        const val coreKtx = "androidx.core:core-ktx:1.3.1"
        const val appcompat = "androidx.appcompat:appcompat:1.2.0"
        const val viewbinding = "androidx.databinding:viewbinding:4.2.1"
        const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:2.3.1"
        const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1"
        const val localbroadcast = "androidx.localbroadcastmanager:localbroadcastmanager:1.0.0"
    }

    object google {
        const val material = "com.google.android.material:material:1.2.1"
    }

    object squareup {
        const val version = "1.12.0"
        
        const val kotlinpoet = "com.squareup:kotlinpoet:$version"
        const val kotlinpoet_ksp = "com.squareup:kotlinpoet-ksp:$version"
    }

    val baseLibs: List<String>
        get() = listOf(
            kotlin.stdlib,
            androidx.coreKtx,
            androidx.appcompat,
        )

}

//fun DependencyHandler.implementation(list: List<String>) {
//    list.forEach { dependency ->
//        add("implementation", dependency)
//    }
//}
//
//fun DependencyHandler.implementation(dependency: String) {
//    add("implementation", dependency)
//}
