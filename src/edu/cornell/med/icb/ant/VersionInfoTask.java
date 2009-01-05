/*
 * Copyright (C) 2008-2009 Institute for Computational Biomedicine,
 *                         Weill Medical College of Cornell University
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.cornell.med.icb.ant;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.net.URL;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.Map;

/**
 * Prints the "Implementation-Version" read from the manifest of a jar file.
 */
public class VersionInfoTask extends Task {
    /**
     * URL of the jar file to read the manifest from.
     */
    private String jarfileUrl;
    /**
     * The ant property to set with the version information.
     */
    private String property;

    /**
     * Called by the project to let the task do its work. This method may be
     * called more than once, if the task is invoked more than once.
     * For example, if target1 and target2 both depend on target3, then running
     * "ant target1 target2" will run all tasks in target3 twice.
     *
     * @throws BuildException if something goes wrong with the build.
     */
    @Override
    public void execute() {
        if (jarfileUrl == null) {
            throw new BuildException("jarfileUrl not set");
        }

        log("Loading from: " + jarfileUrl, Project.MSG_VERBOSE);
        String version;
        try {
            final URL manifestUrl = new URL("jar:" + jarfileUrl + "!/" + JarFile.MANIFEST_NAME);
            final Manifest manifest = new Manifest(manifestUrl.openStream());
            final Attributes attributes = manifest.getMainAttributes();
            for (Map.Entry entry : attributes.entrySet()) {
                log(entry.getKey() + ":" + entry.getValue(), Project.MSG_DEBUG);
            }
            version = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        } catch (Exception e) {
            // NOTE - if an exception is logged here, the build will fail
            //log("Class '" + jarfileUrl + "' not found!", e, Project.MSG_WARN);
            version = "UNKNOWN";
        }

        log("version for '" + jarfileUrl + "' is: " + version, Project.MSG_INFO);
        if (property != null) {
            getProject().setNewProperty(property, version);
        } else {
            System.out.println(version);
        }
    }

    /**
     * Set the url of the jar file to read the manifest from.
     * @param url The url of a jar file.
     */
    public void setJarfileUrl(final String url) {
        this.jarfileUrl = url;
    }

    /**
     * Set the name of an ant property to set with the version information.
     * @param property The name of the property to set.
     */
    public void setProperty(final String property) {
        this.property = property;
    }
}
