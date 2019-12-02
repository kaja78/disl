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
package org.disl.util.wiki.visjs

import org.disl.meta.ForeignKeyMeta
import org.disl.meta.MappingSource
import org.disl.meta.Table

/**
 * Representation of vis.js network Edge.
 */
class Edge {

    String from
    String to
    String label = ''
    String color = 'blue'
    String arrows = 'to'
    String dashes = false
    String title

    static Edge foreignKey(Table table, ForeignKeyMeta fk) {
        new Edge(
                to: fk.targetTable.class.name,
                from: table.class.name,
                label: fk.name,
                title: "${fk.toString()}.${fk.sourceColumns}=${fk.targetTable.toString()}.${fk.targetColumns}")
    }

    static Edge mappingSource(MappingSource src, MappingSource target) {
        new Edge(
                from: src.class.name,
                to: target.class.name,
                label: src.sourceAlias,
                title: (src.join?src.join.condition:null)
        )
    }

    static Edge mappingTarget(MappingSource src, MappingSource target) {
        new Edge(
                to: target.class.name,
                from: src.class.name,
                label: 'target',
                color: 'red'
        )
    }

}
