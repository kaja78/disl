package org.disl.meta
import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Test;


abstract class MappingSource extends Base {
	String sourceAlias
	Join join=new Join.NONE(source:this)	

	public abstract String getRefference();
	
	public String toString() {
		return getClass().getSimpleName()
	}
	
	public String getFromClause() {
		return join.fromClause
	}
	
	public MappingSource on(String condition){
		this.join.condition=condition
		this
	}


}
