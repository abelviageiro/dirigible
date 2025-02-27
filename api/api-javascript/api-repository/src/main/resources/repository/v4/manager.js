/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */

exports.getResource = function(path) {
	var resourceInstance = org.eclipse.dirigible.api.v3.repository.RepositoryFacade.getResource(path);
	var resource = new Resource();
	resource.native = resourceInstance;
	return resource;
};

exports.createResource = function(path, content, contentType) {
	var resourceInstance = org.eclipse.dirigible.api.v3.repository.RepositoryFacade.createResource(path, content, contentType);
	var resource = new Resource();
	resource.native = resourceInstance;
	return resource;
};

exports.updateResource = function(path, content) {
	var resourceInstance = org.eclipse.dirigible.api.v3.repository.RepositoryFacade.updateResource(path, content);
	var resource = new Resource();
	resource.native = resourceInstance;
	return resource;	
};

exports.deleteResource = function(path) {
	org.eclipse.dirigible.api.v3.repository.RepositoryFacade.deleteResource(path);
};

exports.getCollection = function(path) {
	var collectionInstnace = org.eclipse.dirigible.api.v3.repository.RepositoryFacade.getCollection(path);
	var collection = new Collection();
	collection.native = collectionInstnace;
	return collection;
};

exports.createCollection = function(path) {
	var collectionInstnace = org.eclipse.dirigible.api.v3.repository.RepositoryFacade.createCollection(path);
	var collection = new Collection();
	collection.native = collectionInstnace;
	return collection;
};

exports.deleteCollection = function(path) {
	org.eclipse.dirigible.api.v3.repository.RepositoryFacade.deleteCollection(path);
};

function Resource() {

	this.getName = function() {
		return this.native.getName();
	};

	this.getPath = function() {
		return this.native.getPath();
	};

	this.getParent = function() {
		var collectionInstance = this.native.getParent();
		var collection = new Collection();
		collection.native = collectionInstance;
		return collection;
	};

	this.getInformation = function() {
		var informationInstance = this.native.getInformation();
		var information = new EntityInformation();
		information.native = informationInstance;
		return information;
	};

	this.create = function() {
		this.native.create();
	};

	this.delete = function() {
		this.native.delete();
	};

	this.renameTo = function(name) {
		this.native.renameTo(name);
	};

	this.moveTo = function(path) {
		this.native.moveTo(path);
	};

	this.copyTo = function(path) {
		this.native.copyTo(path);
	};

	this.exists = function() {
		return this.native.exists();
	};

	this.isEmpty = function() {
		return this.native.isEmpty();
	};

	this.getContent = function() {
		return this.native.getContent();
	};

	this.setContent = function(content) {
		this.native.setContent(content);
	};

	this.isBinary = function() {
		this.native.isBinary();
	};

	this.getContentType = function() {
		this.native.getContentType();
	};
}

function Collection() {

	this.getName = function() {
		return this.native.getName();
	};

	this.getPath = function() {
		return this.native.getPath();
	};

	this.getParent = function() {
		var collectionInstance = this.native.getParent();
		var collection = new Collection();
		collection.native = collectionInstance;
		return collection;
	};

	this.getInformation = function() {
		var informationInstance = this.native.getInformation();
		var information = new EntityInformation();
		information.native = informationInstance;
		return information;
	};

	this.create = function() {
		this.native.create();
	};

	this.delete = function() {
		this.native.delete();
	};

	this.renameTo = function(name) {
		this.native.renameTo(name);
	};

	this.moveTo = function(path) {
		this.native.moveTo(path);
	};

	this.copyTo = function(path) {
		this.native.copyTo(path);
	};

	this.exists = function() {
		return this.native.exists();
	};

	this.isEmpty = function() {
		return this.native.isEmpty();
	};

	this.getCollectionsNames = function() {
		return this.native.getCollectionsNames();
	};

	this.createCollection = function(name) {
		var collectionInstance = this.native.createCollection(name);
		var collection = new Collection();
		collection.native = collectionInstance;
		return collection;
	};

	this.getCollection = function(name) {
		var collectionInstance = this.native.getCollection(name);
		var collection = new Collection();
		collection.native = collectionInstance;
		return collection;
	};

	this.removeCollection = function(name) {
		this.native.removeCollection(name);
	};

	this.getResourcesNames = function() {
		return this.native.getResourcesNames();
	};

	this.getResource = function(name) {
		var resourceInstance = this.native.getResource(name);
		var resource = new Resource();
		resource.native = resourceInstance;
		return resource;
	};

	this.removeResource = function(name) {
		this.native.removeResource(name);
	};

	this.createResource = function(name, content) {
		var resourceInstance = this.native.createResource(name, content);
		var resource = new Resource();
		resource.native = resourceInstance;
		return resource;
	};
}

function EntityInformation() {

	this.getName = function() {
		return this.native.getName();
	};

	this.getPath = function() {
		return this.native.getPath();
	};

	this.getPermissions = function() {
		return this.native.getPermissions();
	};

	this.getSize = function() {
		return this.native.getSize();
	};

	this.getCreatedBy = function() {
		return this.native.getCreatedBy();
	};

	this.getCreatedAt = function() {
		return this.native.getCreatedAt();
	};

	this.getModifiedBy = function() {
		return this.native.getModifiedBy();
	};

	this.getModifiedAt = function() {
		return this.native.getModifiedAt();
	};
}
