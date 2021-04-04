import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.serialization") version "1.4.30"
    application
}

group = "me.luamoroso"
version = "1.0-SNAPSHOT"

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        useIR = true
    }
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")

    //arrow
    implementation("io.arrow-kt:arrow-fx-coroutines:0.13.1")

    //ktorm
    implementation("org.ktorm:ktorm-core:3.3.0")

    //mysql
    implementation("mysql:mysql-connector-java:8.0.23")

    //hikari
    implementation("com.zaxxer:HikariCP:4.0.3")

    //ktor
    implementation("io.ktor:ktor-server-core:1.5.3")
    implementation("io.ktor:ktor-server-netty:1.5.3")
    implementation("io.ktor:ktor-serialization:1.5.3")

    //guava
    implementation ("com.google.guava:guava:30.1.1-jre")



    //Loging
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.6")
    implementation("org.slf4j:slf4j-api:1.7.5")
    implementation("ch.qos.logback:logback-classic:1.0.13")
    implementation("com.amazonaws:aws-xray-recorder-sdk-slf4j:2.4.0")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("AppKt")
}