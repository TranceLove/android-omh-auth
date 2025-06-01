import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoReportBase
import org.gradle.testing.jacoco.tasks.JacocoReportsContainer

private const val jacocoTaskGroup = "verification"

fun Project.setupJacoco() {
    tasks.withType<Test> {
        configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }

    tasks.register<JacocoReport>("jacocoTestReport") {
        group = jacocoTaskGroup
        description = "Code coverage report for both Android and Unit tests."
        dependsOn(
            tasks.getByName("testDebugUnitTest"),
            tasks.getByName("syncDebugLibJars"),
            tasks.getByName("mergeDebugJavaResource"),
            tasks.getByName("copyDebugJniLibsProjectAndLocalJars"),
            tasks.getByName("copyDebugJniLibsProjectOnly")
        )
        reports.setUp(this@setupJacoco)
        setDirs(this@setupJacoco)
    }

    val minimumCoverageAllowed = properties.get("minimunCoverageAllowed").toString().toBigDecimal()
    tasks.register<JacocoCoverageVerification>("jacocoCoverageVerification") {
        group = jacocoTaskGroup
        description = "Code coverage verification for Android both Android and Unit tests."
        dependsOn(
            tasks.getByName("testDebugUnitTest"),
            tasks.getByName("syncDebugLibJars"),
            tasks.getByName("copyDebugJniLibsProjectAndLocalJars"),
            tasks.getByName("copyDebugJniLibsProjectOnly")
        )
        violationRules {
            rule {
                limit {
                    minimum = minimumCoverageAllowed
                }
            }
            rule {
                element = "CLASS"
                excludes = listOf(
                    "**.FactorFacade.Builder",
                    "**.ServiceFacade.Builder",
                    "**.ChallengeFacade.Builder",
                    "**.Task",
                )
                limit {
                    minimum = minimumCoverageAllowed
                }
            }
        }
        setDirs(this@setupJacoco)
    }
}

fun JacocoReportsContainer.setUp(project: Project) {
    csv.required.set(false)
    xml.apply {
        required.set(true)
        outputLocation.set(project.file("${project.layout.buildDirectory}/reports/code-coverage/xml"))
    }
    html.apply {
        required.set(true)
        outputLocation.set(project.file("${project.layout.buildDirectory}/reports/code-coverage/html"))
    }
}


private fun JacocoReportBase.setDirs(project: Project) {
    val classDirectoriesTree = project.fileTree("${project.layout.buildDirectory}") {
        include(
            "**/classes/**/main/**",
            "**/intermediates/classes/debug/**",
            "**/intermediates/javac/debug/*/classes/**", // Android Gradle Plugin 3.2.x support.
            "**/tmp/kotlin-classes/debug/**"
        )
        exclude(
            "**/R.class",
            "**/R\$*.class",
            "**/*\$1*",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            "android/**/*.*",
            "**/models/**",
            "**/*\$Lambda$*.*",
            "**/*\$inlined$*.*",
            "**/presentation/**",
            "**/data/**/datasource/**",
            "**/data/**/models/**",
            "**/utils/**",
            "**/factories/**",
            "**/*$*",
        )
    }

    val sourceDirectoriesTree = project.files("${project.projectDir}/src/main/java")

    val executionDataTree = project.fileTree("${project.layout.buildDirectory}") {
        include(
            "outputs/code_coverage/**/*.ec",
            "jacoco/jacocoTestReportDebug.exec",
            "jacoco/testDebugUnitTest.exec",
            "jacoco/test.exec"
        )
    }
    sourceDirectories.setFrom(sourceDirectoriesTree)
    classDirectories.setFrom(classDirectoriesTree)
    executionData.setFrom(executionDataTree)
}
