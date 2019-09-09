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
package org.disl.util.doc.step

import java.io.File;

import org.disl.meta.Table
import org.disl.pattern.FileOutputStep
import org.disl.util.doc.LineageRenderer;
import org.disl.util.doc.IDocumentationStep.ITableDocumentationStep

class DefaultTableDocumentationStep extends AbstractDocStep implements ITableDocumentationStep {
	
	Table table
	
	public String getFileName() {
		"model/${table.class.name}.html"
	}

	@Override
	public String getCode() {
"""\
<link rel="stylesheet" type="text/css" href="../disldoc.css">
${table.class.name}
<H1>Table ${table.name} (${table.schema})</H1> 
${table.description}

${LineageRenderer.renderContainer(table.class.name)}

<H2>Columns</H2>
<table>
<tr><th>Name</th><th>Data type</th><th>Decription</th></tr>
${table.columns.collect({"""<tr><td class="nowrap">$it.name</td><td class="nowrap">$it.dataType</td><td>$it.description</td></tr>\n"""}).join()}
</table>
"""	}
	
	
	

	

}
