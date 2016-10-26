package org.disl.util.doc.step

class DefaultOverviewFrameStep extends AbstractDocStep {

	@Override
	protected String getFileName() {
		'overview-frame.html'
	}

	@Override
	public String getCode() {
"""\
<link rel="stylesheet" type="text/css" href="disldoc.css">
<li><a href="allelements-frame.html" target="elements">All elements</a></li>
<H2>Packages</H2>
${metaManager.packageContent.keySet().collect({
	"""<li><a href="model/${it}-frame.html" target="elements">${it}</a></li>\n"""
}).join()}
"""
	}

}
