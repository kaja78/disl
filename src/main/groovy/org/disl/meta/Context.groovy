/*
 * Copyright 2015 - 2016 Karel H�bl <karel.huebl@gmail.com>.
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

import groovy.sql.Sql
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode;

import java.util.logging.Level

import org.disl.db.reverseEngineering.ReverseEngineeringService
/**
 * <p>Abstraction of execution environment. 
 * Context maps logical resource name to physical deployment.
 * Each context is defined by configuration file [context name].context.properties. Configuration file must be available on classpath.
 * Default context name is "default".</p>
 * Currently the only supported resource is database schema.
 * Context may be also used to define properties, which may be used to parametrize DISL processes. Context properties may be defined in configuration file. 
 * Context property values may be overriden by values of system properties.
 * Environment variables are available as context properties with env. prefix.
 * For exmple PATH environment variable is available as env.PATH context property.
 * Global properties are defined in file global.context.properties. Global properties are valid in all contexts.
 * The value of global property may be overiden in context configuration file [context name].context.properties.
 * Context configuration files may be stored in DISL home directory and on classpath.
 * DISL home directory is defined by disl.home system variable.
 * If disl.home system variable is not specified and .disl directory from current working folder is used.
 * If .disl directory does not exists in current working folder, user.home/.disl directory is used.
 * <p>Order of context variable value definitions:</p>
 * <ol>System property</ol>
 * <ol>Environment variable (for variables starting with env.)</ol>
 * <ol>Context configuratuon file in disl.home directory.</ol>
 * <ol>Context configuration file in classpath.</ol>
 * <ol>Global context configuration file in classpath.</ol>
 * <ol>Global context configuration file in disl.home directory.</ol>
 * <p>
 * Intention of variable definition options:
 * <li>Use system properties to override any value in runtime.</li>
 * <li>Use configuration files in disl.home to define sensitive information (passwords), which should not be stored in project source code.</li>
 * <li>Use global properties to define variable calues common to multiple contexts.</li>
 * </p>
 * */
@CompileStatic
public class Context implements Cloneable {

	public static final String CONTEXT_DEFAULT="default"
	public static final String EXECUTION_MODE_DEFAULT="default"
	public static final String GLOBAL_CONTEXT_CONFIG_FILENAME = 'global.context.properties'

	public static final String DISL_HOME_PROPERTY="disl.home"
	public static final String CONTEXT_PROPERTY="disl.context"
	public static final String EXECUTION_MODE_PROPERTY="disl.executionMode"

	public static String contextClassName='org.disl.meta.Context'

	String name
	String executionMode
	Properties config
	Map<String,PhysicalSchema> schemaMap=new HashMap<String, PhysicalSchema>();


	static {
		disableGroovySqlLogging()
	}
	
	@CompileStatic(TypeCheckingMode.SKIP)
	protected static void disableGroovySqlLogging() {
		Sql.LOG.setLevel(Level.SEVERE)
	}
	
	private final static ThreadLocal<String> localContextName=new ThreadLocal<String>() {
		@Override
		protected String initialValue() {
			System.getProperty(CONTEXT_PROPERTY, CONTEXT_DEFAULT)
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
			localContext.set(createContext())
		}
		localContext.get()
	}

	private static Context createContext() {
		Context context=(Context)Class.forName(contextClassName).newInstance()
		context.setName(getContextName())
		context.setExecutionMode(context.getProperty(EXECUTION_MODE_PROPERTY,EXECUTION_MODE_DEFAULT))
		return context
	}

	public static void init(Context parentContext) {
		Context currentContext=localContext.get()
		if (currentContext==null || !parentContext.getName().equals(currentContext.getName())) {
			localContext.set((Context)parentContext.clone())
			localContextName.set(parentContext.getName())
		}
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

	public static ReverseEngineeringService getReverseEngineeringService(String logicalSchemaName) {
		ReverseEngineeringService res=getContext().getPhysicalSchema(logicalSchemaName).getReverseEngineeringService()
		res.setLogicalSchemaName(logicalSchemaName)
		return res
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
		PhysicalSchema schema=(PhysicalSchema)Class.forName(schemaClassName).newInstance()
		schema.name=schemaName
		schema.init()
		schemaMap[schemaName]=schema
	}

	protected Properties getConfig() {
		if (config==null) {
			config = loadConfigFromFile()
		}
		config
	}

	/**
	 *
	 * */
	protected Properties loadConfigFromFile() {
		config = new Properties()
		String configFileName="${name}.context.properties"
		checkConfig(configFileName)
		loadConfigFromResource(GLOBAL_CONTEXT_CONFIG_FILENAME)
		loadConfigFromFile(new File(dislHomeDirectory, GLOBAL_CONTEXT_CONFIG_FILENAME))
		loadConfigFromResource(configFileName)
		loadConfigFromFile(new File(dislHomeDirectory, configFileName))
		return config
	}

	protected void checkConfig(String configFileName) {
		if (getClass().getResource("/${configFileName}")==null) {
			throw new RuntimeException("Context configuration file ${configFileName} not available on classpath.")
		}
	}

	protected void loadConfigFromFile(File file) {
		if (file.exists()) {
			config.load(new FileInputStream(file))
		}
	}
	protected void loadConfigFromResource(String name) {
		String resourcePath="/${name}"
		if (getClass().getResource(resourcePath)) {
			config.load(getClass().getResourceAsStream(resourcePath))
		}
	}

	protected File getDislHomeDirectory() {
		new File(System.getProperty(DISL_HOME_PROPERTY,getDefaultDislHome()))
	}

	protected String getDefaultDislHome() {
		File projectDislHome=new File('.disl')
		if (projectDislHome.isDirectory()) {
			return projectDislHome
		}
		File parentDislHome=new File('../.disl')
		if (parentDislHome.isDirectory()) {
			return parentDislHome
		}
		return "${System.getProperty('user.home')}/.disl"
	}

	public String getProperty(String key) {
		Object systemProperty=System.getProperties().get(key)
		if (systemProperty!=null) {
			return systemProperty.toString()
		}
		if (key.startsWith('env.')) {
			String environmentVariable=System.getenv(key.substring(4))
			if (environmentVariable!=null) {
				return environmentVariable
			}
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

	@Override
	public Context clone() throws CloneNotSupportedException {
		Context clone=(Context)super.clone()
		clone.setSchemaMap(new HashMap<String, PhysicalSchema>())
		return clone
	}
}
