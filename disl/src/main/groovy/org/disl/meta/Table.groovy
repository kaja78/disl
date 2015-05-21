package org.disl.meta

import java.lang.reflect.Field

import org.disl.pattern.Executable
import org.disl.pattern.Pattern
import org.junit.Before


abstract class Table extends MappingSource implements Initializable, Executable {
	public abstract String getSchema()
	
	List columns=[]	
	String description
	List<IndexMeta> indexes=[]
	List<Column> primaryKeyColumns=[]
	List uniqueKeys=[]
	List<ForeignKeyMeta> foreignKeys=[]
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
		//"${ownerPrefix}${name}"
		"${name}@src"
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
		
		Description desc=this.getClass().getAnnotation(Description)
		if (desc!=null) {
			description=desc.value()
		}
		
		Collection<Index> inds=this.getClass().getAnnotations().findAll {Index.class.equals(it.annotationType())}
		inds.each {initIndex(it)}		
	}
	
	protected void initIndex(Index index) {
		IndexMeta indexMeta=new IndexMeta(columns: index.columns().collect({this[it]}))
		indexes.add(indexMeta)
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
		
		PrimaryKey primaryKey=f.getAnnotation(PrimaryKey)
		if (primaryKey!=null) {
			primaryKeyColumns.add(column)
		}

		DefaultMapping defaultMapping=f.getAnnotation(DefaultMapping)
		if (defaultMapping!=null) {
			column.setDefaultMapping(defaultMapping.value())
		}
				
		ForeignKey foreignKey=f.getAnnotation(ForeignKey)
		if (foreignKey!=null) {
			foreignKeys.add(new ForeignKeyMeta(
				sourceColumn: column.name,
				targetTable: MetaFactory.create(foreignKey.targetTable()),
				targetColumn: foreignKey.targetColumn()
				))
		}
	}
	
	public Iterable<String> getColumnDefinitions() {
		columns.collect {it.columnDefinition}
	}
	
	static class UniqueKeyMeta {
		String name
		List columns=[]
	}	
	
	static class ForeignKeyMeta {
		String name
		String sourceColumn 
		Table targetTable
		String targetColumn
	}
	
	static class IndexMeta {
		List<Column> columns
		
		public List<String> getColumnNames() {
			return columns.collect({it.name})
		}
	}
}
