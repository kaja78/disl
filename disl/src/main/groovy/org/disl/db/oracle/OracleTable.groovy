package org.disl.db.oracle

import org.disl.meta.Table

abstract class OracleTable extends Table {

	PartitionByMeta partitionByMeta

	@Override
	public void init() {
		super.init();
		initPartitionBy()
	}

	protected void initPartitionBy() {
		partitionByMeta=new NoPartitioning()
		
		PartitionByRangeInterval anotation=this.getClass().getAnnotation(PartitionByRangeInterval)
		if (anotation!=null) {
			partitionByMeta=new PartitionByRangeIntervalMeta(columnName: anotation.columnName(), interval: anotation.interval(), defaultLessThan: anotation.defaultLessThan())
			return;
		}
	}

	static class NoPartitioning implements PartitionByMeta {
		String getPartitionByClause(){
			''
		}
	}
	
	static class PartitionByRangeIntervalMeta implements PartitionByMeta {
		String columnName
		String interval
		String defaultLessThan


		String getPartitionByClause(){
			"""\
PARTITION BY RANGE ($columnName) INTERVAL ($interval)
    (PARTITION P_DEFAULT VALUES LESS THAN ($defaultLessThan))"""
		}
	}
}
