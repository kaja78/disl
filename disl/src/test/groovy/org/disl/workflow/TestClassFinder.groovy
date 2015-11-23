package org.disl.workflow

import org.disl.workflow.ClassFinder.FileSystemFinder
import org.disl.workflow.ClassFinder.JarFinder
import org.hsqldb.lib.HsqlArrayHeap
import org.junit.Assert
import org.junit.Test

class TestClassFinder {

	@Test
	public void testFindTypesJar() {
		Class sourceClass=org.junit.runner.Runner
		Class typeInSubpackage=org.junit.runner.notification.Failure
		Class typeInSamePackage=org.junit.runner.Result
		Class typeInParentPackage=org.junit.ComparisonFailure
		Class typeInOtherPackage=org.junit.validator.AnnotationValidator

		ClassFinder jarFinder=ClassFinder.createClassFinder(sourceClass)	
		Collection<Class> types=jarFinder.findTypes {true}
		
		Assert.assertTrue(types.contains(sourceClass))
		Assert.assertTrue(types.contains(typeInSubpackage))
		Assert.assertTrue(types.contains(typeInSamePackage))
		
		Assert.assertFalse(types.contains(typeInParentPackage))
		Assert.assertFalse(types.contains(typeInOtherPackage))		
	}
	
	@Test
	public void testFindTypesDir() {
		Class sourceClass=org.disl.meta.Base
		Class typeInSamePackage=org.disl.meta.Table
		Class typeInOtherPackage=org.disl.workflow.Job

		ClassFinder fsFinder=ClassFinder.createClassFinder(sourceClass)	
		Collection<Class> types=fsFinder.findTypes {true}
		
		Assert.assertTrue(types.contains(sourceClass))
		Assert.assertTrue(types.contains(typeInSamePackage))
		
		Assert.assertFalse(types.contains(typeInOtherPackage))
	}

	
}
