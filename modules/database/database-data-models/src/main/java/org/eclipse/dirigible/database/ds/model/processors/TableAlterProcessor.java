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
package org.eclipse.dirigible.database.ds.model.processors;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.database.ds.model.DataStructureTableColumnModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.DataTypeUtils;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.table.AlterTableBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Table Alter Processor.
 */
public class TableAlterProcessor {

	private static final Logger logger = LoggerFactory.getLogger(TableAlterProcessor.class);
	
	private static final String INCOMPATIBLE_CHANGE_OF_TABLE = "Incompatible change of table [%s] by adding a column [%s] which is [%s]"; //$NON-NLS-1$

	/**
	 * Execute the corresponding statement.
	 *
	 * @param connection the connection
	 * @param tableModel the table model
	 * @throws SQLException the SQL exception
	 */
	public static void execute(Connection connection, DataStructureTableModel tableModel) throws SQLException {
		logger.info("Processing Alter Table: " + tableModel.getName());
		
		String tableName = tableModel.getName();
		
//		ISqlDialect nativeDialect = SqlFactory.deriveDialect(connection);
		
		Map<String, String> columnDefinitions = new HashMap<String, String>();
		DatabaseMetaData dmd = connection.getMetaData();
		ResultSet rsColumns = dmd.getColumns(null, null, tableName, null);
		while (rsColumns.next()) {
//			String typeName = nativeDialect.getDataTypeName(DataTypeUtils.getDatabaseType(rsColumns.getInt(5)));
			String typeName = DataTypeUtils.getDatabaseTypeName(rsColumns.getInt(5));
			columnDefinitions.put(rsColumns.getString(4).toUpperCase(), typeName);
		}
		
		List<String> modelColumnNames = new ArrayList<String>();
		
		// ADD iteration
		for (DataStructureTableColumnModel columnModel : tableModel.getColumns()) {

			String name = columnModel.getName();
			DataType type = DataType.valueOf(columnModel.getType());
			String length = columnModel.getLength();
			boolean isNullable = columnModel.isNullable();
			boolean isPrimaryKey = columnModel.isPrimaryKey();
			boolean isUnique = columnModel.isUnique();
			String defaultValue = columnModel.getDefaultValue();
			String precision = columnModel.getPrecision();
			String scale = columnModel.getScale();
			String args = "";
			if (length != null) {
				if (type.equals(DataType.VARCHAR) || type.equals(DataType.CHAR)) {
					args = ISqlKeywords.OPEN + length + ISqlKeywords.CLOSE;
				}
			} else if ((precision != null) && (scale != null)) {
				if (type.equals(DataType.DECIMAL)) {
					args = ISqlKeywords.OPEN + precision + "," + scale + ISqlKeywords.CLOSE;
				}
			}
			if (defaultValue != null) {
				if ("".equals(defaultValue)) {
					if ((type.equals(DataType.VARCHAR) || type.equals(DataType.CHAR))) {
						args += " DEFAULT '" + defaultValue + "' ";
					}
				} else {
					args += " DEFAULT " + defaultValue + " ";
				}

			}
			
			modelColumnNames.add(name.toUpperCase());

			if (!columnDefinitions.containsKey(name.toUpperCase())) {
				
				AlterTableBuilder alterTableBuilder = SqlFactory.getNative(connection).alter().table(tableModel.getName());
				
				alterTableBuilder.add().column(name, type, isPrimaryKey, isNullable, isUnique, args);

				if (!isNullable) {
					throw new SQLException(String.format(INCOMPATIBLE_CHANGE_OF_TABLE, tableName, name, "NOT NULL"));
				}
				if (isPrimaryKey) {
					throw new SQLException(String.format(INCOMPATIBLE_CHANGE_OF_TABLE, tableName, name, "PRIMARY KEY"));
				}
				
				executeAlterBuilder(connection, alterTableBuilder);

			} else if (!columnDefinitions.get(name.toUpperCase()).equals(type.toString())) {
				throw new SQLException(String.format(INCOMPATIBLE_CHANGE_OF_TABLE, tableName, name, "of type " + columnDefinitions.get(name) + " to be changed to" + type));
			}
		}
		
		// DROP iteration
		
		for (String columnName : columnDefinitions.keySet()) {
			if (!modelColumnNames.contains(columnName.toUpperCase())) {
				AlterTableBuilder alterTableBuilder = SqlFactory.getNative(connection).alter().table(tableModel.getName());
				alterTableBuilder.drop().column(columnName, DataType.BOOLEAN);
				executeAlterBuilder(connection, alterTableBuilder);
			}
		}
		
	}

	private static void executeAlterBuilder(Connection connection, AlterTableBuilder alterTableBuilder)
			throws SQLException {
		final String sql = alterTableBuilder.build();
		PreparedStatement statement = connection.prepareStatement(sql);
		try {
			logger.info(sql);
			statement.executeUpdate();
		} catch (SQLException e) {
			logger.error(sql);
			logger.error(e.getMessage(), e);
			throw new SQLException(e.getMessage(), e);
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

}
