/*
 * Copyright 2015 Karel Hübl <karel.huebl@gmail.com>.
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
package org.disl.meta;

import groovy.sql.Sql;

import java.util.Map;
import java.util.Properties;
/**
 * <p>Abstraction of execution environment. Context maps logical resource name to physical deployment.
 * Each context is defined by configuration file [context name].context.properties. Default context name is "default".</p>
 * Currently the only supported resource is database schema.
 * */
public class Context {
	public static final String CONTEXT_DEFAULT="default"
	public static final String EXECUTION_MODE_DEFAULT="default"
	
	String name
	String executionMode=EXECUTION_MODE_DEFAULT
	Properties config
	Map<String,PhysicalSchema> schemaMap=new HashMap<String, PhysicalSchema>();


	private final static ThreadLocal<String> localContextName=new ThreadLocal<String>() {
		@Override
		protected String initialValue() {
			CONTEXT_DEFAULT
		}
	};

	private final static ThreadLocal<Context> localContext=new ThreadLocal<Context>();

	public static void setContextName(String newName) {
		String currentContextName=localContextName.get()
		if (!newName.equals(currentContextName)) {
			localContextName.set(newName)
			localContext.set(null)
		}
	}

	public static String getContextName() {
		localContextName.get()
	}

	public static Context getContext() {
		if (localContext.get()==null) {
			localContext.set(new Context(name: getContextName()))
		}
		localContext.get()
	}

	public static Sql getSql(String logicalSchemaName) {
		getContext().getPhysicalSchema(logicalSchemaName).getSql()
	}

	public static String getPhysicalSchemaName(String logicalSchemaName) {
		if (logicalSchemaName==null) {
			return null
		}
		return getContext().getPhysicalSchema(logicalSchemaName).getSchema()
	}
	
	public static String getContextProperty(String key) {
		return getContext().getProperty(key)
	}
	
	public static String getContextProperty(String key, String defaultValue) {
		return getContext().getProperty(key,defaultValue)
	}

	public PhysicalSchema getPhysicalSchema(String schemaName) {
		if (schemaMap[schemaName]==null) {
			createSchema(schemaName)
		}
		schemaMap[schemaName]
	}

	protected void createSchema(String schemaName) {
		String schemaType=getProperty(schemaName)
		if (schemaType==null) {
			throw new RuntimeException("schemaType not defined for schema $schemaName in context ${Context.getContextName()}");
		}
		String schemaClassName="org.disl.db.${schemaType.toLowerCase()}.${schemaType}Schema"
		PhysicalSchema schema=Class.forName(schemaClassName).newInstance()
		schema.name=schemaName
		schema.init()
		schemaMap[schemaName]=schema
	}

	protected Properties getConfig() {
		if (config==null) {
			config=new Properties()
			config.load(getClass().getResourceAsStream("/${name}.context.properties"))
		}
		config
	}

	public String getProperty(String key) {
		Object systemProperty=System.getProperties().get(key)
		if (systemProperty!=null) {
			return systemProperty.toString()
		}
		return getConfig()[key]
	}

	public String getProperty(String key,String defaultValue) {
		String value=getProperty(key)
		if (value==null) {
			value=defaultValue
		}
		return value
	}
}
