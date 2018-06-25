package org.obi1.cdd.service;

import com.ca.rp.plugins.dto.model.ConnectivityInput;
import com.ca.rp.plugins.dto.model.ConnectivityResult;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.Map;

/**
 * Created by admin
 */
@Path("connectivity-tests/connectivity-test")
public class ConnectivityTest {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ConnectivityResult connect(ConnectivityInput connectivityInput) {

		ConnectivityResult connectivityResult = new ConnectivityResult(true, null);
		boolean status = true;
		String errorMessage = null;

		Map<String, String> endpointProperties = connectivityInput.getEndPointProperties();

		if (status == false) {
			connectivityResult.setSuccess(status);
			connectivityResult.setErrorMessage(errorMessage);
		}

		return connectivityResult;
	}

}