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

package com.robo4j.camara.client.unit;

import com.robo4j.ConfigurationException;
import com.robo4j.RoboContext;
import com.robo4j.RoboUnit;
import com.robo4j.configuration.Configuration;
import com.robo4j.socket.http.codec.CameraConfigMessage;
import com.robo4j.units.rpi.camera.RaspistillRequest;
import com.robo4j.units.rpi.camera.RpiCameraProperty;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miroslav Wengner (@miragemiko)
 */
public class CameraConfigurationUnit extends RoboUnit<CameraConfigMessage> {

    private static final String PROPERTY_TARGET = "target";
    private String target;

    public CameraConfigurationUnit(RoboContext context, String id) {
        super(CameraConfigMessage.class, context, id);
    }


    @Override
    protected void onInitialization(Configuration configuration) throws ConfigurationException {
        target = configuration.getString(PROPERTY_TARGET, null);
        if (target == null) {
            throw ConfigurationException.createMissingConfigNameException(PROPERTY_TARGET);
        }
    }

    @Override
    public void onMessage(CameraConfigMessage message) {
        //@formatter:off
		final RaspistillRequest cameraRequest = new RaspistillRequest(true)
				.put(RpiCameraProperty.WIDTH, message.getWidth().toString())
				.put(RpiCameraProperty.HEIGHT, message.getHeight().toString())
                .put(RpiCameraProperty.BRIGHTNESS, message.getBrightness().toString())
                .put(RpiCameraProperty.SHARPNESS, message.getSharpness().toString())
                .put(RpiCameraProperty.TIMEOUT, "2")
				.put(RpiCameraProperty.TIMELAPSE, "100")
                .put(RpiCameraProperty.ENCODING, "jpg")
				.put(RpiCameraProperty.NOPREVIEW, "")
				.put(RpiCameraProperty.OUTPUT, "-");
		//@formatter:on
        getContext().getReference(target).sendMessage(cameraRequest);
    }
}
