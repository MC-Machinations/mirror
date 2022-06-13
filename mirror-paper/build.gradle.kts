plugins {
    id("config-kotlin")
    id("config-publish")
}

description = "Paper-specific reflection utilities"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(project(":mirror-core"))
    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")
}
