package com.polycom.edge.webadmin.remote.rest.utils;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ServerResource;

public class RepResult {
	
	public static Representation respResult(ServerResource servResource, Status status, String repStr, Exception e){
		
		if(servResource != null){
			servResource.setStatus(status);
		}
		if(e != null){
			e.printStackTrace();
		}
		return new StringRepresentation(repStr, MediaType.TEXT_PLAIN);
	}
	
}
