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
exports.getSources = function(parameters) {
    var sources = [];
    sources = sources.concat(getMaster(parameters));
    sources = sources.concat(getDetails(parameters));
    return sources;
};

function getMaster(parameters) {
    return [{
        'location': '/template-application-angular/ui/perspectives/views/master-list/index.html.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/index.html',
        'engine': 'velocity',
        'collection': 'uiListMasterModels'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/master-list/extensions/view.js.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/extensions/view.js',
        'collection': 'uiListMasterModels'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/master-list/master/index.html.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/index.html',
        'engine': 'velocity',
        'collection': 'uiListMasterModels'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/master-list/master/controller.js.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/controller.js',
        'engine': 'velocity',
        'collection': 'uiListMasterModels'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/master-list/master/extensions/view.js.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/extensions/view.js',
        'collection': 'uiListMasterModels'
    }, {
		'location': '/template-application-angular/ui/perspectives/views/master-list/extensions/entity-view.extensionpoint.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/extensions/view.extensionpoint',
		'collection': 'uiListMasterModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/master-list/extensions/view.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/extensions/view.extension',
		'collection': 'uiListMasterModels'
	}, {
		'location': '/template-application-angular/ui/perspectives/views/master-list/master/extensions/entity-view-master.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/{{fileName}}/master/extensions/view-master.extension',
		'collection': 'uiListMasterModels'
	}];
}

function getDetails(parameters) {
    return [{
        'location': '/template-application-angular/ui/perspectives/views/master-list/details/index.html.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/index.html',
        'engine': 'velocity',
        'collection': 'uiListDetailsModels'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/master-list/details/controller.js.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/controller.js',
        'engine': 'velocity',
        'collection': 'uiListDetailsModels'
    }, {
        'location': '/template-application-angular/ui/perspectives/views/master-list/details/extensions/view.js.template', 
        'action': 'generate',
        'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/extensions/view.js',
        'collection': 'uiListDetailsModels'
    }, {
		'location': '/template-application-angular/ui/perspectives/views/master-list/details/extensions/entity-view-detail.extension.template', 
		'action': 'generate',
		'rename': 'ui/{{perspectiveName}}/views/master/details/{{fileName}}/extensions/view-detail.extension',
		'engine': 'velocity',
		'collection': 'uiListDetailsModels'
	}];
}
