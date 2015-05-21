package org.disl.meta

class FunctionalColumn extends Column {
	String defaultMapping

	String getMapping(){
		if (defaultMapping!=null) {
			return defaultMapping+' '+toString()
		}
		toString()
	}
}
