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
package org.disl.util.doc.step

import org.disl.meta.Lookup
import org.disl.util.doc.LineageRenderer
import org.disl.util.doc.IDocumentationStep.ILookupDocumentationStep

class DefaultLookupDocumentationStep extends AbstractDocStep implements ILookupDocumentationStep {

	Lookup lookup

	@Override
	protected String getFileName() {
		"model/${lookup.class.name}.html"
	}

	@Override
	public String getCode() {
"""\
<link rel="stylesheet" type="text/css" href="../disldoc.css">
${lookup.class.name}
<H1>Lookup ${lookup.name} (${lookup.schema})</H1> 

${LineageRenderer.renderContainer(lookup.class.name)}

<H2>Lookup data</H2>
<table>
<tr>${lookup.getColumns().collect({"<th>${it.name}</th>"}).join()}</tr>
${lookup.getRecords().collect({"""<tr>${it.collect({"<td>${it}</td>"}).join()}</tr>"""}).join()}
</table>

"""
	}

}
