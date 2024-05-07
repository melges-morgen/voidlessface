import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

plugins {
    kotlin("jvm") version "1.9.23"
    id("org.openapi.generator") version "7.5.0"
}

group = "ru.openklub"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktor_version: String by project

dependencies {
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.getByName("compileKotlin").dependsOn(tasks.getByName("openApiGenerate"))
sourceSets.named("main").configure {
    this.extensions.getByName<SourceDirectorySet>("kotlin")
        .srcDirs("${project.layout.buildDirectory.get()}/generated/src/main/kotlin")
}

openApiGenerate {
    generatorName.set("kotlin")
    inputSpec.set("$rootDir/specs/petstore.yaml")
    outputDir.set("${project.layout.buildDirectory.get()}/generated")
    apiPackage.set("ru.openklub.api")
    invokerPackage.set("ru.openklub.invoker")
    modelPackage.set("ru.openklub.model")
    cleanupOutput.set(true)
    globalProperties.set(
        mapOf(
            "apis" to "",
            "models" to "",
            "supportingFiles" to "ApiKeyAuth.kt,Authentication.kt,HttpBasicAuth.kt,HttpBearerAuth.kt,OAuth.kt,ApiAbstractions.kt,ApiClient.kt,Bytes.kt,HttpResponse.kt,OctetByteArray.kt,PartConfig.kt,RequestConfig.kt,RequestMethod.kt")
    )
    configOptions.set(
        mapOf(
            "useSettingsGradle" to "false",
            "omitGradleWrapper" to "true",
            "omitGradlePluginVersions" to "true",
            "sourceFolder" to "/src/main/kotlin",
            "library" to "multiplatform",
            "dateLibrary" to "kotlinx-datetime"
        )
    )
}