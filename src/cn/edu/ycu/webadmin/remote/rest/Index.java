package cn.edu.ycu.webadmin.remote.rest;

import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class Index extends ServerResource{

	@Get
	public Representation get(){
		redirectPermanent(new Reference("/index.html"));
		return null;
	}
}
