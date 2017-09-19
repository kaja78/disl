/*
 * Copyright 2015 - 2017 Karel Hübl <karel.huebl@gmail.com>.
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
