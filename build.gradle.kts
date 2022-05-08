plugins {
    kotlin("jvm") version "1.6.21"
}

repositories {
    maven("https://maven.dimensional.fun/releases")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    mavenCentral()
}

dependencies {
    /* kotlin */
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")

    /* discord */
    implementation("dev.kord:kord-core:0.8.x-SNAPSHOT")

    /* networking */
    val ktor = "2.0.1"
    implementation("io.ktor:ktor-client-core:$ktor")
    implementation("io.ktor:ktor-client-cio:$ktor")
    implementation("io.ktor:ktor-client-websockets:$ktor")

    /* configuration */
    val hoplite = "2.1.2"
    implementation("com.sksamuel.hoplite:hoplite-core:$hoplite")
    implementation("com.sksamuel.hoplite:hoplite-toml:$hoplite")

    /* logging */
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")

    /* misc */
    implementation("io.insert-koin:koin-core:3.1.6")
    implementation("mixtape.oss:kyuso-jvm:1.0")
}
