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
package org.eclipse.dirigible.api.v3.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.cxf.helpers.IOUtils;
import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService;
import org.eclipse.dirigible.core.extensions.service.ExtensionsCoreService;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.junit.Before;
import org.mockito.Mockito;

public abstract class AbstractApiSuiteTest extends AbstractGuiceTest {

	private static List<String> TEST_MODULES = new ArrayList<String>();

	private IExtensionsCoreService extensionsCoreService;

	private IRepository repository;

	@Before
	public void setUp() throws Exception {
		this.extensionsCoreService = getInjector().getInstance(ExtensionsCoreService.class);
		this.repository = getInjector().getInstance(IRepository.class);
	}

	@Before
	public void registerModules() {

		// v3
		
		TEST_MODULES.add("core/v3/java/call.js");
		TEST_MODULES.add("core/v3/java/invoke.js");
		TEST_MODULES.add("core/v3/java/deep.js");
		TEST_MODULES.add("core/v3/java/null.js");
		
		TEST_MODULES.add("core/v3/console/log.js");

		TEST_MODULES.add("security/v3/user/getName.js");

		TEST_MODULES.add("core/v3/env/get.js");
		TEST_MODULES.add("core/v3/env/list.js");
		TEST_MODULES.add("core/v3/globals/get.js");
		TEST_MODULES.add("core/v3/globals/list.js");
		TEST_MODULES.add("core/v3/context/get.js");
		TEST_MODULES.add("core/v3/extensions/getExtensions.js");
		TEST_MODULES.add("core/v3/extensions/getExtensionPoints.js");

		TEST_MODULES.add("db/v3/database/getDatabaseTypes.js");
		TEST_MODULES.add("db/v3/database/getDataSources.js");
		TEST_MODULES.add("db/v3/database/getMetadata.js");
		TEST_MODULES.add("db/v3/database/getConnection.js");
		TEST_MODULES.add("db/v3/query/query.js");
		TEST_MODULES.add("db/v3/update/update.js");

		TEST_MODULES.add("http/v3/request/isValid.js");
		TEST_MODULES.add("http/v3/request/getMethod.js");
		TEST_MODULES.add("http/v3/request/getRemoteUser.js");
		TEST_MODULES.add("http/v3/request/getPathInfo.js");
		TEST_MODULES.add("http/v3/request/getPathTranslated.js");
		TEST_MODULES.add("http/v3/request/getHeader.js");
		TEST_MODULES.add("http/v3/request/isUserInRole.js");
		TEST_MODULES.add("http/v3/request/getAttribute.js");
		TEST_MODULES.add("http/v3/request/getAuthType.js");
		TEST_MODULES.add("http/v3/request/getHeaderNames.js");
		TEST_MODULES.add("http/v3/request/getServerName.js");
		TEST_MODULES.add("http/v3/response/getHeaderNames.js");
		TEST_MODULES.add("http/v3/client/get.js");
		TEST_MODULES.add("http/v3/client/get-binary.js");
		TEST_MODULES.add("http/v3/session/getAttributeNames.js");
		
		TEST_MODULES.add("io/v3/streams/copy.js");
		TEST_MODULES.add("io/v3/streams/text.js");
		TEST_MODULES.add("io/v3/files/createTempFile.js");
		TEST_MODULES.add("io/v3/files/fileStreams.js");

		TEST_MODULES.add("utils/v3/base64/encode.js");
		TEST_MODULES.add("utils/v3/base64/decode.js");
		TEST_MODULES.add("utils/v3/hex/encode.js");
		TEST_MODULES.add("utils/v3/hex/decode.js");
		TEST_MODULES.add("utils/v3/digest/sha1.js");
		TEST_MODULES.add("utils/v3/xml/fromJson.js");
		TEST_MODULES.add("utils/v3/xml/toJson.js");
		TEST_MODULES.add("utils/v3/uuid/validate.js");
		TEST_MODULES.add("utils/v3/uuid/alias.js");
		TEST_MODULES.add("utils/v3/uuid/alias-modules.js");
		TEST_MODULES.add("utils/v3/url/encode.js");
		TEST_MODULES.add("utils/v3/url/decode.js");
		
		TEST_MODULES.add("indexing/v3/writer/add.js");
		TEST_MODULES.add("indexing/v3/searcher/search.js");
		TEST_MODULES.add("indexing/v3/searcher/between.js");
		
		TEST_MODULES.add("cms/v3/cmis/getSession.js");
		TEST_MODULES.add("cms/v3/cmis/getRootFolder.js");
		TEST_MODULES.add("cms/v3/cmis/getChildren.js");
		
		// v4
		TEST_MODULES.add("utils/v4/base64/encode.js");
		TEST_MODULES.add("utils/v4/base64/decode.js");
		TEST_MODULES.add("utils/v4/digest/md5.js");
		TEST_MODULES.add("utils/v4/digest/md5Hex.js");
		TEST_MODULES.add("utils/v4/digest/sha1.js");
		TEST_MODULES.add("utils/v4/digest/sha1Hex.js");
		TEST_MODULES.add("utils/v4/digest/sha256.js");
		TEST_MODULES.add("utils/v4/digest/sha384.js");
		TEST_MODULES.add("utils/v4/digest/sha512.js");
		TEST_MODULES.add("utils/v4/hex/encode.js");
		TEST_MODULES.add("utils/v4/hex/decode.js");
		TEST_MODULES.add("utils/v4/escape/escapeCsv.js");
		TEST_MODULES.add("utils/v4/escape/unescapeCsv.js");
		TEST_MODULES.add("utils/v4/escape/escapeJavascript.js");
		TEST_MODULES.add("utils/v4/escape/unescapeJavascript.js");
		TEST_MODULES.add("utils/v4/escape/escapeHtml3.js");
		TEST_MODULES.add("utils/v4/escape/unescapeHtml3.js");
		TEST_MODULES.add("utils/v4/escape/escapeHtml4.js");
		TEST_MODULES.add("utils/v4/escape/unescapeHtml4.js");
		TEST_MODULES.add("utils/v4/escape/escapeJava.js");
		TEST_MODULES.add("utils/v4/escape/unescapeJava.js");
		TEST_MODULES.add("utils/v4/escape/escapeJson.js");
		TEST_MODULES.add("utils/v4/escape/unescapeJson.js");
		TEST_MODULES.add("utils/v4/escape/escapeXml.js");
		TEST_MODULES.add("utils/v4/escape/unescapeXml.js");
		TEST_MODULES.add("utils/v4/url/encode.js");
		TEST_MODULES.add("utils/v4/url/decode.js");
		TEST_MODULES.add("utils/v4/url/escape.js");
		TEST_MODULES.add("utils/v4/url/escapePath.js");
		TEST_MODULES.add("utils/v4/url/escapeForm.js");
		
		TEST_MODULES.add("db/v4/database/getDatabaseTypes.js");
		TEST_MODULES.add("db/v4/database/getDataSources.js");
		TEST_MODULES.add("db/v4/database/getMetadata.js");
		TEST_MODULES.add("db/v4/database/getConnection.js");
		TEST_MODULES.add("db/v4/query/query.js");
		TEST_MODULES.add("db/v4/update/update.js");
		TEST_MODULES.add("db/v4/sequence/nextval.js");
		
		TEST_MODULES.add("core/v4/configurations/get.js");
		TEST_MODULES.add("core/v4/console/log.js");
		TEST_MODULES.add("core/v4/context/get.js");
		TEST_MODULES.add("core/v4/env/get.js");
		TEST_MODULES.add("core/v4/env/list.js");
		TEST_MODULES.add("core/v4/extensions/getExtensions.js");
		TEST_MODULES.add("core/v4/extensions/getExtensionPoints.js");
		TEST_MODULES.add("core/v4/globals/get.js");
		TEST_MODULES.add("core/v4/globals/list.js");
		
		TEST_MODULES.add("indexing/v4/writer/add.js");
		TEST_MODULES.add("indexing/v4/searcher/search.js");
		TEST_MODULES.add("indexing/v4/searcher/between.js");
		
		TEST_MODULES.add("http/v4/request/isValid.js");
		TEST_MODULES.add("http/v4/request/getMethod.js");
		TEST_MODULES.add("http/v4/request/getRemoteUser.js");
		TEST_MODULES.add("http/v4/request/getPathInfo.js");
		TEST_MODULES.add("http/v4/request/getPathTranslated.js");
		TEST_MODULES.add("http/v4/request/getHeader.js");
		TEST_MODULES.add("http/v4/request/isUserInRole.js");
		TEST_MODULES.add("http/v4/request/getAttribute.js");
		TEST_MODULES.add("http/v4/request/getAuthType.js");
		TEST_MODULES.add("http/v4/request/getHeaderNames.js");
		TEST_MODULES.add("http/v4/request/getServerName.js");
		TEST_MODULES.add("http/v4/response/getHeaderNames.js");
		TEST_MODULES.add("http/v4/client/get.js");
		TEST_MODULES.add("http/v4/client/get-binary.js");
		TEST_MODULES.add("http/v4/session/getAttributeNames.js");
		
		TEST_MODULES.add("cms/v4/cmis/getSession.js");
		TEST_MODULES.add("cms/v4/cmis/getRootFolder.js");
		TEST_MODULES.add("cms/v4/cmis/getChildren.js");
		TEST_MODULES.add("cms/v4/cmis/createFolder.js");
		TEST_MODULES.add("cms/v4/cmis/createDocument.js");
		
		TEST_MODULES.add("io/v4/streams/copy.js");
		TEST_MODULES.add("io/v4/streams/text.js");
		TEST_MODULES.add("io/v4/files/createTempFile.js");
		TEST_MODULES.add("io/v4/files/fileStreams.js");
		
		TEST_MODULES.add("security/v4/user/getName.js");
		
	}

