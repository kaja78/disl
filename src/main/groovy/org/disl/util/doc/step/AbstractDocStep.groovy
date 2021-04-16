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
	
	String link(String className,String urlPrefix='') {
		DocGenerator.link(className,urlPrefix)
	}
	
	String link(Base base,String urlPrefix='') {
		DocGenerator.link(base.class.name,urlPrefix)
	}

}
