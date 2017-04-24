/*
 * Copyright 2015 - 2017 Karel Hübl <karel.huebl@gmail.com>.
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
package org.disl.meta

import org.disl.pattern.Executable
import org.disl.pattern.ExecutionInfo
import org.disl.pattern.Pattern

/**
 * Set of related DISL model objects documented by @Description.
 * Intent of this object is to describe part of DISL model and enable generation of documentation.
 * Following class level annotations are supported:
 * <li>@Name - name of perspective.</li>
 * <li>@Elements - set of DISL model classes.</li>
 * <li>@Description </li>
 * */
abstract class Perspective extends Base implements  Initializable,Executable {

    Set<Base> elements=new TreeSet<>(new ClassComparator())

    abstract Pattern getPattern()

    void init() {
        super.init()
        initElements()
        initName()
        initTags()
    }

    @Override
    void execute() {
        getPattern().execute()
    }

    @Override
    void simulate() {
        getPattern().simulate()
    }

    @Override
    ExecutionInfo getExecutionInfo() {
        getPattern().getExecutionInfo()
    }

    /**
     * Init elements by annotation value.
     * */
    void initElements() {
        Elements elements = this.getClass().getAnnotation(Elements)
        if (elements) {
            elements.value().each {
                this.elements.add MetaFactory.create(it)
            }
        }
    }

    static class ClassComparator implements Comparator {
        @Override
        int compare(Object o1, Object o2) {
            return o1.class.name.compareTo(o2.class.name)
        }
    }


}
