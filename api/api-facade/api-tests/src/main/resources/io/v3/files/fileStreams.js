/*
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var files = require('io/v3/files');
var streams = require('io/v3/streams');

var tempFile1 = files.createTempFile("dirigible", ".txt");
console.log('Temp file 1: ' + tempFile1);
files.writeText(tempFile1, "Eclipse Dirigible");

var tempFile2 = files.createTempFile("dirigible", ".txt");
console.log('Temp file 2: ' + tempFile2);

var input = files.createInputStream(tempFile1);
var output = files.createOutputStream(tempFile2);

streams.copy(input, output);

var result = files.readText(tempFile2);

files.deleteFile(tempFile1);
files.deleteFile(tempFile2);

result == "Eclipse Dirigible";
