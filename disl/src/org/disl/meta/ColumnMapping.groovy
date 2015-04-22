package org.disl.meta

import javax.management.InstanceOfQueryExp;

import org.codehaus.groovy.ast.expr.ClosureExpression;

abstract class ColumnMapping {

	Mapping parent
	def expression
	String alias
	
	String toString(){
		if (parent.getSourceAlias()==null) {
			return getAlias();
		}
		"${parent.getSourceAlias()}.${getAlias()}"
	}
	
	String getMappingExpression() {
		if (expression instanceof Closure) {
			expression=expression.call()
		}
		"$expression as $alias"
	}
	

}
