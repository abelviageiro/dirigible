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
package org.eclipse.dirigible.repository.local;

import static org.junit.Assert.fail;

import org.eclipse.dirigible.repository.generic.RepositoryGenericCacheTest;
import org.junit.Before;

// TODO: Auto-generated Javadoc
/**
 * The Class LocalRepositoryCacheTest.
 */
public class LocalRepositoryCacheTest extends RepositoryGenericCacheTest {

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		try {
			repository = new LocalRepository("target/test");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
