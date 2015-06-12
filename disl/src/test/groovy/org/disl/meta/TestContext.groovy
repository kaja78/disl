/*
 * Copyright 2015 Karel Hübl <karel.huebl@gmail.com>.
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

import static org.junit.Assert.*

import org.disl.test.DislTestCase
import org.junit.Test

class TestContext extends DislTestCase {
	@Test
	void testGetContext() {
		Context context=Context.getContext()
		assert context.name=="disl-test"		
		assert context.getConfig()["L2"]=="Hsqldb"
		assert context.getPhysicalSchema("L2").schema=="PUBLIC"
	}
	
	@Test
	void testGetSql() {
		Context.getSql("L2").execute("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES")
	}

}
