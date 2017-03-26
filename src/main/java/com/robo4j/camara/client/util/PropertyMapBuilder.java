/*
 * Copyright (c) 2014, 2017, Marcus Hirt, Miroslav Wengner
 *
 * Robo4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Robo4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Robo4J. If not, see <http://www.gnu.org/licenses/>.
 */

package com.robo4j.camara.client.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class PropertyMapBuilder<Key, Value> {

	private Map<Key, Value> map;

	private PropertyMapBuilder() {
		this.map = new HashMap<>();
	}

	public static <Key, Value> PropertyMapBuilder Builder() {
		return new PropertyMapBuilder<Key, Value>();
	}

	public PropertyMapBuilder put(Key key, Value value) {
		map.put(key, value);
		return this;
	}

	public Map<Key, Value> create() {
		return Collections.unmodifiableMap(map);
	}

}
