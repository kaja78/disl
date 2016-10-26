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
import org.disl.meta.Lookup
import org.disl.meta.Mapping
import org.disl.meta.Table
import org.disl.util.doc.IDocumentationStep.ILookupDocumentationStep
import org.disl.util.doc.IDocumentationStep.IMappingDocumentationStep
import org.disl.util.doc.IDocumentationStep.IPackageDocumentationStep
import org.disl.util.doc.IDocumentationStep.ITableDocumentationStep
import org.disl.util.doc.step.DefaultAllElementsFrame
import org.disl.util.doc.step.DefaultCssStep
import org.disl.util.doc.step.DefaultIndexStep
import org.disl.util.doc.step.DefaultMappingDocumentationStep
import org.disl.util.doc.step.DefaultOverviewFrameStep
import org.disl.util.doc.step.DefaultOverviewSummaryStep
import org.disl.util.doc.step.DefaultPackageFrameStep
import org.disl.util.doc.step.DefaultPackageSummaryStep
import org.disl.util.doc.step.DefaultTableDocumentationStep
import org.disl.util.doc.step.DefaultLookupDocumentationStep


class DocGenerator {
	String outputFolder='build/docs/disldoc'
	MetaManager metaManager=new MetaManager()

	IDocumentationStep indexStep=new DefaultIndexStep(docGenerator:this)
	IDocumentationStep cssStep=new DefaultCssStep(docGenerator:this)
	IDocumentationStep allElementsStep=new DefaultAllElementsFrame(docGenerator:this)
	IDocumentationStep overviewFrameStep=new DefaultOverviewFrameStep(docGenerator:this)
	IDocumentationStep overviewSummaryStep=new DefaultOverviewSummaryStep(docGenerator:this)
	ITableDocumentationStep tableDocumentationStep=new DefaultTableDocumentationStep(docGenerator:this)
	IMappingDocumentationStep mappingDocumentationStep=new DefaultMappingDocumentationStep(docGenerator:this)
	ILookupDocumentationStep lookupDocumentationStep=new DefaultLookupDocumentationStep(docGenerator:this)
	IPackageDocumentationStep packageFrameStep=new DefaultPackageFrameStep(docGenerator:this)
	IPackageDocumentationStep packageSummaryStep=new DefaultPackageSummaryStep(docGenerator:this)


	static String link(Base base,String urlPrefix='') {
		"<a href='${urlPrefix}${base.class.name}.html' target='main'>$base.name</a>"
	}
	
	String link(String baseClassName,String urlPrefix='') {
		Base base=metaManager.elementMap.get(baseClassName)
		if (base) {
			return link(base)
		}
		return ''
	}

	void addRootPackage(String rootPackage) {
		metaManager.addRootPackage(rootPackage)
	}

	public void generate() {
		clean()
		org.codehaus.groovy.runtime.NullObject.metaClass.toString = {return ''}
		indexStep.execute()
		cssStep.execute()
		allElementsStep.execute()
		overviewFrameStep.execute()
		overviewSummaryStep.execute()
		generateTables()
		generateMappings()
		generatePackages()
		generateLookups()
	}
	
	void clean() {
		new File(getOutputFolder()).deleteDir()
	}

	protected void generateTables() {
		metaManager.elementMap.values().findAll({it instanceof Table}).each {
			tableDocumentationStep.setTable(it)
			tableDocumentationStep.execute()
		}
	}
	
	protected void generateMappings() {
		metaManager.elementMap.values().findAll({it instanceof Mapping}).each {
			mappingDocumentationStep.setMapping(it)
			mappingDocumentationStep.execute()
		}
	}
	
	protected void generateLookups() {
		metaManager.elementMap.values().findAll({it instanceof Lookup}).each {
			lookupDocumentationStep.setLookup(it)
			lookupDocumentationStep.execute()
		}
	}
	
	protected void generatePackages() {
		metaManager.packageContent.keySet().each {
			packageFrameStep.setPackageName(it)
			packageFrameStep.execute()
			packageSummaryStep.setPackageName(it)
			packageSummaryStep.execute()
		}
	}
}
