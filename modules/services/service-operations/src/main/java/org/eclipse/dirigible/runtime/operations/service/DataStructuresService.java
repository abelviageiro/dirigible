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
package org.eclipse.dirigible.runtime.operations.service;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.database.ds.api.DataStructuresException;
import org.eclipse.dirigible.runtime.operations.processor.DataStructuresProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Front facing REST service serving the Data Structures.
 */
@Singleton
@Path("/ops/data-structures")
@RolesAllowed({ "Operator" })
@Api(value = "Operations - Data Structures", authorizations = { @Authorization(value = "basicAuth", scopes = {}) })
@ApiResponses({ @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
		@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Internal Server Error") })
public class DataStructuresService extends AbstractRestService implements IRestService {

	private static final Logger logger = LoggerFactory.getLogger(DataStructuresService.class);

	@Inject
	private DataStructuresProcessor processor;
	
	@Context
	private HttpServletResponse response;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
	 */
	@Override
	public Class<? extends IRestService> getType() {
		return DataStructuresService.class;
	}

	/**
	 * List all the data structures currently registered.
	 *
	 * @return the response
	 * @throws DataStructuresException the data structure exception
	 */
	@GET
	@Path("")
	@Produces({ "application/json" })
	public Response listDataStructures()
			throws DataStructuresException {
		String user = UserFacade.getName();
		if (user == null) {
			sendErrorForbidden(response, NO_LOGGED_IN_USER);
			return Response.status(Status.FORBIDDEN).build();
		}

		return Response.ok().entity(processor.list()).build();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractRestService#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}

}
