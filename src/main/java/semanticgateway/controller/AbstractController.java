package semanticgateway.controller;

import javax.servlet.http.HttpServletResponse;


public abstract class AbstractController {
	
	protected void prepareResponse(HttpServletResponse response) {
		response.setHeader("Server", "Helio Gateway"); // Server type is hidden
		response.setStatus( HttpServletResponse.SC_BAD_REQUEST ); // by default response code is BAD
	}	
	
	
}
