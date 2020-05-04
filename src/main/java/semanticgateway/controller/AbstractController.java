package semanticgateway.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;


public abstract class AbstractController {
	
	protected static Map<String,String> dynamicViews;
	
	static {
		dynamicViews = new HashMap<>();
		dynamicViews.put("http://localhost:8080/oeg/people/Freddy%20Priyatna","views/authors.html");
		dynamicViews.put("http://localhost:8080/oeg/people/Javier%20Bajo","views/authors.html");
	}
	
	
	protected void prepareResponse(HttpServletResponse response) {
		response.setHeader("Server", "Helio Gateway"); // Server type is hidden
		response.setStatus( HttpServletResponse.SC_BAD_REQUEST ); // by default response code is BAD
	}	
	
	protected Boolean htmlIsRequested(Map<String, String> headers) {
		return headers.containsKey("accept") && headers.get("accept").contains("text/html");
	}
	
}
