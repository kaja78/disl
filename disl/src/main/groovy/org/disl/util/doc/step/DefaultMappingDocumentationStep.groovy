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

import org.disl.meta.Mapping
import org.disl.meta.TableMapping
import org.disl.pattern.FileOutputStep
import org.disl.util.doc.LineageRenderer;
import org.disl.util.doc.IDocumentationStep.IMappingDocumentationStep

class DefaultMappingDocumentationStep extends AbstractDocStep implements IMappingDocumentationStep {
	
	Mapping mapping
	
	public String getFileName() {
		"model/${mapping.class.name}.html"
	}

	@Override
	public String getCode() {
"""\
<link rel="stylesheet" type="text/css" href="../disldoc.css">
${mapping.class.name}
<H1>${mapping.name}</H1>
${mapping.pattern?"Pattern: ${mapping.pattern.class.simpleName}":''} 
${mapping.description}

${lineage}

${targetSection}

<H2>Columns</H2>
<table>
<tr><th>Name</th><th>Expression</th><th>Description</th></tr>
${mapping.columns.collect({"<tr><td>$it.alias</td><td><code><pre>$it.expression</pre></code></td><td>$it.description</td></tr>\n"}).join()}
</table>

$sources

<H2>Filter</H2>
<div><pre><code>${mapping.filter}</code></pre></div>

<H2>SQL</H2>
<textarea>
${mapping.getSQLQuery()}
</textarea>
"""	}
	
	String getSources() {"""\
<H2>Sources</H2>
<table>
<tr><th>Alias</th><th>Name</th><th>Join type</th><th>Join condition</th></tr>
${mapping.sources.collect {
			"<tr><td>${it.sourceAlias}</td><td>${link(it)}</td><td>${it.join.class.simpleName}</td><td><pre><code>${it.join.condition}</code></pre></td></tr>\n"
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
<div>
<pre><code>
${new LineageRenderer(metaManager,mapping).toHtml()}
</code></pre>
</div>
"""
	}

}
