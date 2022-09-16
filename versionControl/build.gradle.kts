plugins {
    org.jetbrains.kotlin.jvm
    `version-catalog`
}

catalog {
    fun VersionCatalogBuilder.version(depTypes: DepTypes) {
        depTypes.version(this)
    }
    
    fun VersionCatalogBuilder.library(
        depTypes: DepTypes,
        artifact: String,
        alias: String? = null,
    ) {
        depTypes.library(this, artifact, alias)
    }
    
    fun VersionCatalogBuilder.plugin(depTypes: DepTypes, artifact: String) {
        depTypes.plugin(this, artifact)
    }
    versionCatalog {
        DepTypes.values().forEach {
            version(it)
        }
    }
    versionCatalog {
        plugin(DepTypes.JetbrainsKotlin, "kotlin-gradle-plugin")
        plugin(DepTypes.JetbrainsKotlinAndroid, "org.jetbrains.kotlin.android.gradle.plugin")
        plugin(DepTypes.AndroidBuildTool, "gradle")
        plugin(DepTypes.JfrogBuildInfo, "build-info-extractor-gradle")
        plugin(DepTypes.Dokka, "dokka-gradle-plugin")
    }
    versionCatalog {
        // GradlePlugins
        library(DepTypes.JetbrainsKotlin, "kotlin-gradle-plugin")
        library(DepTypes.JetbrainsKotlinAndroid, "org.jetbrains.kotlin.android.gradle.plugin")
        library(DepTypes.AndroidBuildTool, "gradle")
        library(DepTypes.JfrogBuildInfo, "build-info-extractor-gradle")
        library(DepTypes.Dokka, "dokka-gradle-plugin")
        // Kotlin
        library(DepTypes.JetbrainsKotlin, "kotlin-stdlib")
        library(DepTypes.JetbrainsKotlin, "kotlin-reflect")
        library(DepTypes.JetbrainsKotlin, "kotlin-stdlib-jdk8")
        // AndroidX
        library(DepTypes.AndroidXCore, "core-ktx")
        library(DepTypes.AndroidXAppcompat, "appcompat")
        library(DepTypes.AndroidXDataBinding, "viewbinding")
        library(DepTypes.AndroidXLifecycle, "lifecycle-livedata-ktx")
        library(DepTypes.AndroidXLifecycle, "lifecycle-viewmodel-ktx")
        library(DepTypes.AndroidXLocalBroadcast, "localbroadcastmanager")
        // GoogleMaterial
        library(DepTypes.Material, "material")
        // Squareup
        library(DepTypes.Squareup, "kotlinpoet")
        library(DepTypes.Squareup, "kotlinpoet-ksp")
    }
    versionCatalog {
        bundle("baseLibs", listOf(
            DepTypes.JetbrainsKotlin.alias("kotlin-stdlib"),
            DepTypes.AndroidXCore.alias("core-ktx"),
            DepTypes.AndroidXAppcompat.alias("appcompat"),
        ))
    }
}

@Suppress("EnumEntryName")
internal enum class DepTypes(private val groupName: String, private val versionName: String) {
    JetbrainsKotlin("org.jetbrains.kotlin", "1.7.10"),
    JetbrainsKotlinAndroid("org.jetbrains.kotlin.android", "1.7.10"),
    AndroidBuildTool("com.android.tools.build", "7.2.2"),
    JfrogBuildInfo("org.jfrog.buildinfo", "4.29.0"),
    Dokka("org.jetbrains.dokka", "1.7.10"),
    AndroidXCore("androidx.core", "1.3.1"),
    AndroidXAppcompat("androidx.appcompat", "1.2.0"),
    AndroidXDataBinding("androidx.databinding", "4.2.1"),
    AndroidXLifecycle("androidx.lifecycle", "2.3.1"),
    AndroidXLocalBroadcast("androidx.localbroadcastmanager", "1.0.0"),
    Material("com.google.android.material", "1.2.1"),
    Squareup("com.squareup", "1.12.0"),
    ;
    
    private val versionAlias = name.decapitalize().substringBefore("_")
    
    fun library(
        builder: VersionCatalogBuilder,
        artifact: String,
        alias: String?,
    ) {
        builder.library(alias ?: alias(artifact), groupName, artifact).versionRef(versionAlias)
    }
    
    fun version(builder: VersionCatalogBuilder) {
        builder.version(versionAlias, versionName)
    }
    
    fun plugin(builder: VersionCatalogBuilder, artifact: String) {
        builder.plugin(alias(artifact), "$groupName:$artifact").versionRef(versionAlias)
    }
    
    fun alias(artifact: String): String {
        val aliasPrefix = name.decapitalize().substringBefore("_")
        val aliasSuffix = artifact.replace(groupName, "")
            .removeSuffix("plugin")
            .replace(".", "-")
            .split("-")
            .filter { it.isNotBlank() && !aliasPrefix.contains(it, true) }
            .joinToString("") { it.capitalize() }
        return aliasPrefix + aliasSuffix
    }
}

task<Copy>("publishCatalogToGradle") {
    group = "publishing"
    from(tasks.generateCatalogAsToml)
    into("../gradle")
}
