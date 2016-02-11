/*
 * Copyright 2015 - 2016 Karel Hübl <karel.huebl@gmail.com>.
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

import org.disl.meta.Table;

/**
 * Abstract parent for Oracle table. Add support for Oracle partitioning annotations.
 * @See PartitionBy and PartitionByRangeInterval.
 * */
abstract class OracleTable extends Table {

	PartitionByMeta partitionByMeta

	@Override
	public void init() {
		partitionByMeta=new PartitionByMetaImpl()
		PartitionByMetaImpl.initPartitionBy(this)
		PartitionByRangeIntervalMeta.initPartitionBy(this)
		super.init();
	}



	static class PartitionByMetaImpl implements PartitionByMeta {
		String partitionByClause=''

		public static void initPartitionBy(def owner) {
			PartitionBy anotation=owner.getClass().getAnnotation(PartitionBy)
			if (anotation==null) {
				return;
			}
			owner.partitionByMeta=new PartitionByMetaImpl(
					partitionByClause: anotation.value())
		}
	}

	static class PartitionByRangeIntervalMeta implements PartitionByMeta {
		String columnName
		String interval
		String defaultLessThan
		String subpartitioningClause


		String getPartitionByClause(){
			"""\
PARTITION BY RANGE ($columnName) INTERVAL ($interval)
${subpartitioningClause}
    (PARTITION P_DEFAULT VALUES LESS THAN ($defaultLessThan))"""
		}

		public static void initPartitionBy(def owner) {
			PartitionByRangeInterval anotation=owner.getClass().getAnnotation(PartitionByRangeInterval)
			if (anotation==null) {
				return;
			}
			owner.partitionByMeta=new PartitionByRangeIntervalMeta(
					columnName: anotation.columnName(),
					interval: anotation.interval(),
					defaultLessThan: anotation.defaultLessThan(),
					subpartitioningClause: anotation.subpartitioningClasue())
		}
	}
}
