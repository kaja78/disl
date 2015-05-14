package org.disl.db.oraclebi

import org.disl.pattern.ExecuteSQLQueryStep
import org.junit.Test

class OBIPurgeAllCache extends ExecuteSQLQueryStep {

		String pattern="Call SAPurgeAllCache()"
		
		@Override
		public void execute() {
			super.execute();
			//Open OBI connection blocks JVM shutdown.
			getSql().close()
		}

}
