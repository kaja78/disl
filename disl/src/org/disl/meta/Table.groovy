package org.disl.meta

import java.lang.reflect.Field

import org.disl.pattern.Pattern
import org.junit.Before


abstract class Table extends MappingSource implements Initializable {
	public abstract String getSchema()
	
	List columns=[]
	String description	
	List primaryKeyColumns=[]
	List uniqueKeys=[]
	List foreignKeys=[]
	Pattern pattern
	
	@Override
	public String getRefference() {		
		String alias=""		
		if (sourceAlias!=null) {
			alias=" ${sourceAlias}"
		}		
		return "${fullName}${alias}"
	}
	
	public String getFullName() {
		String ownerPrefix=""		
		String owner=getPhysicalSchema()
		if (owner!=null) {
			ownerPrefix="${owner}."
		}		
		"${ownerPrefix}${name}"
	}
	
	public String getPhysicalSchema() {
		Context.getPhysicalSchemaName(getSchema())
	}

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
	
	@Before
	@Override
	public void init() {
		initColumns()
		initConstraints()
		
		
	}
	
	protected void initColumns() {
		getFieldsByType(Column).each {initColumn(it)}		
	}
	
	protected void initConstraints() {
		getClass().getFields().findAll({it.getAnnotation(UniqueKey)!=null})
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
		
		DataType dataType=f.getAnnotation(DataType)
		if (dataType!=null) {
			column.setDataType(dataType.value())
		}
	}
	
	public Iterable<String> getColumnDefinitions() {
		columns.collect {it.columnDefinition}
	}

	class UniqueKeyMeta {
		String name
		List columns=[]
	}	
	
	class ForeignKeyMeta {
		String name
		Table targetTable
		String targetColumn
	}
}
