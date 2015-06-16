package org.disl.meta;

import java.util.List;

class IndexMeta {
	List<Column> columns
	
	public List<String> getColumnNames() {
		return columns.collect({it.name})
	}
	
	public static initIndexes(IndexOwner object) {
		Collection<Index> inds=object.getClass().getAnnotations().findAll {Index.class.equals(it.annotationType())}
		inds.each {initIndex(it,object)}

	}
	
	protected static void initIndex(Index index, IndexOwner object) {
		IndexMeta indexMeta=new IndexMeta(columns: index.columns().collect({object[it]}))
		object.indexes.add(indexMeta)
	}
	
	interface IndexOwner {
		List<IndexMeta> getIndexes();
	}
}