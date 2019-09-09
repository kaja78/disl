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
package org.disl.util.sqlDep

import groovy.transform.CompileStatic;

import org.junit.Ignore;
import org.junit.Test

@Ignore
@CompileStatic
class TestSqlDepService {

	@Test	
	void testPost() {
		SqlDepService sqlDep=new SqlDepService(restUrl: SqlDepService.SQL_DEP_MOCK_URL)
		CreateSqlSetRequest request=new CreateSqlSetRequest(customSqlSetName: 'test', queries:[new SqlDepQuery(sourceCode: 'select 1 as a from dual',groupName:'testing group',name:'testing query')])
		println sqlDep.post(request)
	}
}
