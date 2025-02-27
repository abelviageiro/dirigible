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
package org.eclipse.dirigible.api.v3.http;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.eclipse.dirigible.api.v3.http.client.HttpClientHeader;
import org.eclipse.dirigible.api.v3.http.client.HttpClientParam;
import org.eclipse.dirigible.api.v3.http.client.HttpClientProxyUtils;
import org.eclipse.dirigible.api.v3.http.client.HttpClientRequestOptions;
import org.eclipse.dirigible.api.v3.http.client.HttpClientResponse;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;

/**
 * Java face for HTTP operations
 */
public class HttpClientFacade implements IScriptingFacade {

	/**
	 * Performs a GET request for the specified URL and options
	 *
	 * @param url
	 *            the URL
	 * @param options
	 *            the options
	 * @return the response as JSON
	 * @throws IOException
	 *             In case an I/O exception occurs
	 */
	public static final String get(String url, String options) throws IOException {
		HttpClientRequestOptions httpClientRequestOptions = parseOptions(options);
		HttpGet httpGet = createGetRequest(url, httpClientRequestOptions);
		CloseableHttpClient httpClient = HttpClientProxyUtils.getHttpClient(httpClientRequestOptions.isSslTrustAllEnabled());
		CloseableHttpResponse response = httpClient.execute(httpGet);
		return processResponse(response, httpClientRequestOptions.isBinary());
	}

	/**
	 * Performs a POST request for the specified URL and options
	 *
	 * @param url
	 *            the URL
	 * @param options
	 *            the options
	 * @return the response as JSON
	 * @throws IOException
	 *             In case an I/O exception occurs
	 */
	public static final String post(String url, String options) throws IOException {
		HttpClientRequestOptions httpClientRequestOptions = parseOptions(options);
		HttpPost httpPost = createPostRequest(url, httpClientRequestOptions);
		CloseableHttpClient httpClient = HttpClientProxyUtils.getHttpClient(httpClientRequestOptions.isSslTrustAllEnabled());
		CloseableHttpResponse response = httpClient.execute(httpPost);
		return processResponse(response, httpClientRequestOptions.isBinary());
	}

	/**
	 * Performs a PUT request for the specified URL and options
	 *
	 * @param url
	 *            the URL
	 * @param options
	 *            the options
	 * @return the response as JSON
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static final String put(String url, String options) throws IOException {
		HttpClientRequestOptions httpClientRequestOptions = parseOptions(options);
		HttpPut httpPut = createPutRequest(url, httpClientRequestOptions);
		CloseableHttpClient httpClient = HttpClientProxyUtils.getHttpClient(httpClientRequestOptions.isSslTrustAllEnabled());
		CloseableHttpResponse response = httpClient.execute(httpPut);
		return processResponse(response, httpClientRequestOptions.isBinary());
	}

	/**
	 * Performs a PATCH request for the specified URL and options
	 *
	 * @param url
	 *            the URL
	 * @param options
	 *            the options
	 * @return the response as JSON
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static final String patch(String url, String options) throws IOException {
		HttpClientRequestOptions httpClientRequestOptions = parseOptions(options);
		HttpPatch httpPatch = createPatchRequest(url, httpClientRequestOptions);
		CloseableHttpClient httpClient = HttpClientProxyUtils.getHttpClient(httpClientRequestOptions.isSslTrustAllEnabled());
		CloseableHttpResponse response = httpClient.execute(httpPatch);
		return processResponse(response, httpClientRequestOptions.isBinary());
	}

	/**
	 * Performs a DELETE request for the specified URL and options
	 *
	 * @param url
	 *            the URL
	 * @param options
	 *            the options
	 * @return the response as JSON
	 * @throws IOException
	 *             In case an I/O exception occurs
	 */
	public static final String delete(String url, String options) throws IOException {
		HttpClientRequestOptions httpClientRequestOptions = parseOptions(options);
		HttpDelete httpDelete = createDeleteRequest(url, httpClientRequestOptions);

		CloseableHttpClient httpClient = HttpClientProxyUtils.getHttpClient(httpClientRequestOptions.isSslTrustAllEnabled());
		CloseableHttpResponse response = httpClient.execute(httpDelete);
		return processResponse(response, httpClientRequestOptions.isBinary());
	}

