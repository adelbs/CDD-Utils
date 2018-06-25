package org.obi1.cdd.vo;

import org.json.JSONObject;

public class ReleaseVo extends RequestVo {

	//Filtros para encontrar um release existente
	private String filterName;
	
	private String filterStatus;
	
	//ID do release a ser trabalhado
	private Integer releaseId;
	
	//ID da application relacionada ao releaseId
	private Integer applicationId;

	//Se true, duplica o release baseado no releaseId e atualiza o releaseId com o release duplicado
	private Boolean duplicateRelease;

	//Se for duplicar, considera o nome, versao e descricao abaixo para o novo release
	private String newReleaseName;
	
	private String newReleaseVersion;
	
	private String newReleaseDescription;
	
	//Se vier preenchido, cria uma nova versao de aplicacao e contentsource ao release
	private String newApplicationVersion;
	
	//Nome do endpoint para adicionar o contentsource (pelo nome do endpoint, o plugin e encontrado)
	private String endpointName;
	
	//Parametros dos templateparameters (utilizados como parametros do plugin que retorna o contentsource)
	private String[] contentParametersValue;
	
	//Valores dos tokens a serem utilizados no release
	private TokenVo[] token;
	
	//Se vier true, executa a release, senao, apenas retorna os valores encontrados
	private Boolean executeRelease;
	
	//Nao precisa enviar no request. Este campo e preenchido com o resultado do request para o cdd executar o release
	private JSONObject resultExecution;
	private String errorMessage;
	
	
	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public String getFilterStatus() {
		return filterStatus;
	}

	public void setFilterStatus(String filterStatus) {
		this.filterStatus = filterStatus;
	}

	public Integer getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(Integer releaseId) {
		this.releaseId = releaseId;
	}

	public Boolean getDuplicateRelease() {
		return duplicateRelease;
	}

	public void setDuplicateRelease(Boolean duplicateRelease) {
		this.duplicateRelease = duplicateRelease;
	}

	public String getNewReleaseVersion() {
		return newReleaseVersion;
	}

	public void setNewReleaseVersion(String newReleaseVersion) {
		this.newReleaseVersion = newReleaseVersion;
	}

	public String getNewReleaseName() {
		return newReleaseName;
	}

	public void setNewReleaseName(String newReleaseName) {
		this.newReleaseName = newReleaseName;
	}

	public String getNewReleaseDescription() {
		return newReleaseDescription;
	}

	public void setNewReleaseDescription(String newReleaseDescription) {
		this.newReleaseDescription = newReleaseDescription;
	}

	public Integer getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Integer applicationId) {
		this.applicationId = applicationId;
	}

	public String getNewApplicationVersion() {
		return newApplicationVersion;
	}

	public void setNewApplicationVersion(String newApplicationVersion) {
		this.newApplicationVersion = newApplicationVersion;
	}

	public String getEndpointName() {
		return endpointName;
	}

	public void setEndpointName(String endpointName) {
		this.endpointName = endpointName;
	}

	public String[] getContentParametersValue() {
		return contentParametersValue;
	}

	public void setContentParametersValue(String[] contentParametersValue) {
		this.contentParametersValue = contentParametersValue;
	}

	public JSONObject getResultExecution() {
		return resultExecution;
	}

	public void setResultExecution(JSONObject resultExecution) {
		this.resultExecution = resultExecution;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Boolean getExecuteRelease() {
		return executeRelease;
	}

	public void setExecuteRelease(Boolean executeRelease) {
		this.executeRelease = executeRelease;
	}

	public TokenVo[] getToken() {
		return token;
	}

	public void setToken(TokenVo[] token) {
		this.token = token;
	}

}
