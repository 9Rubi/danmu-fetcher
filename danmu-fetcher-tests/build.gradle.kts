import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("jvm") apply true
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":danmu-fetcher"))
    implementation("ch.qos.logback:logback-classic:1.2.1")
}

//tasks.withType(Test::class.java) {
//    jvmArgs = listOf("-XX:MaxPermSize=256m")
//    testLogging {
//        events.addAll(listOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED))
//        showStandardStreams = true
//        exceptionFormat = TestExceptionFormat.FULL
//    }
//}