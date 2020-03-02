plugins {
    kotlin("jvm") apply true
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":danmu-fetcher"))
    implementation(project(":danmu-fetcher-handler"))
    api("com.google.code.gson:gson:2.8.0")
    implementation("ch.qos.logback:logback-classic:1.2.1")
    api("io.ktor:ktor-client-logging-jvm:1.3.1")
}

//tasks.withType(Test::class.java) {
//    jvmArgs = listOf("-XX:MaxPermSize=256m")
//    testLogging {
//        events.addAll(listOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED))
//        showStandardStreams = true
//        exceptionFormat = TestExceptionFormat.FULL
//    }
//}