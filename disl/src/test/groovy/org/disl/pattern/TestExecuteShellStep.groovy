package org.disl.pattern

import org.junit.Assert
import org.junit.Test

/**
 * Created by kh13346 on 31.8.2017.
 */
class TestExecuteShellStep {


    ExecuteShellStep getTestingStep() {
        new ExecuteShellStep() {
            @Override
            String getCode() {
                if (isWindows())
                {
                    return 'dir'
                }
                return 'ls'
            }
        }
    }

    @Test
    void testExecute() {
        ExecuteShellStep t=getTestingStep()
        String expected
        if (t.isWindows()) {
            expected="cmd /c dir".execute().text
        } else {
            expected="sh -c dir".execute().text
        }
        t.execute()
        Assert.assertEquals(expected ,t.processOutput)
    }

}
