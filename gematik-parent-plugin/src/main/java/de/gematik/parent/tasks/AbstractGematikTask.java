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
package de.gematik.parent.tasks;

import org.gradle.api.DefaultTask;

public abstract class AbstractGematikTask extends DefaultTask {

    static final String STAR_LINE = "**********************************************************************************";
    private static final String START_LINE_TEXT = "************";

    AbstractGematikTask() {
        super();
    }

    void printGematikLogo() {

        getLogger().quiet("");
        getLogger().quiet("");
        getLogger().quiet("##################################################################################");
        getLogger().quiet("##################################################################################");
        getLogger().quiet("");
        getLogger().quiet("");
        getLogger().quiet("           ######   ######## ##     ##    ###    ######## #### ##    ## ");
        getLogger().quiet("          ##    ##  ##       ###   ###   ## ##      ##     ##  ##   ##  ");
        getLogger().quiet("          ##        ##       #### ####  ##   ##     ##     ##  ##  ##   ");
        getLogger().quiet("          ##   #### ######   ## ### ## ##     ##    ##     ##  #####    ");
        getLogger().quiet("          ##    ##  ##       ##     ## #########    ##     ##  ##  ##   ");
        getLogger().quiet("          ##    ##  ##       ##     ## ##     ##    ##     ##  ##   ##  ");
        getLogger().quiet("           ######   ######## ##     ## ##     ##    ##    #### ##    ## ");
        getLogger().quiet("");
        getLogger().quiet("");
        getLogger().quiet("##################################################################################");
        getLogger().quiet("##################################################################################");
        getLogger().quiet("");

    }

    String getLineForString(final String text) {
        final int lineStarEnd = STAR_LINE.length() - text.length() - START_LINE_TEXT.length() * 2;
        String star = "";
        for (int i = 0; i < lineStarEnd; i++) {
            star += " ";
        }
        final String versionline = START_LINE_TEXT + text + star + START_LINE_TEXT;
        return versionline;
    }

    protected void printTextBlockBorder() {
        getLogger().quiet(AbstractGematikTask.STAR_LINE);
        getLogger().quiet(AbstractGematikTask.STAR_LINE);
    }

}
