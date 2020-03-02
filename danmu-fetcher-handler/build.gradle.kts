import tanvd.kosogor.proxy.publishJar

plugins {
    kotlin("jvm") apply true
    id("tanvd.kosogor") version "1.0.7" apply true
}
repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":danmu-fetcher"))
}


publishJar{
    publication{
        artifactId = "danmu-fetcher-handler"
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
