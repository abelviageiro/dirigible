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
angular.module('page', []);
angular.module('page').controller('PageController', function ($scope, $http) {
	
	
	$scope.methods = [{
		'key': '*',
		'label': '*'
	}, {
		'key': 'GET',
		'label': 'GET'
	}, {
		'key': 'POST',
		'label': 'POST'
	}, {
		'key': 'PUT',
		'label': 'PUT'
	}, {
		'key': 'DELETE',
		'label': 'DELETE'
	}, {
		'key': 'READ',
		'label': 'READ'
	}, {
		'key': 'WRITE',
		'label': 'WRITE'
	}];
	
	$scope.scopes = [{
		'key': 'HTTP',
		'label': 'HTTP'
	}, {
		'key': 'CMIS',
		'label': 'CMIS'
	}];
	
	$scope.openNewDialog = function() {
		$scope.actionType = 'new';
		$scope.entity = {};
		$scope.entity.method = '*';
		$scope.entity.scope = 'HTTP';
		toggleEntityModal();
	};

	$scope.openEditDialog = function(entity) {
		$scope.actionType = 'update';
		$scope.entity = entity;
		toggleEntityModal();
	};

	$scope.openDeleteDialog = function(entity) {
		$scope.actionType = 'delete';
		$scope.entity = entity;
		toggleEntityModal();
	};

	$scope.close = function() {
		load();
		toggleEntityModal();
	};
	
	$scope.create = function() {
		$scope.access.constraints.push($scope.entity);
		toggleEntityModal();
	};

	$scope.update = function() {
		// auto-wired
		toggleEntityModal();
	};

	$scope.delete = function() {
		$scope.access.constraints = $scope.access.constraints.filter(function(e) {
			return e !== $scope.entity;
		}); 
		toggleEntityModal();
	};

	
	function toggleEntityModal() {
		$('#entityModal').modal('toggle');
		$scope.error = null;
	}

	
	var messageHub = new FramesMessageHub();
	var contents;
	
	function getResource(resourcePath) {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', resourcePath, false);
        xhr.send();
        if (xhr.status === 200) {
        	return xhr.responseText;
        }
	}
	
	function loadContents(file) {
		if (file) {
			return getResource('../../../../../../services/v3/ide/workspaces' + file);
		}
		console.error('file parameter is not present in the URL');
	}

	function load() {
		var searchParams = new URLSearchParams(window.location.search);
		$scope.file = searchParams.get('file');
		contents = loadContents($scope.file);
		$scope.access = JSON.parse(contents);
		$scope.access.constraints.forEach(function(constraint){
			constraint.rolesLine = constraint.roles.join();	
		});
		
	}
	
	load();

	function saveContents(text) {
		console.log('Save called...');
		if ($scope.file) {
			var xhr = new XMLHttpRequest();
			xhr.open('PUT', '../../../../../../services/v3/ide/workspaces' + $scope.file);
			xhr.setRequestHeader('X-Requested-With', 'Fetch');
			xhr.onreadystatechange = function() {
				if (xhr.readyState === 4) {
					console.log('file saved: ' + $scope.file);
				}
			};
			xhr.send(text);
			messageHub.post({data: $scope.file}, 'editor.file.saved');
		} else {
			console.error('file parameter is not present in the request');
		}
	}
	
	var serializeAccess = function() {
		var accessContents = JSON.parse(JSON.stringify($scope.access));
		accessContents.constraints.forEach(function(constraint){
			if (constraint.rolesLine) {
				constraint.roles = constraint.rolesLine.split(',');
				delete constraint.rolesLine;
				delete constraint.$$hashKey;
			}
		});
		return accessContents;
	}

	$scope.save = function() {
		var accessContents = serializeAccess();
		contents = JSON.stringify(accessContents);
		saveContents(contents);
	};
	
	$scope.$watch(function() {
		var accessContents = serializeAccess();
		var access = JSON.stringify(accessContents);
		if (contents !== access) {
			messageHub.post({data: $scope.file}, 'editor.file.dirty');
		}
	});
	

});
