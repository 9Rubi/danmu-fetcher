import tanvd.kosogor.proxy.*

val ktorVersion: String by project
val kotlinVersion: String by project

plugins {
    kotlin("jvm") apply true
    id("tanvd.kosogor") version "1.0.7" apply true
}

dependencies {
    compile("io.ktor:ktor-client-logging-jvm:$ktorVersion")
    compile("io.ktor:ktor-client-cio:$ktorVersion")
    compile("io.ktor:ktor-client-json:$ktorVersion")
    compile("io.ktor:ktor-client-gson:$ktorVersion")
}

publishJar{
    publication{
        artifactId = "danmu-fetcher"
    }
    bintray {
//        username = project.properties["bintrayUser"]?.toString() ?: System.getenv("BINTRAY_USER")
//        secretKey = project.properties["bintrayApiKey"]?.toString() ?: System.getenv("BINTRAY_API_KEY")
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