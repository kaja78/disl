package org.disl.util.doc.step

import org.disl.meta.Mapping
import org.disl.meta.Table
import org.disl.util.doc.IDocumentationStep.IPackageDocumentationStep

class DefaultPackageSummaryStep extends AbstractDocStep implements IPackageDocumentationStep {

	String packageName
	
	@Override
	protected String getFileName() {
		"model/${packageName}-summary.html";
	}

	@Override
	public String getCode() {
		"""\
<link rel="stylesheet" type="text/css" href="../disldoc.css">
<H1>Package ${packageName}</H1>
${tableSection}
${mappingSection}
"""
	}
	
	String getTableSection() {
		List<Table> tables=metaManager.getPackageElements(packageName).findAll({it instanceof Table})
		if (tables.size()>0) {
			return """\
<H2>Tables</H2>
<table>
<tr><th>Name</th><th>Description</th></tr>
${tables.collect({
	"<tr><td>${link(it)}</td><td>${it.description}</td</th>"
}).join()}
</table>
"""
		}
	}
	
	String getMappingSection() {
		List<Mapping> tables=metaManager.getPackageElements(packageName).findAll({it instanceof Mapping})
		if (tables.size()>0) {
			return """\
<H2>Mappings</H2>
<table>
<tr><th>Name</th><th>Description</th></tr>
${tables.collect({
	"<tr><td>${link(it)}</td><td>${it.description}</td</th>"
}).join()}
</table>
"""
		}
	}

}
