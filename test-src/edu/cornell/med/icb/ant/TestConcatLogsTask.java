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

import org.junit.Test;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.selectors.FilenameSelector;
import org.apache.tools.ant.Project;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Describe class here.
 *
 * @author Kevin Dorff
 */
public class TestConcatLogsTask {

    private final static String CONCAT_LOGS_OUTPUT = "test-results/try-concat-logs-output.log";

    @Test
    public void testConcatLogs() throws IOException {

        // delete the file we will be writing
        File output = new File(CONCAT_LOGS_OUTPUT);
        output.delete();

        Project project = new Project();
        project.setBasedir(".");

        ConcatLogsTask task = new ConcatLogsTask();
        task.setProject(project);
        task.setOutput(CONCAT_LOGS_OUTPUT);

        FilenameSelector selector = new FilenameSelector();
        selector.setName("*.log");
        FileSet fileset = new FileSet();
        fileset.setDir(new File("test-data"));
        fileset.addFilename(selector);
        task.addFileset(fileset);

        // Write the new log file
        task.execute();

        assertTrue(CONCAT_LOGS_OUTPUT + " should exist.", output.exists());

        List lines = FileUtils.readLines(output);
        assertEquals("Wrong number of lines", 31, lines.size());
    }

}
