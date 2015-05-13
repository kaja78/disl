package org.disl.db.oraclebi

import org.disl.meta.PhysicalSchema


class OracleBISchema extends PhysicalSchema {
	String host
	int port=9703

	
	OracleBISchema() {
		jdbcDriver="oracle.bi.jdbc.AnaJdbcDriver"
	}
	
	public String getJdbcUrl() {
		"jdbc:oraclebi://${getHost()}:${getPort()}/"
	}
	
}
