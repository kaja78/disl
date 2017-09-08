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
