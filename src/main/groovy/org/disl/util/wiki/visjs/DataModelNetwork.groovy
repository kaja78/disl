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
package org.disl.util.wiki.visjs

import org.disl.meta.Table

/**
 * Helper for building data model for vis.js network visualisation. See http://visjs.org/docs/network/.
 */
class DataModelNetwork {

    Set<Node> nodes = new HashSet<Node>();
    List<Edge> edges = [];

    public DataModelNetwork(){}

    public DataModelNetwork(Table table) {
        add(table)
    }
    void add(Table table) {
        if (nodes.add(new Node(table))) {
            table.foreignKeys.each {
                add(it.targetTable)
                edges.add(Edge.foreignKey(table,it))
            }
        }
    }

}
