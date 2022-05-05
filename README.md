# Gematik Gradle Plugins

## Description

This Plugin adds functionality for Version handling with version.txt files and easy deployment.

## Version handling

The build.gradle file in your Project don’t need version information. The Plugin loads the Versionnumber at build time from version.txt. If the file does’t
exists, creates the plugin a new one with initial version 1.0.0-SNAPSHOT. To change or set Version use the Task 'versionset':

**Change the Version.**

    #> gradle versionset --value=a.b.c-SNAPSHOT

## Deployment

The Plugin provides the deploy task. This Task execute build, publishToMavenLocal and uploadArchives

## Getting Started

**build.gradle.**

    buildscript {
        dependencies {
            classpath "de.gematik:gematik-parent-plugin:1.3.2"
            }
    }
    apply plugin: "de.gematik.parent"

# PublishPlugin

## Description

This Plugin prepare the project for signing and gitHub Publishing

## Configuration

The Configuration includes the gitHub necessary data to generate the scm, organization, issueManagement blocks and the
general data about you project.

**Publish configuration.**

    gematikPublish {
        name = "Gematik Projectname"
        description = "Information about the project"
        gitHubProjectName = "name of gematik githubproject without .git"
        developerEMail = "email for developer contact"
    }

## Signing Artefacts

The Plugin search for tasks that provide artefacts. All with the following names would used:

-   sourceJar

-   testSourceJar

-   adocJar

-   javadocJar

-   groovydocJar

-   androidTestSourceJar

-   additionalArtefact1

-   additionalArtefact2

-   …​

-   additionalArtefact99

**Example Artefact-Task.**

    task sourceJar(type: Jar) {
        archiveClassifier.set("sources")
        from "${project.buildDir}/../src/main/java/"
        include "**/*.java"
    }

## Environment

The gradle.properties or Environment need the following Variables. If you not publish artefakts set empty String

**Publish configuration.**

    #Username for publish Neuxs
    ORG_GRADLE_PROJECT_SERVER_NEXUS_USERNAME=""
    #Password for publish Neuxs
    ORG_GRADLE_PROJECT_SERVER_NEXUS_PASSWORD=""
    #Nexus URL to publish Snapshots
    SNAPSHOT_REPOSITORY=""
    #Nexus URL to publish Releases
    RELEASE_REPOSITORY=""

## Getting Started

### Java Library/Application

**build.gradle.**

    buildscript {
        dependencies {
            classpath "de.gematik:gematik-parent-plugin:1.3.2"
            }
    }
    apply plugin: "de.gematik.publish"

### Android Library

**build.gradle.**

    buildscript {
        dependencies {
            classpath "de.gematik:gematik-parent-plugin:1.3.2"
            }
    }
    apply plugin: "de.gematik.android.publish"
