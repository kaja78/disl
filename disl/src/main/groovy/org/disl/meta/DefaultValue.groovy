package org.disl.meta

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Defines default value for Column.
 * */
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultValue {
	String value()
}
