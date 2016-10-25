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

//@CompileStatic
class LineageRenderer {
	
	List<Base> sources=[]
	List<Base> targets=[]
	Base element
	
	LineageRenderer(){	
	}
	
	LineageRenderer(MetaManager metaManager,Base base) {
		this.element=base
		Set<String> baseSources=metaManager.sourceUsage.get(base.class.name)
		Set<String> baseTargets=metaManager.targetUsage.get(base.class.name)
		if (baseSources) {
			baseSources.each {
				targets.add(metaManager.elementMap[it])
			}
		}
		if (baseTargets) {
			baseTargets.each {
				sources.add(metaManager.elementMap[it])
			}
		}
	}
	
	
	

	
	String toHtml() {
		new RowRenderer().toString()
	}
	
	class RowRenderer {
		int rowCount=Math.max(sources.size(), targets.size())
		int sourcesLength=maxLength(sources)
		int targetsLength=maxLength(targets)
		
		int maxLength(List<Base> list) {
			int maxLength=0
			list.each {
					maxLength=Math.max(maxLength, it.name.length())			
			}
			maxLength
		}
		
		String toString() {
			(1..rowCount).collect({renderRow(it)}).join() 
		}
		
		String renderRow(int row) {
			"${valueAt(sources,sourcesLength,row)} ${valueAt(sources.size(),'-->',row)} ${valueAt(1,element.name,row)} ${valueAt(targets.size(),'-->',row)} ${valueAt(targets,targetsLength,row)}\n"
		}
		
		String valueAt(List<Base> list, int maxLength,int row) {
			int firstRowIndex=(rowCount-list.size())/2
			int lastRowIndex=firstRowIndex+list.size()-1
			if (row>=firstRowIndex+1 && row<=lastRowIndex+1) {
				return link(list.get(row-firstRowIndex-1),maxLength)
			}
			return ''.padRight(maxLength)
		}
		
		String valueAt(int listSize,String value,int row) {
			int firstRowIndex=(rowCount-listSize)/2
			int lastRowIndex=firstRowIndex+listSize-1
			if (row>=firstRowIndex+1 && row<=lastRowIndex+1) {
				return value
			}
			return ''.padRight(value.length())
		}
		
		String link(Base base,int maxLength=base.name.length()) {
			"<a href='${base.class.name}.html'>${base.name}</a>${''.padRight(maxLength-base.name.length())}"
		}
		
		
		String connector(List<Base> list, int maxLength,int row) {
			
		}

		
	}
	
	
	

}
