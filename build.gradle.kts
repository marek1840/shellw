plugins {
    java
    kotlin("jvm") version "1.3.72"
}

version = "1.0-SNAPSHOT"

val javaVersion = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("junit", "junit", "4.12")
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    jar {
        archiveFileName.set("${rootProject.name}.jar")
        from(configurations.compileClasspath.map { config -> config.map { if (it.isDirectory) it else zipTree(it) } })
    }
}
