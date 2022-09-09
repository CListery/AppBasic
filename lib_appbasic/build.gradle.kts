plugins {
    id("lib")
    id("kre-publish")
    id("org.jetbrains.dokka")
}

android {
    defaultConfig {
        buildConfigField("int", "LOG_METHOD_COUNT", "2")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}