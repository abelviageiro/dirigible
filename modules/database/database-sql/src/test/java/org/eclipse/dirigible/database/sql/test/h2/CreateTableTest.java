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
package org.eclipse.dirigible.database.sql.test.h2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.dialects.h2.H2SqlDialect;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class CreateTableTest.
 */
public class CreateTableTest {
	
	/**
	 * Creates the table generic.
	 */
	@Test
	public void createTableGeneric() {
		String sql = SqlFactory.getNative(new H2SqlDialect())
			.create()
			.table("CUSTOMERS")
			.column("ID", DataType.INTEGER, true, false, false)
			.column("FIRST_NAME", DataType.VARCHAR, false, false, true, "(20)")
			.column("LAST_NAME", DataType.VARCHAR, false, true, false, "(30)")
			.build();
		
		assertNotNull(sql);
		assertEquals("CREATE TABLE CUSTOMERS ( ID INTEGER NOT NULL PRIMARY KEY , FIRST_NAME VARCHAR (20) NOT NULL UNIQUE , LAST_NAME VARCHAR (30) )", sql);
	}
	
	/**
	 * Creates the table type safe.
	 */
	@Test
	public void createTableTypeSafe() {
		String sql = SqlFactory.getNative(new H2SqlDialect())
			.create()
			.table("CUSTOMERS")
			.columnInteger("ID", true, false, false)
			.columnVarchar("FIRST_NAME", 20, false, true, true)
			.columnVarchar("LAST_NAME", 30, false, true, false)
			.build();
		
		assertNotNull(sql);
		assertEquals("CREATE TABLE CUSTOMERS ( ID INTEGER NOT NULL PRIMARY KEY , FIRST_NAME VARCHAR (20) UNIQUE , LAST_NAME VARCHAR (30) )", sql);
	}

}
