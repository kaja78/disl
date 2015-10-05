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

import groovy.io.FileType

import java.lang.reflect.Modifier
/**
 * Factory for DISL model objects.
 * */
class MetaFactory {
	static <T> T create(Class<T> type) {
		def instance=type.newInstance();
		if (instance instanceof Initializable) {
			instance.init();
		}
		instance
	}

	/**
	 * Traverse all class files in traversePath and creates instances for all found classes in given rootPackage (including subpackages) which are assignable to assignableType.
	 * 
	 * Example: 
	 * //Generate all tables defined in disl model for your data warehouse.
	 * MetaFactory.createAll("bin","com.yourDw",Table).each({it.generate})
	 * */
	static Collection createAll(String traversePath,String rootPackage,Class assignableType) {
		def typesToCreate=findNonAbstractTypes(traversePath,rootPackage,assignableType)
		typesToCreate.collect {create(it)}
	}
	
	static Collection<Class> findNonAbstractTypes(String traversePath,String rootPackage,Class assignableType) {
		findTypes(traversePath,rootPackage,{assignableType.isAssignableFrom(it) && !Modifier.isAbstract(it.getModifiers())})
	}

	static Collection<Class> findTypes(String traversePath,String rootPackage,Closure classFilter) {
		File rootDir = new File(traversePath)
		File traverseDir = new File (rootDir,rootPackage.replace('.', '/'))
		def filterClassFiles = ~/.*\.class$/
		def types=[]
		traverseDir.traverse (type: FileType.FILES, nameFilter: filterClassFiles){
			String classFile=it.absolutePath.substring(rootDir.absolutePath.length()+1)
			String className=classFile.substring(0,classFile.length()-6).replace('\\', '.')
			def type=Class.forName(className)
			types.add(type)
		}
		types.findAll classFilter
	}
}
