plugins {
    id("java-library")
}

dependencies {
    implementation(project(":common"))
    implementation("org.slf4j:slf4j-api:2.+")
    implementation("io.vertx:vertx-web:5.+")
    runtimeOnly("org.slf4j:slf4j-simple:2.+")

    testImplementation("io.vertx:vertx-junit5:5.+")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}