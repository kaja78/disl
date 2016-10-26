package org.disl.util.doc.step

import org.disl.meta.Base
import org.disl.pattern.FileOutputStep
import org.disl.util.doc.DocGenerator;
import org.disl.util.doc.IDocumentationStep;
import org.disl.util.doc.MetaManager;

abstract class AbstractDocStep extends FileOutputStep implements IDocumentationStep {

	DocGenerator docGenerator
	
	protected abstract String getFileName();
	
	@Override
	public final File getFile() {
		new File(docGenerator.outputFolder,getFileName())
	}
	
	MetaManager getMetaManager() {
		docGenerator.getMetaManager()
	}
	
	String link(Base base,String urlPrefix='') {
		DocGenerator.link(base,urlPrefix)
	}
	
	String link(String baseClassName) {
		docGenerator.link(baseClassName)
	}

}
