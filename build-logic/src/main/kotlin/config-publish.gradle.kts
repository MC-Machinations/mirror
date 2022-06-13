plugins {
    `maven-publish`
    signing
}

val isSnapshot = version().endsWith("-SNAPSHOT")

publishing {

    publications {
        register<MavenPublication>("maven") {
            standardConfig(version())
        }
    }

    repositories {
        val url = if (isSnapshot) {
            "https://oss.sonatype.org/content/repositories/snapshots"
        } else {
            "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
        }
        maven(url) {
            name = "ossrh"
            credentials(PasswordCredentials::class)
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["maven"])
}

tasks {
    withType<PublishToMavenRepository> {
        dependsOn(check)
    }
}

fun MavenPublication.standardConfig(versionName: String) {
    group = project.group
    artifactId = project.name
    version = versionName

    from(components["java"])

    withoutBuildIdentifier()
    pom {
        standard()
    }

}

fun MavenPom.standard() {
    val repoPath = "Machine-Maker/stuff"
    val repoUrl = "https://github.com/$repoPath"

    name.set(project.name)
    description.set("Reflection utilities")
    url.set(repoUrl)
    inceptionYear.set("2022")
    packaging = "jar"

    licenses {
        license {
            name.set("GNU General Public License Version 3.0")
            url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
            distribution.set("repo")
        }
    }

    issueManagement {
        system.set("GitHub")
        url.set("$repoUrl/issues")
    }

    developers {
        developer {
            id.set("Machine_Maker")
            name.set("Jake Potrebic")
            email.set("machine@machinemaker.me")
            url.set("https://github.com/Machine-Maker")
        }
    }

    scm {
        url.set(repoUrl)
        connection.set("scm:git:$repoUrl.git")
        developerConnection.set("scm:git:git@github.com:$repoPath.git")
    }
}

fun version(): String {
    return project.version.toString()
}
