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
package org.eclipse.dirigible.database.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.dirigible.database.sql.builders.AlterBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.sql.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.sql.builders.records.SelectBuilder;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.sql.builders.sequence.LastValueIdentityBuilder;
import org.eclipse.dirigible.database.sql.builders.sequence.NextValueSequenceBuilder;

/**
 * The SQL Dialect interface.
 *
 * @param <SELECT>
 *            the generic type
 * @param <INSERT>
 *            the generic type
 * @param <UPDATE>
 *            the generic type
 * @param <DELETE>
 *            the generic type
 * @param <CREATE>
 *            the generic type
 * @param <ALTER>
 *            the generic type
 * @param <DROP>
 *            the generic type
 * @param <NEXT>
 *            the generic type
 */
public interface ISqlDialect<SELECT extends SelectBuilder, INSERT extends InsertBuilder, UPDATE extends UpdateBuilder, DELETE extends DeleteBuilder, CREATE extends CreateBranchingBuilder, ALTER extends AlterBranchingBuilder, DROP extends DropBranchingBuilder, NEXT extends NextValueSequenceBuilder, LAST extends LastValueIdentityBuilder>
		extends ISqlFactory<SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP, NEXT, LAST>, ISqlKeywords {

	/**
	 * Default implementation returns the direct toString() conversion. It may
	 * get overridden for specific database dialects
	 *
	 * @param dataType
	 *            the data type
	 * @return the data type name
	 */
	public String getDataTypeName(DataType dataType);

	/**
	 * PRIMARY KEY argument for a column for the create table script Default is
	 * "PRIMARY KEY".
	 *
	 * @return the primary key argument
	 */
	public String getPrimaryKeyArgument();

	/**
	 * Identity argument for a column for the create table script Default is
	 * "IDENTITY".
	 *
	 * @return the primary key argument
	 */
	public String getIdentityArgument();

	/**
	 * NOT NULL argument for a column for the create table script Default is
	 * "NOT NULL".
	 *
	 * @return the not null argument
	 */
	public String getNotNullArgument();

	/**
	 * UNIQUE argument for a column for the create table script Default is
	 * "UNIQUE".
	 *
	 * @return the unique argument
	 */
	public String getUniqueArgument();

	/**
	 * Check existence of a table.
	 *
	 * @param connection
	 *            the current connection
	 * @param table
	 *            the table name
	 * @return true if the table exists and false otherwise
	 * @throws SQLException
	 *             the SQL exception
	 */
	@Override
	public boolean exists(Connection connection, String table) throws SQLException;

	/**
	 * Returns the count of rows in the given table.
	 *
	 * @param connection
	 *            the current connection
	 * @param table
	 *            the table name
	 * @return count of rows
	 * @throws SQLException
	 *             the SQL exception
	 */
	@Override
	public int count(Connection connection, String table) throws SQLException;

	/**
	 * Checks if the database is capable of schema-level filtering statements
	 * (e.g. to reduce the provisioned schemas down to those that the current
	 * user is entitled to see).
	 *
	 * @return true if the feature is supported , false otherwise
	 */
	public boolean isSchemaFilterSupported();

	/**
	 * If the database supports schema filtering SQL statements (see
	 * {@link #isSchemaFilterSupported()}), this method provides the
	 * corresponding SQL statement.
	 *
	 * @return a filtering SQL statement
	 */
	public String getSchemaFilterScript();

	/**
	 * Does this database support catalogs synonymous to schemas.
	 *
	 * @return whether it is a catalog for schema
	 */
	boolean isCatalogForSchema();

	/**
	 * Gives the dialect specific name of the CURRENT_DATE function.
	 *
	 * @return the name of the function
	 */
	String functionCurrentDate();

	/**
	 * Gives the dialect specific name of the CURRENT_TIME function.
	 *
	 * @return the name of the function
	 */
	String functionCurrentTime();

	/**
	 * Gives the dialect specific name of the CURRENT_TIMESTAMP function.
	 *
	 * @return the name of the function
	 */
	String functionCurrentTimestamp();

	/**
	 * Checks if the database is capable to create and use Sequences.
	 *
	 * @return true if the feature is supported , false otherwise
	 */
	public boolean isSequenceSupported();
	
	/**
	 * Returns the database name
	 * 
	 * @param connection the active database connection 
	 * @return the database name
	 */
	public String getDatabaseName(Connection connection);
}
