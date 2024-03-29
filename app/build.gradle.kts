plugins {
    id("app")
}

android {
    defaultConfig {
        buildConfigField("int", "LOG_METHOD_COUNT", "10")
    }
}

dependencies {
    implementation(libs.androidXCoreKtx)
    implementation(libs.androidXAppcompat)
    implementation(libs.material)
    implementation(libs.androidXLocalBroadcastLocalbroadcastmanager)
    implementation("androidx.multidex:multidex:2.0.1")
    
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")
    
    testImplementation("junit:junit:4.13.2")
    
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
    androidTestImplementation("androidx.test.ext:truth:1.4.0")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    
    implementation(project(":lib_appbasic"))
    implementation(project(":libapp"))
}
