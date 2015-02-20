package org.disl.meta;

import groovy.sql.Sql;

public abstract class Context {
	
	protected static ThreadLocal<Context> localContext=new ThreadLocal<Context>();
	
	Map<String,PhysicalSchema> schemaMap=initSchemaMap();
	
	protected abstract Map<String,PhysicalSchema> initSchemaMap();
	
	public static void set(Context context) {
		localContext.set(context)
	}
	
	public static Context get() {
		localContext.get()
	}
	
	Sql getSql(String schema) {
		schemaMap[schema].getSql()
	}

}
