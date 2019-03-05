package org.disl.db.oracle

import org.disl.db.ChangedContextTest
import org.disl.meta.Context
import org.junit.Before

abstract class OracleTest extends ChangedContextTest {

    @Override
    String getContextName() {
        'oracle.db.test'
    }
}
