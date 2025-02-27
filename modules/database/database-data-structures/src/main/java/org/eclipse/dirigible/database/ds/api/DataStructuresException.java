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
package org.eclipse.dirigible.database.ds.api;

/**
 * The Data Structures Exception.
 */
public class DataStructuresException extends Exception {

	private static final long serialVersionUID = 5800180600419241248L;

	/**
	 * Instantiates a new data structures exception.
	 */
	public DataStructuresException() {
		super();
	}

	/**
	 * Instantiates a new data structures exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 * @param enableSuppression
	 *            the enable suppression
	 * @param writableStackTrace
	 *            the writable stack trace
	 */
	public DataStructuresException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new data structures exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public DataStructuresException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new data structures exception.
	 *
	 * @param message
	 *            the message
	 */
	public DataStructuresException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new data structures exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public DataStructuresException(Throwable cause) {
		super(cause);
	}

}
