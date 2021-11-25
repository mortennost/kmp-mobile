rootProject.name = "kmp_workshop"

val buildAndroid = extra["org.gradle.project.buildAndroid"].toString().toBoolean()
if (buildAndroid) {
    include("app")
}

include("common")
