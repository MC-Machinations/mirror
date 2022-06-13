import java.nio.charset.StandardCharsets

plugins {
    idea
    `java-library`
    checkstyle
    id("com.github.hierynomus.license")
}

checkstyle {
    configDirectory.set(rootProject.file(".checkstyle"))
    isShowViolations = true
    toolVersion = "10.3"
}

license {
    header = rootProject.file("HEADER")
    mapping("java", "SLASHSTAR_STYLE")
}


java {
    withSourcesJar()
    withJavadocJar()
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}


tasks {
    test {
        useJUnitPlatform()
    }

    processResources {
        charset(StandardCharsets.UTF_8.name())
    }

    javadoc {
        charset(StandardCharsets.UTF_8.name())
    }

    compileJava {
        charset(StandardCharsets.UTF_8.name())
    }

    withType<Jar> {
        from(rootProject.projectDir) {
            into("META-INF")
            include("LICENSE")
        }
    }
}

idea {
    module {
        isDownloadSources = true
    }
}
