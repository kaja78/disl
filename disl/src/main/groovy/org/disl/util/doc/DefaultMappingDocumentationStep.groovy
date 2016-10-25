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

import static org.disl.util.doc.DocGenerator.link

import org.disl.meta.Mapping
import org.disl.meta.TableMapping
import org.disl.pattern.FileOutputStep
import org.disl.util.doc.IDocumentationStep.IMappingDocumentationStep

class DefaultMappingDocumentationStep extends FileOutputStep implements IMappingDocumentationStep {
	
	Mapping mapping
	DocGenerator docGenerator
	
	MetaManager getMetaManager() {
		docGenerator.getMetaManager()
	}

	@Override
	public File getFile() {
		return new File(docGenerator.outputFolder,"${mapping.class.name}.html")
	}

	@Override
	public String getCode() {
"""\
${mapping.class.name}
<H1>${mapping.name}</H1> 
${mapping.description}

${lineage}

${targetSection}

<H2>Columns</H2>
<table>
<tr><td>Name</td><td>Expression</td><td>Description</td></tr>
${mapping.columns.collect({"<tr><td>$it.alias</td><td>$it.expression</td><td>$it.description</td></tr>\n"}).join()}
</table>

$sources

<H2>Filter</H2>
${mapping.filter}

<H2>SQL</H2>
<pre><code>
${mapping.getSQLQuery()}
</code></pre>
"""	}
	
	String getSources() {"""\
<H2>Sources</H2>
<table>
<tr><td>Alias</td><td>Name</td><td>Join type</td><td>Join condition</td></tr>
${mapping.sources.collect {
			"<tr><td>${it.sourceAlias}</td><td>${link(it)}</td><td>${it.join.class.simpleName}</td><td>${it.join.condition}</td></tr>\n"
		}.join()		
}
${mapping.setOperations.collect {
			"<tr><td>${it.source.sourceAlias}</td><td>${link(it.source)}</td><td>${it.class.simpleName}</td><td></td></tr>\n"
		}.join()}
</table>
"""
	}
	
	String getTargetSection() {
		if (mapping instanceof TableMapping) {
			return """\
<H2>Target:  ${link(mapping.target)}</H2>"""
		}
		return ''
	}
	
	
	String getLineage() {
		"""\
<H2>Linage</H2>
<pre><code>
${new LineageRenderer(metaManager,mapping).toHtml()}
</code></pre>
"""
	}

}
