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
package org.disl.workflow

import groovy.io.FileType
import groovy.transform.CompileStatic

import java.lang.reflect.Modifier
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.regex.Pattern

/**
 * Helper for finding classes based on search conditions.
 * */
@CompileStatic
abstract class ClassFinder {
	
	URL sourceURL
	Class sourceClass
	
	protected String getClassName(String fileName) {
		return fileName.substring(0,fileName.length()-6).replace('/', '.').replace('\\', '.')
	}
	
	protected File getCodeSourceFile() {
		new File(URLDecoder.decode(sourceClass.getProtectionDomain().getCodeSource().getLocation().file))
	}
	
	public static ClassFinder createClassFinder(Class sourceClass) {
		URL sourceURL=sourceClass.getResource('/'+sourceClass.getName().replace('.', '/')+".class");
		switch (sourceURL.getProtocol()) {
			case 'jar':
				return new JarFinder(sourceClass: sourceClass,sourceURL: sourceURL)
				break
			case 'file':
				return new FileSystemFinder(sourceClass: sourceClass,sourceURL: sourceURL)
				break
		}
		throw new RuntimeException("Unsupported URL protocol for source class ${sourceClass.name}");
	}
	
	public List<Class> findNonAbstractTypes(Class assignableType) {
		findNonAbstractTypes(sourceClass.getPackage().getName(),assignableType)		
	}
	
	public List<Class> findNonAbstractTypes(String rootPackage,Class assignableType) {
		findTypes(rootPackage,{assignableType.isAssignableFrom(((Class)it)) && !Modifier.isAbstract(((Class)it).getModifiers())})
	}

	public List<Class> findTypes(Closure classFilter) {
		return findTypes(sourceClass.getPackage().getName(),classFilter); 
	}
	
	public abstract List<Class> findTypes(String rootPackage,Closure classFilter);

	static class FileSystemFinder extends ClassFinder {
			
		public List<Class> findTypes(String rootPackage,Closure classFilter) {
			File rootDir = getCodeSourceFile()
			File traverseDir = new File (rootDir,rootPackage.replace('.', '/'))
			Pattern filterClassFiles = ~/.*\.class$/
			List<Class> types=[]
			traverseDir.traverse ((Map<String,Object>)[type: FileType.FILES, nameFilter: filterClassFiles]) {
				String classFile=it.absolutePath.substring(rootDir.absolutePath.length()+1)				
				Class type=Class.forName(getClassName(classFile))
				types.add(type)
			}
			types.findAll(classFilter).toList()
		}

	}

	static class JarFinder extends ClassFinder {
		JarFile getJarFile() {
			new JarFile(getCodeSourceFile())			
		}
		public List<Class> findTypes(String rootPackage,Closure classFilter) {
			List<JarEntry> entries=getJarFile().entries().findAll({((JarEntry)it).name.startsWith(rootPackage.replace('.', '/')) && ((JarEntry)it).name.endsWith('.class')}).toList()
			return entries.collect({Class.forName(getClassName(it.name))}).findAll(classFilter).toList()
		}

	}
}
