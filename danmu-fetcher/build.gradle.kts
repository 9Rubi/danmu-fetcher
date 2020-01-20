import tanvd.kosogor.proxy.*

val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project

plugins {
    kotlin("jvm") apply true
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api("io.ktor:ktor-client-core:$ktor_version")
    api("io.ktor:ktor-client-core-jvm:$ktor_version")
    api("io.ktor:ktor-client-json-jvm:$ktor_version")
    api("io.ktor:ktor-client-jackson:$ktor_version")
    api("io.ktor:ktor-client-cio:$ktor_version")
    api("io.ktor:ktor-websockets:$ktor_version")
    api("io.ktor:ktor-client-websockets:$ktor_version")
    api("io.ktor:ktor-client-logging-jvm:$ktor_version")
}

publishJar{
    publication{
        artifactId = "danmu-fetcher"
    }
    bintray {
        repository = "for-fun"
        info {
            publish = false
            githubRepo = "https://github.com/9Rubi/danmu-fetcher.git"
            vcsUrl = "https://github.com/9Rubi/danmu-fetcher.git"
            userOrg = "9rubi"
            license = "MIT"
        }
    }
}