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
package org.eclipse.dirigible.engine.js.api;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;

/**
 * The Javascript Engine Processor interface.
 */
public interface IJavascriptEngineProcessor {

	/**
	 * Execute service.
	 *
	 * @param module
	 *            the module
	 * @throws ScriptingException
	 *             the scripting exception
	 */
	public void executeService(String module) throws ScriptingException;

}
