/*
 * Copyright 2015 - 2016 Karel H�bl <karel.huebl@gmail.com>.
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
package org.disl.workflow

import org.disl.workflow.ClassFinder.FileSystemFinder
import org.disl.workflow.ClassFinder.JarFinder
import org.junit.Assert
import org.junit.Test

import groovy.transform.CompileStatic;
@CompileStatic
class TestClassFinder {
	
	ClassFinder fsFinder=ClassFinder.createClassFinder('org.disl.meta')
	ClassFinder jarFinder=ClassFinder.createClassFinder('org.junit.runner')

	@Test
	public void testFindTypesJar() {
		Class sourceClass=org.junit.runner.Runner
		Class typeInSubpackage=org.junit.runner.notification.Failure
		Class typeInSamePackage=org.junit.runner.Result
		Class typeInParentPackage=org.junit.ComparisonFailure
		Class typeInOtherPackage=org.junit.validator.AnnotationValidator

		Assert.assertEquals(0,jarFinder.findTypes(Object,{false}).size())
		
		Collection<Class<Object>> types=jarFinder.findTypes (Object,{true})
		println jarFinder.listClassNames('org.junit.runner')
		
		Assert.assertTrue(types.contains(sourceClass))
		Assert.assertTrue(types.contains(typeInSubpackage))
		Assert.assertTrue(types.contains(typeInSamePackage))
		
		Assert.assertFalse(types.contains(typeInParentPackage))
		Assert.assertFalse(types.contains(typeInOtherPackage))		
	}
	
	@Test
	public void testFindTypesDir() {
		Class testingClass=org.disl.meta.Base
		Class typeInSamePackage=org.disl.meta.Table
		Class typeInOtherPackage=org.disl.workflow.Job

		
		Assert.assertEquals(0,fsFinder.findTypes(Object,{false}).size())
		
		Collection<Class<Object>> types=fsFinder.findTypes (Object,{true})
		println fsFinder.listClassNames('org.disl.meta')
		
		Assert.assertTrue(types.contains(testingClass))
		Assert.assertTrue(types.contains(typeInSamePackage))
		
		Assert.assertFalse(types.contains(typeInOtherPackage))
	}
	
	@Test
	public void testToString() {
		Assert.assertTrue fsFinder.toString().startsWith('org.disl.workflow.ClassFinder$CompositeFinder: org.disl.workflow.ClassFinder$FileSystemFinder: file:')
		Assert.assertTrue jarFinder.toString().startsWith('org.disl.workflow.ClassFinder$CompositeFinder: org.disl.workflow.ClassFinder$JarFinder: jar:file:')
	}

	
}
