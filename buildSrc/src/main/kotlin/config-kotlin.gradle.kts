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
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}


tasks {
    test {
        useJUnitPlatform()
    }

    processResources {
        filteringCharset = StandardCharsets.UTF_8.name()
    }

    javadoc {
        options {
            encoding = StandardCharsets.UTF_8.name()
            (this as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
        }
    }

    compileJava {
        options.encoding = StandardCharsets.UTF_8.name()
    }

    withType<Jar> {
        from(rootProject.projectDir) {
            into("META-INF")
            include("LICENSE")
        }
    }

    withType<PublishToMavenRepository> {
        dependsOn(check)
    }
}

idea {
    module {
        isDownloadSources = true
    }
}
