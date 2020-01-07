/*
 * Copyright (c) 2020 gematik GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.gematik.parent

import de.gematik.parent.tasks.VersionSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.maven.MavenDeployment
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.plugins.signing.SigningPlugin

class PublishPlugin implements Plugin<Project> {
    @Override
    String toString() {
        return super.toString()
    }

    @Override
    void apply(Project project) {

        project.getPlugins().apply(SigningPlugin.class);
        project.getPlugins().apply(MavenPublishPlugin.class);
        project.getPlugins().apply(MavenPlugin.class);

        if (project.extensions.findByType(PublishPluginExtension) == null)
            project.extensions.create('gematikPublish', PublishPluginExtension)

        if (project.tasks.findByName("versionset") != null)
            project.getTasks().each { t ->
                if (!(t instanceof VersionSet) && !t.getName().equals("createPublishTarget")) {
                    t.dependsOn(project.tasks.getByName("versionset"));
                }
            };
        def gematikPublish = project.extensions.findByType(PublishPluginExtension)
        def developerEMail = gematikPublish.developerEMail != null ? gematikPublish.developerEMail : 'referenzimplementierung@gematik.de'

        if (project.hasProperty('signing.secretKeyRingFile')) {
            project.signing {
                sign project.configurations.archives
            }
            project.model {
                project.tasks.generatePomFileForMavenJavaPublication {
                    destination = project.file("$buildDir/generated-pom.xml")
                }
                project.tasks.publishMavenJavaPublicationToMavenLocal {
                    dependsOn project.tasks.signArchives
                }
            }
        }

        if (project.hasProperty('signing.secretKeyRingFile')) {
            project.tasks.uploadArchives {
                repositories {
                    mavenDeployer {
                        pom.withXml {
                            def root = asNode()

                            // add all items necessary for maven central publication
                            root.children().last() + {
                                resolveStrategy = Closure.DELEGATE_FIRST

                                description gematikPublish.description
                                name gematikPublish.name
                                url 'https://github.com/gematik/' + gematikPublish.gitHubProjectName
                                organization {
                                    name 'de.gematik'
                                    url 'https://github.com/gematik'
                                }
                                issueManagement {
                                    system 'GitHub'
                                    url 'https://github.com/gematik/' + gematikPublish.gitHubProjectName + '/issues'
                                }
                                licenses {
                                    license {
                                        name 'Apache License 2.0'
                                        url 'https://www.apache.org/licenses/LICENSE-2.0.txt'
                                    }
                                }
                                scm {
                                    url 'https://github.com/gematik/' + gematikPublish.gitHubProjectName
                                    connection 'scm:git:git://github.com/gematik/' + gematikPublish.gitHubProjectName + '.git'
                                    developerConnection 'scm:git:ssh://git@github.com:gematik/' + gematikPublish.gitHubProjectName + '.git'
                                }
                                developers {
                                    developer {
                                        name 'gematik'
                                        email developerEMail
                                        url 'https://gematik.github.io/'
                                        organization 'gematik GmbH'
                                        organizationUrl 'https://www.gematik.de/'
                                    }
                                }
                            }
                        }
                        beforeDeployment { MavenDeployment deployment -> project.signing.signPom(deployment) }
                    }
                }
            }
        }

        project.afterEvaluate { p ->
            project.publishing.publications {
                mavenJava(MavenPublication) {
                    if (project.tasks.findByName("versionset") != null)
                        ((VersionSet) project.tasks.getByName("versionset")).versionSet()
                    println "Project-Version: " + project.version
                    if (project.tasks.findByName("sourceJar") != null) {
                        artifact project.tasks.getByName("sourceJar")
                    }
                    if (project.tasks.findByName("testSourceJar") != null) {
                        artifact project.tasks.getByName("testSourceJar")
                    }
                    if (project.tasks.findByName("adocJar") != null) {
                        artifact project.tasks.getByName("adocJar")
                    }
                    if (project.tasks.findByName("javadocJar") != null) {
                        artifact project.tasks.getByName("javadocJar")
                    }
                    if (project.tasks.findByName("groovydocJar") != null) {
                        artifact project.tasks.getByName("groovydocJar")
                    }
                    if (project.tasks.findByName("androidTestSourceJar") != null) {
                        artifact project.tasks.getByName("androidTestSourceJar")
                    }
                    if (project.tasks.findByName("apkRelease") != null) {
                        artifact project.tasks.getByName("apkRelease")
                    }
                    if (project.tasks.findByName("apkReleaseUnsigned") != null) {
                        artifact project.tasks.getByName("apkReleaseUnsigned")
                    }
                    if (project.tasks.findByName("aarRelease") != null) {
                        artifact project.tasks.getByName("aarRelease")
                    }
                    if (project.tasks.findByName("aarReleaseUnsigned") != null) {
                        artifact project.tasks.getByName("aarReleaseUnsigned")
                    }
                    for (int i = 1; i < 100; i++) {
                        if (project.tasks.findByName("additionalArtefact" + i) != null) {
                            artifact project.tasks.getByName("additionalArtefact" + i)
                        } else {
                            break;
                        }
                    }


                    if (project.hasProperty('signing.secretKeyRingFile')) {
                        pom.withXml {
                            def root = asNode()

                            // add all items necessary for maven central publication
                            root.children().last() + {
                                resolveStrategy = Closure.DELEGATE_FIRST

                                description gematikPublish.description
                                name gematikPublish.name
                                url 'https://github.com/gematik/' + gematikPublish.gitHubProjectName
                                organization {
                                    name 'de.gematik'
                                    url 'https://github.com/gematik'
                                }
                                issueManagement {
                                    system 'GitHub'
                                    url 'https://github.com/gematik/' + gematikPublish.gitHubProjectName + '/issues'
                                }
                                licenses {
                                    license {
                                        name 'Apache License 2.0'
                                        url 'https://www.apache.org/licenses/LICENSE-2.0.txt'
                                    }
                                }
                                scm {
                                    url 'https://github.com/gematik/' + gematikPublish.gitHubProjectName
                                    connection 'scm:git:git://github.com/gematik/' + gematikPublish.gitHubProjectName + '.git'
                                    developerConnection 'scm:git:ssh://git@github.com:gematik/' + gematikPublish.gitHubProjectName + '.git'
                                }
                                developers {
                                    developer {
                                        name 'gematik'
                                        email developerEMail
                                        url 'https://gematik.github.io/'
                                        organization 'gematik GmbH'
                                        organizationUrl 'https://www.gematik.de/'
                                    }
                                }
                            }
                        }

                        // create the signed artifacts
                        project.tasks.signArchives.signatureFiles.each {
                            artifact(it) {
                                def matcher = it.file =~ /-${project.version}-(.*)\.jar\.asc$/
                                if (matcher.find()) {
                                    classifier = matcher.group(1)
                                } else {
                                    classifier = null
                                }
                                extension = 'jar.asc'
                            }
                        }

                        // create the sign pom artifact
                        pom.withXml {
                            def pomFile = new File("${project.buildDir}/generated-pom.xml")
                            writeTo(pomFile)
                            def pomAscFile = project.signing.sign(pomFile).signatureFiles[0]
                            artifact(pomAscFile) {
                                classifier = null
                                extension = 'pom.asc'
                            }
                        }
                    }
                }
            }
        }
    }


}
