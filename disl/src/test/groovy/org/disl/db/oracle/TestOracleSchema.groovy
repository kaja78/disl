/*
 * Copyright 2015 - 2018 Karel Hübl <karel.huebl@gmail.com>.
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
import org.disl.db.reverseEngineering.ReverseEngineeringService
import org.disl.meta.Context
import org.disl.meta.PhysicalSchema
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TestOracleSchema extends ChangedContextTest {

    PhysicalSchema s

    @Before
    void init() {
        super.init()
        s=Context.getContext().getPhysicalSchema('default')
    }

    @Override
    String getContextName() {
        'oracle.db.test'
    }

    @Test
    void testJdbcUrl() {
        PhysicalSchema testOverrideByUrlSchema=Context.getContext().getPhysicalSchema('testOverrideByUrl')
        Assert.assertEquals('jdbc:oracle:thin:@myhost:1521:mysid',testOverrideByUrlSchema.getJdbcUrl())
    }

    @Test
    void testEvaluateExpression() {
        Assert.assertEquals(3,s.evaluateExpression('1+2'),0)
    }

    @Test
    void testEvaluateAggregateExpression() {
        Assert.assertEquals(3,s.evaluateAggregateExpression("sum(a)",[[a:1],[a:2]]),0)
    }

    @Test
    void testEvaluateExpressionOnProxy() {
        PhysicalSchema proxySchema=Context.getContext().getPhysicalSchema('oracleProxy')
        String proxyUser=Context.getContextProperty('default.user')
        String targetSchema=Context.getContextProperty('oracleProxy.schema')
        Assert.assertEquals("$proxyUser[$targetSchema]".toString(),proxySchema.getUser())
        Assert.assertEquals(3,proxySchema.evaluateExpression('1+2'),0)
    }
}