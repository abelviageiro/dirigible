/**
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.core.migrations.synchronizer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.script.ScriptEngineManager;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService;
import org.eclipse.dirigible.core.migrations.api.MigrationsException;
import org.eclipse.dirigible.core.migrations.definition.MigrationDefinition;
import org.eclipse.dirigible.core.migrations.definition.MigrationStatusDefinition;
import org.eclipse.dirigible.core.migrations.service.MigrationsCoreService;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Migrations Synchronizer.
 */
@Singleton
public class MigrationsSynchronizer extends AbstractSynchronizer {

	private static final Logger logger = LoggerFactory.getLogger(MigrationsSynchronizer.class);

	private static final Map<String, MigrationDefinition> MIGRATIONS_PREDELIVERED = Collections.synchronizedMap(new HashMap<String, MigrationDefinition>());

	private static final Set<String> MIGRATIONS_SYNCHRONIZED = Collections.synchronizedSet(new HashSet<String>());

	@Inject
	private MigrationsCoreService migrationsCoreService;
	
	@Inject
	private DataSource dataSource;

	@Inject
	private PersistenceManager<MigrationDefinition> migrationsPersistenceManager;

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		MigrationsSynchronizer migrationsSynchronizer = StaticInjector.getInjector().getInstance(MigrationsSynchronizer.class);
		migrationsSynchronizer.synchronize();
	}

	/**
	 * Register pre-delivered roles.
	 *
	 * @param location
	 *            the roles path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredMigrations(String location) throws IOException {
		InputStream in = MigrationsSynchronizer.class.getResourceAsStream(location);
		try {
			String json = IOUtils.toString(in, StandardCharsets.UTF_8);
			MigrationDefinition migrationDefinition = migrationsCoreService.parseMigration(json);
			migrationDefinition.setLocation(location);
			MIGRATIONS_PREDELIVERED.put(location, migrationDefinition);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (MigrationsSynchronizer.class) {
			logger.trace("Synchronizing Migrations artifacts...");
			try {
				clearCache();
				synchronizePredelivered();
				synchronizeRegistry();
				startMigrations();
				cleanup();
				clearCache();
			} catch (Exception e) {
				logger.error("Synchronizing process for Migrations artifacts failed.", e);
			}
			logger.trace("Done synchronizing Migrations artifacts.");
		}
	}

	/**
	 * Clear cache.
	 */
	private void clearCache() {
		MIGRATIONS_SYNCHRONIZED.clear();
	}

	/**
	 * Synchronize predelivered.
	 *
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void synchronizePredelivered() throws SynchronizationException {
		logger.trace("Synchronizing predelivered Migrations artifacts...");

		// Migrations
		for (MigrationDefinition migrationDefinition : MIGRATIONS_PREDELIVERED.values()) {
			synchronizeMigration(migrationDefinition);
		}

		logger.trace("Done synchronizing predelivered Migrations artifacts.");
	}

	/**
	 * Synchronize role.
	 *
	 * @param migrationDefinition
	 *            the migration definition
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void synchronizeMigration(MigrationDefinition migrationDefinition) throws SynchronizationException {
		try {
			if (!migrationsCoreService.existsMigration(migrationDefinition.getLocation())) {
				migrationsCoreService.createMigration(migrationDefinition.getLocation(), migrationDefinition.getProject(), 
						migrationDefinition.getMajor(), migrationDefinition.getMinor(), migrationDefinition.getMicro(),
						migrationDefinition.getHandler(), migrationDefinition.getEngine(), migrationDefinition.getDescription());
				logger.info("Synchronized a new Migration procedure from location: {}", migrationDefinition.getLocation());
			} else {
				MigrationDefinition existing = migrationsCoreService.getMigration(migrationDefinition.getLocation());
				if (!migrationDefinition.equals(existing)) {
					logger.error("Modified Migration procedure was met during synchronization!");
				}
			}
			MIGRATIONS_SYNCHRONIZED.add(migrationDefinition.getLocation());
		} catch (MigrationsException e) {
			throw new SynchronizationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeRegistry()
	 */
	@Override
	protected void synchronizeRegistry() throws SynchronizationException {
		logger.trace("Synchronizing Migrations from Registry...");

		super.synchronizeRegistry();

		logger.trace("Done synchronizing Migrations from Registry.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeResource(org.eclipse.dirigible.
	 * repository.api.IResource)
	 */
	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		String resourceName = resource.getName();
		if (resourceName.endsWith(IMigrationsCoreService.FILE_EXTENSION_MIGRATE)) {
			MigrationDefinition migrationDefinition = migrationsCoreService.parseMigration(resource.getContent());
			migrationDefinition.setLocation(getRegistryPath(resource));
			synchronizeMigration(migrationDefinition);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#cleanup()
	 */
	@Override
	protected void cleanup() throws SynchronizationException {
		logger.trace("Cleaning up Roles and Access artifacts...");

		try {
			List<MigrationDefinition> migrationDefinitions = migrationsCoreService.getMigrations();
			for (MigrationDefinition migrationDefinition : migrationDefinitions) {
				if (!MIGRATIONS_SYNCHRONIZED.contains(migrationDefinition.getLocation())) {
					migrationsCoreService.removeMigration(migrationDefinition.getLocation());
					logger.warn("Cleaned up Migration definition from location: {}", migrationDefinition.getLocation());
				}
			}

		} catch (MigrationsException e) {
			throw new SynchronizationException(e);
		}

		logger.trace("Done cleaning up Migrations artifacts.");
	}
	
	private void startMigrations() {
		logger.trace("Start running Migrations...");

		List<String> migratedProjects = new ArrayList<String>();
		for (String migrationLocation : MIGRATIONS_SYNCHRONIZED) {
			String project = "";
			try {
				project = "location: " + migrationLocation;
				MigrationDefinition migrationDefinition = migrationsCoreService.getMigration(migrationLocation);
				project = migrationDefinition.getProject();
				if (migratedProjects.contains(project)) {
					continue;
				}
				List<MigrationDefinition> migrationDefinitions = migrationsCoreService.getMigrationsPerProject(project);
				MigrationStatusDefinition migrationStatusDefinition = migrationsCoreService.getMigrationStatus(project);
				MigrationDefinition lastMigration = null;
				for (MigrationDefinition migration : migrationDefinitions) {
					if (migrationStatusDefinition != null) {
						if (migration.getMajor() > migrationStatusDefinition.getMajor()) {
							performMigration(migration);
						} else if (migration.getMajor() == migrationStatusDefinition.getMajor() 
								&& migration.getMinor() > migrationStatusDefinition.getMinor()) {
							performMigration(migration);
						} else if (migration.getMajor() == migrationStatusDefinition.getMajor() 
								&& migration.getMinor() == migrationStatusDefinition.getMinor()
								&& migration.getMicro() > migrationStatusDefinition.getMicro()) {
							performMigration(migration);
						} else {
							logger.trace("Migration for project {} with version {}.{}.{} has been skipped because the project status is with a higher version", 
									project, migration.getMajor(), migration.getMinor(), migration.getMicro());
						}
					} else {
						performMigration(migration);
					}
					lastMigration = migration;
				}
				if (migrationStatusDefinition == null) {
					migrationsCoreService.createMigrationStatus(project, lastMigration.getMajor(), lastMigration.getMinor(), lastMigration.getMicro(), 
							lastMigration.getLocation());
				} else {
					if (migrationStatusDefinition.getMajor() != lastMigration.getMajor()
							|| migrationStatusDefinition.getMinor() != lastMigration.getMinor()
							|| migrationStatusDefinition.getMicro() != lastMigration.getMicro()) {
						migrationsCoreService.updateMigrationStatus(project, lastMigration.getMajor(), lastMigration.getMinor(), lastMigration.getMicro(), 
								lastMigration.getLocation());
					}
				}
				migratedProjects.add(project);
			} catch (MigrationsException | ScriptingException e) {
				logger.error("Migration procedure for project {} artifacts failed.", project);
				logger.error("Migration procedure error: ", e);
			}
			
		}

		logger.trace("Done running Migrations.");
	}

	private void performMigration(MigrationDefinition migration) throws ScriptingException {
		ScriptEngineExecutorsManager.executeServiceModule(migration.getEngine(), migration.getHandler(), null);
	}
}
