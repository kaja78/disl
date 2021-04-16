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
package org.disl.db.oracleproxy

import groovy.util.logging.Slf4j
import org.disl.db.oracle.OracleSchema
import org.disl.meta.Context
import org.disl.meta.PhysicalSchema



/**
 * Implementation of DISL physical schema for Oracle Database supporting connection using proxy user.
 * */
@Slf4j
class OracleproxySchema extends OracleSchema {

    /**
     * DISL logical schema used for database connection.
     * */
    PhysicalSchema proxy

    @Override
    void init() {
        super.init()
        String proxySchema=getSchemaProperty('proxySchema','default')
        proxy=Context.getContext().getPhysicalSchema(proxySchema)
        log.debug("")

    }

    @Override
    String getUser() {
        return "${proxy.user}[${this.getSchema()}]"
    }

    @Override
    String getPassword() {
        return proxy.getPassword()
    }

    @Override
    String getJdbcUrl() {
        if (!proxy) {
            return null
        }
        return proxy.getJdbcUrl()
    }
}
