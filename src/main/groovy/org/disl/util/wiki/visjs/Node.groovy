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
package org.disl.util.wiki.visjs

import org.disl.meta.Lookup
import org.disl.meta.Mapping
import org.disl.meta.Table
import org.disl.util.wiki.WikiHelper

/**
 * Representation of vis.js network Node.
 */
class Node {
    String id
    String label
    String title
    String color='LightGrey'
    String shape='box'
    String targetUrl
    int size=25


    Node(Table t) {
        id = t.class.name
        label = t.toString()
        title = t.description
        targetUrl = WikiHelper.url(t)
        try {
            switch (t['stereotype']) {
                case 'dimension':
                    color = 'LightBlue'
                    break
                case 'fact':
                    color = 'Yellow'
                    break
            }
        } catch (MissingPropertyException e) {
            //do nothing
        }

    }

    Node(Mapping m) {
        id = m.class.name
        label = m.toString()
        title = m.description
        color = 'LightGreen'
        shape = 'ellipse'
        targetUrl = WikiHelper.url(m)
    }

    Node(Lookup l) {
        id = l.class.name
        label = l.toString()
        title = l.description
        color = 'DarkGreen'
        shape = 'ellipse'
        targetUrl = WikiHelper.url(l)
    }

    @Override
    boolean equals(def obj) {
        if (obj instanceof Node) {
            return id.equals(obj.id)
        }
        return false
    }

    @Override
    int hashCode() {
        return id.hashCode()
    }
}
