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

import java.io.File;

import org.disl.meta.Table
import org.disl.pattern.FileOutputStep
import org.disl.util.doc.IDocumentationStep.ITableDocumentationStep

class DefaultTableDocumentationStep extends FileOutputStep implements ITableDocumentationStep {
	
	Table table
	DocGenerator docGenerator
	
	MetaManager getMetaManager() {
		docGenerator.getMetaManager()
	}

	@Override
	public File getFile() {
		return new File(docGenerator.outputFolder,"${table.class.name}.html")
	}

	@Override
	public String getCode() {
"""\
${table.class.name}
<H1>${table.name} (${table.schema})</H1> 
${table.description}

<H2>Lineage</H2>
<pre><code>
${new LineageRenderer(metaManager,table).toHtml()}
</code></pre>
<H2>Columns</H2>
<table>
<tr><td>Name</td><td>Data type</td><td>Decription</td></tr>
${table.columns.collect({"<tr><td>$it.name</td><td>$it.dataType</td><td>$it.description</td></tr>\n"}).join()}
</table>
"""	}
	
	

	

}
