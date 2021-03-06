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
            classpath "de.gematik:gematik-parent-plugin:1.2.1"
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
        developerEMail = "if not set is 'referenzimplementierung@gematik.de' default value"
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

## Getting Started

### Java Library/Application

**build.gradle.**

    buildscript {
        dependencies {
            classpath "de.gematik:gematik-parent-plugin:1.2.1"
            }
    }
    apply plugin: "de.gematik.publish"

### Android Library

**build.gradle.**

    buildscript {
        dependencies {
            classpath "de.gematik:gematik-parent-plugin:1.2.1"
            }
    }
    apply plugin: "de.gematik.android.publish"
