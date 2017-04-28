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

import org.disl.meta.Mapping
import org.disl.meta.TableMapping
import org.disl.pattern.FileOutputStep


/**
 * Generate markdown wiki page file with Mapping documentation.
 */
class MappingPageStep extends FileOutputStep {

    Mapping mapping

    @Override
    File getFile() {
        return WikiHelper.getWikiPageFile(mapping)
    }

    @Override
    String getCharset() {
        'utf-8'
    }


    String getCode() {
        """\
+++
    title= "${mapping.name}"
    packages=["${mapping.getClass().getPackage().getName().replace('.','/')}"]
    schemas=["${mapping.getSchema()}"]
+++

${WikiHelper.renderElementDescription(mapping)}

${targetSection}
${mapping.pattern ? "Pattern: ${mapping.pattern.class.simpleName}" : ''}

${WikiHelper.renderDataLineage(mapping)}

<table>
<tr><th>Name</th><th>Expression</th><th>Description</th></tr>
${mapping.columns.collect({"<tr><td>$it.alias</td><td><code><pre>$it.expression</pre></code></td><td>${WikiHelper.renderColumnDescription(it.description)}</td></tr>\n"}).join()}
</table>

$sources

##  Filter
<pre><code>${mapping.filter}</code></pre>

## SQL
<pre><code>${mapping.getSQLQuery()}</code></pre>
"""
    }
    String getTargetSection() {
        if (mapping instanceof TableMapping) {
            return """\
## Target:  [${mapping.target}](${WikiHelper.url(mapping.target)})"""
        }
        return ''
    }

    String getSources() {"""\
<H2>Sources</H2>
<table>
<tr><th>Alias</th><th>Name</th><th>Join type</th><th>Join condition</th></tr>
${mapping.sources.collect {
        "<tr><td>${it.sourceAlias}</td><td>[${it.name}](${WikiHelper.url(it)})</td><td>${it.join.class.simpleName}</td><td><pre><code>${it.join.condition}</code></pre></td></tr>\n"
    }.join()
    }
${mapping.setOperations.collect {
        "<tr><td>${it.source.sourceAlias}</td><td>[${it.source.name}](${WikiHelper.url(it.source)})</td><td>${it.class.simpleName}</td><td></td></tr>\n"
    }.join()}
</table>
"""
    }
}
