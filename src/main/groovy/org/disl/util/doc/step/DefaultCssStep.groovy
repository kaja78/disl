/*
 *
 * Copyright 2015 - 2021 Karel Hübl <karel.huebl@gmail.com>.
 *
 * This file is part of disl.
 *
 * Disl is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Disl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Disl.  If not, see <http://www.gnu.org/licenses/>.
 */
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

iframe
{
  width:100%;
}
"""
	}

}
