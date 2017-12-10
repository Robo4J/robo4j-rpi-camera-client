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

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.robo4j.RoboBuilder;
import com.robo4j.RoboContext;
import com.robo4j.RoboReference;
import com.robo4j.units.rpi.camera.RaspistillRequest;
import com.robo4j.units.rpi.camera.RpiCameraProperty;
import com.robo4j.util.SystemUtil;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class CameraClientDeclarativeMain {

	public static void main(String[] args) throws Exception {

		InputStream configInputStream = null;

		switch (args.length) {
		case 0:
			configInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("robo4j.xml");
			System.out.println("Default configuration used");
			break;
		case 1:
			Path path = Paths.get(args[0]);
			configInputStream = Files.newInputStream(path);
			System.out.println("Robo4j config file has been used: " + args[0]);
			break;
		default:
			System.out.println("Could not find the *.xml settings for the CameraClient!");
			System.out.println("java -jar camera.jar robo4j.xml");
			System.exit(2);
			break;
		}

		RoboBuilder builder = new RoboBuilder(
				Thread.currentThread().getContextClassLoader().getResourceAsStream("robo4jSystem.xml"));
		builder.add(configInputStream);
		RoboContext system = builder.build();

		System.out.println("State before start:");
		System.out.println(SystemUtil.printStateReport(system));
		system.start();

		System.out.println("State after start:");
		System.out.println(SystemUtil.printStateReport(system));

		//@formatter:off
		final RaspistillRequest activeRequest = new RaspistillRequest(true)
				.put(RpiCameraProperty.WIDTH, "640")
				.put(RpiCameraProperty.HEIGHT, "480").put(RpiCameraProperty.TIMEOUT, "2")
				.put(RpiCameraProperty.TIMELAPSE, "100").put(RpiCameraProperty.ENCODING, "jpg")
				.put(RpiCameraProperty.NOPREVIEW, "")
				.put(RpiCameraProperty.OUTPUT, "-");
		//@formatter:on

		RoboReference<RaspistillRequest> cameraUnit = system.getReference("camera");
		cameraUnit.sendMessage(activeRequest);

		System.out.println("Press enter to quit!");
		System.in.read();
		system.shutdown();

	}
}
