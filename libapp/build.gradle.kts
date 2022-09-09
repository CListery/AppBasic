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
    implementation(project(":lib_appbasic"))
}
