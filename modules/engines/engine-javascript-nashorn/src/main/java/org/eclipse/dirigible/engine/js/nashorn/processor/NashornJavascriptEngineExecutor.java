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
package org.eclipse.dirigible.engine.js.nashorn.processor;

import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.eclipse.dirigible.api.v3.core.ConsoleFacade;
import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.api.resource.ResourcePath;
import org.eclipse.dirigible.engine.js.api.AbstractJavascriptExecutor;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Nashorn Javascript Engine Executor.
 */
public class NashornJavascriptEngineExecutor extends AbstractJavascriptExecutor {

	private static final Logger logger = LoggerFactory.getLogger(NashornJavascriptEngineExecutor.class);
	
	public static final String ENGINE_NAME = "Nashorn JavaScript Engine";

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#executeServiceModule(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public Object executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException {
		return executeService(module, executionContext, true);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#executeServiceCode(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public Object executeServiceCode(String code, Map<Object, Object> executionContext) throws ScriptingException {
		return executeService(code, executionContext, false);
	}

	/**
	 * Execute service.
	 *
	 * @param moduleOrCode
	 *            the module or code
	 * @param executionContext
	 *            the execution context
	 * @param isModule
	 *            the is module
	 * @return the object
	 * @throws ScriptingException
	 *             the scripting exception
	 */
	public Object executeService(String moduleOrCode, Map<Object, Object> executionContext, boolean isModule) throws ScriptingException {
		logger.trace("entering: executeServiceModule()"); //$NON-NLS-1$
		logger.trace("module or code=" + moduleOrCode); //$NON-NLS-1$

		if (moduleOrCode == null) {
			throw new ScriptingException("JavaScript module name cannot be null");
		}

		if (isModule) {
			ResourcePath resourcePath = getResourcePath(moduleOrCode, MODULE_EXT_JS, MODULE_EXT_NASHORN);
			moduleOrCode = resourcePath.getModule();
			if (HttpRequestFacade.isValid()) {
				HttpRequestFacade.setAttribute(HttpRequestFacade.ATTRIBUTE_REST_RESOURCE_PATH, resourcePath.getPath());
			}
		}

		Object result = null;

		ScriptEngineManager engineManager = new ScriptEngineManager();
		// NashornScriptEngine engine = (NashornScriptEngine) engineManager.getEngineByName("nashorn");
		ScriptEngine engine = engineManager.getEngineByName("nashorn");

		try {
			NashornRepositoryModuleSourceProvider sourceProvider = createRepositoryModuleSourceProvider();
			Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put("SourceProvider", sourceProvider);

			bindings.put(IJavascriptEngineExecutor.JAVASCRIPT_ENGINE_TYPE, IJavascriptEngineExecutor.JAVASCRIPT_TYPE_NASHORN);
			bindings.put(IJavascriptEngineExecutor.CONSOLE, ConsoleFacade.getConsole());
			bindings.put(IJavascriptEngineExecutor.CONTEXT, executionContext);

			String code = (isModule ? sourceProvider.loadSource(moduleOrCode) : moduleOrCode);

			try {
				engine.eval(Require.CODE);
				engine.eval("load(\"nashorn:mozilla_compat.js\");");
				result = engine.eval(code);
				forceFlush();
			} catch (ScriptException e) {
				if ((e.getMessage() != null) && e.getMessage().contains("\"exports\" is not defined")) {
					String message = "Requested endpoint is not a service, but rather a library.";
					// throw new ScriptingDependencyException(message);
					logger.warn(message);
					return message;
				}
				throw new ScriptingException(e);
			}
		} catch (Throwable e) {
			throw new ScriptingException(e);
		}

		logger.trace("exiting: executeServiceModule()");

		return result;

	}

	/**
	 * Creates the repository module source provider.
	 *
	 * @return the nashorn repository module source provider
	 */
	private NashornRepositoryModuleSourceProvider createRepositoryModuleSourceProvider() {
		NashornRepositoryModuleSourceProvider repositoryModuleSourceProvider = null;
		repositoryModuleSourceProvider = new NashornRepositoryModuleSourceProvider(this, IRepositoryStructure.PATH_REGISTRY_PUBLIC);
		return repositoryModuleSourceProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#getType()
	 */
	@Override
	public String getType() {
		return JAVASCRIPT_TYPE_NASHORN;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.engine.api.script.IEngineExecutor#getName()
	 */
	@Override
	public String getName() {
		return ENGINE_NAME;
	}
}
