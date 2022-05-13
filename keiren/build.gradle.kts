kotlin {
    explicitApi()
}

dependencies {
    implementation(project(":common"))

    /* kotlin */
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")

    /* networking */
    val ktor = "2.0.1"

    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor")

    // client
    implementation("io.ktor:ktor-client-core:$ktor")
    implementation("io.ktor:ktor-client-cio:$ktor")
    implementation("io.ktor:ktor-client-websockets:$ktor")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor")

    // server
    implementation("io.ktor:ktor-server-core:$ktor")

    /* logging */
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")
}
