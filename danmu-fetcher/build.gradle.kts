import tanvd.kosogor.proxy.*

val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project

plugins {
    kotlin("jvm") apply true
}

dependencies {
    api("io.ktor:ktor-client-jackson:$ktor_version")
    api("io.ktor:ktor-client-logging-jvm:$ktor_version")
    api("io.ktor:ktor-client-cio:$ktor_version")
    api("io.ktor:ktor-client-websockets:$ktor_version")
}

publishJar{
    publication{
        artifactId = "danmu-fetcher"
    }
    bintray {
        username = project.properties["bintrayUser"]?.toString() ?: System.getenv("BINTRAY_USER")
        secretKey = project.properties["bintrayApiKey"]?.toString() ?: System.getenv("BINTRAY_API_KEY")
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