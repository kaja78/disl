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

import java.lang.invoke.SwitchPoint;
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
	String rootDir
	
	protected String getClassName(String fileName) {
		return fileName.substring(0,fileName.length()-6).replace('/', '.').replace('\\', '.')
	}
	
	@Override
	public String toString() {
		"${getClass().getName()}: $sourceURL"
	}
	
	
	public static ClassFinder createClassFinder(String rootPackage) {
		List<ClassFinder> finders=[]
		String rootDir=rootPackage.replace('.','/')
		ClassLoader classLoader=Thread.currentThread().getContextClassLoader()
		classLoader.getResources(rootDir).each {
			URL url=(URL)it
			
			switch (url.getProtocol()) {
				case 'jar':
					finders.add new JarFinder(rootDir: rootDir,sourceURL: url)
					break
				case 'file':
					finders.add new FileSystemFinder(rootDir: rootDir,sourceURL: url)
					break
				default :
					throw new RuntimeException("Unsupported URL protocol for resource $url");
			}
		}
		return new CompositeFinder(finders:finders,rootDir:rootDir)		
	}
	
	public List<Class> findNonAbstractTypes(Class assignableType) {
		findNonAbstractTypes(rootDir,assignableType)		
	}
	
	public List<Class> findNonAbstractTypes(String rootDir,Class assignableType) {
		findTypes(rootDir,{assignableType.isAssignableFrom(((Class)it)) && !Modifier.isAbstract(((Class)it).getModifiers())})
	}

	public List<Class> findTypes(Closure classFilter) {
		return findTypes(rootDir,classFilter);
	}
	
	public abstract List<Class> findTypes(String rootPackage,Closure classFilter);

	static class FileSystemFinder extends ClassFinder {
			
		public List<Class> findTypes(String rootDir,Closure classFilter) {
			
			File traverseDir = new File (sourceURL.toURI())
			Pattern filterClassFiles = ~/.*\.class$/
			List<Class> types=[]
			traverseDir.traverse ((Map)[type: FileType.FILES, nameFilter: filterClassFiles]) {
				String classFile=rootDir+'/'+it.absolutePath.substring(traverseDir.absolutePath.length()+1)				
				Class type=Class.forName(getClassName(classFile))
				types.add(type)
			}
			types.findAll(classFilter).toList()
		}

	}

	static class JarFinder extends ClassFinder {
		JarFile getJarFile() {
			((JarURLConnection)sourceURL.openConnection()).getJarFile()			
		}
		public List<Class> findTypes(String rootPackage,Closure classFilter) {
			String rootDir=rootPackage.replace('.','/')
			JarFile jarFile=getJarFile()
			
			Collection<JarEntry> entries=getJarFile().entries().findAll()
			entries=entries.findAll({
				String name=((JarEntry)it).getName()
				(name.startsWith(rootDir) && name.endsWith('.class'))})
			
			return entries.collect({Class.forName(getClassName(it.name))}).findAll(classFilter).toList()
		}

	}
	
	static class CompositeFinder extends ClassFinder {
		
		List<ClassFinder> finders
		
		public List<Class> findTypes(String rootPackage,Closure classFilter) {
			List<Class> types=[]
			finders.each {
				types.addAll(it.findTypes(rootPackage,classFilter))
			}
			return types
		}
		
		@Override
		public String toString() {
			"${getClass().getName()}: ${finders.join(',\n')}"
		}
		
	} 
}
