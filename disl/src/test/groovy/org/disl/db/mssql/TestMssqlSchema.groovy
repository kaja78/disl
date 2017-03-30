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
package org.disl.db.mssql

import org.disl.db.ChangedContextTest
import org.disl.db.reverseEngineering.ReverseEngineeringService
import org.disl.meta.Context
import org.disl.meta.PhysicalSchema
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

/**
 * Unit test for Vertica schema.
 * This test is ignored by default and may be run manually from IDE.
 * To run this test manualy:
 * 1) Copy Oracle JDBC driver jars into lib dir.
 * 2) Create context configuration file name oracle.db.test.context.properties in src/test/resources
 *
 * Template for context config:
 *
 default=Mssql
 default.host=192.168.2.194
 default.port=1433
 default.databaseName=mdm
 default.schema=[schema]
 default.user=[user]
 default.password=[password]

 * */
@Ignore
class TestMssqlSchema extends ChangedContextTest {

    PhysicalSchema s

    @Before
    void init() {
        super.init()
        s=Context.getContext().getPhysicalSchema('default')
    }

    @Override
    String getContextName() {
        'mssql.db.test'
    }

    @Test
    void testReverse() {
        ReverseEngineeringService s=Context.getReverseEngineeringService('default')
        new File("build/test/mssql/MDM_OBJECT.groovy").delete()
        s.reverseSchemaTables('mssql','MDM_OBJECT','dbo',new File('build/test'))
        assert new File("build/test/mssql/MDM_OBJECT.groovy").exists()
    }

    @Test
    void testEvaluateExpression() {
        Assert.assertEquals(3,s.evaluateExpression('1+2'))
    }

    @Test
    void testEvaluateAggregateExpression() {
        Assert.assertEquals(3,s.evaluateAggregateExpression("sum(a)",[[a:1],[a:2]]))
    }
}
