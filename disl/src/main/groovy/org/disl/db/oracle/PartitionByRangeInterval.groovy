package org.disl.db.oracle

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
@interface PartitionByRangeInterval {
	String columnName()
	String interval()
	String defaultLessThan()
}