	public void runSuite(IJavascriptEngineExecutor executor, IRepository repository)
			throws RepositoryWriteException, IOException, ScriptingException, ContextException, ExtensionsException {
		for (String testModule : TEST_MODULES) {
			try {
				HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
				HttpServletResponse mockedResponse = Mockito.mock(HttpServletResponse.class);
				mockRequest(mockedRequest);
				mockResponse(mockedResponse);
		
				ThreadContextFacade.setUp();
				try {
					ThreadContextFacade.set(HttpServletRequest.class.getCanonicalName(), mockedRequest);
					ThreadContextFacade.set(HttpServletResponse.class.getCanonicalName(), mockedResponse);
					extensionsCoreService.createExtensionPoint("/test_extpoint1", "test_extpoint1", "Test");
					extensionsCoreService.createExtension("/test_ext1", "/test_ext_module1", "test_extpoint1", "Test");
					
					Object result = null;
					
					result = runTest(executor, repository, testModule);
					
					assertNotNull(result);
					assertTrue("API test failed: " + testModule, Boolean.parseBoolean(result.toString()));
					 
				} finally {
					extensionsCoreService.removeExtension("/test_ext1");
					extensionsCoreService.removeExtensionPoint("/test_extpoint1");
				}
			} finally {
				ThreadContextFacade.tearDown();
			}
		}
	}

