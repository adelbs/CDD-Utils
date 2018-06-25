package org.obi1.cdd.communication;

import org.json.JSONArray;
import org.json.JSONObject;
import org.obi1.cdd.util.Method;
import org.obi1.cdd.vo.ReleaseVo;
import org.obi1.cdd.vo.TokenVo;

public class RestCDD {
	
	private ReleaseVo release;
	private HttpRequest httpRequest;
	
	public RestCDD(ReleaseVo release) {
		this.release = release;
		this.httpRequest = new HttpRequest();
	}
	
	/**
	 * Encontra um release baseado nos filtros passados e atualiza o objeto release com o id encontrado
	 * @throws Exception
	 */
	public void setReleaseAndApplicationId() throws Exception {
		JSONObject response = httpRequest.getJsonBody(httpRequest.execute(Method.GET, release,
				"/design/0000/v1/releases?filter="+ release.getFilterName() +
				(release.getFilterStatus() != null ? "&status="+ release.getFilterStatus() : "")));
		
		response = (JSONObject) response.getJSONArray("data").get(0);
		
		release.setReleaseId(response.getInt("id"));
		release.setApplicationId(((JSONObject) response.getJSONArray("applications").get(0)).getInt("id"));
		
		response = httpRequest.getJsonBody(httpRequest.execute(Method.GET, release, "/design/0000/v1/releases/"+ release.getReleaseId() +"/tokens"));
		
		if (release.getToken() == null) {
			TokenVo[] token = new TokenVo[response.getJSONArray("data").length()];
			JSONObject tokenJson;
			for (int i = 0; i < response.getJSONArray("data").length(); i++) {
				tokenJson = (JSONObject) response.getJSONArray("data").get(i);
				token[i] = new TokenVo();
				token[i].setId(tokenJson.getInt("id"));
				token[i].setName(tokenJson.getString("name"));
				token[i].setValue("");
			}
			release.setToken(token);
		}
	}

	/**
	 * Duplica um release baseado no releaseId e atualiza o mesmo releaseId com o id da nova release
	 * @throws Exception
	 */
	public void duplicateRelease() throws Exception {
		try {
			//Marcando o release anterior com status DONE
			httpRequest.execute(Method.PATCH, release,
					"/execution/0000/v1/releases-execution/"+ release.getReleaseId() +"?force=true", "{\"status\": \"DONE\"}", true, true);
		}
		catch (Exception x) {}
		
		//Duplicando o release
		JSONObject requestBody = new JSONObject();
		requestBody.put("id", release.getReleaseId());
		requestBody.put("name", release.getNewReleaseName());
		requestBody.put("version", release.getNewReleaseVersion());
		requestBody.put("description", release.getNewReleaseDescription());
		
		JSONObject response = httpRequest.getJsonBody(httpRequest.execute(Method.POST, release, "/design/0000/v1/releases", requestBody.toString(), true, true));
		release.setReleaseId(response.getJSONObject("data").getInt("id"));
	}

