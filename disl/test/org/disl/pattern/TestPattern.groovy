package org.disl.pattern

import junit.framework.Assert

import org.disl.meta.Base;
import org.junit.Test

class TestPattern  {
	
	@Test
	public void testSimulate() {
		Pattern testingPattern=new TestingPattern(element:new Base());		
		testingPattern.simulate();
		Assert.assertEquals("DROP \nBase", testingPattern.steps[0].code)
		
		
	}
	
	class TestingPattern extends Pattern {
		Base element;
		
		@Override
		public List<Step> getSteps() {
			[new Step(name:"1", pattern: """\
DROP 
${element.name}""")]
		}
		
	
		
		
	}
	
	
	
}
