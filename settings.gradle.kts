if (file("gradle/libs.versions.toml").exists()) {
    enableFeaturePreview("VERSION_CATALOGS")
}

pluginManagement {
    repositories {
        mavenCentral()
        google()
    }
}
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "AppBasic"
include(":lib_appbasic")
include(":libapp")
include(":app")
include(":versionControl")
