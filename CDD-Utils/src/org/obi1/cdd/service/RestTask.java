package org.obi1.cdd.service;

import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.obi1.cdd.communication.HttpRequest;
import org.obi1.cdd.util.Method;
import org.obi1.cdd.vo.RequestVo;

import com.ca.rp.plugins.dto.model.ExternalTaskInputs;
import com.ca.rp.plugins.dto.model.ExternalTaskResult;
import com.ca.rp.plugins.dto.model.TaskConstants;
import com.jayway.jsonpath.JsonPath;

@Path("tasks/rest")
public class RestTask {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public ExternalTaskResult execute(@QueryParam(TaskConstants.ACTION_PARAM) String action, ExternalTaskInputs taskInputs) {
		ExternalTaskResult externalTaskResult;

		if (TaskConstants.EXECUTE_ACTION.equalsIgnoreCase(action))
			externalTaskResult = executeTask(taskInputs);
		else if (TaskConstants.STOP_ACTION.equalsIgnoreCase(action))
			externalTaskResult = stopTask(taskInputs);
		else
			throw new IllegalArgumentException("Unexpected invalid action '" + action + "' while trying to execute task.");

		return externalTaskResult;
	}

	private ExternalTaskResult executeTask(ExternalTaskInputs taskInputs) {
		HttpRequest httpRequest = new HttpRequest();
		ExternalTaskResult externalTaskResult;
		String value = "";
		
		try {
			//Pegando atributos do endpoint e da task
			Map<String, String> taskProperties = taskInputs.getTaskProperties();
			Map<String, String> endpointProperties = taskInputs.getEndPointProperties();

			String username = endpointProperties.get("username");
			String password = endpointProperties.get("password");
			String baseUrl = endpointProperties.get("URL");

			String restUrl = taskProperties.get("url");
			String method = taskProperties.get("method");
			String requestBody = taskProperties.get("body");
			String requestHeader = taskProperties.get("header");
			String valuesFrom = taskProperties.get("from");
			String jsonPath = taskProperties.get("jasonpath");
			String headerAttr = taskProperties.get("attribute");

			RequestVo requestVo = new RequestVo();
			requestVo.setUrl(baseUrl == null ? "" : baseUrl);
			
			if (username != null && password != null)
				requestVo.setAuth("Basic "+ new String(Base64.encodeBase64 (StringUtils.getBytesUtf8(username +":"+ password))));
			
			requestVo.setHeader(requestHeader);
			
			//Executando
			boolean isJsonBody = false;
			if (requestBody != null && !"".equals(requestBody) && requestBody.substring(0, 1).equals("{"))
				isJsonBody = true;
				
			HttpResponse httpResponse = httpRequest.execute(Method.get(method), requestVo, (restUrl == null ? "" : restUrl), requestBody, false, isJsonBody);
			
			//Encontrando o resultado
			if (valuesFrom.equals("RESPONSE BODY (JSON)")) {
				value = JsonPath.parse(httpRequest.getJsonBody(httpResponse).toString()).read(jsonPath);
			}
			else if (valuesFrom.equals("RESPONSE HEADER")) {
				for (Header header : httpResponse.getAllHeaders()) {
					if (header.getName().equalsIgnoreCase(headerAttr)) {
						value = header.getValue();
						break;
					}
				}
			}
			else {
				value = EntityUtils.toString(httpResponse.getEntity());
			}
			
			if (value != null && !"".equals(value))
				externalTaskResult = ExternalTaskResult.createResponseForFinished("Success", "Value found: "+ value);
			else
				externalTaskResult = ExternalTaskResult.createResponseForFailure("Failure", "JSON Path not found");

			externalTaskResult.setOutputParameter("value", value);
		} 
		catch (Exception e) {
			e.printStackTrace();
			externalTaskResult = ExternalTaskResult.createResponseForFailure("Failure", "Error: "+ e.getMessage());			
		}

		return externalTaskResult;
	}

	private ExternalTaskResult stopTask(ExternalTaskInputs taskInputs) {
		ExternalTaskResult externalTaskResult;

		externalTaskResult = ExternalTaskResult.createResponseForFailure("Failed to stop task", "Cannot stop");

		return externalTaskResult;
	}

}