	/**
	 * Performs a HEAD request for the specified URL and options
	 *
	 * @param url
	 *            the URL
	 * @param options
	 *            the options
	 * @return the response as JSON
	 * @throws IOException
	 *             In case an I/O exception occurs
	 */
	public static final String head(String url, String options) throws IOException {
		HttpClientRequestOptions httpClientRequestOptions = parseOptions(options);
		HttpHead httpHead = createHeadRequest(url, httpClientRequestOptions);

		CloseableHttpClient httpClient = HttpClientProxyUtils.getHttpClient(httpClientRequestOptions.isSslTrustAllEnabled());
		CloseableHttpResponse response = httpClient.execute(httpHead);
		return processResponse(response, httpClientRequestOptions.isBinary());
	}

	/**
	 * Performs a TRACE request for the specified URL and options
	 *
	 * @param url
	 *            the URL
	 * @param options
	 *            the options
	 * @return the response as JSON
	 * @throws IOException
	 *             In case an I/O exception occurs
	 */
	public static final String trace(String url, String options) throws IOException {
		HttpClientRequestOptions httpClientRequestOptions = parseOptions(options);
		HttpTrace httpTrace = createTraceRequest(url, httpClientRequestOptions);

		CloseableHttpClient httpClient = HttpClientProxyUtils.getHttpClient(httpClientRequestOptions.isSslTrustAllEnabled());
		CloseableHttpResponse response = httpClient.execute(httpTrace);
		return processResponse(response, httpClientRequestOptions.isBinary());
	}

	private static void prepareHeaders(HttpClientRequestOptions httpClientRequestOptions, HttpRequestBase httpRequestBase) {
		for (HttpClientHeader httpClientHeader : httpClientRequestOptions.getHeaders()) {
			httpRequestBase.setHeader(httpClientHeader.getName(), httpClientHeader.getValue());
		}
	}

	private static String processResponse(CloseableHttpResponse response, boolean binary) throws IOException {
		try {
			HttpClientResponse httpClientResponse = new HttpClientResponse();
			httpClientResponse.setStatusCode(response.getStatusLine().getStatusCode());
			httpClientResponse.setStatusMessage(response.getStatusLine().getReasonPhrase());
			httpClientResponse.setProtocol(response.getProtocolVersion().getProtocol());
			httpClientResponse.setProtocol(response.getProtocolVersion().getProtocol());
			HttpEntity entity = response.getEntity();
			if (entity.getContent() != null) {
				byte[] content = IOUtils.toByteArray(entity.getContent());

				if ((ContentType.getOrDefault(entity).getMimeType().equals(ContentType.TEXT_PLAIN.getMimeType())
						|| ContentType.getOrDefault(entity).getMimeType().equals(ContentType.TEXT_HTML.getMimeType())
						|| ContentType.getOrDefault(entity).getMimeType().equals(ContentType.TEXT_XML.getMimeType())
						|| ContentType.getOrDefault(entity).getMimeType().equals(ContentType.APPLICATION_JSON.getMimeType())
						|| ContentType.getOrDefault(entity).getMimeType().equals(ContentType.APPLICATION_ATOM_XML.getMimeType())
						|| ContentType.getOrDefault(entity).getMimeType().equals(ContentType.APPLICATION_XML.getMimeType())
						|| ContentType.getOrDefault(entity).getMimeType().equals(ContentType.APPLICATION_XHTML_XML.getMimeType())) && (!binary)) {
					Charset charset = ContentType.getOrDefault(entity).getCharset();
					String text = new String(content, charset != null ? charset : StandardCharsets.UTF_8);
					httpClientResponse.setText(text);
				} else {
					httpClientResponse.setData(content);
				}
			}

			for (Header header : response.getAllHeaders()) {
				httpClientResponse.getHeaders().add(new HttpClientHeader(header.getName(), header.getValue()));
			}
			EntityUtils.consume(entity);
			return GsonHelper.GSON.toJson(httpClientResponse);
		} finally {
			response.close();
		}
	}

