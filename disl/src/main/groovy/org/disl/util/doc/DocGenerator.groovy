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

import org.disl.meta.Base
import org.disl.meta.Mapping
import org.disl.meta.Table
import org.disl.pattern.Step
import org.disl.util.doc.IDocumentationStep.IMappingDocumentationStep
import org.disl.util.doc.IDocumentationStep.ITableDocumentationStep


class DocGenerator {
	String outputFolder='build/docs/disldoc'
	MetaManager metaManager=new MetaManager()
	
	ITableDocumentationStep tableDocumentationStep=new DefaultTableDocumentationStep(docGenerator:this)
	IMappingDocumentationStep mappingDocumentationStep=new DefaultMappingDocumentationStep(docGenerator:this)
	Step packageDocumentationStep
	Step homeDocumentationStep
	
	static String link(Base base) {
		"<a href='${base.class.name}.html'>$base.name</a>"
	}
	
	void addRootPackage(String rootPackage) {
		metaManager.addRootPackage(rootPackage)
	}
	
	void generate() {
		org.codehaus.groovy.runtime.NullObject.metaClass.toString = {return ''}
		metaManager.elementMap.values().findAll({it instanceof Table}).each {
			tableDocumentationStep.setTable(it)
			tableDocumentationStep.execute()
		}
		metaManager.elementMap.values().findAll({it instanceof Mapping}).each {
			mappingDocumentationStep.setMapping(it)
			mappingDocumentationStep.execute()
		}
	}
	
	
}
