
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