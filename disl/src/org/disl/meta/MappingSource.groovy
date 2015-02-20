package org.disl.meta
import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Test;


class MappingSource extends Base {
	String sourceAlias
	Join join=new Join.NONE(source:this)	

	
	public String toString() {
		join.fromClause
	}
	
	public MappingSource on(String condition){
		this.join.condition=condition
		this
	}


}
