/*
 *
 * Copyright 2015 - 2021 Karel Hübl <karel.huebl@gmail.com>.
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
package org.disl.meta

import org.junit.Assert
import org.junit.Test

class TestTerm {

    @Name('Testing term')
    @Description('Testing term description.')
    static class TestingTerm extends Term {

        @Name('Testing subterm')
        @Description('Testing subterm description.')
        static class TestingSubterm extends Term {}
    }

    @Test
    void testTerm() {
        Term term=MetaFactory.create(TestingTerm)
        Term subterm=MetaFactory.create(TestingTerm.TestingSubterm)

        Assert.assertEquals('Testing term',term.name)
        Assert.assertEquals('Testing term description.',term.description)

        Assert.assertEquals('Testing subterm',subterm.name)
        Assert.assertEquals('Testing subterm description.',subterm.description)
    }
}
