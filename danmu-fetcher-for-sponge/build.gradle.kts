import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
plugins {
    kotlin("jvm") apply true
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "ink.rubi"
version = "0.1.0"

repositories {
    jcenter()
    mavenCentral()
    maven{
        url = uri("https://repo.spongepowered.org/maven")
    }
}

dependencies {
    compile(project(":danmu-fetcher")){
        exclude("org.slf4j","slf4j-api")
        exclude("com.google.code.gson","gson")
    }
    compile(project(":danmu-fetcher-handler"))
    compileOnly("org.spongepowered:spongeapi:7.1.0")
    compileOnly("org.slf4j:slf4j-api:1.7.26")
    api("io.ktor:ktor-client-logging-jvm:1.3.1")
}
tasks.withType<ShadowJar>{
    relocate("io.ktor","ink.rubi.project.lib.ktor")
}
