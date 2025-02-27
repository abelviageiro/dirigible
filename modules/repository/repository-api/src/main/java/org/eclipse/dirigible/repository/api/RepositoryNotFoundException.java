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
package org.eclipse.dirigible.repository.api;

/**
 * The Class RepositoryNotFoundException.
 */
public class RepositoryNotFoundException extends RepositoryReadException {

	/**
	 * Instantiates a new repository not found exception.
	 */
	public RepositoryNotFoundException() {
		super();
	}

	/**
	 * Instantiates a new repository not found exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public RepositoryNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new repository not found exception.
	 *
	 * @param message
	 *            the message
	 */
	public RepositoryNotFoundException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new repository not found exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public RepositoryNotFoundException(Throwable cause) {
		super(cause);
	}

}
