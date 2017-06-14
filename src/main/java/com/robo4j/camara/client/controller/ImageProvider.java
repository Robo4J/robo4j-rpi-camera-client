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

package com.robo4j.camara.client.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;

import com.robo4j.core.AttributeDescriptor;
import com.robo4j.core.ConfigurationException;
import com.robo4j.core.DefaultAttributeDescriptor;
import com.robo4j.core.RoboContext;
import com.robo4j.core.RoboUnit;
import com.robo4j.core.client.util.RoboClassLoader;
import com.robo4j.core.configuration.Configuration;
import com.robo4j.core.httpunit.Constants;
import com.robo4j.core.logging.SimpleLoggingUtil;
import com.robo4j.db.sql.dto.ERoboPointDTO;
import com.robo4j.db.sql.util.DBSQLConstants;
import com.robo4j.hw.rpi.camera.CameraClientException;

/**
 * ImageProvider is capable to display current image
 *
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class ImageProvider extends RoboUnit<byte[]> {

	private static final String ROBO_POINT_TYPE = "image";
	private static final String ATTRIBUTE_IMAGE_NAME = "image.jpg";
	private final static Collection<AttributeDescriptor<?>> KNOWN_ATTRIBUTES = Collections.unmodifiableCollection(
			Collections.singleton(DefaultAttributeDescriptor.create(byte[].class, ATTRIBUTE_IMAGE_NAME)));

	private volatile byte[] imageBytes;
	private String persistenceUnitName;

	public ImageProvider(RoboContext context, String id) {
		super(byte[].class, context, id);
	}

	/**
	 * if persistence (persistenceUnitName) unit is not configured, images are
	 * not stored in database
	 * 
	 * @param configuration
	 *            - unit can by configure for string images into database
	 * @throws ConfigurationException
	 */
	@Override
	protected void onInitialization(Configuration configuration) throws ConfigurationException {

		InputStream is = RoboClassLoader.getInstance().getResource("20161021_NoSignal_240.jpg");
		try {
			imageBytes = new byte[is.available()];
			is.read(imageBytes);
		} catch (IOException e) {
			SimpleLoggingUtil.error(getClass(), "not readable image");
		}

		persistenceUnitName = configuration.getString(DBSQLConstants.KEY_PERSISTENCE_UNIT, null);
	}

	@Override
	public void onMessage(byte[] image) {
		imageBytes = image;
		System.out.println(getClass().getSimpleName() + " image: " + image.length + " persistenceUnitName= "
				+ persistenceUnitName);
		if (persistenceUnitName != null && image.length > 0) {
			storeImage(image);
		}
	}

	@Override
	public Collection<AttributeDescriptor<?>> getKnownAttributes() {
		return KNOWN_ATTRIBUTES;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> R onGetAttribute(AttributeDescriptor<R> attribute) {
		if (ATTRIBUTE_IMAGE_NAME.equals(attribute.getAttributeName())) {
			return (R) imageBytes;
		}
		return null;
	}

	// Private Methods
	private void storeImage(byte[] image) {
		try {
			String dataString = new String(Base64.getEncoder().encode(image), Constants.DEFAULT_ENCODING);
			if (persistenceUnitName != null) {
				ERoboPointDTO pointDTO = new ERoboPointDTO(ROBO_POINT_TYPE, dataString);
				getContext().getReference(persistenceUnitName).sendMessage(pointDTO);
			}
		} catch (UnsupportedEncodingException e) {
			throw new CameraClientException("storing image", e);
		}
	}

}
