package org.disl.meta

import java.lang.reflect.Field

import org.disl.pattern.Pattern


abstract class Table extends MappingSource implements Initializable {
	String schema
	List columns=[]
	Pattern pattern
	
	protected Column c(String name) {
		createColumn(name)
	}
	
	private Column createColumn(String name) {
		Column c=new Column(name, this)
		columns.add(c)
		c
	}
	
	public void execute() {
		getPattern().execute()
	}
	
	public void simulate() {
		getPattern().simulate()
	}
	
	@Override
	public void init() {
		initColumns()
		
	}
	
	protected void initColumns() {
		getFieldsByType(Column).each {initColumn(it)}		
	}
	
	protected void initColumn(Field f) {
		Column column=this[f.getName()]
		
		if (column==null) {			
			column=f.getType().newInstance()
			column.name=f.getName()
			column.parent=this
			this[column.name]=column
			columns.add(column)
		}
		column.setName(column.name);
		
		Description desc=f.getAnnotation(Description)
		if (desc!=null) {
			column.setDescription(desc.value())
		}
		
		//TODO: Constraints
	}
	
	public Iterable<String> getColumnDefinitions() {
		columns.collect {it.columnDefinition}
	}
	
}
