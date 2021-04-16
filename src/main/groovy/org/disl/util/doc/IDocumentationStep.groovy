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

import org.disl.meta.Base
import org.disl.meta.Lookup
import org.disl.meta.Mapping
import org.disl.meta.Table
import org.disl.workflow.Job

interface IDocumentationStep {
	
	void execute()
	void simulate()	
	DocGenerator getDocGenerator()
	void setDocGenerator(DocGenerator docGenerator)
	
	static interface ITableDocumentationStep extends IDocumentationStep {
		void setTable(Table table)
	}
	
	static interface IMappingDocumentationStep extends IDocumentationStep {
		void setMapping(Mapping mapping)
	}
	
	static interface ILookupDocumentationStep extends IDocumentationStep {
		void setLookup(Lookup lookup)
	}
	
	static interface IPackageDocumentationStep extends IDocumentationStep {
		void setPackageName(String packageName)
	}
	
	static interface IJobDocumentationStep extends IDocumentationStep {
		void setJob(Job job)
	}
	
	static interface ILineageStep extends IDocumentationStep {
		void setElementClassName(String elementClassName)
	}
		
}