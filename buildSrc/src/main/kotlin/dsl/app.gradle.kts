plugins {
    id("android-common")
    android
    id("kotlin-parcelize")
}

android {
    compileSdk = AppConfig.compileSdk
    
    buildFeatures {
        viewBinding = true
    }
    
    defaultConfig {
        applicationId = "${AppConfig.GROUP_ID}.${AppConfig.ARTIFACT_ID}.demo"
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName

//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = "androidx.test.ext.junit.runners.AndroidJUnit4"
        
        buildConfigField("int", "LOG_METHOD_COUNT", "10")
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
    implementation(Dependencies.androidx.coreKtx)
    implementation(Dependencies.androidx.appcompat)
    implementation(Dependencies.google.material)
    implementation(Dependencies.androidx.localbroadcast)
    
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")
    
    testImplementation("junit:junit:4.13.2")
    
    androidTestImplementation("androidx.test:core:1.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.3")
    androidTestImplementation("androidx.test.ext:truth:1.4.0")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    
    implementation(project(":lib_appbasic"))
    implementation(project(":libapp"))
}
