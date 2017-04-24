/*
 * Copyright 2015 - 2017 Karel Hübl <karel.huebl@gmail.com>.
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
package org.disl.util.wiki

import org.disl.meta.Lookup
import org.disl.pattern.FileOutputStep

/**
 * Generate markdown wiki page file with Lookup documentation.
 */
class LookupPageStep extends FileOutputStep {
    Lookup lookup

    @Override
    File getFile() {
        return WikiHelper.getWikiPageFile(lookup)
    }

    @Override
    String getCharset() {
        'utf-8'
    }

    @Override
    public String getCode() {
        """\
+++
    title= "${lookup.name}"
    packages=["${lookup.getClass().getPackage().getName().replace('.','/')}"]
    schemas=["${lookup.getSchema()}"]
+++
${WikiHelper.renderElementDescription(lookup)}

<H2>Columns</H2>
<table>
<tr><th>Name</th><th>Data type</th><th>Decription</th></tr>
${lookup.columns.collect({"""<tr><td class="nowrap">$it.name</td><td class="nowrap">$it.dataType</td><td>${WikiHelper.renderColumnDescription(it.description)}</td></tr>\n"""}).join()}
</table>
"""	}

}
