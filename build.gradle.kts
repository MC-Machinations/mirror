plugins {
    `java-library`
}

group = "me.machinemaker.mirror"
version = "0.2.0"

subprojects {
    apply(plugin="java-library")

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly(rootProject.libs.checker.qual)
        implementation(rootProject.libs.geantyref)

        testImplementation(rootProject.libs.junit.api)
        testRuntimeOnly(rootProject.libs.junit.engine)
    }

}

