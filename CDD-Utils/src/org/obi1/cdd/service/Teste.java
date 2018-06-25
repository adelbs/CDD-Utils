package org.obi1.cdd.service;

import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.ca.rp.plugins.dto.model.ExternalTaskInputs;
import com.ca.rp.plugins.dto.model.ExternalTaskResult;
import com.ca.rp.plugins.dto.model.TaskConstants;

@Path("tasks/task2")
public class Teste {

	private static final String PARAMETER1 = "parameter1";
	private static final String PARAMETER2 = "parameter2";
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public ExternalTaskResult execute(@QueryParam(TaskConstants.ACTION_PARAM) String action, ExternalTaskInputs taskInputs) {
		ExternalTaskResult externalTaskResult;

		if (TaskConstants.EXECUTE_ACTION.equalsIgnoreCase(action)) {
			externalTaskResult = executeTask(taskInputs);
		} 
		else if (TaskConstants.STOP_ACTION.equalsIgnoreCase(action)) {
			externalTaskResult = stopTask(taskInputs);
		} 
		else {
			throw new IllegalArgumentException("Unexpected invalid action '" + action + "' while trying to execute task.");
		}

		return externalTaskResult;
	}
	
	private ExternalTaskResult executeTask(ExternalTaskInputs taskInputs) {
		ExternalTaskResult externalTaskResult;

		try {
			Map<String, String> taskProperties = taskInputs.getTaskProperties();
			Map<String, String> endpointProperties = taskInputs.getEndPointProperties();
			
			String parameter1 = taskProperties.get(PARAMETER1);
			String parameter2 = taskProperties.get(PARAMETER2);
			
			if (parameter2.equals("SUCCESS")) {
				externalTaskResult = ExternalTaskResult.createResponseForFinished("Success!!!", "<a href='http://www.ca.com'>Link</a>");
				externalTaskResult.setOutputParameter("output_parameter1", "OK="+ parameter1);
			} 
			else {
				externalTaskResult = ExternalTaskResult.createResponseForFailure("Failure!!!", "Fail="+ parameter1);
				externalTaskResult.setOutputParameter("output_parameter1", "Fail="+ parameter1);
			}
		} 
		catch (Exception e) {
			externalTaskResult = ExternalTaskResult.createResponseForFailure("Failed to execute", e.getLocalizedMessage());
		}

		return externalTaskResult;
	}

	private ExternalTaskResult stopTask(ExternalTaskInputs taskInputs) {
		ExternalTaskResult externalTaskResult;

		externalTaskResult = ExternalTaskResult.createResponseForFailure("Failed to stop task", "Cannot stop");

		return externalTaskResult;
	}

}
