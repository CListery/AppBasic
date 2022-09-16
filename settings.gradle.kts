if (file("gradle/libs.versions.toml").exists()) {
    enableFeaturePreview("VERSION_CATALOGS")
}

rootProject.name = "AppBasic"
include(":lib_appbasic")
include(":libapp")
include(":app")
include(":versionControl")