	private void mockRequest(HttpServletRequest mockedRequest) {
		when(mockedRequest.getMethod()).thenReturn("GET");
		when(mockedRequest.getRemoteUser()).thenReturn("tester");
		when(mockedRequest.getPathInfo()).thenReturn("/path");
		when(mockedRequest.getPathTranslated()).thenReturn("/translated");
		when(mockedRequest.getHeader("header1")).thenReturn("header1");
		when(mockedRequest.getHeaderNames()).thenReturn(Collections.enumeration(Arrays.asList("header1", "header2")));
		when(mockedRequest.getServerName()).thenReturn("server1");
		when(mockedRequest.getHeader("header1")).thenReturn("header1");
		when(mockedRequest.isUserInRole("role1")).thenReturn(true);
		when(mockedRequest.getAttribute("attr1")).thenReturn("val1");
		when(mockedRequest.getAuthType()).thenReturn("Basic");
		
		HttpSession mockedSession = Mockito.mock(HttpSession.class);
		//when(mockedRequest.getSession()).thenReturn(mockedSession);
		when(mockedRequest.getSession(true)).thenReturn(mockedSession);
		mockSession(mockedSession);
	}
	
	private void mockSession(HttpSession mockedSession) {
		when(mockedSession.getAttributeNames()).thenReturn(Collections.enumeration(Arrays.asList("attr1")));
	}

	private void mockResponse(HttpServletResponse mockedResponse) {
		when(mockedResponse.getHeaderNames()).thenReturn(Arrays.asList("header1", "header2"));
	}

	private Object runTest(IJavascriptEngineExecutor executor, IRepository repository, String testModule) throws IOException, ScriptingException {

		try {
			InputStream in = AbstractApiSuiteTest.class.getResourceAsStream(IRepositoryStructure.SEPARATOR + testModule);
			try {
				if (in == null) {
					throw new IOException(IRepositoryStructure.SEPARATOR + testModule + " does not exist");
				}
				repository.createResource(
						IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepositoryStructure.SEPARATOR + testModule,
						IOUtils.readBytesFromStream(in));
			} finally {
				if (in != null) {
					in.close();
				}
			}
		} catch (RepositoryWriteException e) {
			throw new IOException(IRepositoryStructure.SEPARATOR + testModule, e);
		}
		Object result = null;
		long start = System.currentTimeMillis();
		result = executor.executeServiceModule(testModule, null);
		long time = System.currentTimeMillis() - start;
		System.out.println(String.format("API test [%s] on engine [%s] passed for: %d ms", testModule,
				executor.getType(), time));
		return result;
	}
}
