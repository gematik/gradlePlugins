/*
 * Copyright (c) 2019 gematik GmbH
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
package de.gematik.parent;

import java.util.Map.Entry;
import java.util.Set;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.MavenPlugin;
import org.gradle.api.plugins.quality.CheckstylePlugin;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

import de.gematik.parent.tasks.Deploy;
import de.gematik.parent.tasks.VersionSet;

/**
 * Plugin to add needed Plugins for Company projects
 */
public class GematikParentPlugin implements Plugin<Project> {
    private static final String CREATE_PUBLISH_TARGET = "createPublishTarget";

    /**
     * If not an Android-Project automatic add Java-Plugin
     * Additionally add {@link MavenPlugin}, {@link MavenPublishPlugin}, {@link CheckstylePlugin}, nu.studer.credentials Plugin, jacoco, sonarqube
     * 
     * @param project
     */
    @Override
    public void apply(final Project project) {
        if (!project.getPlugins().hasPlugin("com.android.application") && !project.getPlugins().hasPlugin("com.android.library")
                && !project.getPlugins().hasPlugin("java-library") && !project.getPlugins().hasPlugin("com.android.feature")
                && !project.getPlugins().hasPlugin("com.android.instantapp")) {
            project.getPlugins().apply(JavaPlugin.class);
        }
        project.getPlugins().apply(MavenPlugin.class);
        project.getPlugins().apply(MavenPublishPlugin.class);
        project.getPlugins().apply(CheckstylePlugin.class);
        project.getPlugins().apply("nu.studer.credentials");
        if (!project.getPlugins().hasPlugin("jacoco-android") && !project.getPlugins().hasPlugin("jacoco")) {
            project.getPlugins().apply("jacoco");
        }
        if (!project.getPlugins().hasPlugin("org.sonarqube") && !project.getPlugins().hasPlugin("sonarqube")
                && !project.getRootProject().getPlugins().hasPlugin("org.sonarqube") && !project.getRootProject().getPlugins().hasPlugin("sonarqube")) {
            project.getPlugins().apply("org.sonarqube");
        }

        configurePublishingExtension(project);

        final VersionSet versionSet = project.getTasks().create("versionset", VersionSet.class);
        versionSet.dependsOn(CREATE_PUBLISH_TARGET);
        project.getTasks().create("deploy", Deploy.class);
        setVersionForAllTasks(project, versionSet);
    }

    private void setVersionForAllTasks(final Project project, final VersionSet versionSet) {
        project.getTasks().all(t -> {
            if (!(t instanceof VersionSet) && !t.getName().equals(CREATE_PUBLISH_TARGET)) {
                t.dependsOn(versionSet);
            }
        });
    }

    private void configurePublishingExtension(final Project project) {
        final PublishingExtension publishingExtension = project.getExtensions().getByType(PublishingExtension.class);

        final Set<Entry<String, SoftwareComponent>> entrySet = project.getComponents().getAsMap().entrySet();
        if (!entrySet.isEmpty()) {
            final MavenPublication publication = publishingExtension.getPublications().create("mavenJava", MavenPublication.class);
            final SoftwareComponent value = entrySet.iterator().next().getValue();
            publication.from(value);
        }
    }

}
