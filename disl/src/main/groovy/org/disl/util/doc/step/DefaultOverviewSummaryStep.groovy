package org.disl.util.doc.step

class DefaultOverviewSummaryStep extends AbstractDocStep {

	@Override
	protected String getFileName() {
		'overview-summary.html'
	}

	@Override
	public String getCode() {
"""\
<link rel="stylesheet" type="text/css" href="disldoc.css">
<H2>Packages</H2>
${metaManager.packageContent.keySet().collect({
	"""<li><a href="model/${it}-summary.html" target="main">${it}</a></li>\n"""
}).join()}
"""
	}

}