	/**
	 * Parse HTTP Request Options
	 * 
	 * @param options
	 * @return
	 */
	public static HttpClientRequestOptions parseOptions(String options) {
		return GsonHelper.GSON.fromJson(options, HttpClientRequestOptions.class);
	}

	/**
	 * Prepare HTTP Request Configurations
	 * 
	 * @param httpClientRequestOptions
	 * @return
	 */
	public static RequestConfig prepareConfig(String httpClientRequestOptions) {
		return prepareConfig(parseOptions(httpClientRequestOptions));
	}

	/**
	 * Prepare HTTP Request Configurations
	 * 
	 * @param httpClientRequestOptions
	 * @return
	 */
	public static RequestConfig prepareConfig(HttpClientRequestOptions httpClientRequestOptions) {
		RequestConfig.Builder configBuilder = RequestConfig.custom();
		configBuilder.setAuthenticationEnabled(httpClientRequestOptions.isAuthenticationEnabled())
				.setCircularRedirectsAllowed(httpClientRequestOptions.isCircularRedirectsAllowed())
				.setContentCompressionEnabled(httpClientRequestOptions.isContentCompressionEnabled())
				.setExpectContinueEnabled(httpClientRequestOptions.isExpectContinueEnabled())
				.setRedirectsEnabled(httpClientRequestOptions.isRedirectsEnabled())
				.setRelativeRedirectsAllowed(httpClientRequestOptions.isRelativeRedirectsAllowed())
				.setMaxRedirects(httpClientRequestOptions.getMaxRedirects())
				.setConnectionRequestTimeout(httpClientRequestOptions.getConnectionRequestTimeout())
				.setConnectTimeout(httpClientRequestOptions.getConnectTimeout()).setSocketTimeout(httpClientRequestOptions.getSocketTimeout())
				.setCookieSpec(httpClientRequestOptions.getCookieSpec())
				.setProxyPreferredAuthSchemes(httpClientRequestOptions.getProxyPreferredAuthSchemes())
				.setTargetPreferredAuthSchemes(httpClientRequestOptions.getTargetPreferredAuthSchemes());

		if ((httpClientRequestOptions.getProxyHost() != null) && (httpClientRequestOptions.getProxyPort() != 0)) {
			configBuilder.setProxy(new HttpHost(httpClientRequestOptions.getProxyHost(), httpClientRequestOptions.getProxyPort()));
		}

		RequestConfig config = configBuilder.build();
		return config;
	}

	/**
	 * Build HTTP GET Request
	 * 
	 * @param url
	 * @param httpClientRequestOptions
	 * @return
	 */
	public static HttpGet createGetRequest(String url, HttpClientRequestOptions httpClientRequestOptions) {
		HttpGet httpGet = new HttpGet(url);
		RequestConfig config = prepareConfig(httpClientRequestOptions);
		httpGet.setConfig(config);
		prepareHeaders(httpClientRequestOptions, httpGet);
		return httpGet;
	}

	/**
	 * Build HTTP POST Request
	 * 
	 * @param url
	 * @param httpClientRequestOptions
	 * @return
	 */
	public static final HttpPost createPostRequest(String url, HttpClientRequestOptions httpClientRequestOptions) throws IOException {
		if (httpClientRequestOptions.getData() != null) {
			return createPostBinaryRequest(url, httpClientRequestOptions);
		} else if (httpClientRequestOptions.getText() != null) {
			return createPostTextRequest(url, httpClientRequestOptions);
		} else if (httpClientRequestOptions.getParams() != null) {
			return createPostFormRequest(url, httpClientRequestOptions);
		} else if (httpClientRequestOptions.getFiles() != null) {
			return createPostFilesRequest(url, httpClientRequestOptions);
		}
		throw new IllegalArgumentException("The element [data] or [text] or [params] or [files] in [options] have to be set for POST requests");
	}

	private static final HttpPost createPostBinaryRequest(String url, HttpClientRequestOptions httpClientRequestOptions) throws IOException {
		RequestConfig config = prepareConfig(httpClientRequestOptions);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(config);
		prepareHeaders(httpClientRequestOptions, httpPost);
		HttpEntity entity = EntityBuilder.create().setBinary(httpClientRequestOptions.getData()).setContentType(ContentType.APPLICATION_OCTET_STREAM).build();
		httpPost.setEntity(entity);
		return httpPost;
	}

