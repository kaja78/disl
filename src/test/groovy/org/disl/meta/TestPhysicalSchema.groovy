/*
 * Copyright 2015 - 2016 Karel Hübl <karel.huebl@gmail.com>.
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

import groovy.transform.CompileStatic

import org.disl.meta.TestTable.TESTING_TABLE
import org.disl.test.DislTestCase
import org.junit.Assert
import org.junit.ComparisonFailure
import org.junit.Test

@CompileStatic
class TestPhysicalSchema extends DislTestCase {


	@Test
	void testPassword() {
		Context.setContextName('disl-test')
		PhysicalSchema s1=Context.getContext().getPhysicalSchema("s1")
		PhysicalSchema s2=Context.getContext().getPhysicalSchema("s2")
		Assert.assertEquals('secure',s1.password)
		Assert.assertEquals('secure',s2.password)
	}

	@Test
	void testGetSql() {
		Context.setContextName('disl-test')
		Context.getContext().getPhysicalSchema("default").getSql()
	}

	@Test
	void testValidateTable() {
		TESTING_TABLE testingTable=(TESTING_TABLE)MetaFactory.create(TESTING_TABLE)
		testingTable.execute()
		PhysicalSchema defaultSchema=Context.getContext().getPhysicalSchema("default")
		defaultSchema.validateTableDeployment(testingTable)

		def A=testingTable.A
		testingTable.columns.remove(testingTable.A)
		try {
			defaultSchema.validateTableDeployment(testingTable)
			Assert.fail('ComparisonFailure expected')
		} catch (ComparisonFailure e) {
			Assert.assertTrue(e.getMessage().startsWith('Column definition of deployed PUBLIC.TESTING_TABLE does not match to model. expected:<[]B'))
		}
		
		testingTable.columns.add(A)
		try {
			defaultSchema.validateTableDeployment(testingTable)
			Assert.fail('ComparisonFailure expected')
		} catch (ComparisonFailure e) {
			Assert.assertTrue(e.getMessage().startsWith('Column definition of deployed PUBLIC.TESTING_TABLE does not match to model. expected:<[B'))
		}
		
		
	}
}
