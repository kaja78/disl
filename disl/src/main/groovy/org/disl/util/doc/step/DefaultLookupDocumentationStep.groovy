package org.disl.util.doc.step

import org.disl.meta.Lookup
import org.disl.util.doc.IDocumentationStep.ILookupDocumentationStep;

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
<H1>${lookup.name} (${lookup.schema})</H1> 

<table>
<tr>${lookup.getColumns().collect({"<th>${it.name}</th>"}).join()}</tr>
${lookup.getRecords().collect({"""<tr>${it.collect({"<td>${it}</td>"}).join()}</tr>"""}).join()}
</table>

"""
	}

}
