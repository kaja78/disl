/*
 * Copyright 2015 - 2017 Karel Hübl <karel.huebl@gmail.com>.
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
package org.disl.db.oracleimport groovy.sql.Sqlimport org.disl.db.reverseEngineering.ReverseEngineeringServiceimport org.disl.meta.Table/** * Oracle specific ReverseEngineeringService implementation. * */class OracleReverseEngineeringService extends ReverseEngineeringService {	boolean useCharLength=true	@Override	protected String getDataType(String dataTypeName, BigDecimal size,			BigDecimal decimalDigits, BigDecimal sqlDataType) {		if (isUseCharLength() && (dataTypeName=="VARCHAR2" || dataTypeName=="CHAR")) {			return "${dataTypeName}(${size} CHAR)"		}		return super.getDataType(dataTypeName, size, decimalDigits, sqlDataType);	}	@Override	public List<Table> reverseEngineerTables(Sql sql, String tablePattern,			String tableTypes, String sourceSchemaFilterPattern,String catalog=null) {		List<Table> tables=super.reverseEngineerTables(sql, tablePattern, tableTypes,sourceSchemaFilterPattern,catalog)		tables.each { initDescription(sql,it) }		return tables	}				protected void initDescription(Sql sql,Table table) {		sql.eachRow("select comments from all_tab_comments where owner=${table.physicalSchema} and table_name=${table.getName()}",{row -> table.description=row.COMMENTS})		sql.eachRow("select column_name,comments from all_col_comments where owner=${table.physicalSchema} and table_name=${table.getName()}",{row -> table.columns.find({it.getName()==row.COLUMN_NAME}).description=row.COMMENTS})			}}