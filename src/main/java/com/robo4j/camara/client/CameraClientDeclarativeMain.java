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

package com.robo4j.camara.client;

import java.io.IOException;

import com.robo4j.core.RoboBuilder;
import com.robo4j.core.RoboBuilderException;
import com.robo4j.core.RoboContext;
import com.robo4j.core.util.SystemUtil;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class CameraClientDeclarativeMain {

	public static void main(String[] args) throws RoboBuilderException, IOException {
		RoboBuilder builder = new RoboBuilder(Thread.currentThread().getContextClassLoader().getResourceAsStream("robo4jSystem.xml"));
		builder.add(Thread.currentThread().getContextClassLoader().getResourceAsStream("robo4j.xml"));
		RoboContext system = builder.build();

		System.out.println("State before start:");
		System.out.println(SystemUtil.printStateReport(system));
		system.start();

		System.out.println("State after start:");
		System.out.println(SystemUtil.printStateReport(system));

		System.out.println("Press enter to quit!");
		System.in.read();
		system.shutdown();
	}

}
