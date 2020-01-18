import tanvd.kosogor.proxy.publishJar
import tanvd.kosogor.proxy.shadowJar

val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project
val apache_commons_lang3: String by project


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
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    compile("ch.qos.logback:logback-classic:$logback_version")
    compile("io.ktor:ktor-client-core:$ktor_version")
    compile("io.ktor:ktor-client-core-jvm:$ktor_version")
    compile("io.ktor:ktor-client-jetty:$ktor_version")
    compile("io.ktor:ktor-client-json-jvm:$ktor_version")
    compile("io.ktor:ktor-client-jackson:$ktor_version")
    compile("io.ktor:ktor-client-cio:$ktor_version")
    compile("io.ktor:ktor-websockets:$ktor_version")
    compile("io.ktor:ktor-client-websockets:$ktor_version")
    compile("io.ktor:ktor-client-logging-jvm:$ktor_version")
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
shadowJar {
    jar {
        archiveName = "test.jar"
//        mainClass = "tanvd.example.MainKt"
    }
}