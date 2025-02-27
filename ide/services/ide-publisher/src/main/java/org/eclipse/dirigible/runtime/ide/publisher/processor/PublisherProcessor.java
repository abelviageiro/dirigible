/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.runtime.ide.publisher.processor;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.core.publisher.api.PublisherException;
import org.eclipse.dirigible.core.publisher.definition.PublishLogDefinition;
import org.eclipse.dirigible.core.publisher.definition.PublishRequestDefinition;
import org.eclipse.dirigible.core.publisher.service.PublisherCoreService;
import org.eclipse.dirigible.core.publisher.synchronizer.PublisherSynchronizer;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processing the Publisher Service incoming requests.
 */
public class PublisherProcessor {

	private Logger logger = LoggerFactory.getLogger(PublisherProcessor.class);

	@Inject
	private PublisherCoreService publishCoreService;

	@Inject
	private IRepository repository;

	/**
	 * Request publishing.
	 *
	 * @param user
	 *            the user
	 * @param workspace
	 *            the workspace
	 * @param path
	 *            the path
	 * @return the long
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public long requestPublishing(String user, String workspace, String path) throws PublisherException {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, null, null);
		if ("*".equals(path)) {
			path = "";
		}
		PublishRequestDefinition publishRequestDefinition = publishCoreService.createPublishRequest(workspacePath.toString(), path,
				IRepositoryStructure.PATH_REGISTRY_PUBLIC);
		logger.info("Publishing request created [{}]", publishRequestDefinition.getId());
		// force synchronization ?
		PublisherSynchronizer.forceSynchronization();
		return publishRequestDefinition.getId();
	}

	/**
	 * Gets the publishing request.
	 *
	 * @param id
	 *            the id
	 * @return the publishing request
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public PublishRequestDefinition getPublishingRequest(long id) throws PublisherException {
		PublishRequestDefinition publishRequestDefinition = publishCoreService.getPublishRequest(id);
		return publishRequestDefinition;
	}

	/**
	 * List publishing log.
	 *
	 * @return the list
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public List<PublishLogDefinition> listPublishingLog() throws PublisherException {
		List<PublishLogDefinition> publishLogDefinitions = publishCoreService.getPublishLogs();
		return publishLogDefinitions;
	}

	/**
	 * Clear publishing log.
	 *
	 * @throws PublisherException
	 *             the publisher exception
	 */
	public void clearPublishingLog() throws PublisherException {
		List<PublishLogDefinition> publishLogDefinitions = publishCoreService.getPublishLogs();
		for (PublishLogDefinition publishLogDefinition : publishLogDefinitions) {
			publishCoreService.removePublishLog(publishLogDefinition.getId());
		}
	}

	/**
	 * Exists workspace.
	 *
	 * @param user
	 *            the user
	 * @param workspace
	 *            the workspace
	 * @return true, if successful
	 */
	public boolean existsWorkspace(String user, String workspace) {
		StringBuilder workspacePath = generateWorkspacePath(user, workspace, null, null);
		ICollection collection = repository.getCollection(workspacePath.toString());
		return collection.exists();
	}

	/**
	 * Generate workspace path.
	 *
	 * @param user
	 *            the user
	 * @param workspace
	 *            the workspace
	 * @param project
	 *            the project
	 * @param path
	 *            the path
	 * @return the string builder
	 */
	private StringBuilder generateWorkspacePath(String user, String workspace, String project, String path) {
		StringBuilder relativePath = new StringBuilder(IRepositoryStructure.PATH_USERS).append(IRepositoryStructure.SEPARATOR).append(user)
				.append(IRepositoryStructure.SEPARATOR).append(workspace);
		if (project != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR).append(project);
		}
		if (path != null) {
			relativePath.append(IRepositoryStructure.SEPARATOR).append(path);
		}
		return relativePath;
	}

}
