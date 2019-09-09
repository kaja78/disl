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

import org.disl.util.doc.DocGenerator
import org.disl.util.doc.IDocumentationStep.IPackageDocumentationStep

class DefaultPackageFrameStep extends AbstractDocStep implements IPackageDocumentationStep {

	String packageName
	
	@Override
	protected String getFileName() {
		"model/${packageName}-frame.html";
	}

	@Override
	public String getCode() {
		"""\
<link rel="stylesheet" type="text/css" href="../disldoc.css">
<a href="${packageName}-summary.html" target="main">${packageName}</a>
<H2>DISL elements</H2>
${metaManager.packageContent.get(packageName).collect {
	"<li>${DocGenerator.link(it)}</li>\n"
}.join()}
"""
	}

}
