plugins {
    id("config-kotlin")
    id("config-publish")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(project(":mirror-core"))
    compileOnly(libs.paper)
}
