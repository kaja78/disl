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

import org.disl.test.DislTestCase;
import org.junit.Assert
import org.junit.Test

class TestPhysicalSchema {
	

	@Test
	void testPassword() {
		Context.setContextName('disl-test')
		PhysicalSchema s1=Context.getContext().getPhysicalSchema("s1")
		PhysicalSchema s2=Context.getContext().getPhysicalSchema("s2")	
		Assert.assertEquals('secure',s1.password)
		Assert.assertEquals('secure',s2.password)
	}

}
