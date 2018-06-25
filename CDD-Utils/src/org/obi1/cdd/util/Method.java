package org.obi1.cdd.util;

public enum Method {
	GET, POST, PUT, DELETE, PATCH;
	
	public static Method get(String method) {
		if ("GET".equals(method))
			return GET;
		else if ("POST".equals(method))
			return POST;
		else if ("PUT".equals(method))
			return PUT;
		else if ("DELETE".equals(method))
			return DELETE;
		else if ("PATCH".equals(method))
			return PATCH;
		return null;
	}
}
