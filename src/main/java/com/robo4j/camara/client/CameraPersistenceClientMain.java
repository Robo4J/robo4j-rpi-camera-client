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

import com.robo4j.camara.client.controller.ImageProvider;
import com.robo4j.core.RoboSystem;
import com.robo4j.core.concurrency.SchedulePeriodUnit;
import com.robo4j.core.configuration.Configuration;
import com.robo4j.core.configuration.ConfigurationFactory;
import com.robo4j.core.httpunit.HttpClientUnit;
import com.robo4j.core.httpunit.HttpServerUnit;
import com.robo4j.core.util.SystemUtil;
import com.robo4j.db.sql.RoboPointSQLPersistenceUnit;
import com.robo4j.db.sql.SQLDataSourceUnit;
import com.robo4j.db.sql.util.DBSQLConstants;
import com.robo4j.units.rpi.camera.RaspistillUnit;

/**
 * Demo is optimised for PostgreSQL 9.4 Database It's necessary to prepare
 * Database schema by using Flyway. Flyway is available inside robo4j-db-sql
 * module.
 *
 *
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class CameraPersistenceClientMain {

	private static final String PERSISTENCE_UNIT_NAME = "dbSqlUnit";
	private static final String TARGET_STORAGE_UNIT = "imagePersistenceUnit";
	private static final String IMAGE_CONTROLLER = "imageController";
	private static final String IMAGE_PROVIDER = "imageProvider";

	public static void main(String[] args) throws Exception {
		final RoboSystem system = new RoboSystem();
		Configuration sqlConfig = ConfigurationFactory.createEmptyConfiguration();
		SQLDataSourceUnit sqlUnit = new SQLDataSourceUnit(system, PERSISTENCE_UNIT_NAME);
		sqlConfig.setString("sourceType", "postgresql");
		sqlConfig.setString("packages", "com.robo4j.db.sql.model");
		sqlConfig.setInteger("limit", 3);
		sqlConfig.setString("sorted", "asc");
		sqlConfig.setString("hibernate.hbm2ddl.auto", "validate");
		sqlConfig.setString("hibernate.connection.url", "jdbc:postgresql://192.168.178.42:5433/robo4j1");
		sqlConfig.setString("targetUnit", TARGET_STORAGE_UNIT);

		RoboPointSQLPersistenceUnit imageSQLPersistenceUnit = new RoboPointSQLPersistenceUnit(system,
				TARGET_STORAGE_UNIT);
		Configuration imageSQLPersistenceUnitConfig = ConfigurationFactory.createEmptyConfiguration();
		imageSQLPersistenceUnitConfig.setString("persistenceUnit", PERSISTENCE_UNIT_NAME);
		imageSQLPersistenceUnitConfig.setString("config", "default");

		RaspistillUnit imageController = new RaspistillUnit(system, IMAGE_CONTROLLER);
		Configuration config = ConfigurationFactory.createEmptyConfiguration();
		config.setString("targetOut", "httpClient");
		config.setString("storeTarget", IMAGE_PROVIDER);
		config.setString("client", "192.168.178.42");
		config.setString("clientPort", "8027");
		config.setString("clientUri", "/imageController");
		config.setString("width", "640");
		config.setString("height", "480");
		config.setString("rotation", "180");
		config.setString("timeout", "1");
		config.setString("timelapse", "100");
		imageController.initialize(config);

		HttpClientUnit httpClientUnit = new HttpClientUnit(system, "httpClient");
		config = ConfigurationFactory.createEmptyConfiguration();
		config.setString("address", "192.168.178.42");
		config.setInteger("port", 8027);
		Configuration targetUnits1 = config.createChildConfiguration("targetUnits");
		targetUnits1.setString("imageController", "POST");
		httpClientUnit.initialize(config);

		SchedulePeriodUnit schedulePeriodUnit = new SchedulePeriodUnit(system, "scheduleController");
		config = ConfigurationFactory.createEmptyConfiguration();
		config.setString("unit", IMAGE_CONTROLLER);
		config.setInteger("delay", 1);
		config.setInteger("period", 500);
		config.setString("timeUnit", "MILLISECONDS");
		schedulePeriodUnit.initialize(config);

		HttpServerUnit httpServerUnit = new HttpServerUnit(system, "httpServer");
		config = ConfigurationFactory.createEmptyConfiguration();
		config.setInteger("port", 8025);
		config.setString("target", IMAGE_PROVIDER);
		config.setString("packages", "com.robo4j.core.httpunit.codec");
		Configuration targetUnits2 = config.createChildConfiguration("targetUnits");
		targetUnits2.setString(IMAGE_PROVIDER, "GET");
		httpServerUnit.initialize(config);

		ImageProvider imageProvider = new ImageProvider(system, IMAGE_PROVIDER);
		config = ConfigurationFactory.createEmptyConfiguration();
		config.setString(DBSQLConstants.KEY_PERSISTENCE_UNIT, TARGET_STORAGE_UNIT);
		imageProvider.initialize(config);

		system.addUnits(sqlUnit, imageSQLPersistenceUnit, imageController, httpClientUnit, schedulePeriodUnit,
				httpServerUnit, imageProvider);
		sqlUnit.initialize(sqlConfig);
		imageSQLPersistenceUnit.initialize(imageSQLPersistenceUnitConfig);
		imageController.start();

		system.start();

		System.out.println("System: State after start:");
		System.out.println(SystemUtil.printStateReport(system));

		System.out.println("State after start:");
		System.out.println(SystemUtil.printStateReport(system));

		System.out.println("Press enter to quit!");
		System.in.read();
		system.shutdown();
		System.out.println("System: State after shutdown:");
		System.out.println(SystemUtil.printStateReport(system));

	}
}
