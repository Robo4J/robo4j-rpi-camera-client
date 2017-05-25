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
import java.util.Collection;
import java.util.Collections;

import com.robo4j.core.AttributeDescriptor;
import com.robo4j.core.ConfigurationException;
import com.robo4j.core.DefaultAttributeDescriptor;
import com.robo4j.core.RoboContext;
import com.robo4j.core.RoboUnit;
import com.robo4j.core.client.util.RoboClassLoader;
import com.robo4j.core.configuration.Configuration;
import com.robo4j.core.logging.SimpleLoggingUtil;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class ImageProvider extends RoboUnit<byte[]> {

	private static final String ATTRIBUTE_IMAGE_NAME = "image.jpg";
	private final static Collection<AttributeDescriptor<?>> KNOWN_ATTRIBUTES = Collections.unmodifiableCollection(
			Collections.singleton(DefaultAttributeDescriptor.create(byte[].class, ATTRIBUTE_IMAGE_NAME)));

	private volatile byte[] imageBytes;

	public ImageProvider(RoboContext context, String id) {
		super(byte[].class, context, id);
	}

	@Override
	protected void onInitialization(Configuration configuration) throws ConfigurationException {

		InputStream is = RoboClassLoader.getInstance().getResource("20161021_NoSignal_240.jpg");
		try {
			imageBytes = new byte[is.available()];
			is.read(imageBytes);
		} catch (IOException e) {
			SimpleLoggingUtil.error(getClass(), "not readable image");
		}
	}

	@Override
	public void onMessage(byte[] image) {
		imageBytes = image;
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

}
