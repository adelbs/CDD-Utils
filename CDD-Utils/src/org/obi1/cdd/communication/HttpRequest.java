package org.obi1.cdd.communication;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.obi1.cdd.util.Method;
import org.obi1.cdd.vo.RequestVo;

public class HttpRequest {

	private HttpClient httpclient;
	
	public HttpResponse execute(Method method, RequestVo requestVo, String url) throws Exception {
		return execute(method, requestVo, url, null, true, true);
	}
	
	public HttpResponse execute(Method method, RequestVo requestVo, String url, String body, boolean checkStatus200, boolean jsonBody) throws Exception {

		HttpResponse httpResponse;
		HttpRequestBase request = null;
		String httpUrl = requestVo.getUrl() + url;
		
		//Encoding
		if (httpUrl.indexOf("?") > -1) {
			String baseUrl = httpUrl.split("\\?")[0];
			String[] parameters = httpUrl.split("\\?")[1].split("&");
			String[] paramItem;
			
			baseUrl += "?";
			for (int i = 0; i < parameters.length; i++) {
				paramItem = parameters[i].split("=");
				for (String param : paramItem)
					baseUrl += URLEncoder.encode(param, "UTF-8") + "=";
				
				baseUrl = baseUrl.substring(0, baseUrl.length() - 1) + "&";
			}
			
			httpUrl = baseUrl.substring(0, baseUrl.length() - 1).replaceAll("\\+", "%20");
		}
		
		//Configuring method and header
		if (method == Method.GET)
			request = new HttpGet(httpUrl);
		else if (method == Method.POST)
			request = new HttpPost(httpUrl);
		else if (method == Method.PATCH)
			request = new HttpPatch(httpUrl);
		
		if (requestVo.getAuth() != null)
			request.addHeader("Authorization", requestVo.getAuth());
		
		if (requestVo.getHeader() != null && requestVo.getHeader().length() > 0) {
			String[] headers = requestVo.getHeader().split("\n");
			String[] headerItem;
			for (String headerLine : headers) {
				headerItem = headerLine.split(":");
				request.addHeader(headerItem[0], headerItem[1]);
			}
		}		

		if (jsonBody) {
			request.addHeader("Accept", "application/json");
			request.addHeader("Content-Type", "application/json");
		}
		else {
			request.addHeader("Accept", "text/plain");
		}
		
		//Content
		if (body != null) {
			if (method == Method.POST)
				((HttpPost) request).setEntity(new StringEntity(body));
			else if (method == Method.PATCH)
				((HttpPatch) request).setEntity(new StringEntity(body));
		}

		//Executing
		httpResponse = getHttpclient().execute(request);
		
		//Check the response code
		if (checkStatus200 && httpResponse.getStatusLine().getStatusCode() != 200)
			throw new Exception("Status Code "+ httpResponse.getStatusLine().getStatusCode() + " - " + EntityUtils.toString(httpResponse.getEntity()));
		
		return httpResponse;
	}

	public JSONObject getJsonBody(HttpResponse httpResponse) throws JSONException, ParseException, IOException {
		JSONObject result;
		if (httpResponse != null && httpResponse.getEntity() != null)
			result = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
		else
			result = new JSONObject("{}");
		
		return result;
	}
	
	private HttpClient getHttpclient() {
		if (httpclient == null)
			httpclient = HttpClients.createMinimal();
		return httpclient;
	}
}
