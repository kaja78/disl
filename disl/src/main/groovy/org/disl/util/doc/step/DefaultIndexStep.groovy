package org.disl.util.doc.step

class DefaultIndexStep  extends AbstractDocStep {
	
	@Override
	protected String getFileName() {
		'index.html'
	}

	@Override
	public String getCode() {
		"""\
<frameset cols="20%,*">
	<frameset rows="40%,60%">
		<frame name="packages" src="overview-frame.html">
		<frame name="elements" src="allelements-frame.html">
	</frameset>
	<frame name="main" src="overview-summary.html">
</frameset>
"""
	}

}
