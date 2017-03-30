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
package org.disl.db.oracle

import org.disl.db.ChangedContextTest
import org.disl.meta.Context
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

/**
 * Unit test for OracleReverseEngineerig service.
 * This test is ignored by default and may be run manually from IDE.
 * To run this test manualy:
 * 1) Copy Oracle JDBC driver jars into lib dir.
 * 2) Create context configuration file name oracle.db.test.context.properties in src/test/resources
 *
 * Template for context config:
 *
 default=Oracle
 default.host=localhost
 default.port=1521
 default.serviceName=orcl
 default.schema=[user]
 default.user=[user]
 default.password=[password]

 * */
@Ignore
class TestOracleReverseEngineeringService extends ChangedContextTest {

    OracleReverseEngineeringService s

    @Before
    void init() {
        super.init()
        s=Context.getReverseEngineeringService('default')
    }

    public String getContextName() {
        'oracle.db.test'
    }

    @Test
    void testReverseEngineerSchema() {
        new File("build/test/oracle/USER_TABLES.groovy").delete()
        s.reverseSchemaTables('oracle','USER_TABLES','SYS',new File('build/test'))
        assert new File("build/test/oracle/USER_TABLES.groovy").exists()
    }

}