	private static final HttpPost createPostTextRequest(String url, HttpClientRequestOptions httpClientRequestOptions) throws IOException {
		if (httpClientRequestOptions.getText() == null) {
			throw new IllegalArgumentException("The element [text] in [options] cannot be null for POST requests in [text] mode");
		}
		RequestConfig config = prepareConfig(httpClientRequestOptions);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(config);
		prepareHeaders(httpClientRequestOptions, httpPost);

		EntityBuilder entityBuilder = EntityBuilder.create().setText(httpClientRequestOptions.getText()).setContentType(ContentType.create(httpClientRequestOptions.getContentType()));
		if (httpClientRequestOptions.isCharacterEncodingEnabled()) {
			entityBuilder.setContentEncoding(httpClientRequestOptions.getCharacterEncoding());
		}
		httpPost.setEntity(entityBuilder.build());
		return httpPost;
	}

	private static final HttpPost createPostFormRequest(String url, HttpClientRequestOptions httpClientRequestOptions) throws IOException {
		if (httpClientRequestOptions.getParams() == null) {
			throw new IllegalArgumentException("The element [params] in [options] cannot be null for POST requests in [form] mode");
		}
		RequestConfig config = prepareConfig(httpClientRequestOptions);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(config);
		prepareHeaders(httpClientRequestOptions, httpPost);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (HttpClientParam httpClientParam : httpClientRequestOptions.getParams()) {
			params.add(new BasicNameValuePair(httpClientParam.getName(), httpClientParam.getValue()));
		}
		HttpEntity entity = new UrlEncodedFormEntity(params);
		httpPost.setEntity(entity);
		return httpPost;
	}

