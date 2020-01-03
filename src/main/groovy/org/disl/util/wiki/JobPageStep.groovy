/*
 * Copyright 2015 - 2020 Karel Hübl <karel.huebl@gmail.com>.
 *
 * This file is part of disl.
 *
 * Disl is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Disl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Disl.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.disl.util.wiki

import groovy.util.logging.Slf4j
import org.disl.meta.Base
import org.disl.pattern.Executable
import org.disl.pattern.FileOutputStep
import org.disl.workflow.Job
import org.disl.workflow.JobEntry

@Slf4j
class JobPageStep  extends FileOutputStep {

    Job job

    @Override
    File getFile() {
        return WikiHelper.getWikiPageFile(job)
    }

    @Override
    String getCharset() {
        'utf-8'
    }


    @Override
    public String getCode() {
        """\
${renderHeader()}

${WikiHelper.renderElementDescription(job)}

${this.job.getJobEntries().collect({renderJobEntry(it)}).join()}

"""
    }

    String renderHeader() {
        """\
+++
    title= "${job.name}"
    packages=["${job.getClass().getPackage().getName().replace('.', '/')}"]
    types=["Job"]
+++
"""
    }

    String renderJobEntry(JobEntry entry) {
        Executable executable=entry.executable
        if (executable instanceof  Base) {
            return  "* [${executable.name}](${url(executable)}) - ${executable.description}\n"
        }
        return "* ${executable.class.name}\n"
    }

    protected String url(Executable executable) {
        try {
            return WikiHelper.url(executable)
        } catch (Exception e) {
            log.warn("Unable to get url for Executable ${executable}.")
        }

    }
}