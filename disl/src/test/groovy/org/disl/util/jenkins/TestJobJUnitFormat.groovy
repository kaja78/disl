/*
 * Copyright 2015 - 2016 Karel Hübl <karel.huebl@gmail.com>.
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
package org.disl.util.jenkins

import org.disl.meta.MetaFactory
import org.disl.pattern.ExecutionInfo
import org.disl.pattern.Pattern
import org.disl.pattern.Step
import org.disl.workflow.Job
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Created by Karel on 8. 12. 2016.
 */
class TestJobJUnitFormat {

    SampleJob job

    @Before
    void init() {
        job=MetaFactory.create(SampleJob)
        try {
            job.execute()
        } catch (Exception e) {}
        //Set duration to expected value.
        setDuration(job.executionInfo,3000)
        setDuration(job.jobEntries[0].executionInfo,2000)
        setDuration(job.jobEntries[1].executionInfo,1000)
        job.jobEntries.each {
            it.executable.steps.each {
                setDuration(it.executionInfo,1000)
            }
        }
    }

    private void setDuration(ExecutionInfo executionInfo, int duration) {
        if (executionInfo.endTime) {
            executionInfo.endTime=executionInfo.startTime+duration
        }
    }

    @Test
    void testFormat() {
        String expected="""\
<?xml version="1.0" encoding="UTF-8"?>
<testsuite>
\t<testcase name="executionSummary" classname="org.disl.util.jenkins.TestJobJUnitFormat\$SampleJob.!Summary" time="0"><system-out> Execution results for SampleJob                                         :
*********************************************************************************************
*  Name                                              *   Status   *  Time (ms)*        Rows *
*********************************************************************************************
* SampleJob                                          *      ERROR *       3000 *          0 *
*********************************************************************************************
* SampleExecutable1                                  *   FINISHED *       2000 *          2 *
* SampleExecutable2                                  *      ERROR *       1000 *          0 *
*********************************************************************************************
</system-out></testcase>
\t<testcase name="1_Step1" classname="org.disl.util.jenkins.TestJobJUnitFormat\$SampleJob.1_SampleExecutable1" time="1"><system-out>Step1 code.</system-out></testcase>
\t<testcase name="2_Step2" classname="org.disl.util.jenkins.TestJobJUnitFormat\$SampleJob.1_SampleExecutable1" time="1"><system-out>Step2 code.</system-out></testcase>
\t<testcase name="1_Step1" classname="org.disl.util.jenkins.TestJobJUnitFormat\$SampleJob.2_SampleExecutable2" time="1"><system-out>Step1 code.</system-out><failure message="/ by zero"></failure></testcase>
\t<testcase name="2_Step2" classname="org.disl.util.jenkins.TestJobJUnitFormat\$SampleJob.2_SampleExecutable2" time="0"><system-out>Step2 code.</system-out><skipped/></testcase>
</testsuite>"""
        Assert.assertEquals(expected,new JobJUnitFormat(job: job).format())
    }

    @Test
    void testSave() {
        new JobJUnitFormat(job: job).save()
    }

    static abstract class SampleStep extends Step {
        boolean fail=false
        int executeInternal(){
            if (fail) {
                int i=1/0
            }
            1
        }
    }
    static class Step1 extends SampleStep {
        String code='Step1 code.'
    }

    static class Step2 extends SampleStep {
        String code='Step2 code.'
    }

    static class SampleExecutable1 extends Pattern {
        @Override
        void init() {
            add(Step1)
            add(Step2)
        }
    }

    static class SampleExecutable2 extends Pattern {
        @Override
        void init() {
            add(new Step1(fail:true))
            add(Step2)
        }
    }

    static class SampleJob extends Job {
        SampleJob() {
            addType SampleExecutable1
            addType SampleExecutable2
        }
    }
}
