val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "3.0.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
    id("org.openapi.generator") version "7.8.0"
    id("app.cash.sqldelight") version "2.0.2"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")

    // include openApi generated sources
    sourceSets {
        main {
            java {
                srcDir("${layout.buildDirectory.asFile.get()}/generated/api")
            }
        }
    }
}
// task to copy openApi document to project resources
tasks.register<Copy>("copyOpenApiDocument") {
    from("../klink-api/api.yaml")
    into("$rootDir/src/main/resources")
}
// execute `copyOpenApiDocument` before running generator
tasks.named("openApiGenerate") {
    dependsOn("copyOpenApiDocument")
}
// process project resources after api is copied
tasks.named("processResources") {
    dependsOn("copyOpenApiDocument")
}
// compile code after generating sources from api document
tasks.named("compileKotlin") {
    dependsOn("openApiGenerate")
}

// == Plugin configs ==
// openApi generator config
openApiGenerate {
    generatorName.set("kotlin")
    // generate only models, no api stubs
    globalProperties.put("models", "")
    globalProperties.put("modelTests", "false")

    inputSpec.set("$rootDir/src/main/resources/api.yaml")
    outputDir.set("${layout.buildDirectory.asFile.get()}/generated/api")

    configOptions.put("serializationLibrary", "kotlinx_serialization")
    modelNameSuffix.set("ApiDto")
}
// sqldelight
sqldelight {
    databases {
        create("KlinkDatabase") {
            packageName.set("com.example")
            dialect("app.cash.sqldelight:postgresql-dialect:2.0.2")
        }
    }
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-websockets-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-cors-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    // project dependencies
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("com.zaxxer:HikariCP:6.0.0")
    implementation("app.cash.sqldelight:jdbc-driver:2.0.2")
    implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")

    val koinVersion = "4.0.0"
    implementation(project.dependencies.platform("io.insert-koin:koin-bom:$koinVersion"))
    implementation("io.insert-koin:koin-core")// Koin for Ktor
    implementation("io.insert-koin:koin-ktor")
    implementation("io.insert-koin:koin-logger-slf4j")

    val arrowVersion = "1.2.4"
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrowVersion")

    // pgjdbc
    implementation("com.impossibl.pgjdbc-ng:pgjdbc-ng:0.8.9")
}
