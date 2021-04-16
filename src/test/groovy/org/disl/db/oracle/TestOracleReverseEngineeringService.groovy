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
package org.disl.db.oracle

import groovy.sql.GroovyResultSetProxy
import groovy.sql.Sql
import org.disl.db.ChangedContextTest
import org.disl.meta.Context
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

/**
 * Unit test for OracleReverseEngineerig service.
 * This test is ignored by default and may be run manually from IDE.
 * To run this test manualy:
 * 1) Copy Oracle JDBC driver jars into lib dir.
 * 2) Create context configuration file name oracle.db.test.context.properties in src/test/resources
 *
 * Template for context config:
 *
 default=Oracle
 default.host=localhost
 default.port=1521
 default.serviceName=orcl
 default.schema=[user]
 default.user=[user]
 default.password=[password]

 * */
@Ignore
class TestOracleReverseEngineeringService extends OracleTest {

    OracleReverseEngineeringService s
    Sql sql


    @Before
    void init() {
        super.init()
        s=Context.getReverseEngineeringService('default')
        sql=Context.getSql('default')
        sql.execute'create table DISL_TEST_ORA_REVERSE (C32CHAR char(32 char),C32 char(32),VC4000 varchar2(4000),VC4000CHAR varchar2(4000 CHAR),CDATE DATE,CTIMESTAMP TIMESTAMP,CTIMESTAMP_9 TIMESTAMP(9),CN NUMBER,CN38 NUMBER(38),CN38_2 NUMBER(38,2),NC_30 NCHAR(32),NVC2_32 NVARCHAR2(32))'
        sql.execute 'comment on table DISL_TEST_ORA_REVERSE is \'DISL reverse engineering testing table.\''
        sql.execute 'comment on column DISL_TEST_ORA_REVERSE.C32CHAR is \'Data type is CHAR(32 CHAR).\''
        sql.execute 'comment on column DISL_TEST_ORA_REVERSE.C32 is \'Data type is CHAR(32).\''
    }

    @Test
    void testReverseEngineerSchema() {
        new File("build/test/oracle/DISL_TEST_ORA_REVERSE.groovy").delete()
        s.reverseSchemaTables('oracle','DISL_TEST_ORA_REVERSE',Context.getPhysicalSchemaName('default'),new File('build/test'))
        String expected='''\
package oracle

import org.disl.meta.*

@Description("""DISL reverse engineering testing table.""")
@groovy.transform.CompileStatic
class DISL_TEST_ORA_REVERSE extends AbstractOracleTable {

\t\t@Description("""Data type is CHAR(32 CHAR).""")
\t\t@DataType("CHAR(32 CHAR)")
\t\tColumn C32CHAR

\t\t@Description("""Data type is CHAR(32).""")
\t\t@DataType("CHAR(32)")
\t\tColumn C32

\t\t
\t\t@DataType("VARCHAR2(4000)")
\t\tColumn VC4000

\t\t
\t\t@DataType("VARCHAR2(4000 CHAR)")
\t\tColumn VC4000CHAR

\t\t
\t\t@DataType("DATE")
\t\tColumn CDATE

\t\t
\t\t@DataType("TIMESTAMP(6)")
\t\tColumn CTIMESTAMP

\t\t
\t\t@DataType("TIMESTAMP(9)")
\t\tColumn CTIMESTAMP_9

\t\t
\t\t@DataType("NUMBER")
\t\tColumn CN

\t\t
\t\t@DataType("NUMBER(38)")
\t\tColumn CN38

\t\t
\t\t@DataType("NUMBER(38,2)")
\t\tColumn CN38_2

\t\t
\t\t@DataType("NCHAR(32)")
\t\tColumn NC_30

\t\t
\t\t@DataType("NVARCHAR2(32)")
\t\tColumn NVC2_32
}'''
        Assert.assertEquals(expected, new File("build/test/oracle/DISL_TEST_ORA_REVERSE.groovy").getText())
    }

    @After
    void after() {
        sql.execute('drop table DISL_TEST_ORA_REVERSE')
    }

}
