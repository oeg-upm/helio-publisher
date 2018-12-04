package semanticgateway.controller;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import framework.objects.RDF;


public abstract class AbstractController {
	
	protected void prepareResponse(HttpServletResponse response) {
		response.setHeader("Server", "Semantic Gateway");
		response.setStatus( HttpServletResponse.SC_BAD_REQUEST ); // by default response code is BAD
	}	
	
	
}
