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
package org.disl.util.doc

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.disl.meta.Perspective

import java.lang.reflect.Modifier

import org.disl.meta.Base
import org.disl.meta.Mapping
import org.disl.meta.MappingSource
import org.disl.meta.MetaFactory
import org.disl.meta.Table
import org.disl.meta.TableMapping
import org.disl.workflow.ClassFinder
import org.disl.workflow.Job

@Slf4j
@CompileStatic
class MetaManager {

	List<Class> dislClasses=[]

	Set<String> elements=new TreeSet(new ClassNameComparator())
	Map<String,String> elementDescriptionMap=new HashMap()
	Map<String,Set<String>> sourceUsage=new TreeMap(new ClassNameComparator())
	Map<String,Set<String>> targetUsage=new TreeMap(new ClassNameComparator())

	/**
	 * Map holding set of model element names by package name string key.
	 * */
	Map<String,Set<String>> packageContent=new TreeMap()
	Map<String,Set<String>> packageTables=new TreeMap()
	Map<String,Set<String>> packageMappings=new TreeMap()

	void addRootPackage(String rootPackage) {
		log.info("Searching for DISL classes in root package $rootPackage")
		ClassFinder cf=ClassFinder.createClassFinder(rootPackage)
		List<Class<Base>> classes=cf.findTypes(Base,{
			Class type=(Class)it
			int modifiers=type.getModifiers()
			return includeType(type) && !Modifier.isAbstract(modifiers) && ((Modifier.isStatic(modifiers) && type.isLocalClass()) || !type.isLocalClass())
		})
		dislClasses.addAll(classes)
		log.info("${classes.size()} DISL elements found for root package $rootPackage.")
	}

	protected boolean includeType(Class type) {
		(MappingSource.isAssignableFrom(type) || Job.isAssignableFrom(type))
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	void process(Closure closure) {
		dislClasses.each {
			log.info("Processing ${it.name}.")
			Class<Base> modelElement=it
			String packageName=modelElement.getPackage().getName()
			try {
				Base element=MetaFactory.create(modelElement)				
				closure.call(element)
				addPackageContent(packageName,element)
				addUsage(element)
				if (element instanceof MappingSource) {
					elementDescriptionMap.put(element.class.name,element.description)
				}
			} catch (Exception e) {
				log.error(e.getMessage())
				e.printStackTrace()
			}
		}
	}

	void addPackageContent(String packageName,Base modelElement) {
		elements.add(modelElement.class.name)
		addContent(packageContent,packageName,modelElement.class.name)
		if (modelElement instanceof Mapping) {
			addContent(packageMappings,packageName,modelElement.class.name)
		}
		if (modelElement instanceof Table) {
			addContent(packageTables,packageName,modelElement.class.name)
		}		
	}

	void addContent(Map<String,Set<String>> map,String packageName, String className) {
		Set<String> l=map.get(packageName)
		if (!l) {
			l=new TreeSet(new ClassNameComparator())
			map.put(packageName,l)
		}
		l.add(className)
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

	void addUsage(Job job) {
		job.jobEntries.each {
			addSourceUsage(it.executable.class.name,job.class.name)
			addTargetUsage(job.class.name,it.executable.class.name)
		}
	}

	void addUsage(Perspective perspective) {
		//do nothing
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

	public static String getElementName(String className) {
		if (className.contains('$')) {
			return className.substring(className.lastIndexOf('$')+1)
		}
		if (className.contains('.')) {
			return className.substring(className.lastIndexOf('.')+1)
		}
		return className
	}


	static class ClassNameComparator implements Comparator<String> {
		@Override
		public int compare(String o1, String o2) {
			o1=MetaManager.getElementName(o1)+o1
			o2=MetaManager.getElementName(o2)+o2
			return o1.compareTo(o2)
		}
	}
}