	private static final HttpPost createPostFilesRequest(String url, HttpClientRequestOptions httpClientRequestOptions) throws IOException {
		if (httpClientRequestOptions.getParams() == null) {
			throw new IllegalArgumentException("The element [files] in [options] cannot be null for POST requests in [file] mode");
		}
		RequestConfig config = prepareConfig(httpClientRequestOptions);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(config);
		prepareHeaders(httpClientRequestOptions, httpPost);
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
		for (String filePath : httpClientRequestOptions.getFiles()) {
			File file = new File(filePath);
			multipartEntityBuilder.addBinaryBody(file.getName(), file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
		}
		HttpEntity entity = multipartEntityBuilder.build();
		httpPost.setEntity(entity);
		return httpPost;
	}

	/**
	 * Build HTTP PUT Request
	 * 
	 * @param url
	 * @param httpClientRequestOptions
	 * @return
	 */
	public static final HttpPut createPutRequest(String url, HttpClientRequestOptions httpClientRequestOptions) throws IOException {
		if (httpClientRequestOptions.getData() != null) {
			return createPutBinaryRequest(url, httpClientRequestOptions);
		} else if (httpClientRequestOptions.getText() != null) {
			return createPutTextRequest(url, httpClientRequestOptions);
		} else if (httpClientRequestOptions.getParams() != null) {
			return createPutFormRequest(url, httpClientRequestOptions);
		} else if (httpClientRequestOptions.getFiles() != null) {
			return createPutFilesRequest(url, httpClientRequestOptions);
		}
		throw new IllegalArgumentException("The element [data] or [text] or [params] or [files] in [options] have to be set for PUT requests");
	}

	private static final HttpPut createPutBinaryRequest(String url, HttpClientRequestOptions httpClientRequestOptions) throws IOException {
		RequestConfig config = prepareConfig(httpClientRequestOptions);
		HttpPut httpPut = new HttpPut(url);
		httpPut.setConfig(config);
		prepareHeaders(httpClientRequestOptions, httpPut);
		HttpEntity entity = EntityBuilder.create().setBinary(httpClientRequestOptions.getData()).setContentType(ContentType.APPLICATION_OCTET_STREAM).build();
		httpPut.setEntity(entity);
		return httpPut;
	}

	private static final HttpPut createPutTextRequest(String url, HttpClientRequestOptions httpClientRequestOptions) throws IOException {
		if (httpClientRequestOptions.getText() == null) {
			throw new IllegalArgumentException("The element [text] in [options] cannot be null for POST requests in [text] mode");
		}
		RequestConfig config = prepareConfig(httpClientRequestOptions);
		HttpPut httpPut = new HttpPut(url);
		httpPut.setConfig(config);
		prepareHeaders(httpClientRequestOptions, httpPut);
		EntityBuilder entityBuilder = EntityBuilder.create().setText(httpClientRequestOptions.getText()).setContentType(ContentType.create(httpClientRequestOptions.getContentType()));
		if (httpClientRequestOptions.isCharacterEncodingEnabled()) {
			entityBuilder.setContentEncoding(httpClientRequestOptions.getCharacterEncoding());
		}
		httpPut.setEntity(entityBuilder.build());
		return httpPut;
	}

	private static final HttpPut createPutFormRequest(String url, HttpClientRequestOptions httpClientRequestOptions) throws IOException {
		if (httpClientRequestOptions.getParams() == null) {
			throw new IllegalArgumentException("The element [params] in [options] cannot be null for POST requests in [form] mode");
		}
		RequestConfig config = prepareConfig(httpClientRequestOptions);
		HttpPut httpPut = new HttpPut(url);
		httpPut.setConfig(config);
		prepareHeaders(httpClientRequestOptions, httpPut);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (HttpClientParam httpClientParam : httpClientRequestOptions.getParams()) {
			params.add(new BasicNameValuePair(httpClientParam.getName(), httpClientParam.getValue()));
		}
		HttpEntity entity = new UrlEncodedFormEntity(params);
		httpPut.setEntity(entity);
		return httpPut;
	}

	private static final HttpPut createPutFilesRequest(String url, HttpClientRequestOptions httpClientRequestOptions) throws IOException {
		if (httpClientRequestOptions.getParams() == null) {
			throw new IllegalArgumentException("The element [files] in [options] cannot be null for POST requests in [file] mode");
		}
		RequestConfig config = prepareConfig(httpClientRequestOptions);
		HttpPut httpPut = new HttpPut(url);
		httpPut.setConfig(config);
		prepareHeaders(httpClientRequestOptions, httpPut);
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
		for (String filePath : httpClientRequestOptions.getFiles()) {
			File file = new File(filePath);
			multipartEntityBuilder.addBinaryBody(file.getName(), file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
		}
		HttpEntity entity = multipartEntityBuilder.build();
		httpPut.setEntity(entity);
		return httpPut;
	}

	/**
	 * Build HTTP PATCH Request
	 * 
	 * @param url
	 * @param httpClientRequestOptions
	 * @return
	 */
	public static final HttpPatch createPatchRequest(String url, HttpClientRequestOptions httpClientRequestOptions) throws IOException {
		if (httpClientRequestOptions.getData() != null) {
			return createPatchBinaryRequest(url, httpClientRequestOptions);
		} else if (httpClientRequestOptions.getText() != null) {
			return createPatchTextRequest(url, httpClientRequestOptions);
		} else if (httpClientRequestOptions.getParams() != null) {
			return createPatchFormRequest(url, httpClientRequestOptions);
		} else if (httpClientRequestOptions.getFiles() != null) {
			return createPatchFilesRequest(url, httpClientRequestOptions);
		}
		throw new IllegalArgumentException("The element [data] or [text] or [params] or [files] in [options] have to be set for PATCH requests");
	}

	private static final HttpPatch createPatchBinaryRequest(String url, HttpClientRequestOptions httpClientRequestOptions) throws IOException {
		RequestConfig config = prepareConfig(httpClientRequestOptions);
		HttpPatch httpPatch = new HttpPatch(url);
		httpPatch.setConfig(config);
		prepareHeaders(httpClientRequestOptions, httpPatch);
		HttpEntity entity = EntityBuilder.create().setBinary(httpClientRequestOptions.getData()).setContentType(ContentType.APPLICATION_OCTET_STREAM).build();
		httpPatch.setEntity(entity);
		return httpPatch;
	}

	private static final HttpPatch createPatchTextRequest(String url, HttpClientRequestOptions httpClientRequestOptions) throws IOException {
		if (httpClientRequestOptions.getText() == null) {
			throw new IllegalArgumentException("The element [text] in [options] cannot be null for POST requests in [text] mode");
		}
		RequestConfig config = prepareConfig(httpClientRequestOptions);
		HttpPatch httpPatch = new HttpPatch(url);
		httpPatch.setConfig(config);
		prepareHeaders(httpClientRequestOptions, httpPatch);
		EntityBuilder entityBuilder = EntityBuilder.create().setText(httpClientRequestOptions.getText()).setContentType(ContentType.create(httpClientRequestOptions.getContentType()));
		if (httpClientRequestOptions.isCharacterEncodingEnabled()) {
			entityBuilder.setContentEncoding(httpClientRequestOptions.getCharacterEncoding());
		}
		httpPatch.setEntity(entityBuilder.build());
		return httpPatch;
	}

	private static final HttpPatch createPatchFormRequest(String url, HttpClientRequestOptions httpClientRequestOptions) throws IOException {
		if (httpClientRequestOptions.getParams() == null) {
			throw new IllegalArgumentException("The element [params] in [options] cannot be null for POST requests in [form] mode");
		}
		RequestConfig config = prepareConfig(httpClientRequestOptions);
		HttpPatch httpPatch = new HttpPatch(url);
		httpPatch.setConfig(config);
		prepareHeaders(httpClientRequestOptions, httpPatch);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (HttpClientParam httpClientParam : httpClientRequestOptions.getParams()) {
			params.add(new BasicNameValuePair(httpClientParam.getName(), httpClientParam.getValue()));
		}
		HttpEntity entity = new UrlEncodedFormEntity(params);
		httpPatch.setEntity(entity);
		return httpPatch;
	}

	private static final HttpPatch createPatchFilesRequest(String url, HttpClientRequestOptions httpClientRequestOptions) throws IOException {
		if (httpClientRequestOptions.getParams() == null) {
			throw new IllegalArgumentException("The element [files] in [options] cannot be null for POST requests in [file] mode");
		}
		RequestConfig config = prepareConfig(httpClientRequestOptions);
		HttpPatch httpPatch = new HttpPatch(url);
		httpPatch.setConfig(config);
		prepareHeaders(httpClientRequestOptions, httpPatch);
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
		for (String filePath : httpClientRequestOptions.getFiles()) {
			File file = new File(filePath);
			multipartEntityBuilder.addBinaryBody(file.getName(), file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
		}
		HttpEntity entity = multipartEntityBuilder.build();
		httpPatch.setEntity(entity);
		return httpPatch;
	}

	/**
	 * Build HTTP DELETE Request
	 * 
	 * @param url
	 * @param httpClientRequestOptions
	 * @return
	 */
	public static HttpDelete createDeleteRequest(String url, HttpClientRequestOptions httpClientRequestOptions) {
		HttpDelete httpDelete = new HttpDelete(url);
		RequestConfig config = prepareConfig(httpClientRequestOptions);
		httpDelete.setConfig(config);
		prepareHeaders(httpClientRequestOptions, httpDelete);
		return httpDelete;
	}

	/**
	 * Build HTTP HEAD Request
	 * 
	 * @param url
	 * @param httpClientRequestOptions
	 * @return
	 */
	public static HttpHead createHeadRequest(String url, HttpClientRequestOptions httpClientRequestOptions) {
		RequestConfig config = prepareConfig(httpClientRequestOptions);
		HttpHead httpHead = new HttpHead(url);
		httpHead.setConfig(config);
		prepareHeaders(httpClientRequestOptions, httpHead);
		return httpHead;
	}

	/**
	 * Build HTTP TRACE Request
	 * 
	 * @param url
	 * @param httpClientRequestOptions
	 * @return
	 */
	public static HttpTrace createTraceRequest(String url, HttpClientRequestOptions httpClientRequestOptions) {
		RequestConfig config = prepareConfig(httpClientRequestOptions);
		HttpTrace httpTrace = new HttpTrace(url);
		httpTrace.setConfig(config);
		prepareHeaders(httpClientRequestOptions, httpTrace);
		return httpTrace;
	}
}
