import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    kotlin("jvm") version "1.4.10"
    id("org.openjfx.javafxplugin") version "0.0.9"
}

group = "online.ruin_of_future"
version = "1.0-SNAPSHOT"

repositories {
    maven(url = "https://maven.aliyun.com/repository/public")
    mavenLocal()
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("no.tornado:tornadofx:1.7.20")
    implementation("com.beust:klaxon:5.0.1")
    implementation("org.jsoup:jsoup:1.13.1")
    testCompile("junit", "junit", "4.12")
}

javafx {
    version = "12"
    modules("javafx.controls")
}

application {
    mainClassName = "online.ruin_of_future.bnu_lib_reserver.MainKt"
}

// compile bytecode to java 11 (default is java 6)
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
