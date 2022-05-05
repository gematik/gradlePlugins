/*
 * Copyright (c) 2022 gematik GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.gematik.parent

import de.gematik.parent.tasks.VersionSet
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication

class PublishPlugin extends AbstractPublishPlugin {
    @Override
    String toString() {
        return super.toString()
    }

    @Override
    void apply(Project project) {

        addPlugins(project)
        if (project.hasProperty('signing.secretKeyRingFile')) {
            project.model {
                project.tasks.publishMavenJavaPublicationToMavenLocal {
                    dependsOn project.tasks.signArchives
                }
            }
            project.signing {
                sign project.publishing.publications.mavenJava
            }
        }

        project.afterEvaluate { p ->
            project.publishing.repositories.maven {
                if (project.hasProperty("SERVER_NEXUS_USERNAME") && project.hasProperty("SERVER_NEXUS_PASSWORD"))
                {
                    name = 'Nexus'
                    credentials {
                        username project.getProperty("SERVER_NEXUS_USERNAME")
                        password project.getProperty("SERVER_NEXUS_PASSWORD")
                    }
                    if(project.version.endsWith('-SNAPSHOT')) {
                        url project.getProperty("SNAPSHOT_REPOSITORY")
                    } else {
                        url project.getProperty("RELEASE_REPOSITORY")
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
                            addPom(root)
                        }

                    }
                }
            }
        }
    }


}
