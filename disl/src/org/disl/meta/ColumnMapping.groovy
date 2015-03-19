package org.disl.meta

import javax.management.InstanceOfQueryExp;

import org.codehaus.groovy.ast.expr.ClosureExpression;

abstract class ColumnMapping {

	def expression
	String alias
	
	String toString() {
		alias
	}
	
	String getMappingExpression() {
		if (expression instanceof Closure) {
			expression=expression.call()
		}
		"$expression as $alias"
	}
	

}
