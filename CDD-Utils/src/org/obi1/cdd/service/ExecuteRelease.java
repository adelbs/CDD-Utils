package org.obi1.cdd.service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.obi1.cdd.communication.RestCDD;
import org.obi1.cdd.vo.ReleaseVo;

@Path("release")
public class ExecuteRelease {

	@POST
	@Path("/execute")
	public Response execute(@Context HttpServletRequest request, ReleaseVo release) {
		
		RestCDD cdd = new RestCDD(release);
		
		try {
			release.setAuth(request.getHeader("authorization"));
			
			if (release.getReleaseId() == null)
				cdd.setReleaseAndApplicationId();
			
			if (release.getDuplicateRelease() != null && release.getDuplicateRelease())
				cdd.duplicateRelease();
			
			if (release.getNewApplicationVersion() != null && release.getEndpointName() != null)
				cdd.createNewApplicationVersion();
			
			if (release.getToken() != null && release.getToken().length > 0 && release.getToken()[0].getValue() != null)
				cdd.setTokenValues();
			
			if (release.getExecuteRelease() != null && release.getExecuteRelease())
				cdd.executeRelease();
		}
		catch (Exception x) {
			x.printStackTrace();
			release.setErrorMessage(x.getMessage());
		}
		
		return Response.ok(new JSONObject(release).toString(), MediaType.APPLICATION_JSON).build();
	}
	
}