	/**
	 * Cria nova versao da aplicacao e adiciona novo contentsource para atrelar o conteudo ao release
	 * @throws Exception
	 */
	public void createNewApplicationVersion() throws Exception {
		
		//Criando nova versao
		JSONObject applicationJson = new JSONObject();
		applicationJson.put("id", release.getApplicationId());
		
		JSONObject requestBody = new JSONObject();
		requestBody.put("application", applicationJson);
		requestBody.put("name", release.getNewApplicationVersion());
		
		JSONObject response = httpRequest.getJsonBody(httpRequest.execute(Method.POST, release, 
				"/design/0000/v1/releases/"+ release.getReleaseId() +"/application-versions?force=true", requestBody.toString(), true, true));
		
		int applicationVersionId = response.getJSONObject("data").getInt("id");
		
		//Encontrando o endpoint pelo nome
		response = httpRequest.getJsonBody(httpRequest.execute(Method.GET, release, "/administration/0000/v1/endpoints?filter="+ release.getEndpointName()));
		JSONObject endpoint = null;
		for (int i = 0; i < response.getJSONArray("data").length(); i++) {
			endpoint = (JSONObject) response.getJSONArray("data").get(i);
			if (endpoint.getString("name").equals(release.getEndpointName()))
				break;
		}
		
		if (endpoint == null) throw new Exception("Endpoint not found = " + response.toString());
		
		//Encontrando o pluginservicetemplate pelo plugin encontrado acima (atrelado ao endpoint)
		response = httpRequest.getJsonBody(httpRequest.execute(Method.GET, release, "/administration/0000/v1/plugin-service-templates?type=CONTENT"));
		JSONObject pluginServiceTemplate = null;
		for (int i = 0; i < response.getJSONArray("data").length(); i++) {
			pluginServiceTemplate = (JSONObject) response.getJSONArray("data").get(i);
			if (pluginServiceTemplate.getInt("pluginId") == endpoint.getJSONObject("pluginService").getJSONObject("plugin").getInt("id"))
				break;
		}
		
		if (pluginServiceTemplate == null) throw new Exception("PluginServiceTemplate not found = " + response.toString());
		
		//Encontrando os parametros do pluginservicetemplate encontrado acima
		response = httpRequest.getJsonBody(httpRequest.execute(Method.GET, release, 
				"/administration/0000/v1/plugin-service-templates/"+ pluginServiceTemplate.getInt("id")));
		JSONArray templateParameters = response.getJSONObject("data").getJSONArray("templateParameters");
		

		//Montando objeto para criar contentsource
		JSONObject bodyContentSource = new JSONObject();
		JSONObject bodyPluginService = new JSONObject();
		JSONObject bodyEndpoint = new JSONObject();
		JSONObject bodyPlugin = new JSONObject();
		JSONArray bodyParameters = new JSONArray();
		JSONObject bodyTemplateParameter;
		
		for (int i = 0; i < templateParameters.length(); i++) {
			if (release.getContentParametersValue().length >= i && release.getContentParametersValue()[i] != null) {
				bodyTemplateParameter = new JSONObject();
				bodyTemplateParameter.put("templateParameterId", ((JSONObject) templateParameters.get(i)).getInt("id"));
				bodyTemplateParameter.put("value", release.getContentParametersValue()[i]);
				bodyParameters.put(bodyTemplateParameter);
			}
		}
		
		bodyEndpoint.put("id", endpoint.getInt("id"));
		bodyPlugin.put("id", endpoint.getJSONObject("pluginService").getJSONObject("plugin").getInt("id"));
		
		bodyPluginService.put("name", endpoint.getString("name"));
		bodyPluginService.put("templateId", pluginServiceTemplate.getInt("id"));
		bodyPluginService.put("endpoint", bodyEndpoint);
		bodyPluginService.put("plugin", bodyPlugin);
		bodyPluginService.put("parameters", bodyParameters);
		
		bodyContentSource.put("name", endpoint.getString("name"));
		bodyContentSource.put("pluginService", bodyPluginService);
		
		//Enviando requisicao para criar o contentsource
		httpRequest.execute(Method.POST, release, 
				"/design/0000/v1/releases/"+ release.getReleaseId() +"/application-versions/"+ applicationVersionId +"/content-sources", bodyContentSource.toString(), true, true);
	}

	/**
	 * Atualiza o valor dos tokens do release
	 * @throws Exception
	 */
	public void setTokenValues() throws Exception {

		//Atualizando os IDs dos tokens
		JSONObject response = httpRequest.getJsonBody(httpRequest.execute(Method.GET, release, "/design/0000/v1/releases/"+ release.getReleaseId() +"/tokens"));
		JSONObject tokenJson;
		for (int i = 0; i < response.getJSONArray("data").length(); i++) {
			tokenJson = (JSONObject) response.getJSONArray("data").get(i);
			for (int j = 0; j < release.getToken().length; j++) {
				if (release.getToken()[j].getName() != null && release.getToken()[j].getName().equals(tokenJson.getString("name"))) {
					release.getToken()[j].setId(tokenJson.getInt("id"));
					break;
				}
			}
		}
		
		//Atualizando os valores no release do cdd
		JSONObject requestBody = new JSONObject();
		JSONArray tokenList = new JSONArray();
		JSONObject tokenValue;
		for (int i = 0; i < release.getToken().length; i++) {
			if (!"last_successful_build".equalsIgnoreCase(release.getToken()[i].getName()) && !"last_successful_commit".equalsIgnoreCase(release.getToken()[i].getName())) {
				tokenValue = new JSONObject();
				tokenValue.put("releaseToken", new JSONObject("{\"id\": "+ release.getToken()[i].getId() +"}") );
				tokenValue.put("value", release.getToken()[i].getValue());
				tokenList.put(tokenValue);
			}
		}
		
		requestBody.put("tokenValues", tokenList);
		
		httpRequest.execute(Method.POST, release, "/execution/0000/v1/releases-execution/"+ release.getReleaseId() +"/manifests", requestBody.toString(), true, true);
	}
	
	/**
	 * Executa a release
	 * @throws Exception
	 */
	public void executeRelease() throws Exception {
		//Recuperando o id da primeira fase
		JSONObject response = httpRequest.getJsonBody(httpRequest.execute(Method.GET, release, "/design/0000/v1/releases/"+ release.getReleaseId() +"/phases"));
		JSONObject firstPhase = (JSONObject) response.getJSONArray("data").get(0);
		
		//Executando a primeira fase
		JSONObject requestBody = new JSONObject();
		requestBody.put("isApproved", true);
		requestBody.put("phaseId", firstPhase.getInt("id"));
		requestBody.put("status", "RUNNING");
		
		response = httpRequest.getJsonBody(httpRequest.execute(Method.PATCH, release,
				"/execution/0000/v1/releases-execution/"+ release.getReleaseId() +"/phases-execution/"+ firstPhase.getInt("id"), requestBody.toString(), true, true));
		
		release.setResultExecution(response);
	}
	
}
