import groovy.sql.GroovyResultSet
import groovy.sql.GroovyResultSetProxy
import groovy.sql.Sql

import java.sql.ResultSet

import org.disl.meta.Context


Sql sql=Context.getSql("L2")
ResultSet res=sql.getConnection().getMetaData().getTables(null, null, "%TABLE%", null)
GroovyResultSet gRes=new GroovyResultSetProxy(res).getImpl()
gRes.eachRow {processRow(it)}


public void processRow(def row) {
	println row
}