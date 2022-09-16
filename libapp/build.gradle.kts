plugins {
    id("lib")
}

android {
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(libs.bundles.baseLibs)
    implementation(libs.androidXCoreKtx)
    implementation(libs.jetbrainsKotlinStdlibJdk8)
    implementation(libs.jetbrainsKotlinReflect)
    compileOnly(libs.androidXDataBindingViewbinding)
    
    testImplementation("junit:junit:4.13.2")
    
    androidTestImplementation("androidx.test:core:1.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.3")
    androidTestImplementation("androidx.test.ext:truth:1.4.0")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    
    implementation(project(":lib_appbasic"))
}
