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
package de.gematik.parent.tasks;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

public class VersionSet extends AbstractGematikTask {

    private static final String VERSION_TXT = "version.txt";
    private static final String INIT_VERSION = "1.0.0-SNAPSHOT";

    @Input
    String versionToSet = INIT_VERSION;

    public VersionSet() {
        setGroup("Gematik");
        setDescription("Load the project version from " + VERSION_TXT + " file. If this file not exists would the file created with initial version '"
                + INIT_VERSION + "' or with the Version from build.gradle at project root directory.");
    }

    @Option(option = "value", description = "Versionupdate in " + VERSION_TXT)
    void setVersion(final String versionToSet) {
        if (checkVersion(versionToSet)) {
            this.versionToSet = versionToSet;
            try (final BufferedWriter bw = new BufferedWriter(new FileWriter(VERSION_TXT))) {
                bw.write(versionToSet);
            } catch (final IOException e) {
                throw new RuntimeException("Could not change or create " + VERSION_TXT + "  file!", e);
            }
            removeVersionFromGradleFile();
        } else {
            errorWrongVersionFormat(versionToSet);
        }
    }

    @TaskAction
    public void versionSet() {
        setVersionForProject();
        printGematikLogo();
        final String versionString = "  VERSION  " + getProject().getVersion() + "  ";
        final String groupString = "  GROUP    " + getProject().getGroup() + "  ";
        final String artefactString = "  NAME     " + getProject().getRootProject().getName() + " ";
        final String versionline = getLineForString(versionString);
        final String groupline = getLineForString(groupString);
        final String artefactline = getLineForString(artefactString);
        getLogger().quiet(STAR_LINE);
        getLogger().quiet(groupline);
        getLogger().quiet(artefactline);
        getLogger().quiet(versionline);
        getLogger().quiet(STAR_LINE);
    }

    private void setVersionForProject() {
        final File versionFile = new File(VERSION_TXT);
        if (!versionFile.exists()) {
            getProject().getLogger().warn("File " + VERSION_TXT + " not found! Create new file.");
            try (final BufferedWriter bw = new BufferedWriter(new FileWriter(versionFile))) {
                if (getProject().getVersion() != null && checkVersion(getProject().getVersion().toString().trim())) {
                    bw.write(getProject().getVersion().toString().trim());
                    removeVersionFromGradleFile();
                } else {
                    bw.write(versionToSet);
                    getProject().getLogger()
                            .error("The Version number '" + getProject().getVersion()
                                    + "' in build.gradle could not used because it don't match rules"//
                                    + "Version rules: \n"//
                                    + "{major}.{minor}.{patch}-{buildnummer} \n"//
                                    + "{major}.{minor}.{patch}-SNAPSHOT\n"//
                                    + "{major}.{minor}.{patch}.{gematikpatch}-{buildnummer}\n\n"//
                                    + "The initial version number " + versionToSet + " for " + VERSION_TXT + " would used!");
                }
            } catch (final IOException e) {
                throw new RuntimeException(
                        "Error by file " + VERSION_TXT + "creation! Please create file " + VERSION_TXT + " manually and add the Version number"
                                + " as content.",
                        e);
            }
        }
        try (final BufferedReader br = new BufferedReader(new FileReader(versionFile))) {
            final String version = br.readLine().trim();
            if (checkVersion(version)) {
                getProject().setVersion(version);
            } else {
                errorWrongVersionFormat(version);
            }
        } catch (final Exception e) {
            getProject().getLogger().error("File " + VERSION_TXT + " not found or nit readable!", e);
        }
    }

    private void removeVersionFromGradleFile() {
        final List<String> lines = new ArrayList<>();
        try (final BufferedReader br = new BufferedReader(new FileReader(getProject().getBuildFile()))) {
            String readLine = null;
            while ((readLine = br.readLine()) != null) {
                if (!(readLine.trim().startsWith("version") && readLine.trim().contains(getProject().getVersion().toString().trim()))) {
                    lines.add(readLine);
                }
            }

        } catch (final IOException e) {
            throw new RuntimeException("Error by remove version number from build.gradle file!", e);
        }
        try (final BufferedWriter bw = new BufferedWriter(new FileWriter(getProject().getBuildFile()))) {
            boolean first = true;
            for (final String line : lines) {
                if (!first) {
                    bw.newLine();
                }
                first = false;
                bw.write(line);
            }
        } catch (final IOException e) {
            throw new RuntimeException("Error by remove version number from build.gradle file!", e);
        }
    }

    private void errorWrongVersionFormat(final String version) {
        throw new RuntimeException("The Version number '" + version + " could not used because it don't match rules"
                + "Version rules: \n"//
                + "{major}.{minor}.{patch}-{buildnummer} \n"//
                + "{major}.{minor}.{patch}-SNAPSHOT\n"//
                + "{major}.{minor}.{patch}.{gematikpatch}-{buildnummer}");
    }

    private boolean checkVersion(final String version) {
        return version.length() > 0 && (version.split("\\.").length == 3 || version.split("\\.").length == 4);
    }
}
