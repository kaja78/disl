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

import org.disl.meta.Lookup
import org.disl.meta.Mapping
import org.disl.meta.Table
import org.disl.meta.TableMapping
import org.disl.util.wiki.visjs.Edge
import org.disl.util.wiki.visjs.LineageNode
import org.disl.util.wiki.visjs.Node

/**
 * Helper for building lineage model for vis.js network visualisation. See http://visjs.org/docs/network/.
 */
class MappingLineageNetwork {

    private int maxDepth = 10
    Set<LineageNode> nodes = new HashSet<LineageNode>();
    List<Edge> edges = [];

    MappingLineageNetwork(Table table) {
        initLineage(table,1)
    }

    MappingLineageNetwork(Mapping mapping) {
        if (mapping instanceof TableMapping) {
            Node target=new LineageNode(mapping.target,0)
            nodes.add target
            edges.add Edge.mappingTarget(mapping, mapping.target)
        }
        initLineage(mapping,1)
    }

    boolean initLineage(Mapping mapping, int depth) {
        if (depth > maxDepth) {
            return false
        }
        Node node = new LineageNode(mapping,depth)
        if (depth == 1) {
            node.color = 'Red'
        }
        mapping.sources.each {
            edges.add Edge.mappingSource(it, mapping)
            initLineage(it,depth+1)
        }

        mapping.setOperations.each {
            edges.add Edge.mappingSource(it.getSource(), mapping)
            initLineage(it.getSource(),depth+1)
        }
        return nodes.add(node)
    }

    boolean initLineage(Table table, int depth) {
        if (depth > maxDepth) {
            return false
        }
        return nodes.add(new LineageNode(table,depth))

    }

    boolean initLineage(Lookup lookup, int depth) {
        if (depth > maxDepth) {
            return false
        }
        return nodes.add(new LineageNode(lookup,depth))

    }

}
