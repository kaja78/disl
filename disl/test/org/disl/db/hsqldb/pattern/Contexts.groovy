package org.disl.db.hsqldb.pattern

import org.disl.db.hsqldb.HsqldbSchema
import org.disl.meta.Context
import org.disl.meta.PhysicalSchema

class Contexts {

	public static final String L0="L0"
	
	public static final Context DEV=new Context() {
		
		@Override
		protected Map<String, PhysicalSchema> initSchemaMap() {
			
			[L0 : new HsqldbSchema()]
		}
	}
}
