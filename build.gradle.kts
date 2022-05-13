import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.6.21" apply false
    kotlin("plugin.serialization") version "1.6.21" apply false
}

application {
    mainClass.set("dimensional.makoia.bot.MakoiaLauncher")
}

dependencies {
    implementation(project(":common"))

    /* kotlin */
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")

    /* ktor */
    implementation("io.ktor:ktor-server-core:2.0.1")
    implementation("io.ktor:ktor-server-cio:2.0.1")
    implementation("io.ktor:ktor-server-content-negotiation:2.0.1")

    /* twitch */
    implementation(project(":keiren"))

    /* discord */
    implementation("dev.kord:kord-core:0.8.x-SNAPSHOT")
    implementation("com.kotlindiscord.kord.extensions:kord-extensions:1.5.3-20220505.185229-7")

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

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.dimensional.fun/releases")
        maven("https://maven.kotlindiscord.com/repository/maven-public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    apply(plugin = "kotlin")
    apply(plugin = "kotlinx-serialization")

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "16"
            freeCompilerArgs = listOf(
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xopt-in=kotlin.contracts.ExperimentalContracts"
            )
        }
    }
}
