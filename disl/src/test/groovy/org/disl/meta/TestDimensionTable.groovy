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


@UniqueKey(columns=["ID","NAME"])
@Description("This is testing dimension.")
class TestDimensionTable extends Table {
	String schema="L2"
	
	@PrimaryKey
	@DataType("INTEGER")
	@Description("Surrogate key.")
	Column KEY
	
	@UniqueKey
	@DataType("INTEGER")
	@Description("Natural key")
	Column ID
	
	@DataType("VARCHAR(200)")
	@Description("Dimension name.")
	Column NAME
	
	
	
}
