package org.disl.meta

import groovy.io.FileType

import java.lang.reflect.Modifier

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
		def typesToCreate=findTypes(traversePath,{it.getName().startsWith(rootPackage) && assignableType.isAssignableFrom(it) && !Modifier.isAbstract(it.getModifiers())})
		typesToCreate.collect {create(it)}
	}

	private static Collection<Class> findTypes(String traversePath,Closure classFilter) {
		File traverseDir = new File(traversePath)
		def filterClassFiles = ~/.*\.class$/
		def types=[]
		traverseDir.traverse (type: FileType.FILES, nameFilter: filterClassFiles){
			String classFile=it.absolutePath.substring(traverseDir.absolutePath.length()+1)
			String className=classFile.substring(0,classFile.length()-6).replace('\\', '.')
			def type=Class.forName(className)
			types.add(type)
		}
		types.findAll classFilter
	}
}
