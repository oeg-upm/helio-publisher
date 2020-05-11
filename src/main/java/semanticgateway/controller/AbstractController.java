package semanticgateway.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.ExpiredJwtException;


public abstract class AbstractController {
	
	
	
	
	
	
	protected void prepareResponse(HttpServletResponse response) {
		response.setHeader("Server", "Helio Gateway"); // Server type is hidden
		response.setStatus( HttpServletResponse.SC_BAD_REQUEST ); // by default response code is BAD
	}	
	
	protected Boolean htmlIsRequested(Map<String, String> headers) {
		return headers.containsKey("accept") && headers.get("accept").contains("text/html");
	}
	
	
	
	
	
}
