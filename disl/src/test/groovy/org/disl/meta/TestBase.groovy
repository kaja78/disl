package org.disl.meta

import org.junit.Assert
import org.junit.Test

class TestBase {

    @Test
    void testTags() {
        Base base=MetaFactory.create(TestingBaseChild)
        Assert.assertEquals(['A','B'].toArray(),base.getTags().toArray())

    }

    @Tags('A')
    static class TestingBase extends Base {

    }

    @Tags('B')
    static class TestingBaseChild extends TestingBase {

    }
}
