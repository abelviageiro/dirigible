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
package test.org.eclipse.dirigible.runtime.ide.generation.velocity;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.runtime.ide.generation.processor.GenerationProcessor;
import org.junit.Test;

public class VelocityGeneratorTest {
	
	@Test
	public void generate() throws IOException {
		GenerationProcessor generationProcessor = new GenerationProcessor();
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("testParameter", "testValue");
		byte[] result = generationProcessor.generateContent(parameters, "/location", "test $testParameter".getBytes(), null, null, "velocity");
		assertEquals("test testValue", new String(result));
	}

}
