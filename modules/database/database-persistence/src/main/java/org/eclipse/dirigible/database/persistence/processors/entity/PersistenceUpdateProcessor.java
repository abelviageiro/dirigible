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
package org.eclipse.dirigible.database.persistence.processors.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.eclipse.dirigible.database.persistence.IEntityManagerInterceptor;
import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.parser.Serializer;
import org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Persistence Update Processor.
 *
 * @param <T>
 *            the generic type
 */
public class PersistenceUpdateProcessor<T> extends AbstractPersistenceProcessor {

	private static final Logger logger = LoggerFactory.getLogger(PersistenceUpdateProcessor.class);

	/**
	 * Instantiates a new persistence update processor.
	 *
	 * @param entityManagerInterceptor
	 *            the entity manager interceptor
	 */
	public PersistenceUpdateProcessor(IEntityManagerInterceptor entityManagerInterceptor) {
		super(entityManagerInterceptor);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor#generateScript(java.sql.
	 * Connection, org.eclipse.dirigible.database.persistence.model.PersistenceTableModel)
	 */
	@Override
	protected String generateScript(Connection connection, PersistenceTableModel tableModel) {
		UpdateBuilder updateBuilder = SqlFactory.getNative(SqlFactory.deriveDialect(connection)).update().table(tableModel.getTableName());

		for (PersistenceTableColumnModel columnModel : tableModel.getColumns()) {
			if (!columnModel.isPrimaryKey()) {
				updateBuilder.set(columnModel.getName(), ISqlKeywords.QUESTION);
			}
		}

		updateBuilder.where(new StringBuilder() //
				.append(getPrimaryKey(tableModel)) //
				.append(ISqlKeywords.SPACE) //
				.append(ISqlKeywords.EQUALS) //
				.append(ISqlKeywords.SPACE) //
				.append(ISqlKeywords.QUESTION) //
				.toString());

		String sql = updateBuilder.toString();
		logger.trace(sql);
		return sql;
	}

	/**
	 * Update.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @param pojo
	 *            the pojo
	 * @return the int
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public int update(Connection connection, PersistenceTableModel tableModel, T pojo) throws PersistenceException {
		logger.trace("update -> connection: " + connection.hashCode() + ", tableModel: " + Serializer.serializeTableModel(tableModel) + ", pojo: "
				+ Serializer.serializePojo(pojo));
		String sql = null;
		PreparedStatement preparedStatement = null;
		try {
			PersistenceTableColumnModel primaryKeyColumnModel = getPrimaryKeyModel(tableModel);
			Object id = getValueFromPojo(pojo, primaryKeyColumnModel);
			if (id == null) {
				throw new PersistenceException("The key for update cannot be null.");
			}
			sql = generateScript(connection, tableModel);
			preparedStatement = openPreparedStatement(connection, sql);
			setValuesFromPojo(tableModel, pojo, preparedStatement);
			setValue(preparedStatement, tableModel.getColumns().size(), id);
			return preparedStatement.executeUpdate();
		} catch (Exception e) {
			logger.error(sql);
			logger.error(e.getMessage(), e);
			throw new PersistenceException(sql, e);
		} finally {
			closePreparedStatement(preparedStatement);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor#shouldSetColumnValue(org.
	 * eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel)
	 */
	@Override
	protected boolean shouldSetColumnValue(PersistenceTableColumnModel columnModel) {
		return !columnModel.isPrimaryKey();
	}
}
