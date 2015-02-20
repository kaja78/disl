package org.disl.meta

class MetaFactory {
	static Object create(Class type) {
		def instance=type.newInstance();
		if (instance instanceof Initializable) {
			instance.init();
		}
		instance
	}
}
