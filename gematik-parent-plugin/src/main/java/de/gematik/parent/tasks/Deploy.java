/*
 * Copyright (c) 2021 gematik GmbH
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
package de.gematik.parent.tasks;

import org.gradle.api.tasks.TaskAction;

public class Deploy extends AbstractGematikTask {

    public Deploy() {
        setGroup("Gematik");
        setDescription("Build the Project and publish artefacts to mavenlocal and deploy to remote repository with 'publish'.");
        dependsOn(getProject().getTasks().findByName("build"), getProject().getTasks().findByName("publishToMavenLocal"));
        finalizedBy(getProject().getTasks().findByName("publish"));
    }

    @TaskAction
    void deploy() {
        printTaskInfo();
    }

    private void printTaskInfo() {
        printTextBlockBorder();
        getLogger().quiet(getLineForString(" Gematik local Build with UPLOAD to remote Repository "));
        getLogger().quiet(getLineForString(""));
        getLogger().quiet(getLineForString("   Build: publishToMavenLocal => publish      "));
        printTextBlockBorder();
    }

}
