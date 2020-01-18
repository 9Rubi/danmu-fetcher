import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project
val apache_commons_lang3: String by project


plugins {
    `maven-publish`
    kotlin("jvm") version "1.3.61"
    id("com.github.johnrengelman.shadow") version "5.0.0"
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

val shadowJar: ShadowJar by tasks
/*
tasks.withType<ShadowJar>{
    manifest.attributes.apply {
        put("Implementation-Title", "just for practise")
        put("Implementation-Version", archiveVersion.get())
        put("Main-Class", "Test")
    }
}
*/

shadowJar.apply {
    manifest.attributes.apply {
        put("Implementation-Title", "just for practise")
        put("Implementation-Version", archiveVersion.get())
        put("Main-Class", "Test")
    }
    archiveBaseName.set(project.name + "-shadow")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}
val allInOne by tasks.registering(ShadowJar::class) {
    archiveClassifier.set("all")
    from(shadowJar)
}
publishing {
    repositories {
        maven {
            url = uri("$buildDir/repo")
        }
    }
    publications {
        register("mavenJava", MavenPublication::class) {
//            dependsOn(":shadowJar")
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(allInOne.get())
        }
    }
}