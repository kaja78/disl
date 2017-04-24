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

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.disl.meta.*
import org.disl.util.doc.MetaManager
import org.disl.util.wiki.visjs.Edge
import org.disl.util.wiki.visjs.LineageNode

class WikiHelper extends MetaManager {

    Map<String,Set<LineageNode>> mappingLineageUsage =new HashMap<>()
    Map<String,Set<LineageNode>> tableLineageUsage =new HashMap<>()
    Map<String,Set<LineageNode>> mappingTargetUsage =new HashMap<>()

    static String wikiRootDir='../../spwr-bi-wiki'

    static String url(Table t) {
        "/data-model/${fileName(t)}"
    }

    static String url(Lookup t) {
        "/data-model/${fileName(t)}"
    }

    static String url(Mapping m) {
        "/data-mapping/${fileName(m)}"
    }

    static String url(Perspective p) {
        "/perspective/${fileName(p)}"
    }

    @Override
    protected boolean includeType(Class type) {
        (MappingSource.isAssignableFrom(type) || Perspective.isAssignableFrom(type))
    }


    static String fileName(Base base) {
        fileName(base.class.name)
    }

    static String fileName(String className) {
        "${className.toLowerCase().replace('.','/')}"
    }

    static File getDataFile(Base base) {
        new File("${wikiRootDir}/static${url(base)}.json")
    }

    static File getLineageDataFile(String className) {
        new File("${wikiRootDir}/static/data-mapping/${fileName(className)}.json")
    }

    static File getDataModelDataFile(String className) {
        new File("${wikiRootDir}/static/data-model/${fileName(className)}.json")
    }

    static File getWikiPageFile(Base base) {
        new File("${wikiRootDir}/content${url(base)}.md")
    }

    static void generate(Table table) {
        new TablePageStep(table:table).execute()
        new TableDataModelData(table:table).execute()
        new TableLineageDataStep(table:table).execute()
    }

    static void generate(Lookup lookup) {
        new LookupPageStep(lookup:lookup).execute()
    }

    static void generate(Mapping mapping) {
        new MappingPageStep(mapping:mapping).execute()
        new MappingLineageDataStep(mapping:mapping).execute()
    }

    static void generate(Perspective perspective) {
        perspective.execute()
    }

     static String renderDataModel(Table table) {
        if (table.foreignKeys.isEmpty()) {
            return ''
        }
        return """{{< datamodel "${fileName(table)}.json" >}}"""
    }

    static String renderDataModel(Perspective perspective) {
        return """{{< datamodel "${fileName(perspective)}.json" >}}"""
    }

    static String renderDataLineage(MappingSource mapping) {
        """{{< lineage "${fileName(mapping)}.json" >}}"""
    }

    void generateWiki() {
        process {
            if (it instanceof Table || it instanceof Mapping || it instanceof Perspective) {
                WikiHelper.generate(it)
            }
        }
        insertMappingLineageUsage()
        insertTableLineageUsage()
        insertMappingTargetUsage()

    }

    protected void insertMappingTargetUsage() {
        mappingTargetUsage.keySet().each { target ->
            try {
                Set<LineageNode> nodes = mappingTargetUsage.get(target)
                List<Edge> edges = nodes.collect({ node -> new Edge(from: node.id, to: target) })
                def lineageNetwork
                if (getLineageDataFile(target).exists()) {
                    lineageNetwork = new JsonSlurper().parse(getLineageDataFile(target))
                    lineageNetwork.nodes.addAll(nodes)
                    lineageNetwork.edges.addAll(edges)
                } else {
                    lineageNetwork = [nodes: nodes, edges: edges]
                }
                getLineageDataFile(target).write(new JsonBuilder(lineageNetwork).toPrettyString())
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
    }

    protected void insertTableLineageUsage() {
        tableLineageUsage.keySet().each { source ->
            try {
                Set<LineageNode> nodes = tableLineageUsage.get(source)
                List<Edge> edges = nodes.collect({ node -> new Edge(from: source, to: node.id) })
                def lineageNetwork
                if (getLineageDataFile(source).exists()) {
                    lineageNetwork = new JsonSlurper().parse(getLineageDataFile(source))
                    lineageNetwork.nodes.addAll(nodes)
                    lineageNetwork.edges.addAll(edges)
                } else {
                    lineageNetwork = [nodes: nodes, edges: edges]
                }
                getLineageDataFile(source).write(new JsonBuilder(lineageNetwork).toPrettyString())
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Insert source usage data into mapping lineage data files.
     * */
    protected void insertMappingLineageUsage() {
        mappingLineageUsage.keySet().each { source ->
            try {
                Set<LineageNode> nodes = mappingLineageUsage.get(source)
                List<Edge> edges = nodes.collect({ node -> new Edge(from: source, to: node.id) })
                def lineageNetwork
                if (getLineageDataFile(source).exists()) {
                    lineageNetwork = new JsonSlurper().parse(getLineageDataFile(source))
                    lineageNetwork.nodes.addAll(nodes)
                    lineageNetwork.edges.addAll(edges)
                } else {
                    lineageNetwork = [nodes: nodes, edges: edges]
                }
                getLineageDataFile(source).write(new JsonBuilder(lineageNetwork).toPrettyString())
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
    }


    @Override
    void addUsage(Mapping mapping) {
        mapping.sources.each {
            addLineageSourceUsage(it,mapping)
        }
        mapping.setOperations.each {
            addLineageSourceUsage(it.source,mapping)
        }
        if (mapping instanceof  TableMapping) {
            addMappingTargetUsage(mapping,mapping.getTarget())
        }
    }

    void addLineageSourceUsage(Mapping sourceMapping, Mapping mapping) {
        Set<LineageNode> l=mappingLineageUsage.get(sourceMapping.class.name)
        if (!l) {
            l=new HashSet<LineageNode>()
            mappingLineageUsage.put(sourceMapping.class.name,l)
        }
        l.add(new LineageNode(mapping,0))
    }

    void addMappingTargetUsage(Mapping sourceMapping, Table targetTable) {
        Set<LineageNode> l=mappingTargetUsage.get(targetTable.class.name)
        if (!l) {
            l=new HashSet<LineageNode>()
            mappingTargetUsage.put(targetTable.class.name,l)
        }
        l.add(new LineageNode(sourceMapping,2))
    }

    void addLineageSourceUsage(Table sourceTable, Mapping mapping) {
        Set<LineageNode> l=tableLineageUsage.get(sourceTable.class.name)
        if (!l) {
            l=new HashSet<LineageNode>()
            tableLineageUsage.put(sourceTable.class.name,l)
        }
        l.add(new LineageNode(mapping,0))
    }

    void addLineageSourceUsage(Lookup sourceLookup, Mapping mapping) {
        //do nothing for now
        //TODO: implement later
    }

    static String renderElementDescription(Base base) {
        if (base.description && !'null'.equals(base.description)) {
            if (base.description.contains('.')) {
                return base.description.replaceFirst('\\.','.\n<!--more-->\n')
            }
            return "${base.description}\n<!--more-->\n"
        }
        return '<!--more-->'
    }

    static String renderColumnDescription(String description) {
        if (description==null || description.equals('null')) {
            return ''
        }
        return description
    }
}
