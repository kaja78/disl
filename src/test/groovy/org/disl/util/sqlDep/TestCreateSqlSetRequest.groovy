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
package org.disl.util.sqlDep

import static org.junit.Assert.*

import org.junit.Test

class TestCreateSqlSetRequest {
		
	@Test
	void testToJson() {
		CreateSqlSetRequest request=new CreateSqlSetRequest(userAccountId: 'xxx',customSqlSetName: 'test', queries:[new SqlDepQuery(sourceCode: 'select 1 as a from dual',groupName:'testing group',name:'testing query')])
		String expected='''\
{
    "dialect": "generic",
    "queries": [
        {
            "groupName": "testing group",
            "sourceCode": "select 1 as a from dual",
            "name": "testing query"
        }
    ],
    "customSqlSetName": "test",
    "userAccountId": "xxx"
}'''
		assertEquals(expected,request.toJson())
	}

}
