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

import de.gematik.parent.PublishPluginExtension
import de.gematik.parent.tasks.VersionSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.plugins.signing.SigningPlugin

abstract class AbstractPublishPlugin implements Plugin<Project> {

    private Project project;

    protected void addPom(root) {
        root.children().last() + {
            resolveStrategy = Closure.DELEGATE_FIRST

            description getPublishPluginExtension().description
            name getPublishPluginExtension().name
            url 'https://github.com/gematik/' + getPublishPluginExtension().gitHubProjectName
            organization {
                name 'de.gematik'
                url 'https://github.com/gematik'
            }
            issueManagement {
                system 'GitHub'
                url 'https://github.com/gematik/' + getPublishPluginExtension().gitHubProjectName + '/issues'
            }
            licenses {
                license {
                    name 'Apache License 2.0'
                    url 'https://www.apache.org/licenses/LICENSE-2.0.txt'
                }
            }
            scm {
                url 'https://github.com/gematik/' + getPublishPluginExtension().gitHubProjectName
                connection 'scm:git:git://github.com/gematik/' + getPublishPluginExtension().gitHubProjectName + '.git'
                developerConnection 'scm:git:ssh://git@github.com:gematik/' + getPublishPluginExtension().gitHubProjectName + '.git'
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

    protected void addPlugins(Project project) {
        this.project = project;
        project.getPlugins().apply(SigningPlugin.class);
        project.getPlugins().apply(MavenPublishPlugin.class);

        if (project.extensions.findByType(PublishPluginExtension) == null)
            project.extensions.create('gematikPublish', PublishPluginExtension)

        if (project.tasks.findByName("versionset") != null)
            project.getTasks().each { t ->
                if (!(t instanceof VersionSet) && !t.getName().equals("createPublishTarget")) {
                    t.dependsOn(project.tasks.getByName("versionset"));
                }
            };

        if (project.hasProperty('signing.secretKeyRingFile')) {
            project.signing {
                sign project.configurations.archives
            }
        }
    }

    protected String getDeveloperEMail() {
        getPublishPluginExtension().developerEMail != null ? getPublishPluginExtension().developerEMail : 'software-development@gematik.de'
    }

    protected PublishPluginExtension getPublishPluginExtension() {
        this.project.extensions.findByType(PublishPluginExtension)
    }


}
