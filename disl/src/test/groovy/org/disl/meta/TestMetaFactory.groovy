package org.disl.meta

import org.junit.Assert
import org.junit.Test

/**
 * Created by Karel on 12. 12. 2016.
 */
class TestMetaFactory {

    @Test
    void testFindTypes() {
        Assert.assertTrue(MetaFactory.findTypes('org.disl.meta',Object).size()>0)
        Assert.assertTrue(MetaFactory.findTypes('org.junit.runner',Object).size()>0)
    }
}
