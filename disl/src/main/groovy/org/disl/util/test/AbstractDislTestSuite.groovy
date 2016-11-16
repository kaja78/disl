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
package org.disl.util.test

import groovy.transform.CompileStatic
import junit.framework.JUnit4TestAdapter
import junit.framework.TestSuite

import org.disl.meta.Mapping
import org.disl.meta.Table
import org.disl.workflow.ClassFinder
import org.junit.runner.RunWith
import org.junit.runners.AllTests

@CompileStatic
@RunWith(AllTests.class)
abstract class AbstractDislTestSuite {

	private String rootPackage=getClass().getPackage().getName()
	private ClassFinder classFinder
	protected TestSuite testSuite
		
	protected ClassFinder getClassFinder() {
		if (!classFinder) {
			classFinder=ClassFinder.createClassFinder(rootPackage)
		}
		return classFinder
	}
	
	public void setRootPackage(String rootPackage) {
		classFinder=null
		this.rootPackage=rootPackage
	}
	
	public String getRootPackage() {
		return this.rootPackage
	}
		
	TestSuite getTestSuite() {
		if (!testSuite) {
			testSuite = new TestSuite(this.getClass().getName());
			initTestSuite()
		}
		return testSuite
	}

	protected void initTestSuite() {				
		addTestSuite('mappings', Mapping)
		addTestSuite('tables', Table)
		addTestSuite('dislTestCase', AbstractDislTestCase)		
	}
	
	protected void addTestSuite(String name,Class assignableType) {
		TestSuite suite=new TestSuite(name)
		getClassFinder().findNonAbstractTypes(assignableType).each {
			suite.addTest(new JUnit4TestAdapter(it))
		}
		this.testSuite.addTest(suite)		
	}
}
