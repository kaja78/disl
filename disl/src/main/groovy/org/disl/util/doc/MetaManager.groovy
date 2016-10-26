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
package org.disl.util.doc

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

import java.lang.reflect.Modifier

import org.disl.meta.Base
import org.disl.meta.Mapping
import org.disl.meta.MappingSource
import org.disl.meta.MetaFactory
import org.disl.meta.Table
import org.disl.meta.TableMapping
import org.disl.workflow.ClassFinder

@CompileStatic
class MetaManager {

	Map<String,Base> elementMap=new TreeMap()
	Map<String,Set<String>> sourceUsage=new TreeMap()
	Map<String,Set<String>> targetUsage=new TreeMap()

	/**
	 * Map holding set of model element names by package name string key.
	 * */
	Map<String,Set<String>> packageContent=new TreeMap()

	void addRootPackage(String rootPackage) {
		ClassFinder cf=ClassFinder.createClassFinder(rootPackage)

		cf.findTypes({
			Class type=(Class)it
			int modifiers=type.getModifiers()
			(MappingSource.isAssignableFrom(type)) && !Modifier.isAbstract(modifiers) && ((Modifier.isStatic(modifiers) && type.isLocalClass()) || !type.isLocalOrAnonymousClass())
		}).each {
			add(it)
		}
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	void add(Class<Base> modelElement) {
		String packageName=modelElement.getPackage().getName()
		Base element=MetaFactory.create(modelElement)
		addPackageContent(packageName,element)
		addUsage(element)
	}

	void addPackageContent(String packageName,Base modelElement) {
		Set<String> l=packageContent.get(packageName)
		if (!l) {
			l=new TreeSet()
			packageContent.put(packageName,l)
		}
		if (l.add(modelElement.class.name)) {
			elementMap.put(modelElement.class.name,modelElement)
		}
	}

	void addUsage(Mapping mapping) {
		mapping.sources.each {
			addSourceUsage(it.class.name,mapping.class.name)
			addTargetUsage(mapping.class.name,it.class.name)
		}
		mapping.setOperations.each {
			addSourceUsage(it.source.class.name,mapping.class.name)
			addTargetUsage(mapping.class.name,it.source.class.name)
		}
		if (mapping instanceof TableMapping) {
			addTargetUsage(mapping.target.class.name,mapping.class.name)
			addSourceUsage(mapping.class.name,mapping.target.class.name)
		}
	}

	void addUsage(MappingSource source) {
		//do nothing
	}

	void addSourceUsage(String className,String usedByClassName) {
		Set<String> l=sourceUsage.get(className)
		if (!l) {
			l=new TreeSet()
			sourceUsage.put(className,l)
		}
		l.add(usedByClassName)
	}

	void addTargetUsage(String className,String usedByClassName) {
		Set<String> l=targetUsage.get(className)
		if (!l) {
			l=new TreeSet()
			targetUsage.put(className,l)
		}
		l.add(usedByClassName)
	}
	
	List<Base> getPackageElements(String packageName) {
		packageContent.get(packageName).collect() {
			elementMap.get(it)
		}
	}
}
