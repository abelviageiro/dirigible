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
package org.eclipse.dirigible.cms.managed;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.dirigible.cms.api.ICmsProvider;
import org.eclipse.dirigible.commons.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsProviderManaged implements ICmsProvider {
	
	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_JNDI_NAME. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_JNDI_NAME = "DIRIGIBLE_CMS_MANAGED_CONFIGURATION_JNDI_NAME"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD = "DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD_KEY. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD_KEY = "key"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD_DEST. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD_DEST = "destination"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_NAME. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_NAME = "DIRIGIBLE_CMS_MANAGED_CONFIGURATION_NAME"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_KEY. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_KEY = "DIRIGIBLE_CMS_MANAGED_CONFIGURATION_KEY"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CMS_MANAGED_CONFIGURATION_DESTINATION. */
	public static final String DIRIGIBLE_CMS_MANAGED_CONFIGURATION_DESTINATION = "DIRIGIBLE_CMS_MANAGED_CONFIGURATION_DESTINATION"; //$NON-NLS-1$
	
	/** The Constant DIRIGIBLE_CONNECTIVITY_CONFIGURATION_JNDI_NAME. */
	public static final String DIRIGIBLE_CONNECTIVITY_CONFIGURATION_JNDI_NAME = "DIRIGIBLE_CONNECTIVITY_CONFIGURATION_JNDI_NAME"; //$NON-NLS-1$

	/** The Constant NAME. */
	public static final String NAME = "cmis"; //$NON-NLS-1$

	/** The Constant TYPE. */
	public static final String TYPE = "managed"; //$NON-NLS-1$

	private static final String PARAM_USER = "User"; //$NON-NLS-1$
	private static final String PARAM_PASSWORD = "Password"; //$NON-NLS-1$

	private static final Logger logger = LoggerFactory.getLogger(CmsProviderManaged.class);
	
	private Object cmisSession;
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public Object getSession() {
		if (this.cmisSession == null) {
			try {
				this.cmisSession = lookupCmisSession();
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NamingException e) {
				String message = "Error in initializing the managed CMIS session";
				logger.error(message, e);
				throw new IllegalStateException(message, e);
			}
		}
		return this.cmisSession;
	}
	
	/**
	 * Retrieve the CMIS Configuration from the target platform
	 *
	 * @return the managed CMIS session
	 * @throws NamingException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public Object lookupCmisSession() throws NamingException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		final InitialContext ctx = new InitialContext();
		Configuration.load("/dirigible-cms.properties");
		String key = Configuration.get(DIRIGIBLE_CMS_MANAGED_CONFIGURATION_JNDI_NAME);
		if (key != null) {
			Object ecmService = ctx.lookup(key);
			if (ecmService != null) {
				String authMethod = Configuration.get(DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD);
				logger.debug(String.format("CMIS Authentication Method: %s", authMethod));
				String uniqueName = null;
				String secretKey = null;
				if (DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD_KEY.equals(authMethod)) {
					uniqueName = Configuration.get(DIRIGIBLE_CMS_MANAGED_CONFIGURATION_NAME);
					secretKey = Configuration.get(DIRIGIBLE_CMS_MANAGED_CONFIGURATION_KEY);
				} else if (DIRIGIBLE_CMS_MANAGED_CONFIGURATION_AUTH_METHOD_DEST.equals(authMethod)) {
					String destinationName = Configuration.get(DIRIGIBLE_CMS_MANAGED_CONFIGURATION_DESTINATION);
					Map destinationPropeties = initializeFromDestination(destinationName);
					if (destinationPropeties.get(PARAM_USER) != null) {
						uniqueName = (String) destinationPropeties.get(PARAM_USER);
					}
					if (destinationPropeties.get(PARAM_PASSWORD) != null) {
						secretKey = (String) destinationPropeties.get(PARAM_PASSWORD);
					}
				} else {
					String message = String.format("Connection to CMIS Repository was failed. Invalid Authentication Method: %s", authMethod);
					logger.error(message);
					throw new SecurityException(message);
				}
				logger.info(String.format("Connecting to CMIS Repository with name: %s for type: %s", uniqueName, authMethod));
				try {
					Method connectMethod = ecmService.getClass().getMethod("connect", String.class, String.class);
					Object openCmisSession = connectMethod.invoke(ecmService, uniqueName, secretKey);
					if (openCmisSession != null) {
						logger.info(String.format("Connection to CMIS Repository with name: %s was successful.", uniqueName));
						return openCmisSession;
					}
				} catch (Throwable t) {
					String message = "Connection to CMIS Repository was failed.";
					logger.error(message, t);
					throw new IllegalStateException(message, t);
				}
			} else {
				String message = "ECM is requested as CMIS service, but it is not available.";
				logger.error(message);
				throw new IllegalStateException(message);
			}
		} else {
			String message = "CMIS service JNDI name has not been provided.";
			logger.error(message);
			throw new IllegalArgumentException(message);
		}
		String message = "Initializing the managed CMIS session failed.";
		logger.error(message);
		throw new IllegalStateException(message);
	}

	private Map initializeFromDestination(String destinationName) throws NamingException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		logger.debug(String.format("CMIS Lookup Destination: %s", destinationName));
		Object connectivityService = lookupConnectivityConfiguration();
		Method configurationMethod = connectivityService.getClass().getMethod("getConfiguration", String.class);
		Object destinationConfiguration = configurationMethod.invoke(connectivityService, destinationName);
		Method propertiesMethod = destinationConfiguration.getClass().getMethod("getAllProperties");
		Map destinationPropeties = (Map) propertiesMethod.invoke(destinationConfiguration);
		logger.debug(String.format("CMIS Destination Properties: %s", destinationPropeties.toString()));
		return destinationPropeties;
	}
	
	/**
	 * Retrieve the Connectivity Configuration from the target platform
	 *
	 * @return the managed connectivity service proxy
	 * @throws NamingException
	 */
	static Object lookupConnectivityConfiguration() throws NamingException {
		final InitialContext ctx = new InitialContext();
		String key = Configuration.get(DIRIGIBLE_CONNECTIVITY_CONFIGURATION_JNDI_NAME);
		if (key != null) {
			return ctx.lookup(key);
		}
		return null;
	}

}
