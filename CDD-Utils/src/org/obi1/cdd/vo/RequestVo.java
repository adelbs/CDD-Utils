package org.obi1.cdd.vo;

public class RequestVo {

	//Url do Rest (ex.: http://localhost:8080/cdd)
	private String url;
	
	//Authorization basic extraida do header (nao precisa enviar no json)
	private String auth;

	//Atributos a serem enviados no header
	private String header;
	
	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}
	
	public void addHeader(String line) {
		this.header += "\n"+ line;
	}
}
