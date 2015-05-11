package org.disl.meta;

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKey {
	Class<Table> targetTable();
	String targetColumn();

}
