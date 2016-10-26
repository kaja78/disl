package org.disl.util.doc.step

import java.io.File

import org.disl.pattern.FileOutputStep;

class DefaultCssStep extends AbstractDocStep {
	
	@Override
	protected String getFileName() {
		'disldoc.css'
	}

	@Override
	public String getCode() {
		"""\
body {
    font-family: sans-serif;
}
table {
    border-collapse: collapse;
}

table, th, td {
    border: 1px solid black;
}

td.nowrap {
	white-space: nowrap;
}

th {
  font-wight: bold;
  background-color: silver;
  white-space: nowrap;
}

div {
	border: 1px solid black;
}

li {
		list-style-type: none;
}

textarea
{
  width:100%;
  height:50%
}
"""
	}

}
