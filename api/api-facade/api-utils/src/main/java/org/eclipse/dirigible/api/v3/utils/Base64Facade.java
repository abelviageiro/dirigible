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
package org.eclipse.dirigible.api.v3.utils;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.dirigible.commons.api.helpers.BytesHelper;

/**
 * The Class Base64Facade.
 */
public class Base64Facade {

	/**
	 * Base64 encode.
	 *
	 * @param input
	 *            the input
	 * @return the base64 encoded input
	 */
	public static final String encode(String input) {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return encode(bytes);
	}

	/**
	 * Base64 encode.
	 *
	 * @param input
	 *            the input
	 * @return the base64 encoded input
	 */
	public static final String encode(byte[] input) {
		Base64 base64 = new Base64();
		return base64.encodeAsString(input);
	}
	
	/**
	 * Base64 encode.
	 *
	 * @param input
	 *            the input
	 * @return the base64 encoded input
	 */
	public static final byte[] encodeNative(byte[] input) {
		Base64 base64 = new Base64();
		return base64.encode(input);
	}

	/**
	 * Base64 decode.
	 *
	 * @param input
	 *            the input
	 * @return the base64 decoded output
	 */
	public static final byte[] decode(String input) {
		Base64 base64 = new Base64();
		return base64.decode(input);
	}
	
	/**
	 * Base64 decode.
	 *
	 * @param input
	 *            the input
	 * @return the base64 decoded output
	 */
	public static final byte[] decodeNative(byte[] input) {
		Base64 base64 = new Base64();
		return base64.decode(input);
	}

}
