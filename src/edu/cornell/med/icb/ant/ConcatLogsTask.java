/*
 * Copyright (C) 2007-2008 Institute for Computational Biomedicine,
 *               Weill Medical College of Cornell University
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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.util.List;
import java.util.LinkedList;

/**
 * Ant task to concatenate log files. This task assumes that each log file has a header line
 * and that each of the log files being concatonated has the same (or equivalent) header
 * line. The header line from the first log file will be kept, all subsequent log files
 * will skip writing the first line of the file.
 */
public class ConcatLogsTask extends Task {

    /**
     * The file to write to.
     */
    private File outputFile;

    /**
     * The log files to wrote to the output file.
     */
    private final List<FileSet> filesets = new LinkedList<FileSet>();

    /**
     * Set to true to fail on an error.
     */
    private boolean failOnError = true;

    /**
     * Default constructor for task.
     */
    public ConcatLogsTask() {
    }

    /**
     * Main work completed here.
     */
    public void execute() {
        if (outputFile == null) {
            throw new BuildException(
                    "There must be an output attribute", getLocation());
        }
        if (filesets.size() == 0) {
            throw new BuildException(
                    "There must be a file attribute or a fileset child element", getLocation());
        }

        boolean hadError = false;

        PrintWriter output = null;
        try {
            outputFile.delete();
            output = new PrintWriter(new FileOutputStream(outputFile));

            int fileNum = 0;
            for (FileSet fs : filesets) {
                final DirectoryScanner ds = fs.getDirectoryScanner(getProject());
                final File dir = fs.getDir(getProject());
                for (String src : ds.getIncludedFiles()) {
                    final File logFile = new File(dir, src);
                    String line;

                    BufferedReader input = null;
                    try {
                        input =  new BufferedReader(new FileReader(logFile));
                        int lineNum = 0;
                        while ((line = input.readLine()) != null) {
                            if (lineNum == 0) {
                                if (fileNum == 0) {
                                    output.printf("%s%n", line);
                                }
                            } else {
                                output.printf("%s%n", line);
                            }
                            lineNum++;
                        }
                        fileNum++;
                    } finally {
                        if (input != null) {
                            input.close();
                        }
                    }
                }
            }
        } catch (IOException e) {
            hadError = true;
            System.out.println(e.toString());
        } finally {
            if (output != null) {
                output.close();
            }
        }

        if (hadError && failOnError) {
            throw new BuildException("Log concat failed.", getLocation());
        }
    }

    /**
     * Handles the <code>output</code> attribute.
     * @param outputFilname the attribute value
     */
    public void setOutput(final String outputFilname) {
        outputFile = new File(outputFilname);
    }

    /**
     * Handles the <code>failonerror</code> attribute.
     * @param failOnErrorVal the attribute value converted to a boolean
     */
    public void setFailonerror(final boolean failOnErrorVal) {
        this.failOnError = failOnErrorVal;
    }

    /**
     * Add a <code>fileset</code> to handle.
     * @param set the FileSet to add
     */
    public void addFileset(final FileSet set) {
        filesets.add(set);
    }
}
