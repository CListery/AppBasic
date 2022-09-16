
pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

files("../gradle/libs.versions.toml").also { catalogVersionsFile->
    if (true == catalogVersionsFile.singleOrNull()?.exists()) {
        enableFeaturePreview("VERSION_CATALOGS")
        dependencyResolutionManagement {
            versionCatalogs {
                create("buildSrcLibs") {
                    from(catalogVersionsFile)
                }
            }
        }
    }
}