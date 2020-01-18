import tanvd.kosogor.proxy.publishJar
import tanvd.kosogor.proxy.shadowJar

val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project

plugins {
    kotlin("jvm") version "1.3.61" apply true
    id("tanvd.kosogor") version "1.0.6" apply true
}

group = "ink.rubi"
version = "0.0.1"

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    api("io.ktor:ktor-client-core:$ktor_version")
    api("io.ktor:ktor-client-core-jvm:$ktor_version")
    api("io.ktor:ktor-client-json-jvm:$ktor_version")
    api("io.ktor:ktor-client-jackson:$ktor_version")
    api("io.ktor:ktor-client-cio:$ktor_version")
    api("io.ktor:ktor-websockets:$ktor_version")
    api("io.ktor:ktor-client-websockets:$ktor_version")
    api("io.ktor:ktor-client-logging-jvm:$ktor_version")
    api("ch.qos.logback:logback-classic:$logback_version")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

publishJar{
    publication{
        artifactId = rootProject.name
    }
}