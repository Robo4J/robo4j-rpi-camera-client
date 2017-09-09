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

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.robo4j.camara.client.controller.ImageProvider;
import com.robo4j.core.RoboBuilder;
import com.robo4j.core.RoboContext;
import com.robo4j.core.configuration.Configuration;
import com.robo4j.core.configuration.ConfigurationFactory;
import com.robo4j.core.util.SystemUtil;
import com.robo4j.socket.http.units.HttpClientUnit;
import com.robo4j.socket.http.units.HttpServerUnit;
import com.robo4j.socket.http.util.JsonUtil;
import com.robo4j.units.rpi.camera.CameraPeriodUnit;
import com.robo4j.units.rpi.camera.RaspistillUnit;

/**
 * Demo is providing desired camera images and sending them to the client
 * 
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class CameraClientMain {
	private static final String IMAGE_CONTROLLER = "imageController";
	private static final String IMAGE_PROVIDER = "imageProvider";

	public static void main(String[] args) throws Exception {
		final RoboBuilder builder = new RoboBuilder();

		Configuration config = ConfigurationFactory.createEmptyConfiguration();
		config.setString("targetOut", "httpClient");
		config.setString("storeTarget", IMAGE_PROVIDER);
		config.setString("client", "192.168.178.42");
		config.setString("clientPort", "8027");
		config.setString("clientUri", "/imageController");
		config.setString("width", "320");
		config.setString("height", "240");
		config.setString("rotation", "180");
		config.setString("timeout", "1");
		config.setString("timelapse", "100");
		builder.add(RaspistillUnit.class, config, IMAGE_CONTROLLER);

		config = ConfigurationFactory.createEmptyConfiguration();
		config.setString("address", "192.168.178.42");
		config.setInteger("port", 8027);
		Map<String, Object> httpClientConfig = Collections.singletonMap("imageController", "POST");
		config.setString("targetUnits", JsonUtil.getJsonByMap(httpClientConfig));
		builder.add(HttpClientUnit.class, config, "httpClient");

		config = ConfigurationFactory.createEmptyConfiguration();
		config.setInteger("port", 8025);
		config.setString("target", IMAGE_PROVIDER);
		config.setInteger("stopper", -1);
		config.setString("packages", "com.robo4j.socket.http.codec");
		Map<String, Object> httpServerConfig = Collections.singletonMap(IMAGE_PROVIDER, "GET");
		config.setString("targetUnits", JsonUtil.getJsonByMap(httpServerConfig));
		builder.add(HttpServerUnit.class, config, "httpServer");
		builder.add(ImageProvider.class, IMAGE_PROVIDER);

		config = ConfigurationFactory.createEmptyConfiguration();
		config.setString("unit", IMAGE_CONTROLLER);
		config.setInteger("delay", 1);
		config.setInteger("period", 500);
		config.setString("timeUnit", "MILLISECONDS");
		builder.add(CameraPeriodUnit.class, config, "scheduleController");

		RoboContext system = builder.build();
		system.start();

		System.out.println("System: State after start:");
		System.out.println(SystemUtil.printStateReport(system));

		System.out.println("Press enter to quit!");
		System.in.read();
		system.stop();
		system.shutdown();
		System.out.println("System: State after shutdown:");
		System.out.println(SystemUtil.printStateReport(system));

	}
}
