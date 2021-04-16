/*
 *
 * Copyright 2015 - 2021 Karel Hübl <karel.huebl@gmail.com>.
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
package org.disl.db.oracleimport groovy.sql.Sqlimport org.disl.db.reverseEngineering.ReverseEngineeringServiceimport org.disl.meta.Tableimport java.sql.PreparedStatementimport java.sql.ResultSet/** * Oracle specific ReverseEngineeringService implementation. * */class OracleReverseEngineeringService extends ReverseEngineeringService {	@Override	protected String getDataType(String dataTypeName, String size,								 BigDecimal decimalDigits, BigDecimal sqlDataType,BigDecimal charOctetLenght) {		if (size==null) {			return dataTypeName		} else if (decimalDigits==null || decimalDigits==0) {			return "${dataTypeName}(${size})"		}		return "${dataTypeName}(${size},${decimalDigits})"	}	@Override	protected ResultSet getColumnsMetadata(Sql sql, Table table) {		PreparedStatement stmt=sql.getConnection().prepareStatement("""\select				null TABLE_CAT,				col.owner TABLE_SCHEM,				col.TABLE_NAME,				col.COLUMN_NAME,				null DATA_TYPE,				col.DATA_TYPE TYPE_NAME,				CASE                    WHEN CHAR_USED='B' or col.DATA_TYPE in ('NCHAR','NVARCHAR2')  THEN TO_CHAR(CHAR_LENGTH)                    WHEN CHAR_USED='C' THEN CHAR_LENGTH||' CHAR'				                    ELSE TO_CHAR(DATA_PRECISION)				END COLUMN_SIZE,				null BUFFER_LENGTH,				DATA_SCALE DECIMAL_DIGITS,				c.COMMENTS REMARKS,				col.DATA_DEFAULT COLUMN_DEF,				null SQL_DATA_TYPE,				null SQL_DATETIME_SUB,				col.DATA_LENGTH CHAR_OCTET_LENGTH,				col.COLUMN_ID ORDINAL_POSITION,				decode(col.NULLABLE,'Y','YES','NO') IS_NULLABLE,				null SCOPE_CATALOG,				null SCOPE_SCHEMA,				null SCOPE_TABLE,				null SOURCE_DATA_TYPE,				'NO' IS_AUTOINCREMENT,				'NO' IS_GENERATEDCOLUMN				from				all_tab_columns col				left outer join all_col_comments c on (col.owner=c.owner and col.table_name=c.table_name and col.column_name=c.column_name)				where 					col.owner=:1					and col.table_name=:2				order by col.OWNER,col.TABLE_NAME,col.COLUMN_ID"""		)		stmt.setString(1,table.getPhysicalSchema())		stmt.setString(2,table.getName())		return stmt.executeQuery()	}	@Override	public List<Table> reverseEngineerTables(Sql sql, String tablePattern,			String tableType, String sourceSchemaFilterPattern,String catalog=null) {		List<Table> tables=super.reverseEngineerTables(sql, tablePattern, tableType,sourceSchemaFilterPattern,catalog)		tables.each { initDescription(sql,it) }		return tables	}				protected void initDescription(Sql sql,Table table) {		sql.eachRow("select comments from all_tab_comments where owner=${table.physicalSchema} and table_name=${table.getName()}",{row -> table.description=row.COMMENTS})	}}