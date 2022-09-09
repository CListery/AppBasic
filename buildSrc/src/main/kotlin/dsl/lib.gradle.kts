plugins {
    id("android-common")
    `android-library`
    id("kotlin-parcelize")
}

android {
    compileSdk = AppConfig.compileSdk
    
    defaultConfig {
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk

//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = "androidx.test.ext.junit.runners.AndroidJUnit4"
    }
    lint {
        abortOnError = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
//    sourceSets {
//        named("main") {
////            kotlin.srcDirs("src/main/kotlin")
//            java.srcDirs("src/main/java", "src/main/kotlin")
//        }
//    }
}
dependencies {
    Dependencies.baseLibs.forEach { implementation(it) }
    implementation(Dependencies.androidx.coreKtx)
    implementation(Dependencies.kotlin.stdlib_jdk8)
    implementation(Dependencies.kotlin.reflect)
    compileOnly(Dependencies.androidx.viewbinding)
    
    testImplementation("junit:junit:4.13.2")
    
    androidTestImplementation("androidx.test:core:1.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.3")
    androidTestImplementation("androidx.test.ext:truth:1.4.0")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
