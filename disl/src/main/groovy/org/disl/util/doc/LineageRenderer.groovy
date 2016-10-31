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

import org.disl.meta.Base

@CompileStatic
class LineageRenderer {
	
	List<String> sources=[]
	List<String> targets=[]
	String elementClassName
	
	LineageRenderer(){	
	}
	
	LineageRenderer(MetaManager metaManager,String elementClassName) {
		this.elementClassName=elementClassName
		Set<String> baseSources=metaManager.sourceUsage.get(elementClassName)
		Set<String> baseTargets=metaManager.targetUsage.get(elementClassName)
		if (baseSources) {
			baseSources.each {
				targets.add(it)
			}
		}
		if (baseTargets) {
			baseTargets.each {
				sources.add(it)
			}
		}
	}
	
	static String renderContainer(String className){
		"""\
<H2>Lineage</H2>
<iframe src="${className}-lineage.html"></iframe>
"""
	}
	
	String renderLineage() {
		"""\
<pre><code>
${new RowRenderer().toString()}</code></pre>"""
	}
	
	class RowRenderer {
		int rowCount=Math.max(sources.size(), targets.size())
		int sourcesLength=maxLength(sources)
		int targetsLength=maxLength(targets)
		
		int maxLength(List<String> list) {
			int maxLength=0
			list.each {
					maxLength=Math.max(maxLength, MetaManager.getElementName(it).length())			
			}
			maxLength
		}
		
		String toString() {
			(1..rowCount).collect({renderRow(it)}).join(null) 
		}
		
		String renderRow(int row) {
			"${valueAt(sources,sourcesLength,row)} ${valueAt(sources.size(),'-->',row)} ${valueAt(1,MetaManager.getElementName(elementClassName),row)} ${valueAt(targets.size(),'-->',row)} ${valueAt(targets,targetsLength,row)}\n"
		}
		
		String valueAt(List<String> list, int maxLength,int row) {
			int firstRowIndex=(int)(rowCount-list.size())/2
			int lastRowIndex=firstRowIndex+list.size()-1
			if (row>=firstRowIndex+1 && row<=lastRowIndex+1) {
				return link(list.get(row-firstRowIndex-1),maxLength)
			}
			return ''.padRight(maxLength)
		}
		
		String valueAt(int listSize,String value,int row) {
			int firstRowIndex=(int)(rowCount-listSize)/2
			int lastRowIndex=firstRowIndex+listSize-1
			if (row>=firstRowIndex+1 && row<=lastRowIndex+1) {
				return value
			}
			return ''.padRight(value.length())
		}
		
		String link(String elementClassName,int maxLength=elementClassName.length()) {
			"<a href='${elementClassName}.html' target='_parent'>${MetaManager.getElementName(elementClassName)}</a>${''.padRight(maxLength-MetaManager.getElementName(elementClassName).length())}"
		}
		
		
		String connector(List<Base> list, int maxLength,int row) {
			
		}

		
	}
	
	
	

}
