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
var digest = require('utils/v4/digest');

var input = [41, 42, 43];
var result = digest.sha256(input);

console.log(result);

result.length === 32 && result[0] === 100;
