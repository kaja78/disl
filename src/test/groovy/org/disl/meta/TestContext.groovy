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
import org.junit.Ignore

import static org.junit.Assert.*

import org.disl.test.DislTestCase
import org.junit.Test

class TestContext extends DislTestCase {
	@Test
	void testGetContext() {
		Context context=Context.getContext()
		assert context.getName().equals('disl-test')		
		assert context.getConfig()["default"]=="Hsqldb"
		assert context.getPhysicalSchema("default").schema=="PUBLIC"
	}
	
	@Test
	void testGetSql() {
		Context.getSql("default").execute("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES")
	}
	
	@Test
	void testGetContextProperty() {
		Assert.assertEquals(System.getenv('PATH'),Context.getContextProperty('env.PATH'))
	}

	@Test
	void testGetGlobalProperties() {
		Assert.assertEquals('globalValue',Context.getContextProperty('global.value'))
		Assert.assertEquals('disl-test',Context.getContextProperty('global.overridenValue'))
	}

	@Test
	@Ignore
	void testDislHomeProperties() {
		Assert.assertEquals('disl',Context.getContextProperty('disl.test.global'))
		Assert.assertEquals('disl-test',Context.getContextProperty('disl.test.user'))
	}

}
